package com.zhonghong.mediasdk.photo;

import java.util.List;
import com.zhonghong.mediasdk.Constants;
import android.app.Activity;
import android.app.Fragment;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PPBFragment extends Fragment implements SuperImageViewListener{
	
	private static final String TAG = "PPB";
    private SuperImageView  mPhotoImageView = null;
    private Context mContext = null;
    private FrameLayout mPhotoLayout = null;
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;
    private int rotateCoefficient = 0;
     
	@Override
	public void onAttach(Activity act) {	
		super.onAttach(act);
		mContext = act;
		mPhotoImageView = new SuperImageView(act); 
		//mPhotoImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		mPhotoLayout = new FrameLayout(mContext);
		//mPhotoLayout.addView(mPhotoImageView);
		
		mPhotoImageView.setShowImgListener(this);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        mScreenHeight = dm.heightPixels;
        mScreenWidth = dm.widthPixels;
	}
	
   @Override
   public void onDetach(){
	   super.onDetach();
	   mContext = null;
	   mPhotoImageView = null;
	   mPhotoLayout = null;
   }
   
   @Override
   public void onResume(){
	   Log.d(TAG,"onResume");
	   getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	   //getActivity().getWindow().addFlags((WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN));
	   super.onResume();
	   
	   showImg();
   }
   
   @Override
   public void onPause(){
	   Log.d(TAG,"onPause");
	   super.onPause();
   }
   
   @Override
   public void onStart(){
	   Log.d(TAG,"onStart");
	   super.onStart();
   }
   
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
	   Log.d(TAG,"onCreateView");
	  return super.onCreateView(inflater, container, savedInstanceState);
   }
   /**
    * 
    * @param inflater
    * @param container
    * @return
    */
   public FrameLayout onCreatePhotoPBView(LayoutInflater inflater,View userview, ViewGroup container){
	   if(mPhotoLayout != null){
		   mPhotoLayout.removeAllViews();
		   mPhotoLayout.addView(mPhotoImageView);
		   mPhotoLayout.addView(userview);
	   }
	   return mPhotoLayout;
   }

   /**
    * 
    */
   public void showImg(){
	   String path = PhotoServiceHelper.getPlaybackFilePath();
	   
	   if((path == null) || (mPhotoImageView == null)){
		   Log.e(TAG, "showImg [FAIL],"+path);
		   return;
	   }
	   
	   mPhotoImageView.clearAnimation();
       Drawable toRecycle = mPhotoImageView.getDrawable();

       if (toRecycle != null)
       {
           Bitmap b = ((BitmapDrawable) toRecycle).getBitmap();
           if (b != null)
           {
               b.recycle();
               System.gc();
           }
       }
       rotateCoefficient = 0;
       Bitmap b = PhotoAsyncDataLoader.makeBitmap(mScreenHeight, mScreenHeight * mScreenWidth,path , new BitmapFactory.Options());
       mPhotoImageView.setImageBitmap(b);
       mPhotoImageView.invalidate();    
   }
   
    /**
     * 
     */
   @Override
	public void showNextImg() {
	    PhotoServiceHelper.nextPhoto();
	    showImg();
	}
	
   /**
    * 
    */
	@Override
	public void showPreImg() {
	    // TODO Auto-generated method stub
		PhotoServiceHelper.prevPhoto();
        showImg();
	}
   /**
    * 
    */
	@Override
	public void singleClickEvent() {
	    // TODO Auto-generated method stub
		Log.d(TAG,"singleClickEvent");
		PhotoServiceHelper.singleClickEvent();
	}
	
	/**
	 * 
	 */
	public void rotateImage(float rotate){
		
		if(rotate == 0){
			rotate = 90;
		}
		
		AnimationSet set = new AnimationSet(false);
		RotateAnimation anim =new RotateAnimation(rotateCoefficient, rotateCoefficient+rotate,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setFillAfter(true);
		mPhotoImageView.startAnimation(anim);
	    rotateCoefficient += rotate;
	}
	
	/**
	 * 
	 */
	public boolean ZoomIn()
	{
		if(mPhotoImageView == null){
			Log.d(TAG,"zoomIn [FAIL],mPhotoImageView:"+mPhotoImageView);
			return false;
		}
		return mPhotoImageView.ZoomIn();
	}
	
	/**
	 * 
	 */
	public boolean ZoomOut()
	{
		if(mPhotoImageView == null){
			Log.d(TAG,"ZoomOut [FAIL],mPhotoImageView:"+mPhotoImageView);
			return false;
		}
		return mPhotoImageView.ZoomOut();
	}
}
