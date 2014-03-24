package project1

import java.net.ServerSocket
import java.io.InputStreamReader
import java.io.IOException
import java.io.PrintWriter
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.FileOutputStream
import scala.io.BufferedSource
import java.io.PrintStream
import java.io.BufferedReader

object MyServer
{
  def main(args:Array[String]):Unit = {
   
	  var isRunning = true
	  var port = -1
	  var filename = ""
	  var isUploading = false
	  var stdIn =
              new BufferedReader(new InputStreamReader(System.in))
	  
	  System.out.println("Server Initialized!")
	  var serverSocket = new ServerSocket()
	  while(isRunning){
	    
	    
	   try{ 
	   if(port < 0)
	    {
			System.out.println("Usage: Designate a port number the server will listen on")
			System.out.println("Please Enter Port Number(1025~65535):")
			System.out.print(">>")
			port = Integer.parseInt(stdIn.readLine())
		    serverSocket = new ServerSocket(port)
			System.out.println("Server is listenning on port "+port)
			
	    }
	    else
	    {
	      
		  System.out.println("Server is listenning on port "+port)
	    }
	    
        var clientSocket = serverSocket.accept()
        var out = new PrintStream(clientSocket.getOutputStream())
        var inputStream = new BufferedSource(clientSocket.getInputStream())
	    var in = inputStream.getLines
	    //System.out.println("Client:")
        out.println("TCP Connection Established with Server!")
        var clientservice = processRequest
        var realPort = clientSocket.getPort()
        var outputLine = clientservice.processInput(in.next.split(" "));
	    var clientIP = clientservice.clientIP
	    clientservice.getInputStream(in)
	    System.out.println("Welcome Client "+clientIP + " "+ realPort)
        out.println("Welcome "+clientIP + " "+ realPort)
        var running = true
        while( running )//
    	{
            var inputline = in.next
		var token = inputline.split(" ")
		if(token(0).equals("UPLOAD"))
		{
			filename = token(1)
			System.out.println(clientIP +" "+realPort+ ": "+inputline)
			//clientservice.uploadFile(filename)
			out.println("UPLOAD START")
			//out.println("UPLOAD DONE")
	    		}
	 
	            	System.out.println(clientIP +" "+realPort+ ": "+inputline)
	    	outputLine = clientservice.processInput(token)
	        
	        if(outputLine.equals("BYE"))
	        {		                	
	        	out.println(outputLine)
	        	running = false
	        }
	        else if(outputLine.equals("SERVER SHUTDOWN"))
	        {	
	        	out.println(outputLine)
	        	isRunning = false
	        	inputStream.close
	        	out.close
	        	stdIn.close
	        	clientSocket.close
	        	sys.exit(1)
	        }
	        else 
	        	out.println(outputLine)
	        out.flush()
    	}//end of while
	    
        }catch{
          case e:IOException =>
          println("Exception caught when trying to listen on port "
            + port + " or listening for a connection")
          sys.exit(1)   
        }  
	  }//shutdown server	  
  }
  
}
    