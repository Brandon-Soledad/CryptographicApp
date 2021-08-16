import java.io.Serializable;
import java.math.BigInteger;


/**
 * @author Brandon Soledad
 */
public class CurvePoint implements Serializable {

   
    private static final long serialVersionUID = -1L;
    
 	private static final BigInteger p = BigInteger.valueOf(2).pow(521).subtract(BigInteger.ONE);
 	
 	private static final Integer d = -376014;

    private BigInteger x;
    private BigInteger y;

    public CurvePoint(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }

    public BigInteger getX() {
        return x;
    }

    public BigInteger getY() {
        return y;
    }

    public CurvePoint multiply(BigInteger num) {
        CurvePoint point1 = new CurvePoint(x, y);
        CurvePoint point2 = new CurvePoint(x, y);
        String s = num.toString(2);
        for (int i = s.length() - 1; i >= 0; i--) {
            point1 = point1.add(point1);
            if (s.charAt(i) == '1') {
                point1 = point1.add(point2);
            }
        }
        return point1;
    }

    public CurvePoint add(CurvePoint point) {
        BigInteger xNumerator = x.multiply(point.getY()).add(y.multiply(point.getX()));

        BigInteger xDenominator = BigInteger.ONE.add(BigInteger.valueOf(d).multiply(x)
            .multiply(point.getX()).multiply(y).multiply(point.getY()));

        BigInteger xResult = xNumerator.multiply(xDenominator.modInverse(p)).mod(p);

        BigInteger yNumerator = y.multiply(point.getY()).subtract(x.multiply(point.getX()));

        BigInteger yDenominator = BigInteger.ONE.subtract(BigInteger.valueOf(d).multiply(x)
            .multiply(point.getX()).multiply(y).multiply(point.getY()));

        BigInteger yResult = yNumerator.multiply(yDenominator.modInverse(p)).mod(p);
        
        return new CurvePoint(xResult, yResult);
    }

}