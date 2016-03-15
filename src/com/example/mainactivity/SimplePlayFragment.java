package com.example.mainactivity;

import com.example.common.fileoperation.Mp3Information;
import com.example.lyrics.ImageOperation;
import com.example.lyrics.MarqueeTextView;
import com.example.mp3player01.R;
import com.example.playactivity.PlayActivity;
import com.example.service.PlayBackService;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SimplePlayFragment extends Fragment {

	private View view;
	private MainActivity mCallback;
	
	private ProgressBar mProgressBar;	
	private MarqueeTextView textTitle;
	private TextView textArtistAndAlbum;
	private ImageButton btnPlay;
	private ImageView imgAlbum;
	private Bitmap bmAlbum;

	private Mp3Information mp3;

	private final int STATUS_PLAY = 0;
	private final int STATUS_PAUSE = 1;
	private int status = STATUS_PAUSE;
	
	private SyncProgressThread syncProgress = new SyncProgressThread();

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback = (MainActivity) activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_simple_play, container, false);
		init(view);
		mCallback.requestMp3Information();
		return view;
	}

	private void init(View view) {
		textTitle = (MarqueeTextView) view.findViewById(R.id.simple_song_title);
		textTitle.startScroll();
		textArtistAndAlbum = (TextView) view.findViewById(R.id.simple_song_artist);
		mProgressBar = (ProgressBar) view.findViewById(R.id.simple_progressbar);
		imgAlbum = (ImageView) view.findViewById(R.id.simple_album_image);
		imgAlbum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mCallback, PlayActivity.class);
				startActivity(intent);
			}
			
		});
		btnPlay = (ImageButton) view.findViewById(R.id.simple_button_play);
		btnPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				switch (status) {
				// 如果当前状态为播放，则暂停当前播放，将图标设置为 "播放"
				case STATUS_PLAY:
					mCallback.pause();
					break;
				// 如果当前状态为暂停，则开始当前播放，将图标设置为"暂停"
				case STATUS_PAUSE:
					mCallback.play();
					break;
				}
			}

		});
	}

	public void closeThreads() {
		if (syncProgress != null) {
			syncProgress.close();
		}
	}

	@Override
	public void onDestroy() {
		if (syncProgress != null) {
			syncProgress.close();
		}
		super.onDestroy();
	}
	
	public void setMp3Information(Mp3Information mp3, int duration, int status) {
		this.mp3 = mp3;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mp3.getImgPath(), opts);
		
		opts.inSampleSize = ImageOperation.computeSampleSize(opts, -1, 256*256);
		opts.inJustDecodeBounds = false;
		bmAlbum = BitmapFactory.decodeFile(mp3.getImgPath(), opts);
		imgAlbum.setImageBitmap(bmAlbum);
		textTitle.setMarqueeText(mp3.getTitle());
		textArtistAndAlbum.setText(mp3.getArtist() + "  " + mp3.getAlbum());

		mProgressBar.setMax(duration);
		switch (status) {
		case PlayBackService.STATUS_PAUSE:
			btnPlay.setBackgroundResource(R.drawable.control_play_circle);
			break;
		case PlayBackService.STATUS_PLAY:
			btnPlay.setBackgroundResource(R.drawable.control_pause_circle);
			break;
		}
		this.status = status;
		if (this.isResumed()) {
			try {
				syncProgress.setPause(false);
				syncProgress.start();
			} catch (Exception e) {
				
			}
			
		}
	}
	
	public void pause() {

		btnPlay.setBackgroundResource(R.drawable.control_play_circle);
		status = STATUS_PAUSE;
		syncProgress.setPause(true);
	}
	
	public void play() {
		btnPlay.setBackgroundResource(R.drawable.control_pause_circle);
		status = STATUS_PLAY;
		if (this.isResumed()) {
				if (syncProgress == null)
					syncProgress.start();
				else syncProgress.setPause(false);
		}
	}


	private class SyncProgressThread extends Thread {

		private volatile Thread thisThread;
		private volatile boolean pause;

		@Override
		public void run() {
			thisThread = Thread.currentThread();
			pause = false;
			while (thisThread != null) {
				if (!pause) {
					mCallback.requestProgress();
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void close() {
			thisThread = null;
		}

		public void setPause(boolean pause) {
			this.pause = pause;
		}
		
		public boolean isPause() {
			return pause;
		}
	}
	
	public void setProgress(int progress) {
		mProgressBar.setProgress(progress);
	}

	@Override
	public void onPause() {
		super.onPause();
		syncProgress.setPause(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		syncProgress.setPause(false);
	}
}
