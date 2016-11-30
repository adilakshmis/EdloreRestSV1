package com.cds.edlore.util;

import java.io.Serializable;

public class UploadManual implements Serializable {

	/**
	 * Kiru V
	 */
	private static final long serialVersionUID = 1L;

	// Declaring the attributes
	private String id;
	private String upload_file_name;
	private String resource_type;
	private String manual_url;

	public UploadManual() {
		// default constructor
	}

	// Setters and getters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUpload_file_name() {
		return upload_file_name;
	}

	public void setUpload_file_name(String upload_file_name) {
		this.upload_file_name = upload_file_name;
	}

	public String getResource_type() {
		return resource_type;
	}

	public void setResource_type(String resource_type) {
		this.resource_type = resource_type;
	}

	public String getManual_url() {
		return manual_url;
	}

	public void setManual_url(String manual_url) {
		this.manual_url = manual_url;
	}

	@Override
	public String toString() {
		return "UploadManual [id=" + id + ", upload_file_name="
				+ upload_file_name + ", resource_type=" + resource_type
				+ ", manual_url=" + manual_url + "]";
	}

}
