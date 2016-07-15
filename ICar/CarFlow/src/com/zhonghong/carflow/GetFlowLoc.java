/**
 * 
 */
package com.zhonghong.carflow;

import java.util.HashMap;

import com.zhonghong.http.HttpCallback;
import com.zhonghong.http.HttpStatusCallback;
import com.zhonghong.http.HttpUtils;
import com.zhonghong.utils.JsonHelper;

/**
 * @author YC
 * @time 2016-7-6 下午2:15:04
 * TODO:
 */
public class GetFlowLoc implements GetFlowAbs , HttpCallback{

	private static final String GET_FLOW_URL = "/mno-service/mnoService/getCardFlowInfoByHmi?";
	
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
	public void SetInfo(String appKey, String sign, String iccid, String token) {
		mAppKey = appKey;
		mSign = sign;
		mIccid = iccid;
		mToken = token;
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
		HashMap<String, String> map = mJsonHelper.parserToMap(response);
		if (map == null){
			if (mHttpStatusCallback != null){
				mHttpStatusCallback.onStatus(HttpStatusCallback.RESULT_FAILED);
			}
		}
		else {
			String status = map.get("status");
			if (status != null && "SUCCEED".equals(status)){
				mFlowInfoBean.setUseFlow(Float.valueOf(map.get("useFlow")));
				mFlowInfoBean.setSurplusFlow(Float.valueOf(map.get("surplusFlow")));
				mFlowInfoBean.setCurrFlowTotal(Float.valueOf(map.get("currFlowTotal")));
				
				if (mHttpStatusCallback != null){
					mHttpStatusCallback.onStatus(HttpStatusCallback.RESULT_SUCCESS);
				}
			}
		}
	}

	
	
	
}
