/**
 * 
 */
package com.zhcar.apprecord;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.database.Cursor;
import android.text.TextUtils;

import com.taimi.utils.SignatureGenerator;
import com.zhcar.data.AppUseRecord;
import com.zhcar.data.GlobalData;
import com.zhcar.provider.CarProviderData;
import com.zhcar.utils.Saver;
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

	/**生产环境*/
	private static final String APPRECORD_URL_PROCDUCT = "http://cowindev.timanetwork.com/app-use-record/internal/recordIntel/addRecord?";
	/**测试环境*/
	private static final String APPRECORD_URL_TEST = "http://cowinmguat.timasync.com/app-use-record/internal/recordIntel/addRecord?";
	private static final String TAG = "PostAppRecord";
	
	private String mAppKey;
	private String mSign;
	private AppUseRecord recordInfo;
	
	private JsonHelper mJsonHelper;
	
	private HttpStatusCallback mHttpStatusCallback;
	
	private String VIN;
	private String TOKEN;
	
	public PostAppRecord(){
		mJsonHelper = new JsonHelper();
	}
	
	@Override
	public void SetInfo(AppUseRecord recordInfo) {
		queryCarinfo();
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
        } catch (Exception e) {
			e.printStackTrace();
		}
        return sign;
	}
	
	
	@Override
	public int Refresh() {
		HttpUtils http = new HttpUtils();
		String reqUrl = "";
		if (Saver.isEnvironmentProduct()){
			reqUrl = APPRECORD_URL_PROCDUCT;
		}
		else{
			reqUrl = APPRECORD_URL_TEST;
		}
			reqUrl += ("appkey=" + mAppKey
					+ "&sign=" + mSign);
		
		http.postJson(reqUrl, mJsonHelper.map2Json(recordInfo.toMap()), this, mHttpStatusCallback, recordInfo.hashCode());
		return 0;
	}

	@Override
	public void SetHttpStatusCallback(HttpStatusCallback callback) {
		mHttpStatusCallback = callback;
	}

	@Override
	public void onReceive(String response) {
		HashMap<String, String> map = mJsonHelper.json2Map(response);
		if (map == null) {
			if (mHttpStatusCallback != null) {
				mHttpStatusCallback.onStatus(HttpStatusCallback.RESULT_FAILED,
						recordInfo.hashCode());
			}
		} else {
			String status = map.get("status");
			if (!TextUtils.isEmpty(status) && "SUCCEED".equals(status)) {
				if (mHttpStatusCallback != null) {
					mHttpStatusCallback.onStatus(
							HttpStatusCallback.RESULT_SUCCESS,
							recordInfo.hashCode());
				}

			} else {
				if (mHttpStatusCallback != null) {
					mHttpStatusCallback.onStatus(
							HttpStatusCallback.RESULT_FAILED,
							recordInfo.hashCode());
				}
			}
		}
	}

	private void queryCarinfo() {
		ContentResolver resolver = GlobalData.mContext.getContentResolver();
		Cursor cursor = resolver.query(CarProviderData.URI_CARINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			VIN = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_VIN));
			TOKEN = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_TOKEN));
			}
		if (cursor != null){
			cursor.close();
			cursor = null;
		}
	}
	
	
}
