/**
 * 
 */
package com.zh.uitls;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 助理音乐的网络资源，包括歌词以及专辑图片，设计在后台单线程中处理下载
 * 
 * @author zhonghong.chenli
 * @date 2015-12-19 上午2:59:57
 */
public class NetResource {
	private static final String tag = NetResource.class.getSimpleName();
	public class LrcNet {
		private String songName;
		private String url;
		private String albumID;

		public LrcNet(String songName, String url, String albumID) {
			super();
			this.songName = songName;
			this.url = url;
			this.albumID = albumID;
		}

		public String getSongName() {
			return songName;
		}

		public String getUrl() {
			return url;
		}

		public String getAlbumID() {
			return albumID;
		}

	}

	/**
	 * 获得歌词url
	 * @throws Exception 
	 */
	public LrcNet[] getLrcURL(String songName, String singerName) throws Exception {
		String url = "http://geci.me/api/lyric";
		String result = null;
		do{
			if(songName == null){
				L.w(tag, "songName = null");
				return null;
			}
			url += "/" + songName.replace(" ", "%20");
			if(singerName != null){
				url += "/" + singerName.replace(" ", "%20");
			}
			L.e(tag, "URL : " + url);
			result = androidHttpClinetGetRequest(url);
//			HashMap<String, String> map = new HashMap<String, String>();
//			map.put("title", songName);
//			map.put("singer.name", singerName);
//			result = androidHttpClinePostRequest("http://www.wearefamily.link/carapi/api-json/requestlrc", map);
			
		}while(false);
		JSONObject  dataJson = new JSONObject(result);
		int count = dataJson.getInt("count");
		L.e(tag, "歌词count :" + count);
		if(count == 0){
			songName.replace("（", "(").replace("\"", "(").replace("“", "(");
			if(songName.contains("(")){			//开可以抽出模块拓展一下规则
				L.e(tag, "((((((((((((((");
				int indexC = songName.indexOf("(");
				if(indexC != 0){
					songName = songName.substring(0, indexC);
				}else{
					return null;
				}
				return getLrcURL(songName, singerName);
			}
			if(singerName != null){
				return getLrcURL(songName, null);
			}
			return null;
		}
		JSONArray array = dataJson.getJSONArray("result");
		LrcNet[] lrcResult = new LrcNet[array.length()];
		for(int i = 0; i < array.length(); i++){
			JSONObject item = (JSONObject) array.get(i);
			lrcResult[i] = new LrcNet(item.getString("song"), item.getString("lrc"), item.getString("aid"));
			L.i(tag, "index = " + i);
			L.i(tag, "歌曲id：" + item.getString("sid"));
			L.i(tag, "专辑id：" + item.getString("aid"));
			L.i(tag, "歌手id：" + item.getString("artist_id"));
			L.i(tag, "歌词链接：" + item.getString("lrc"));
			L.i(tag, "歌曲名：" + item.getString("song"));
		}
		return lrcResult;
	}
	
	
	private String androidHttpClinetGetRequest(String url) {
		String result = null;
		// 生成请求对象
		HttpGet httpGet = new HttpGet(url);
		HttpClient httpClient = new DefaultHttpClient();

		// 发送请求
		try {

			HttpResponse response = httpClient.execute(httpGet);
			// 请求成功
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 取得返回的字符串
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * android http client post
	 */
	private String androidHttpClinePostRequest(String url, HashMap<String, String> params){
		String result = null;
		 try
         {
			 List<NameValuePair> nvps = null;
			if(params != null){
				// 创建参数列表
				nvps = new ArrayList<NameValuePair>();
				Set<String> keySet = params.keySet();
				for (String key : keySet) {
					nvps.add(new BasicNameValuePair(key, params.get(key)));
				}
				// url格式编码
			}
        	 HttpEntity requestHttpEntity = new UrlEncodedFormEntity(nvps, "UTF-8");
             // URL使用基本URL即可，其中不需要加参数
             HttpPost httpPost = new HttpPost(url);
             // 将请求体内容加入请求中
             httpPost.setEntity(requestHttpEntity);
             // 需要客户端对象来发送请求
             HttpClient httpClient = new DefaultHttpClient();
             // 发送请求
             HttpResponse response = httpClient.execute(httpPost);
         	 // 请求成功
 			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
 				// 取得返回的字符串
 				result = EntityUtils.toString(response.getEntity(), "UTF-8");
 			}
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }
		 return result;
	}
	
	/**
	 * 图片下载测试
	 * @param args
	 */
	public static void main(String[] args) {
		String url = "http://s.geci.me/album-cover/157/1573814.jpg";
		byte[] btImg = getDataFromNetByUrl(url);
		if(null != btImg && btImg.length > 0){
			System.out.println("读取到：" + btImg.length + " 字节");
			String fileName = "D:\\beyond。jpg";
			writeImageToDisk(btImg, fileName);
		}else{
			System.out.println("没有从该连接获得内容");
		}
	}
	
	/**
	 * 网络读取资源并写入到文件
	 * @return
	 */
	public boolean netReadWriteFile(String url, String filePath){
		byte[] btImg = getDataFromNetByUrl(url);
		if(null != btImg && btImg.length > 0){
			L.e(tag, "读取到：" + btImg.length + " 字节");
			writeImageToDisk(btImg, filePath);
			return true;
		}
		L.e(tag, "没有从该连接获得内容");
		return false;
	}
	/**
	 * 将图片写入到磁盘
	 * @param img 图片数据流
	 * @param fileName 文件保存时的名称
	 */
	public static void writeImageToDisk(byte[] img, String fileName){
		try {
			File file = new File(fileName);
			FileOutputStream fops = new FileOutputStream(file);
			fops.write(img);
			fops.flush();
			fops.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据地址获得数据的字节流
	 * @param strUrl 网络连接地址
	 * @return
	 */
	public static byte[] getDataFromNetByUrl(String strUrl){
		try {
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
			byte[] btImg = readInputStream(inStream);//得到图片的二进制数据
			return btImg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 从输入流中获取数据
	 * @param inStream 输入流
	 * @return
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream inStream) throws Exception{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while( (len=inStream.read(buffer)) != -1){
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
}
