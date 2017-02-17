package com.zhonghong.chelianupdate.adapter;

import java.io.File;
import java.lang.ref.WeakReference;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zhonghong.chelianupdate.R;
import com.zhonghong.chelianupdate.bean.DownloadInfo;
import com.zhonghong.chelianupdate.utils.DownloadManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadListAdapter extends BaseAdapter{

	private final Context context;
    private final LayoutInflater inflater;
    private DownloadManager downloadManager;
    
    public DownloadListAdapter(Context context,DownloadManager downloadManager)
    {
    	this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.downloadManager=downloadManager;
    }
    
	@Override
	public int getCount() {
		return downloadManager.getDownloadInfoListCount();
	}

	@Override
	public Object getItem(int pos) {
		return downloadManager.getDownloadInfo(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup group) {
		DownloadItemViewHolder holder = null;
        DownloadInfo downloadInfo = downloadManager.getDownloadInfo(pos);
        if (convertView == null) {
        	convertView = inflater.inflate(R.layout.item_download_list_item, null);
            holder = new DownloadItemViewHolder(context, downloadInfo, this);
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
            holder.refresh();
        } else {
            holder = (DownloadItemViewHolder) convertView.getTag();
            holder.update(downloadInfo);
        }
		
        HttpHandler<File> handler = downloadInfo.getHandler();
        if (handler != null) {
            RequestCallBack callBack = handler.getRequestCallBack();
            if (callBack instanceof DownloadManager.ManagerCallBack) {
                DownloadManager.ManagerCallBack managerCallBack = (DownloadManager.ManagerCallBack) callBack;
                if (managerCallBack.getBaseCallBack() == null) {
                    managerCallBack.setBaseCallBack(new DownloadRequestCallBack());
                }
            }
            callBack.setUserTag(new WeakReference<DownloadItemViewHolder>(holder));
        }
		return convertView;
	}

	public class DownloadItemViewHolder {
		@ViewInject(R.id.txv_file_name)
        TextView txvFileName;
		@ViewInject(R.id.txv_downloaded_size)
        TextView txvDownloaded;
		@ViewInject(R.id.txv_progress)
        TextView txvProgress;
		@ViewInject(R.id.pb_progress)
        ProgressBar pbProgress;
		@ViewInject(R.id.btn_changeable)
        Button btnChangeable;
		@ViewInject(R.id.btn_cancel)
        Button btnCancel;

        private DownloadListAdapter downloadListAdapter;
        private DownloadInfo downloadInfo;
        private Context context;
        
        public DownloadItemViewHolder(Context context,DownloadInfo downloadInfo,DownloadListAdapter downloadListAdapter) {
            this.downloadInfo = downloadInfo;
            this.downloadListAdapter=downloadListAdapter;
            this.context=context;
        }

        @OnClick(R.id.btn_changeable)
        public void changeable(View view) {
            HttpHandler.State state = downloadInfo.getState();
            switch (state) {
            	//按钮显示为"文件夹"
            	case SUCCESS:
            	break;
            	
            	//按钮显示为"停止"
                case WAITING:
                case STARTED:
                case LOADING:
                    try {
                        downloadManager.stopDownload(downloadInfo);
                    } catch (DbException e) {
                        LogUtils.e(e.getMessage(), e);
                    }
                    break;
                
                //按钮显示为"继续"
                case CANCELLED:
                case FAILURE:
                    try {
                        downloadManager.resumeDownload(downloadInfo, new DownloadRequestCallBack());
                    } catch (DbException e) {
                        LogUtils.e(e.getMessage(), e);
                    }
                    downloadListAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }

        @OnClick(R.id.btn_cancel)
        public void remove(View view) {
            try {
                downloadManager.removeDownload(downloadInfo);
                downloadListAdapter.notifyDataSetChanged();
            } catch (DbException e) {
                LogUtils.e(e.getMessage(), e);
            }
        }

        public void update(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
            refresh();
        }

        public void refresh() {
            txvFileName.setText(downloadInfo.getFileName());
            txvDownloaded.setText(calculateDownloadedSize(downloadInfo.getProgress()));
            if (downloadInfo.getFileLength() > 0) {
                pbProgress.setProgress((int) (downloadInfo.getProgress() * 100 / downloadInfo.getFileLength()));
                txvProgress.setText(calculateDownloadProgress(downloadInfo.getFileLength(), downloadInfo.getProgress()));
            } else {
            	pbProgress.setProgress(0);
            	txvProgress.setText("0%");
            }

            btnChangeable.setVisibility(View.VISIBLE);
            btnChangeable.setText(context.getString(R.string.stop));
            HttpHandler.State state = downloadInfo.getState();
            switch (state) {
                case WAITING:
                    btnChangeable.setText(context.getString(R.string.stop));
                    break;
                case STARTED:
                	btnChangeable.setText(context.getString(R.string.stop));
                    break;
                case LOADING:
                	btnChangeable.setText(context.getString(R.string.stop));
                    break;
                case CANCELLED:
                	btnChangeable.setText(context.getString(R.string.resume));
                    break;
                case SUCCESS:
                	btnChangeable.setText(context.getString(R.string.open_file_path));
                    break;
                case FAILURE:
                	btnChangeable.setText(context.getString(R.string.retry));
                    break;
                default:
                    break;
            }
        }
    }

    public static class DownloadRequestCallBack extends RequestCallBack<File> {
    	
        @SuppressWarnings("unchecked")
        private void refreshListItem() {
            if (userTag == null) return;
            WeakReference<DownloadItemViewHolder> tag = (WeakReference<DownloadItemViewHolder>) userTag;
            DownloadItemViewHolder holder = tag.get();
            if (holder != null) {
                holder.refresh();
            }
        }

        @Override
        public void onStart() {
            refreshListItem();
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            refreshListItem();
        }

        @Override
        public void onSuccess(ResponseInfo<File> responseInfo) {
            refreshListItem();
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            refreshListItem();
        }

        @Override
        public void onCancelled() {
            refreshListItem();
        }
    }
    private String calculateDownloadProgress(long total,long downloaded)
    {
    	float fProgress=(float)downloaded*100/total;
    	return String.format("%.1f", fProgress)+"%";
    }
    
    private String calculateDownloadedSize(long downloaded)
    {
    	float fDownloaded=(float)downloaded/1048576;
    	return String.format("%.2f", fDownloaded)+" MB";
    }
}