package com.cds.edlore.util;

/**
 * @author Kiru V
 *
 */
public class UploadManualSatus {

	//declare parameters
	private String file_Id;
	private String status;
	private String message;

	// setters and getters
	public String getFile_Id() {
		return file_Id;
	}

	public void setFile_Id(String file_Id) {
		this.file_Id = file_Id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "UploadManualSatus [file_Id=" + file_Id + ", status=" + status
				+ ", message=" + message + "]";
	}

}
