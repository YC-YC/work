package com.zhonghong.media.photo.adapter;

import java.util.List;
import com.zhonghong.newphoto.R;
import com.zhonghong.mediasdk.photo.PhotoAsyncDataLoader;
import com.zhonghong.mediasdk.photo.PhotoItemInfo;
import com.zhonghong.mediasdk.photo.PhotoServiceHelper;

import android.content.Context;
import android.graphics.Bitmap;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PFileGridBaseAdapter extends BaseAdapter  {

	private static final String TAG = "FileGridBaseAdapter";
    private Context contex;

	
	public PFileGridBaseAdapter(Context contex){
		super();
		this.contex = contex;
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
		
		if(converView==null){
			converView = LayoutInflater.from(contex).inflate(R.layout.photo_gridview_item, null);
			viewHolder = new ViewHolder(converView);		
			converView.setTag(viewHolder);			
		}else{
			viewHolder = (ViewHolder) converView.getTag();
		}
		mitinfo = PhotoServiceHelper.loadPhotoItemInfoOnPos(-1,position,(Object)viewHolder, new PhotoAsyncDataLoader.FinishedCallback(){
				@Override
				public void finishedLoaded(Object vh,PhotoItemInfo mItemInfo){
					Log.d(TAG,"finishedLoaded===========");
					setViewHolderData((ViewHolder)vh,mItemInfo);
				}
			}
		);
	
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
	}
	
	 abstract class VHolder {
	        View baseView;
	        public abstract boolean setPressedState(int idevid);
	        public abstract boolean setDefaultState(int idevid);
	        public abstract boolean setName(String name);
	    }
	 
	 class ViewHolder extends VHolder {
		
		private ImageView imageview = null;
		private TextView textView = null;
		
		public ViewHolder(View baseView){
			this.baseView = baseView;
		}

		@Override
		public boolean setPressedState(int idevid) {
			return true;
		}
		@Override
		public boolean setDefaultState(int idevid) {
			return true;
		}
		
		@Override
		public boolean setName(String name) {
			if(textView == null){
				textView = (TextView) baseView.findViewById(R.id.griditem_name);
			}
			textView.setText(name);
			return true;
		}
		
		
		public void setIcon (Bitmap bm){
			if(imageview == null){
				imageview = (ImageView) baseView.findViewById(R.id.griditem_pic);
			}
	        if(bm != null){	        	
	            imageview.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(bm));
	        }else{
				imageview.setImageResource(R.drawable.default_photo);
	        }
		}
	 }

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
