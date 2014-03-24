package SimpleCS

import java.net.Socket
import java.io.PrintStream
import scala.io.BufferedSource

object Client {

  def main(args: Array[String]): Unit = {
    var socket = new Socket("localhost", 50000)
    var out = new PrintStream(socket.getOutputStream())
    var in = new BufferedSource(socket.getInputStream())
    out.println("hello")
    while(in.getLines.hasNext)
    {
      out.println("i am client");
      println(in.getLines.next)
    }
  }
  
}