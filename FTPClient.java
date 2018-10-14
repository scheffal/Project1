import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

class FTPClient { 

    public static void main(String argv[]) throws Exception 
    { 
        String sentence; 
        String modifiedSentence; 
        boolean isOpen = true;
        int number=1;
        boolean notEnd = true;
	String statusCode;
	boolean clientgo = true;
	int port1;   
	int port2;
	
	BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);


	if(sentence.startsWith("connect")){
	String serverName = tokens.nextToken(); // pass the connect command
	serverName = tokens.nextToken();
	port1 = Integer.parseInt(tokens.nextToken());
	port2 = Integer.parseInt(tokens.nextToken());
        System.out.println("You are connected to " + serverName);
        
	Socket ControlSocket= new Socket(serverName, port1);
        
	while(isOpen && clientgo)
        {      
	      
          	DataOutputStream outToServer = 
          	new DataOutputStream(ControlSocket.getOutputStream()); 
          
	  	DataInputStream inFromServer = new DataInputStream(new BufferedInputStream (ControlSocket.getInputStream()));
          
    	 	 sentence = inFromUser.readLine();
	   
        	if(sentence.equals("list:"))
        	{
	    		int port = port2 + 2;
	    		outToServer.writeBytes (port + " " + sentence + " " + '\n');
	
           	 	ServerSocket welcomeData = new ServerSocket(port);
	   	 	Socket dataSocket =welcomeData.accept(); 

 	   	 	DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
			
			//TODO Fix - client socket closes before message read
			Thread.sleep(1000);
		       	
			while(notEnd) 
            		{
				if(inData.available() <= 0)
				{	
					notEnd = false;
				}else{
                			modifiedSentence = inData.readUTF();

					System.out.println(modifiedSentence);
				}	
			       
            		}
	
			inData.close();
	 		welcomeData.close();
	 		dataSocket.close();
	 		System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

        	}
         	else if(sentence.startsWith("retr: "))
        	{
			int port = port2 + 2;
			
			StringTokenizer tokenRetr = new StringTokenizer(sentence);
			String fileName = tokenRetr.nextToken();
			fileName = tokenRetr.nextToken();
			System.out.println(fileName);
			
			outToServer.writeBytes(port + " " + sentence + " " + fileName + " " + '\n');


			ServerSocket welcomeData = new ServerSocket(port);
			Socket dataSocket = welcomeData.accept();

			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));


			FileOutputStream fos = new FileOutputStream(fileName);
		        BufferedOutputStream bufOut = new BufferedOutputStream(fos);

			int bytesRead = 0;
			byte[] byteArray = new byte[1024];
			
			if(inData != null)
			{
				bytesRead = inData.read(byteArray, 0, byteArray.length);

			}

			bufOut.write(byteArray, 0, bytesRead);
				
			bufOut.close();
			inData.close();
			welcomeData.close();
	
	 		System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");


		}else if(sentence.startsWith("stor: "))
		{
			int port = port2 + 2;

			StringTokenizer tokenStor = new StringTokenizer(sentence);
			String fileStor = tokenStor.nextToken();
			fileStor = tokenStor.nextToken();
			System.out.println(fileStor);

			outToServer.writeBytes(port + " " + sentence + " " + fileStor + " " + "\n");

			ServerSocket welcomeData = new ServerSocket(port);
			Socket dataSocket = welcomeData.accept();

			OutputStreamWriter dataOutToServer = new OutputStreamWriter(dataSocket.getOutputStream(), "UTF-8");

			File file = new File(fileStor);
			BufferedReader read = new BufferedReader(new FileReader(file));
			String str;
			while((str = read.readLine()) != null)
			{
				System.out.println(str);
				dataOutToServer.write(str);
			}		
			
			dataOutToServer.close();
			read.close();
			//inData.close();
			welcomeData.close();
			
	 		System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");


		}else if(sentence.startsWith("close"))
		{
			int port = port2 + 2;
			outToServer.writeBytes(port + " " + sentence + "\n");

			
			inFromUser.close();
			outToServer.close();
			inFromServer.close();
			ControlSocket.close();
			System.out.println("Closed");
			System.exit(0);
		}
		else{
			System.out.println("Invalid command. Options\n retr: file.txt || stor: file.txt || close");
		}
        }
    }
}
}
