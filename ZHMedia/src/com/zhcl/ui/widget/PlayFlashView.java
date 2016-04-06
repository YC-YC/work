package com.zhcl.ui.widget;

import java.util.Random;

import com.zh.uitls.L;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class PlayFlashView extends View{
	private static final String tag = PlayFlashView.class.getSimpleName();
	private int picthNum = 0;   //音调高低
	private int index=0;  //用来设置每0.3S重新获取一组 随机数
	int[] nowIndex= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	int[] lastIndex= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	int COUNT=32;
	int columnWidth=0;
	 // 创建画笔  
    Paint p = new Paint();  
    Paint p2 = new Paint();
    /* 创建一个缓冲区 */  
    Bitmap  mSCBitmap = null;  
    DisplayMetrics dm;
    int width=0;
    int height=0;
    
	public PlayFlashView(Context context) {
		super(context);
		init();
	}

	public PlayFlashView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init(){
		p.setColor(Color.WHITE);// 白色
		p2.setColor(Color.LTGRAY/*Color.GRAY*/);//灰色
		p.setStyle(Paint.Style.FILL);//设置填满 
		p2.setStyle(Paint.Style.FILL);//设置填满 
		p.setAntiAlias(true);  
        p.setDither(true); 
        p2.setAntiAlias(true);  
        p2.setDither(true); 
		p.setAlpha(255);  //设置透明度
		p2.setAlpha(80);  //设置透明度
		
		p.setShadowLayer(1, 1, 1, 0xFF000000);  
		p2.setShadowLayer(1, 1, 1, 0xFF000000);  
		picthNum=0;		
		dm=new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		width=dm.widthPixels;
		height=dm.heightPixels;
		columnWidth=(width-30)/COUNT-5;
		//System.out.println("columnWidth          "+columnWidth);
//		initScreen();
	};
	
	/** 每个柱子宽，包含间隙 */
	private float unitW;
	/** 间隙大小 */
	private int mixW = 3;
	/** 频谱最大数 */
	private float V_MAX = 128.0f;
	/** 每一小格的高度 */
	private float unitH;
	/** 倒影最小一个的高度 */
	private float cloneUnitH;
	int maxW;
	int maxH;
	/**
	 * 设置柱子个数
	 * @param COUNT
	 */
	private void setCount(int COUNT){
		this.COUNT = COUNT;
	}
	
	
	
	/**
	 * 计算每个柱子长度和宽度
	 */
	private void computer(){
//		if(unitW != 0){
//			return;
//		}
		int maxW = getMeasuredWidth();
		int maxH = getMeasuredHeight() * 2 / 3;
		cloneUnitH = (getMeasuredHeight() - maxH) / V_MAX;
		unitW = maxW / COUNT;
		unitH = maxH / V_MAX;
	}
	
	private void createBitMap(Canvas canvas){
//		computer();
		int maxW = getMeasuredWidth();
		int maxH = getMeasuredHeight() * 3 / 4;
		cloneUnitH = (getMeasuredHeight() - maxH) / V_MAX;
		unitW = maxW / COUNT;
		unitH = maxH / V_MAX;
		if(picthNum > 0){   //如果当前音调>0
			for(int i = 0; i < COUNT; i++){		 		
				if(nowIndex[i] > 0){   //画柱子和横线			
					canvas.drawRect(unitW * i + mixW,  maxH - unitH * nowIndex[i], unitW * (i + 1) - mixW , maxH , p);
					canvas.drawRect(unitW * i + mixW,  maxH , unitW * (i + 1) - mixW , maxH + cloneUnitH * nowIndex[i], p2);
					//canvas.drawRect(unitW * i + mixW,  maxH, unitW * (i + 1) - mixW , maxH - unitH * nowIndex[i], p);
//					canvas.drawRect(everyWidth*i+15, Yheight, everyWidth*i+columnWidth+15, Yheight+(nowIndex[i]*60)/100, p2);
					if(lastIndex[i] < nowIndex[i]){
						canvas.drawLine(unitW * i + mixW, maxH - unitH * nowIndex[i], unitW * (i + 1) - mixW, maxH - unitH * nowIndex[i], p);
						lastIndex[i]=nowIndex[i];
					}else if(lastIndex[i]-nowIndex[i]>7){
						lastIndex[i]=lastIndex[i]-2;
						canvas.drawLine(unitW * i + mixW, maxH - unitH * lastIndex[i], unitW * (i + 1) -mixW, maxH - unitH * lastIndex[i], p);
					}
				} else if(nowIndex[i] <= 0){
					if(lastIndex[i] > 2){
					    lastIndex[i] = lastIndex[i]-2;
						canvas.drawLine(unitW * i + mixW, maxH - unitH * lastIndex[i], unitW * (i + 1) -5, maxH - unitH * lastIndex[i], p);						
					}else{
						lastIndex[i]=0;
						canvas.drawLine(unitW * i + mixW, maxH, unitW * (i + 1) -5, maxH, p);				
						}
					  }						
				   }
				} else if(picthNum<=0){    //如果音调小于=0,则停止画柱子,只画横线		   
				   for(int i=0;i<COUNT;i++){
					   if(lastIndex[i]>2){
						    lastIndex[i]=lastIndex[i]-2;
						    canvas.drawLine(unitW * i + mixW, maxH - unitH * lastIndex[i], unitW * (i + 1) -5, maxH - unitH * lastIndex[i], p);		
						}else{
							lastIndex[i]=0;
							canvas.drawLine(unitW * i + mixW, maxH, unitW * (i + 1) -5, maxH, p);						
						}
				  }		
			 }
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
//		super.onDraw(canvas);
//		initScreen();
		createBitMap(canvas);
//		canvas.drawBitmap(mSCBitmap, 0, 0, new Paint());
		
	}
	
	
	private void setIndexList(){
		index++;
		if(index >= 2){
			index=0;
			Random r=new Random();
			for(int i=0;i<COUNT;i++){
				int num=picthNum+r.nextInt(100)-50;
				if(num > 0){
					nowIndex[i]=num;
				}else{
					nowIndex[i]=0;
				}
			}				
		}
   }
	
	/**
	 * 更新
	 * @param data
	 */
	public void updata(String[] data){
		for(int i = 0; i < COUNT; i++){
			nowIndex[i] = Integer.parseInt(data[i + 1]);
		}
		setCount(data.length - 1);
		picthNum = 1;
		postInvalidate();
//		invalidate();
	}
	
//	public void  updatePicthNum(int picthNum){
//		this.picthNum=picthNum;
//		new Thread(new puThread()).start();
//	}
//	
//	class puThread implements Runnable{
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			try {
//				Thread.sleep(300);
//				if(picthNum>1){
//					picthNum=picthNum-1;
//					
//				}else{
//					picthNum=0;
//				}
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//	}
}
