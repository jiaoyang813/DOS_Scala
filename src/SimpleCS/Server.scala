package SimpleCS

import java.net.ServerSocket
import scala.io.BufferedSource
import java.io.PrintStream

object Server {

  def main(args: Array[String]): Unit = {
    var socket = new ServerSocket(50000)
    var client = socket.accept()
    var in = new BufferedSource(client.getInputStream())
    var out = new PrintStream(client.getOutputStream())
    var input = in.getLines
    while(input.hasNext)
    {
    	println(input.next)
    	out.println("received")
    }
    
    
  }

}