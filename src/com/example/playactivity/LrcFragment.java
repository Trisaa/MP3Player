package com.example.playactivity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.common.fileoperation.LrcParser;
import com.example.common.fileoperation.Lyric;
import com.example.common.fileoperation.Mp3Information;
import com.example.lyrics.LyricNewTextView;
import com.example.mp3player01.R;

public class LrcFragment extends Fragment {
	private View view;
	private LyricNewTextView textLrc;
	private String flag="00000000000";
	private Lyric lyric;
	private LrcParser lrcPaser;
	private Mp3Information currentMp3;
	public boolean start=true;
	
	public void setMp3Information(Mp3Information currentMp3_1){
		this.currentMp3=currentMp3_1;
		lrcPaser=new LrcParser();
//		System.out.println("path----------->"+currentMp3.getLrcPath());
		try {
			lyric=null;
			lyric=lrcPaser.parser(currentMp3.getLrcPath());
			if(lyric==null){
				lyric=new Lyric();
			}
			textLrc.setlyric(lyric);
			textLrc.setList(lyric.getlist());
//			System.out.println("---------------------------"+lyric.getlist().get(1).getSentence());
//			System.out.println("path----------->"+currentMp3.getLrcPath());
//			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}
	public void setcurrentTime(int currentTime){
		if(lyric==null){
			return;
		}
		Lrctextchanged(currentTime);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view= inflater.inflate(R.layout.fragment_lyric, container, false);
		textLrc=(LyricNewTextView)view.findViewById(R.id.lrctextview);
		lrcPaser=new LrcParser();
		lyric=new Lyric();
		return view;
	}
	public void Lrctextchanged(int currentTime){
		if(flag!=lyric.updateLrc(currentTime)&&start){
			start=false;
			flag=lyric.updateLrc(currentTime);
			textLrc.setTime(currentTime);
			start=textLrc.invalidate_1();
		}
//		else{
//			textLrc.stopScroll();
//		}
	}
		
	

}
