package project2

import java.net.Socket
import java.net.URL
import java.net.InetAddress
import java.io.PrintWriter
import java.io.BufferedReader
import java.io.ObjectInputStream
import java.io.IOException
import scala.reflect.io.File
import java.io.InputStreamReader
import scala.reflect.io.Path
import java.io.FileInputStream
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import scala.util.control.Breaks._
import java.io.OutputStreamWriter
import scala.collection.mutable.ArrayBuffer
import java.io.FileReader

class ServerThread(var socket : Socket, var serverName:String ,var listenPort:Int) extends Thread {
	var chordnode : ChordNode = null;
	var key : ChordKey = null;
	var serviceType : String = null;
	//client being servicing
	var client :String = null;
	var Hash = new Hash
	var clientPort : Int = -1;
	var IncomingServer:String = null;
	var url = new URL("http", InetAddress.getByName(serverName).getHostAddress(), 
        		              listenPort, "");
    chordnode = new ChordNode(url.toString());
   
    
    override def run = {
		try{
		  var out = new PrintWriter(socket.getOutputStream(), true);
		  var in = new BufferedReader(
		        new InputStreamReader(socket.getInputStream()));
		  var inputLine, outputLine : String =null;
		  var clientservice = new processRequest
		  clientservice.db.setPath(System.getProperty("user.dir")+
		          "/data/"+serverName+listenPort);
		  clientservice.setOutStream(out);
		  clientservice.setInStream(in);
		  var port = socket.getPort();
        //read chord.txt is necessary,because one server only have one chord.txt shared by
        //all the thread, if fire up a thread, it should read the chord.txt as a global variable
          var chordExist = Path(System.getProperty("user.dir")+
	          "/data/"+serverName+listenPort+"/chord.txt").isFile;
        if(chordExist)
        {
            try
    		{
              var inputFile = new ObjectInputStream(
                            new FileInputStream(System.getProperty("user.dir")+
	        		          "/data/"+serverName+listenPort+"/chord.txt"));
    		  chordnode.setSuccessor(inputFile.readObject().asInstanceOf[ChordNode]);
    		  chordnode.setPredecessor(inputFile.readObject().asInstanceOf[ChordNode]);
    		  chordnode.setFingerTable(inputFile.readObject().asInstanceOf[FingerTable]);
    		} catch{
    		  case ex : IOException => ex.printStackTrace()
    		}
        }
        
        //start conversation here
        out.println("Welcome");
        inputLine = in.readLine(); //register ip
        if(inputLine.equals("FILESFROMSERVER"))
        {
        	var fromPred ="";
        	var rcvfilename : String = null;
        	var rcvlockstatus : String = null;
        	var rcvlockowner : String = null;
        	
        	breakable{
        	while((fromPred = in.readLine())!=null)
        	{
        	    if(fromPred.equals("ENDTRANSFER"))
        	    	break;
        	    if(fromPred.split(" ")(0).equals("FILE"))
        	    {
        	    	
        	    	rcvfilename = fromPred.split(" ")(1);
        	    	rcvlockstatus = fromPred.split(" ")(2);
        	    	
        	    	if(rcvlockstatus.equals("ReadLock"))
        	    	{
    	    		rcvlockowner = in.readLine();
    	    		var line = "";
    	    		try {
    	 			  var writer = new BufferedWriter(new OutputStreamWriter(
    	 			          new FileOutputStream(System.getProperty("user.dir")+
	 	    				          "/data/"+serverName+listenPort+"/"+rcvfilename), "utf-8"));
    	 				
    	 			   while( !line.equals("ENDFILE"))
         	    		{
       	 				 line = in.readLine();
       	 				if(line.equals("ENDFILE"))
       	 				{
       	 				  break;
       	 				}	
          	 				 writer.write(line);
       	 				 //System.out.println(line);
   	 			    	 writer.newLine();	
   	 			    	
         	    		}
       	 			    writer.flush();
       	 			    writer.close();
    	 			} catch{
		               case ex : IOException => ex.printStackTrace()
		            }
    	    		var curState = new MetaData(rcvlockstatus);
    	    		clientservice.db.updateMetaFile(rcvfilename, curState);
    	    	    clientservice.db.updateReadLock(rcvfilename, rcvlockowner);
        	    		
        	    	}else if(rcvlockstatus.equals("WriteLock"))
        	    	{
    	    		rcvlockowner = in.readLine();
    	    		var line = "";
    	    		try {
    	 			   var writer = new BufferedWriter(new OutputStreamWriter(
    	 			          new FileOutputStream(System.getProperty("user.dir")+
	 	    				          "/data/"+serverName+listenPort+"/"+rcvfilename), "utf-8"));
    	 				
    	 			   while( !line.equals("ENDFILE"))
         	    		{
       	 				 line = in.readLine();
       	 				if(line.equals("ENDFILE"))
       	 				{
       	 				  break;
       	 				}	
          	 		     writer.write(line);
       	 				 //System.out.println(line);
   	 			    	 writer.newLine();	
   	 			    	
         	    		}
       	 			    writer.flush();
       	 			    writer.close();
    	 			} catch{
		               case ex : IOException => ex.printStackTrace()
		            }
    	    		var curState = new MetaData(rcvlockstatus);
    	    		clientservice.db.updateMetaFile(rcvfilename, curState);
    	    	    clientservice.db.updateWriteLock(rcvfilename, rcvlockowner);
        	    		
        	    	}else//no lock on file
        	    	{
        	    		System.out.println("Recv: "+ rcvfilename);
        	    		var line = "";
        	    		try {
        	 			   var writer = new BufferedWriter(new OutputStreamWriter(
        	 			          new FileOutputStream(System.getProperty("user.dir")+
		 	    				          "/data/"+serverName+listenPort+"/"+rcvfilename), "utf-8"));	
        	 		
        	 			   while( !line.equals("ENDFILE"))
          	    		{
        	 				 line = in.readLine();
        	 				if(line.equals("ENDFILE"))
        	 				{
        	 				  break;
        	 				}	
           	 				 writer.write(line);
        	 				 //System.out.println(line);
    	 			    	 writer.newLine();	
    	 			    	
          	    		}
        	 			    writer.flush();
        	 			    writer.close();
        	 			}catch{
		               case ex : IOException => ex.printStackTrace()
		            }
        	    		var curState = new MetaData(rcvlockstatus);
        	    		clientservice.db.updateMetaFile(rcvfilename, curState);
        	    	}
        	    	
        	    }
        	}
          }
          System.out.println("FILEs FROM PREDECESSOR");
        	//socket.close();//job done	
        }
        else if(inputLine.equals("ChordUpdate"))
        {	
        	var succ : ChordNode = null;
        	var pred : ChordNode = null;
        	var obj : Any = null;
        	var ftable : FingerTable = null;
        	try
        	{
        	 var ooi = new ObjectInputStream(socket.getInputStream());
        	 obj = ooi.readObject();// read succ
        	 succ = obj.asInstanceOf[ChordNode];
        	 obj = ooi.readObject();//read pred
        	 pred = obj.asInstanceOf[ChordNode];
        	 obj = ooi.readObject(); //read ftable
        	 ftable = obj.asInstanceOf[FingerTable];
        	 chordnode.setSuccessor(succ);
			 chordnode.setPredecessor(pred);
			 chordnode.setFingerTable(ftable);
			 ooi.close
			 	//chordnode.printFingerTable(System.out);
        	}catch{
		               case ex : IOException => ex.printStackTrace()
		            }

        	var dir = Path(System.getProperty("user.dir")+
   		          "/data/"+serverName+listenPort);
        	if(!dir.isDirectory)
        		dir.createDirectory(true, true)
        	//write chord.txt
        	try 
	        {
        	  var writer = new ObjectOutputStream(
			               new FileOutputStream(System.getProperty("user.dir")+
	        		        "/data/"+serverName+listenPort+"/chord.txt"));
			  writer.writeObject(succ);
			  writer.writeObject(pred);
			  writer.writeObject(ftable);
			  writer.close()
			} catch{
		               case ex : IOException => ex.printStackTrace()
		    }
        	
        	//load newest chordnode information.
    		try
    		{
    		  var inputFile = new ObjectInputStream(
    		                  new FileInputStream(System.getProperty("user.dir")+
    		                  "/data/"+serverName+listenPort+"/chord.txt"));
    			chordnode.setSuccessor(inputFile.readObject().asInstanceOf[ChordNode]);
    			chordnode.setPredecessor(inputFile.readObject().asInstanceOf[ChordNode]);
    			chordnode.setFingerTable(inputFile.readObject().asInstanceOf[FingerTable]);
                inputFile.close()
    		} catch{
		               case ex : IOException => ex.printStackTrace()
		    }
        	//System.out.println("Update from ChordServer!");
        	socket.close();//job done
        }
        else{
	        outputLine = clientservice.processInputline(inputLine.split(" "));
	        out.println(outputLine +" "+ port); // welcome client ip port
	        System.out.println(clientservice.client +":"+port+" "+inputLine);
	        //while loop communication
	        var running = true;
	       
	        breakable{
	        while (running) {
	          inputLine = in.readLine()
	    	  var operation : String =  inputLine.split(" ")(0)
	    	  if(operation.equals("READ")||operation.equals("READLOCK")||
	    			operation.equals("WRITE") || operation.equals("WRITELOCK")||
	    			operation.equals("RELEASELOCK")||operation.equals("DELETE")||
	    			operation.equals("UPLOAD"))
	    		serviceType = "FILELOOKUP";
	    	else 
	    		serviceType = "LOCALSERVICE";
	    	
	        serviceType match 
	        {
	        	case "FILELOOKUP" =>
        		//make file key
        		//System.out.println("File Lookup Operation");
        		//System.out.println(inputLine);
        		var file = inputLine.split(" ")(1);
        		var fileKey = new ChordKey(file);
        		var ftable = chordnode.getFingerTable();
        		var f1,f2 : Finger = null;
        		var targetnode : ChordNode = null;
        		//local lookup
        		//System.out.println(chordnode);
        		var predkey:ChordKey = null;
        		if(chordnode.getPredecessor() == null)
        		    predkey = chordnode.getNodeKey();
        		else 
        			predkey = chordnode.getPredecessor().getNodeKey();
                //check if file belongs to this node
        		//if so, do a local lookup operation
        		if(fileKey.isBetween(predkey,chordnode.getNodeKey())
        				|| predkey.compareTo(chordnode.getNodeKey()) == 0)
        		{
        			// deal with upload later
        			//System.out.println("Local LookUp");
        			outputLine = clientservice.processInputline(inputLine.split(" "));
        			System.out.println(clientservice.client +":"+port+" "+inputLine);
            		out.println(outputLine);
        		}
        		else
        		{
        			//file is not on local, go to other node for help
            		//first check ftable to find node responsible for this filekey
        			//System.out.println("Remote LookUp");
        		for(i <- 0 to Hash.KEY_LENGTH -1)
        		{
    			  f1 = ftable.getFinger(i);
    			  if( i == 31 )
    				f2 = ftable.getFinger(0);
    			  else
    				f2 = ftable.getFinger(i+1);
    			  if(fileKey.isBetween(f1.getStart(),f2.getStart()))
    			  {
    				targetnode = f1.getNode();			
    				break;
    			  }    		
        		}
            		System.out.println("target node:"+targetnode);
	        		//find or not , ask client go to the node for help   
	    			var id = targetnode.getNodeId().substring(7,targetnode.getNodeId().length());
	    			var nodeIP = id.split(":")(0);
	    			var nodePort = id.split(":")(1).toInt;
	    			//goto ip port
	    			out.println("GOTO " + nodeIP+" "+nodePort );
	    			running = false;
	    			//socket.close();//job done!	
	    			break;
        		}	
	        	case "LOCALSERVICE" =>
	        	{
        		//System.out.println("Local Operation");
        		if (inputLine.equals("BYE"))
                {
                	System.out.println("");
                	out.println(this.getName() +":BYE");
                	running = false;
                	System.out.println("Server is listenning on port "+listenPort);	
                    //break;
                } 
        		
        		if(inputLine.equals("SHUTDOWN SERVER"))
        		{
        			outputLine = clientservice.processInputline(inputLine.split(" "));
        			System.out.println(clientservice.client +":"+port+" "+inputLine);
        			//out.println(outputLine);	
        			//transfer file to its successor
	        		var successor : ChordNode = null;
	        		
	        		if(chordnode.getSuccessor() != null && chordnode.getPredecessor() != null
	        				&& !chordnode.getSuccessor().equals(chordnode))
	        		{
	        			successor = chordnode.getSuccessor();
	        			var id = successor.getNodeId().substring(7,successor.getNodeId().length());
	        			var succIP = id.split(":")(0);
	        			var succPort = id.split(":")(1).toInt;
	        			System.out.println("Succ "+succIP + succPort);
	        			out.println("GOTO " + succIP+" "+succPort );	
	        			try
	        			{
	        			  var clientSocket = new Socket(succIP, succPort);
	        			  var sout = new PrintWriter(clientSocket.getOutputStream(), true);
	        			  var sin = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        					//transfer file to my successor
        				  sin.readLine();//welcome
        					//notify it is an inter-server file transfer
        				  sout.println("FILESFROMSERVER"); 
        					//retrieve files and locks from db
        				  var files  = clientservice.db.metafile;
        				  var rlock = clientservice.db.rmap;
        				  var wlock  = clientservice.db.wmap; 
        				  for(it <- files)
        				  {
    				    	 var record = it._2
    				    	 var fname = it._1
    				    	 var lockstatus = record.lock;
    				    	 var lockowner = "";
    				    	 if(lockstatus.equals("ReadLock"))
				    		 {
				    			 var owners = rlock(fname);
				    			 for(owner <- owners)
				    			 {
				    				 lockowner  = lockowner + " "+owner;
				    			 }
				    			 
				    		 }else if(lockstatus.equals("WriteLock"))
				    		 {
				    			 lockowner = wlock(fname);
				    		 }
				    		 
    				    	 //if the file are not system info files, transfer it to successor
    				    	 if(!fname.equals("ReadLock.txt")&& !fname.equals("WriteLock.txt")&&
    				    			!fname.equals("chord.txt")&& !fname.equals("metafile.txt")&&
    				    			!fname.equals(".DS_Store"))
    				    	 {
    				    		 System.out.println("SEND FILE "+fname);
    				    		 if(lockstatus.equals("ReadLock"))
    				    		 {
    				    			sout.println("FILE "+fname + " ReadLock ");
    				    			sout.println(lockowner);
    				    			 //send files
    				    			try
    				 	    		{
    				 	    		  var inputFile = new BufferedReader(
    				 	    		                  new FileReader(System.getProperty("user.dir")+
    				 	    				          "/data/"+serverName+listenPort+"/"+fname))
    				 	    		  var line : String = null;
    				 	    		  while ((line = inputFile.readLine()) != null) {
    				 	    			sout.println(line);
    				 	    		  }
    				 	    		  sout.println("ENDFILE");
    				 	    			//in.readLine();
    				 	    			inputFile.close();
    				 	    		 } catch {
    				 	    		  case ex: IOException =>
    				 	    		    ex.printStackTrace()
    				 	    		 }	
    				    			 
    				    		 }else if(lockstatus.equals("WriteLock"))
    				    		 {
				    			   sout.println("FILE "+fname + " WriteLock");
				    			   sout.println(lockowner);
				    			   try 
    				 	    	   {
    				 	    	     var inputFile = new BufferedReader(
    				 	    	                   new FileReader(System.getProperty("user.dir")+
    				 	    				       "/data/"+serverName+listenPort+"/"+fname))
    				 	    	     var line : String = null;
    				 	    	     while ( (line = inputFile.readLine())!=null) {
    				 	    	     sout.println(line);
    				 	    	     }
    				 	    	     sout.println("ENDFILE");
    				 	    			//in.readLine();
    				 	    	     inputFile.close();
    				 	    	   } catch{
				 	    		     case ex: IOException =>
				 	    		    ex.printStackTrace()
				 	    		   } 
    				    			 
    				    		 }
    				    		 else // no lock on file
    				    		 {
				    			    sout.println("FILE "+fname + " NULL");
				    			    try
    				 	    	    {
				    			    var inputFile = new BufferedReader(
				    			                   new FileReader(System.getProperty("user.dir")+
    				 	    				       "/data/"+serverName+listenPort+"/"+fname))
    				 	    	    var line : String = null;
    				 	    	    while ((line = inputFile.readLine())!=null) {
    				 	    	     sout.println(line);
    				 	    	    }
    				 	    	    sout.println("ENDFILE");
    				 	    			//in.readLine();
    				 	    		inputFile.close();
    				 	    	    } catch{
				 	    		     case ex: IOException =>
				 	    		    ex.printStackTrace()
				 	    		    } 
    				    		 }
    				    			
        				      }	 
        				    }
        				sout.println("ENDTRANSFER");
        				System.out.println("SEND ALL FILE");
        				}catch {
        				  case e : IOException =>
        					e.printStackTrace();
        					System.err.println("Couldn't get I/O for the connection to "
        					           +succIP+":"+succPort);
        					System.exit(1);
        				}
	        		}
	        		else
	        		{
	        			System.out.println("I AM THE LAST NODE");
	        			out.println(outputLine);	
	        		}
	        		
	        		System.out.println("Notify chord server");
	        		//notify chord server : I leave now!
	        		try
	        		{
	        		  var chordSocket = new Socket("localhost", 40000); //change later
	        		  var cout = new PrintWriter(chordSocket.getOutputStream(), true);
	        		  var cin = new BufferedReader(new InputStreamReader
	        		                   (chordSocket.getInputStream()));
	                    
	                    System.out.println("ChordServer:"+cin.readLine());
	                    cout.println("LEAVE "+InetAddress.getByName(serverName).getHostAddress()
	                    		     +" "+listenPort);//send ip and port to chord
	                    
	        		}catch {
	        		  case e: IOException=>
	                        System.err.println("Could not connect ChordServer");
	                        System.exit(-1);
	                }
        			
        			System.exit(1);
        		}
        		outputLine = clientservice.processInputline(inputLine.split(" "));
        		System.out.println(clientservice.client +":"+port+" "+inputLine);
        		out.println(outputLine);	
        	break;
        	}
	        	
	        case _ => break;
	        }      
	      }
	    }
      }
     socket.close();
    } catch{
	case ex : IOException => ex.printStackTrace()
    }

  }
     
}
