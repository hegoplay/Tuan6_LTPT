package bai4.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Order {
	@BsonProperty("order_status")
	OrderStatus orderStatus;
	@BsonProperty("shipping_address")
	Address address;
	@BsonProperty("customer")
	Customer customer;
	Staff staff;
	@BsonProperty("order_details")
	List<OrderDetail> orderDetails = new ArrayList<>();
	@BsonId
	private String orderID;
	@BsonProperty("order_date")
	private Date orderDate;
	@BsonProperty("shipped_date")
	private Date shippedDate;
	@BsonProperty("order_total")
	private double orderTotal;
	public Order() {
	}
	

	public Order(OrderStatus orderStatus, Date orderDate) {
		super();
		this.orderStatus = orderStatus;
		this.orderDate = orderDate;
	}



	public double getOrderTotal() {
		orderTotal = 0;
		for(OrderDetail detail : orderDetails) {
			orderTotal+= detail.getLineTotal();
		}
		return orderTotal;
	}


	@Override
	public int hashCode() {
		return Objects.hash(orderID);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		return Objects.equals(orderID, other.orderID);
	}


	public OrderStatus getOrderStatus() {
		return orderStatus;
	}


	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}


	public Address getAddress() {
		return address;
	}


	public void setAddress(Address address) {
		this.address = address;
	}


	public Customer getCustomer() {
		return customer;
	}


	public void setCustomer(Customer customer) {
		this.customer = customer;
	}


	public Staff getStaff() {
		return staff;
	}


	public void setStaff(Staff staff) {
		this.staff = staff;
	}


	public List<OrderDetail> getOrderDetails() {
		return orderDetails;
	}


	public void setOrderDetails(List<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}


	public String getOrderID() {
		return orderID;
	}


	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}


	public Date getOrderDate() {
		return orderDate;
	}


	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}


	public Date getShippedDate() {
		return shippedDate;
	}


	public void setShippedDate(Date shippedDate) {
		this.shippedDate = shippedDate;
	}



	@Override
	public String toString() {
		return "Order [orderStatus=" + orderStatus.getStatus() + ", address=" + address + ", customer=" + customer + ", staff="
				+ staff + ", orderDetails=" + orderDetails + ", orderID=" + orderID + ", orderDate=" + orderDate
				+ ", shippedDate=" + shippedDate + ", orderTotal=" + orderTotal + "]";
	}
	
}
