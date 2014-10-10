// DELETE: SUMMER PART 1
package ca.ubc.cpsc210.nextbus.translink;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ca.ubc.cpsc210.nextbus.model.BusRoute;
import ca.ubc.cpsc210.nextbus.util.LatLon;
import ca.ubc.cpsc210.nextbus.util.Segment;

/**
 * Parser for KML files that contain bus route data.
 */
public class KMLParser extends DefaultHandler {
	private StringBuilder sb;
	private BusRoute route;
	private String north;
	private String south;
	private String east;
	private String west;
	
	/**
	 * Constructor
	 * @param route  the route to which parsed route segments are to be added
	 */
	public KMLParser(BusRoute route) {
		this.route = route;
	}

	@Override
	public void endDocument() throws SAXException {
		route.setBounds(Double.parseDouble(north), 
					Double.parseDouble(south), 
					Double.parseDouble(east), 
					Double.parseDouble(west));
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		sb = new StringBuilder();
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals("north"))
			north = sb.toString().trim();
		else if (qName.equals("south"))
			south = sb.toString().trim();
		else if (qName.equals("east"))
			east = sb.toString().trim();
		else if (qName.equals("west"))
			west = sb.toString().trim();
		else if (qName.equals("coordinates"))
			route.addSegment(parseSegment(sb.toString().trim()));
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		sb.append(new String(ch, start, length));
	}

	/**
	 * Parses a single segment of the bus route
	 * @param coordinates  the coordinates of points on this segment
	 * @return the parsed segment
	 */
	private Segment parseSegment(String coordinates) {
		Segment seg = new Segment();
		String[] coords = coordinates.split(" ");

		for (String c : coords) {
			String[] llh = c.split(",");
			LatLon pt = new LatLon(llh[1], llh[0]);
			seg.addPoint(pt);
		}

		return seg;
	}
	
	// END DELETE
}
