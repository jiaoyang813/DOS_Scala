package project2

class Tuple(var elem1:String, var elem2:String, var elem3:String) {
    
	def isEqual(other:Tuple):Boolean =
	{
		if(other != null)
		{
			if( this.elem1.equals(other.elem1)
				&&this.elem2.equals(other.elem2)
				&&this.elem3.equals(other.elem3) )
			{
				return true;
			}
			else
				return false;		
		}
		else
			return false;
	}
	
	override def toString() :String =
	{
		return this.elem1 +" "+this.elem2+" "+ this.elem3
	}
}