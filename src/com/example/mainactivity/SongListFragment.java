package com.example.mainactivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.common.fileoperation.Mp3Information;
import com.example.mp3player01.R;
import com.example.playactivity.PlayActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

public class SongListFragment extends ListFragment {
	
	private MainActivity mCallback;

	// ListView�ؼ���ListViewAdapter
	private Mp3Adapter adapter;

	private ArrayList<Mp3Information> mp3List = new ArrayList<Mp3Information>(); // ������Ϣ�б�
	private ArrayList<Mp3Information> playList = new ArrayList<Mp3Information>(); // �����б�
	private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	private boolean multipleChoiceMode = false;
	
	public SongListFragment(ArrayList<Mp3Information> mp3List) {
		this.mp3List = mp3List;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback = (MainActivity) activity;
		for (int i = 0; i < mp3List.size(); i++) {
			Mp3Information mp3 = mp3List.get(i);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("mp3_title", mp3.getTitle());
			map.put("mp3_artist", mp3.getArtist());
			map.put("mp3_album", mp3.getAlbum());
			map.put("checkbox", false);
			list.add(map);
		}
		// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
		adapter = new Mp3Adapter(getActivity(), list,
				R.layout.row_song_list);
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_song_list, container, false);
	}


	// ����ListView�������ſ�ʼʱ���ã��������ѡ�е���Ϣ
	public void reset() {
		playList.clear();
		multipleChoiceMode = false;
		adapter.reset();
	}
	
	public class Mp3Adapter extends BaseAdapter {

		public class ViewHolder {
			public TextView title;
			public TextView artist;
			public CheckBox checkbox;
		}

		private Context context = null;
		public HashMap<Integer, Boolean> isSelected;
		private List<HashMap<String, Object>> list = null;
		private LayoutInflater inflater = null;

		public Mp3Adapter(Context context, List<HashMap<String, Object>> list,
				int resource) {
			this.context = context;
			this.list = list;
			inflater = LayoutInflater.from(context);
			init();
		}

		// ��ʼ�������е�checkbox������Ϊδѡ��
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
							CompoundButton buttonView,
							boolean isChecked) {
						int id = buttonView.getId();
						// ÿ��getView()��ʱ����isChecked��״̬�Ƿ�ı䣬����ı��ˣ���ִ�����²���
						if (isSelected.get(id) != isChecked) {
							isSelected.put(id, isChecked);
							if (isChecked) {
								playList.add(mp3List.get(id - 1));
								multipleChoiceMode = true;
							} else {
								playList.remove(mp3List.get(id - 1));
							}
							mCallback.setPlayList(playList, MainActivity.MULTIPLE_CHOICE);
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
					holder.title.setText("�������ȫ������");
					holder.artist.setText("����" + list.size() + "�׸���");
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

	@Override
	public void onListItemClick(ListView l, View view, int position, long id) {
		if (position == 0) {
			playList = new ArrayList<Mp3Information>(mp3List);
			mCallback.setPlayList(playList, MainActivity.SINGLE_CHOICE);
			mCallback.start(MainActivity.MODE_RANDOM);
			reset();
		}
		else {
			Mp3Adapter.ViewHolder holder = (Mp3Adapter.ViewHolder) view.getTag();
			CheckBox cb = holder.checkbox;
			
			if (multipleChoiceMode == true) {
				cb.toggle();
			} else {
				playList = new ArrayList<Mp3Information>();
				playList.add(mp3List.get(position - 1));
				mCallback.setPlayList(playList, MainActivity.SINGLE_CHOICE);
				mCallback.start(MainActivity.MODE_RANDOM);
				reset();
			}
		}
		Intent intent = new Intent(getActivity(), PlayActivity.class);
		startActivity(intent);
	}
	
}