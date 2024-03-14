package bai4.dao;

public enum CollectionsList {
	orders("orders"),
	customers("customers"),
	staffs("staffs"),
	products("products");
	
	private String name;
	private CollectionsList(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
}
