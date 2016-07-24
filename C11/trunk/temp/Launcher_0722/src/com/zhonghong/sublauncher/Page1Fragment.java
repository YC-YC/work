/**
 * 
 */
package com.zhonghong.sublauncher;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.zhonghong.launcher.R;
import com.zhonghong.menuitem.BtMusicCommand;
import com.zhonghong.menuitem.LEDCommand;
import com.zhonghong.menuitem.NaviCommand;
import com.zhonghong.menuitem.USBCommand;

/**
 * @author YC
 * @time 2016-7-4 上午10:06:18 
 * TODO:副屏第一页
 */
public class Page1Fragment extends Fragment implements OnClickListener{

	private View mView;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.sub_main_page1, null);
		initView();
		return mView;
	}

	private void initView() {

		((Button)mView.findViewById(R.id.btn_sub_navi)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.btn_sub_bt_music)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.btn_sub_usb)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.btn_sub_led)).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_sub_navi:
			new NaviCommand().execute(view.getContext());
			break;
		case R.id.btn_sub_bt_music:
			new BtMusicCommand().execute(view.getContext());
			break;
		case R.id.btn_sub_usb:
			new USBCommand().execute(view.getContext());
			break;
		case R.id.btn_sub_led:
			new LEDCommand().execute(view.getContext());
			break;
		}
	}
}
