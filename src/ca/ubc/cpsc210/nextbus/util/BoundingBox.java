package ca.ubc.cpsc210.nextbus.util;

public class BoundingBox {
	private double north;
	private double south;
	private double east;
	private double west;

	public BoundingBox() {
		north = 90.0;
		south = -90.0;
		east = 180.0;
		west = -180.0;
	}

	public BoundingBox(double n, double s, double e, double w) {
		north = n;
		south = s;
		east = e;
		west = w;
	}

	public double getNorth() {
		return north;
	}

	public double getSouth() {
		return south;
	}

	public double getEast() {
		return east;
	}

	public double getWest() {
		return west;
	}
}
