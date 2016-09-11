/**
 * 
 */
package com.zhcar.readnumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhcar.R;
import com.zhcar.base.BaseApplication;
import com.zhcar.base.UpdateUiBaseActivity;
import com.zhcar.data.GlobalData;
import com.zhcar.permission.MainActivity;
import com.zhcar.provider.CarProviderData;
import com.zhcar.utils.Saver;
import com.zhcar.utils.UpdateUiManager;
import com.zhcar.utils.UpdateUiManager.UpdateViewCallback;
import com.zhcar.utils.Utils;

/**
 * @author YC
 * @time 2016-8-6 上午10:40:04
 * TODO:五码版本号
 */
public class VersionActivity extends UpdateUiBaseActivity implements OnClickListener{

	private static final String TAG = "VersionActivity";
	private TextView mCurMEID;
	private CheckBox mEnvProcduct;
	private Map<Integer, String> versions = new HashMap<Integer, String>(){
			{
				put(R.id.version_meid, null);
				put(R.id.version_imsi, null);
				put(R.id.version_esn, null);
				put(R.id.version_iccid, null);
				put(R.id.version_sn, null);
				put(R.id.version_vin, null);
			}
	};
	
	private List<Integer> writeBtns = new ArrayList<Integer>(){
		{
			
			add(R.id.write_meid);
			add(R.id.write_imsi);
			add(R.id.write_esn);
			add(R.id.write_iccid);
			add(R.id.write_sn);
			add(R.id.write_vin);
		}
	};
	
//	private Map<Integer, String> mpa = new HashMap<Integer, String>(){
//		{
//			
//		}
//	};
	
	private Map<Integer, String> carInfoMaps = new HashMap<Integer, String>(){
		{
			put(R.id.version_meid, CarProviderData.KEY_CARINFO_MEID);
			put(R.id.version_imsi, CarProviderData.KEY_CARINFO_IMSI);
			put(R.id.version_esn, CarProviderData.KEY_CARINFO_ESN);
			put(R.id.version_iccid, CarProviderData.KEY_CARINFO_ICCID);
			put(R.id.version_sn, CarProviderData.KEY_CARINFO_SN);
			put(R.id.version_vin, CarProviderData.KEY_CARINFO_VIN);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_version);
		getContentResolver().registerContentObserver(CarProviderData.URI_CARINFO, true, mCarInfoObserver);
		mCurMEID = (TextView) findViewById(R.id.curmeid);
		mEnvProcduct = (CheckBox) findViewById(R.id.env_procduct);
//		mEnvProcduct.setOnCheckedChangeListener(checkedChangeListener);
		mEnvProcduct.setOnClickListener(mClickListener);
		
		getVersion();
		freshVersion();
		setClick();
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.env_procduct:
				if (Saver.isEnvironmentProduct()){
					updateEnvironmentsToConfigProvider(GlobalData.ENV_TEST);
				}
				else{
					updateEnvironmentsToConfigProvider(GlobalData.ENV_PROCDUCT);
				}
				break;
			default:
				break;
			}
		}
	};
	
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Log.i(TAG, "isChecked = " + isChecked);
			if (isChecked){
//				GlobalData.environments = GlobalData.ENV_PROCDUCT;
				updateEnvironmentsToConfigProvider(GlobalData.ENV_PROCDUCT);
			}
			else{
//				GlobalData.environments = GlobalData.ENV_DEVELOPMENT;
				updateEnvironmentsToConfigProvider(GlobalData.ENV_TEST);
			}
//			UpdateUiManager.getInstances().callUpdate(UpdateUiManager.CMD_UPDATE_ENVIRONMENT, "");
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		mCurMEID.setText(((TelephonyManager) BaseApplication.getInstanse().getSystemService(Context.TELEPHONY_SERVICE))
				.getDeviceId());
		refreshEnvironmentState();
		
	}
	
	
	
	@Override
	protected void onDestroy() {
		getContentResolver().unregisterContentObserver(mCarInfoObserver);
		super.onDestroy();
	}
	private ContentObserver mCarInfoObserver = new ContentObserver(new Handler()) {
		
		public void onChange(boolean selfChange) {
			Log.i(TAG, "CarInfoProvider 数据变化");
			getVersion();
			freshVersion();
		};
	};
	private class CarInfoObserver extends ContentObserver{

		public CarInfoObserver(Handler handler) {
			super(handler);
		}
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i(TAG, "CarInfoProvider 数据变化");
			getVersion();
			freshVersion();
		}
	};
	
	private void setClick(){
		Iterator<Integer> iterator = writeBtns.iterator();
		while (iterator.hasNext()){
			Integer id = iterator.next();
			setOnClick(id);
		}
	}
	
	private void setOnClick(int id){
		View view = findViewById(id);
		if (view != null){
			view.setOnClickListener(this);
		}
	}
	
	/**
	 * 更新五码显示信息
	 */
	private void freshVersion(){
		Iterator<Integer> iterator = versions.keySet().iterator();
		while (iterator.hasNext()){
			Integer id = iterator.next();
			String text = versions.get(id);
			setOnClick(id);
			setText(id, text/*text == null ? getResources().getString(R.string.unknown): text*/);
		}
	}
	
	/**
	 * 更新环境配置
	 */
	private void refreshEnvironmentState(){
		if (mEnvProcduct == null)
			return;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				Log.i(TAG, "GlobalData.environments = " + GlobalData.environments);
				if (Saver.isEnvironmentProduct()){
					mEnvProcduct.setChecked(true);
				}
				else{
					mEnvProcduct.setChecked(false);
				}
			}
		});
	}
	
	/**
	 * 获取五码信息
	 */
	private void getVersion(){
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(CarProviderData.URI_CARINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			String MEID = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_MEID));
			String IMSI = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_IMSI));
			String ESN = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_ESN));
			String ICCID = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_ICCID));
			String SN = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_SN));
			String VIN = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_VIN));
			versions.put(R.id.version_meid, MEID);
			versions.put(R.id.version_imsi, IMSI);
			versions.put(R.id.version_esn, ESN);
			versions.put(R.id.version_iccid, ICCID);
			versions.put(R.id.version_sn, SN);
			versions.put(R.id.version_vin, VIN);
		}

		if (cursor != null){
			cursor.close();
			cursor = null;
		}
	}
	
	private void setText(int id, String text){
		EditText editText = (EditText) findViewById(id);
		if (editText != null){
			editText.setText(text);
		}
	}
	
	private String getInputText(int id){
		return ((EditText) findViewById(id)).getText().toString();
	}
	
	public void doClick(View view){
		switch (view.getId()) {
		case R.id.curmeid:
			if (Utils.isClickTimes(5, true)){
				startActivity(new Intent(this, MainActivity.class));
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.write_vin:
			if (writeInfo(R.id.version_vin)){
				Toast.makeText(this, getResources().getString(R.string.write_ok), 200).show();
			}
			break;
		case R.id.write_esn:
			if (writeInfo(R.id.version_esn)){
				Toast.makeText(this, getResources().getString(R.string.write_ok), 200).show();
			}
			break;
		case R.id.write_iccid:
			if (writeInfo(R.id.version_iccid)){
				Toast.makeText(this, getResources().getString(R.string.write_ok), 200).show();
			}
			break;
		case R.id.write_imsi:
			if (writeInfo(R.id.version_imsi)){
				Toast.makeText(this, getResources().getString(R.string.write_ok), 200).show();
			}
			break;
		case R.id.write_meid:
			if (writeInfo(R.id.version_meid)){
				Toast.makeText(this, getResources().getString(R.string.write_ok), 200).show();
			}
			break;
		case R.id.write_sn:
			if (writeInfo(R.id.version_sn)){
				Toast.makeText(this, getResources().getString(R.string.write_ok), 200).show();
			}
			break;
		default:
			break;
		}
	}
	
	private boolean writeInfo(int id){
		String inputText = getInputText(id);
		if (!TextUtils.isEmpty(inputText) 
				&& !inputText.equals(versions.get(id))){
			versions.put(id, inputText);
			updateCarInfo(carInfoMaps.get(id), inputText);
			return true;
		}
		return false;
	}
	private void updateCarInfo(String key, String value) {
		ContentResolver resolver = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(key, value);
		Cursor cursor = resolver.query(CarProviderData.URI_CARINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			int row = resolver.update(CarProviderData.URI_CARINFO, values, null, null);
		}
		else{
			resolver.insert(CarProviderData.URI_CARINFO, values);
		}
		if (cursor != null){
			cursor.close();
			cursor = null;
		}
	}
	
	/**
	 * 更新生产环境到配置信息
	 */
	private void updateEnvironmentsToConfigProvider(int environments) {
		ContentResolver resolver = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_CONFIG_ENVIRONMENTS, environments);
		Cursor cursor = resolver.query(CarProviderData.URI_CONFIG, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			int row = resolver.update(CarProviderData.URI_CONFIG, values, null, null);
		}
		else{
			resolver.insert(CarProviderData.URI_CONFIG, values);
		}
		if (cursor != null){
			cursor.close();
			cursor = null;
		}
	}
	
	private UpdateViewCallback mUpdateViewCallback = new UpdateViewCallback() {
		
		@Override
		public void onUpdate(int cmd, String val) {
			switch (cmd) {
			case UpdateUiManager.CMD_UPDATE_ENVIRONMENT:
				refreshEnvironmentState();
				break;
			}
		}
	};

	@Override
	protected UpdateViewCallback getUpdateViewCallback() {
		return mUpdateViewCallback;
	}
}