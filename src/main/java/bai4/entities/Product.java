package bai4.entities;

import java.util.List;
import java.util.Objects;

import org.bson.Document;

public class Product {
	private long productId;
	private String brand;
	private String category;
	private String name;
	private List<String> colors;
	private int modelYear;
	private double price;
	
	public Product(long productId, String brand, String category, String name, List<String> colors, int modelYear,
			double price) {
		this.productId = productId;
		this.brand = brand;
		this.category = category;
		this.name = name;
		this.colors = colors;
		this.modelYear = modelYear;
		this.price = price;
	}
	public Product(long productId) {
		this.productId = productId;
	}
	public Product() {
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(productId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		return productId == other.productId;
	}
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getColors() {
		return colors;
	}
	public void setColors(List<String> colors) {
		this.colors = colors;
	}
	public int getModelYear() {
		return modelYear;
	}
	public void setModelYear(int modelYear) {
		this.modelYear = modelYear;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	@Override
	public String toString() {
		return "Product [productId=" + productId + ", brand=" + brand + ", category=" + category + ", name=" + name
				+ ", colors=" + colors + ", modelYear=" + modelYear + ", price=" + price + "]";
	}
	public Document convertToDoc() {
		Document d = new Document();
		d.append("_id", (int)getProductId());
		d.append("brand_name", getBrand());
		d.append("category_name", getCategory());
		d.append("colors", getColors());
		d.append("model_year", getModelYear());
		d.append("product_name", getName());
		d.append("price",getPrice());
		return d;
	}
	
}
