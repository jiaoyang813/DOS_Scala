package Chord

import scala.collection.mutable.ArrayBuffer
import java.io.ObjectInputStream
import scala.collection.immutable.TreeMap
import java.io.PrintStream
import java.io.PrintWriter
import java.io.BufferedReader
import java.io.ObjectOutputStream
import java.io.IOException
import java.net.Socket
import java.io.InputStreamReader
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.FileInputStream 

class Chord extends Serializable {
	var nodeList = new ArrayBuffer[ChordNode]()
	var sortedNodeMap = new TreeMap[ChordKey, ChordNode]();
	var sortedKeyArray : Array[ChordKey] = _
    var removed:ChordNode = _
    
	def createNode(nodeId:String) {
	   
		var node = new ChordNode(nodeId);
		nodeList.append(node)
		
		if (sortedNodeMap.contains(node.getNodeKey)) {
			 System.out.println("Duplicated Key "+ node.getNodeKey);
		}
		
		sortedNodeMap += (node.getNodeKey() -> node)
	}

	def joinChord(id : String) ={
		//refresh nodeList
	  //use temp because nodeList need to be cleared and reconstructed during createNode()
	  var temp = new ArrayBuffer[ChordNode]
	  nodeList += new ChordNode(id)
	  temp.appendAll(nodeList)
	  nodeList.clear
	  sortedNodeMap =  new TreeMap[ChordKey, ChordNode]()
	  sortedKeyArray = null;
	  try {
		for( cn <- temp)
		{
			createNode(cn.nodeId);
		}
		
	  } catch{
	  case ex : ChordException => ex.printStackTrace();
	  } 
	  
	  for ( i <- 1 to nodeList.size - 1) {
		var node = getNode(i);
		node.join(getNode(0));
		var preceding = node.getSuccessor().getPredecessor();
		node.stabilize();
		if (preceding == null) {
			node.getSuccessor().stabilize();
		} else {
			preceding.stabilize();
		}
		}
	    
	    for ( i <- 0 to nodeList.size -1) {
			var node = getNode(i);
			node.fixFingers();
			//node.printFingerTable(System.out);
		}
	}
	
	def leaveChord(id : String) ={
	    var temp = new ArrayBuffer[ChordNode]
	    
		//refresh nodelist
		removed = new ChordNode(id);
		//System.out.println("removed node id " + id)
		//System.out.println("nodelist size " + nodeList.size)
		nodeList -= removed;
		temp.appendAll(nodeList)
		nodeList.clear
		//System.out.println("nodelist size after remove " + nodeList.size)
		sortedNodeMap  = new TreeMap[ChordKey, ChordNode]()
		sortedKeyArray = null;
		for (cn <- temp) {
	     createNode(cn.getNodeId);
	    }
	 
	
		for (i <- 1 to nodeList.size - 1) {
			var node = getNode(i);
			node.join(getNode(0));
			var preceding = node.getSuccessor().getPredecessor();
			node.stabilize();
			if (preceding == null) {
				node.getSuccessor().stabilize();
			} else {
				preceding.stabilize();
			}
		}
	
		for (i <- 0 to nodeList.size - 1) {
			var node = getNode(i);
			node.fixFingers();
		}
  }
	
	def notifyAllPeers() ={
		//System.out.println("start broadcast");
		//String from=null;
		var log = System.out;
		log = new PrintStream("chordresult.log");
		nodeList -= removed;
		for( node <- nodeList)
		{
			var id = node.nodeId.substring(7,node.nodeId.length());
			var ip = id.split(":")(0);
			var port = Integer.parseInt(id.split(":")(1));
			//System.out.println(ip+port);
			try
			{
			  var clientSocket = new Socket(ip, port);
				var out = new PrintWriter(clientSocket.getOutputStream(), true);
				var in = new BufferedReader(
					        new InputStreamReader(clientSocket.getInputStream()));
				var oos = 
						new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
				//transfer succ, pred and fingertable
				in.readLine();//welcome
				out.println("ChordUpdate"); //send succ and pred first;
				//System.out.println(from);
				//send successor, predecessor
				oos.writeObject(node.successor);
				//System.out.println("Send");
				oos.writeObject(node.predecessor);
				//System.out.println("Send");
				//send finger table
				oos.writeObject(node.fingerTable);
				node.printFingerTable(log);
				oos.flush();
			}catch {
			  case ex : IOException => 
			    ex.printStackTrace();
				System.err.println("Couldn't get I/O for the connection to " +ip+":"+port);
			} 
			
			//System.out.println("Notify all");
				
		}
	}
	
	def save() =
	{
		//save object to disc
		try{
		    var writer = new ObjectOutputStream(
		          new FileOutputStream(System.getProperty("user.dir")+
        		          "/chord.txt"));
			  
		    writer.writeObject(nodeList);
		    writer.writeObject(sortedNodeMap);
		    writer.writeObject(sortedKeyArray);
		    writer.flush();
			} catch {
			  case ex : IOException => 
			    ex.printStackTrace();
				//System.err.println("Couldn't get I/O for the connection to " +ip+":"+port);
			} 
		
	}
	
	//read object in mem if any
	def read() =
	{
		try// right path!!!!!!!!
		{
		  var inputFile = 
				new ObjectInputStream(new FileInputStream(
						System.getProperty("user.dir")+"/chord.txt"));
			nodeList = inputFile.readObject().asInstanceOf[ArrayBuffer[ChordNode]];
			sortedNodeMap = inputFile.readObject().asInstanceOf[TreeMap[ChordKey, ChordNode]];
			sortedKeyArray=inputFile.readObject().asInstanceOf[Array[ChordKey]];
			inputFile.close();
		} catch {
		  case ex : IOException => 
		    ex.printStackTrace()
			  // report
		}
	}
	def getNode(i : Int) : ChordNode = {
	    nodeList(i);
	}

	//public int getChordSize(){ return nodeList.size();}
	
	def getSortedNode(i : Int) : ChordNode = {
		if (sortedKeyArray == null) {
			sortedKeyArray = sortedNodeMap.keySet.toArray;
		}
		sortedNodeMap(sortedKeyArray(i));
	}
}