import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * @author Brandon Soledad
 */
public class SymmetricCryptography {
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	
	// Computes a cryptographic hash of a byte array 
	public static String computeCryptographicHash(byte[] m) {
		return bytesToHex(SHAKE.KMACXOF256("".getBytes(), m, 512, "D".getBytes()));
	}
	
	// Encrypts a byte array symmetrically under a password 
	public static byte[] encryptDataFile(byte[] m, byte[] password) {
		
		SecureRandom random = new SecureRandom();
	    byte[] X = new byte[64];
	    random.nextBytes(X);
	    
	    
	    byte[] keka = SHAKE.KMACXOF256(concatenateByteArrays(X, password), "".getBytes(), 1024, "S".getBytes());
	    
	    
	    byte[] ke = Arrays.copyOfRange(keka, 0, keka.length / 2);
	    byte[] Y = new byte[m.length];
	    byte[] tmp = SHAKE.KMACXOF256(ke, "".getBytes(), m.length * 8, "SKE".getBytes());
	    for (int i = 0; i < m.length; i++) {
	    	Y[i] = (byte) (tmp[i] ^ m[i]);
	    }
	    
	    byte[] ka = Arrays.copyOfRange(keka, keka.length / 2, keka.length);
	    byte[] Z = SHAKE.KMACXOF256(ka, m, 512, "SKA".getBytes());
	
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    try {
	    	output.write(X);
			output.write(Y);
			output.write(Z);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    return output.toByteArray();
	}
	
	// Decrypts a symmetric cryptogram under a given password
	public static byte[] decryptCryptogram(byte[] xyz, byte[] password) {
		byte[] z = Arrays.copyOfRange(xyz, 0, 64);
		byte[] keka = SHAKE.KMACXOF256(concatenateByteArrays(z, password), "".getBytes(), 1024, "S".getBytes());
		
		
		byte[] ke = Arrays.copyOfRange(keka, 0, keka.length / 2);
		byte[] c = Arrays.copyOfRange(xyz, 64, xyz.length - 64);
		byte[] tmp = SHAKE.KMACXOF256(ke, "".getBytes(), c.length * 8, "SKE".getBytes());
		byte[] m = new byte[c.length];
		for (int i = 0; i < c.length; i++) {
			m[i] = (byte) (tmp[i] ^ c[i]);
		}
		
		byte[] ka = Arrays.copyOfRange(keka, keka.length / 2, keka.length);
		byte[] tp = SHAKE.KMACXOF256(ka, m, 512, "SKA".getBytes());
		
		byte[] t = Arrays.copyOfRange(xyz, xyz.length - 64, xyz.length);
		if (Arrays.equals(tp, t)) {
			return m;
		} else {
			return null;
		}
	}
	
	// Compute an authentication tag of a byte array under a given password
	public static String computeAuthenticationTag(byte[] m, byte[] password) {
		byte[] t = SHAKE.KMACXOF256(password, m, 512, "T".getBytes());
		return bytesToHex(t);
	}
	
	private static String bytesToHex(byte[] bytes) {
	    char[] hex = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int a = bytes[j] & 0xFF;
	        hex[j * 2] = HEX_ARRAY[a >>> 4];
	        hex[j * 2 + 1] = HEX_ARRAY[a & 0x0F];
	    }
	    return new String(hex);
	}
	
	private static byte[] concatenateByteArrays(byte[] a, byte[] b) {
	    byte[] result = new byte[a.length + b.length]; 
	    System.arraycopy(a, 0, result, 0, a.length); 
	    System.arraycopy(b, 0, result, a.length, b.length); 
	    return result;
	} 
}