package com.zhonghong.cancusom;

import android.content.Context;

import com.zhonghong.canbase.service.CanParserBase;

/**
 * 
 * @author YC
 * @time 2016-4-28 上午9:30:49
 */
public class CustomParse extends CanParserBase{
	
	private CustomUI mCustomUI;
	
	public CustomParse(Context context) {
		mCustomUI = new CustomUI(context);
		mCustomUI.dialoghandler.sendEmptyMessageDelayed(CanConstance.MSG_SHOW_AIR_DIALOG, 2000);
	}
	
	@Override
	public void parsePacket(short[] caninfo) {
		//解析can的数据
		if(caninfo[0]==0x2e&&caninfo[1]==0x2){
			parseAirInfo(caninfo);
		}
		
	}
	/**
	 * 解析空调信息
	 * @param caninfo
	 */
	public void parseAirInfo(short[] caninfo){

		/**
		 * 0x0: 吹面模式0x1: 吹面/吹足模式0x2: 吹足模式0x3: 吹足/除霜模式0x4: 除霜模式
		 */
		CanDataManager.currentAirInfo.blowMode =
		getIntByBits(caninfo[3], 2, 3);
		/**
		 * 0x0: 内循环状态0x1: 外循环状态
		 */
		CanDataManager.currentAirInfo.AirCircurlationMode =
		getIntByBits(caninfo[3], 5, 1);
		/**
		 * 0x0: 无风挡位0x1: 风速一挡0x2: 风速二挡0x3: 风速三挡0x4: 风速四挡
		 * 0x5: 风速五挡0x6: 风速六挡0x7: 风速七挡0x8: 风速八挡
		 */
		CanDataManager.currentAirInfo.windSpeed =
		getIntByBits(caninfo[4], 8, 4);
		/**
		 * 0x0: 一挡（最冷挡）0x1: 二挡0x2: 三挡0x3: 四挡0x4: 五挡0x5: 六挡
		 * 0x6: 七挡0x7: 八挡0x8: 九挡（最热挡）
		 */
		getIntByBits(caninfo[4], 12, 4);

		
		CanDataManager.getInstance().checkAirInfoUpdate();
		
	}
}
