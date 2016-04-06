/**
 * 
 */
package com.zhcl.Adapter;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhcl.dao.VideoDir;
import com.zhonghong.zhvideo.R;

/**
 * 所有歌曲文件夹列表adapter
 * @author ChenLi
 */
public class AllVideoDirListAdapter extends BaseAdapter {
	private static final String tag = "AllVideoDirListAdapter";
	Vector<VideoDir> allVideoDir;
	Context context;
	/**
	 * 得到一个LayoutInfalter对象用来导入布局
	 */
	private LayoutInflater mInflater; 

	public AllVideoDirListAdapter(Context context){
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}
	
	public void setallVideoDir(Vector<VideoDir> allVideoDir){
		this.allVideoDir = allVideoDir;
	}
	
	@Override
	public int getCount() {
		if(allVideoDir != null){
			return allVideoDir.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int index) {
		if(allVideoDir != null){
			allVideoDir.get(index);
		}
		return null;
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if(allVideoDir == null){
			return convertView;
		}
		  ViewHolder holder;
          if (convertView == null) {
              convertView = mInflater.inflate(R.layout.video_dir_list_item, null);
              holder = new ViewHolder();
              /*得到各个控件的对象*/
              holder.title = (TextView) convertView.findViewById(R.id.name);
              holder.info = (TextView) convertView.findViewById(R.id.info);
              convertView.setTag(holder); //绑定ViewHolder对象
          } else {
              holder = (ViewHolder) convertView.getTag(); //取出ViewHolder对象
          }
          VideoDir videodir = allVideoDir.get(position);
          /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
          holder.title.setText(videodir.getSubName());
          holder.info.setText(videodir.getVideoDirSet().size() + "首" + ", " + videodir.getdirPath());
          return convertView;
	}

	
	 /*存放控件 的ViewHolder*/
    public final class ViewHolder {
        public TextView title;
        public TextView info;
    }

}
