
package com.zhonghong.mediasdk.photo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zhonghong.mediasdk.photo.SuperImageViewListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MyGifView extends View {
    private static final String TAG = "MyGifView";
    private long movieStart;
    private Movie movie;

    // 此处必须重写该构造方法
    public MyGifView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        // 以文件流（InputStream）读取进gif图片资源
        // movie=Movie.decodeStream(getResources().openRawResource(R.drawable.keyboard));
    }

    public void setPath(String path)
    {
        try {
            movie = Movie.decodeStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i(TAG, "wrong path....");
        }
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        
        if (movie != null) {
            
            long curTime = android.os.SystemClock.uptimeMillis();
            // 第一次播放
            if (movieStart == 0) {
                movieStart = curTime;
            }
            
            int duraction = movie.duration();
            int relTime = (int) ((curTime - movieStart) % duraction);
            movie.setTime(relTime);
            movie.draw(canvas, 0, 0);
            // 强制重绘
            invalidate();
        }
        super.onDraw(canvas);
    }
    
    float touchDownX = 0.0f;
//    float touchDownY = 0.0f;
    SuperImageViewListener showListener = null;
    
    public void setShowImgListener(SuperImageViewListener showListener)
    {
        this.showListener = showListener;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 主点按下
            case MotionEvent.ACTION_DOWN:
                touchDownX = event.getX();
//                touchDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = touchDownX - event.getX();
                if(deltaX >200)
                {
                    if(this.showListener != null)
                    {
                        this.showListener.showNextImg();
//                        changeImage = true;
                    }
                }
                else if(deltaX < -200)
                {
                    if(this.showListener != null)
                    {
                        this.showListener.showPreImg();
//                        changeImage = true;
                    }
                }
                break;
        }
                
        return super.onTouchEvent(event);
    }
    
}
