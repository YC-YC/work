package com.zhonghong.mediasdk.photo;
import  com.zhonghong.mediasdk.PlaybackStatus;

public interface IPhotoStatusCB{
    
     /**
      *  when photo play error,OnError()处理,callback
      *  @param  errMessage
      */
     void photoPlayErrorWithMsg(String errMessage);
      
      /**
       *  when handle photo info,callback
       */
      void handleMessageInfo(String strMessage);
      
      /**
       * 
       */      
      public void photoPrepared();
      
      /**
       *  when photo completes,callback
       */
      void photoCompleted();
         
      /**
       * destroy video activity
       */
      void photoFinish();
      /**
      *
      */
      void updatePlaybackStatus(PlaybackStatus pbstatus);
}