/**
 * 
 */
package com.zhonghong.launcher.item;

import java.util.ArrayList;
import java.util.List;

import com.zhonghong.launcher.R;

import android.content.Context;

/**
 * @author YC
 * @time 2016-4-11 上午9:50:44
 * 主界面Item控制
 */
public class ItemControl {

//	private static final int ITEM_NUM = 15;
	
/*	private final List<ItemInfoBean> mItemInfos = new ArrayList<ItemInfoBean>(){
		add(0, new ItemInfoBean("导航", R.drawable.radio_selector));
		add(0, new ItemInfoBean("蓝牙音乐", R.drawable.radio_selector));
		add(0, new ItemInfoBean("收音机", R.drawable.radio_selector));
		add(0, new ItemInfoBean("USB设备", R.drawable.radio_selector));
		add(0, new ItemInfoBean("导航", R.drawable.radio_selector));
		add(0, new ItemInfoBean("导航", R.drawable.radio_selector));
		add(0, new ItemInfoBean("导航", R.drawable.radio_selector));
		add(0, new ItemInfoBean("导航", R.drawable.radio_selector));
	};*/
	
	private String[] mItemTexts = new String[] { 
			"导航", 
			"蓝牙音乐", 
			"收音机",
			"USB设备", 
			"蓝牙电话",
			"LED屏幕", 
			"扩展", 
			"百度Carlife",
			"Ilink商城", 
			"语音记事",
			"一键呼叫", 
			"系统设置", 
			"",
			"", 
			""};
	private int[] mItemImgs = new int[] { 
			R.drawable.radio_selector,
			R.drawable.music_selector, 
			R.drawable.radio_selector,
			R.drawable.usb_selector, 
			R.drawable.bt_selector,
			R.drawable.radio_selector,
			R.drawable.radio_selector, 
			R.drawable.radio_selector,
			R.drawable.radio_selector, 
			R.drawable.radio_selector,
			R.drawable.radio_selector,
			R.drawable.setup_selector, 
			R.drawable.item_default,
			R.drawable.item_default, 
			R.drawable.item_default
			/*R.drawable.radio_selector,
			R.drawable.setup_selector, 
			R.drawable.usb_selector,
			R.drawable.bt_selector, 
			R.drawable.music_selector,
			R.drawable.radio_selector,
			R.drawable.setup_selector, 
			R.drawable.usb_selector,
			R.drawable.bt_selector, 
			R.drawable.music_selector,
			R.drawable.radio_selector,
			R.drawable.setup_selector, 
			R.drawable.usb_selector,
			R.drawable.bt_selector, 
			R.drawable.music_selector*/};
	
	private Command[] commands;
	
	public ItemControl() {
		commands = new Command[mItemTexts.length];
		for (int i = 0; i < mItemTexts.length; i++)
		{
			commands[i] = new NoCommand();
		}
	}
	
	public String[] getItemTexts(){
		return mItemTexts;
	}
	
	public int[] getItemImgIds(){
		return mItemImgs;
	}
	
	/**设置Item的事件*/
	public void setCommand(int index, Command command){
		if (index < 0 && index >= mItemTexts.length)
		{
			return;
		}
		
		commands[index] = command;
	}
	
	
	public boolean onItemKeyDown(Context context, int index){
		if (index < 0 && index >= mItemTexts.length)
		{
			return false;
		}
		
		return commands[index].execute(context);
	}
	
	
}
