/**
 * 
 */
package com.zhonghong.recordinfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

	private static final String FILE_NAME = "logicinfo";
	private Context mContext;
	
	private static final String KEY_INFO = "info";
	private static final String TAG = "FileRecorder";
	
	
	public FileRecorder(Context context) {
		super();
		this.mContext = context;
	}

	@Override
	public RecordInfoBean read() {
		SharedPreferences pref = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		String result = pref.getString(KEY_INFO, "");
		RecordInfoBean recordInfoBean = new RecordInfoBean();
		try {
			recordInfoBean = deSerialization(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordInfoBean;
	}

	@Override
	public void write(RecordInfoBean cellInfo) {
		SharedPreferences pref = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		try {
			editor.clear();
			String info = serialize(cellInfo);
			editor.putString(KEY_INFO, info);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**序列化对象*/
	private String serialize(RecordInfoBean info) throws IOException {
		long startTime = System.currentTimeMillis();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteArrayOutputStream);
		objectOutputStream.writeObject(info);
		String serStr = byteArrayOutputStream.toString("ISO-8859-1");
		serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
		objectOutputStream.close();
		byteArrayOutputStream.close();
		Log.d(TAG, "serialize str =" + serStr);
		long endTime = System.currentTimeMillis();
		Log.d(TAG, "序列化耗时为:" + (endTime - startTime));
		return serStr;
	}

	/**反序列化对象*/
	private RecordInfoBean deSerialization(String str) throws IOException,
			ClassNotFoundException {
		long startTime = System.currentTimeMillis();
		String redStr = java.net.URLDecoder.decode(str, "UTF-8");
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				redStr.getBytes("ISO-8859-1"));
		ObjectInputStream objectInputStream = new ObjectInputStream(
				byteArrayInputStream);
		RecordInfoBean info = (RecordInfoBean) objectInputStream.readObject();
		objectInputStream.close();
		byteArrayInputStream.close();
		long endTime = System.currentTimeMillis();
		Log.d(TAG, "反序列化耗时为:" + (endTime - startTime));
		if (info == null){
			info = new RecordInfoBean();
		}
		return info;
	}


}

