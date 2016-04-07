package com.zhonghong.media.photo;

import com.zhonghong.newphoto.R;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoLoadingFragment extends Fragment {

	private AnimationDrawable loadDrawable;
	private String fwversion;
	private TextView tvVersion;
	private ImageView loadImg;
	private TextView loadmsg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View fragmentView = inflater.inflate(R.layout.photo_loading_fragment, container,false);
		loadImg = (ImageView)fragmentView.findViewById(R.id.loading_bar);
		loadmsg = (TextView)fragmentView.findViewById(R.id.loadmsg);
		loadDrawable = (AnimationDrawable)loadImg.getBackground();
		loadDrawable.start();
		tvVersion = (TextView)fragmentView.findViewById(R.id.marketVersionLabel);
		fwversion = getVersion();
		tvVersion.setText(getString(R.string.verprefix)+" "+fwversion);
		return fragmentView;
	}
	/**
	 * 
	 * @return
	 */
    private String getVersion(){
        PackageInfo pkg;
        String versionName = null;
        try {
            pkg = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String appName = pkg.applicationInfo.loadLabel(getActivity().getPackageManager()).toString(); 
            versionName = pkg.versionName; 
            //System.out.println("appName:" + appName);
            System.out.println("versionName:" + versionName);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return versionName;
    }
    
    /**
     * @author victorchen
     */
    public void stopScanningAnimation(){
		if(loadDrawable != null && loadDrawable.isRunning()){
			loadDrawable.stop();
		}
    }
    
	/**
	 * @author victorchen
	 */
	public void displayNoMusicPrompt(){
		if(loadDrawable != null && loadDrawable.isRunning()){
			loadDrawable.stop();
		}
		loadImg.setVisibility(View.INVISIBLE);
		loadmsg.setVisibility(View.VISIBLE);	
	}
	
	/**
	 * @author victorchen
	 */
	public boolean IsdisplayNoMusicPrompt(){

		if((loadmsg != null) && (loadmsg.getVisibility() == View.VISIBLE)){
			return true;
		}
		return false;
	}
	
	/**
	 * @author victorchen
	 */
	public void displayScanningLoadPrompt(){
		loadmsg.setVisibility(View.INVISIBLE);
		loadImg.setVisibility(View.VISIBLE);
		if(loadDrawable == null){
			loadDrawable = (AnimationDrawable)loadImg.getBackground();
		}
		if(loadDrawable != null ){
			loadDrawable.start();
		}
	}
	/**
	 * @author victorchen
	 */
	public void displayLoadingMusicPrompt(){
		if(loadDrawable != null && !loadDrawable.isRunning()){
			loadDrawable.start();
		}
		loadImg.setVisibility(View.VISIBLE);
		loadmsg.setVisibility(View.INVISIBLE);	
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(loadDrawable != null && loadDrawable.isRunning()){
			loadDrawable.stop();
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(hidden){
			if(loadDrawable != null && loadDrawable.isRunning())
				loadDrawable.stop();
		}else{
			if(loadDrawable != null && !loadDrawable.isRunning())
				loadDrawable.start();
		}		
	}
	

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(loadDrawable != null && !loadDrawable.isRunning())
			loadDrawable.start();
	}
}
