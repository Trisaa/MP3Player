package com.example.playactivity;

import com.example.common.fileoperation.Mp3Information;
import com.example.lyrics.ImageOperation;
import com.example.lyrics.MarqueeTextView;
import com.example.mainactivity.MainActivity;
import com.example.mp3player01.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DetailPlayFragment extends Fragment {
	
	private View view;
	private PlayActivity mCallback;
	
	private MarqueeTextView textTitle;
	private TextView textArtist;
	private TextView textAlbum;
	private ImageView imgAlbum;
	private Bitmap bmAlbum;
	
	private Mp3Information mp3;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_detail_play, container, false);
		init(view);
		return view;
	}

	private void init(View view) {
		textTitle = (MarqueeTextView) view.findViewById(R.id.detail_song_title);
		textTitle.startScroll();
		textArtist = (TextView) view.findViewById(R.id.detail_song_artist);
		textAlbum = (TextView) view.findViewById(R.id.detail_song_album);
		imgAlbum = (ImageView) view.findViewById(R.id.detail_album_image);
	}
	
	public void setMp3Information(Mp3Information mp3) {
		this.mp3 = mp3;
		textTitle.setMarqueeText(mp3.getTitle());
		textAlbum.setText(mp3.getAlbum());
		textArtist.setText(mp3.getArtist());

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mp3.getImgPath(), opts);
		
		opts.inSampleSize = ImageOperation.computeSampleSize(opts, -1, 256*256);
		opts.inJustDecodeBounds = false;
		bmAlbum = BitmapFactory.decodeFile(mp3.getImgPath(), opts);
		imgAlbum.setImageBitmap(bmAlbum);
	}
}
