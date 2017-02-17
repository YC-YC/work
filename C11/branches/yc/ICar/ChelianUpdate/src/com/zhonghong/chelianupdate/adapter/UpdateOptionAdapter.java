package com.zhonghong.chelianupdate.adapter;

import java.util.List;

import com.zhonghong.chelianupdate.R;
import com.zhonghong.chelianupdate.bean.SimpleAppInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UpdateOptionAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private List<SimpleAppInfo> infoList;
	
	public UpdateOptionAdapter(Context context,List<SimpleAppInfo> infoList)
	{
		this.inflater=LayoutInflater.from(context);
		this.infoList=infoList;
	}
	@Override
	public int getCount() {
		return infoList.size();
	}

	@Override
	public Object getItem(int pos) {
		return infoList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup group) {
		ViewHolder holder=null;
		if(convertView==null)
		{
			convertView=inflater.inflate(R.layout.item_update_list_item, null);
			holder=new ViewHolder();
			holder.txvAppName=(TextView) convertView.findViewById(R.id.txv_app_name);
			holder.txvFileSize=(TextView) convertView.findViewById(R.id.txv_app_size);
			holder.txvVersion=(TextView) convertView.findViewById(R.id.txv_app_version);
			holder.txvProgress=(TextView) convertView.findViewById(R.id.txv_progress);
			holder.pbProgress=(ProgressBar) convertView.findViewById(R.id.pb_progress);
			
			holder.txvAppName.setText("升级项目 : "+infoList.get(pos).getAppName());
			holder.txvFileSize.setText("文件大小 : "+toFileSize(infoList.get(pos).getFileSize()));
			holder.txvVersion.setText("版本号 : "+infoList.get(pos).getVersion());
			holder.pbProgress.setMax((int) infoList.get(pos).getFileSize());
			convertView.setTag(holder);
		}
		else
		{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.txvProgress.setText(calculateDownloadProgress(infoList.get(pos).getFileSize(),infoList.get(pos).getDownloaded()));
		holder.pbProgress.setProgress((int) infoList.get(pos).getDownloaded());
		return convertView;
	}
	
	private class ViewHolder
	{
		TextView txvAppName;
		TextView txvVersion;
		TextView txvFileSize;
		TextView txvProgress;
		ProgressBar pbProgress;
	}
	
	private String calculateDownloadProgress(long total,long downloaded)
    {
    	float fProgress=(float)downloaded*100/total;
    	return String.format("%.1f", fProgress)+"%";
    }

	private String toFileSize(long fileLength)
	{
		if (fileLength < 1) {
			return "";
		}
		if (fileLength < 1024) {
			return Long.toString(fileLength) + "b";
		} else if (fileLength < 1024 * 1024) {
			float fl = (float) fileLength / 1024;
			return String.format("%.2f", fl)+"kb";
		} else {
			float fl = (float) fileLength / (1024 * 1024);
			return String.format("%.2f", fl)+"mb";
		}
	}
}
