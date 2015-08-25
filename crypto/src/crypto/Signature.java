// Thierno & Adrien Projet Crypto 2014/2015 Polytech Marseille
// On a une courbe E avec une point Générateur G, une cléPriv = alpha, une clé Pub = [alpha]G
// Pour signer un message m avec ECDSA, il y a 6 étapes
/* 1) h = hash(m) mod(ordre)
	2) k = random()
	3)(x,y) = [k]G
	4) r = x mod(ordre) 						if r=0 goto step2
	5) s = (k^-1)*(h + r*alpha) mod(ordre)      if s=0 goto step2
	6) Send m,r,s
	
	Pour vérifier la signature, 4 étapes
	1) On vérifie que les données envoyées sont cohérentes
	2) h = hash(m) mod(ordre)
	3) s1 = s^-1 modulo(ordre)
	4) [h*s1]G + [r.s1]Q = V
	Ensuite on vérifie V.getX == r et si oui c'est bon
 */
package crypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;


import math.Courbe;
import math.Point;


public class Signature {
	public Courbe C;
	public BigInteger ordre; // L'ordre de la courbe
	private BigInteger alpha; // la clé privée
	public Point Q; // la clé publique
	

	public Signature (Courbe C, BigInteger ordre, BigInteger alpha)
	{
		this.C = C;
		this.ordre = ordre;
		this.alpha = alpha;
		this.Q = C.multiplication(C.getG(), alpha);
	}

	private BigInteger getRand ()
	{
		BigInteger k;
		BigInteger p = this.ordre;
		Random rnd = new SecureRandom();
		do {
			k = new BigInteger(p.bitLength(), rnd);
		} while (k.compareTo(p) >= 0);
		return k.mod(p);
	}
	// Hash un string sous la forme d'un bigInteger modulo l'ordre
	public BigInteger hash (String M) throws NoSuchAlgorithmException
	{
		byte[] data = M.getBytes();
		byte[] hash;

		MessageDigest md;
		md = MessageDigest.getInstance("SHA-512");
		md.update(data);
		hash = md.digest();
		BigInteger C = new BigInteger(hash);
		return C.mod(this.ordre);
	}
	
	public BigInteger[] ECDSA (String m) throws NoSuchAlgorithmException {
		// Déclaration
		BigInteger h,k,r,s;
		Point p1;
		// Corps
		h = this.hash(m); // Etape 1
		s = BigInteger.ZERO;
		r =  s; k = s;
		while ( s.toString().equals("0"))
		{
			r = BigInteger.ZERO;
			while ( r.toString().equals("0"))
			{
				k = this.getRand(); // Etape 2
				p1 = this.C.multiplication(this.C.getG(), k); // Etape 3
				r = p1.getX().mod(this.ordre); // Etape 4
			}
			s = ((k.modInverse(this.ordre)).multiply(h.add(r.multiply(alpha)))).mod(this.ordre); // Etape 5
		}
		BigInteger tab[] = { r , s } ;
		System.out.println("Le message et la signature du signataire");
		System.out.println("m = " + m);
		System.out.println("r = " + r);
		System.out.println("s = " + s);
		return tab;
	} 
	
	public int check_ECDSA (String m, BigInteger r, BigInteger s, Point Kpub) throws NoSuchAlgorithmException {
		// Etape 1) On check que Kpub appartiens bien à la courbe 
		//et que ordre*Kpub = point à l'infini
		if( !C.existe(Kpub) || C.multiplication(Kpub, ordre).getZ() == true)
			{return -1;}
		// Toujours etape 1 de verification, on vérifie que r, s appartiennent (1 .. ordre-1)
		if( r.compareTo(ordre) >= 0 || s.compareTo(ordre) >= 0 
			|| r.compareTo(BigInteger.ZERO) <= 0 
			|| s.compareTo(BigInteger.ZERO)<= 0) 
			{return -1;}
		// Etape 2) h = hash(m) mod(ordre)
		BigInteger h = hash(m);
		// Etape 3) s1 = s^-1 modulo(ordre)
		BigInteger s1 = s.modInverse(ordre);
		// Etape 4) [h*s1]G + [r.s1]Q = V
		BigInteger b1 = h.multiply(s1);
		BigInteger b2 = r.multiply(s1);
		Point P1 = C.multiplication(C.getG(), b1);
		Point Q1 = C.multiplication(Kpub, b2);
		Point V = C.addition(P1, Q1);
		System.out.println("\nCe que le destinataire retrouve :");
		System.out.println("m = " + m);
		System.out.println("x = " + V.getX());
		// Verif finale
		if ( V.getX().toString().equals(r.toString()))
			{return 0;}
		else
			{return 1;}
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {

		// Courbe w256-001
		Courbe c3 = new Courbe (
				"2481513316835306518496091950488867366805208929993787063131352719741796616329",
				"4387305958586347890529260320831286139799795892409507048422786783411496715073",
				"8884933102832021670310856601112383279507496491807071433260928721853918699951",
				"7638166354848741333090176068286311479365713946232310129943505521094105356372",
				"762687367051975977761089912701686274060655281117983501949286086861823169994");
		BigInteger alpha = new BigInteger("2481038759442424424299810242807468076325797668646767699023329088839404"); //  clé privée au pif
		BigInteger ordre = new BigInteger("8884933102832021670310856601112383279454437918059397120004264665392731659049"); // Ordre pris du même fichier
		// La signature de Alice qui servira a signer
		Signature Sa = new Signature(c3, ordre, alpha);
		// La signature de Bob qui servira a checker 
		Signature Sb = new Signature( c3, ordre, BigInteger.TEN);
		BigInteger tab[] = new BigInteger[2];
		String m = "Hey Salut ça va ?"; // m
		// Signature ICI
		tab = Sa.ECDSA(m);
		BigInteger r = tab[0];
		BigInteger s = tab[1];
		//System.out.println(Sb.Q);
		// Verification de signature ici
		int result = Sb.check_ECDSA(m, r, s, Sa.Q);
		if( result == 0)
			System.out.println(" (r = x) => La signature est bonne");
		else
			System.out.println("La signature n'est pas bonne");
		

		

	}

}
 