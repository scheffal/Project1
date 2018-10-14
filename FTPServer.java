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
            
        
            ServerSocket welcomeSocket = new ServerSocket(3702);
         
            while(true)
            {
                Socket connectionSocket = welcomeSocket.accept();
                
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
			outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		}catch(IOException iox){
			
		}
	
	}
	
	public void run(){
		String frstln;
		boolean done = false;
		try{
		do{
			fromClient = inFromClient.readLine();
            
            		StringTokenizer tokens = new StringTokenizer(fromClient);
            		frstln = tokens.nextToken();
            		port = Integer.parseInt(frstln);
            		clientCommand = tokens.nextToken();
           	
        		if(clientCommand.equals("list:"))
            		{ 
                		Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                		DataOutputStream  dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
                
				System.out.println("FTP Client Connected...\n");
					
				File dir = new File(".");
				File[] files = dir.listFiles();
				for(File file : files) 
				{
					if(file.isFile())
					{
						dataOutToClient.writeUTF(file.getName());
					}
				}	 
                    
		   		dataOutToClient.close();
                     		dataSocket.close();
		   		System.out.println("Data Socket closed");
               		}
 
          		if(clientCommand.equals("retr:"))
          		{
        	  		Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
		        
				OutputStreamWriter dataOutToClient = new OutputStreamWriter(dataSocket.getOutputStream(), "UTF-8");

		        	String fileName = tokens.nextToken();

			
				File file = new File(fileName);
				BufferedReader read = new BufferedReader(new FileReader(file));

				String str;

				while((str = read.readLine()) != null)
				{
					System.out.println(str);
					dataOutToClient.write(str);
				}
				

				System.out.println("File Sent: " + fileName);	
			
				read.close();
		        	dataOutToClient.close();
				dataSocket.close();
				System.out.println("Data Socket closed");
          		}
	  		if(clientCommand.equals("stor:"))
	  		{
		  		Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
		  		DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

		  		String fileStor = tokens.nextToken();
		  		System.out.println(fileStor);

		  		FileOutputStream fos = new FileOutputStream(fileStor);
		
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
		  
	  		}
			if(clientCommand.equals("close"))
			{
				System.out.println("Closing...");
				inFromClient.close();
				outToClient.close();
				connectionSocket.close();
				done = true;
			}

		}while(!done);
		}catch(IOException iox){
			
		
		}

	}
		
}
