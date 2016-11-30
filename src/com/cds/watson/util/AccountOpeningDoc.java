package com.cds.watson.util;

/**
 * @author Kiru V
 *
 * pojo class for AccountOpeningDoc to map all properties and content to filent doc
 */
public class AccountOpeningDoc {

	// declaring the all attributes 
	
	private String nameAsinNRIC;
	private String dateOfBirth;	
	private String countryOfBirth;
	private String gender;	
	private String citizen;	
	private String countryOfResidency;
	private String nationality;
	private String NRIC;
	private String maritalStatus;
	private String numberOfDependents;
	private String homeNumber;
	private String mobileNumber;
	private String officeNumber;
	private String emailAddress;
	private String address;
	private String floorNo;
	private String unitNo;
	private String streetName;
	private String postalCode;
	private String occupation;
	private String companyName;
	private String lengthOfService;
	private String monthlyIncome;
	
	// setters and getters 
	public String getNameAsinNRIC() {
		return nameAsinNRIC;
	}
	public void setNameAsinNRIC(String nameAsinNRIC) {
		this.nameAsinNRIC = nameAsinNRIC;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getCountryOfBirth() {
		return countryOfBirth;
	}
	public void setCountryOfBirth(String countryOfBirth) {
		this.countryOfBirth = countryOfBirth;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getCitizen() {
		return citizen;
	}
	public void setCitizen(String citizen) {
		this.citizen = citizen;
	}
	public String getCountryOfResidency() {
		return countryOfResidency;
	}
	public void setCountryOfResidency(String countryOfResidency) {
		this.countryOfResidency = countryOfResidency;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getNRIC() {
		return NRIC;
	}
	public void setNRIC(String nRIC) {
		NRIC = nRIC;
	}
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public String getNumberOfDependents() {
		return numberOfDependents;
	}
	public void setNumberOfDependents(String numberOfDependents) {
		this.numberOfDependents = numberOfDependents;
	}
	public String getHomeNumber() {
		return homeNumber;
	}
	public void setHomeNumber(String homeNumber) {
		this.homeNumber = homeNumber;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getOfficeNumber() {
		return officeNumber;
	}
	public void setOfficeNumber(String officeNumber) {
		this.officeNumber = officeNumber;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getFloorNo() {
		return floorNo;
	}
	public void setFloorNo(String floorNo) {
		this.floorNo = floorNo;
	}
	public String getUnitNo() {
		return unitNo;
	}
	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getLengthOfService() {
		return lengthOfService;
	}
	public void setLengthOfService(String lengthOfService) {
		this.lengthOfService = lengthOfService;
	}
	public String getMonthlyIncome() {
		return monthlyIncome;
	}
	public void setMonthlyIncome(String monthlyIncome) {
		this.monthlyIncome = monthlyIncome;
	}
	
	// To string to display the data properly 
	@Override
	public String toString() {
		return "AccountOpeningDoc [nameAsinNRIC=" + nameAsinNRIC
				+ ", dateOfBirth=" + dateOfBirth + ", countryOfBirth="
				+ countryOfBirth + ", gender=" + gender + ", citizen="
				+ citizen + ", countryOfResidency=" + countryOfResidency
				+ ", nationality=" + nationality + ", NRIC=" + NRIC
				+ ", maritalStatus=" + maritalStatus + ", numberOfDependents="
				+ numberOfDependents + ", homeNumber=" + homeNumber
				+ ", mobileNumber=" + mobileNumber + ", officeNumber="
				+ officeNumber + ", emailAddress=" + emailAddress
				+ ", address=" + address + ", floorNo=" + floorNo + ", unitNo="
				+ unitNo + ", streetName=" + streetName + ", postalCode="
				+ postalCode + ", occupation=" + occupation + ", companyName="
				+ companyName + ", lengthOfService=" + lengthOfService
				+ ", monthlyIncome=" + monthlyIncome + "]";
	}
	
	
	
}
