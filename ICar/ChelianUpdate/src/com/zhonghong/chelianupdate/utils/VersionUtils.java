package com.zhonghong.chelianupdate.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class VersionUtils {
	private static final String VERSION_KEY_OS="OSVer";
	private static final String VERSION_KEY_APP="APPVer";
	private static final String VERSION_KEY_MCU="MCUVer";
	private static final String VERSION_KEY_CAN="CANVer";
	private static final String VERSION_KEY_AIR="AIRVer";
	private static final String VERSION_KEY_8836="8836Ver";
	private static final String TAG = "VersionUtils";
	
	public static boolean isOSVersionValid(Context context,String newVer)
	{
		if(newVer==null)
			Log.i("Upgrade","new version aaaaa");
		String oldVer=Settings.System.getString(context.getContentResolver(), VERSION_KEY_OS);
		return isNewVerValid(newVer,oldVer);
	}
	public static boolean isAPPVersionValid(Context context,String newVer)
	{
		String oldVer=Settings.System.getString(context.getContentResolver(), VERSION_KEY_APP);
		return isNewVerValid(newVer,oldVer);
	}
	public static boolean isMCUVersionValid(Context context,String newVer)
	{
		String oldVer=Settings.System.getString(context.getContentResolver(), VERSION_KEY_MCU);
		return isNewVerValid(newVer,oldVer);
	}
	public static boolean isCANVersionValid(Context context,String newVer)
	{
		String oldVer=Settings.System.getString(context.getContentResolver(), VERSION_KEY_CAN);
		return isNewVerValid(newVer,oldVer);
	}
	public static boolean isAIRVersionValid(Context context,String newVer)
	{
		String oldVer=Settings.System.getString(context.getContentResolver(), VERSION_KEY_AIR);
		return isNewVerValid(newVer,oldVer);
	}
	public static boolean is8836VersionValid(Context context,String newVer)
	{
		String oldVer=Settings.System.getString(context.getContentResolver(), VERSION_KEY_8836);
		return isNewVerValid(newVer,oldVer);
	}
	
	public static boolean isNewVerValid(String newVer,String oldVer)
	{
		Log.i(TAG, "newVer = " + newVer + ", oldVer = " + oldVer);
		if(oldVer==null)
		{
			return true;
		}
		Pattern p=Pattern.compile("([0-9]+\\.)+[0-9]+");  
        Matcher m1=p.matcher(oldVer); 
        String versionCode="";
        if(m1.find()){  
        	versionCode=m1.group();
        	System.out.println(versionCode);
        } 
        if(versionCode==null||versionCode.trim().equals(""))
        {
        	return true;
        }
        Matcher m2=p.matcher(newVer);
        if(m2.find()){  
        	newVer=m2.group();
        } 
        else{
        	return false;
        }
        return (compareVersion(newVer,versionCode)==1);
	}
	
	private static int compareVersion(String versionValA,String versionValB)
	{
		String[] piecesA=versionValA.split("\\.");
		String[] piecesB=versionValB.split("\\.");
		for(int i=0;i<piecesA.length&&i<piecesB.length;i++)
		{
			if(Integer.parseInt(piecesA[i])>Integer.parseInt(piecesB[i]))
			{
				return 1;
			}
			else if(Integer.parseInt(piecesA[i])<Integer.parseInt(piecesB[i]))
			{
				return -1;
			}
		}
		if(piecesA.length>piecesB.length)
		{
			return 1;
		}
		else if(piecesA.length<piecesB.length)
		{
			return -1;
		}	
		return 0;
	}
}
