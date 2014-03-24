package project1

import scala.io.BufferedSource

object processRequest {
  
	var port = 0;
	var ID = ""
	var FileName = ""
	var result = ""
    var clientIP = ""
	val db = myDB
	var input = new BufferedSource(System.in)
	var in = input.getLines
	def getInputStream(inputStream:Iterator[String]) =
	{
	   in = inputStream
	}
	def uploadFile(FileName:String) =
	{
		var curState = new MetaData("NULL");
		db.updateMetaFile(FileName, curState);
	}
	
      def processInput(inputLine : Array[String]) : String = {
	  
	  inputLine(0) match {
	    case "BYE" => 
	      { db.closeDB
	        "BYE"}
	    case "SHUTDOWN SERVER" =>{
	      if(inputLine.length == 2 && inputLine(1).equals("SERVER") )//client make server shut down
			{
				db.closeDB()
				"SERVER SHUTDOWN"
			}
			else "Invalclient Command"
	    }
	    case "INSERT" => {
	      if(inputLine.length != 5)
			"Format Mismatch(INSERT FILENAME STRING STRING STRING)"
			var t = new Tuple(inputLine(2),inputLine(3),inputLine(4))
			FileName = inputLine(1)
			db.loadFile(FileName)
			if(db.insert(t))
			{
				db.saveCurFile(FileName)
				"INSERT DONE"
			}
			else "Conflicts!"
	    }
	    case "REGISTER" => {
	      clientIP = inputLine(1) 
	      "REGISTERED"
	      }
	    case "READLOCK" => {
	      FileName = inputLine(1)
			if(db.checkReadLock(FileName, clientIP))  "already got readlock"
			if(db.getReadLock(FileName, clientIP)) "get readlock"
			else "cannot get readlock"}   
	    case "WRITELOCK" =>{
	        FileName = inputLine(1)
			if(db.checkWriteLock(FileName, clientIP)) "already got writelock"
			if(db.getWriteLock(FileName, clientIP)) "get writelock"
			else  "cannot get writelock"
	    }
	    case "RELEASELOCK" =>{
	        FileName = inputLine(1)
			if(db.checkReadLock(FileName, clientIP)
					||db.checkWriteLock(FileName, clientIP))
			{
				if(db.releaseLock(FileName, clientIP))  "Lock Released"
				else  "cannot release lock"
			}
			else "No lock on file";
	    }
	    case "READ" =>{
	        FileName = inputLine(1)
	        if(!db.metafile.contains(FileName)) "No Such File"
	        else if(db.checkReadLock(FileName, clientIP))
			{
				if(inputLine.length != 2)  "Format Mismatch(READ [FILENAME])";
				if(!FileName.equals(db.curFileName))
				{
					if(!db.loadFile(FileName)) "NO SUCH FILE";
					else db.printCurFile();	
				}
                "FILE ["+FileName+"] SHOWN ON SERVER";
			}
			else  "Require ReadLock";
	    }
	    case "WRITE" =>{
	        FileName = inputLine(1)
			if(!db.metafile.contains(FileName)) "No Such File"
	        else if(db.checkWriteLock(FileName, clientIP))
			{
				var t = new Tuple(inputLine(2),inputLine(3),inputLine(4));
				db.loadFile(FileName);
				if(db.insert(t))
				{
					db.saveCurFile(FileName);
				    "WRITE DONE";
				}
				else "Conflicts!";
			}
			else  "Require WriteLock";
	    }
	    case "SHOWDIR" =>{
	        db.printDir();;
		    "RESULT ON SERVER";
	    }
	    case "SHUTDOWN" =>{
	      if(inputLine.length == 2&&inputLine(1).equals("SERVER") )//client make server shut down
			{
				db.closeDB();
			    "SERVER SHUTDOWN";
			}
			else  "Invalclient Command";
	    }
	    case "DELETE" =>{
	      if(inputLine.length == 2) //delete filename
			{
				FileName = inputLine(1);
				if(db.checkReadLock(FileName, clientIP)||
				    db.checkWriteLock(FileName, clientIP))
					return "file locked";
			   if(db.deleteFile(inputLine(1))) inputLine(1)+ " Deleted"
			   else  "Cannot Delete "+ inputLine(1)
			}
			else if(inputLine.length == 5)
			{	
				FileName = inputLine(1)
				var toDel = new Tuple(inputLine(2),inputLine(3),inputLine(4))
				if(!FileName.equals(db.curFileName))
					db.loadFile(FileName)
				if(db.delete(toDel))
				{
					db.saveCurFile(FileName)
					"DELETE DONE"
				}
				else "NO MATCH"
			}
			else "Format Mismatch(DELETE FILENAME STRING STRING STRING)"
	    }
	    case "MATCH" =>{
	        FileName = inputLine(1);
			if(inputLine.length != 5)
				return "Format Mismatc(MATCH FILENAME STRING STRING STRING)"
			var toMatch = new Tuple(inputLine(2),inputLine(3),inputLine(4))
			if(!FileName.equals(db.curFileName))
				db.loadFile(FileName);
			
			if(db.search(toMatch)!=null )
				"MATCH FOUND: "+ db.search(toMatch).toString()
			else   "NO MATCH";		
	    }
	    case "SHOWCURFILE" =>{
	      if(!FileName.equals("") && !FileName.equals(db.curFileName))
			{	
				db.loadFile(FileName);
				FileName = db.curFileName;
			}
			if(db.curFile != null)
			{
				db.printCurFile();
				"FILE ["+FileName+"] SHOWN ON SERVER";
			}
			else
			  "NO FILE";
	    }
	    case "UPLOAD" =>{
	      FileName = inputLine(1)
	      if(db.metafile.contains(FileName) &&
	          (db.checkReadLock(FileName, clientIP) 
	          || db.checkWriteLock(FileName, clientIP)) )
	        "Cannot UPLOAD"
	      else
	      {
	        db.uploadFile(FileName, in)
	        "UPLOADED"
	      }
	        
	    }
	      
	    case _ => "Invalid Input"
	  }
	}
}