package com.zhonghong.chelianupdate.dialog;

import java.io.Serializable;

import com.zhonghong.chelianupdate.R;
import com.zhonghong.chelianupdate.activity.UpdateActivity;
import com.zhonghong.chelianupdate.base.AppConst;
import com.zhonghong.chelianupdate.bean.GroupVersionVo;
import com.zhonghong.chelianupdate.utils.Saver;
import com.zhonghong.sdk.android.ZHAppSdk;
import com.zhonghong.sdk.android.utils.AppManager;
import com.zhonghong.sdk.android.utils.PreferenceUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * 
 * 升级下载完成，提示进行升级的对话框
 * */
public class InstallDialog extends Dialog{

	private Context mContext;
	private Button btnPositive;
    private Button btnNegative;
    private GroupVersionVo groupVersionVo;
	public InstallDialog(Context context,GroupVersionVo groupVersionVo) {
		super(context,android.R.style.Theme_Translucent_NoTitleBar);
		this.mContext=context;
		this.groupVersionVo=groupVersionVo;
		init();
	}
	private void init()
	{
		View contentView = LayoutInflater.from(mContext).inflate(R.layout.install_popupwindow, null); 
	    btnPositive=(Button) contentView.findViewById(R.id.btn_positive);
	    btnNegative=(Button) contentView.findViewById(R.id.btn_negative);
	    
	    btnPositive.setOnClickListener(new View.OnClickListener() {
	    	@Override
			public void onClick(View view) {
				Intent intent=new Intent(AppConst.ACTION_CHELIAN_INSTALL_UPDATE);
				Bundle bundle=new Bundle();
				bundle.putSerializable("vo_list", (Serializable) groupVersionVo.getUpdateVoList());
				intent.putExtras(bundle);
				mContext.startService(intent);
				Saver.setInstallState(Saver.INSTALL_STATE_BEGIN);
				/*Activity activiy=AppManager.getAppManager().currentActivity();
	    		if(activiy!=null&&activiy instanceof UpdateActivity)
	    		{
	    			activiy.finish();
	    		}*/
				dismiss();
			}
		});
	    btnNegative.setOnClickListener(new View.OnClickListener() {
	    	@Override
			public void onClick(View view) {
	    		int times = Saver.getDelayUpgradeTimes() + 1;
	    		if (times >= 3){
//	    			Settings.System.putString(mContext.getContentResolver(), "updateinstall", "finish"); 
	    			Saver.setInstallState(Saver.INSTALL_STATE_CANCEL);
	    		}
	    		Saver.setDelayUpgradeTimes(times);
	    		dismiss();
			}
		});
	    super.setContentView(contentView);
	}
	
}
