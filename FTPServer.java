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
            String frstln;
         
	
            while(true)
            {
                Socket connectionSocket = welcomeSocket.accept();
               
                DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            
                  fromClient = inFromClient.readLine();
                 
                  StringTokenizer tokens = new StringTokenizer(fromClient);
                  frstln = tokens.nextToken();
                  port = Integer.parseInt(frstln);
                  clientCommand = tokens.nextToken();
                 	
		  System.out.println(port);

                  if(clientCommand.equals("list:"))
                  { 
                      Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                      DataOutputStream  dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
                      

			File dir = new File(".");
			File[] files = dir.listFiles();
			for(File file : files) 
			{
				if(file.isFile())
				{
					System.out.println(file.getName());
					dataOutToClient.writeBytes(file.getName());
				}
			}
     
                          

                           dataSocket.close();
			   System.out.println("Data Socket closed");
                     }
        
//			......................
             
                if(clientCommand.equals("retr:"))
                {
                //..............................
		//..............................
		 
		}
	    }
	}
}
    
