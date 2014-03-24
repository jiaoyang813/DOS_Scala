package Chord

import java.net.InetAddress
import java.io.PrintStream
import Chord.ChordException
import java.net.URL

object testChord {
	var HASH_FUNCTION = "CRC32";

	var KEY_LENGTH = 32;

	var NUM_OF_NODES = 10;
  def main(args: Array[String]): Unit = {
    var host = InetAddress.getLocalHost().getHostAddress();
    var port = 50000;
    var chord = new Chord()
    var out = System.out;
	out = new PrintStream("testchordscala.log");
	var hash = new Hash()

	for (i <- 0 to NUM_OF_NODES - 1) {
	  var url = new URL("http", host, port + i, "");
	      chord.createNode(url.toString());
	}
	 
	out.println(NUM_OF_NODES + " nodes are created.");
	
	for ( i <- 0 to NUM_OF_NODES - 1) {
		var node = chord.getSortedNode(i);
		out.println(node);
	}
	
	for (i <- 1 to NUM_OF_NODES - 1) {
		var node = chord.getNode(i);
		//System.out.println(node)
		node.join(chord.getNode(0));
		var preceding = node.getSuccessor().getPredecessor();
		node.stabilize();
		if (preceding == null) {
			node.getSuccessor().stabilize();
		} else {
			preceding.stabilize();
		}
	}
	out.println("Chord ring is established.");

	for (i <- 0 to NUM_OF_NODES - 1) {
		var node = chord.getNode(i);
		node.fixFingers();
	}
	out.println("Finger Tables are fixed.");

	for (i <- 0 to NUM_OF_NODES - 1){
		var node = chord.getSortedNode(i);
		node.printFingerTable(out);
	}

	var url = new URL("http","192.168.1.14", 50000, "");
	chord.nodeList -= new ChordNode(url.toString)
	
	//chord.leaveChord(url.toString());
	//var chordnode = chord.getSortedNode(2);//let 50001 find C.txt
	System.out.println("50000 leaves");
	out.println("50000 leave us");
	for (i <- 0 to NUM_OF_NODES -2) {
		var node = chord.getSortedNode(i);
		node.printFingerTable(out);
	}
  }

}