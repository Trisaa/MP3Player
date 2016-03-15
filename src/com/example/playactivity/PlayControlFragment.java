package com.example.playactivity;

import com.example.lyrics.MacroDefination;
import com.example.mp3player01.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PlayControlFragment extends Fragment implements MacroDefination {

	private View view;
	private PlayActivity mCallback;
	
	private int status = STATUS_STOP;
	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnRewind;
	private SeekBar mSeekBar;
	private TextView textDuration;
	private TextView textCurrent;
	
	private SyncSeekBarThread thread = new SyncSeekBarThread();
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback = (PlayActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_play_control, container, false);
		btnPlay = (ImageButton) view.findViewById(R.id.control_play);
		btnForward = (ImageButton) view.findViewById(R.id.control_forward);
		btnRewind = (ImageButton) view.findViewById(R.id.control_rewind);
		mSeekBar = (SeekBar) view.findViewById(R.id.control_seekbar);
		textDuration = (TextView) view.findViewById(R.id.control_duration);
		textCurrent = (TextView) view.findViewById(R.id.control_current);
		
		btnPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				switch (status) {
				case STATUS_STOP:
				case STATUS_PAUSE:
					mCallback.play();
					break;
				case STATUS_PLAY:
					mCallback.pause();
					break;
				}
			}
			
		});
		btnForward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mCallback.next();
			}
			
		});
		btnRewind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mCallback.prev();
			}
			
		});
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			private int touchEnd;

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// touchBegin = arg0.getProgress();
				thread.setPause(true);
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				touchEnd = arg0.getProgress();
				switch (status) {
				case STATUS_PLAY:
					sendProgressCmd(touchEnd);
					thread.setPause(false);
					break;
				case STATUS_PAUSE:
					sendProgressCmd(touchEnd);
					thread.setPause(false);
					break;
				case STATUS_STOP:
					break;
				}
			}

			
		});
		return view;
	}

	/**
	 * 同步SeekBar线程
	 * 
	 * @author XIA
	 * 
	 */
	private class SyncSeekBarThread extends Thread {

		private volatile Thread thisThread;
		private volatile boolean pause;
		private SyncProgress sync = new SyncProgress();

		@Override
		public void run() {
			thisThread = Thread.currentThread();
			pause = false;
			while (thisThread != null) {
				if (!pause) {
					getActivity().runOnUiThread(sync);
				}
				try {
					Thread.sleep(200);
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

		private class SyncProgress implements Runnable {

			@Override
			public void run() {
				SyncSeekBar();
			}

			private void SyncSeekBar() {
				Intent intent = new Intent(ACTIVITY_REQUEST_PROGRESS);
				mCallback.sendBroadcast(intent);
			}
		}

	}
	
	@Override
	public void onPause() {
		super.onPause();
		thread.setPause(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		Intent intent = new Intent(ACTIVITY_REQUEST_PROGRESS);
		mCallback.sendBroadcast(intent);
		if (status == STATUS_PLAY) {
			try {
				thread.start();
			} catch (Exception e) {

			}
			thread.setPause(false);
		}
	}

	private String convertToTime(int ms) {
		int second = ms / 1000;
		int min = second / 60;
		second = second - min * 60;
		String str;
		if (second < 10)
			str = new String(min + ":0" + second);
		else
			str = new String(min + ":" + second);
		return str;
	}
	
	public void setDuration(int duration) {
		textDuration.setText(convertToTime(duration));
		mSeekBar.setMax(duration);
	}
	
	public void setStatus(int status) {
		this.status = status;
		switch (status) {
		case STATUS_PLAY:
			btnPlay.setBackgroundResource(R.drawable.control_pause);
			try {
				thread.start();
			} catch (Exception e) {
				
			}
			thread.setPause(false);
			break;
		case STATUS_PAUSE:
			btnPlay.setBackgroundResource(R.drawable.control_play);
			thread.setPause(false);
			break;
		}
	}
	
	public void setProgress(int progress) {
		mSeekBar.setProgress(progress);
		textCurrent.setText(convertToTime(progress));
	}
	
	public void sendProgressCmd(int progress) {
		Intent intent = new Intent(ACTIVITY_CMD_PROGRESS);
		intent.putExtra(DATA_PROGRESS, progress);
		mCallback.sendBroadcast(intent);
	}
}
