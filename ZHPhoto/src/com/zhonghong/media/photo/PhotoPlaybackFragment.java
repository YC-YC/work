package com.zhonghong.media.photo;

import com.zhonghong.newphoto.R;
import com.zhonghong.media.photo.PhotoLauncher;
import com.zhonghong.mediasdk.Constants;
import com.zhonghong.mediasdk.photo.PPBFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class PhotoPlaybackFragment extends PPBFragment {

	private static final String TAG = "PhotoPlaybackFragment";
	private static final int DEFAULT_TIMEOUT = 5 * 1000;
	private static final int DEFAULT_HIDE_DELAY = 500;
	
	private View mPhotoPlaybackFragment = null;
	
	//private Button setting_btn = null;
	private Button allphoto_btn = null;
	private Button playpause_btn = null;
	private Button zoomout_btn = null;
	private Button zoomin_btn = null;
	private Button leftrotate_btn = null;
	private Button rightrotate_btn = null;
	//private Button delete_btn = null;
	
	//top /bottom control bar
	private Animation top_widget_out_anim = null;
	private Animation top_widget_in_anim = null;
	private Animation bottom_widget_in_anim = null;
	private Animation bottom_widget_out_anim = null;
	private RelativeLayout topControlLayout = null;
	private RelativeLayout bottomControlLayout = null;
	private FrameLayout ll_playback = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onStop(){
		super.onStop();
		mUserUIHandler.removeCallbacks(mHideControllerRunnable);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		showController();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mPhotoPlaybackFragment = inflater.inflate(R.layout.photo_playback_fragment, container, false);
		return  super.onCreatePhotoPBView(inflater,mPhotoPlaybackFragment,container);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		initView();
		setListener();
	}
	
	private void initView(){
		//setting_btn = (Button) mPhotoPlaybackFragment.findViewById(R.id.setting_btn);
		allphoto_btn = (Button) mPhotoPlaybackFragment.findViewById(R.id.allphoto_btn);
		playpause_btn = (Button) mPhotoPlaybackFragment.findViewById(R.id.play_picture_btn);
		//delete_btn = (Button) mPhotoPlaybackFragment.findViewById(R.id.del_btn);
		leftrotate_btn = (Button) mPhotoPlaybackFragment.findViewById(R.id.rotate_left_btn);
		rightrotate_btn = (Button) mPhotoPlaybackFragment.findViewById(R.id.rotate_right_btn);
		zoomin_btn = (Button) mPhotoPlaybackFragment.findViewById(R.id.zoomin_btn);
		zoomout_btn = (Button) mPhotoPlaybackFragment.findViewById(R.id.zoomout_btn);
		topControlLayout = (RelativeLayout) mPhotoPlaybackFragment.findViewById(R.id.top_bar);
		bottomControlLayout = (RelativeLayout) mPhotoPlaybackFragment.findViewById(R.id.bottom_bar);
		ll_playback = (FrameLayout) mPhotoPlaybackFragment.findViewById(R.id.photo_playbacklayout);
	}
	
	/**
	 * 为控件设置监听事件
	 */
	private void setListener(){
		//setting_btn.setOnClickListener(new OnClikListenerImpl());
		allphoto_btn.setOnClickListener(new OnClikListenerImpl());
		playpause_btn.setOnClickListener(new OnClikListenerImpl());
		//delete_btn.setOnClickListener(new OnClikListenerImpl());
		leftrotate_btn.setOnClickListener(new OnClikListenerImpl());
		rightrotate_btn.setOnClickListener(new OnClikListenerImpl());
		zoomin_btn.setOnClickListener(new OnClikListenerImpl());
		zoomout_btn.setOnClickListener(new OnClikListenerImpl());
		//ll_playback.setOnTouchListener(llplaybacklistener);
	}
	
	/**
	 * 
	 * @author Administrator
	 *
	 */
	private class OnClikListenerImpl implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.play_picture_btn:
				((PhotoLauncher) getActivity()).sendCmdToService(Constants.CMD_MSG_PLAY_PAUSE,0,0,null,true);
				break;
			/*case R.id.setwallpaper_btn:
				((PhotoLauncher) getActivity()).sendCmdToService(Constants.CMD_MSG_PHOTO_SETTING,0,0,null,true);
				break;*/
			/*case R.id.del_btn:
				((PhotoLauncher) getActivity()).sendCmdToService(Constants.CMD_MSG_DELETE,0,0,null,true);
				break;*/
			case R.id.rotate_right_btn:
				((PhotoLauncher) getActivity()).sendCmdToService(Constants.CMD_MSG_RIGHT_ROTATE,0,0,null,true);
				break;
			case R.id.rotate_left_btn:
				((PhotoLauncher) getActivity()).sendCmdToService(Constants.CMD_MSG_LEFT_ROTATE,0,0,null,true);
				break;
			case R.id.zoomin_btn:
				((PhotoLauncher) getActivity()).sendCmdToService(Constants.CMD_MSG_ZOOM_IN,0,0,null,true);
				break;
			case R.id.zoomout_btn:
				((PhotoLauncher) getActivity()).sendCmdToService(Constants.CMD_MSG_ZOOM_OUT,0,0,null,true);
				break;
			/*case R.id.setting_btn:
				((PhotoLauncher) getActivity()).sendCmdToService(Constants.CMD_MSG_PHOTO_SETTING,0,0,null,true);
				break;*/
			case R.id.allphoto_btn:
				((PhotoLauncher)getActivity()).switchFragment(PhotoLauncher.BROWSER_FRAGMENT);
				updateUIInitialFFDPos();
				break;
			default:
				break;
			}
			//reset hide msg
			hideControlDelay(DEFAULT_TIMEOUT);
		}
		
	}
	
	/**
	 * 
	 */
	private Handler mUserUIHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			}
		}
	};
	/**
	 * top/bottom control bar
	 */
	/*private OnTouchListener llplaybacklistener = new OnTouchListener() {
		@Override
		public boolean onTouch(View arg0, MotionEvent event) {
			// TODO Auto-generated method stub
	        switch (event.getAction() & MotionEvent.ACTION_MASK) {
	        	// 主点按下
	        	case MotionEvent.ACTION_DOWN:
	        		break;     	
	        	case MotionEvent.ACTION_UP:
	        	break;
	        	default:
	        		break;
	        }
	        
            if(IsVisibleControlBar()){
                // TODO Auto-generated method stub
                hideControlDelay(DEFAULT_HIDE_DELAY);
            }else {
    			//auto hide delay
                hideControlDelay(DEFAULT_TIMEOUT);
                showController();
            }
			return false;
		}
    };*/
	
	/**
	 * 
	 */
    public void hideControlDelay() {
    	mUserUIHandler.removeCallbacks(mHideControllerRunnable);
    	mUserUIHandler.postDelayed(mHideControllerRunnable, DEFAULT_TIMEOUT);
    }
    
    /**
     * 
     * @param milsec
     */
    public void hideControlDelay(int milsec) {
    	mUserUIHandler.removeCallbacks(mHideControllerRunnable);
    	mUserUIHandler.postDelayed(mHideControllerRunnable, milsec);
    }
    
    /**
     * 
     */
    private Runnable mHideControllerRunnable = new Runnable(){
        @Override
        public void run(){
            hideController();
        }
    };
	/**
	 * 
	 */
	private void hideController() {
		 getActivity().getWindow().clearFlags((WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN));
        if(top_widget_out_anim == null){
            top_widget_out_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.top_widget_out);
        }
        if(bottom_widget_out_anim == null){
            bottom_widget_out_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_widget_out);
        }
        
        if(IsVisibleControlBar()){
            if(topControlLayout != null){
                topControlLayout.setVisibility(View.INVISIBLE);
                topControlLayout.startAnimation(top_widget_out_anim);
            }else{
                System.err.println("playStatusLayout is null ptr!!");
            }
  
            if (bottomControlLayout != null) {
                bottomControlLayout.setVisibility(View.INVISIBLE);
                bottomControlLayout.startAnimation(bottom_widget_out_anim);
            } else{
                System.err.println("playControlLayout is null ptr!!");
            }
            Log.d(TAG,"hideController....");
        }
    }
	/**
	 * 
	 */
	private void showController() {
	    getActivity().getWindow().addFlags((WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN));   
		if(top_widget_in_anim == null){
			top_widget_in_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.top_widget_in);
		}
		if(bottom_widget_in_anim == null){
			bottom_widget_in_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_widget_in);
		}
		
		if(!IsVisibleControlBar())
		{	
			if (bottomControlLayout != null ) {
				bottomControlLayout.setVisibility(View.VISIBLE);
				bottomControlLayout.startAnimation(bottom_widget_in_anim);				
			} else {
				Log.i(TAG, "playControlLayout is null");
			}
			
			//add status bar display
			if (topControlLayout != null) {
				topControlLayout.setVisibility(View.VISIBLE);
				topControlLayout.startAnimation(top_widget_in_anim);
			} else {
				Log.i(TAG, "playStatusLayout is null");
			}
			
			//auto hide delay
            hideControlDelay(DEFAULT_TIMEOUT);
			Log.d(TAG,"showController....");
		}
	}
    /**
     * 
     */
    private boolean IsVisibleControlBar(){
    	if(topControlLayout.getVisibility() == View.VISIBLE){
    		return true;
    	}
    	
    	return false;
    }
	/*****************************************************************************************/
	/*****************************************************************************************/
	/**
	 * @author victorchen
	 * 
	 */
	public void updateUIInitialFFDPos(){
		((PhotoLauncher) getActivity()).sendCmdToService(Constants.CMD_MSG_PAUSE,0,0,null,true);
		((PhotoLauncher) getActivity()).sendCmdToService(Constants.CMD_MSG_SYNC_BRPB_INFO,0,0,null,true);
	}
	
	/**
	 * update play pasue icon
	 */
	public void updateUIPlaypauseIcon(int istatus){
		if(playpause_btn != null){
			if(istatus == Constants.PB_PLAY){
				playpause_btn.setBackgroundResource(R.drawable.play_dwn);
			}else{
				playpause_btn.setBackgroundResource(R.drawable.play);
			}
		}
	}
	
	/**
	 * update top bottom bar status,show /dismiss
	 */
	public void updateUIControlBarStatus(){
        if(IsVisibleControlBar()){
            // TODO Auto-generated method stub
            hideControlDelay(DEFAULT_HIDE_DELAY);
        }else {
			//auto hide delay
            hideControlDelay(DEFAULT_TIMEOUT);
            showController();
        }
	}
}
