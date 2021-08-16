import java.io.Serializable;
import java.math.BigInteger;

/**
 * 
 * @author Brandon Soledad
 *
 */
public class KeyPair implements Serializable {

    private static final long serialVersionUID = -4L;

    private BigInteger k;
    private CurvePoint p;

    public KeyPair(BigInteger k, CurvePoint p) {
        this.k = k;
        this.p = p;
    }

    public BigInteger getK() { // Unused Code
        return k;
    }

    public CurvePoint getP() {
        return p;
    }

}