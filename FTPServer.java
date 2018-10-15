/*************************************************************
 * FTP Server
 * CIS 457
 * 10/16/2018
 *
 * Authors:
 * 	Ali Scheffler
 * 	Dylan Shoup
 *
 * This program implements an FTP Server. The programs creates
 * a socket on the server side, and waits for requests from a 
 * client. When a request from a client comes, another socket
 * is created for the control connection. The program receives
 * a command from a client and acts accordingly. Commands are 
 * sending a list to the client of all files in current 
 * directory, sending a file to a client, receiving a file from 
 * a client, and closing connection. Multithreading is 
 * implemented to allow multiple clients to connect to the 
 * server.
 * **********************************************************/

import java.io.*; 
import java.net.*;
import java.util.*;

public class FTPServer{

	public static void main(String argv[]) throws Exception	
	{
 
            String fromClient;
            String clientCommand;
            byte[] data;
            int port;
            
 	    //Create socket on server side       
            ServerSocket welcomeSocket = new ServerSocket(3702);
         
            while(true)
            {
		//Wait for connection from client
                Socket connectionSocket = welcomeSocket.accept();

		//Create a thread for each client
                ClientHandler handler = new ClientHandler(connectionSocket);
                handler.start();
	    }
	}
}

class ClientHandler extends Thread{
	
	private DataOutputStream outToClient;
	private BufferedReader inFromClient;
	private Socket connectionSocket;
	String fromClient;
    	String clientCommand;
    	byte[] data;
    	int port;
	
	public ClientHandler(Socket connectionSocketIn){
		connectionSocket = connectionSocketIn;
		try{
			//Create input and output stream for client over control connection
			outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            		inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

			System.out.println("User connected" + connectionSocket.getInetAddress());
			
		}catch(IOException iox){
			System.out.println("Error");
		}
	
	}
	
	public void run(){
		String frstln;
		boolean done = false;
		try{
			do{
				//Read line in from client
				fromClient = inFromClient.readLine();

				//Get command
            			StringTokenizer tokens = new StringTokenizer(fromClient);
				frstln = tokens.nextToken();
            			port = Integer.parseInt(frstln);
            			clientCommand = tokens.nextToken();
           
        			if(clientCommand.equals("list:"))
            			{ 
					//Create socket on server side
                			Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);

			
					//Create output stream to client
                			DataOutputStream  dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
					
					//Get all files in current directory
					File dir = new File(".");
					File[] files = dir.listFiles();
					for(File file : files) 
					{
						//Write to client
						if(file.isFile())
						{
							dataOutToClient.writeUTF(file.getName());
						}
					}	 
                    
					//Close all streams and sockets
		   			dataOutToClient.close();
                     			dataSocket.close();
					
               			}
 
          			else if(clientCommand.equals("retr:"))
          			{
					//Create data socket
        	  			Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
					
					OutputStreamWriter dataOutToClient = new OutputStreamWriter(dataSocket.getOutputStream(), "UTF-8");
	
					//Get filename
			        	String fileName = tokens.nextToken();
					
					try{
						//Create file object
						File file = new File(fileName);

						//Create stream to read in from file
						BufferedReader read = new BufferedReader(new FileReader(file));
						String str;

						//Read from file and write out to client
						while((str = read.readLine()) != null)
						{	
							dataOutToClient.write(str);
						}
				
						System.out.println("File " + fileName + " sent");


						//Close all streams and socket			
						read.close();
				        	dataOutToClient.close();
						dataSocket.close();
						
					}catch(FileNotFoundException e)
					{
						System.out.println("File not found: " + fileName);
					}
          			}
	  			else if(clientCommand.equals("stor:"))
	  			{
					//Create data socket
		  			Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
		  			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

					//Get file name
		  			String fileStor = tokens.nextToken();

					//Create stream to write to file
		  			FileOutputStream fos = new FileOutputStream(fileStor);
			  		BufferedOutputStream bufOut = new BufferedOutputStream(fos);
	
			  		int bytesRead = 0;
			  		byte[] byteArray = new byte[4096];

					//Read in from client
		  			if(inData != null)
		  			{
			  			bytesRead = inData.read(byteArray, 0, byteArray.length);
		  			}

					//Write only if file is not empty
					if(bytesRead > 0)
					{
						//Write to file
		  				bufOut.write(byteArray, 0, bytesRead);
					}

					System.out.println(fileStor + " saved");

					//Close streams and socket
		  			bufOut.close();
		  			inData.close();
					dataSocket.close();
		  
	  			}
				else if(clientCommand.equals("close"))
				{
					//Close all streams and control socket
					System.out.println("User" + connectionSocket.getInetAddress() + " disconnected");
					inFromClient.close();
					outToClient.close();
					connectionSocket.close();
					done = true;
				}else{
					System.out.println("Command not found");
				}

		}while(!done);
		}catch(IOException iox){
			
			System.out.println("Error");	
		}

	}
		
}
