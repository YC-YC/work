/**
 * 
 */
package com.zhcar.utils.http;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

import android.util.Log;

/**
 * @author YC
 * @time 2016-7-6 下午3:27:25 
 * TODO:解释JSON
 */
public class JsonHelper {

	private static final String TAG = "JsonHelper";

	private String cacheJsonStr = "";

	private HashMap<String, String> resultMap = new HashMap<String, String>();

	/** 将JSON字符串解释成Map */
	public HashMap<String, String> parserToMap(String jsonStr) {
		if (jsonStr == null || "".equals(jsonStr.trim())) {
			Log.i(TAG, "数据为空");
			return null;
		}

		if (jsonStr.equals(cacheJsonStr)) {
			Log.i(TAG, "与上一次相同");
			return resultMap;
		}

		cacheJsonStr = jsonStr;
		resultMap.clear();
		Log.i(TAG, "Get jsonStr = " + jsonStr);
		
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			//得到Key值的指示器
			Iterator<String> iterator = jsonObject.keys();
			while (iterator.hasNext()) {
				String objKey = iterator.next();
//				Log.i(TAG, "Get result obj =" + obj);
				String key = "";
				String val = "";
				Object objVal = jsonObject.get(objKey);
				//直接val为JSON
				if (objVal instanceof JSONObject){
					JSONObject newObj = (JSONObject) objVal;
					Iterator<String> it = newObj.keys();
					while(it.hasNext()){
						key = it.next();
						val = newObj.getString(key);
//						Log.i(TAG, "Get result key =[" + key + "], val = [" + val + "]");
						resultMap.put(key, val);
					}
				}
				//直接Key-val
				else if (objVal instanceof String) {
					key = objKey;
					val = (String) objVal;
//					Log.i(TAG, "Get result key =[" + key + "], val = [" + val + "]");
					resultMap.put(key, val);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*try {
			JSONTokener parser = new JSONTokener(jsonStr);
			Object temp = parser.nextValue();
			if (temp == null || !(temp instanceof JSONObject)){
				Log.i(TAG, "json nextValue error ");
				return null;
			}
			
			JSONObject msgs = (JSONObject) temp;
			@SuppressWarnings("unchecked")
			Iterator<String> it1 = msgs.keys();
			while(it1.hasNext()){
				String obj = it1.next();
				Log.i(TAG, "Get result obj =" + obj);
				String key = "";
				String val = "";
				Object temp1 = msgs.get(obj);
				if (temp1 instanceof JSONObject){
					JSONObject newObj = (JSONObject) temp1;
					Iterator<String> it = newObj.keys();
					while(it.hasNext()){
						key = it.next();
						val = newObj.getString(key);
						Log.i(TAG, "Get result key =[" + key + "], val = [" + val + "]");
						resultMap.put(key, val);
					}
				}else if (temp1 instanceof String) {
					key = obj;
					val = (String) temp1;
					Log.i(TAG, "Get result key =[" + key + "], val = [" + val + "]");
					resultMap.put(key, val);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
*/
		
		Log.i(TAG, "Get result = " + resultMap.toString());
		return resultMap;
	}
}
