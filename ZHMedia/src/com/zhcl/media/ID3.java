/**
 * 
 */
package com.zhcl.media;

/**
 * id3信息类，需作为sonInfo的成员变量
 * @author chenli
 * 
 */
public class ID3 {
	private String title = "";
	private String artist = "未知歌手";
	private String album = "未知专辑";
	private boolean noTitle = true;
	private boolean noArtist = true;
	private boolean noAlbum = true;

	public String getTitle() {
		if(this.noTitle){
			return "";
		}
		return title;
	}

	public String getAlbum() {
		if (this.noAlbum) {
			return "未知专辑";
		}
		return album;
	}

	public String getArtist() {
		if (this.noArtist) {
			return "未知歌手";
		}
		return artist;
	}

	public void setTitle(String title) {
		if ((title == null) || (title.trim().length() == 0)
				|| (title.trim().equals("unknown"))
				|| (title.trim().equals("<unknown>"))) {
			this.noTitle = true;
			return;
		}
		this.noTitle = false;
		// 可能需要对title进行一点字符替换处理
		this.title = title.trim();
		this.title = this.title.replace("rkutf8", ""); //rk机器才需要，手机上暂时无异常
	}

	public void setAlbum(String album) {
		if ((album == null) || (album.trim().length() == 0)
				|| (album.trim().equals("unknown"))
				|| (album.trim().equals("<unknown>"))) {
			this.noAlbum = true;
			return;
		}
		this.noAlbum = false;
		this.album = album.trim();
		this.album = this.album.replace("rkutf8", ""); //rk机器才需要，手机上暂时无异常
	}

	public void setArtist(String artist) {
		if ((artist == null) || (artist.trim().length() == 0)
				|| (artist.trim().equals("unknown"))
				|| (artist.trim().equals("<unknown>"))) {
			this.noArtist = true;
			return;
		}
		this.noArtist = false;
		this.artist = artist.trim();
		this.artist = this.artist.replace("rkutf8", ""); //rk机器才需要，手机上暂时无异常
	}

	
	public boolean isNoTitle() {
		return noTitle;
	}

	public boolean isNoArtist() {
		return noArtist;
	}

	public boolean isNoAlbum() {
		return noAlbum;
	}

	public String toString() {
		return "ID3{title='" + this.title + '\'' + ", artist='" + this.artist
				+ '\'' + ", album='" + this.album + '\'' + ", titleEmpty="
				+ this.noTitle + ", artistEmpty=" + this.noArtist
				+ ", albumEmpty=" + this.noAlbum + '}';
	}
	
}
