import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 
 * @author Brandon Soledad
 * EllipticCurveCryptography
 */
public class ECC {
	
	//public generator
	private static final CurvePoint G = new CurvePoint(new BigInteger("4"), new BigInteger("2"));
	
	private static final BigInteger r = BigInteger.valueOf(2).pow(519).subtract(new BigInteger
			("337554763258501705789107630418782636071904961214051226618635150085779108655765"));
	
	//Generates a ECDHIES key pair from a given password 
	public static KeyPair generateEllipticKeyPair(byte[] password) {
		byte[] tmp = SHAKE.KMACXOF256(password, "".getBytes(), 512, "K".getBytes());
		
		BigInteger s = BigInteger.valueOf(4).multiply(new BigInteger(tmp));
		
		CurvePoint point = G.multiply(s);
		
		KeyPair k = new KeyPair(s, point);
		return k;
	}
	
	//Encrypts a byte array under the ECHIES public key 
	public static Cryptogram encryptDataFile(byte[] m, CurvePoint point) {
		SecureRandom random = new SecureRandom();
	    byte[] tmp = new byte[64];
	    random.nextBytes(tmp);
	    
	    BigInteger k = BigInteger.valueOf(4).multiply(new BigInteger(tmp));
	    
	    CurvePoint W = point.multiply(k);
	    
	    CurvePoint X = G.multiply(k);
	    
	    byte[] keka = SHAKE.KMACXOF256(W.getX().toByteArray(), "".getBytes(), 1024, "P".getBytes());
	    
	    byte[] ke = Arrays.copyOfRange(keka, 0, keka.length / 2);
	    byte[] y = new byte[m.length];
	    byte[] tmp2 = SHAKE.KMACXOF256(ke, "".getBytes(), m.length * 8, "PKE".getBytes());
	    for (int i = 0; i < m.length; i++) {
	    	y[i] = (byte) (tmp2[i] ^ m[i]);
	    }
	    
	    byte[] ka = Arrays.copyOfRange(keka, keka.length / 2, keka.length);
	    byte[] z = SHAKE.KMACXOF256(ka, m, 512, "PKA".getBytes());
	    
	    Cryptogram xyz = new Cryptogram(X, y, z);
		return xyz;
	}
	
	// Decrypts a cryptogram under a given password:
	public static byte[] decryptCryptogram(Cryptogram xyz, byte[] password) {
		byte[] temp = SHAKE.KMACXOF256(password, "".getBytes(), 512, "K".getBytes());
		
		BigInteger o = BigInteger.valueOf(4).multiply(new BigInteger(temp));
		
		CurvePoint T = xyz.getX().multiply(o);
		
		byte[] keka = SHAKE.KMACXOF256(T.getX().toByteArray(), "".getBytes(), 1024, "P".getBytes());
		
		byte[] ke = Arrays.copyOfRange(keka, 0, keka.length / 2);
		byte[] m = new byte[xyz.getY().length];
		byte[] tmp2 = SHAKE.KMACXOF256(ke, "".getBytes(), xyz.getY().length * 8, "PKE".getBytes());
		for (int i = 0; i < m.length; i++) {
	    	m[i] = (byte) (tmp2[i] ^ xyz.getY()[i]);
	    }

		byte[] ka = Arrays.copyOfRange(keka, keka.length / 2, keka.length);
		byte[] tp = SHAKE.KMACXOF256(ka, m, 512, "PKA".getBytes());
		
		if (Arrays.equals(tp, xyz.getZ())) {
			return m;
		} else {
			return null;
		}
	}
	
	// Generates a signature for a byte array under a given password
	public static Signature generateSignature(byte[] m, byte[] password) {
		byte[] temp = SHAKE.KMACXOF256(password, "".getBytes(), 512, "K".getBytes());
		
		BigInteger s = BigInteger.valueOf(4).multiply(new BigInteger(temp));
		
		byte[] tmp2 = SHAKE.KMACXOF256(s.toByteArray(), m, 512, "N".getBytes());
		
		BigInteger k = BigInteger.valueOf(4).multiply(new BigInteger(tmp2));
		
		CurvePoint U = G.multiply(k);
		
		byte[] h = SHAKE.KMACXOF256(U.getX().toByteArray(), m, 512, "T".getBytes());
		
		BigInteger z = k.subtract((new BigInteger(h)).multiply(s)).mod(r);
		
		Signature hz = new Signature(h, z);
		return hz;
	}
	
	// Verifies a signature for a byte array under the ECDHIES public key 
	public static boolean verifySignature(Signature hz, byte[] m, CurvePoint point) {
		CurvePoint U = (G.multiply(hz.getZ())).add(point.multiply(new BigInteger(hz.getH())));
		
		if (Arrays.equals(SHAKE.KMACXOF256(U.getX().toByteArray(), m, 512, "T".getBytes()), 
				hz.getH())) {
			return true;
		} else {
			return false;
		}
	}

	//Computes a square root with a LSB (Least significant bit)
	public static BigInteger sqrt(BigInteger b, BigInteger p, boolean lsb) {
	    assert(p.testBit(0) && p.testBit(1)); // p = 3 (mod 4)
	    if (b.signum() == 0) {
	        return BigInteger.ZERO;
	    }
	    BigInteger r = b.modPow(p.shiftRight(2).add(BigInteger.ONE), p);
	    if (r.testBit(0) != lsb) {
	        r = p.subtract(r); 
	    }
	    return (r.multiply(r).subtract(b).mod(p).signum() == 0) ? r : null;
	}
	
}