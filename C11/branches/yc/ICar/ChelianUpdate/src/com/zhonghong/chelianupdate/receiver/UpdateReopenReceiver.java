package com.zhonghong.chelianupdate.receiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.zhonghong.chelianupdate.base.AppConst;
import com.zhonghong.chelianupdate.bean.GroupVersionVo;
import com.zhonghong.chelianupdate.bean.UpdateVo;
import com.zhonghong.chelianupdate.utils.DialogManager;
import com.zhonghong.chelianupdate.utils.FileUtil;
import com.zhonghong.chelianupdate.utils.JSONParser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateReopenReceiver extends BroadcastReceiver{

	private static final String TAG="Update";
	private Context mContext;
	private GroupVersionVo groupVersionVo;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.mContext=context;
		Log.i(TAG,"reopen receive");
		if(intent.getAction().equals("com.zhonghong.zuiserver.BROADCAST"))
		{
			String tag=intent.getStringExtra("upgrate_reopen");
			if(tag==null)
				return ;
		}
		String result=FileUtil.getGroupVersionVo(mContext);
		JSONParser parser = new JSONParser(result);
		if(parser.getStatus().isOk())
		{
			groupVersionVo = parser.getFullInfoCheckValid(mContext);
			List<UpdateVo> voList = groupVersionVo.getUpdateVoList();
			if (voList != null) {
				for (UpdateVo vo : voList) {
					Log.i(TAG, vo.getAppName());
				}
				DialogManager manager=DialogManager.getInstance();
				manager.showDownloadDialog(mContext, groupVersionVo,false);
			}
		}
		
	}

}
