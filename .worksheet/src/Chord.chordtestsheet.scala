package Chord
import java.security.MessageDigest
import java.util.zip.CRC32
import scala.collection.immutable.TreeMap
import scala.collection.mutable.ArrayBuffer
object chordtestsheet {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(230); 

  println("Welcome to the Scala worksheet");$skip(27); 
  var crc32 = new CRC32();System.out.println("""crc32  : java.util.zip.CRC32 = """ + $show(crc32 ));$skip(18); ;
			crc32.reset();$skip(37); ;
			crc32.update("50000".getBytes());$skip(32); ;
			var code = crc32.getValue();System.out.println("""code  : Long = """ + $show(code ));$skip(47); ;
			code &= (0xffffffffffffffffL >>> (64 - 32));$skip(53); 
			var value : Array[Byte] = new Array[Byte](32 / 8);System.out.println("""value  : Array[Byte] = """ + $show(value ));$skip(106); 
			for (i <- 0 to value.length-1)
			{
				value(value.length - i - 1) =  ((code >> 8 * i) & 0xff).toByte
			};$skip(56); val res$0 = 
			//System.out.println(value.toString())
			value;System.out.println("""res0: Array[Byte] = """ + $show(res$0));$skip(61); 
			
		var node = new ChordNode("http://192.168.1.14:50000");System.out.println("""node  : Chord.ChordNode = """ + $show(node ));$skip(59); ;
	  var node1 = new ChordNode("http://192.168.1.14:50001");System.out.println("""node1  : Chord.ChordNode = """ + $show(node1 ));$skip(59); ;
	  var node2 = new ChordNode("http://192.168.1.14:50002");System.out.println("""node2  : Chord.ChordNode = """ + $show(node2 ));$skip(60); ;
    var node3 = new ChordNode("http://192.168.1.14:50003");System.out.println("""node3  : Chord.ChordNode = """ + $show(node3 ));$skip(60); ;
    var node4 = new ChordNode("http://192.168.1.14:50004");System.out.println("""node4  : Chord.ChordNode = """ + $show(node4 ));$skip(60); ;
    var node5 = new ChordNode("http://192.168.1.14:50005");System.out.println("""node5  : Chord.ChordNode = """ + $show(node5 ));$skip(60); ;
    var node6 = new ChordNode("http://192.168.1.14:50006");System.out.println("""node6  : Chord.ChordNode = """ + $show(node6 ));$skip(60); ;
    var node7 = new ChordNode("http://192.168.1.14:50007");System.out.println("""node7  : Chord.ChordNode = """ + $show(node7 ));$skip(60); ;
    var node8 = new ChordNode("http://192.168.1.14:50008");System.out.println("""node8  : Chord.ChordNode = """ + $show(node8 ));$skip(60); ;
    var node9 = new ChordNode("http://192.168.1.14:50009");System.out.println("""node9  : Chord.ChordNode = """ + $show(node9 ));$skip(43); ;
	  var list = new ArrayBuffer[ChordNode]();System.out.println("""list  : scala.collection.mutable.ArrayBuffer[Chord.ChordNode] = """ + $show(list ));$skip(18); val res$1 = 
     list += node;System.out.println("""res1: scala.collection.mutable.ArrayBuffer[Chord.ChordNode] = """ + $show(res$1));$skip(19); val res$2 = 
     list += node1;System.out.println("""res2: scala.collection.mutable.ArrayBuffer[Chord.ChordNode] = """ + $show(res$2));$skip(18); val res$3 = 
     list -= node;System.out.println("""res3: scala.collection.mutable.ArrayBuffer[Chord.ChordNode] = """ + $show(res$3));$skip(51); 
     
     var temp = new ArrayBuffer[ChordNode]();System.out.println("""temp  : scala.collection.mutable.ArrayBuffer[Chord.ChordNode] = """ + $show(temp ));$skip(17); 
     temp = list}
     
}
