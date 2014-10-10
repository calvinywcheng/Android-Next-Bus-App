package ca.ubc.cpsc210.nextbus.util;

import java.util.ArrayList;
import java.util.List;

import ca.ubc.cpsc210.nextbus.model.BusRoute;

/**
 * A segment that forms part of a bus route
 * @see BusRoute
 */
public class Segment {
	private List<LatLon> points;

	public Segment() {
		points = new ArrayList<LatLon>();
	}

	public void addPoint(LatLon pt) {
		points.add(pt);
	}

	public List<LatLon> getPoints() {
	    return points;
	}
}
