/**
 * 
 */
package com.zhonghong.menuitem;

import java.util.ArrayList;
import java.util.List;

import com.zhonghong.launcher.R;

import android.content.Context;

/**
 * @author YC
 * @time 2016-4-11 上午9:50:44
 * @TODO：主界面Item控制
 */
public class ItemControl {
	
	private final List<ItemInfoBean> mItemInfos = new ArrayList<ItemInfoBean>(){
		{
			clear();
			add(new ItemInfoBean("导航", R.drawable.item_navi_selector));
			add(new ItemInfoBean("蓝牙音乐", R.drawable.item_bt_music_selector));
			add(new ItemInfoBean("收音机", R.drawable.item_radio_selector));
			add(new ItemInfoBean("USB设备", R.drawable.item_usb_selector));
			add(new ItemInfoBean("蓝牙电话", R.drawable.item_bt_selector));
			add(new ItemInfoBean("LED屏幕", R.drawable.item_led_selector));
			add(new ItemInfoBean("扩展", R.drawable.item_extend_selector));
			add(new ItemInfoBean("百度Carlife", R.drawable.item_carlife_selector));
			add(new ItemInfoBean("Ilink商城", R.drawable.item_appstore_selector));
			add(new ItemInfoBean("语音记事", R.drawable.item_speech_selector));
			add(new ItemInfoBean("一键呼叫", R.drawable.item_call_selector));
			add(new ItemInfoBean("系统设置", R.drawable.item_setup_selector));
			add(new ItemInfoBean("", R.drawable.item_default));
			add(new ItemInfoBean("", R.drawable.item_default));
			add(new ItemInfoBean("", R.drawable.item_default));
		}
	};

	
	private Command[] commands;
	
	public ItemControl() {
		commands = new Command[mItemInfos.size()];
		for (int i = 0; i < mItemInfos.size(); i++)
		{
			commands[i] = new NoCommand();
		}
	}
	
	public List<ItemInfoBean> getItemInfos() {
		return mItemInfos;
	}

	/**设置Item的事件*/
	public void setCommand(int index, Command command){
		if (index < 0 && index >= mItemInfos.size())
		{
			return;
		}
		commands[index] = command;
	}
	
	
	public boolean onItemKeyDown(Context context, int index){
		if (index < 0 && index >= mItemInfos.size())
		{
			return false;
		}
		return commands[index].execute(context);
	}
	
	
}
