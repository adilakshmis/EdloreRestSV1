package com.cds.watson.util;

/**
 * @author Kiru V
 *
 */
public class WatsonSearchDocument {

	// declare attribute
	private String name;
	private String mimetype;
	private String title;
	private String url;

	// setters and getters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WatsonSearchDocument [name=" + name + ", mimetype=" + mimetype
				+ ", title=" + title + ", url=" + url + "]";
	}

}
