package bai4.entities;

public class OrderDetail {
	private int quantity;
	private String color;
	private double price;
	private double discount;
	private double lineTotal; //tinh, khong gan
	private Product product;
	public OrderDetail() {
	}
	public double getLineTotal() {
		//
		this.lineTotal = quantity*price * (1-discount);
		return lineTotal;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	@Override
	public String toString() {
		return "OrderDetail [quantity=" + quantity + ", color=" + color + ", price=" + price + ", discount=" + discount
				+ ", lineTotal=" + getLineTotal() + ", product=" + product + "]";
	}
	
}
