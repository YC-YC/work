/**
 * 
 */
package com.zhonghong.mediascanerlib.filebean;

/**
 * @author YC
 * @time 2016-12-9 下午2:55:08
 * TODO:
 */
public abstract class FileInfo {
	/**路径*/
	private String path;
	/**文件名*/
	private String name;
	/**上次修改时间*/
	private long modifiled;
	/**文件大小*/
	private long size;
	
	public enum FileType{
		TYPE_AUDIO,
		TYPE_VIDEO,
		TYPE_IMAGE,
		TYPE_OTHER;
	}
	
	public FileInfo(String path, String name, long modifiled, long size) {
		super();
		this.path = path;
		this.name = name;
		this.modifiled = modifiled;
		this.size = size;
	}
	
//	public abstract String getDBTableName();
	
	public long getId(){
		return (modifiled + size);
	}
	
	public String getPath() {
		return path;
	}
	public String getName() {
		return name;
	}
	public long getModifiled() {
		return modifiled;
	}
	public long getSize() {
		return size;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().toString() + " [path=" + path + ", name=" + name + ", modifiled="
				+ modifiled + ", size=" + size + "]";
	}
	
	
}
