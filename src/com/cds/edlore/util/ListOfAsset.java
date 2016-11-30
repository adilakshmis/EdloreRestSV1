package com.cds.edlore.util;

import java.util.List;

/**
 * @author Kiru V
 *
 */
public class ListOfAsset {
	
	//declare attributes
	private String folderName;
	private List<Asset> listAsset;

	// setters and getters
	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public List<Asset> getListAsset() {
		return listAsset;
	}

	public void setListAsset(List<Asset> listAsset) {
		this.listAsset = listAsset;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return " [folderName=" + folderName + ", listAsset="
				+ listAsset + "]";
	}

}
