package com.example.common.fileoperation;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;


public class Mp3Information implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String TAG = "TAG"; // �ļ�ͷ1-3

	private String title; // ������4-33

	private String artist; // ������34-63

	private String album; // ר����61-93

	private String year; // ��94-97
	
	private String path;
	
	private String lrcPath;
	
	private String imgPath;
	
	private String length;
	
	
	public Mp3Information(File mp3) {
		path = mp3.getAbsolutePath();
		try {
			if (!getID3v2(mp3)) {
				getID3v1(mp3);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//���û���ҵ�ID3�е�Title��Artist��Ϣ
		if (title == null || title == "") {
			title = mp3.getName();
			title = title.replace(".mp3", "");
		}
		if (artist == null || artist == "") {
			int flag = title.lastIndexOf("_");
			if (flag >=0) {
				artist = title.substring(flag + 1);
				title = title.replace("_" + artist, "");
			}
			if (artist == null) {
				artist = "δ֪������";
			}
		}
		//�����Ƿ��ж�Ӧ��lrc�ļ�
		lrcPath = path.replace(".mp3", ".lrc");
		File lrcFile = new File(lrcPath);
		if (!lrcFile.exists()) {
			lrcPath = "";
		}
		if (album == null || album == "") {
			album = "δ֪��Ƭ��";
		}
	}
	
	private boolean getID3v1(File mp3) throws IOException {

		RandomAccessFile ran = new RandomAccessFile(mp3, "r");
		byte[] buffer = new byte[128];
		ran.seek(ran.length() - 128);
		ran.read(buffer);
		ran.close();
		if (buffer.length != 128) {
			// throw new RuntimeException("���ݳ��Ȳ��Ϸ�:" + buffer.length);
		}
		String tag;
		tag = new String(buffer, 0, 3);
		// ֻ��ǰ�����ֽ���TAG�Ŵ��������ֽ�

		if (tag.equalsIgnoreCase("TAG")) {
			title = new String(buffer, 3, 30, "GB2312").trim();
			artist = new String(buffer, 33, 30, "GB2312").trim();
			album = new String(buffer, 63, 30, "GB2312").trim();
			year = new String(buffer, 93, 4, "GB2312").trim();
			return true;
		} else {
			return false;
		}

	}
	
	private boolean getID3v2(File mp3) throws IOException {
		ID3v2Information id3v2 = new ID3v2Information();
		RandomAccessFile ran = new RandomAccessFile(mp3, "r");
		byte[] buffer = new byte[256];
		ran.seek(0);
		ran.read(buffer);
		ran.close();
		if (id3v2.checkID3V2(buffer, 0) < 0) {
			return false;
		}
		id3v2.parseID3V2(buffer, 10);
		getID3FromID3v2(id3v2);
		return true;
		
	}
	
	private void getID3FromID3v2(ID3v2Information id3v2) {
		title = id3v2.getStrTitle();
		artist = id3v2.getStrArtist();
		album = id3v2.getStrAlbum();
		year = id3v2.getStrYear();
	}

	@Override
	public String toString() {
		return "Mp3Information [title=" + title + ", artist=" + artist
				+ ", album=" + album + ", year=" + year + ", path=" + path
				+ "]";
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public String getYear() {
		return year;
	}

	public String getPath() {
		return path;
	}

	public String getLrcPath() {
		return lrcPath;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public void setPath(String Path){
		this.path=Path;
	}
}
