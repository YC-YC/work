/**
 * 
 */
package com.zhonghong.zui;

import android.content.Context;

/**
 * @author chen
 *
 */
public class NotifyLife implements INotifyLife{
	private static final String tag = NotifyLife.class.getSimpleName();
	private NotifyLife(){}
	private static NotifyLife mNotifyLife;
	public static NotifyLife getInstance(){
		if(mNotifyLife == null){
			mNotifyLife = new NotifyLife();
		}
		return mNotifyLife;
	}
	
	Context context;
	public void init(Context context){
		this.context = context;
		ZuiConn.getInstance().init(context);
	}
	
	@Override
	public void enterPage() {
		ZuiConn.getInstance().enterPage();
	}
	@Override
	public void exitPage() {
		ZuiConn.getInstance().exitPage();
	}
	@Override
	public void destroy() {
		ZuiConn.getInstance().destroy();
	}
	
	
}
