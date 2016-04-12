package com.zhonghong.cancusom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhonghong.cancusom.CanDataManager.CanDataChangeCallback;
import com.zhonghong.zhcan.R;

public class CustomUI {
	private static final String TAG = "CustomUI";
	Context context;
	private View airView;
	private Dialog mAirDialog = null;
	private numView mNumView;
	private ImageView windview,windpowerview,ocview,cview,blowmodeview;
	private TextView kmtx;
	//风速强度
	int windpowerpress[] = { R.drawable.windpower0press,
			R.drawable.windpower1press, R.drawable.windpower2press,
			R.drawable.windpower3press, R.drawable.windpower4press,
			R.drawable.windpower5press, R.drawable.windpower6press,
			R.drawable.windpower7press};
	//吹风模式
	int blowmode[]={R.drawable.blowheadpress,R.drawable.blowheadfootpress,
			R.drawable.blowfootpress
	};
	//吹风模式小图标
	int blowlittlemode[]={R.drawable.blowheadlittlepress,R.drawable.blowheadfootlittlepress,
			R.drawable.blowfootlittlepress
	};
	//空调模式
	int airmode[]={
			R.drawable.airautopress,R.drawable.aironpress,R.drawable.airoffpress
	};
	// 车内空气循环模式
	int AirCircurlationMode[]={
			R.drawable.airautocirclepress,R.drawable.airincirclepress,
			R.drawable.airoutcirclepress
	};
	public CustomUI(Context c) {
		// TODO Auto-generated constructor stub
		context = c;
		CanDataManager.getInstance().register(mCanDataChangeCallback);
		setupViews();	
	}
	
	CanDataChangeCallback mCanDataChangeCallback = new CanDataChangeCallback() {
		
		@Override
		public void callback() {
			//CanDataManager.currentAirInfo
		}
	};
	
	int num = 0;
	public Handler dialoghandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case CanConstance.MSG_SHOW_AIR_DIALOG:
				showAirDialog(true);
				break;
			case CanConstance.MSG_HIDE_AIR_DIALOG:
				 showAirDialog(false);
				break;
			case 500://test
				num++;
				mNumView.setnum(num%99);
				setpowerui(num%7);
				setwindmode(num%3);
				dialoghandler.removeMessages(500);
				dialoghandler.sendEmptyMessageDelayed(500, 1*1000);
				CanDataManager.currentAirInfo.blowMode = num%2;
				CanDataManager.currentAirInfo.windSpeed = (num+2)%8;
				CanDataManager.currentAirInfo.AirCircurlationMode = num%3;
				CanDataManager.currentAirInfo.leftTemperature = (byte) (num%30);;
				CanDataManager.currentAirInfo.bAutoHighWind = (num%2 == 1) ? true:false;
				Log.i(TAG, "CustomUI num = " + num);
				CanDataManager.getInstance().checkAirInfoUpdate();
			}
		};
	};
	
	/**
	 * 初始化AirDialog View
	 */
	private void setupViews() {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		airView = inflater.inflate(R.layout.customlayout, null);
		initview();
		dialoghandler.sendEmptyMessageDelayed(500, 500);
		
	}
	
	private void initview(){
		mNumView = (numView)airView.findViewById(R.id.numid);
		windview = (ImageView)airView.findViewById(R.id.windid);
		windpowerview = (ImageView)airView.findViewById(R.id.windpowerid);
		ocview = (ImageView)airView.findViewById(R.id.ocid); 
		cview = (ImageView)airView.findViewById(R.id.cid);
		blowmodeview = (ImageView)airView.findViewById(R.id.blowmodeid);
		kmtx = (TextView)airView.findViewById(R.id.kmtx);
		Typeface type = Typeface.createFromAsset(airView.getContext().getAssets(), "EXPANSIVA.OTF");
		kmtx.setTypeface(type);
	}
	/**
	 * 比较空调信息，有改变就显示
	 */
	public void checkAirInfoUpdate(){
		boolean dataUpdate = false;
		if(CanDataManager.currentAirInfo.blowMode != CanDataManager.lastAirInfo.blowMode){
			dataUpdate = true;
			setwindmode(CanDataManager.currentAirInfo.blowMode);
			CanDataManager.lastAirInfo.blowMode = CanDataManager.currentAirInfo.blowMode;
		}
		if(dataUpdate){
			dialoghandler.sendEmptyMessage(CanConstance.MSG_SHOW_AIR_DIALOG);
		}
	}
	/**
	 * 设置风速强度
	 * @param num
	 */
	private void setpowerui(int num){
		if(num>=0&&num<=7){
			windpowerview.setBackgroundResource(windpowerpress[num]);
		}
	}
	/**
	 * 设置吹风模式
	 * @param num
	 */
	private void setwindmode(int num){
		if(num>=0&&num<3){
			blowmodeview.setBackgroundResource(blowmode[num]);
		}
	}
	/**
	 * 创建airdialog
	 * @param v
	 */
	private void createSystemDialog(View v) {
		mAirDialog = new Dialog(context, R.style.TranslucentDialog) {
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				// TODO Auto-generated method stub
				if (isShowing()
						&& event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					this.dismiss();
					return true;
				}
				return false;
			}
		};
		mAirDialog.setContentView(v);
		mAirDialog.setCanceledOnTouchOutside(true);
		Window window = mAirDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = window.getWindowManager().getDefaultDisplay().getWidth();
		lp.height = window.getWindowManager().getDefaultDisplay().getHeight();
		lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//系统Dialog
		lp.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		WindowManager wn = (WindowManager) context
				.getSystemService(context.WINDOW_SERVICE);
		lp.dimAmount = 0;
		window.setAttributes(lp);
		window.addFlags(LayoutParams.FLAG_NOT_FOCUSABLE
				| LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

	}
	/**
	 * 显示隐藏AirDialog
	 * @param show
	 */
	public void showAirDialog(boolean show) {
		if (show) {
			if (mAirDialog == null) {
				createSystemDialog(airView);
			}
			if (mAirDialog != null && !mAirDialog.isShowing()) {
				mAirDialog.show();
				dialoghandler.removeMessages(CanConstance.MSG_HIDE_AIR_DIALOG);
				dialoghandler.sendEmptyMessageDelayed(
						CanConstance.MSG_HIDE_AIR_DIALOG,
						CanConstance.AIR_SHOW_TIME);
			} else {
				dialoghandler.removeMessages(CanConstance.MSG_HIDE_AIR_DIALOG);
				dialoghandler.sendEmptyMessageDelayed(
						CanConstance.MSG_HIDE_AIR_DIALOG,
						CanConstance.AIR_SHOW_TIME);
			}
		} else {
			if (mAirDialog != null && mAirDialog.isShowing()) {
				mAirDialog.dismiss();
				dialoghandler.removeMessages(CanConstance.MSG_HIDE_AIR_DIALOG);
			}
		}
	}

}
