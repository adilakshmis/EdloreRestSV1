package com.cds.edlore.util;

import java.util.List;

/**
 * @author Kiru V
 *
 */
public class BrowseUrl {

	// declare attributes
	private String deviceName;
	private List<String> listOfModelNames;

	// setters and getters
	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public List<String> getListOfModelNames() {
		return listOfModelNames;
	}

	public void setListOfModelNames(List<String> listOfModelNames) {
		this.listOfModelNames = listOfModelNames;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BrowseUrl [deviceName=" + deviceName + ", listOfModelNames="
				+ listOfModelNames + "]";
	}

}
