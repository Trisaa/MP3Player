package com.example.common.fileoperation;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import com.example.lyrics.PinyinComparator;

public class Mp3FileOperation extends FileOperation {
	
		/**
		 * ��ȡĿ¼�е�mp3�ļ������ֺʹ�С
		 */
	public ArrayList<Mp3Information> getMp3Information(String Mp3Path, String LrcPath, String ImgPath) {
		ArrayList<Mp3Information> list = new ArrayList<Mp3Information>();
		File file = new File(Mp3Path);
		//File[] fileList = file.listFiles();
		//for (int i = 0; i < fileList.length; i++) {
			if (file.getName().endsWith(".mp3")) {
				Mp3Information info = new Mp3Information(file);
				
				File img = new File(super.sdCardRoot + File.separator + ImgPath 
						+ File.separator + info.getAlbum() + "_" + info.getArtist() + ".jpg");
				if (img.exists())
					info.setImgPath(super.sdCardRoot + File.separator + ImgPath 
							+ File.separator + info.getAlbum() + "_" + info.getArtist() + ".jpg");
				else info.setImgPath("");
				list.add(info);
			}
		return list;
	}
	
	public static class Mp3ComparatorBySongTitle implements Comparator<Mp3Information> {

		@Override
		public int compare(Mp3Information arg0,
				Mp3Information arg1) {
			try {
			PinyinComparator comparator = new PinyinComparator();
			int result = comparator.compare(arg0.getTitle(), arg1.getTitle());
			if (result > 0) {
				return 1;
			}
			else if (result < 0) {
				return -1;
			}
			else
				return 0;		
			} catch (Exception e) {
				//�������Exception�򷵻�-1
				System.out.println(arg0.getTitle() + arg1.getTitle());
				return -1;
			}
		}
	}

	public static class Mp3ComparatorByArtist implements Comparator<Mp3Information> {

		@Override
		public int compare(Mp3Information arg0,
				Mp3Information arg1) {
			PinyinComparator comparator = new PinyinComparator();
			int result = comparator.compare(arg0.getArtist(), arg1.getArtist());
			if (result > 0) {
				return 1;
			}
			else if (result < 0) {
				return -1;
			}
			else
				return 0;		
		}
	}
}
