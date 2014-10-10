package ca.ubc.cpsc210.nextbus.translink;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import ca.ubc.cpsc210.nextbus.model.BusLocation;
import ca.ubc.cpsc210.nextbus.model.BusRoute;
import ca.ubc.cpsc210.nextbus.model.BusStop;

public class BusLocationParser extends AbstractTranslinkParser {
	private StringBuilder sb;
	private BusStop busStop;
	private BusRoute route;
	private String mapURL;
	private double lat;
	private double lon;
	private String destination;
	private String recordedTime;
	private BusLocation bl;
	private boolean hasLat;
	private boolean hasLon;
	private int id;
	
	/**
	 * Constructor
	 * 
	 * @param busStop  the bus stop to which bus location information will be added
	 */
	public BusLocationParser(BusStop busStop) {
		this.busStop = busStop;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		sb = new StringBuilder();
		
		if(qName.equals("Bus")) {
			route = null;
			mapURL = null;
			destination = null;
			recordedTime = null;
			bl = null;
			lat = lon = 0.0;
			hasLat = hasLon = false;
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
	
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		
		String data = sb.toString().trim();
		
		if(qName.equals("RouteNo")) {
			String routeName = data;
			route = busStop.getRouteNamed(routeName);	
		}
		else if(qName.equals("Href")) {
			mapURL = data;
		}
		else if(qName.equals("Latitude")) {
			lat = Double.parseDouble(data);
			hasLat = true;
		}
		else if(qName.equals("Longitude")) {
			lon = Double.parseDouble(data);
			hasLon = true;
		}
		else if(qName.equals("Destination")) {
			destination = data;
		}
		else if(qName.equals("RecordedTime")) {
			recordedTime = data;
		}
		else if(qName.equals("VehicleNo")) {
			id = Integer.parseInt(data);
		}
		else if(qName.equals("Bus")) {
			// add bus location if route is not null and has a recorded latitude and longitude
			if (route != null) {
				route.setMapURL(mapURL);
				if (hasLat && hasLon) {
					bl = new BusLocation(id, route, lat, lon, destination, recordedTime);
					busStop.addBusLocation(bl);
				}
			}
		}
		else if(qName.equals("Code")) 
			code = Integer.parseInt(data);
		else if(qName.equals("Message"))
			message = data;
		else if(qName.equals("Error"))
			isError = true;
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		
		sb.append(new String(ch, start, length));
	}
}
