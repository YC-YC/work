package com.zhonghong.zhcan;

import com.zhonghong.zhcan.view.AirConditionDialog;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	private String TAG = getClass().getSimpleName();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	
	public void doClick(View view)
	{
		switch (view.getId()) {
		case R.id.btn_open_dialog:
			openAirDialog();
			break;
		default:
			break;
		}
	}


	private void openAirDialog() {
		AirConditionDialog dialog = new AirConditionDialog(MainActivity.this);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.CENTER_HORIZONTAL);
	    /*
         * 将对话框的大小按屏幕大小的百分比设置
         */
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        
        Point outSize = new Point();
		d.getSize(outSize);
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (outSize.x); 
        p.height = (int) (outSize.y); 
        Log.i(TAG , "Display outSize = " + outSize.toString());
        window.setAttributes(p);
        dialog.show();
	}
	
}
