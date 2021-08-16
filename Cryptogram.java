import java.io.Serializable;

/**
 * @author Brandon Soledad
 */
public class Cryptogram implements Serializable {

    private static final long serialVersionUID = -2L;

    private CurvePoint X;
    private byte[] Y;
    private byte[] Z;

    public Cryptogram(CurvePoint a, byte[] b, byte[] c) {
        this.X = a;
        this.Y = b;
        this.Z = c;
    }

    public CurvePoint getX() {
        return X;
    }

    public byte[] getY() {
        return Y;
    }

    public byte[] getZ() {
        return Z;
    }

}