package com.uniteksolusi.otomill.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class FileAccessManager {

//	private static FileAccessManager instance = new FileAccessManager();
//	
//	public static synchronized FileAccessManager getInstance() {
//		return instance;
//	}
//	
//	private FileAccessManager() {
//
//	}
	
	
//	HashMap<String, Thread> lockMap = new HashMap<String, Thread>();
//	
//	public boolean lock(File f) {
//		
//		Thread lockingThread = lockMap.get(f.getAbsolutePath());
//		
//		while(lockingThread != null 
//				&& lockingThread != Thread.currentThread() 
//					&& lockingThread.isAlive()) {
//			
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			
//			System.out.println(Thread.currentThread().getName() + " : trying to get lock on " + f + " , owned by: " + lockingThread.getName());
//			System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
//			System.out.println(Arrays.toString(lockingThread.getStackTrace()));
//			System.out.println();
//			
//		}
//		
//		lockMap.put(f.getAbsolutePath(), Thread.currentThread());
//
//		return true;
//		
//	}
//	
//	public boolean release(File f) {
//		
//		Thread lockOwner = lockMap.get(f.getAbsolutePath()) ; 
//		if(lockOwner != null) {
//			
//			if(lockOwner != Thread.currentThread()) { //locked but current thread is not the owner
//				System.out.println(Thread.currentThread() + " is trying to release " + f + " , owned by " + lockOwner);
//				return false;
//			} else { //locked and current thread is the owner, then remove
//				lockMap.remove(f.getAbsolutePath());
//			}
//			
//		} // else, file is not locked
//		
//		return true;
//		
//	}
	

	
	private static HashMap<String, FileAccessManager> managers = new HashMap<String, FileAccessManager>();
	
	public static synchronized FileAccessManager getInstance(String absoluteFilePath) {
		
		if(!managers.containsKey(absoluteFilePath)) {
			managers.put(absoluteFilePath, new FileAccessManager(absoluteFilePath));
		}
		
		return managers.get(absoluteFilePath);
	}
	
	
	private File theFile;
	private FileAccessManager(String absoluteFilePath) {
		this.theFile = new File(absoluteFilePath);

	}
	
	public synchronized void synchronizedWrite(String s, boolean append) throws IOException {
		FileWriter fw = new FileWriter(theFile, append);
		fw.write(s);
		fw.flush();
		fw.close();
	}
	
	public synchronized byte[] synchronizedRead() throws IOException {
		return Files.readAllBytes(theFile.toPath());
	}
	
	public static synchronized void synchronizedWrite(File f, String s, boolean append) throws IOException {
		FileWriter fw = new FileWriter(f, append);
		fw.write(s);
		fw.flush();
		fw.close();
	}
	
	public static synchronized byte[] synchronizedRead(File f) throws IOException {
		return Files.readAllBytes(f.toPath());
	}

}
