package Chord
import java.security.MessageDigest
import java.util.zip.CRC32
import scala.collection.immutable.TreeMap
import scala.collection.mutable.ArrayBuffer
object chordtestsheet {

  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  var crc32 = new CRC32();                        //> crc32  : java.util.zip.CRC32 = java.util.zip.CRC32@6f440a7a
			crc32.reset();
			crc32.update("50000".getBytes());
			var code = crc32.getValue();
                                                  //> code  : Long = 2185026437
			code &= (0xffffffffffffffffL >>> (64 - 32))
			var value : Array[Byte] = new Array[Byte](32 / 8)
                                                  //> value  : Array[Byte] = Array(0, 0, 0, 0)
			for (i <- 0 to value.length-1)
			{
				value(value.length - i - 1) =  ((code >> 8 * i) & 0xff).toByte
			}
			//System.out.println(value.toString())
			value                     //> res0: Array[Byte] = Array(-126, 60, -37, -123)
			
		var node = new ChordNode("http://192.168.1.14:50000");
                                                  //> node  : Chord.ChordNode = ChordNode[ID=http://192.168.1.14:50000,KEY=1243920
                                                  //| 744]
	  var node1 = new ChordNode("http://192.168.1.14:50001");
                                                  //> node1  : Chord.ChordNode = ChordNode[ID=http://192.168.1.14:50001,KEY=102573
                                                  //| 9262]
	  var node2 = new ChordNode("http://192.168.1.14:50002");
                                                  //> node2  : Chord.ChordNode = ChordNode[ID=http://192.168.1.14:50002,KEY=275427
                                                  //| 1300]
    var node3 = new ChordNode("http://192.168.1.14:50003");
                                                  //> node3  : Chord.ChordNode = ChordNode[ID=http://192.168.1.14:50003,KEY=354300
                                                  //| 1298]
    var node4 = new ChordNode("http://192.168.1.14:50004");
                                                  //> node4  : Chord.ChordNode = ChordNode[ID=http://192.168.1.14:50004,KEY=129666
                                                  //| 1873]
    var node5 = new ChordNode("http://192.168.1.14:50005");
                                                  //> node5  : Chord.ChordNode = ChordNode[ID=http://192.168.1.14:50005,KEY=978210
                                                  //| 279]
    var node6 = new ChordNode("http://192.168.1.14:50006");
                                                  //> node6  : Chord.ChordNode = ChordNode[ID=http://192.168.1.14:50006,KEY=27393
                                                  //| 46525]
    var node7 = new ChordNode("http://192.168.1.14:50007");
                                                  //> node7  : Chord.ChordNode = ChordNode[ID=http://192.168.1.14:50007,KEY=35609
                                                  //| 75563]
    var node8 = new ChordNode("http://192.168.1.14:50008");
                                                  //> node8  : Chord.ChordNode = ChordNode[ID=http://192.168.1.14:50008,KEY=11575
                                                  //| 75002]
    var node9 = new ChordNode("http://192.168.1.14:50009");
                                                  //> node9  : Chord.ChordNode = ChordNode[ID=http://192.168.1.14:50009,KEY=87189
                                                  //| 1404]
	  var list = new ArrayBuffer[ChordNode]() //> list  : scala.collection.mutable.ArrayBuffer[Chord.ChordNode] = ArrayBuffer
                                                  //| ()
     list += node                                 //> res1: scala.collection.mutable.ArrayBuffer[Chord.ChordNode] = ArrayBuffer(C
                                                  //| hordNode[ID=http://192.168.1.14:50000,KEY=1243920744])
     list += node1                                //> res2: scala.collection.mutable.ArrayBuffer[Chord.ChordNode] = ArrayBuffer(C
                                                  //| hordNode[ID=http://192.168.1.14:50000,KEY=1243920744], ChordNode[ID=http://
                                                  //| 192.168.1.14:50001,KEY=1025739262])
     list -= node                                 //> res3: scala.collection.mutable.ArrayBuffer[Chord.ChordNode] = ArrayBuffer(C
                                                  //| hordNode[ID=http://192.168.1.14:50001,KEY=1025739262])
     
     var temp = new ArrayBuffer[ChordNode]()      //> temp  : scala.collection.mutable.ArrayBuffer[Chord.ChordNode] = ArrayBuffer
                                                  //| ()
     temp = list
     
}