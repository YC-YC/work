package com.zhonghong.updatemcu103.view;

import java.util.ArrayList;

import com.zhonghong.updatemcu103.R;
import com.zhonghong.updatemcu103.UpdateMCU103;
import com.zhonghong.updatemcu103.UpdateStatus;
import com.zhonghong.updatemcu103.Utils;
import com.zhonghong.updatemcu103.R.id;
import com.zhonghong.updatemcu103.R.layout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ListView mFileList;
	private ArrayList<String> mFileStr = new ArrayList<String>();
	private ArrayAdapter<String> mAdapter;
	
	private TextView mTVState;
	private StringBuilder mUpdateState = new StringBuilder();
	
	private String path;
	
	private ProgressDialog progressDialog;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		searchFile();
	}
	
	private void initView() {
		mTVState = (TextView) findViewById(R.id.state);
		mFileList = (ListView) findViewById(R.id.filelist);
		mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mFileStr);
		mFileList.setAdapter(mAdapter);
		mFileList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				path = mFileStr.get(position);
				mUpdateState.append("\r\n选择的文件是：" + path);
				mTVState.setText(mUpdateState.toString());
			}
		});
		mUpdateState.setLength(0);
		mUpdateState.append("\r\n选择的文件是：" + path);
		mTVState.setText(mUpdateState.toString());
	}

	public void doClick(View view){
		switch (view.getId()) {
		case R.id.startupdate:
			if (UpdateMCU103.getInstances().isUpdateing()){
				mUpdateState.append("\r\n正在升级,请勿重复升级！");
				mTVState.setText(mUpdateState.toString());
			}
			if (Utils.hasFileExits(path)){
				UpdateMCU103.getInstances().startUpdate(path, new UpdateStatus() {
					
					@Override
					public void onStatus(int cmd, int value) {
						Message msg = Message.obtain();
						msg.what = UPDATE_CALLBACK;
						msg.arg1 = cmd;
						msg.arg2 = value;
						mHandle.sendMessage(msg);
					}
				});
			}
			break;
		case R.id.search:
			searchFile();
			break;
		case R.id.test:
			updateProgress(100, 10);
			break;
		default:
			break;
		}
	}
	
	private void searchFile(){
		new Thread(){
			public void run() {
				Utils.clearSearchList();
				Utils.searchFile("/mnt/USB/", ".bin");
				Utils.searchFile("/mnt/USB1/", ".bin");
				Utils.searchFile("/mnt/USB2/", ".bin");
				mHandle.sendEmptyMessage(SEARCH_FILE_FINISH);
			};
		}.start();
	}
	
	private static final int UPDATE_CALLBACK = 0x100;
	private static final int SEARCH_FILE_FINISH = 0x101;
	
	private Handler mHandle = new Handler() {
		
		private int totalFileSize;
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SEARCH_FILE_FINISH:
				mFileStr.clear();
				mFileStr.addAll(Utils.getSearchList());
				mAdapter.notifyDataSetChanged();
				break;
			case UPDATE_CALLBACK: {
				int cmd = msg.arg1;
				int value = msg.arg2;
				switch (cmd) {
				case UpdateStatus.STATUS_ERROR:
					mUpdateState.append("\r\n升级出错：" + getUpdateErrMessage(value));
					mTVState.setText(mUpdateState.toString());
					break;
				case UpdateStatus.STATUS_START:
					totalFileSize = value;
					mUpdateState.append("\r\n开始升级...");
					mTVState.setText(mUpdateState.toString());
					break;
				case UpdateStatus.STATUS_UPDATEING:
					updateProgress(totalFileSize, value);
					break;
				case UpdateStatus.STATUS_FINISH:
					if (progressDialog != null && progressDialog.isShowing()){
						progressDialog.dismiss();
					}
					mUpdateState.append("\r\n升级成功!");
					mTVState.setText(mUpdateState.toString());
					break;

				default:
					break;
				}
			}
				break;

			default:
				break;
			}
		};
	};
	
	private void updateProgress(int total, int curValue){
		if (progressDialog == null){
			progressDialog = new ProgressDialog(this);
		}
		if (!progressDialog.isShowing()){
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setTitle("升级中...");
			progressDialog.setMessage("请勿断电！");
			progressDialog.setIcon(android.R.drawable.ic_dialog_alert);
			progressDialog.setMax(100);
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
			progressDialog.setProgress(0);
		}
		
		progressDialog.setProgress(curValue*100/total);
	}
	
	private String getUpdateErrMessage(int value){
		switch (value) {
		case 0:
			return "文件不存在";
		case 1:
			return "文件错误";
		case 2:
			return "正在升级";
		}
		return "未知错误";
	}

}
