package com.zhonghong.media.util;



import com.zhonghong.newphoto.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;


/*
*Custom progress dialog
*/
public class CustomProgressDialog extends Dialog {
	private Context context = null;
	private static CustomProgressDialog customProgressDialog = null;
	
	public CustomProgressDialog(Context context){
		super(context);
		this.context = context;
	}
	
	public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }
	
	public static CustomProgressDialog createDialog(Context context){
		customProgressDialog = new CustomProgressDialog(context,R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.custom_progressdialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		
		return customProgressDialog;
	}
 
    /*
    *windows have focus
    */
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (customProgressDialog == null){
    		return;
    	}
    	
        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();     
    }
 
    /*
     * dispatchTouchEvent
     * cancel touch event avoid to dialog is closed
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
    	return true;
    }
    /**
     * 
     * [Summary]
     * @param strMessage
     * @return
     *
     */
    public CustomProgressDialog setMessage(String strMessage){
    	TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
    	
    	if (tvMsg != null){
    		tvMsg.setText(strMessage);
    	}
    	
    	return customProgressDialog;
    }
}