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
		        //DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

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
			

		        dataOutToClient.close();
			dataSocket.close();
			System.out.println("Data Socket closed");
          }
		}while(!frstln.equals("close"));
		}catch(IOException iox){
		}
		}
		
	}
