package project2

import java.io.PrintStream

class ChordNode(var nodeId : String) extends Serializable {

	var serialVersionUID = -6693578100380476701L;

	var nodeKey = new ChordKey(nodeId)
    
	var predecessor : ChordNode =_

	var successor : ChordNode =_

	var fingerTable : FingerTable =_
    var hash = new Hash()
	this.nodeId = nodeId;
	this.nodeKey = new ChordKey(nodeId);
	this.fingerTable = new FingerTable(this);
	this.create();
	
    
	/**
	 * Lookup a successor of given identifier
	 * 
	 * @param identifier
	 *            an identifier to lookup
	 * @return the successor of given identifier
	 */
	def findSuccessor(identifier:String ) : ChordNode = {
		var key = new ChordKey(identifier);
		return findSuccessor(key);
	}

	/**
	 * Lookup a successor of given key
	 * 
	 * @param identifier
	 *            an identifier to lookup
	 * @return the successor of given identifier
	 */
	def findSuccessor(key:ChordKey) : ChordNode = {

		if (this.equals(successor) ) {
			return this
		}
		else if (key.isBetween(this.getNodeKey(), successor.getNodeKey())
				|| key.compare(successor.getNodeKey()) == 0) {
			return successor;
		} else {
			var node = closestPrecedingNode(key);
			if (node.equals(this)) {
				return successor.findSuccessor(key);
			}
			else
				return node.findSuccessor(key);
		}
	}

	def closestPrecedingNode(key: ChordKey) : ChordNode = {
		for (i <- hash.KEY_LENGTH - 1 to (0, -1)) {
			var finger = fingerTable.getFinger(i);
			var fingerKey = finger.getNode().getNodeKey();
			if (fingerKey.isBetween(this.getNodeKey(), key)) {
			  return finger.getNode();
				
			}
		}
		return this;
	}

	/**
	 * Creates a new Chord ring.
	 */
	def create() = {
		predecessor = null;
		successor = this;
	}

	/**
	 * Joins a Chord ring with a node in the Chord ring
	 * 
	 * @param node
	 *            a bootstrapping node
	 */
	def join(node:ChordNode) = {
		predecessor = null;
		successor = node.findSuccessor(this.getNodeId());
	}

	/**
	 * Verifies the successor, and tells the successor about this node. Should
	 * be called periodically.
	 */
	def stabilize() {
		var node = successor.getPredecessor();
		if (node != null) {
			var key = node.getNodeKey();
			if ((this.equals(successor))
					|| key.isBetween(this.getNodeKey(), successor.getNodeKey())) {
				successor = node;
			}
		}
		successor.notifyPredecessor(this);
	}

	def notifyPredecessor(node : ChordNode) {
		var key = node.getNodeKey();
		if (predecessor == null
				|| key.isBetween(predecessor.getNodeKey(), this.getNodeKey())) {
			predecessor = node;
		}
	}

	/**
	 * Refreshes finger table entries.
	 */
	def fixFingers() {
		for (i <- 0 to hash.KEY_LENGTH -1) {
			var finger = fingerTable.getFinger(i);
			var key = finger.getStart();
			finger.setNode(findSuccessor(key));
		}
	}

	    override def toString() : String = {
		var sb = new StringBuilder();
		sb.append("ChordNode[");
		sb.append("ID=" + nodeId);
		sb.append(",KEY=" + nodeKey);
		sb.append("]");
		sb.toString();
	}

   override	def equals(o : Any) : Boolean = {	
	 o.isInstanceOf[ChordNode] && this.getNodeId().equals(o.asInstanceOf[ChordNode].getNodeId)
   }
   
	def printFingerTable(out:PrintStream) {
		out.println("=======================================================");
		out.println("FingerTable: " + this);
		out.println("-------------------------------------------------------");
		out.println("Predecessor: " + predecessor);
		out.println("Successor: " + successor);
		out.println("-------------------------------------------------------");
		var i = 0
		for (i <- 0  to hash.KEY_LENGTH - 1) {
			var finger = fingerTable.getFinger(i);
			out.println(finger.getStart() + "\t" + finger.getNode());
		}
		out.println("=======================================================");
	}

	def getNodeId() : String ={nodeId}

	def setNodeId(nodeId:String ) {
		this.nodeId = nodeId;
	}

	def getNodeKey() : ChordKey ={
		return nodeKey;
	}

	def setNodeKey(nodeKey:ChordKey) {
		this.nodeKey = nodeKey;
	}

	def getPredecessor() : ChordNode ={predecessor}

	def setPredecessor(predecessor:ChordNode) = {
		this.predecessor = predecessor;
	}

	def getSuccessor() : ChordNode = {successor}

	def setSuccessor(successor:ChordNode) {
		this.successor = successor;
	}

	def getFingerTable() : FingerTable = { fingerTable }

	def setFingerTable(fingerTable:FingerTable) {
		this.fingerTable = fingerTable;
	}

}
