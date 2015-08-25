package math;

import java.math.BigInteger;

/** Courbe elliptique
 * @author Thierno BALDE et Adrien Debono
 */
public class Courbe {

	BigInteger a4;
	BigInteger a6;
	BigInteger p;
	Point g;

	public Courbe(	String a4, String a6,
			String p) {

		// y2 = x3 + a4*xy + a6 
		this.a4 = new BigInteger(a4);
		this.a6 = new BigInteger(a6);
		this.p = new BigInteger(p);


	}

	public Courbe(String a42, String a62, String p2, String gx, String gy) {
		this.a4 = new BigInteger(a42);
		this.a6 = new BigInteger(a62);
		this.p = new BigInteger(p2);
		this.g =new Point(gx,gy,true);
		//this.g.setX(gx); this.g.setY(gy); this.g.setZ(true);
	}

	/*
	 * xQ = xP
	 * yQ = -yP -a1*xP - a3
	 */
	public Point oppose(Point P) {
		Point Q = new Point();
		BigInteger yQ = (P.getY()).negate().mod(p);
		Q.setX(P.getX().mod(p) );
		Q.setY(yQ);
		Q.setZ(P.getZ());

		return Q;
	}


	public  Point addition(Point P, Point Q) {
		Point opposeP = oppose(P);
		// On teste d'abord si P = oppose(Q)
		if ( opposeP.toString().equals(Q.toString()))
			return new Point("0","1",false);
		else if ( P.getZ() == false && Q.getZ() == true)
			return Q;
		else if (Q.getZ() == false)
			return P;
		else
		{ // Ici on additione, car tous les tests sont pass�s
			Point R = new Point();
			BigInteger lambda;
			BigInteger XP = P.getX();
			BigInteger YP = P.getY();
			BigInteger XQ = Q.getX();
			BigInteger YQ = Q.getY();
			BigInteger div = new BigInteger("0");
			// On calcule lambda
			if ( XP.toString().equals(XQ.toString()) )
			{ // XP = XQ -> lambda = (3XP2 + a4) / ( 2YP )
				lambda = XP.modPow(new BigInteger("2"), p);
				lambda = lambda.multiply(new BigInteger("3")).mod(p);
				lambda = lambda.add(a4);
				div = YP.multiply(new BigInteger("2")).mod(p);
				lambda = lambda.multiply(div.modInverse(p));
				lambda = lambda.mod(p);

			}
			else
			{ // XP != XQ -> lambda = (yp - yq) / ( xp - xq)
				lambda = YP.subtract(YQ).mod(p);
				div = XP.subtract(XQ).mod(p);
				lambda = lambda.multiply((div.modInverse(p)));
				lambda = lambda.mod(p);
			}
			// On fixe XR et YR gr�ce � lambda
			// XR = lambda2 - XP - XQ
			// YR =  lambda*(xp - xr) - yp
			BigInteger XR = (lambda.modPow(new BigInteger("2"), p).subtract(XP).subtract(XQ)).mod(p);
			BigInteger YR = (lambda.multiply(XP.subtract(XR)).subtract(YP)).mod(p);

			R.setX(XR);
			R.setY(YR);
			R.setZ(true);
			return R;
		}
	}
	// Multiplication par un BigInteger
	public Point multiplication(Point p2, BigInteger n) {

		String impair="";
		int ind = 0;
		BigInteger un =new BigInteger("1");
		BigInteger deux =new BigInteger("2");
		if(n.equals(BigInteger.ZERO)) return new Point("0","1",false);
		else if(n.equals(un)) return p2;

		Point MP = new Point(p2.getX(), p2.getY(),p2.getZ());

		while(n.compareTo(un)==1)//si n>1
		{
			impair += (n.mod(deux));
			n = n.divide(deux);

		}

		ind  = impair.length(); //pour savoir combien de division on a fait dans la boucle precedente

		while(ind > 0)
		{
			ind--;
			MP = addition(MP,MP);
			if(impair.charAt(ind) == '1')
			{
				MP = addition(MP,p2);
			}
			
		}
		return MP;
	}
	
	// Multiplication par un entier
	public Point multiplication(Point p2, int n) {

		int tab[] = new int[n];
		int ind = 0;

		if(n == 0) return new Point("0","1",false);
		else if(n == 1) return p2;

		Point MP = new Point(p2.getX(), p2.getY(),p2.getZ());

		while(n > 1)
		{
			tab[ind ++] = n%2;
			n = n/2;

		}
		ind  = ind - 1;
		while(ind >= 0)
		{
			MP = addition(MP,MP);
			if(tab[ind] == 1)
			{
				MP = addition(MP,p2);
			}
			ind--;
		}
		return MP;
	}
	
	public Point getG()
	{ return this.g; }
	public BigInteger getP()
	{ return this.p; }

	public boolean existe(Point p2) {
		BigInteger res;
		res= new BigInteger("0");
		BigInteger x = p2.getX();
		BigInteger y = p2.getY();
		res = res.add(y.modPow(new BigInteger("2"), p));
		res = res.subtract(x.modPow(new BigInteger("3"), p));
		res = res.subtract((x.multiply(a4)));
		res = res.subtract(a6);
		res = res.mod(p);
		return (res == BigInteger.ZERO);
	}

	public static void main(String[] args)
	{

		try {	assert false ; throw new RuntimeException("-ea") ; }
		catch (AssertionError e){}
		// TDD ADDITION
		Courbe ce = new Courbe("1","1","5");
		Point P = new Point("2","1",true);
		assert(ce.existe(P)); // le point existe
		assert( P.toString().equals("(2,1,true)"));
		assert !(ce.existe(new Point("1","2",true))); // le point n'existe pas

		// On teste l'affichage d'un point à l'infini
		Point O = new Point("23", "0", false);
		assert(O.toString().equals("Point a linfini")):O;

		Point Q;
		Q = ce.oppose(P);
		assert(Q.toString().equals("(2,4,true)"));
		assert(ce.existe(Q)); // le point existe
		// On va tester ici l'addition
		// P fini et Q fini
		Q.setX("0");
		Q.setY("1");
		Q.setZ(true);
		Point PQ = ce.addition(P,Q);
		assert(PQ.toString().equals("(3,4,true)")):PQ;

		//P infini et Q fini = (0,1,true)
		P.setZ(false);
		Q.setZ(true);
		PQ = ce.addition(P,Q);
		assert(PQ.toString().equals(Q.toString()));

		// Q infini et P fini = (2,1,true)
		P.setZ(true);
		Q.setZ(false);
		PQ = ce.addition(P,Q);
		assert(PQ.toString().equals(P.toString()));

		// Q infini et P i nfini
		P.setZ(false);
		Q.setZ(false);
		PQ = ce.addition(P,Q);
		assert(PQ.toString().equals("Point a linfini")):PQ;

		// P fini = (2,1,true) , Q fini = oppose(P)
		P = new Point("0","1",true);
		assert(ce.existe(P));
		Q = ce.oppose(P);
		System.out.println("Q = " + Q);
		PQ = ce.addition(P,Q);
		assert(PQ.toString().equals("Point a linfini")):PQ;

		Point P1 = ce.addition(P,P); System.out.println("P1 = " +P1); assert(ce.existe(P1));
		Point P2 = ce.addition(P1,P); System.out.println("P2 = " +P2);assert(ce.existe(P2));
		Point P3 = ce.addition(P2,P); System.out.println("P3 = " +P3);assert(ce.existe(P3));
		Point P4 = ce.addition(P3,P); System.out.println("P4 = " +P4);assert(ce.existe(P4));
		Point P5 = ce.addition(P4,P); System.out.println("P5 = " +P5);assert(ce.existe(P5));
		Point P6 = ce.addition(P5,P); System.out.println("P6 = " +P6);assert(ce.existe(P6));
		Point P7 = ce.addition(P6,P); System.out.println("P7 = " +P7);assert(ce.existe(P7));
		Point P8 = ce.addition(P7,P); System.out.println("P8 = " +P8);assert(ce.existe(P8));
		Point P9 = ce.addition(P,P8); System.out.println("P9 = " +P9);assert(ce.existe(P9));
		Point P10 = ce.addition(P9,P); System.out.println("P10 = " +P10);


		// TDD Multiplication
		P = new Point("0","1",true);
		Q = ce.multiplication(P,0);
		assert(Q.toString().equals("Point a linfini")):Q;
		Q = ce.multiplication(P,9);
		assert(Q.toString().equals("Point a linfini")):Q;
		Q = ce.multiplication(P,10);
		assert(Q.toString().equals(P.toString())):Q;
		
		// TDD Multiplication BigInt * Point
		Q=ce.multiplication(P, BigInteger.ZERO);
		assert(Q.toString().equals("Point a linfini"));
		Q = ce.multiplication(P,new BigInteger("9"));
		assert(Q.toString().equals("Point a linfini")):Q;
		Q = ce.multiplication(P,new BigInteger("10"));
		assert(Q.toString().equals(P.toString())):Q;
		
		
		// TDD gros nombre avec w256-001
		Courbe c2 = new Courbe("2481513316835306518496091950488867366805208929993787063131352719741796616329",
				"4387305958586347890529260320831286139799795892409507048422786783411496715073",
				"8884933102832021670310856601112383279507496491807071433260928721853918699951");
		Point G = new Point ("7638166354848741333090176068286311479365713946232310129943505521094105356372",
				"762687367051975977761089912701686274060655281117983501949286086861823169994",true);
		assert(c2.existe(G));
		Q = c2.addition(G,G);
		System.out.println(Q);
		assert(c2.existe(Q));
		Q = c2.multiplication(G,  new BigInteger("100"));
		assert(c2.existe(Q));
		System.out.println(Q);
	}

}
