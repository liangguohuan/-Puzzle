package com.hanson.pintu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

public class FileUtil {
	/**
	 * 创建文件
	 * 
	 * @throws IOException
	 */
	public static File createFile(String dirName, String fileName) throws IOException {
		createDir(dirName);
		File file = new File(dirName + fileName);
		file.createNewFile();
		return file;
	}
	
	/**
	 * 创建目录
	 * 
	 * @param dirName
	 */
	public static File createDir(String dirName) {
		String[] arr = dirName.split("/");
		dirName = dirName.replaceFirst("^/", "").replaceFirst("/$", "");
		String pre = "";
		File dir = new File("/");
		for (int i = 0, len=arr.length; i < len; i++) {
			String temp = "/" + arr[i];
			String suffix = i == len - 1 ? "" : "/";
			dir = new File(pre + temp + suffix);
			dir.mkdir();
			pre += temp;
		}
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public static boolean isFileExist(String fileName){
		File file = new File(fileName);
		return file.exists();
	}
	
	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public static File write2SDFromInput(String path,String fileName,InputStream input){
		File file = null;
		OutputStream output = null;
		try{
			createDir(path);
			file = createFile(path, fileName);
			output = new FileOutputStream(file);
			byte buffer [] = new byte[4 * 1024];
			while((input.read(buffer)) != -1){
				output.write(buffer);
			}
			output.flush();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				output.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return file;
	}
	
	/**
	 * 将字节数组中数据写入到SD卡中
	 * @param path
	 * @param fileName
	 * @param b
	 * @return
	 */
	public static File write2SDFromByte(String path,String fileName,byte[] b){
		File file = null;
		OutputStream output = null;
		try{
			createDir(path);
			file = createFile(path, fileName);
			output = new FileOutputStream(file);
			output.write(b);
			output.flush();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				output.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 将字符串写入到SD卡中
	 */
	public static void write2SDFromString(String path, String fileName, String string) {
		try {
			createDir(path);
			createFile(path, fileName);
			FileWriter bufWriter = (new FileWriter(path + fileName));
			bufWriter.write(string);
			bufWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static String readFileByLines(String fileName) {
    	StringBuffer sb = new StringBuffer();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                sb.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return sb.toString();
    }
    
    /**
     * 以文件整个字节长度读取文件
     * @param fileName
     * @return
     * @throws Exception
     */
    public static byte[] readFileByAllByte(String fileName) throws Exception {
    	FileInputStream fs = new FileInputStream(fileName);  
        byte[] buffer = new byte[fs.available()]; 
        fs.read(buffer); 
        fs.close();
        return buffer;
    }
    
    /**
     * 以文件整个字节长度读取文件,以传入的 InputStream 作为参数
     * @param is
     * @return
     * @throws Exception
     */
    public static byte[] readFileByAllByteFromInputStream(InputStream is) throws Exception {
        byte[] buffer = new byte[is.available()]; 
        is.read(buffer); 
        is.close();
        return buffer;
    }
    
    /**
     * 读取目录
     * @param path
     * @return
     */
    public static CharSequence[] readDirectory(String path) throws Exception {
    	File d = new File(path);
		File list[] = d.listFiles();
		int len = list.length;
		CharSequence[] cs = new CharSequence[len];
		for (int i = 0; i < len; i++) {
			cs[i] = list[i].getName();
		}
		return cs;
    }
    
    /**
     * 删除文件
     * @param fileName
     */
    public static void delFile(String fileName) throws Exception {
    	File file = new File(fileName);
    	file.delete();
    }
    
    /**
     * 从 APK 包中写入文件
     * @param context
     * @param fileName
     * @param input
     * @throws Exception 
     */
    @SuppressWarnings("static-access")
	public static void write2Package(Context context, String fileName, String fileNameFrom) throws Exception {
    	DesUtil du = new DesUtil();
		OutputStream output = null;
		output = context.openFileOutput(fileName, context.MODE_WORLD_WRITEABLE);
		byte[] buffer = readFileByAllByte(fileNameFrom);
		//buffer = du.decrypt(buffer);
		output.write(buffer);
		output.close();
	}
	
    /**
     * 从APK包中取得文件
     * @param context
     * @param fileName
     * @return
     */
	public static InputStream getIsFromPackage(Context context, String fileName) {
		InputStream is = null;
		try {
			is = context.openFileInput("play.jpg");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return is;
	}
	
	/**
	 * 从文件路径创建InputStream
	 * @param fileNameFrom
	 * @return
	 */
	public static InputStream getIsFromFile(String fileNameFrom) {
		InputStream is = null;
        try {
			is = new FileInputStream(fileNameFrom);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return is;
	}
	
	/**
	 * 从APK包中写入文件并返回该文件流InputStream
	 * @param context
	 * @param fileName
	 * @param fileNameFrom
	 * @return
	 * @throws Exception 
	 */
	public static InputStream getTempFileFromPackage(Context context, String fileName, String fileNameFrom) throws Exception {
		write2Package(context, fileName, fileNameFrom);
		InputStream is = getIsFromPackage(context, fileName);
		return is;
	}
}