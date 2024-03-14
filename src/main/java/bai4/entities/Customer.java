package bai4.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Customer {
	@BsonId
	private String customerId;
	@BsonProperty("first_name")
	private String firstName;
	@BsonProperty("last_name")
	private String lastName;
	@BsonProperty("phones")
	private List<Phone> phones  = new ArrayList<>();
	private String email;
	@BsonProperty("registration_date")
	private Date registrationDate;
	private Address address;
	
	
	public Customer(String staffId, String firstName, String lastName, List<Phone> phones, String email,
			Date registrationDate) {
		this.customerId = staffId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phones = phones;
		this.email = email;
		this.registrationDate = registrationDate;
	}
	public Customer(String staffId) {
		this.customerId = staffId;
	}
	public Customer() {
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String staffId) {
		this.customerId = staffId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public List<Phone> getPhones() {
		return phones;
	}
	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	@Override
	public String toString() {
		return "Customer [staffId=" + customerId + ", firstName=" + firstName + ", lastName=" + lastName + ", phones="
				+ phones + ", email=" + email + ", registrationDate=" + registrationDate + "]";
	}
	
	
}
