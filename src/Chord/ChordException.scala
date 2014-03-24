package Chord

class ChordException extends Exception with Serializable {

	def ChordException() = {
	  //super();
	  System.out.println("ChordException")
	}

	def ChordException(message:String , cause:Throwable) = {
		//super(message, cause);
	  System.out.println(message)
	}

	def ChordException( message:String) = {
		//super(message);
	  System.out.println(message)
	}

	def ChordException(cause:Throwable ) = {
		//super(cause);
	}

}