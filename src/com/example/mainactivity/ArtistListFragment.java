package com.example.mainactivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.common.fileoperation.Mp3Information;
import com.example.lyrics.MacroDefination;
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
import android.widget.ListView;
import android.widget.TextView;

public class ArtistListFragment extends ListFragment implements MacroDefination {

	private ArtistListAdapter mListAdapter;
	private MainActivity      mCallback;
	
	private HashMap<String, ArrayList<Mp3Information>> mArtistMap;
	private List<String> mArtistList = new ArrayList<String>();
	
	public ArtistListFragment(List<String> artistList, HashMap<String, ArrayList<Mp3Information>> artistMap) {
		this.mArtistMap = artistMap;
		this.mArtistList = artistList;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback = (MainActivity) activity;
		//设置Adapter
		mListAdapter = new ArtistListAdapter(getActivity(), mArtistList);
		setListAdapter(mListAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_artist_list, container,
				false);
	}
	
	private class ArtistListAdapter extends BaseAdapter {

		private List<String> artists;
		private Context context;
		private LayoutInflater inflater = null;
		
		public class ViewHolder {
			public TextView artist;
			public TextView songNum;
		}
		
		public ArtistListAdapter (Context context, List<String> artists) {
			this.context = context;
			this.artists = artists;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return artists.size();
		}

		@Override
		public Object getItem(int arg0) {
			return artists.get(arg0);
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
				view = inflater.inflate(R.layout.artist_list_row, null);
				holder.artist = (TextView) view.findViewById(R.id.artist_list_artist);
				holder.songNum = (TextView) view.findViewById(R.id.artist_list_content);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			String key = mArtistList.get(position);
			ArrayList<Mp3Information> songs = mArtistMap.get(key);
			holder.artist.setText(key);
			holder.songNum.setText("共有" + songs.size() + "首歌曲");
			
			return view;
		}
		
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String artist = mArtistList.get(position);
		
		ArrayList<Mp3Information> list = mArtistMap.get(artist);
		mCallback.setPlayList(list, SINGLE_CHOICE);
		mCallback.start(MODE_RANDOM);
		Intent intent = new Intent(getActivity(), PlayActivity.class);
		startActivity(intent);
	}

}
