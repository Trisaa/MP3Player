package com.example.mainactivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.example.common.fileoperation.Mp3FileOperation;
import com.example.common.fileoperation.Mp3Information;
import com.example.lyrics.PinyinComparator;
import com.example.mp3player01.R;
import com.example.service.PlayBackService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.widget.Toast;

public class LoadActivity extends Activity {
	private static final int LOAD_DISPLAY_TIME = 200;
	ArrayList<Mp3Information> mMp3List  = new ArrayList<Mp3Information>();
	ArrayList<String> mArtistList       = new ArrayList<String>();
	HashMap<String, ArrayList<Mp3Information>> mArtistMap 
	                                    = new HashMap<String, ArrayList<Mp3Information>>();
	ArrayList<Mp3Information> mPlayList = new ArrayList<Mp3Information>();

	private final String MP3_PATH = "MIUI/music/mp3";      // Mp3文件路径
	private final String LRC_PATH = "MIUI/music/lyric";
	private final String IMG_PATH = "MIUI/music/album";
	private final int  TIME =60000;
	
	private Cursor cursor=null;   //读取数据库
	private String[] path;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_load);
		Intent intent = new Intent(this, PlayBackService.class);
		startService(intent);
		//利用Mediastore接口得到存储卡上的歌曲信息
		cursor = this.getContentResolver().query(  
	            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,  
	            null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		initMp3Information();
		initArtistInformation();
		
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent mainIntent = new Intent(LoadActivity.this, MainActivity.class);
				mainIntent.putExtra("mMp3List", mMp3List);
				mainIntent.putExtra("mArtistList", mArtistList);
				mainIntent.putExtra("mArtistMap", mArtistMap);
				mainIntent.putExtra("mPlayList", mPlayList);
				LoadActivity.this.startActivity(mainIntent);
				LoadActivity.this.finish();
			}
		}, LOAD_DISPLAY_TIME);
	}
	//加载存储卡内的艺术家
	
	private void initArtistInformation() {
		mArtistMap = new HashMap<String, ArrayList<Mp3Information>>();

		// 选出所有Artist
		for (int i = 0; i < mMp3List.size(); i++) {
			String artist = mMp3List.get(i).getArtist();
			if (!mArtistList.contains(artist)) {
				mArtistList.add(artist);
			}
		}
		Collections.sort(mArtistList, new PinyinComparator());
		// 选出与Artist匹配的Mp3Information
		
		for (int i = 0; i < mArtistList.size(); i++) {
			ArrayList<Mp3Information> songs = new ArrayList<Mp3Information>();
			String artist = mArtistList.get(i);
			for (int j = 0; j < mMp3List.size(); j++) {
				Mp3Information mp3 = mMp3List.get(j);
				if (artist.compareToIgnoreCase(mp3.getArtist()) == 0) {
					songs.add(mp3);
				}
			}
			// 放入Map中
			mArtistMap.put(artist, songs);
		}
	}
	//加载存储卡内的歌曲信息
	
	private void initMp3Information() {
		int i=0;
		long time=0;
		Mp3FileOperation fileOperation = new Mp3FileOperation();
		//歌曲文件路径
		path = new String[cursor.getCount()];
		if(cursor.moveToFirst())
		{
			do{
				path[i]=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));	
				if(path[i].endsWith(".mp3"))
				{
					    //获得歌曲的时长
					time=cursor.getLong(cursor  
                            .getColumnIndex(MediaStore.Audio.Media.DURATION));
					 if(time>TIME)
					 {	 
						try{
					    //
					    mMp3List.add(fileOperation.getMp3Information(path[i], LRC_PATH, IMG_PATH).get(0));
						}catch(Exception e)
						{
							e.printStackTrace();
						}
					 }
					 i++;
				}
			}
		    while(cursor.moveToNext());
			
		}
		i=0;
		Collections.sort(mMp3List,
				new Mp3FileOperation.Mp3ComparatorBySongTitle());
	}
	
}
	
