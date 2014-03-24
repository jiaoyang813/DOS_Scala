package project2

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket
import scala.io.Source
import java.io.IOException
import java.io.BufferedWriter
import java.net.UnknownHostException
import java.io.OutputStreamWriter
import java.io.FileOutputStream
import scala.util.control.Breaks._

object MyClient
{
    def main(args:Array[String]) :Unit = {
      System.out.println("Client Started");
      var listenPort = 20000;//default client's listening port
	  var serverName : String = null;// server to connect
	  var portNumber = -1;// server's listening port
      var isClientRunning = true;
      var userInput :String = null; //init input
      var fromUser= ""; // communicate input
      var requestLast = ""; //store last fromUser
      var fromServer : String = null;
      var stdIn = new BufferedReader(new InputStreamReader(System.in));
      var clientIp = InetAddress.getLocalHost().getHostAddress();
    while(isClientRunning)
    {
    	if(portNumber < 0)
    	{
	    	System.out.println("Usage: [ServerName] [Port] to connect");
	    	System.out.println("Or Enter SHUTDOWN to Terminate:");
	    	System.out.print(">>");
		    userInput = stdIn.readLine()
		    var token = userInput.split(" ");
		    
		    if(token.length == 2)
		    {
		    	if(token(0) != null&& token(1).toInt >0)
		    	{
			    	serverName = token(0)
			    	portNumber = token(1).toInt;
			    	//System.out.println("send request: "+request);
		    	}
		    }
		    else if(token.length == 1)
		    {
		    	if(token(0).equals("SHUTDOWN"))
		    	{
		    		System.out.println("CLIENT SHUTDOWN");
		    		stdIn.close();
		    		isClientRunning = false;
		    		System.exit(1);
		    	}
		    	else
		    	{	
		    		System.err.println("Something Weird");
		    		System.exit(0);
		    	}
		    		
		    }else
		    {
		    	System.err.println("Something Weird");
		    	System.exit(0);
		    }
    	}
	    //System.out.println("Enter Listenning Port: ");
	    //System.out.print(">>");
	    //listenPort =Integer.parseInt( stdIn.readLine());
	    
		try{ 
		  var clientSocket = new Socket(serverName, portNumber);
		  var out = new PrintWriter(clientSocket.getOutputStream(), true);
	      var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	    
	      fromServer = in.readLine();
	   // System.out.println(serverName+":"+portNumber+" "+fromServer);
	      out.println("REGISTER "+ clientIp+" "+listenPort);
          
	      breakable{
	      while ( fromServer != null) {
	        fromServer = in.readLine()
    	    var request = requestLast.split(" ")(0);
            if(request.equals("READ")||request.equals("READLOCK")||
    			request.equals("WRITE") || request.equals("WRITELOCK")||
    			request.equals("RELEASELOCK")||request.equals("DELETE")||
    			request.equals("UPLOAD"))
            {
        	  out.println(requestLast);
        	  requestLast = "";
            }
            else{
	           if (fromUser.equals("BYE"))
	           {
	    		out.println(fromUser);
	    		//reset server port
	    		fromUser = "";
	    		portNumber = -1;
	    		serverName = null;
	    		clientSocket.close()
	    		out.close()
	    		in.close()
	        	break
	           }

	        if(fromServer!=null && fromServer.equals("SERVER SHUTDOWN"))
	        {
	        	System.out.println(serverName+":"+portNumber + " "+fromServer);
	        	fromUser = "";
	        	portNumber = -1;
	        	serverName = null;
	        	break
	        }
	        if(fromServer!=null && fromServer.equals("STARTUPLOAD"))
	        {
	        	var FileName = fromUser.split(" ")(1);
	        	try
	    		{
	    		  var inputFile = 
	    				new BufferedReader(new FileReader(System.getProperty("user.dir")+"/file/"+FileName))
	    		  var inputLine : String = null;
	    			while ((inputLine = inputFile.readLine())!=null) {
	    				out.println(inputLine);
	    			}
	    			out.println("ENDFILE");
	    			fromServer = in.readLine();
	    			inputFile.close();
	    		} catch{
	    		  case ex : IOException =>
	    		    ex.printStackTrace()
	    		} 
	        }
	        if(fromServer!=null && fromServer.equals("SENDDIR"))// send directory
	    	{
	        	var dir = "";
	    		while(!(dir = in.readLine()).equals("ENDDIR"))
	    		{
	    			System.out.println(dir);
	    		}
	    	}
	        
	        if(fromServer!=null && fromServer.equals("SENDFILE"))// send directory
	    	{
	        	var FileName = fromUser.split(" ")(1);
	        	var line = "";
	        	System.out.println("---Start of "+FileName+"---");
	        	try {
	 			   var writer = new BufferedWriter(new OutputStreamWriter(
	 			          new FileOutputStream(System.getProperty("user.dir")+"/"+FileName), "utf-8"));
	 				//write it to disc 
	 			   while(!line.equals("ENDFILE")
		    				&& !line.equals("NOFILE"))
		    		{
	 				    line = in.readLine();
	 				    if(line.equals("ENDFILE")
			    				||line.equals("NOFILE"))
	 				    	break;
		    			System.out.println(line);
		    			writer.write(line);
		    			writer.newLine();	
		    		}
	 			System.out.println("---End of "+FileName+"---");
	 			writer.close();
	 			} catch{
	    		  case ex : IOException =>
	    		    ex.printStackTrace()
	    		} 
	    		
	    	}
	        
	        
	        var serverinfo = fromServer.split(" ");
	    	if(serverinfo(0).equals("GOTO"))
	    	{
	    		out.println("BYE");//disconnect from current server
	    		//reset server and port
	    		serverName = serverinfo(1);
	    		portNumber = serverinfo(2).toInt;
	    		System.out.println("goto "+serverName+" "+portNumber);
	    		requestLast = fromUser;// store last user input
	    		break;
	    	}
	    	else
	    	{
	    	    System.out.println(serverName+":"+portNumber + " "+fromServer);
	        	System.out.print(">>");
	            fromUser = stdIn.readLine();
	            if (fromUser != null) {
	            	out.println(fromUser);
	                //System.out.println("Client: " + fromUser);
	            }else{
	            	System.out.println("NULL COMMAND");
	            	out.println("NULL CMD");
	            }
	        
	    	  }
	       }
        }
	   }
        clientSocket.close();
	   } catch{
	     case e:UnknownHostException =>
		    System.err.println("Don't know about host " +serverName);
		    portNumber = -1;
		case e : IOException =>
		    System.err.println("Couldn't get I/O for the connection to " +
		        serverName +":"+ portNumber)
		    portNumber = -1
		  //  System.exit(1);
	   } 
    
	}
    

 }
}      
                
             