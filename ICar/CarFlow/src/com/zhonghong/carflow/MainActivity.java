package com.zhonghong.carflow;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.zhonghong.conn.ConnZui.InitListener;
import com.zhonghong.conn.ZHRequest;
import com.zhonghong.jarmain.ZHCar;
import com.zhonghong.log.L;
import com.zhonghong.utils.JsonHelper;

public class MainActivity extends Activity {

	private static final String TAG = "JsonHelper";
	HashMap<String, String> data = new HashMap<String, String>();
	private JsonHelper jsonHelper;
	private String jsonStr = "{"
			+ "info:{"
			+ "\"name\":	null," 
			+ "\"id\":	0,"
			+ "\"remindValue\":	350,"
			+ "\"vin\":	null,"
			+ "\"mobile\":	null,"
			+ "\"phoneNum\":	\"18918978962\","
			+ "\"remind\":	false,"
			+ "\"useFlow\":	222,"
			+ "\"surplusFlow\":	678,"
			+ "\"currFlowTotal\":	900"
			+ "},"
			+ "status:	\"SUCCEED\""
			+ "}";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		jsonHelper = new JsonHelper();
		ZHCar.getInstance().init(this, mInitListener);	
	}


	public void doClick(View view){
		switch (view.getId()) {
		case R.id.test_parsejson:
			HashMap<String, String> map = jsonHelper.parserToMap(jsonStr);
			break;

		default:
			break;
		}
	}
	
	InitListener mInitListener = new InitListener() {
		@Override
		public void state(int state, String content) {
			switch(state){
			case InitListener.STATE_SUCCESS:
				//测试发送， 必须在初始化成功后才能发送，其实就是简单的一个aidl连接状态
				jsonStr = GetMCUVersion();
				 L.i(TAG, "获取MCU信息：" + jsonStr);
				break;
			case InitListener.STATE_FAIL:
				break;
			case InitListener.STATE_RECONN:
				break;
			} 
		}
	}; 
	
	
	public String GetMCUVersion(){
		data.clear();
		data.put("req", "version");
		return ZHRequest.getInstance().httpPostFormAidl("/setup", data);
	}
}
