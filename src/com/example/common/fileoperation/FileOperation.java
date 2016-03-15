package com.example.common.fileoperation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

public class FileOperation {
	
	protected String sdCardRoot;

	public FileOperation() {
		sdCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		System.out.println(sdCardRoot);
	}

	/**
	 * 在SD卡上创建文件
	 */
	public File createFileInSDCard(String fileName, String dir)
			throws IOException {
		File file = new File(sdCardRoot + File.separator + dir + File.separator + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 */
	public File createSDDir(String dir) {
		File dirFile = new File(sdCardRoot + dir + File.separator);
		return dirFile;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public boolean isFileExist(String fileName, String path) {
		File file = new File(sdCardRoot+ File.separator + path + File.separator + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File writeToSDCardFromInputStream(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createFileInSDCard(fileName, path);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			int temp;
			while ((temp = input.read(buffer)) != -1) {
				output.write(buffer, 0, temp);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}
}
