/**
 * 
 */
package com.zhcl.ui.music;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.media.SongManager;
import com.zhcl.service.service.client.AudioPlayerClient;
import com.zhonghong.zhmedia.R;

/**
 * @author ChenLi
 * 
 */
public class MusicPlayerFragment extends Fragment {
	private final String tag = "MusicPlayerFragment";
	Context context;
	ListView songList;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.music_player_fragment, container,
				false);
		context = inflater.getContext();
		initView(view);
		AudioPlayerClient.getInstance().init(context);
		return view;
	}

	void initView(View view) {
		songList = (ListView)view.findViewById(R.id.songList);
	}

	
	/**
	 * id3 信息先查系统数据库，如果系统数据库不存在则自己解析，如果中途改变了文件属性，在播放当前歌曲的时候校准
	 */
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() { 
		// TODO Auto-generated method stub
		super.onResume();
		reFresh();
		
		showAllSong();
	} 
	
	private void reFresh() {
		Utils.getInstance().startTime("更新所有id3");
		SongManager.getInstance().updataAllSongInfo();
		Utils.getInstance().endUseTime("更新所有id3");
	}
	
	
	private void showAllSong(){
		ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
		Cursor allSongCursor = SongManager.getInstance().queryAllSong();
		if(allSongCursor != null){
			int count = 0;
		while(allSongCursor.moveToNext()){
			HashMap<String, String> hashMap = new HashMap<String, String>();  
			//Song_table (id long not null, type integer not null, fid long not null, name text not null, singername text, albumname text, 
			String path = allSongCursor.getString(allSongCursor.getColumnIndex("path"));
			String title = allSongCursor.getString(allSongCursor.getColumnIndex("name"));
			String aristr = allSongCursor.getString(allSongCursor.getColumnIndex("singername"));
			String album = allSongCursor.getString(allSongCursor.getColumnIndex("albumname"));
			hashMap.put("title", (++count ) + "." + title.trim() + " - " + aristr.trim());
			hashMap.put("aristr", "歌手：" + aristr.trim());
			hashMap.put("album", "专辑：" + album.trim());
			hashMap.put("path", path.trim());
			data.add(hashMap);
		} 
	}
		
		SimpleAdapter listAdapter = new SimpleAdapter(context, data, R.layout.music_playerlist_item, 
				 new String[]{"title", "aristr", "album", "path"},  new int[]{R.id.songTitle, R.id.songSinger, R.id.songAlbum, R.id.songPath});
		songList.setAdapter(listAdapter);
		songList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String path = ((TextView)arg1.findViewById(R.id.songPath)).getText().toString();
				L.e(tag,"path = " + path);
				AudioPlayerClient.getInstance().openMedia(path);
				AudioPlayerClient.getInstance().start();
			}
		});
	}

}
