/**
 * 
 */
package com.zhcar.apprecord;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.taimi.utils.SignatureGenerator;
import com.zhcar.data.AppUseRecord;
import com.zhcar.data.FlowInfoBean;
import com.zhcar.data.GlobalData;
import com.zhcar.utils.http.HttpCallback;
import com.zhcar.utils.http.HttpStatusCallback;
import com.zhcar.utils.http.HttpUtils;
import com.zhcar.utils.http.JsonHelper;

/**
 * @author YC
 * @time 2016-7-6 下午2:15:04
 * TODO:
 */
public class PostAppRecord implements IPostAppRecord , HttpCallback{

	private static final String POST_APPRECORD_URL = "http://cowindev.timanetwork.com/app-use-record/internal/recordIntel/addRecord?";

	private static final String TAG = "GetFlowLoc";
	
	private String mAppKey;
	private String mSign;
	private AppUseRecord recordInfo;
	
	private JsonHelper mJsonHelper;
	
	private HttpStatusCallback mHttpStatusCallback;
	
	
	public PostAppRecord(){
		mJsonHelper = new JsonHelper();
	}
	
	@Override
	public void SetInfo(AppUseRecord recordInfo) {
		this.mAppKey = GlobalData.AppKey;
		this.mSign = getSignStr();
		this.recordInfo = recordInfo;
	}

	private String getSignStr(){
		Map<String, String> params = new HashMap<String, String>();
        params.put("appkey", GlobalData.AppKey);
        String urlResourcePart = "app-use-record/internal/recordIntel/addRecord";
        String sign = null;
        try {
			sign = SignatureGenerator.generate(urlResourcePart, params, GlobalData.SecretKey);
//			Log.i(TAG, "generate signedStr = " + sign);
			
        } catch (Exception e) {
			e.printStackTrace();
		}
        return sign;
	}
	
	
	@Override
	public int Refresh() {
		HttpUtils http = new HttpUtils();
		String reqUrl = "";
			reqUrl = POST_APPRECORD_URL 
					+ "appkey=" + mAppKey
					+ "&sign=" + mSign;
		
		http.postJson(reqUrl, mJsonHelper.map2Json(recordInfo.toMap()), this, mHttpStatusCallback);
		return 0;
	}

	@Override
	public void SetHttpStatusCallback(HttpStatusCallback callback) {
		mHttpStatusCallback = callback;
	}


	@Override
	public void onReceive(String response) {
		HashMap<String, String> map = mJsonHelper.json2Map(response);
		if (map == null){
			if (mHttpStatusCallback != null){
				mHttpStatusCallback.onStatus(HttpStatusCallback.RESULT_FAILED);
			}
		}
		else {
			String status = map.get("status");
			if (status != null && "SUCCEED".equals(status)){
				if (mHttpStatusCallback != null){
					mHttpStatusCallback.onStatus(HttpStatusCallback.RESULT_SUCCESS);
				}
				
			}
			else{
				if (mHttpStatusCallback != null){
					mHttpStatusCallback.onStatus(HttpStatusCallback.RESULT_FAILED);
				}
			}
		}
	}

	
	
	
}
