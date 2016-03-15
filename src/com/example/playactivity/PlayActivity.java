package com.example.playactivity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost.OnTabChangeListener;

import com.example.common.fileoperation.Mp3Information;
import com.example.lyrics.MacroDefination;
import com.example.mp3player01.R;
import com.example.service.PlayBackService;

public class PlayActivity extends FragmentActivity implements MacroDefination {

	private ActionBar       mActionBar;
	private FragmentTabHost mTabHost;
	private ViewPager       mViewPager;
	private PagerAdapter    mSectionsPagerAdapter;
	private final CommandReceiver cmdReceiver = new CommandReceiver();
	private final IntentFilter mFilter = new IntentFilter();
	private ActionMode          mActionMode;
	private ActionMode.Callback mActionModeCallback;
	
	private DetailPlayFragment mDetailPlayFragment;
	private LrcFragment         mLyricsFragment = new LrcFragment();
	private PlayListFragment mPlayListFragment = new PlayListFragment();
	private PlayControlFragment mPlayControlFragment;
	private int index=0;
	
	private final static String LYRICS    = "歌词";
	private final static String PLAY_LIST = "播放列表";
	
	private final static int LYRICS_POS    = 0;
	private final static int PLAY_LIST_POS = 1;
		
	private ArrayList<Mp3Information> mPlayList = new ArrayList<Mp3Information>();
	
	public class CommandReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			
			String action = intent.getAction();
			System.out.println("PlayActivity receive " + action);
			if (SERVICE_RETURN_PLAY_LIST.compareTo(action) == 0) {
				mPlayList = (ArrayList<Mp3Information>) intent.getSerializableExtra(DATA_PLAY_LIST);
				mPlayListFragment.setPlayList(mPlayList);
				mPlayListFragment.refresh();
			} else if (SERVICE_RETURN_MP3_INFO.compareTo(action) == 0) {
				Mp3Information mp3 = (Mp3Information) intent.getSerializableExtra(DATA_MP3_INFO);
				int duration = intent.getIntExtra(DATA_MP3_DURATION, 0);
				int status = intent.getIntExtra(DATA_PLAY_STATUS, STATUS_PAUSE);
				if (mp3 != null) {
					mDetailPlayFragment.setMp3Information(mp3);
					mPlayControlFragment.setDuration(duration);
					mLyricsFragment.setMp3Information(mp3);
				}
				mPlayControlFragment.setStatus(status);
			} else if (SERVICE_RETURN_PROGRESS.compareTo(action) == 0) {
				int progress = intent.getIntExtra(DATA_PROGRESS, 0);
				mPlayControlFragment.setProgress(progress);
				mLyricsFragment.setcurrentTime(progress);
			
			}
		}
		
	}
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_play);
		
		//设置左上角图标为返回上级
		mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		
		FragmentManager fm = getSupportFragmentManager();
		mDetailPlayFragment = (DetailPlayFragment) fm.findFragmentById(R.id.detail_play_fragment);
		mPlayControlFragment = (PlayControlFragment) fm.findFragmentById(R.id.play_control_fragment);
		initTabHost();
		initViewPager();
		initCAB();
		mFilter.addAction(SERVICE_RETURN_PLAY_LIST);
		mFilter.addAction(SERVICE_RETURN_MP3_INFO);
		mFilter.addAction(SERVICE_RETURN_PROGRESS);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.play, menu);
		return true;
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
		Intent intent = new Intent(ACTIVITY_REQUEST_PLAY_LIST);
		sendBroadcast(intent);
		Intent mp3Intent = new Intent(ACTIVITY_REQUEST_MP3_INFO);
		sendBroadcast(mp3Intent);
	}

	private void initTabHost() {
		mTabHost = (FragmentTabHost) findViewById(R.id.tab_host);
		mTabHost.setup(this, getSupportFragmentManager());
		mTabHost.addTab(mTabHost.newTabSpec(LYRICS).setIndicator(LYRICS), Fragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(PLAY_LIST).setIndicator(PLAY_LIST), Fragment.class, null);
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String indicator) {
				if (indicator.compareToIgnoreCase(LYRICS) == 0) {
					mViewPager.setCurrentItem(LYRICS_POS);
				} else if (indicator.compareToIgnoreCase(PLAY_LIST) == 0) {
					mViewPager.setCurrentItem(PLAY_LIST_POS);
				}
			}
			
		});
	}private void initCAB() {
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
			}
		};
	}
	private void initViewPager() {
		if (mPlayList == null) {
			return;
		}
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
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public ArrayList<Mp3Information> getPlayList() {
		return mPlayList;
	}
	
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		private final int TOTAL_FRAGMENT = PLAY_LIST_POS + 1;

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case LYRICS_POS:
				fragment = mLyricsFragment;
				break;
			case PLAY_LIST_POS:
				fragment = mPlayListFragment;
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
	

	public void next() {
		Intent intent = new Intent(ACTIVITY_CMD_NEXT);
		sendBroadcast(intent);
	}


	public void prev() {
		Intent intent = new Intent(ACTIVITY_CMD_PREV);
		sendBroadcast(intent);
	}
	
	public void setCurrent(int current) {

		Intent intent = new Intent(ACTIVITY_CMD_CURRENT);
		intent.putExtra(DATA_CURRENT, current);
		sendBroadcast(intent);
	}
	
}
