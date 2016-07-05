/**
 * 
 */
package com.zhonghong.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * @author YC
 * @time 2016-3-23 下午6:03:42
 */
public class HttpUtils {

	private static final String tag = "HttpUtils";
	
	public void post(String url, HttpCallback callback)
	{
		new HttpThread(url, callback).start();
	}
	
	private class HttpThread extends Thread{
		private HttpCallback mCallback;
		private String mUrl;
		
		public HttpThread(String url, HttpCallback callback) {
			mCallback = callback;
			mUrl = url;
		}
		@Override
		public void run() {
			try {
				//创建HttpClient
				HttpClient httpClient = new DefaultHttpClient();
				//创建HttpPost
				HttpPost httpPost = new HttpPost(mUrl);
				//建立一个NameValuePair数组，用于存储欲传递的参数
				List<NameValuePair> param = new ArrayList<NameValuePair>();
				HttpEntity entity = new UrlEncodedFormEntity(param, "gb2312");
				//设置传输参数
				httpPost.setEntity(entity);	
				//访问
				HttpResponse response = httpClient.execute(httpPost);
//				Log.i(tag, "response Code = " + response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				{
					String result = EntityUtils.toString(response.getEntity());
//					Log.i(tag, "response result = " + result);
					if (mCallback != null)
					{
						mCallback.onReceive(result);
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
