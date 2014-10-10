package ca.ubc.cpsc210.nextbus.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A latitude/longitude coordinate pair
 */
public class LatLon {
	private double lat;
	private double lon;

	public LatLon(String lat, String lon) {
		this.lat = Double.parseDouble(lat);
		this.lon = Double.parseDouble(lon);
	}

	public LatLon(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	public LatLon(JSONObject json) throws JSONException {
		this(json.getString("Latitude"), json.getString("Longitude"));
	}

	public double getLatitude() {
		return lat;
	}

	public double getLongitude() {
		return lon;
	}

	public String toString() {
		return lat + ", " + lon;
	}
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LatLon other = (LatLon) obj;
        if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
            return false;
        if (Double.doubleToLongBits(lon) != Double.doubleToLongBits(other.lon))
            return false;
        return true;
    }

    /**
	 * Produces JSONObject from this lat/lon
	 * @return  the JSONObject containing lat and lon
	 * @throws JSONException 
	 */
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("Latitude", Double.toString(lat));
		json.put("Longitude", Double.toString(lon));
		return json;
	}
}
