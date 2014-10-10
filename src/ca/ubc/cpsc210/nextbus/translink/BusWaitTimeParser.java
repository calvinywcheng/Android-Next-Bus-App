package ca.ubc.cpsc210.nextbus.translink;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import ca.ubc.cpsc210.nextbus.model.BusRoute;
import ca.ubc.cpsc210.nextbus.model.BusStop;
import ca.ubc.cpsc210.nextbus.model.BusWaitTime;

public class BusWaitTimeParser extends AbstractTranslinkParser {
	private StringBuilder sb;
	private BusStop busStop;
	private BusRoute route;
	private int expectedCountdown;
	private boolean cancelledTrip;
	private boolean cancelledStop;
	private boolean hasCountdown;
	
	/**
	 * Constructor
	 * 
	 * @param busStop  the bus stop to which wait time estimates must be added
	 */
	public BusWaitTimeParser(BusStop busStop) {
		this.busStop = busStop;
		isError = false;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		sb = new StringBuilder();
		
		if(qName.equals("NextBus")) {
			route = null;	
		}
		else if(qName.equals("Schedule")) {
			expectedCountdown = 0;
			hasCountdown = false;
			cancelledTrip = false;
			cancelledStop = false;
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
		else if(qName.equals("ExpectedCountdown")) {
			expectedCountdown = Integer.parseInt(data);
			hasCountdown = true;
		}
		else if(qName.equals("CancelledTrip")) {
			cancelledTrip = Boolean.parseBoolean(data);
		}
		else if(qName.equals("CancelledStop")) {
			cancelledStop = Boolean.parseBoolean(data);
		}
		else if(qName.equals("Schedule")) {
			boolean isCancelled = cancelledTrip || cancelledStop;
			
			// add to wait times only if we parsed a route and an expected countdown - don't care about cancelled status (default false)
			if (hasCountdown && route != null) {
				BusWaitTime bwt = new BusWaitTime(route, expectedCountdown, isCancelled);
				busStop.addWaitTime(bwt);
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
