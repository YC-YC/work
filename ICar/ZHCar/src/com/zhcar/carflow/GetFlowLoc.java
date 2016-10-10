/**
 * 
 */
package com.zhcar.carflow;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.taimi.utils.SignatureGenerator;
import com.zhcar.data.FlowInfoBean;
import com.zhcar.data.GlobalData;
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
public class GetFlowLoc implements GetFlowAbs , HttpCallback{

//	private static final String GET_FLOW_URL = "http://cowinmguat.timasync.com/mno-service/mnoService/getCardFlowInfoByHmi?";
	private static final String URLResourcePart = "mno-service/mnoService/getCardFlowInfoByHmi";
	private static final String TAG = "GetFlowLoc";
	
	private String mAppKey;
	private String mSign;
	private String mIccid;
	private String mToken;
	
	private JsonHelper mJsonHelper;
	
	private HttpStatusCallback mHttpStatusCallback;
	
	private FlowInfoBean mFlowInfoBean = new FlowInfoBean();
	
	public GetFlowLoc(){
		mJsonHelper = new JsonHelper();
	}
	
	@Override
	public void SetInfo(String iccid, String token) {
		mAppKey = Saver.getAppKey();
		mSign = getSignStr(iccid, token);
		mIccid = iccid;
		mToken = token;
	}

	private String getSignStr(String iccid, String token){
		Map<String, String> params = new HashMap<String, String>();
        params.put("iccid", iccid);
        params.put("token", token);
        params.put("appkey", Saver.getAppKey());
        String sign = null;
        try {
			sign = SignatureGenerator.generate(URLResourcePart, params, Saver.getSecretKey());
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
		if (Saver.isEnvironmentProduct()){
			reqUrl = GlobalData.URL_HOST_PROCDUCT;
		}
		else{
			reqUrl = GlobalData.URL_HOST_TEST;
		}
		reqUrl += (URLResourcePart + "?" 
				+ "appkey=" + mAppKey
				+ "&sign=" + mSign
				+ "&iccid=" + mIccid
				+ "&token=" + mToken);
		
		http.get(reqUrl, this, mHttpStatusCallback);
		return 0;
	}

	@Override
	public void SetHttpStatusCallback(HttpStatusCallback callback) {
		mHttpStatusCallback = callback;
	}

	@Override
	public FlowInfoBean GetFlowInfo() {
		return mFlowInfoBean;
	}

	@Override
	public void onReceive(String response) {
		HashMap<String, String> map = mJsonHelper.json2Map(response);
		if (map == null){
			if (mHttpStatusCallback != null){
				mHttpStatusCallback.onStatus(HttpStatusCallback.RESULT_FAILED, 0);
			}
		}
		else {
			String status = map.get("status");
			if (status != null && "SUCCEED".equals(status)){
				try {
					float total = Float.valueOf(map.get("currFlowTotal"));
					total = (float)(Math.round(total*100))/100;
					mFlowInfoBean.setCurrFlowTotal(String.valueOf(total));
					float used = Float.valueOf(map.get("useFlow"));
					used = (float)(Math.round(used*100))/100;
					mFlowInfoBean.setUseFlow(String.valueOf(used));
					mFlowInfoBean.setSurplusFlow(String.valueOf((float)Math.round((total - used)*100)/100));
					if (mHttpStatusCallback != null){
						mHttpStatusCallback.onStatus(HttpStatusCallback.RESULT_SUCCESS, 0);
					}
				} catch (Exception e) {
				}
//				mFlowInfoBean.setUseFlow(map.get("useFlow"));
//				mFlowInfoBean.setSurplusFlow(map.get("surplusFlow"));
//				mFlowInfoBean.setCurrFlowTotal(map.get("currFlowTotal"));
//				if (mHttpStatusCallback != null){
//					mHttpStatusCallback.onStatus(HttpStatusCallback.RESULT_SUCCESS, 0);
//				}
				
			}
			else{
				if (mHttpStatusCallback != null){
					mHttpStatusCallback.onStatus(HttpStatusCallback.RESULT_FAILED, 0);
				}
			}
		}
	}

	
	
	
}
