/**
 * 
 */
package com.zhonghong.launcher.item;

import com.zhonghong.launcher.R;

import android.content.Context;

/**
 * @author YC
 * @time 2016-4-11 上午9:50:44
 * 主界面Item控制
 */
public class ItemControl {

	private static final int ITEM_NUM = 5;
	
	private String[] mItemTexts = new String[] { 
			"收音机", 
			"设置", 
			"USB",
			"电话", 
			"音乐 "};
	private int[] mItemImgs = new int[] { 
			R.drawable.radio_selector,
			R.drawable.setup_selector, 
			R.drawable.usb_selector,
			R.drawable.bt_selector, 
			R.drawable.music_selector};
	
	private Command[] commands;
	
	public ItemControl() {
		commands = new Command[ITEM_NUM];
		for (int i = 0; i < ITEM_NUM; i++)
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
		if (index < 0 && index >= ITEM_NUM)
		{
			return;
		}
		
		commands[index] = command;
	}
	
	
	public boolean onItemKeyDown(Context context, int index){
		if (index < 0 && index >= ITEM_NUM)
		{
			return false;
		}
		
		return commands[index].execute(context);
	}
	
	
}
