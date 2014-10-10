package ca.ubc.cpsc210.nextbus.translink;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import ca.ubc.cpsc210.exception.TranslinkException;
import ca.ubc.cpsc210.nextbus.model.BusStop;

public abstract class AbstractTranslinkService implements ITranslinkService {

	public AbstractTranslinkService() {
		super();
	}

	/**
	 * Parses estimated wait times from response received from Translink service and adds them to 
	 * associated bus stop.
	 * @param is  input source built with response from Translink service
	 * @param stop   bus stop associated with bus locations
	 * @throws TranslinkException 
	 */
	protected void parseWaitTimesFromXML(InputSource is, BusStop stop) 
			throws TranslinkException {
		
		BusWaitTimeParser waitTimeParser;
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();

			XMLReader reader = parser.getXMLReader();

			waitTimeParser = new BusWaitTimeParser(stop);
			reader.setContentHandler(waitTimeParser);
			reader.parse(is);
		} catch (Exception e) {
			// Convert other exception types to TranslinkException so clients do not
            // have to worry about the different possibilities.
            throw new TranslinkException(-1, e.getMessage());
		}
		
		if(waitTimeParser.receivedError()) {
			throw new TranslinkException(waitTimeParser.getErrorCode(), waitTimeParser.getMessage());
		}
	}

	/**
	 * Parses bus locations from response received from Translink service and adds them to 
	 * associated bus stop.
	 * @param is  input source built with response from Translink service
	 * @param stop   bus stop associated with bus locations 
	 * @throws TranslinkException 
	 */
	protected void parseBusLocationsFromXML(InputSource is, BusStop stop) 
			throws TranslinkException {

		AbstractTranslinkParser locationParser;
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();

			XMLReader reader = parser.getXMLReader();

			locationParser = new BusLocationParser(stop);
			reader.setContentHandler(locationParser);
			reader.parse(is);
		} catch (Exception e) {
			// Convert other exception types to TranslinkException so clients do not
            // have to worry about the different possibilities.
            throw new TranslinkException(-1, e.getMessage());
		}
		
		if(locationParser.receivedError()) {
			throw new TranslinkException(locationParser.getErrorCode(), locationParser.getMessage());
		}
	}
	
	/**
	 * Parse bus stop data from response received from Translink web service
	 * @param is   input source built with response from Translink web service
	 * @return  bus stop parsed from input
	 * @throws TranslinkException 
	 */
	protected BusStop parseBusStopFromXML(InputSource is) throws TranslinkException { 
		BusStopParser stopParser;
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();

			XMLReader reader = parser.getXMLReader();

			stopParser = new BusStopParser();
			reader.setContentHandler(stopParser);
			reader.parse(is);
		} catch (Exception e) {
			// Convert other exception types to TranslinkException so clients do not
            // have to worry about the different possibilities.
            throw new TranslinkException(-1, e.getMessage());
		}
		
		if(stopParser.receivedError()) {
			throw new TranslinkException(stopParser.getErrorCode(), stopParser.getMessage());
		}
		
		return stopParser.getParsedStop();
	}
}