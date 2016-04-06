/**
 * 
 */
package com.zhcl.media;

import java.io.File;
import android.annotation.SuppressLint;
import com.zh.dao.SongInfo;
import com.zh.uitls.L;
import com.zh.uitls.Utils;

/**
 * @author chenli
 *
 */
public class SongInfoFactory extends ObjectFactory{
	private static String tag = "SongInfoFactory";
	/**
	 * 根据id和type构造songInfo对象
	 * @param id
	 * @param type
	 */
	public static SongInfo CreateSongInfo(long id, int type){
		return new SongInfo(id, type);
	}
	
	
	/**
	 * 创建songInfo，自行解析好id3
	 * @param path
	 * @return
	 */
	public static SongInfo CreateSongInfo(String path){
		int id = SongInfo.makeId(path);
		int type = 6;
		SongInfo songInfo = CreateSongInfo(id, type);
		ID3 id3 = ID3Factory.getID3(path);
		if("".equals(id3.getTitle())){ 
			File file = new File(path);
			String name = file.getName();
			try{ 
				songInfo.setTitle(name.substring(0,name.lastIndexOf(".")));
			}catch(Exception e){
				L.e(tag, "title :" + name + " path = " + path); 
				e.printStackTrace();
			}
		}else{
			songInfo.setTitle(id3.getTitle());
		}
		songInfo.setAlbum(id3.getAlbum());
		songInfo.setSinger(id3.getArtist());
		songInfo.setFileName(path);  
		songInfo.setPinyin(Utils.getInstance().getPinYin(songInfo.getTitle()));
//		L.i(tag, "id3.getTitle() = " + id3.getTitle());
//		L.i(tag, "id3.getAlbum() = " + id3.getAlbum());
//		L.i(tag, "id3.getArtist() = " + id3.getArtist());
		return songInfo;
	}
}
