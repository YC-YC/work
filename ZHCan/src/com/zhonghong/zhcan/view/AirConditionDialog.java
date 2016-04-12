/**
 * 
 */
package com.zhonghong.zhcan.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhonghong.zhcan.R;

/**
 * @author YC
 * @time 2016-4-9 下午2:14:48
 */
public class AirConditionDialog extends Dialog {

	/**风速*/
	private ImageView mWindSpeed, mWindSpeedLevel;
	/**温度*/
	private ImageView mAirDegree;
	/**空调类型*/
	private ImageView mBigAirType;
	/**空调类型*/
	private TextView mAirType;
	/**空调类型调节*/
	private Button mBtnAirType;
	
	/**空调温度*/
	private TextView mTVAirDegree;
	/**自动调节*/
	private Button mAuto;
	/**空调模式调节*/
	private Button mBtnAirMode;
	/**除窗调节*/
	private Button mFrontWindow;
	
	
	public AirConditionDialog(Context context) {
		super(context,R.style.CustomDialog);
		setCustomView();
	}
	
	/** 自定义界面 */
	private void setCustomView() {/*

		View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_can,
				null);
		super.setContentView(v);

		mWindSpeed = (ImageView) v.findViewById(R.id.img_wind_speed);
		
		mWindSpeedLevel = (ImageView) v.findViewById(R.id.img_wind_speed_level);
		
		mAirDegree = (ImageView) v.findViewById(R.id.img_degree);
		
		mBigAirType = (ImageView) v.findViewById(R.id.img_air_type);
		mAirType = (TextView) v.findViewById(R.id.tv_air_type);
		mBtnAirType = (Button) v.findViewById(R.id.btn_air_type);
		
		mTVAirDegree = (TextView) v.findViewById(R.id.tv_degree);
		
		mAuto = (Button) v.findViewById(R.id.btn_auto);

		mBtnAirMode = (Button) v.findViewById(R.id.btn_air_mode);
		
		mFrontWindow = (Button) v.findViewById(R.id.btn_front_window);
		
		mBtnAirType.setOnClickListener(mViewOnClickListener);
		mAuto.setOnClickListener(mViewOnClickListener);
		mBtnAirMode.setOnClickListener(mViewOnClickListener);
		mFrontWindow.setOnClickListener(mViewOnClickListener);
		
	*/}
	
	
	
	private android.view.View.OnClickListener mViewOnClickListener = new android.view.View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			/*switch (v.getId()) {
			case R.id.btn_air_type:

				break;
			case R.id.btn_auto:

				break;
			case R.id.btn_air_mode:

				break;
			case R.id.btn_front_window:

				break;

			default:
				break;
			}*/
		}
	};
	
}
