package com.example.mainactivity;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.common.fileoperation.Mp3Information;
import com.example.lyrics.MacroDefination;
import com.example.mp3player01.R;
import com.example.service.PlayBackService;
import com.example.settingactivity.SettingActivity;

import android.os.Bundle;
import android.os.Vibrator;
import android.app.ActionBar;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements MacroDefination {
	
	private ActionBar           mActionBar;
	private Vibrator            mVibrator;
	private FragmentTabHost     mTabHost;
	private ViewPager           mViewPager;
	private PagerAdapter        mSectionsPagerAdapter;	
	
	private ActionMode          mActionMode;
	private ActionMode.Callback mActionModeCallback;
	
	private SongListFragment    mSongListFragment;
	private ArtistListFragment  mArtistListFragment;
	private Fragment            mFavoriteListFragment = new Fragment();
	private SimplePlayFragment  mSimplePlayFragment;
	
	private final static String SONGS     = "歌曲";
	private final static String ARTISTS   = "艺术家";
	private final static String FAVORITE  = "我喜爱的";
	
	private final static int SONGS_POS    = 0;
	private final static int ARTISTS_POS  = 1;
	private final static int FAVORITE_POS = 2;
	
	ArrayList<Mp3Information> mMp3List  = new ArrayList<Mp3Information>();
	ArrayList<String> mArtistList       = new ArrayList<String>();
	HashMap<String, ArrayList<Mp3Information>> mArtistMap 
	                                    = new HashMap<String, ArrayList<Mp3Information>>();
	ArrayList<Mp3Information> mPlayList = new ArrayList<Mp3Information>();
	

	
	CommandReceiver cmdReceiver = new CommandReceiver();
	IntentFilter mFilter = new IntentFilter();
	private long mExitTime = 0;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMp3List = (ArrayList<Mp3Information>) getIntent().getSerializableExtra("mMp3List");
		mArtistList = (ArrayList<String>) getIntent().getSerializableExtra("mArtistList");
		mArtistMap = (HashMap<String, ArrayList<Mp3Information>>) getIntent().getSerializableExtra("mArtistMap");
		mPlayList = (ArrayList<Mp3Information>) getIntent().getSerializableExtra("mPlayList");
		setContentView(R.layout.activity_main);
				
		mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		initCAB();
		initTabHost();
		initFragments();
		initViewPager();
		mFilter.addAction(SERVICE_RETURN_MP3_INFO);
		mFilter.addAction(SERVICE_RETURN_PROGRESS);
		mFilter.addAction(SERVICE_REQUEST_MP3_LIST);
		
	}
	
	public void requestMp3Information() {
		Intent intent = new Intent(ACTIVITY_REQUEST_MP3_INFO);
		sendBroadcast(intent);
	}
	
	public void requestProgress() {
		Intent intent = new Intent(ACTIVITY_REQUEST_PROGRESS);
		sendBroadcast(intent);
	}
	
	private void initTabHost() {
		mTabHost = (FragmentTabHost) findViewById(R.id.tab_host);
		mTabHost.setup(this, getSupportFragmentManager());
		mTabHost.addTab(mTabHost.newTabSpec(SONGS).setIndicator(SONGS), Fragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(ARTISTS).setIndicator(ARTISTS), Fragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(FAVORITE).setIndicator(FAVORITE), Fragment.class, null);
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String indicator) {
				if (mActionMode != null) {
					mActionMode.finish();
				}
				if (indicator.compareToIgnoreCase(SONGS) == 0) {
					mViewPager.setCurrentItem(SONGS_POS);
				} else if (indicator.compareToIgnoreCase(ARTISTS) == 0) {
					mViewPager.setCurrentItem(ARTISTS_POS);
				} else if (indicator.compareToIgnoreCase(FAVORITE) == 0) {
					mViewPager.setCurrentItem(FAVORITE_POS);
				}
			}
			
		});
	}
		
	private void initCAB() {
		mActionModeCallback = new ActionMode.Callback() {

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.main_select_list, menu);
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch(item.getItemId()) {
				case R.id.action_add_play:
					System.out.println(mPlayList);
					start(PlayBackService.MODE_SEQUENCE);
					mSongListFragment.reset();
					mActionMode.finish();
					break;
				}
				
				return true;
			}

			/**
			 * 当Contextual ActionBar 退出时调用此方法，重置songListFragment中的ListView
			 */
			@Override
			public void onDestroyActionMode(ActionMode mode) {
				mActionMode = null;
				mSongListFragment.reset();
			}
		};
	}

	private void initViewPager() {
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setAlwaysDrawnWithCacheEnabled(true);
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				mTabHost.setCurrentTab(position);
			}
		});
	}

	private void initFragments() {
		this.mSongListFragment = new SongListFragment(mMp3List);
		this.mArtistListFragment = new ArtistListFragment(mArtistList, mArtistMap);
		mSimplePlayFragment = (SimplePlayFragment) getSupportFragmentManager()
				.findFragmentById(R.id.simple_play_fragment);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent settingIntent = new Intent(MainActivity.this,SettingActivity.class);
			startActivity(settingIntent);
			break;
		}
		return false;
	}


	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(cmdReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(cmdReceiver, mFilter);
		requestMp3Information();
	}

	@Override
	protected void onDestroy() {
		//unregisterReceiver(cmdReceiver);
		super.onDestroy();
	}
	
	public void start(int mode) {
		Intent intent = new Intent(ACTIVITY_CMD_START);
		intent.putExtra(PlayBackService.DATA_PLAY_LIST, mPlayList);
		intent.putExtra(PlayBackService.ACTIVITY_CMD_MODE, mode);
		sendBroadcast(intent);
	}
	
	public void play() {
		Intent intent = new Intent(ACTIVITY_CMD_PLAY);
		sendBroadcast(intent);
	}
	
	public void pause() {
		Intent intent = new Intent(ACTIVITY_CMD_PAUSE);
		sendBroadcast(intent);
	}
	
	public void stop() {
		Intent intent = new Intent(ACTIVITY_CMD_STOP);
		sendBroadcast(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			// 如果按下返回键间隔小于2000ms则退出程序
			if (System.currentTimeMillis() - mExitTime > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				//mSimplePlayFragment.closeThreads();
				Intent intent = new Intent(ACTIVITY_CMD_STOP);
				sendBroadcast(intent);
				finish();
			}
		}
		return false;
	}
	
	/**
	 * viewpager的适配器
	 * 
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		private final int TOTAL_FRAGMENT = FAVORITE_POS + 1;

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case SONGS_POS:
				fragment = mSongListFragment;
				break;
			case ARTISTS_POS:
				fragment = mArtistListFragment;
				break;
			case FAVORITE_POS:
				fragment = mFavoriteListFragment;
				break;
			}
			Bundle args = new Bundle();
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return TOTAL_FRAGMENT;
		}
	}

	
	public class CommandReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			String action = intent.getAction();
			System.out.println("MainActivity receive " + action);
			if (SERVICE_RETURN_PROGRESS.compareTo(action) == 0) {
				int progress = intent.getIntExtra(DATA_PROGRESS, 0);
				mSimplePlayFragment.setProgress(progress);
			} else if (SERVICE_RETURN_MP3_INFO.compareTo(action) == 0) {
				Mp3Information mp3 = (Mp3Information) intent.getSerializableExtra(DATA_MP3_INFO);
				int duration = intent.getIntExtra(DATA_MP3_DURATION, 0);
				int status = intent.getIntExtra(DATA_PLAY_STATUS, STATUS_PAUSE);
				if (mp3 != null)
					mSimplePlayFragment.setMp3Information(mp3, duration, status);
				switch (status) {
				case STATUS_PLAY:
					mSimplePlayFragment.play();
					break;
				case STATUS_PAUSE:
					mSimplePlayFragment.pause();
					break;
				}
			} else if (SERVICE_REQUEST_MP3_LIST.compareTo(action) == 0) {
				Intent reply = new Intent(ACTIVITY_CMD_START);
				reply.putExtra(DATA_PLAY_LIST, new ArrayList<Mp3Information>(mMp3List));
				reply.putExtra(DATA_PLAY_MODE, MODE_RANDOM);
				sendBroadcast(reply);
			}
		}
		
	}
	
	public Vibrator getVibrator() {
		return mVibrator;
	}
	
	public void setPlayList(ArrayList<Mp3Information> playList, int choiceMode) {
		this.mPlayList = playList;
		switch (choiceMode) {
		case SINGLE_CHOICE:
			break;
		case MULTIPLE_CHOICE:
			if (mActionMode == null) {
				mActionMode = startActionMode(mActionModeCallback);
			}
			if (playList.size() == 0) {
				mActionMode.finish();
			} else if (playList.size() == 1) {
				mActionMode.setTitle("已选择 " + playList.get(0).getTitle());
			} else {
				mActionMode.setTitle("已选择" + playList.size() + "首歌曲");
			}
			break;
		}
	}
}
