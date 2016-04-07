package com.zhonghong.mediasdk;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
/**
 *
 */
public class BaseData implements Parcelable,Cloneable{

    private String name;
    private String path;
    private String parentPath;
    private String size;
    private String format;
    private String description;
    private String artist;
    private String title;
    private long modifyTime;
    private long id = 0;
    private long album = 0;
    private int albumBitmapLength;  
    private byte[] albumBitmap;
    //file type
    private int type;
    //photo id
    private int icon;
    private int _duration;
    //duration time
    private String duration;  
    //id3 is reay
    private boolean isId3Ready;

    public BaseData() {
    }

    public BaseData(int type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the parentPath
     */
    public String getParentPath() {
        return parentPath;
    }

    /**
     * @param parentPath the parentPath to set
     */
    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    /**
     * @return the size
     */
    public String getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    public void setDuration(int duration) {
        this._duration = duration;
        this.duration = Tools.formatSecDuration2(duration);
    }

    public int getDuration() {
        return _duration;
    }

    public String getDuration2() {
        return duration;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the modifyTime
     */
    public long getModifyTime() {
        return modifyTime;
    }

    /**
     * @param modifyTime the modifyTime to set
     */
    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * @return the icon
     */
    public int getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(int icon) {
        this.icon = icon;
    }

    /**
     * @return the artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * @param artist the artist to set
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the album
     */
    public long getAlbum() {
        return album;
    }

    /**
     * @param album the album to set
     */
    public void setAlbum(long album) {
        this.album = album;
    }

    public void setAlbumBitmap(byte[] bm) {
        this.albumBitmap = bm;
        albumBitmapLength = albumBitmap.length;
    }
    
    public byte[] getAlbumBitmap() {
        return albumBitmap;
    }
    
    public void setID3ReadyFlag(boolean id3flag){
    	this.isId3Ready = id3flag;
    }
    
    public boolean getID3ReadyFlag(){
    	return this.isId3Ready;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(parentPath);
        dest.writeString(size);
        dest.writeString(format);
        dest.writeString(artist);
        dest.writeLong(modifyTime);
        dest.writeLong(id);
        dest.writeLong(album);
        dest.writeInt(_duration);
        dest.writeInt(albumBitmapLength);
        dest.writeByteArray(albumBitmap);
    }

    public static final Parcelable.Creator<BaseData> CREATOR = new Parcelable.Creator<BaseData>() {
        @Override
        public BaseData createFromParcel(Parcel source) {
            BaseData file = new BaseData();
            file.name = source.readString();
            file.path = source.readString();
            file.parentPath = source.readString();
            file.size = source.readString();
            file.format = source.readString();
            file.artist = source.readString();
            file.modifyTime = source.readLong();
            file.id = source.readLong();
            file.album = source.readLong();
            file._duration = source.readInt();
            file.albumBitmapLength = source.readInt();
            if(file.albumBitmapLength > 0)
            {
            	file.albumBitmap = new byte[file.albumBitmapLength];
            	source.readByteArray(file.albumBitmap);
            }
            else
            {
            	file.albumBitmap = null;
            }

            return file;
        }

        @Override
        public BaseData[] newArray(int size) {
            return new BaseData[size];
        }
    };
    
	/**
	 * @author wendan
	 */
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		BaseData baseData = null;

		try {
			baseData = (BaseData)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return baseData;
	}
}
