/**
 * 
 */
package com.zhonghong.recordinfo;

import java.io.Serializable;

/**
 * @author YC
 * @time 2016-6-22 下午3:22:06
 * TODO:标记应用的包名，类名
 */
public class RecordInfoBean implements Serializable{
	/**包名*/
	private String lastPkgName;
	/**类名*/
	private String lastClassName;

	public RecordInfoBean() {
	}

	public RecordInfoBean(String pkgName, String className) {
		super();
		this.lastPkgName = pkgName;
		this.lastClassName = className;
	}


	public String getLastPkgName() {
		return lastPkgName;
	}

	public String getLastClassName() {
		return lastClassName;
	}

	public void setLastPkgName(String lastPkgName) {
		this.lastPkgName = lastPkgName;
	}

	public void setLastClassName(String lastClassName) {
		this.lastClassName = lastClassName;
	}

	@Override
	public String toString() {
		return "RecordInfoBean [lastPkgName=" + lastPkgName
				+ ", lastClassName=" + lastClassName + "]";
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecordInfoBean other = (RecordInfoBean) obj;
		if (lastClassName == null) {
			if (other.lastClassName != null)
				return false;
		} else if (!lastClassName.equals(other.lastClassName))
			return false;
		if (lastPkgName == null) {
			if (other.lastPkgName != null)
				return false;
		} else if (!lastPkgName.equals(other.lastPkgName))
			return false;
		return true;
	}

	
	
	
}
