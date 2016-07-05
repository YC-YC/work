/**
 * 
 */
package com.zhonghong.leftitem;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


/**
 * @author YC
 * @time 2016-6-22 下午3:47:41
 * TODO:写文件记录信息的具体实现
 */
public class FileRecorder implements IRecorder{

	private static final String FILE_NAME = "leftiteminfo";
	private Context mContext;
	
	private static final String KEY_ITEM_COUNT = "key_item_count";
	private static final String KEY_ITEM = "key_item_";
	private static final String TAG = "FileRecorder";
	
	/**默认值*/
	private final List<Integer> defaultItems = new ArrayList<Integer>(){
		{
			clear();
			add(0);
			add(4);
			add(1);
		}
	};
	
	public FileRecorder(Context context) {
		super();
		this.mContext = context;
	}

	@Override
	public List<Integer> read() {
		List<Integer> itemInfo = new ArrayList<Integer>();
		SharedPreferences pref = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		int count = pref.getInt(KEY_ITEM_COUNT, defaultItems.size());
		for (int i = 0; i < count; i++){
			itemInfo.add(i, pref.getInt(KEY_ITEM+i, defaultItems.get(i)));
			Log.i(TAG, "get " + i + " = " + itemInfo.get(i));
		}
		return itemInfo;
	}

	@Override
	public void write(List<Integer> listInfo) {
		SharedPreferences pref = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		try {
			editor.clear();
			editor.putInt(KEY_ITEM_COUNT, listInfo.size());
			for (int i = 0; i < listInfo.size(); i++){
				editor.putInt(KEY_ITEM+i, listInfo.get(i));
				Log.i(TAG, "set " + i + " = " + listInfo.get(i));
			}
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}

