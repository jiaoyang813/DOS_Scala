package project2

import java.net.InetAddress
import java.io.PrintWriter
import java.io.BufferedReader
import java.net.ServerSocket
import java.net.Socket
import java.io.InputStreamReader
import java.io.IOException
import java.net.URL

object ChordServer {
   
	def main(args : Array[String] ) : Unit = {
		var chord= new Chord;
		// TODO Auto-generated method stub
		var stdIn =
	            new BufferedReader(new InputStreamReader(System.in));
		var portNumber = -1;
		var serverIP ="localhost";
		System.out.println("This Is A Chord Server!");
		System.out.println("Usage: Designate a port number the server will listen on");
		System.out.println("Please Enter Port Number(1025~65535):");
		System.out.print(">>");				
		try {
			portNumber = Integer.parseInt(stdIn.readLine());
			serverIP = InetAddress.getLocalHost().getHostAddress();
		} catch{
		  case e:NumberFormatException =>
		    e.printStackTrace();
		  case ex:IOException =>
		    ex.printStackTrace()
		} 
		var running = true;
		System.out.println("ChordServer is listenning on port "+ portNumber);
		var from : String = null;
		while(running){
		try  { 
		  var serverSocket = new ServerSocket(portNumber);	
		  var clientSocket = serverSocket.accept();
	      var out = new PrintWriter(clientSocket.getOutputStream(), true);
	      var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
          out.println("Welcome to Chord Server "+serverIP+":"+portNumber); 
		  from = in.readLine();//get ip and port
		  if( !from.equals(null) && from.split(" ")(0).equals("JOIN"))
		  { //JOIN IP + port
		    //create chord ring
		    var ip = from.split(" ")(1);
		    var port = from.split(" ")(2).toInt;
		    var url = new URL("http", ip, port, "");
		    chord.joinChord(url.toString());
		    //notify all node!
		    chord.notifyAllPeers();
		    System.out.println("Add Node "+url);
	      }
		    
		  if(!from.equals(null) && from.split(" ")(0).equals("LEAVE"))
		  { 
		    var ip = from.split(" ")(1);
		    var port = from.split(" ")(2).toInt;
		    var url = new URL("http", ip, port, "");
		    chord.leaveChord(url.toString()); 
		    //notify all node!
		    chord.notifyAllPeers();
		    System.out.println("Removed Node "+url);
		  }
		  serverSocket.close()
		    //chord.save();
        } catch{
          case e:IOException => 
             System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        } 
	  }	
	}

}