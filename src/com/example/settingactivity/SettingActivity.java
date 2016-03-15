package com.example.settingactivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.mp3player01.R;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity {
	
	private ActionBar mActionBar;
	private ListView mListView;
	private TextView mVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		//设置左上角图标为返回上级
		mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		mVersion = (TextView) findViewById(R.id.setting_version);
		mVersion.setText("Player");
		mListView = (ListView) findViewById(R.id.setting_list);
		
		initListView();
	}
	
	private void initListView() {

		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> item1 = new HashMap<String, Object>();
		item1.put("Title", "检查更新");
		HashMap<String, Object> item2 = new HashMap<String, Object>();
		item2.put("Title", "联系我们");
		list.add(item1);
		list.add(item2);
		
		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.row_setting_list, 
				new String[] {"Title"}, new int[] {R.id.setting_list_title});
		
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				switch (position) {
				case 0:
					Toast.makeText(SettingActivity.this, "已是最新版本", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(SettingActivity.this, "功能建设中", Toast.LENGTH_SHORT).show();
					break;
				}
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
}
