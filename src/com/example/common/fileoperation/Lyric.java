package com.example.common.fileoperation;

import java.util.ArrayList;
import java.util.List;

/**
 * ������װ�����Ϣ����
 * @author Administrator
 *
 */
public class Lyric {

    private String title;//������
	private String singer;//�ݳ���
	private String album;//ר��	
	//private HashMap<Long,String> infos;//��������Ϣ��ʱ���һһ��Ӧ��Map
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
	
   //����Ϊgetter()  setter()
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


