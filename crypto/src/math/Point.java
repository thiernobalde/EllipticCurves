package math;
import java.io.Serializable;
import java.math.BigInteger;

/** Point
 * @author Thierno BALDE et Adrien Debono
 */
public class Point implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	BigInteger X;
	BigInteger Y;
	Boolean Z; // true = point normal , false = point à l'infini

	public Point()
	{
		X = new BigInteger("0");
		Y = new BigInteger("0");
		Z = true;
	}
	public Point(String i, String j, boolean b)
	{
		X = new BigInteger(i);
		Y = new BigInteger(j);
		Z = b;
	}
	public Point(BigInteger i, BigInteger j,Boolean b) {
		X = i;
		Y = j;
		Z = b;
	}
	@Override
	public String toString()
	{
		if( Z == false)
		{
			return "Point a linfini";
		}
		String txt = "(";
		txt += X;
		txt += ",";
		txt += Y;
		txt += ",";
		txt += Z;
		txt += ")";
		return txt;
	}
	
	// Les getters et les setters !!
	public BigInteger getX(){return X;}
	public BigInteger getY() {return Y;}
	public Boolean getZ(){return Z;}
	void setZ(Boolean b) {Z = b;}
	public void setX(String i) {X = new BigInteger(i);}
	public void setY(String j) {Y = new BigInteger(j);}
	public void setX(BigInteger i) {X = i;}
	public void setY(BigInteger j) {Y = j;}
	// Fin des getters et des setters
	
	public static void main(String[] args)
	{
		try {	assert false ; throw new RuntimeException("-ea") ; }
		catch (AssertionError e){}
		
		Point p = new Point();
		assert p.getX().equals(BigInteger.ZERO):"erreur init defaut x";
		assert p.getY().equals(BigInteger.ZERO):"erreur init defaut y";
		
		Point q = new Point(new BigInteger("5"),new BigInteger("7"), true);
		assert q.getX().equals(new BigInteger("5")):"erreur init defaut x";
		assert q.getY().equals(new BigInteger("7")):"erreur init defaut y";
		
		q.setX("3");
		q.setY("4");
		q.setZ(false);
		
		assert q.getX().equals(new BigInteger("3")):"erreur init defaut x";
		assert q.getY().equals(new BigInteger("4")):"erreur init defaut y";
		assert (q.getZ()== false):"erreur init defaut z";

		
		
				//z = p.addition(q);
	}

	
}
