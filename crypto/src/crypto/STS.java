package crypto;

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


import math.Courbe;
import math.Point;

public class STS {
	
	//BigInteger pubPx;
	static int MaxInputlength = 1000;
	byte[] ivBytes = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00,
	        0x00, 0x00, 0x00, 0x00, 0x01 };

	
	
	private void attendre(int temps)  {
		try{
			Thread.sleep(temps*1000);;
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public byte [] receiveBytes(Socket socket, int nb) throws IOException {
		InputStream in = socket.getInputStream(); 
	    DataInputStream dos = new DataInputStream(in);
	    if(nb==0) nb=MaxInputlength;
	    byte []data = new byte[nb];
	    int nbData;
	    do{
	    	nbData = dos.read(data);
	    }while(nbData<=0);
	    byte [] result = new byte[nbData];
	    int i;
	    for(i=0; i<nbData; i++)
	    	result[i] = data[i];
	    return  result;
	}
	
	
	public void sendBytes(byte[] myByteArray, Socket socket) throws IOException {
	    sendBytes(myByteArray, 0, myByteArray.length, socket);
	}
	
	
	public void sendBytes(byte[] myByteArray, int start, int len, Socket socket) throws IOException {
	    if (len < 0)
	        throw new IllegalArgumentException("Negative length not allowed");
	    if (start < 0 || start >= myByteArray.length)
	        throw new IndexOutOfBoundsException("Out of bounds: " + start);
	    // Other checks if needed.

	    // May be better to save the streams in the support class;
	    // just like the socket variable.
	    OutputStream out = socket.getOutputStream(); 
	    DataOutputStream dos = new DataOutputStream(out);

	    dos.writeInt(len);
	    if (len > 0) {
	        dos.write(myByteArray, start, len);
	    }
	}
	void sendKey(Point b,Socket socket) throws IOException
	{
		ObjectOutputStream flux = new ObjectOutputStream(socket.getOutputStream()) ;
		flux.writeObject(b);
	}
	
	Point receiveKey(Socket socket) throws IOException, ClassNotFoundException
	{
		ObjectInputStream flux = new ObjectInputStream(socket.getInputStream()) ;
		return (Point)flux.readObject();
	}
	
	void sendKey(BigInteger b,Socket socket) throws IOException
	{
		ObjectOutputStream flux = new ObjectOutputStream(socket.getOutputStream()) ;
		flux.writeObject(b);
	}
	
	Data receiveData(Socket socket) throws IOException, ClassNotFoundException
	{
		ObjectInputStream flux = new ObjectInputStream(socket.getInputStream()) ;
		return (Data)flux.readObject();
	}
	
	String receiveCtrl(Socket socket) throws IOException, ClassNotFoundException
	{
		ObjectInputStream flux = new ObjectInputStream(socket.getInputStream()) ;
		return (String)flux.readObject();
	}
	
	
	
	void sendCtrl(String b,Socket socket) throws IOException
	{
		ObjectOutputStream flux = new ObjectOutputStream(socket.getOutputStream()) ;
		flux.writeObject(b);
	}
	
	void sendData(Data b,Socket socket) throws IOException
	{
		ObjectOutputStream flux = new ObjectOutputStream(socket.getOutputStream()) ;
		flux.writeObject(b);
	}
	/*BigInteger receiveKey(Socket socket) throws IOException, ClassNotFoundException
	{
		ObjectInputStream flux = new ObjectInputStream(socket.getInputStream()) ;
		return (BigInteger)flux.readObject();
	}
	*/
	
	public boolean  envoieMessageDH(String message,Courbe ec,String serverName, int portData)
	{
	//generation du nombre aléatoire-clé secrete
		BigInteger x;
		 KeyGenerator  kg = new KeyGenerator();
		 x = kg.generatePrivateKey(255, ec);
		 
	// calcul de a*P 
		 Point xG = kg.generatePublicKey(x, ec);
	
		 
	
		//BufferedReader lecteurFichier;
		
		Socket dataSocket/*,ctrlSocket*/;
		try
		{
			dataSocket = new Socket(serverName, portData);
			
			//ctrlSocket = new Socket(serverName, portCtrl);
			
			sendData(new Data(xG), dataSocket);
			System.out.println("Alice envoie xG = "+xG.toString());
			//sendCtrl("ok", ctrlSocket);
			
			//while(!receiveCtrl(ctrlSocket).equals("ok"));
			attendre(6);
		// reception de B = b*P et de la signature
			
			//byte [] recu = receiveBytes(socket,32);
			//BigInteger yG = new BigInteger(recu);
			
			Data d = receiveData(dataSocket);
			
			
			byte[] rc = d.getRc();
			byte[] sc = d.getSc();
			Point yG = d.getP();
			System.out.println("Alicerecoit yG = "+xG.toString());
			BigInteger K = ec.multiplication(yG,x).getX();
			
			
					
			System.out.println("Alice recoit rc et sc .. ");
			
			byte []KeyBytes = K.toByteArray();
			AES_CTR aes_ctr = new AES_CTR(KeyBytes, ivBytes);
			BigInteger r = new BigInteger(aes_ctr.decrypt(rc));
			BigInteger s = new BigInteger( aes_ctr.decrypt(sc));
			System.out.println("Apres dechiffrement on a:");
			System.out.println("r = "+r.toString());
			System.out.println("s = "+s.toString());
			
			String m = yG.getX().toString()+xG.getX().toString();
			System.out.println("Alice cree le message a signer");
			System.out.println("message = "+m);
			
			BigInteger alpha = BigInteger.TEN; //  clé privée au pif
			BigInteger ordre = new BigInteger("8884933102832021670310856601112383279454437918059397120004264665392731659049");
			Signature Sa = new Signature(ec, ordre,alpha);
			Point KpubBob = new Point("5472399817386276573347449402849993086467118689737435599917192313038729185916",
					"3246676142591741845527478667908903521296800664862943805043432031447361844037",true);
			int result = Sa.check_ECDSA(m, r, s, KpubBob);
			if ( result == 1 )
			{
							
				System.out.println("La signature de Bob n'est pas bonne");
				System.exit(0);
			}
			if ( result == -1 )
			{
				System.out.println("Les paramètres envoyés par Bob sont pas bons");
				System.exit(0);
			}
			System.out.println("La signature de Bob est bonne");
			System.out.println("Authentification de Bob reussie...");
			System.out.println("Alice a une clé commune avec Bob et sait que c'est vraiment lui");
			m = xG.getX().toString() + yG.getX().toString();
			BigInteger []tab = Sa.ECDSA(m);
			
				
			byte [] rChiffre = aes_ctr.encrypt(tab[0].toByteArray());
			byte [] sChiffre = aes_ctr.encrypt(tab[1].toByteArray());
			
		// on envoie le signé chiffré 	
			sendData(new Data(rChiffre,sChiffre,null), dataSocket);
			
			//====================================================================================
			
			dataSocket.close();
		//	ctrlSocket.close();
		}catch(FileNotFoundException exc) {
			System.out.println("Fichier introuvable: "+exc.getMessage());
		}
		catch(UnknownHostException exc) {
			System.out.println("Destinataire inconnu: "+exc.getMessage());
		}
		catch(IOException exc) {
			System.out.println("Probleme d'entree-sortie: "+exc.getMessage());
			exc.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	return true;
	}
	public boolean receptionmessageDH(int portData,Courbe ec)
	{
		try {
				
				System.out.println("en attente de connexion...");
				ServerSocket dataServer = new ServerSocket(portData);
				Socket dataSocket = dataServer.accept();
				//ServerSocket ctrlServer = new ServerSocket(portCtrl);
				//Socket ctrlSocket = ctrlServer.accept();
			
				System.out.println("connexion effectuée");
				//attendre(3); //attente de de 3 secondes		
				
				//while(!receiveCtrl(ctrlSocket).equals("ok"));	
				attendre(3);
			// reception de A = a*P
				
				//byte [] recu = receiveBytes(socket,32);
				//BigInteger pubPy = new BigInteger(recu);
				Point xG = receiveData(dataSocket).getP();
				System.out.println("Bob recoit xG ="+xG.toString());
				
				
				
			//generation du nombre aléatoire-clé secrete b
				
				BigInteger y;
				KeyGenerator  kg = new KeyGenerator();
				y = kg.generatePrivateKey(255, ec);
			 
				// calcul de b*P --> yG
				Point yG = kg.generatePublicKey(y, ec);
				
			//concatenation de xG et yG
				String m = yG.getX().toString() + xG.getX().toString() ;
				System.out.println("Bob cree le message a signer");
				System.out.println("message = "+m);
			// calcul de bAB	--> K
				System.out.println("calcul de aB");
				//BigInteger Key = secreteKey.multiply(pubPy).mod(ec.p);//.mod(ec.getK()) le corsps
			
				BigInteger K = ec.multiplication(xG,y).getX();
				
			// Ici on a m et K
			
				BigInteger beta = new BigInteger("2481038759442424424299810242807468076325797668646767699023329088839404"); //  clé privée au pif
				BigInteger ordre = new BigInteger("8884933102832021670310856601112383279454437918059397120004264665392731659049");
				Signature Sb = new Signature(ec, ordre, beta);
				BigInteger []tab = Sb.ECDSA(m);
				
				System.out.println("Apres signature on a:");
				System.out.println("r = "+tab[0].toString());
				System.out.println("s = "+tab[1].toString());
			//on chiffre ave AES_CTR	
				
				byte KeyBytes[] = K.toByteArray();
				AES_CTR aes_ctr = new AES_CTR(KeyBytes, ivBytes);
				byte [] rChiffre = aes_ctr.encrypt(tab[0].toByteArray());
				byte [] sChiffre = aes_ctr.encrypt(tab[1].toByteArray());
				
			// on envoie le signé chiffré et la bP --> yG	
				sendData(new Data(rChiffre,sChiffre,yG), dataSocket);
				//sendCtrl("ok", ctrlSocket);
				
				
				//================================================================
				Data d = receiveData(dataSocket);
				
				
				byte[] rc = d.getRc();
				byte[] sc = d.getSc();
										
				System.out.println("Bob recoit rc et sc .. ");
				
				
				BigInteger r = new BigInteger(aes_ctr.decrypt(rc));
				BigInteger s = new BigInteger( aes_ctr.decrypt(sc));
				
				
				
				m = xG.getX().toString() + yG.getX().toString();
				System.out.println("message = "+m);
				
				Point KpubAlice = new Point ("3584131351290754011512172425937430585085194474922731073157856328499373657501",
						"13971226047517449285394062543883340621918844410010609833146839085587758576",true);
				
				int result = Sb.check_ECDSA(m, r, s, KpubAlice);
				if ( result == 1 )
				{
					System.out.println("La signature de Alice n'est pas bonne");
					System.exit(0);
				}
				if ( result == -1 )
				{
					System.out.println("Les paramètres envoyés par Alice sont pas bons");
					System.exit(0);
				}
				System.out.println("La signature de Alice est bonne");
				System.out.println("Authentification de Alice reussie...");
				System.out.println("Bob a une clé commune avec Alice et sait que c'est vraiment elle");
				
				
		        dataSocket.close();
		       // ctrlSocket.close();
			}catch(FileNotFoundException exc) {
				System.out.println("Fichier introuvable: "+exc.getMessage());
				exc.printStackTrace();
			}
			catch(UnknownHostException exc) {
				System.out.println("Destinataire inconnu: "+exc.getMessage());
			}
			catch(IOException exc) {
				System.out.println("Probleme d'entree-sortie: "+exc.getMessage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	return true;
	}
}
