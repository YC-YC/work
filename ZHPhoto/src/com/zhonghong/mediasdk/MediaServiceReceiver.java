/* //device/content/providers/media/src/com/android/providers/media/MediaScannerReceiver.java
**
** Copyright 2007, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/

package com.zhonghong.mediasdk;

//import com.zhonghong.mediasdk.music.MusicService;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.net.Uri;
import android.util.Log;

public class MediaServiceReceiver extends BroadcastReceiver
{
    private final static String TAG = "MusicServiceReceiver";
    private final static MediaContainer mMediaCont = MediaContainer.getInstance();
    @Override
    public void onReceive(Context context, Intent intent) {
			String path = null;
			String action = intent.getAction();
			Uri uri = intent.getData();
			if(uri != null){
				path = uri.getPath();
			}
			Log.d(TAG, "====action: " + action + " path: " + path);  			
			//autostart media servcie
//			context.startService(new Intent(context,MusicService.class));
			/*if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
				context.startService(new Intent(context,MusicService.class));
			}
			else*/ if(action.equals(Intent.ACTION_MEDIA_MOUNTED)){
				if( path!=null && IsCareMountedPath(path) ){
					mMediaCont.addMountedDeviceToList(path);
				}
			}else if(action.equals(Intent.ACTION_MEDIA_EJECT)){
				if( path!=null && IsCareMountedPath(path) ){
					mMediaCont.delUnmountedDeviceFromList(path);
				}
			}
    }  
    
    /**
     * 
     * @param path
     * @return
     */
    private boolean IsCareMountedPath(String path){
    	if(mMediaCont.translateMountPathToDeviceID(path) != Constants.INVALID_DEVICE){
    		return true;
    	}else{
    		return false;
    	}
    }
}


