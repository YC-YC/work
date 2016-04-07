package com.zhonghong.media.photo.setwallpaper;

import java.io.IOException;

import com.zhonghong.media.util.ImageUtils;
import com.zhonghong.newphoto.R;

import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SetWallPaperDialog extends Dialog{

	private static final String TAG = "SetWallPaperDialog";
	private String picturepath;
	private Context mContext;
	
	private int mSuggestDesiredWidth;
	private int mSuggestDesiredHeight;
	
	public final static String IMAGE_URI = "iamge_path";
	
	public SetWallPaperDialog(Context context) {
		super(context);
		mContext = context;
		// TODO Auto-generated constructor stub
	}
	
	public SetWallPaperDialog(Context context,String picturepath,int width,int height)
	{
		super(context,R.style.MyDialog);
		this.picturepath = picturepath;
		mContext = context;
		mSuggestDesiredWidth = width;
		mSuggestDesiredHeight = height;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_setwallpaper_dialog);
		this.setCanceledOnTouchOutside(true);
        final TextView normalMode = (TextView) findViewById(R.id.btn_normal);
        final TextView strechMode = (TextView) findViewById(R.id.btn_stretch);
        final TextView cutMode = (TextView) findViewById(R.id.btn_cut);
        
        final Button btnConfirm = (Button) findViewById(R.id.btn_setwallpaper_confirm);
        final Button btnCancel = (Button) findViewById(R.id.btn_setwallpaper_cancel);

        View.OnClickListener listener = new View.OnClickListener() {
        	
        	//只会在第一次执行listener时 执行下面的语句。所以不会重复赋值
        	int record = 2;
            @Override
            public void onClick(View v) {
            	Log.i(TAG, "record==="+record);
                switch (v.getId()) {
                case R.id.btn_normal: // 
                	record = 1;
                	resetBackground(normalMode,strechMode,cutMode);
                    normalMode.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.normal_dwn, 0, 0);
                    break;
                case R.id.btn_stretch: // 
                	record = 2;
                	resetBackground(normalMode,strechMode,cutMode);
                    strechMode.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.stretch_dwn,0,0);
                    break;
                case R.id.btn_cut: // 
                	record = 3;
                	resetBackground(normalMode,strechMode,cutMode);
                    cutMode.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.cut_dwn,0,0);
                    break;
                case R.id.btn_setwallpaper_confirm:
                	Log.i(TAG, "btn_setwallpaper_confirm record="+record);
                	switch (record) {
					case 1:
					case 2:
						setWallPaper(record);
						break;
					case 3:
						startPhotoCrop();
						break;
					default:
						break;
					}
                	SetWallPaperDialog.this.dismiss();
                	break;
                case R.id.btn_setwallpaper_cancel:
                	SetWallPaperDialog.this.dismiss();
                	break;
                }
            }
        };
        
        btnConfirm.setOnClickListener(listener);
        btnCancel.setOnClickListener(listener);
        normalMode.setOnClickListener(listener);
        strechMode.setOnClickListener(listener);
        cutMode.setOnClickListener(listener);
		
	}
	
	public void resetBackground(TextView normalMode,TextView strechMode,TextView cutMode)
	{
		strechMode.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.stretch, 0, 0);
    	cutMode.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cut, 0, 0);
    	normalMode.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.normal, 0, 0);
	}
	
	public void setWallPaper(int record)
	{
		WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext);  
		Bitmap bp = ImageUtils.decodeFile(picturepath,800,480);
		Log.i(TAG, bp.getWidth()+":"+bp.getHeight());
		if(record == 2)
		{
			wallpaperManager.suggestDesiredDimensions(mSuggestDesiredWidth, mSuggestDesiredHeight);
		}
		else
		{
			wallpaperManager.suggestDesiredDimensions(bp.getWidth(), bp.getHeight());
		}
		try {
			wallpaperManager.setBitmap(bp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(mContext, R.string.setwallpaperfail, Toast.LENGTH_SHORT).show(); 
			e.printStackTrace();
		}  
		Toast.makeText(mContext, R.string.setwallpapersucc, Toast.LENGTH_SHORT).show(); 
	}
	
	private void startPhotoCrop() 
	{
		Log.i(TAG, "startPhotoCrop()");
		Intent intent = new Intent("android.intent.action.CROP");
		intent.putExtra(IMAGE_URI, picturepath);
		mContext.startActivity(intent);
	}
}
