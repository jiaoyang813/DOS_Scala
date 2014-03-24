package project1

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket
import scala.io.Source


object MyClient
{
    def main(args:Array[String]) :Unit = {
      
    	System.out.println("Client Initialized!")
		var isClientRunning = true
	    var userInput = ""
	    var uploadFileName = ""
	    var clientIp = InetAddress.getLocalHost().getHostAddress() //client's ip address
	    var stdIn = 
	            new BufferedReader(new InputStreamReader(System.in))
       
      while(isClientRunning)
    {
    	System.out.println("Usage: [ServerName] [Port] [Request]")
    	System.out.println("Or Enter SHUTDOWN to Terminate:")
    	System.out.print(">>")
	    userInput = stdIn.readLine()
	    var request = ""
	    var token = userInput.split(" ")
	    var serverName = ""// server name that client want to connect
	    var portNumber = -1
	    if(token.length >=3)
	    {
	    	if(token(0) != null&& Integer.parseInt(token(1)) >0)
	    	{
		    	serverName = token(0);
		    	portNumber = Integer.parseInt(token(1))
		    	var i = 0
		    	for( i <- 2 to token.length-1)
		    		request += token(i) +" "
		    	System.out.println("send request: "+request)
	    	}
	    }
	    else if(token.length == 1)
	    {
	    	if(token(0).equals("SHUTDOWN"))
	    	{
	    		System.out.println("CLIENT SHUTDOWN")
	    		stdIn.close()
	    		isClientRunning = false
	    		System.exit(1)
	    	}
	    }
	    else
	    {
	    	System.err.println("Something Weird")
	    	isClientRunning = false
	    }
    	
	    if(serverName.equals(null)||portNumber < 0)
	    {
	    	System.err.println("Invalid server name and port number");
	    	System.exit(1);
	    }
    	
	   
	        var clientSocket = new Socket(InetAddress.getByName(serverName), portNumber)
	        var out = new PrintWriter(clientSocket.getOutputStream(), true);
	        var in = new BufferedReader(
	        new InputStreamReader(clientSocket.getInputStream()));
		    //boolean running = true;//current tcp connection is running
		   // boolean isConnected = false;
		    portNumber = clientSocket.getPort();
		    // connect to server
        	var fromServer = in.readLine();
        	System.out.println("FromServer: " + fromServer);
        	//register the client
            out.println("REGISTER "+ clientIp + " " + portNumber);
            fromServer = in.readLine();
    		System.out.println("FromServer: " + fromServer);
    		
    		//send request
            out.println(request);
    		fromServer = in.readLine();
    		System.out.println("FromServer: " + fromServer);
    		
    		if(fromServer.equals("UPLOAD START"))
        	{ 
    		 	//upload filename: localhost 50000 upload file.txt
            	if(token(2).equals("UPLOAD") && token.length == 4)
            	{	
            		out.println(token(2)+" "+token(3));
            		uploadFileName = token(3);
            	}
        		uploadFile(uploadFileName,in, out);
        	}
    		//disconnect server
    		out.println("BYE");
    		serverName = null;
    		portNumber = -1;
    		clientSocket.close();           		

        	if(fromServer.equals("SERVER SHUTDOWN") )
        	{
        		//isConnected = false;
        		serverName = null;
        		portNumber = -1;
        		clientSocket.close();
        		//running = false;
        	}	
        	
        	clientSocket.close()
        	out.close()
        	in.close()
        	
	
       }
		}  
      
	def uploadFile(FileName:String, in:BufferedReader,out:PrintWriter) =
	{
		var path = "/Users/yangjiao/scalaworkspace/DOS_Scala" 
		//System.out.println("Start Upload");
		//System.out.println(path+"/file/"+FileName);
		var fileExist = new File(path+"/file/"+FileName).isFile();
		if(!fileExist)
		{
			System.err.println("No such file");
		}
    	//out.println("UPLOAD START");
			for (inputLine<- Source.fromFile(path+"/file/"+FileName).getLines ) {
				out.println(inputLine)
			}
			System.out.println(FileName + " UPLOADED TO SERVER!");
			out.println("UPLOADED");
	}   
    
    
}      
                
             