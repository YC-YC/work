/**
 * 
 */
package com.zhonghong.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * @author YC
 * @time 2016-4-9 下午5:04:30
 * 加载字体库
 */
public class FontsUtils {

	private static Typeface EXPANSIVA;
	private static Typeface ruizibiger;
	
	/**加载EXPANSIVA字体*/
	public static Typeface getExpansivaTypeface(Context context)
	{
		/*File file = new File("/ResidentFlash/ZuiLauncher/data/fonts/digital-7-italic.ttf");
		if (file.exists())
		{
			Typeface typeface = Typeface.createFromFile(file);
			mTvCurFreq.setTypeface(typeface);
		}*/
		if (EXPANSIVA == null)
		{
			EXPANSIVA = Typeface.createFromAsset(context.getAssets(), "fonts/EXPANSIVA.OTF");
		}
		return EXPANSIVA;
	}
	
	/**加载“锐字逼格”字体*/
	public static Typeface getRuiZiBiGerTypeface(Context context)
	{
		if (ruizibiger == null)
		{
			ruizibiger = Typeface.createFromAsset(context.getAssets(), "fonts/ruizibigeruixian_jian_4.0.TTF");
		}
		return ruizibiger;
	}
}
