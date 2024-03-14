package bai2.entities;

public class Location {
	 private float x;
	 private float y;
	public Location(float x, float y) {
//		super();
		this.x = x;
		this.y = y;
	}
	public Location() {
		
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	@Override
	public String toString() {
		return "Location [x=" + x + ", y=" + y + "]";
	}
	 
}
