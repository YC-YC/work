package com.zhonghong.autotestservice;

import com.zhonghong.autotestlib.bean.ATConst;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void doClick(View view){
		switch (view.getId()) {
		case R.id.start_at_service:{
			Intent it = new Intent(ATConst.Service.ACTION);
			startService(it);
		}
			break;
		case R.id.start_at:
		{
			Intent it = new Intent(ATConst.Broadcast.ACTION);
			it.putExtra(ATConst.Broadcast.KEY_START_AT, "true");
			sendBroadcast(it);
		}
			break;
		case R.id.end_at:
		{
			Intent it = new Intent(ATConst.Broadcast.ACTION);
			it.putExtra(ATConst.Broadcast.KEY_END_AT, "true");
			sendBroadcast(it);
		}
			break;
		}
	}

}
