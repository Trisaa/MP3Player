package com.example.common.fileoperation;

import java.util.ArrayList;
import java.util.List;

/**
 * 用来封装歌词信息的类
 * @author Administrator
 *
 */
public class Lyric {

    private String title;//歌曲名
	private String singer;//演唱者
	private String album;//专辑	
	//private HashMap<Long,String> infos;//保存歌词信息和时间点一一对应的Map
	private List<LrcSentence> infos = new ArrayList<LrcSentence>();
	public List<LrcSentence> getlist(){
		return infos;
	}
	
	public class LrcSentence {
		private long time;
		private String sentence;
		
		public LrcSentence(long time, String sentence) {
			this.time = time;
			this.sentence = sentence;
		}
		
		public long getTime() {
			return time;
		}
		
		public String getSentence() {
			return sentence;
		}
	}
	
	public String updateLrc(long currentTime) {
		for (int i = 0 ; i < infos.size(); i++) {
			if (currentTime < infos.get(i).getTime()) {
				if (i >= 1 && currentTime > infos.get(i - 1).getTime()) {
					return infos.get(i - 1).getSentence();
				} else {
					return infos.get(0).getSentence();
				}
			}
		}
		return null;
		
	}
	public LrcSentence getindex(long currentTime) {
		for (int i = 0 ; i < infos.size(); i++) {
			if (currentTime < infos.get(i).getTime()) {
				if (i >= 1 && currentTime > infos.get(i - 1).getTime()) {
					return infos.get(i - 1);
				} else {
					return infos.get(0);
				}
			}
		}
		return null;
		
	}
	
   //以下为getter()  setter()
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public void add(long time, String sentence) {
		LrcSentence newSentence = new LrcSentence(time, sentence);
		infos.add(newSentence);
	}
}


