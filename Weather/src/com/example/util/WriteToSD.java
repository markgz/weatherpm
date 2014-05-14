package com.example.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Mark
 * 
 */
public class WriteToSD {
	private Context context;
	String filePath = android.os.Environment.getExternalStorageDirectory()
			+ "/weather";

	public WriteToSD(Context context) {
		this.context = context;
		if (!isExist()) {
			writeDB();
		}
	}

	public static void writeFileToSD(String completedFilePath, String storeFolderName) {
		
		String storeFolderPath = Environment.getExternalStorageDirectory()+"/"+storeFolderName;
		
		if(!isExist(storeFolderPath)){
			File file = new File(storeFolderPath);
			file.mkdirs();
		}
		
		String imgFileName = completedFilePath.substring(completedFilePath.lastIndexOf("/")+1);
		
		String storeFilePath = storeFolderPath+"/"+imgFileName;
		
		
		if(isExist(storeFilePath)){
			Log.i("TAG", "There is one image with the same name");
			storeFilePath = storeFolderPath+"/"+new Random().nextInt(99999)+imgFileName;
		}
		
		File imgFile = new File(completedFilePath);
		
		try {
			FileOutputStream fos = new FileOutputStream(storeFilePath);
			
			InputStream is = new FileInputStream(imgFile);
			
			byte[] bytes = new byte[1024];
			
			int len = 0 ;
			
			while((len= is.read(bytes)) != -1){
				
				fos.write(bytes, 0, len);
			}
			
			fos.flush();
			fos.close();
			is.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeDB() {
		InputStream inputStream;
		try {
			inputStream = context.getResources().getAssets()
					.open("addressId.db", 3);
			File file = new File(filePath);

			/*
			 * File deleteFile = new File(filePath + "/database.db");
			 * 
			 * if (deleteFile.exists()) { boolean result = deleteFile.delete();
			 * Log.i("TAG", "delete result: " + result); }
			 */

			if (!file.exists()) {
				file.mkdirs();
			}
			FileOutputStream fileOutputStream = new FileOutputStream(filePath
					+ "/database.db");
			byte[] buffer = new byte[512];
			int count = 0;
			while ((count = inputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, count);
			}
			fileOutputStream.flush();
			fileOutputStream.close();
			inputStream.close();
			System.out.println("success");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static boolean isExist(String fildPath) {
		File file = new File(fildPath);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isExist() {
		File file = new File(filePath + "/database.db");
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}
}
