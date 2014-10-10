package ca.ubc.cpsc210.nextbus.translink;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import ca.ubc.cpsc210.exception.TranslinkException;
import ca.ubc.cpsc210.nextbus.model.BusRoute;
import ca.ubc.cpsc210.nextbus.model.BusStop;

public class BusStopParser extends AbstractTranslinkParser {
	private StringBuilder sb;
	private BusStop busStop;
	private int stopNum;
	private String name;
	private double lat;
	private double lon;
	private Set<BusRoute> routes;
	private boolean hasLat = false;
	private boolean hasLon = false;
	
	/**
	 * Constructor
	 */
	public BusStopParser() {
		busStop = null;
		isError = false;
		stopNum = -1;
		name = null;
		hasLat = false;
		hasLon = false;
	}
	
	/**
	 * Produces the bus stop parsed from XML data
	 * @return  bus stop
	 * @throws TranslinkException if bus stop could not be parsed from data.  Specifically
	 * when the bus stop number, name, latitude or longitude is missing.
	 */
	public BusStop getParsedStop() throws TranslinkException {
		if (busStop == null) 
			throw new TranslinkException(-1, "Unable to parse stop data.");
		return busStop;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		sb = new StringBuilder();	
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		
		if (stopNum != -1 && name != null && hasLat && hasLon)
			busStop = new BusStop(stopNum, name, lat, lon, routes);
	}
	
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		
		String data = sb.toString().trim();
		
		if(qName.equals("StopNo"))
			stopNum = Integer.parseInt(data);
		else if(qName.equals("Name"))
			name = data;
		else if(qName.equals("Latitude")) {
			lat = Double.parseDouble(data);
			hasLat = true;
		}
		else if(qName.equals("Longitude")) {
			lon = Double.parseDouble(data);
			hasLon = true;
		}
		else if(qName.equals("Routes"))
			routes = buildRoutesFromString(data);
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
	
	/**
	 * Parses set of bus routes from comma-delimited string of route names.
	 * @param routesAsString  string of route names, separated by a comma
	 * @return set of bus routes
	 */
	private Set<BusRoute> buildRoutesFromString(String routesAsString) {
	    Set<BusRoute> routes = new HashSet<BusRoute>();
	    StringTokenizer tokenizer = new StringTokenizer(routesAsString, ",");
	    
	    while(tokenizer.hasMoreTokens()) {
	        String next = tokenizer.nextToken();
	        BusRoute route = new BusRoute(next.trim());
	        routes.add(route);
	    }
	    
	    return routes;      
	}
	
}
