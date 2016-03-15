package com.example.playactivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.common.fileoperation.Mp3Information;
import com.example.lyrics.MacroDefination;
import com.example.mainactivity.MainActivity;
import com.example.mainactivity.SongListFragment;
import com.example.mainactivity.SongListFragment.Mp3Adapter.ViewHolder;
import com.example.mp3player01.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class PlayListFragment extends ListFragment implements MacroDefination {

	private ArrayList<Mp3Information> playList = new ArrayList<Mp3Information>(); // 播放列表
	private ArrayList<HashMap<String, Object>> mAdapterList = new ArrayList<HashMap<String, Object>>();
	private Mp3Adapter mAdapter;

	private PlayActivity mCallback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mCallback = (PlayActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_play_list, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	// 重置ListView，当播放开始时调用，用来清空选中的信息
	public void reset() {
		playList.clear();
	}

	public class Mp3Adapter extends BaseAdapter {

		public class ViewHolder {
			public TextView title;
			public TextView artist;
			public CheckBox checkbox;
		}

		private Context context = null;
		public HashMap<Integer, Boolean> isSelected;
		private ArrayList<HashMap<String, Object>> list = null;
		private LayoutInflater inflater = null;

		public Mp3Adapter(Context context, ArrayList<HashMap<String, Object>> list,
				int resource) {
			this.context = context;
			this.list = list;
			inflater = LayoutInflater.from(context);
			init();
		}

		// 初始化，所有的checkbox都设置为未选择
		private void init() {
			isSelected = new HashMap<Integer, Boolean>();
			for (int i = 0; i < list.size() + 1; i++) {
				isSelected.put(i, false);
			}
		}

		public void reset() {
			for (int i = 0; i < list.size() + 1; i++) {
				isSelected.put(i, false);
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return list.size() + 1;
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		

		public void setList(ArrayList<HashMap<String, Object>> list) {
			this.list = list;
		}
		

		@Override
		public View getView(int position, View view, ViewGroup arg2) {
			ViewHolder holder = null;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.row_song_list, null);
				holder.title = (TextView) view.findViewById(R.id.item_title);
				holder.artist = (TextView) view.findViewById(R.id.item_artist);
				holder.checkbox = (CheckBox) view
						.findViewById(R.id.item_checkbox);
				holder.checkbox
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								int id = buttonView.getId();
								// 每次getView()的时候检查isChecked的状态是否改变，如果改变了，则执行以下操作
								if (isSelected.get(id) != isChecked) {
									isSelected.put(id, isChecked);
									if (isChecked) {
										// playList.add(mp3List.get(id - 1));
										// multipleChoiceMode = true;
									} else {
										// playList.remove(mp3List.get(id - 1));
									}
									// mCallback.setPlayList(playList,
									// MainActivity.MULTIPLE_CHOICE);
								}
							}
						});
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			if (holder != null) {
				if (position == 0) {
					holder.checkbox.setVisibility(CheckBox.INVISIBLE);
					holder.title.setText("随机播放列表");
					holder.artist.setText("共有" + list.size() + "首歌曲");
				} else {
					holder.title.setText((String) list.get(position - 1).get(
							"mp3_title"));
					holder.artist.setText((String) list.get(position - 1).get(
							"mp3_artist"));

					holder.checkbox.setId(position);
					holder.checkbox.setVisibility(CheckBox.VISIBLE);
					holder.checkbox.setChecked(isSelected.get(position));
				}
			}
			return view;
		}
	}

	public void setPlayList(ArrayList<Mp3Information> list) {
		this.playList = list;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (position == 0) {
			if (playList == null | playList.size() == 0)
				return;
			mCallback.start(MODE_RANDOM);
		} else {
			mCallback.setCurrent(position - 1);
		}
	}

	public void refresh() {
		mAdapterList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < playList.size(); i++) {
			Mp3Information mp3 = playList.get(i);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("mp3_title", mp3.getTitle());
			map.put("mp3_artist", mp3.getArtist());
			mAdapterList.add(map);
		}
		mAdapter = new Mp3Adapter((Activity) getActivity(), mAdapterList,
				R.layout.row_song_list);
		setListAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
}
