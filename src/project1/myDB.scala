package project1

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.io.BufferedSource
import scala.reflect.io.Path
import scala.io.Source

object myDB {
  
    var metafile : Map[String, MetaData] = Map()
	var curFile : Map[String, Tuple] = Map()
	var rmap : Map[String, ArrayBuffer[String]] = Map()
	var wmap : Map[String,String] = Map()
	var curFileName = ""
	var defpath = "/Users/yangjiao/scalaworkspace/DOS_Scala" // a default path, change it later!!!!!!
	loadMetaFile()
	loadLockFile()

	def uploadFile(FileName:String, in:Iterator[String]) = {
      var path = "/Users/yangjiao/scalaworkspace/DOS_Scala" 
       var writer = new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream(path+"/data/"+FileName), "utf-8"));//+".txt"???????
       var temp = in.next
       temp = in.next //get ride of first line
       while(!temp.equals("UPLOADED"))
	   {  
           System.out.println("receive " + temp)
    	   writer.write(temp)
    	   writer.newLine() 
    	   temp = in.next
	   }
	   System.out.println("UPLOAD DONE!")
			    //System.out.println("Last Input " + input);
	   var curState =new MetaData("NULL")
	   updateMetaFile(FileName,curState)
	   writer.close()	    
    }
    
	def checkReadLock(FileName:String , client:String ) :Boolean =
	{
	  if(checkWriteLock(FileName, client)) false
	  else	if(rmap.contains(FileName) && rmap(FileName).contains(client) )  true
		else false
	}
    
    def checkWriteLock(FileName:String ,  client:String) :Boolean =
	{
        if(checkReadLock(FileName,client)) false
        else if(wmap.contains(FileName) && wmap(FileName).contains(client)) true
		else false
	}
    
    def deleteFile(FileName:String ):Boolean = 
	{
		var path: Path = Path(defpath+"/data/"+FileName)
		//if it does not exist
		if(path.exists&&metafile(FileName).lock.equals("NULL")) 
		{
		  path.delete
		  metafile -= FileName
		  writeMetaFileToDisc()
		  true
		}
		else
		  false

	}
    
    def getReadLock(FileName:String,client: String) :Boolean = 
	{
        var path: Path = Path(defpath+"/data/"+FileName)
		if(path.exists)
		{  
			var temp = metafile(FileName)
			if(!temp.lock.equals("WriteLock"))
			{
				if(temp.lock.equals("ReadLock")){
					var arrtemp = rmap(FileName)
					arrtemp +:= client
					rmap += (FileName -> arrtemp)
					writeReadLockFileToDisc()
				    true
				}
				else{ // no lock on file
					temp.lock = "ReadLock"
					var arrtemp = ArrayBuffer[String]()
					arrtemp +:= client
					rmap += (FileName -> arrtemp)
					writeReadLockFileToDisc()
					updateMetaFile(FileName, temp)
					true
				}
			}
			else//has write lock on file
				false
		}
		else false// no such file
		
	}
    
    def getWriteLock(FileName:String, client:String) :Boolean = 
	{
      var path: Path = Path( defpath + "/data/"+FileName)
		if(path.exists)
		{
			var temp = metafile(FileName)
			if(temp.lock.equals("ReadLock")) false
			else if(temp.lock.equals("WriteLock"))
			{
				if(wmap(FileName).equals(client)) true
				else false
			}
			else if(temp.lock.equals("NULL"))
			{	
				temp.lock = "WriteLock"
				wmap.put(FileName, client)
			    writeWriteLockFileToDisc()
				updateMetaFile(FileName, temp)
				true
			}
			else false	
		}
		else false
		
	}
    
    //release all lock of filename by client
	def releaseLock(FileName:String , client:String ):Boolean = 
	{
	   var path: Path = Path(defpath+"/data/"+FileName)
		if(path.exists)
		{
			var temp = metafile(FileName);
			if(!temp.equals("NULL"))
			{
				if(temp.lock.equals("WriteLock"))
				{
					if(client.equals(wmap(FileName)))
					{
						//the right client 
						temp.lock = "NULL";
						wmap.remove(FileName);
						updateMetaFile(FileName, temp);
						writeWriteLockFileToDisc();
						true
					}
					else false// not the right client		
				}   
				else if(temp.lock.equals("ReadLock"))
				{
					var arrtemp = rmap(FileName)
					if(arrtemp.contains(client))
					{   if(arrtemp.length == 1) 
						{	
							rmap.remove(FileName)
							temp.lock = "NULL"
						}
						else
						{
						    arrtemp -= client
						    rmap.put(FileName, arrtemp)
						}
						writeReadLockFileToDisc();
						updateMetaFile(FileName, temp);
					}
					else false// no read lock from this client	
				}	
			}
			// no lock on file
			true
		}
		else false// no such file 
	}
	
	//remove old metadata and put new one
	def updateMetaFile(FileName:String,curState:MetaData) = 
	{
		if(metafile == null) false
		var cal = Calendar.getInstance();
    	cal.getTime();
    	var sdf = new SimpleDateFormat("yyyy.MM.DD-HH:mm:ss");
    	curState.time = sdf.format(cal.getTime()) ;
		metafile.put(FileName, curState);
		writeMetaFileToDisc();
	}
	
	def loadLockFile() =
	{
		var readlockfileExist = Path(defpath+"/data/ReadLock.txt").exists;
		var writelockfileExist = Path(defpath+"/data/WriteLock.txt").exists;
		if(readlockfileExist)
		{
			getReadLockFileFromDisc();
		}
		else
			createReadLockFile();
		
		if(writelockfileExist)
			getWriteLockFileFromDisc();
		else
			createWriteLockFile();		
	}
	
	def loadMetaFile() =
	{
		
		var metafileExist = Path(defpath+"/data/metafile.txt").exists;
		if(metafileExist)
		{  //if it exists, read it in
			readMetaFileFromDisc();
			//update the metafile if new file is added
			var curDir = getDirFiles();
			for( i<-0 to curDir.length -1)
			{
				if(!(metafile contains curDir(i)))
				{
					var cal = Calendar.getInstance();
			    	cal.getTime();
			    	var sdf = new SimpleDateFormat("yyyy.MM.DD-HH:mm:ss");
			        var temp = new MetaData("NULL",sdf.format(cal.getTime()) );
			        metafile.put(curDir(i), temp);
				}
			}
			writeMetaFileToDisc();
		}
		else{
		//create metafile and read it in
			createMetaFile();
		}
	}
	
	//load FileName from disc
	def loadFile(FileName:String) : Boolean =
	{
		//read file from disc
		curFileName  = FileName;
		var isfileExist = Path(defpath+"/data/"+FileName).exists
		if(isfileExist)
		{
			
			for (inputLine<- Source.fromFile(defpath+"/data/"+FileName).getLines ) {
					var token = inputLine.toString.split(" ")
					var record = new Tuple(token(0), token(1), token(2))
					curFile.put(token(0), record)
				}
			true
		}
		else false
	}
	
	//save dbfile to disc
	def saveCurFile(FileName:String)
	{	
	//save curFile to disc
		
		   var writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(defpath+"/data/"+FileName), "utf-8"));
		    var it = curFile.iterator
		    while(it.hasNext)
		    {
		    	 var record = it.next._2
		    	 var outputLine = record.elem1+" "+record.elem2+" "+ record.elem3;
		    	 writer.write(outputLine);
		    	 writer.newLine();	 
		    } 
		curFileName ="";
		curFile.clear();
		writer.close();
		var cal = Calendar.getInstance();
    	cal.getTime();
    	var sdf = new SimpleDateFormat("yyyy.MM.DD-HH:mm:ss");
    	var temp = new MetaData("NULL")
    	if(rmap.contains(FileName))
         temp =new MetaData( "ReadLock",sdf.format(cal.getTime()) )
    	else if(wmap.contains(FileName))
    	 temp =new MetaData( "WriteLock",sdf.format(cal.getTime()) )
    	else
    	{temp = new MetaData( "NULL",sdf.format(cal.getTime()) )}
        updateMetaFile(FileName, temp);	
	}
	
	def printDir() =
	{
		System.out.println("---START OF DIRECTORY---");
		var it = metafile.iterator
		while(it.hasNext)
	    {
		  var temp = it.next
	      var record = temp._2
	      System.out.println(temp._1+" "+record.toString());
	    }
		System.out.println("---END OF DIRECTORY---");
		//print result
	}
	
	def printCurFile() =
	{
		if(!curFileName.equals(""))
		{
			System.out.println("---START OF "+curFileName+"---");
			var it = curFile.iterator
			while(it.hasNext)
		    {
		    	 var record = it.next._2
		    	 System.out.println(record.toString());
		    }
			System.out.println("---END OF "+curFileName+"---");
		}
		else
		{
			System.out.println("NO FILE");
		}
		
	}
	
	def search(t:Tuple) =
	{
		var key = t.elem1;
		if(curFile.contains(key))
		{
			if(curFile(key).isEqual(t))  curFile(key)
			else  null
		}
		else  null
	}
	
	def delete(t:Tuple) =
	{
		//delete tuples
		if(search(t) != null)
		{
			curFile.remove(t.elem1)
			true
		}
		else false
	}
	
	def insert(t:Tuple) =
	{
		//insert tuples
		//check if item conflicts
		if(curFile.contains(t.elem1)) false
		else
		{ 
		  curFile.put(t.elem1, t)
		  true
		 }
		//dbfile on disc is OUT OF DATE now
	}
	
	def closeDB()
	{
		if(!curFileName.equals(""))
			saveCurFile(curFileName);
		writeMetaFileToDisc();
		writeWriteLockFileToDisc();
		writeReadLockFileToDisc();
	}
	
	def createMetaFile() =
	{
	   var writer = new BufferedWriter(new OutputStreamWriter(
	          new FileOutputStream(defpath+"/data/metafile.txt"), "utf-8"));
		var curDir = getDirFiles();
		//load it in memory
		metafile.clear();
		for(i<-0 to curDir.length-1)
		{
			var cal = Calendar.getInstance();
	    	cal.getTime();
	    	var sdf = new SimpleDateFormat("yyyy.MM.DD-HH:mm:ss");
	        var temp = new MetaData("NULL",sdf.format(cal.getTime()) );
	        metafile.put(curDir(i), temp);
		}
		//write it to disc, keep it up to date
	    var it = metafile.iterator
	    while(it.hasNext)
	    {
	         var temp = it.next
	    	 var metadata = temp._2
	    	 var outputLine = temp._1+" "+metadata.lock+" "+ metadata.time
	    	 writer.write(outputLine)
	    	 writer.newLine()
	    }
	    writer.close()
	}
	
	def createReadLockFile() =
	{
	   var writer = new BufferedWriter(new OutputStreamWriter(
	          new FileOutputStream(defpath+"/data/ReadLock.txt"), "utf-8"))
	   writer.close()
	}
	
	def createWriteLockFile() =
	{		
	  var writer = new BufferedWriter(new OutputStreamWriter(
	          new FileOutputStream(defpath+"/data/WriteLock.txt"), "utf-8"));
	  writer.close();		
	}
	
	
	def getDirFiles() : ArrayBuffer[String] =
	{
		
		var dir = new ArrayBuffer[String]
		var folder = new File(defpath+"/data/");
		var listOfFiles = folder.listFiles();
	// save it in metafile in memory
		for(i <- 0 to listOfFiles.length -1) {
		    if (listOfFiles(i).isFile()) {
		        dir += listOfFiles(i).getName()
		    }
		}
		return dir;
	}
	
	//read metafile.txt to memory 
	def readMetaFileFromDisc() =
	{
		metafile.clear();//clear old data
		for( inputLine<- Source.fromFile(defpath+"/data/metafile.txt").getLines ) {
				var token = inputLine.toString.split(" ")
				var temp = new MetaData(token(1),token(2));
				metafile.put(token(0), temp);
			}
	}
	
	def getReadLockFileFromDisc() =
	{
			for( inputLine <- Source.fromFile(defpath+"/data/ReadLock.txt").getLines ) {
				var token = inputLine.toString.split(" ")
				for( i <- 1 to token.length -1 )
				{
					if(rmap.contains(token(0)))
					{
						var temp = rmap(token(0))
						temp += token(i)
						rmap.put(token(0), temp);
					}
					else
					{
						var temp = new ArrayBuffer[String]()
						temp += token(i)
						rmap.put(token(0), temp);
					}
				}
			}
	}
	
	def getWriteLockFileFromDisc() = 
	{
		wmap.clear();
	    for( inputLine <- Source.fromFile(defpath+"/data/WriteLock.txt").getLines ) {
				var token = inputLine.toString.split(" ")
				wmap.put(token(0), token(1));
	    }
	}
	
	//write the in-memory metafile to disc
	def writeMetaFileToDisc() =
	{
	   var writer = new BufferedWriter(new OutputStreamWriter(
	          new FileOutputStream(defpath+"/data/metafile.txt"), "utf-8"));
		//write it to disc
	    var it = metafile.iterator
	    while(it.hasNext)
	    {
	    	 var input = it.next
	    	 var metadata = input._2
	    	 var outputLine = input._1 +" "+metadata.lock+" "+ metadata.time
	    	 writer.write(outputLine);
	    	 writer.newLine();	 
	    }
	    writer.close();
	}
	
	def writeReadLockFileToDisc() =
	{	
	   var writer = new BufferedWriter(new OutputStreamWriter(
	          new FileOutputStream(defpath+"/data/ReadLock.txt"), "utf-8"));
		//write it to disc
	   var it = rmap.iterator
	    while(it.hasNext)
	    {
	     var temp = it.next
	     var outputLine = temp._1+" ";
	    	 for(i<-0 to temp._2.length-1)
	    		 outputLine += temp._2(i)+" ";
	    	 writer.write(outputLine);
	    	 writer.newLine();	 
	    }
	    writer.close();
	}
	
	def writeWriteLockFileToDisc() =
	{
	   var writer = new BufferedWriter(new OutputStreamWriter(
	          new FileOutputStream(defpath+"/data/WriteLock.txt"), "utf-8"));
		//write it to disc
	    var it = wmap.iterator
	    while(it.hasNext)
	    {
	      var temp = it.next
	      var outputLine = temp._1 +" "+temp._2;
	      writer.write(outputLine);
	      writer.newLine();	 
	    }
	    writer.close();
	}
	
}