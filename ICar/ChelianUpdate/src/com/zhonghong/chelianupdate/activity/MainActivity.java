package com.zhonghong.chelianupdate.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zhonghong.chelianupdate.R;
import com.zhonghong.chelianupdate.adapter.DownloadListAdapter;
import com.zhonghong.chelianupdate.base.GlobalData;
import com.zhonghong.chelianupdate.bean.CarInfo;
import com.zhonghong.chelianupdate.bean.GroupVersionVo;
import com.zhonghong.chelianupdate.bean.UpdateStatusInfo;
import com.zhonghong.chelianupdate.bean.UpdateVo;
import com.zhonghong.chelianupdate.service.DownloadService;
import com.zhonghong.chelianupdate.utils.DownloadManager;
import com.zhonghong.chelianupdate.utils.InfoUtils;
import com.zhonghong.chelianupdate.utils.JSONParser;
import com.zhonghong.chelianupdate.utils.Saver;
import com.zhonghong.chelianupdate.utils.SignatureGenerator;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhonghong.sdk.android.ZHAppSdk;
import com.zhonghong.sdk.android.utils.AppManager;

import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends Activity {

	private static final String URL_TEST="https://admdownload.adobe.com/bin/live/flashplayer22_ha_install.exe";
	private static final String UPGRADE_URL = "http://cowinmguat.timasync.com/update-center/getUpdateInfoByVin?appkey=9909756346&sign=0f248f0eb2dfb85e56d31453a3837af7&vin=LVTDB11B1TS000004&token=YlFJbTFzdld5US93bUVhY1F0L1JsVU16Q29QQTdrUEVKcm1hK25EZysrVjljaDc3bEVTZXg4Nmlac1ZMelRaN1pLekRiMzJqUjMzNwp3VFFURnhXZHBnPT0-___1469610441385___LVTDB11B1TS000004___CL6230004TS0___CL6230004TS0___5";
	
	private static final String DIR_FILE_DOWNLOAD="/mnt/sdcard/download";
	private final String TAG="MainActivity";
	private static final String URL_SOURCE_PART="update-center/getUpdateInfoByVin";
	private static final String URL_HOST="http://cowinmguat.timasync.com/";
	
	
	@ViewInject(R.id.lv_download_list)
	private ListView lvDownloadList;
	@ViewInject(R.id.edt_url)
	private EditText edtUrl;
	@ViewInject(R.id.btn_download)
	private Button btnDownload;
	private DownloadManager downloadManager;
	private DownloadListAdapter downloadListAdapter;

	private String sign="";
	private GroupVersionVo groupVersionVo;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();
		ZHAppSdk.initSDK(context);
		ViewUtils.inject(this);

		edtUrl.setText(UPGRADE_URL);

		downloadManager = DownloadService.getDownloadManager(context);
		downloadListAdapter = new DownloadListAdapter(context, downloadManager);
		lvDownloadList.setAdapter(downloadListAdapter);

		btnDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				download(URL_TEST, "flashplayer22_ha_install.exe");
			}
		});
		/*btnDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CarInfo info = InfoUtils.queryCarinfo();
				Map<String, String> params = new HashMap<String, String>();
				params.put("appkey", GlobalData.AppKey);
				params.put("vin", info.getVin());
				params.put("token", info.getToken());
				try {
					sign = SignatureGenerator.generate(URL_SOURCE_PART, params,
							GlobalData.SecretKey);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (sign != null) {
					RequestParams requestParams = new RequestParams();
					params.put("appkey", GlobalData.AppKey);
					params.put("sign", sign);
					params.put("vin", info.getVin());
					params.put("token",info.getToken());
					upgrade(URL_HOST+URL_SOURCE_PART+"?"+"appkey="+GlobalData.AppKey+"&"+"vin="+info.getVin()+"&"+"sign="+sign+"&"+"token="+info.getToken());
				}
			}
		});*/
		AppManager.getAppManager().addActivity(this);
		//Settings.System.getString(getContentResolver(),"");
	}

	@Override
	public void onResume() {
		super.onResume();

		downloadListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		try {
			if (downloadListAdapter != null && downloadManager != null) {
				downloadManager.backupDownloadInfoList();
			}
		} catch (DbException e) {
			LogUtils.e(e.getMessage(), e);
		}
		AppManager.getAppManager().finishActivity(this);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// 下载按钮的点击事件中进行调用.方法中会根据EditText中输入的地址添加一个任务并进行下载
	private void download(String url,String name) {
		Log.i(TAG, url);
		String target = DIR_FILE_DOWNLOAD+"/8-03-01/" + System.currentTimeMillis();
		try {
			String fileName = (name==null)?url.substring(url.lastIndexOf("/") + 1):name;
			downloadManager.addNewDownload(url, fileName, target + fileName,
					true, true, null);
			downloadListAdapter.notifyDataSetChanged();
		} catch (DbException e) {
			LogUtils.e(e.getMessage(), e);
		}
	}

	private void upgrade(String url) {
		Log.i(TAG, url);
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(1000);
		
		try{
		http.send(HttpRequest.HttpMethod.GET, url,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						Log.i(TAG,responseInfo.result);
						JSONParser parser = new JSONParser(responseInfo.result);
						UpdateStatusInfo status=parser.getStatus();
							if (!status.isOk()) {	//如果返回的信息不是有效的升级信息就进行错误处理
								Toast.makeText(getApplicationContext(),status.getErrorMessage()
											+ " ErrorCode:"
											+ status.getErrorCode(),Toast.LENGTH_LONG).show();
							}
							else
							{
								//获取升级信息
								groupVersionVo = parser.getFullInfo();
								List<UpdateVo> voList=groupVersionVo.getUpdateVoList();
								for(UpdateVo vo:voList)
								{
									download(vo.getFileUrl(),vo.getFileName());
									downloadListAdapter.notifyDataSetChanged();
								}
							}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						Toast.makeText(getApplicationContext(),
								"Upgrade encounter error", Toast.LENGTH_LONG)
								.show();
					}
				});
		}catch(Exception e)
		{e.printStackTrace();}
	}

	public void reportUpdateStatus(String status)
	{
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(1000);
		String url=URL_HOST+"update-center/reportUpdateStatus";
		CarInfo info = InfoUtils.queryCarinfo();
		String vin=info.getVin();
		String token=info.getToken();
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(sign==null)
			return ;
		url=url+"?"+"appKey="+Saver.getAppKey()+"&sign="+sign+"&token="+token+"&vin="+vin+"&appId=mcu"+"&status="+status;
		http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>(){
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(getApplicationContext(), arg0.getMessage(), Toast.LENGTH_LONG).show();
			}
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Toast.makeText(getApplicationContext(), responseInfo.result, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public DownloadListAdapter getDownloadListAdapter() {
		return downloadListAdapter;
	}
}
