package com.zhcar.utils.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * @author YC
 * @time 2016-7-06 11:43
 */
/**
 * 
 * @author YC
 * @time 2016-7-6 上午11:44:07
 * TODO:Http请求（post/get两种方式）
 */
public class HttpUtils {

	private static final String TAG = "HttpUtils";
	
	public void post(String url, HttpCallback callback, HttpStatusCallback statusCallback)
	{
		new HttpThread(url, null, callback, statusCallback, 0, true).start();
	}
	
	public void postJson(String url, String jsonStr, HttpCallback callback, HttpStatusCallback statusCallback, int httpCode)
	{
		new HttpThread(url, jsonStr, callback, statusCallback, httpCode, true).start();
	}
	
	public void get(String url, HttpCallback callback, HttpStatusCallback statusCallback)
	{
		new HttpThread(url, null, callback, statusCallback, 0, false).start();
	}
	
	private class HttpThread extends Thread{
		private HttpCallback mCallback;
		private HttpStatusCallback mStatusCallback;
		private String mUrl;
		private boolean bPost;
		private String mJosnStr;
		private int mHttpCode;
		
		public HttpThread(String url, String jsonStr, HttpCallback callback, HttpStatusCallback statusCallback, int httpCode, boolean bPost) {
			mCallback = callback;
			mStatusCallback = statusCallback;
			mUrl = url;
			mJosnStr = jsonStr;
			this.bPost = bPost;
			mHttpCode = httpCode;
		}
		@Override
		public void run() {
			//Post方式
			if (bPost){
				try {
					//创建HttpClient
					HttpClient httpClient = new DefaultHttpClient();
					//创建HttpPost
					HttpPost httpPost = new HttpPost(mUrl);
					Log.i(TAG, "httpPost url = " + mUrl);
					if (mJosnStr != null){
						Log.i(TAG, "httpPost mJosnStr = " + mJosnStr);
						httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
						StringEntity stringEntity = new StringEntity(mJosnStr, HTTP.UTF_8);
						stringEntity.setContentType("text/json");
//						stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
						//设置传输参数
						httpPost.setEntity(stringEntity);	
					}
					else{
						//建立一个NameValuePair数组，用于存储欲传递的参数
						List<NameValuePair> param = new ArrayList<NameValuePair>();
						HttpEntity entity = new UrlEncodedFormEntity(param, "gb2312");
						//设置传输参数
						httpPost.setEntity(entity);	
					}
					
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
					else
					{
						if (mStatusCallback != null){
							mStatusCallback.onStatus(response.getStatusLine().getStatusCode(), mHttpCode);
						}
					}
					
				} catch (Exception e) {
					if (mStatusCallback != null){
						mStatusCallback.onStatus(HttpStatusCallback.RESULT_CONN_ERR, mHttpCode);
					}
					e.printStackTrace();
				}
			}
			else{
				try {
					//创建HttpClient
					HttpClient httpClient = new DefaultHttpClient();
					
					HttpGet httpGet = new HttpGet(mUrl);
					Log.i(TAG, "HttpGet url = " + mUrl);
					HttpResponse response = httpClient.execute(httpGet);
					HttpEntity httpEntity = response.getEntity();
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					{
//						String result = EntityUtils.toString(response.getEntity());
//					Log.i(tag, "response result = " + result);
//						if (mCallback != null)
//						{
//							mCallback.onReceive(result);
//						}
						InputStream inputStream = httpEntity.getContent();
			            BufferedReader reader = new BufferedReader(new InputStreamReader(
			                    inputStream));
			            String result = "";
			            String line = "";
			            while (null != (line = reader.readLine())){
			                result += line;
			            }
			            if (mCallback != null){
							mCallback.onReceive(result);
						}
					}
					
					/*URL url = new URL(mUrl);
					HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
					InputStreamReader reader = new InputStreamReader(conn.getInputStream());
					BufferedReader buffer = new BufferedReader(reader);
					String inputline;
					String result = "";
					while((inputline = buffer.readLine()) != null){
						result += inputline + "\r\n";
					}
					reader.close();
					conn.disconnect();
					if (mCallback != null){
						mCallback.onReceive(result);
					}*/
				} catch (Exception e) {
					if (mStatusCallback != null){
						mStatusCallback.onStatus(HttpStatusCallback.RESULT_CONN_ERR, mHttpCode);
					}
					e.printStackTrace();
				}
			}
		}
	}
}
