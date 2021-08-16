import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author Brandon Soledad
 */
public class Signature implements Serializable {

    private static final long serialVersionUID = -3L;

    private byte[] x;
    private BigInteger y;

    public Signature(byte[] a, BigInteger b) {
        this.x = a;
        this.y = b;
    }

    public byte[] getH() {
        return x;
    }

    public BigInteger getZ() {
        return y;
    }

}