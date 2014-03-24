package project2

class MetaData(var lock:String,var time:String){
	def this(lock:String) = this(lock, "NULL")	
	override def toString() :String =
	{
		return lock+" "+time
	}
}