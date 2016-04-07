package com.zhonghong.media.photo.adapter;

import java.util.List;
import com.zhonghong.mediasdk.Constants;
import com.zhonghong.mediasdk.photo.PhotoItemInfo;
import com.zhonghong.mediasdk.photo.PhotoAsyncDataLoader;
import com.zhonghong.mediasdk.photo.PhotoServiceHelper;
import com.zhonghong.newphoto.R;
import com.zhonghong.media.util.UserTools;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PFileListBaseAdapter extends BaseAdapter {
	private static final String TAG = "FileListBaseAdapter";
	private Context context;
	private int iSelectedItem = -1;
	
	public PFileListBaseAdapter(Context context){
		super();
		this.context = context;
	}

	@Override
	public int getCount() {
		return PhotoServiceHelper.getBrowserFocusFolderItemCount();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View converView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		PhotoItemInfo mitinfo = null;
		
		if(converView == null){
			converView = LayoutInflater.from(context).inflate(R.layout.video_listview_item, null);		
			viewHolder = new ViewHolder(converView);
			converView.setTag(viewHolder);
			
		}else{
			viewHolder = (ViewHolder) converView.getTag();
		}
		
		mitinfo = PhotoServiceHelper.loadPhotoItemInfoOnPos(-1,position,(Object)viewHolder, new PhotoAsyncDataLoader.FinishedCallback(){
				@Override
				public void finishedLoaded(Object vh,PhotoItemInfo mItemInfo){
					Log.d(TAG,"finishedLoaded ");
					setViewHolderData((ViewHolder)vh,mItemInfo);
				}
			}
		);
		Log.d(TAG,"mitinfo "+mitinfo);
		if(mitinfo != null && viewHolder != null){
			setViewHolderData(viewHolder,mitinfo);
		}

		if(position == PhotoServiceHelper.getBrowserFocusFileIndex()){
            viewHolder.setPressedState(PhotoServiceHelper.getBrowserDeviceID());
        }
        else{
            viewHolder.setDefaultState(PhotoServiceHelper.getBrowserDeviceID());
        }
		
		return converView;
	}
	
	private void setViewHolderData(ViewHolder vh,PhotoItemInfo mItemInfo){
		if(vh == null || mItemInfo == null){
			Log.d(TAG,"setViewHolderData [FAIL],vh "+vh+" mItemInfo "+mItemInfo);
			return;
		}
		vh.setName(mItemInfo.sName);
		vh.setIcon(mItemInfo.aPhotoThumbnail);
		//vh.setTime(UserTools.formatSecDuration2(mItemInfo.iTotalTime));
	}
	
	abstract class VHolder {
        View baseView;
        public abstract boolean setPressedState(int deviceid);
        public abstract boolean setDefaultState(int deviceid);
        public abstract boolean setName(String name);
    }
	
	public class ViewHolder extends VHolder{
		private View baseView = null;
		private ImageView video_pic = null;
		private TextView videoname = null;
		private TextView time = null;
		
		public ViewHolder(View baseview)
        {
            this.baseView = baseview;
        }
		
		@Override
		public boolean setPressedState(int deviceid) {
			setItemBg(deviceid);
			return true;
		}
		@Override
		public boolean setDefaultState(int deviceid) {
			setItemBg2(deviceid);
			return false;
		}
		
		 public void setItemBg(int deviceid){
	            if(baseView == null){
	                return;
	            }
	            
	            switch(deviceid){
	                case Constants.USB_DEVICE:	                	
	                    //baseView.setBackgroundResource(R.drawable.blue_play);
	                    break;
	                case Constants.SDCARD_DEVICE:
	                   // baseView.setBackgroundResource(R.drawable.green_play);
	                    break;
	                case Constants.INTERNAL_DEVICE:
	                  //  baseView.setBackgroundResource(R.drawable.orange_play);
	                    break;
	                default:
	                  //  baseView.setBackgroundResource(R.drawable.horizontal_list);
	            }          
	            baseView.setBackgroundResource(R.drawable.right_list_pressed);
	     }
		 
		 public void setItemBg2(int deviceid){
			 if(baseView == null){
	                return;
	         }
			 
            switch(deviceid){
                case Constants.USB_DEVICE:                	
                    //baseView.setBackgroundResource(R.drawable.music_item_usb_sel);
                    break;
                case Constants.SDCARD_DEVICE:
                   // baseView.setBackgroundResource(R.drawable.music_item_sd_sel);
                    break;
                case Constants.INTERNAL_DEVICE:
                   // baseView.setBackgroundResource(R.drawable.music_item_hdd_sel);
                    break;
                default:
                  //  baseView.setBackgroundResource(R.drawable.horizontal_list);
            }
            baseView.setBackgroundResource(R.drawable.right_list_dispressed);
		 }
		 
		 @Override
		public boolean setName(String name) {
			 if( videoname == null){
				 videoname = (TextView)baseView.findViewById(R.id.listview_videoname_tv);
	         }
			 videoname.setText(name);
	         return true;
		}
		 
		 public void setTime(String musictime){
			 if (time == null) {
				time = (TextView) baseView.findViewById(R.id.listview_videotime_tv);
			 }
			 time.setText(musictime);
		 }
		 
		 public void setIcon (Bitmap bm){	
			 if(video_pic == null){
				 video_pic = (ImageView) baseView.findViewById(R.id.listvideo_item_pic);	
			 }
	         if(bm != null) {
	        	 video_pic.setImageBitmap(bm);  
	         }else{						
	        	 video_pic.setImageResource(R.drawable.default_video_pic);
	         }
		}		 
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
