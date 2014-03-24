package project2

object ATest {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  var s : String =null                            //> s  : String = null
  s.equals(null)                                  //> java.lang.NullPointerException
                                                  //| 	at project2.ATest$$anonfun$main$1.apply$mcV$sp(project2.ATest.scala:6)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$$anonfun$$exe
                                                  //| cute$1.apply$mcV$sp(WorksheetSupport.scala:76)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.redirected(W
                                                  //| orksheetSupport.scala:65)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.$execute(Wor
                                                  //| ksheetSupport.scala:75)
                                                  //| 	at project2.ATest$.main(project2.ATest.scala:3)
                                                  //| 	at project2.ATest.main(project2.ATest.scala)
}