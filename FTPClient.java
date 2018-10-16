/*******************************************************
 * FTP Client 
 * CIS 457
 * 10/16/18
 *
 * Authors:
 *	Ali Scheffler
 *	Dylan Shoup
 *
 * This program implements an FTP Client. The program 
 * connects to a server, and has the options to list the 
 * files in the server's current directory, request a file
 * from the server, send a file to the server, and close
 * the connection. 
 * ****************************************************/

import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

class FTPClient { 

    public static void main(String argv[]) throws Exception 
    { 
	//Declare variables
        String sentence; 
        String modifiedSentence; 
        boolean isOpen = true;
        int number=1;
        boolean notEnd = true;
	String statusCode;
	boolean clientgo = true;
	int port1 = 0;   
	int port2 = 0;
	
	//Create stream to read in from user
	BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 

	//Get line from user
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);

	if(sentence.startsWith("connect")){

		Socket ControlSocket = null;

		//Pass the connect command
		String serverName = tokens.nextToken();

		//Check if more tokens
		if(tokens.hasMoreTokens())
		{		
			serverName = tokens.nextToken();
		}else{
			clientgo = false;	
		}
	
		//Get port number for control connection and for data port
		if(tokens.hasMoreTokens())
		{
			port1 = Integer.parseInt(tokens.nextToken());
		}else{
			clientgo = false;
		}
		if(tokens.hasMoreTokens())
		{
			port2 = Integer.parseInt(tokens.nextToken());
		}else{
			clientgo = false;
		}

		if(!clientgo)
		{
			System.out.println("Unable to connect");
		}else{
        		System.out.println("You are connected to " + serverName);
        
			//Create control connection
			ControlSocket= new Socket(serverName, port1);
		}

	while(isOpen && clientgo)
        {      
	      
		//Create output stream 
          	DataOutputStream outToServer = 
          	new DataOutputStream(ControlSocket.getOutputStream()); 
          
		//Create input stream
	  	DataInputStream inFromServer = new DataInputStream(new BufferedInputStream (ControlSocket.getInputStream()));
          
		//Read line from user
    	 	sentence = inFromUser.readLine();
	   
        	if(sentence.equals("list:"))
        	{
				ServerSocket welcomeData = new ServerSocket(port2);
	  		//Send request over control connection
	    		outToServer.writeBytes (port2 + " " + sentence + " " + '\n');
	
			//Create socket on client side for data connection
           	 	
	   	 	Socket dataSocket = welcomeData.accept(); 

 	   	 	DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
			
			while(inData.available() <=0);
		      //Small delay for synchronization 
			Thread.sleep(500);

			//Read in data from server
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
			//Set value of notEnd back to true
			notEnd = true;	

			//Close all streams and sockets
			inData.close();
	 		welcomeData.close();
	 		dataSocket.close();

	 		System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

        	}
         	else if(sentence.startsWith("retr: "))
        	{	
			//Get file name
			StringTokenizer tokenRetr = new StringTokenizer(sentence);
			String fileName = tokenRetr.nextToken();
			fileName = tokenRetr.nextToken();
			
			//Send request over control connection
			outToServer.writeBytes(port2 + " " + sentence + " " + fileName + " " + '\n');
		
			//Create socket on client side for data connection
			ServerSocket welcomeData = new ServerSocket(port2);
			Socket dataSocket = welcomeData.accept();

			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
		
			//Create a stream to write to file
			FileOutputStream fos = new FileOutputStream(fileName);
		        BufferedOutputStream bufOut = new BufferedOutputStream(fos);

			int bytesRead = 0;
			byte[] byteArray = new byte[4096];
			
			if(inData != null)
			{
				bytesRead = inData.read(byteArray, 0, byteArray.length);

			}

			
			//Write only if file not emtpy
			if(bytesRead > 0)
			{
				//Write to file
				bufOut.write(byteArray, 0, bytesRead);
			}

			//Close streams and socket
			bufOut.close();
			inData.close();
			welcomeData.close();
			dataSocket.close();

	 		System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");


		}else if(sentence.startsWith("stor: "))
		{
			//Get filename
			StringTokenizer tokenStor = new StringTokenizer(sentence);
			String fileStor = tokenStor.nextToken();
			fileStor = tokenStor.nextToken();
			
			//Send request over control connection
			outToServer.writeBytes(port2 + " " + sentence + " " + fileStor + " " + "\n");

			//Create socket on client side for data connection
			ServerSocket welcomeData = new ServerSocket(port2);
			Socket dataSocket = welcomeData.accept();

			OutputStreamWriter dataOutToServer = new OutputStreamWriter(dataSocket.getOutputStream(), "UTF-8");

			try{

				//Create file object
				File file = new File(fileStor);

				//Create stream to read in file
				BufferedReader read = new BufferedReader(new FileReader(file));
				String str;

				//Read line from file and send to server
				while((str = read.readLine()) != null)
				{
					dataOutToServer.write(str);
				}

				read.close();
			}catch(IOException e){
				System.out.println(e.toString());
				System.out.println("Could not find file: " + fileStor);
			}		
			
			//Close all streams and socket
			dataOutToServer.close();
			welcomeData.close();
			dataSocket.close();
			
	 		System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");


		}else if(sentence.startsWith("close"))
		{
			//Send request to close to server
			outToServer.writeBytes(port2 + " " + sentence + "\n");
		
			//Close all streams and control socket	
			inFromUser.close();
			outToServer.close();
			inFromServer.close();
			ControlSocket.close();
			System.out.println("Closed");

			//Exit program
			System.exit(0);
		}
		else{
			System.out.println("Invalid command. Options\n retr: file.txt || stor: file.txt || close");
		}
        }
    }
}
}