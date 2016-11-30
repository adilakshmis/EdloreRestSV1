package com.cds.accopen.util;

public class AccountOpeninFileInfo {

	/**
	 *  Author Kiru V
	 */
	// declaring the attributes
	private String fileName;
	private String url;

	// setters and getters
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "AccountOpeninFileInfo [fileName=" + fileName + ", url=" + url
				+ "]";
	}

}
