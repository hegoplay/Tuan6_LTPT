package bai4.entities;

public enum OrderStatus {
	NEW("new"),
	IN_PROGRESS("in_progress"),
	COMPLETED("completed"),
	PARTIALLY_SHIPPED("partially_shipped"),
	ON_HOLD("on_hold"),
	CANCELLED("cancelled"),
	AWAITING_EXCHANGE("awaiting_exchange");
	private String status;

	private OrderStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	
	
	
}
