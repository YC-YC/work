package com.zhonghong.media.photo.adapter;

import java.util.List;

import com.zhonghong.newphoto.R;
import com.zhonghong.mediasdk.Constants;
import com.zhonghong.mediasdk.FolderItemInfo;
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

public class PFolderListBaseAdapter extends BaseAdapter {
	private static final String TAG = "VFolderListBaseAdapter";
	private Context context;
	private int iSelectedItem = -1;
	
	public PFolderListBaseAdapter(Context context){
		super();
		this.context = context;
	}

	@Override
	public int getCount() {
		return PhotoServiceHelper.getBrowserFolderItemCount();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View converView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		FolderItemInfo fitinfo = null;
		
		if(converView == null){
			converView = LayoutInflater.from(context).inflate(R.layout.photo_folder_item, null);		
			viewHolder = new ViewHolder(converView);
			converView.setTag(viewHolder);
			
		}else{
			viewHolder = (ViewHolder) converView.getTag();
		}
		
		fitinfo = PhotoServiceHelper.LoadFolderItemOnPos(-1, position);		
		if(fitinfo != null && viewHolder != null){
			viewHolder.setName(fitinfo.sFolderName);
		}
		
		if(position == PhotoServiceHelper.getBrowserFocusFolderIndex()){
            viewHolder.setPressedState(PhotoServiceHelper.getBrowserDeviceID());
        }
        else{
            viewHolder.setDefaultState(PhotoServiceHelper.getBrowserDeviceID());
        }
		
		return converView;
	}
	
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 
	 * @author victorchen
	 *
	 */
	public class ViewHolder{
		private View baseView;
		private TextView txfoldername;
		private ImageView imgfoldericon;
		
		public ViewHolder(View baseview)
        {
            this.baseView = baseview;
        }
		
		public void setName(String foldername){			
			 if( txfoldername == null){
				 txfoldername = (TextView)baseView.findViewById(R.id.video_folder_txt);
	          }
			 txfoldername.setText(foldername);
		}
		
		public void setIcon(){			
			 if( imgfoldericon == null){
				 imgfoldericon = (ImageView)baseView.findViewById(R.id.video_folder_icon);
	          }
			 //imgfoldericon.setBackgroundResource(resid);
		}
		public void setPressedState(int idevid){
			baseView.setBackgroundResource(R.drawable.left_list_pressed);
		}
		
		public void setDefaultState(int idevid){
			baseView.setBackgroundResource(R.drawable.left_list_dispressed);
		}
	}
}
