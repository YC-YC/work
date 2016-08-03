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

	private static final String GET_FLOW_URL = "http://cowinmguat.timasync.com/mno-service/mnoService/getCardFlowInfoByHmi?";

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
		mAppKey = GlobalData.AppKey;
		mSign = getSignStr(iccid, token);
		mIccid = iccid;
		mToken = token;
	}

	private String getSignStr(String iccid, String token){
		Map<String, String> params = new HashMap<String, String>();
        params.put("iccid", iccid);
        params.put("token", token);
        params.put("appkey", GlobalData.AppKey);
        String urlResourcePart = "mno-service/mnoService/getCardFlowInfoByHmi";
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
			reqUrl = GET_FLOW_URL 
					+ "appkey=" + mAppKey
					+ "&sign=" + mSign
					+ "&iccid=" + mIccid
					+ "&token=" + mToken;
		
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
				mHttpStatusCallback.onStatus(HttpStatusCallback.RESULT_FAILED);
			}
		}
		else {
			String status = map.get("status");
			if (status != null && "SUCCEED".equals(status)){
				mFlowInfoBean.setUseFlow(map.get("useFlow"));
				mFlowInfoBean.setSurplusFlow(map.get("surplusFlow"));
				mFlowInfoBean.setCurrFlowTotal(map.get("currFlowTotal"));
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
