package com.cds.edlore.util;

/**
 * @author Kiru V
 *
 */
public class Asset {

	// declare attributes
	private String upload_file_name;
	private String download_url;

	// setters and getters
	public String getUpload_file_name() {
		return upload_file_name;
	}

	public void setUpload_file_name(String upload_file_name) {
		this.upload_file_name = upload_file_name;
	}

	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Asset [upload_file_name=" + upload_file_name
				+ ", download_url=" + download_url + "]";
	}
	
}
