package Chord

class Finger(var start:ChordKey, var node :ChordNode) extends Serializable {

	var serialVersionUID = 1696369378218223311L;
	def getStart() : ChordKey = {start}

	def setStart(start:ChordKey) {
		this.start = start;
	}

	def getNode() : ChordNode = {node}

	def setNode( node : ChordNode) {
		this.node = node
	}

}