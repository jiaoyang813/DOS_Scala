package project2

class ChordKey(var identifier : String, var key : Array[Byte]) extends Ordered[ChordKey] with Serializable {
    var hash = new Hash();
    if(identifier != null)
      key = hash.hash(identifier)
    //System.out.println("chordkey "+ key)
	//overload constructor
    def this(k : Array[Byte]) = this(null, k)
    
    def this(id : String) = this(id, null)
    
    
    def getChordKeyById(id:String) ={
      this.identifier = id;
      this.key = hash.hash(this.identifier); 
    }

	def isBetween(fromKey:ChordKey , toKey:ChordKey ) : Boolean = {
		if (fromKey.compare(toKey) < 0) {
			if(this.compare(fromKey) > 0 && this.compare(toKey) < 0) {
			return true
			}
			else 
			  return false
		}
		else if (fromKey.compare(toKey) > 0) {
			if(this.compare(toKey) < 0 || this.compare(fromKey) > 0) {
			return   true
			}
			else
			  return false
		}
		else 
		  false
	}

	/*public ChordKey createStartKey(int index) {
		byte[] newKey = new byte[key.length];
		System.arraycopy(key, 0, newKey, 0, key.length);
		int carry = 0;
		for (int i = (Hash.KEY_LENGTH - 1) / 8; i >= 0; i--) {
			int value = key[i] & 0xff;
			value += (1 << (index % 8)) + carry;
			newKey[i] = (byte) value;
			if (value <= 0xff) {
				break;
			}
			carry = (value >> 8) & 0xff;
		}
		return new ChordKey(newKey);
	}
	*/
	//for 32bit int only
	def createStartKey(index:Int)  = {
	 // System.out.println(key.length)
		var newkey = new Array[Byte](key.length)
		var tempkey = new Array[Byte](key.length)
		var n : Long = 0;
		var int = key.length;
		for(i <- key.length-1 to (0,-1))
		{
		   var value = key(i)& 0xff;
		   n += value << (3-i)*8;
		}
		n = n + Math.pow(2, index).toLong
		for( i <- 3 to (0, -1))
		{  
		   tempkey(i) = (n>>i*8).toByte; 
		}
		
		for(i <- 3 to (0,-1))
		{  
		   newkey(i) = (tempkey(3-i)&0xff).toByte;
		}
		
		new ChordKey(newkey);
	}

	override def compare(that : ChordKey ) : Int = {
    	var left : Long = 0;
		var j = 0;
		for (i <- this.key.length-1  to (0, -1)) {
			left |= this.key(i)<<(8*j) & 0xffL<<(8*j)
			j = j + 1
		}
		
		var right : Long = 0;
		j = 0;
		for (i <- that.key.length-1  to (0, -1)) {
			right |= that.key(i)<<(8*j) & 0xffL<<(8*j)
			j = j + 1
		}	

		if(left - right < 0)
		{ 
		  return -1
		}
		else if(left - right > 0)
		{
		  return 1
		}
		else
		  return 0 
	}

	override def toString() : String = {
		var sb = new StringBuilder();
		if (key.length > 4) {
			var i = 0
			for (i <- 0 to key.length - 1) {
				sb.append(Integer.toString(( key(i)) & 0xff) + ".");
			}
		
		return sb.toString;
			
		}
		else{
			var n : Long = 0;
			var j = 0;
			for (i <- key.length-1  to (0, -1)) {
				n |= key(i)<<(8*j) & 0xffL<<(8*j)
				j = j + 1
			}
			sb.append(n.toString);
		return sb.toString;
		}
	}

	def getIdentifier() : String= { identifier }

	def setIdentifier(identifier:String) = {
		this.identifier = identifier;
	}

	def getKey() : Array[Byte] ={ key }

	def setKey(key : Array[Byte]) =  {
		this.key = key;
	}

}
