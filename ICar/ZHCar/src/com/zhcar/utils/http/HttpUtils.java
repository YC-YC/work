package com.zhcar.utils.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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
		new HttpThread(url, callback, statusCallback, true).start();
	}
	
	public void get(String url, HttpCallback callback, HttpStatusCallback statusCallback)
	{
		new HttpThread(url, callback, statusCallback, false).start();
	}
	
	private class HttpThread extends Thread{
		private HttpCallback mCallback;
		private HttpStatusCallback mStatusCallback;
		private String mUrl;
		private boolean bPost;
		
		public HttpThread(String url, HttpCallback callback, HttpStatusCallback statusCallback, boolean bPost) {
			mCallback = callback;
			mStatusCallback = statusCallback;
			mUrl = url;
			this.bPost = bPost;
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
					else
					{
						if (mStatusCallback != null){
							mStatusCallback.onStatus(response.getStatusLine().getStatusCode());
						}
					}
					
				} catch (Exception e) {
					if (mStatusCallback != null){
						mStatusCallback.onStatus(HttpStatusCallback.RESULT_CONN_ERR);
					}
					e.printStackTrace();
				}
			}
			else{
				try {
					//创建HttpClient
					HttpClient httpClient = new DefaultHttpClient();
					
					HttpGet httpGet = new HttpGet(mUrl);
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
						mStatusCallback.onStatus(HttpStatusCallback.RESULT_CONN_ERR);
					}
					e.printStackTrace();
				}
			}
		}
	}
}
