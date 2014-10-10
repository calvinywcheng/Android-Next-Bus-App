package ca.ubc.cpsc210.nextbus.model;

import ca.ubc.cpsc210.nextbus.util.LatLon;

/**
 * Bus location information for a bus serving a particular stop, including route, position (lat/lon),
 * destination, time at which location was last updated.
 */
public class BusLocation {
	private int id;
	private BusRoute route;
	private String dest;
	private String time;
	private LatLon latlon;

	/**
	 * Constructor
	 * @param id	 the vehicle ID number
	 * @param route  the bus route
	 * @param lat    latitude of bus
	 * @param lon    longitude of bus
	 * @param dest   destination
	 * @param time   time at which location was recorded
	 */
	public BusLocation(int id, BusRoute route, double lat, double lon, String dest, String time) {
		this.id = id;
		this.route = route;
		this.dest = dest;
		this.time = time;
		latlon = new LatLon(lat, lon);
	}
	
	/**
	 * Gets the vehicle ID number
	 * @return the bus ID
	 */
	public int getID() {
		return id;
	}

	/**
	 * Gets bus route
	 * @return bus route
	 */
	public BusRoute getRoute() {
		return route;
	}

	/**
	 * Gets bus location as LatLon object
	 * @return bus location 
	 */
	public LatLon getLatLon() {
		return latlon;
	}
	
	/**
	 * Gets destination
	 * @return destination of this bus
	 */
	public String getDestination() {
	    return dest;
	}
	
	/**
	 * Gets time bus location was recorded
	 * @return  time location was recorded
	 */
	public String getTime() {
	    return time;
	}

	/**
	 * Produce a string describing destination and time
	 * that location was captured.  For example:
	 * "Destination: UBC
	 *  Location at: 10:23:42"
	 *  
	 * @return string describing destination and time location was captured
	 */
	public String getDescription() {
		return "Destination: " + dest + "\nLocation at: " + time;
	}
}
