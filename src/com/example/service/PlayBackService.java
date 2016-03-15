package com.example.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.common.fileoperation.Mp3Information;
import com.example.lyrics.MacroDefination;
import com.example.mainactivity.MainActivity;
import com.example.mp3player01.R;
import com.example.playactivity.PlayActivity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.ImageButton;
import android.widget.RemoteViews;

public class PlayBackService extends Service implements MacroDefination {

	private final IBinder mBinder = new ServiceBinder();
	private MediaPlayer Mp3Player;
	private int status = STATUS_STOP;
	private int mode = MODE_SEQUENCE;
	
	private NotificationManager myNotice; // 通知栏
	private BroadcastReceiver onStopReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("sendMSG")) {
				Intent nointent = new Intent();
				// 在这里处理点击事件
				switch (status) {
				case STATUS_PLAY:
					nointent = new Intent(ACTIVITY_CMD_PAUSE);
					sendBroadcast(nointent);

					break;
				case STATUS_PAUSE:
					nointent = new Intent(ACTIVITY_CMD_PLAY);
					sendBroadcast(nointent);
					break;
				}
			}
		}
	};

	private BroadcastReceiver onNextReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("NextMSG")) {
				Intent next = new Intent(ACTIVITY_CMD_NEXT);
				sendBroadcast(next);
			}
		}
	};
	private BroadcastReceiver onFullReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("fullMSG")) {
				;
			}
		}
	};

	private ArrayList<Mp3Information> mPlayList = new ArrayList<Mp3Information>();
	private List<Integer> mSequenceList = new ArrayList<Integer>();
	private int mCurrentIndex = 0;

	CommandReceiver mMainReceiver = new CommandReceiver();

	public class CommandReceiver extends BroadcastReceiver {

		@Override
		public synchronized void onReceive(Context arg0, Intent intent) {
			String action = intent.getAction();
			System.out.println("Service receive " + action);
			// 当Service接收到Activity的进度条请求时，Service返回一个进度条广播
			if (ACTIVITY_REQUEST_PROGRESS.compareTo(action) == 0) {
				Intent progress = new Intent(SERVICE_RETURN_PROGRESS);
				try {
					progress.putExtra(DATA_PROGRESS,
						Mp3Player.getCurrentPosition());
				} catch (Exception e) {
				}
				sendBroadcast(progress);
			}
			// 当检测到耳机拔出操作，暂停歌曲，并发送暂停广播
			else if ("android.intent.action.HEADSET_PLUG".compareTo(action) == 0) {
				if (intent.getIntExtra("state", 0) == 0) {
					pause();
				}
			} else if (ACTIVITY_CMD_PROGRESS.compareTo(action) == 0) {
				if (Mp3Player != null) {
					int progress = intent.getIntExtra(DATA_PROGRESS, 0);
					Mp3Player.seekTo(progress);
				}
			}
			// 当Service收到开始请求，获得播放列表，开始播放，并且返回一个正在播放歌曲信息
			else if (ACTIVITY_CMD_START.compareTo(action) == 0) {
				mPlayList = (ArrayList<Mp3Information>) intent
						.getSerializableExtra(DATA_PLAY_LIST);
				int mode = intent.getIntExtra(DATA_PLAY_MODE, MODE_RANDOM);
				start(mode);
				PlayBackService.this.updateNotification(
						getCurrent().getTitle(), getCurrent().getArtist());
				sendMp3Information();
			}
			// 当Service接收到暂停请求，暂停，并发送暂停广播
			else if (ACTIVITY_CMD_PAUSE.compareTo(action) == 0) {
				pause();
				PlayBackService.this.updateNotification(
						getCurrent().getTitle(), getCurrent().getArtist());
			} else if (ACTIVITY_CMD_NEXT.compareTo(action) == 0) {
				next();
				PlayBackService.this.updateNotification(
						getCurrent().getTitle(), getCurrent().getArtist());
			} else if (ACTIVITY_CMD_PREV.compareTo(action) == 0) {
				prev();
				PlayBackService.this.updateNotification(
						getCurrent().getTitle(), getCurrent().getArtist());
			}
			// 当Service收到播放请求，播放，并发送开始播放广播
			else if (ACTIVITY_CMD_PLAY.compareTo(action) == 0) {
				play();
				Intent reply = new Intent(SERVICE_RETURN_MP3_INFO);
				reply.putExtra(DATA_PLAY_STATUS, STATUS_PLAY);
				sendBroadcast(reply);
				try {
					PlayBackService.this.updateNotification(getCurrent()
							.getTitle(), getCurrent().getArtist());
				} catch (Exception e) {

				}
			}
			// 当Service收到请求当前播放歌曲请求，发送当前歌曲信息
			else if (ACTIVITY_REQUEST_MP3_INFO.compareTo(action) == 0) {
				if (mPlayList.size() > 0) {
					sendMp3Information();
				}
			}
			// 当Service收到请求播放列表请求，发送播放列表
			else if (ACTIVITY_REQUEST_PLAY_LIST.compareTo(action) == 0) {
				Intent reply = new Intent(SERVICE_RETURN_PLAY_LIST);
				reply.putExtra(DATA_PLAY_LIST, new ArrayList<Mp3Information>(
						mPlayList));
				sendBroadcast(reply);
			} else if (ACTIVITY_CMD_CURRENT.compareTo(action) == 0) {
				int index = intent.getIntExtra(DATA_CURRENT, 0);
				generateSequence();
				for (int i = 0; i < mSequenceList.size(); i++) {
					if (mSequenceList.get(i) == index) {
						mCurrentIndex = i;
						break;
					}
				}
				status = STATUS_STOP;
				play();
				sendMp3Information();
			}
			// 当Service收到停止命令，停止服务
			else if (ACTIVITY_CMD_STOP.compareTo(action) == 0) {
				stopSelf();
			}
		}
	}

	private void sendMp3Information() {
		Intent reply = new Intent(SERVICE_RETURN_MP3_INFO);
		reply.putExtra(DATA_MP3_INFO, getCurrent());
		reply.putExtra(DATA_MP3_DURATION, Mp3Player.getDuration());
		reply.putExtra(DATA_PLAY_STATUS, status);
		sendBroadcast(reply);
	}

	public class ServiceBinder extends Binder {

		public PlayBackService getService() {
			return PlayBackService.this;
		}

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initCommandReceiver();
		IntentFilter stopfilter = new IntentFilter();
		stopfilter.addAction("sendMSG");
		IntentFilter nextfilter = new IntentFilter();
		nextfilter.addAction("NextMSG");
		IntentFilter fullfilter = new IntentFilter();
		fullfilter.addAction("fullMSG");
		this.registerReceiver(onStopReceiver, stopfilter);
		this.registerReceiver(onNextReceiver, nextfilter);
		this.registerReceiver(onFullReceiver, fullfilter);
		myNotice = (NotificationManager) this
				.getSystemService(this.NOTIFICATION_SERVICE);// 获得系统服务
		System.out.println("Service on Create.");
	}

	private void initCommandReceiver() {

		IntentFilter mainFilter = new IntentFilter();
		mainFilter.addAction(ACTIVITY_CMD_START);
		mainFilter.addAction(ACTIVITY_CMD_PLAY);
		mainFilter.addAction(ACTIVITY_CMD_PAUSE);
		mainFilter.addAction(ACTIVITY_CMD_NEXT);
		mainFilter.addAction(ACTIVITY_CMD_PREV);
		mainFilter.addAction(ACTIVITY_CMD_STOP);
		mainFilter.addAction(ACTIVITY_REQUEST_MP3_INFO);
		mainFilter.addAction(ACTIVITY_REQUEST_PLAY_LIST);
		mainFilter.addAction(ACTIVITY_REQUEST_PROGRESS);
		mainFilter.addAction(ACTIVITY_CMD_PROGRESS);
		mainFilter.addAction(ACTIVITY_CMD_CURRENT);
		mainFilter.addAction("android.intent.action.HEADSET_PLUG");
		registerReceiver(mMainReceiver, mainFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (Mp3Player != null) {
			Mp3Player.release();
		}
		unregisterReceiver(mMainReceiver);
		unregisterReceiver(onStopReceiver);
		unregisterReceiver(onNextReceiver);
		unregisterReceiver(onFullReceiver);
		myNotice.cancel(0);
		System.out.println("Service on destory.");
	}

	public void reset() {
		mCurrentIndex = 0;
		status = STATUS_STOP;
		if (Mp3Player != null) {
			Mp3Player.release();
			Mp3Player = null;
		}
	}

	public void start(int mode) {
		reset();
		this.mode = mode;
		generateSequence();
		try {
			play();
		} catch (Exception e) {

		}
	}

	/**
	 * 生成播放序列
	 */
	private void generateSequence() {
		mSequenceList = new ArrayList<Integer>();
		for (int i = 0; i < mPlayList.size(); i++) {
			mSequenceList.add(i);
		}
		switch (mode) {
		case MODE_SEQUENCE:
			break;
		case MODE_RANDOM:
			Collections.shuffle(mSequenceList);
			break;
		case MODE_SINGLE:
			break;
		}
	}

	public void play() {
		try {
			switch (status) {
			case STATUS_STOP:
				if (Mp3Player != null) {
					Mp3Player.release();
				}
				Mp3Player = MediaPlayer.create(this,
						Uri.parse("file://" + getCurrent().getPath()));
				Mp3Player
						.setOnCompletionListener(new OnMp3CompletionListener());
				Mp3Player.start();
				status = STATUS_PLAY;
				sendMp3Information();
				break;
			case STATUS_PLAY:
				
				break;
			case STATUS_PAUSE:
				Mp3Player.start();
				status = STATUS_PLAY;
				sendMp3Information();
			}
			// 如果服务中没有播放列表，发送播放列表请求给Activity
		} catch (IndexOutOfBoundsException e) {
			Intent intent = new Intent(SERVICE_REQUEST_MP3_LIST);
			sendBroadcast(intent);
		}
	}

	public void pause() {
		switch (status) {
		case STATUS_PLAY:
			Mp3Player.pause();
			status = STATUS_PAUSE;
			break;
		}
		Intent reply = new Intent(SERVICE_RETURN_MP3_INFO);
		reply.putExtra(DATA_PLAY_STATUS, STATUS_PAUSE);
		sendBroadcast(reply);
	}

	public void stop() {
		
		if (Mp3Player != null) {
			Mp3Player.release();
		}
	}

	public void next() {
		if (Mp3Player != null) {
			Mp3Player.release();
		}
		mCurrentIndex = mCurrentIndex < mPlayList.size() - 1 ? mCurrentIndex + 1
				: 0;
		status = STATUS_STOP;
		play();

	}

	public void prev() {
		if (Mp3Player != null) {
			Mp3Player.release();
		}
		mCurrentIndex = mCurrentIndex > 0 ? mCurrentIndex - 1 : mPlayList
				.size() - 1;
		status = STATUS_STOP;
		play();
		sendMp3Information();

	}

	public ArrayList<Mp3Information> getPlayList() {
		return mPlayList;
	}

	public Mp3Information getCurrent() {
		return mPlayList.get(mSequenceList.get(mCurrentIndex));
	}

	public void setPlayList(ArrayList<Mp3Information> mPlayList) {
		this.mPlayList = mPlayList;
		mCurrentIndex = 0;
	}

	private class OnMp3CompletionListener implements OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer arg0) {
			status = STATUS_STOP;
			myNotice.cancel(0);
			switch (mode) {
			case MODE_SEQUENCE:
				mCurrentIndex = mCurrentIndex < mPlayList.size() - 1 ? mCurrentIndex + 1
						: 0;
				break;
			case MODE_RANDOM:
				mCurrentIndex = mCurrentIndex < mPlayList.size() - 1 ? mCurrentIndex + 1
						: 0;
				break;
			case MODE_SINGLE:
				break;
			}
			play();
			PlayBackService.this.updateNotification(getCurrent().getTitle(),
					getCurrent().getArtist());
			Intent reply = new Intent(SERVICE_RETURN_MP3_INFO);
			reply.putExtra(DATA_MP3_INFO, getCurrent());
			reply.putExtra(DATA_MP3_DURATION, Mp3Player.getDuration());
			reply.putExtra(DATA_PLAY_STATUS, STATUS_PLAY);
			sendBroadcast(reply);
		}
	}

	public void updateNotification(String title, String singer) {
		// 处理通知栏事物
		long when = System.currentTimeMillis(); // 通知产生的时间，会在通知信息里显示
		// 用上面的属性初始化 Nofification
		Intent stopIntent = new Intent("sendMSG");
		Intent nextIntent = new Intent("NextMSG");
		Intent delIntent = new Intent("fullMSG");
		Intent notificationIntent = new Intent(this, PlayActivity.class);
		PendingIntent pendStopIntent = PendingIntent.getBroadcast(this, 0,
				stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pendNextIntent = PendingIntent.getBroadcast(this, 0,
				nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pendFullIntent = PendingIntent.getBroadcast(this, 0,
				delIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification.Builder builder = new Notification.Builder(this);
		builder.setSmallIcon(R.drawable.ic_launcher)
				.setTicker(title)
				// .setWhen(when)
				.setContentTitle(title)
				.setContentText(singer)
				.setContentIntent(
						PendingIntent.getActivity(this, 0, notificationIntent,
								0)).setDeleteIntent(pendFullIntent);
		switch (status) {
		case STATUS_PAUSE:

			builder.addAction(R.drawable.noticeplay, "播放", pendStopIntent);
			builder.addAction(R.drawable.ic_notification_next, "下一首",
					pendNextIntent);
			break;
		case STATUS_PLAY:
			builder.addAction(R.drawable.noticepause, "暂停", pendStopIntent);
			builder.addAction(R.drawable.ic_notification_next, "下一首",
					pendNextIntent);
			// notification.flags = Notification.FLAG_ONGOING_EVENT;//加入到正在运行
			break;
		}
		Notification notification = builder.build();
		if (status == STATUS_PLAY)
			notification.flags = Notification.FLAG_ONGOING_EVENT;// 加入到正在运行
		this.startForeground(0, notification);// 设置为前台服务
		myNotice.notify(0, notification);
	}
}
