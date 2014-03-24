package project2

import java.io.BufferedReader
import java.net.ServerSocket
import scala.reflect.io.File
import java.io.InputStreamReader
import java.net.InetAddress
import scala.reflect.io.Path
import java.net.Socket
import java.io.IOException
import java.io.PrintWriter

object MultiServer {
	def main(args : Array[String]) {
		var userInput : String = null;
		var stdIn =
	            new BufferedReader(new InputStreamReader(System.in))
		var portNumber = -1
		System.out.println("Usage: Designate a port number the server will listen on");
		System.out.println("Please Enter Port Number(1025~65535):");
		System.out.print(">>");				
		portNumber = Integer.parseInt(stdIn.readLine());
		var serverName = InetAddress.getLocalHost().getHostName();	
		//create chord node of this server
		var serverIP = InetAddress.getLocalHost().getHostAddress();
		//URL url = new URL("http", serverIP, portNumber, "");
        var dir = Path(System.getProperty("user.dir") + "/data/"+ serverName+portNumber);
        if(!dir.isDirectory)
        	dir.createDirectory(true, true)
        //contact chordserver to get succ, pred, fingertable.(localhost 40000)
        try{ 
          var serverSocket = new ServerSocket(portNumber);
          System.out.println("Server is listenning on port "+portNumber);
          var listening = true; 
          var chordServer = "localhost";
          var chordPort = 40000;
          var fromChord : String = null;
          var toChord : String = null;
          try
          {
        	var chordSocket = new Socket(chordServer, chordPort); //change later
    	    var out = new PrintWriter(chordSocket.getOutputStream(), true);
    		var in = new BufferedReader(new InputStreamReader(chordSocket.getInputStream()));
            fromChord = in.readLine();
               // System.out.println("ChordServer:"+fromChord);
            out.println("JOIN "+serverIP+" "+portNumber);//send ip and port to chord
            chordSocket.close();
    	   }catch {
    	     case e:IOException =>
             System.err.println("Could not connect ChordServer "+chordServer+":"+chordPort);
             System.exit(-1);
           }
           
    	   while (listening) {
             new ServerThread(serverSocket.accept(),serverName,portNumber).start();
           } 
    	   serverSocket.close()
        } catch{
          case e : IOException =>
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
        
        
		
	}

}
