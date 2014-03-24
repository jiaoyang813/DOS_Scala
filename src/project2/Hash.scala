package project2

import java.security.MessageDigest
import java.util.zip.CRC32

class Hash extends Serializable {


	var function = "CRC32"

	var KEY_LENGTH = 32

	def hash(identifier:String) : Array[Byte] = {

		if (function.equals("SHA-1")) {
			try {
				var md = MessageDigest.getInstance(function)
				md.reset()
				var code = md.digest(identifier.getBytes())
				var value : Array[Byte] = new Array[Byte](KEY_LENGTH / 8) 
				var shrink = code.length / value.length
				var bitCount = 1
				var j = 0
				for (j <- 0 to code.length * 8 - 1) 
				{
					var currBit = ((code(j / 8) & (1 << (j % 8))) >> j % 8)
					if (currBit == 1)
						bitCount = bitCount + 1
					if (((j + 1) % shrink) == 0) {
						var shrinkBit = if(bitCount % 2 == 0)  0x0 else 0x1
						value(j / shrink / 8) 
						   = (value(j / shrink / 8) | (shrinkBit << ((j / shrink) % 8))).toByte
						bitCount = 1
					}
				}
				value
			} 
		}
		else if (function.equals("CRC32")) {
			var crc32 = new CRC32();
			crc32.reset();
			crc32.update(identifier.getBytes());
			var code = crc32.getValue();
			code &= (0xffffffffffffffffL >>> (64 - KEY_LENGTH))
			var value : Array[Byte] = new Array[Byte](KEY_LENGTH / 8)
			for (i <- 0 to value.length-1) 
			{
				value(value.length - i - 1) =  ((code >> 8 * i) & 0xff).toByte
			}
			//System.out.println(value.toString())
			value
		}

		else if (function.equals("Java")) {
			var code = identifier.hashCode();
			code &= (0xffffffff >>> (32 - KEY_LENGTH));
			var value : Array[Byte] = new Array[Byte](KEY_LENGTH / 8)
			var i = 0
			for (i <- 0 to value.length - 1) {
				value(value.length - i - 1) = ((code >> 8 * i) & 0xff).toByte
			}
			value
		}
		else 
		  null

	}

	def getFunction() : String = {function}

	def setFunction(func:String ) = {
		if (function.equals("SHA-1")) {
			KEY_LENGTH = 160;
		}
		if (function.equals("CRC32")) {
			KEY_LENGTH = 64;
		}
		if (function.equals("Java")) {
			KEY_LENGTH = 32;
		}
		
		function = func
	}

	def getKeyLength() : Int = { KEY_LENGTH }

	def setKeyLength( keyLength:Int ) = { KEY_LENGTH = keyLength }

}
