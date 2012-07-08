package com.imps.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;

public class FileTool {
	  private File file = null;
	  private FileInputStream in = null;
	  private FileOutputStream out = null;

	  private File createFile(String paramString)
	  {
	    File localFile = null;
	    if ((paramString != null) && (paramString.trim().length() > 0))	{
	      localFile = new File(paramString);
	      if (localFile.exists()){
	    	  return null;
	      }else{
	    	  try{
	    	      localFile.createNewFile();
			      return localFile;
	    	  }catch(IOException localIOException){
	    		  return null;
	    	  }  
	      }
	    }else{
	    	return null;
	    }
	  }

	  private File createFolder(String paramString)
	  {
	    File localFile = null;
	    if ((paramString != null) && (paramString.trim().length() > 0))
	    {
	      localFile = new File(paramString);
	      if (!localFile.exists())
	        localFile.mkdirs();
	    }
	    return localFile;
	  }

	  private File createWithUrl(String paramString1, String paramString2)
	  {
	    File localFile = null;
	    int i = paramString2.indexOf(File.separator, paramString1.length());
	    if (i != -1)
	    {
	      i = paramString2.lastIndexOf(File.separator);
	      String str = paramString2.substring(0, i);
	      if ((str != null) && (str.trim().length() > 0))
	        localFile = createFolder(str);
	    }
	    if ((i == -1) || ((localFile != null) && (i + 1 < paramString2.length())))
	      localFile = createFile(paramString2);
	    return localFile;
	  }

	  public void close()
	  {
	    try{
	      if (this.in != null)
	        this.in.close();
	      if (this.out != null)
	        this.out.close();
	      return;
	    }catch (Exception localException){
	        this.in = null;
	        this.out = null;
	        this.file = null;
	    }finally{
	      this.in = null;
	      this.out = null;
	      this.file = null;
	    }
	  }

	  public String open(String paramString,String prefix, boolean paramBoolean1, boolean paramBoolean2)
	  {
	    String localObject = "";
	    if ((paramString != null) && (paramString.trim().length() > 0) && (Environment.getExternalStorageState().equals("mounted"))){
	      String str1 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + prefix;
	      String str2 = str1 + paramString;
	      this.file = new File(str2);
	      if ((!this.file.exists()) && (paramBoolean1))
	        this.file = createWithUrl(str1, str2);
	      if ((this.file == null) || (!this.file.exists())){
	    	  return "";
	      }
	    }
	    try{
	      if (this.file.canRead())
	        this.in = new FileInputStream(this.file);
	      if (this.file.canWrite())
	        this.out = new FileOutputStream(this.file, paramBoolean2);
	      String str3 = this.file.getAbsolutePath();
	      localObject = str3;
	      return localObject;
	    }catch (FileNotFoundException localFileNotFoundException){
	    }catch (SecurityException localSecurityException){
	    }
	    return localObject;
	  }

	  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
	  {
	    int i = -1;
	    if (this.in != null);
	    try
	    {
	      int j = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
	      i = j;
	      return i;
	    }
	    catch (IOException localIOException)
	    {
	    }
	    return i;
	  }

	  public boolean write(byte[] paramArrayOfByte)
	  {
	    try
	    {
	      this.out.write(paramArrayOfByte);
	      return true;
	    }catch (IOException localIOException){
	    }
	    return false;
	  }

	  public boolean write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
	  {
	    try{
	      this.out.write(paramArrayOfByte, paramInt1, paramInt2);
	      return true;
	    }
	    catch (IOException localIOException)
	    {
	    }
	    return false;
	  }
}
