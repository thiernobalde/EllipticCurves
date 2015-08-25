package crypto;
//package courbell;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class AES_CTR {

	byte[] keyBytes;
	byte[] ivBytes;
	Cipher cipher ;
	SecretKeySpec key;
	IvParameterSpec ivSpec;
	public AES_CTR(byte [] keyB,byte [] ivB)throws Exception
	{
		keyBytes = keyB;
		ivBytes = ivB;
	//	System.out.println("=========SecretKeySpec===========");
		key = new SecretKeySpec(keyBytes, "AES");
	//	System.out.println("==========IvParameterSpec==========");
	    ivSpec = new IvParameterSpec(ivBytes);
	  //  System.out.println("=========getInstance===========");
	    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());    
	    cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
	    

	}
	byte[] encrypt (byte[]  data)throws Exception 
	{
		//System.out.println("input : " + data);
		cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
	    ByteArrayInputStream bIn = new ByteArrayInputStream(data);
	    @SuppressWarnings("resource")
		CipherInputStream cIn = new CipherInputStream(bIn, cipher);
	    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
	
	    int ch;
	    while ((ch = cIn.read()) >= 0) {
	      bOut.write(ch);
	    }
	    return bOut.toByteArray();
	    
	    
	}
	
	byte []decrypt(byte[] data)throws Exception
	{
		 cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		 ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		 CipherOutputStream cOut = new CipherOutputStream(bOut, cipher);
		 cOut.write(data);
		 cOut.close();
		 return bOut.toByteArray();
		// System.out.println("plain : " + new String(bOut.toByteArray()));
	}
	
	   public static void main(String[] args)
			{

				try {	assert false ; throw new RuntimeException("-ea") ; }
				catch (AssertionError e){}
				String data ="chiffrement Aes avec mode counter avec des cle 128 192 ou 256 bits";
				
				Random rnd = new SecureRandom();
				BigInteger key = new BigInteger(127, rnd);//191 ou 255
				BigInteger nonce = new BigInteger(127, rnd);
				try{
				
				AES_CTR c  = new AES_CTR(key.toByteArray(),nonce.toByteArray());
					System.out.println("==================text de depart====================");
					// data = "www.java2s.com";
					System.out.println(data);
					byte[] cipher = c.encrypt(data.getBytes());
					
					System.out.println("==================chiffré===========================");
					
					System.out.println(cipher);
					
					System.out.println("==================déchiffré=========================");
					byte [] plain  = c.decrypt(cipher);
					System.out.println(new String(plain));
				}catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
			}
}
