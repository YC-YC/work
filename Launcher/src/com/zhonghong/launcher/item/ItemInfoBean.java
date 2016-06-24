/**
 * 
 */
package com.zhonghong.launcher.item;

/**
 * @author YC
 * @time 2016-6-24 下午3:35:41
 * TODO:菜单项信息
 */
public class ItemInfoBean {

	private String itemText;
	private String itemImgId;

	public ItemInfoBean(String itemText, String itemImgId) {
		super();
		this.itemText = itemText;
		this.itemImgId = itemImgId;
	}

	public String getItemText() {
		return itemText;
	}

	public String getItemImgId() {
		return itemImgId;
	}
	
	
}
