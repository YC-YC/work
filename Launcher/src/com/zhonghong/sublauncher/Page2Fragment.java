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
import com.zhonghong.menuitem.AppstoreCommand;
import com.zhonghong.menuitem.CarlifeCommand;
import com.zhonghong.menuitem.ExtendCommand;
import com.zhonghong.menuitem.UserCommand;

/**
 * @author YC
 * @time 2016-7-4 上午10:06:18 
 * TODO:副屏第二页
 */
public class Page2Fragment extends Fragment implements OnClickListener{

	private View mView;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.sub_main_page2, null);
		initView();
		return mView;
	}

	private void initView() {

		((Button)mView.findViewById(R.id.btn_sub_carlife)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.btn_sub_appstore)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.btn_sub_extend)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.btn_sub_user)).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_sub_carlife:
			new CarlifeCommand().execute(view.getContext());
			break;
		case R.id.btn_sub_appstore:
			new AppstoreCommand().execute(view.getContext());
			break;
		case R.id.btn_sub_extend:
			new ExtendCommand().execute(view.getContext());
			break;
		case R.id.btn_sub_user:
			new UserCommand().execute(view.getContext());
			break;
		}
	}
}
