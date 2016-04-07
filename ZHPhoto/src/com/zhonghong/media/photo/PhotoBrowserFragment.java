package com.zhonghong.media.photo;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.zhonghong.media.photo.adapter.PFileGridBaseAdapter;
import com.zhonghong.mediasdk.Constants;
import com.zhonghong.newphoto.R;

public class PhotoBrowserFragment extends Fragment {

	protected static final String TAG = "PhotoBrowserFragment";

	private View mPhotoListFragment = null;
	//cut by hj
/*	private Button usb = null;
	private Button sd = null;
	private Button hdd = null;*/
	private Button list_sel_btn = null;
	//cut by hj
	private TwoWayGridView photo_gridview = null;
/*	private ListView folder_list = null;
	private ViewPager grid_list_viewpager = null;*/
	private TextView nodata_prompt = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mPhotoListFragment = inflater.inflate(R.layout.photo_browser_fragment_my, container,false);	
		return mPhotoListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
		setListener();
	}
	
	private void initView()
	{
        LayoutInflater mInflater = getActivity().getLayoutInflater();
      //cut by hj
       /* folder_list = (ListView) mPhotoListFragment.findViewById(R.id.photo_folder_list);
        folder_list.setAdapter(new PFolderListBaseAdapter(getActivity()));
        
        photo_gridview = (GridView) mPhotoListFragment.findViewById(R.id.grid_photo);
        photo_gridview.setAdapter(new PFileGridBaseAdapter(getActivity()));
        nodata_prompt = (TextView)mPhotoListFragment.findViewById(R.id.nodata);
            
        usb = (Button) mPhotoListFragment.findViewById(R.id.usb);
        sd = (Button) mPhotoListFragment.findViewById(R.id.sd);
        hdd = (Button) mPhotoListFragment.findViewById(R.id.hdd);*/
        
        photo_gridview = (TwoWayGridView) mPhotoListFragment.findViewById(R.id.grid_photo);
        photo_gridview.setSelector(new ColorDrawable(Color.GRAY));
        photo_gridview.setAdapter(new PFileGridBaseAdapter(getActivity()));
        nodata_prompt = (TextView)mPhotoListFragment.findViewById(R.id.nodata);
        list_sel_btn = (Button)mPhotoListFragment.findViewById(R.id.photo_list_sel);
        
	}
	private void setListener(){
		//cut by hj
		/*usb.setOnClickListener(new DeviceListener());
		sd.setOnClickListener(new DeviceListener());
		hdd.setOnClickListener(new DeviceListener());		
		folder_list.setOnItemClickListener(m_FolderListClickListener);
		photo_gridview.setOnItemClickListener(m_FileListClickListener);*/
		list_sel_btn.setOnClickListener(new DeviceListener());
		//photo_gridview.setOnItemClickListener(m_FileListClickListener);
		
		photo_gridview.setOnItemClickListener(new TwoWayAdapterView.OnItemClickListener() {
			public void onItemClick(TwoWayAdapterView parent, View v, int position, long id) {
				((PhotoLauncher) getActivity()).sendCmdToService(Constants.CMD_MSG_SEL_FILE,position,0,null,false);
			}
		});
	}
	
    /***
     * @author victorchen
     */
//	protected TwoWayAdapterView.OnItemClickListener m_FileListClickListener = new TwoWayAdapterView.OnItemClickListener() {
//		public void onItemClick(TwoWayAdapterView parent, View v, int position, long id) {
//			((PhotoLauncher) getActivity()).sendCmdToService(Constants.CMD_MSG_SEL_FILE,position,0,null,false);
//		}
//	};
	
	/**
	 * When select the grid item click listener
	 */	
	protected OnItemClickListener m_FolderListClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			Log.d(TAG, "m_FolderListClickListener");
			((PhotoLauncher) getActivity()).sendCmdToService(Constants.CMD_MSG_SEL_FOLDER,position,0,null,false);
		}
	}; // end m_ListClickListener

	private class DeviceListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int iDevid = -1;
			switch (v.getId()) {
			case R.id.usb:
				iDevid = Constants.USB_DEVICE;
				break;
			case R.id.sd:
				iDevid = Constants.SDCARD_DEVICE;
				break;
			case R.id.hdd:
				iDevid = Constants.INTERNAL_DEVICE;
				break;
			case R.id.photo_list_sel:
				
				break;
			}
			
			//clear older on file prompt
			//cut by hj
			if(nodata_prompt != null){
				nodata_prompt.setVisibility(View.INVISIBLE);
			}
			
			if(iDevid != -1){
				((PhotoLauncher) getActivity()).sendCmdToService(Constants.CMD_MSG_SEL_DEV,iDevid,0,null,true);
			}
		}	
	}
	/**********************************************************************************************/
	/**********************************************************************************************/
	/**
	 * @author victorchen
	 * 
	 */
	public void updateUIBrowserDevNoFile(int idevid){
		Log.d(TAG,"Display no file found on current dev "+idevid);
		if(nodata_prompt != null){
			nodata_prompt.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * @author victorchen
	 * {@code} notify data change,refresh device list ui
	 */
	public void updateUIBrowserDeviceListRefresh(int idevid){
		//cut by hj
		/*
		sd.setBackgroundResource(R.drawable.sd_dispressed);
		usb.setBackgroundResource(R.drawable.usb_dispressed);
		hdd.setBackgroundResource(R.drawable.hdd_dispressed);
		if(idevid == Constants.SDCARD_DEVICE){
			sd.setBackgroundResource(R.drawable.sd_pressed);
		}else if(idevid == Constants.USB_DEVICE){
			usb.setBackgroundResource(R.drawable.usb_pressed);
		}else if(idevid == Constants.INTERNAL_DEVICE){
			hdd.setBackgroundResource(R.drawable.hdd_pressed);
		}else{
			Log.d(TAG,"updateUIBrowserDeviceListRefresh [FAIL],invalid dev:"+idevid);
		}
	*/}
	/**
	 * @author victorchen
	 * {@code} notify data change,refresh ui
	 */
	public void updateUIBrowserFolderListRefresh(int ifocusindex){
		//cut by hj
		/*if(folder_list != null){
			folder_list.smoothScrollToPosition(ifocusindex);
			//folder_list.setSelection(ifocusindex);
			((BaseAdapter)folder_list.getAdapter()).notifyDataSetChanged();
		}*/
	}
	
	/**
	 * @author victorchen
	 * {@code} notify data change,refresh ui
	 */
	public void updateUIBrowserFileListRefresh(int ifocusindex){
		/*if(video_listview != null){
			video_listview.smoothScrollToPosition(ifocusindex);
			//music_list.setSelection(ifocusindex);
			((BaseAdapter)video_listview.getAdapter()).notifyDataSetChanged();
		}*/
		if(photo_gridview != null){
			photo_gridview.smoothScrollToPosition(ifocusindex);
			//music_grid.setSelection(ifocusindex);
			((BaseAdapter)photo_gridview.getAdapter()).notifyDataSetChanged();
		}
	}
}
