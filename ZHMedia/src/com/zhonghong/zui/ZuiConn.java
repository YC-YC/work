/**
 * 
 */
package com.zhonghong.zui;

import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;

import com.zhcl.filescanner.L;
import com.zhonghong.conn.ConnZui;
import com.zhonghong.conn.ZHRequest;
import com.zhonghong.conn.ConnZui.InitListener;

/**
 * zui通信
 * @author chen
 */
public class ZuiConn implements INotifyLife{ 
	private static final String tag = ZuiConn.class.getSimpleName();
	private ZuiConn(){}
	private static ZuiConn mZuiConn;
	private static final int ZUICONN_ENTER_PAGE = 0x01;
	private static final int ZUICONN_EXIT_PAGE = 0x02;
	private static final int ZUICONN_DESTROY = 0x03;
	/** 是否初始化完成 */ 
	private boolean isInitOK = false;
	Context context;
	Vector<Integer> cmdCache = new Vector<Integer>();
	/**
	 * 消息map
	 */
	HashMap<String, String> data = new HashMap<String, String>();
	public static ZuiConn getInstance(){
		if(mZuiConn == null){
			mZuiConn = new ZuiConn();
		}
		return mZuiConn;
	}
	
	public void init(Context context){
		this.context = context;
		ConnZui.getInstance().init(context, mInitListener); // 初始化与zui通信的对象
	}
	
	/**
	 * 与zui通讯，服务状态监听
	 */
	private InitListener mInitListener = new InitListener() {
		@Override
		public void state(int state, String content) {
			switch (state) {
			case InitListener.STATE_SUCCESS: // 收到初始化成功才可执行控制命令（如，挂断电话）
				isInitOK = true;
				notifyCache();
				break;
			}
		}
	};
	
	
	@Override
	public void enterPage() {
		requestSend(ZUICONN_ENTER_PAGE);
	}
	
	@Override
	public void exitPage() {
		requestSend(ZUICONN_EXIT_PAGE);
	}
	
	@Override
	public void destroy() {
		requestSend(ZUICONN_DESTROY);
	}
	
	/**
	 * 通知缓存命令
	 */
	private void notifyCache(){
		synchronized (cmdCache) {
			for(int i = 0; i < cmdCache.size(); i++){
				functionCmd(cmdCache.get(i));
			}
			cmdCache.clear();
		} 
	}
	
	/**
	 * 具体请求执行
	 * @param cmd
	 */
	private void functionCmd(int cmd){
		switch(cmd){
		case ZUICONN_ENTER_PAGE:
			L.e(tag, "ZUICONN_ENTER_PAGE");
			simpleRequest("entersourcetomcu", "audio");
			break;
		case ZUICONN_EXIT_PAGE:
			L.e(tag, "ZUICONN_EXIT_PAGE");
			break;
		case ZUICONN_DESTROY:
			L.e(tag, "ZUICONN_DESTROY");
			break;
		}
	}
	
	
	/**
	 * 是否允许直接发送,如果允许则，直接发送，否则加入队列
	 */
	private boolean requestSend(Integer cmd){
		if(cmdCache.isEmpty() && isInitOK){
			functionCmd(cmd);
			return true;
		}
		synchronized (cmdCache) {
			cmdCache.add(cmd);
		}
		return false;
	}
	
	/**
	 * 蓝牙音乐播放
	 */
	private void simpleRequest(String req, String value){
		data.clear();
		data.put("req", req);
		data.put("val", value);
		ZHRequest.getInstance().httpPostFormAidl("/system", data);
	}
}
