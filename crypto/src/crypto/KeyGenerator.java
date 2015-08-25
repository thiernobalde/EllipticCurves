package crypto;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import math.Courbe;
import math.Point;


/** Générateur de clé
 * @author Thierno BALDE et Adrien Debono
 */
public class KeyGenerator {
	BigInteger privKey;
	Point pubKey;
	
		
	public BigInteger generatePrivateKey(int taille,Courbe c){
		
		BigInteger r;
		BigInteger p = c.getP();
		Random rnd = new SecureRandom();
		do {
		    r = new BigInteger(taille, rnd);
		} while (r.compareTo(p) >= 0);
		privKey = r;
		return privKey;
	}
	public Point generatePublicKey(BigInteger privKey, Courbe c)
	{
		pubKey = c.multiplication(c.getG(),privKey);
		return pubKey;
	}
	
		
}
