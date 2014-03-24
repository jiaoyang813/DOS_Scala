package Chord

class FingerTable(var node : ChordNode) extends Serializable {
	var hash = new Hash()
	var fingers: Array[Finger] =new Array[Finger](hash.KEY_LENGTH);
    
    //System.out.println(node)
	for (i <- 0  to fingers.length - 1) {
			var start = node.getNodeKey().createStartKey(i);
			fingers(i) = new Finger(start, node);
    }

	def getFinger(i : Int) : Finger = {fingers(i)}

}