package ca.ubc.cpsc210.nextbus.translink;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import ca.ubc.cpsc210.exception.ConnectionException;
import ca.ubc.cpsc210.exception.TranslinkException;
import ca.ubc.cpsc210.nextbus.model.BusRoute;
import ca.ubc.cpsc210.nextbus.model.BusStop;

/**
 * Wrapper around a service which gets real time bus information from Translink.
 */
public class TranslinkService extends AbstractTranslinkService {
	/**
	 * Set timeouts on connection and data acquisition from Translink
	 */
	private static int CONNECT_TIMEOUT = 5000;
	private static int READ_TIMEOUT = 5000;
	
	/**
	 * Translink API key - must be included with any request for data from Translink service
	 */
	private final static String APIKEY = "RkQfKuNOIF4IX7OCgm6M";  // insert your Translink key here
	
	/**
	 * For filtering LogCat messages
	 */
	private final static String LOG_TAG = "TranslinkService";

	/**
	 * Associated Android activity
	 */
	private Activity activity;
	
	/**
	 * HTTP connection client
	 */
	private HttpURLConnection client;

	public TranslinkService(Activity activity) {
		this.activity = activity;
		client = null;
	}
	
	/* (non-Javadoc)
     * @see ca.ubc.cpsc210.nextbus.translink.ITranslinkService#addWaitTimeEstimatesToStop(ca.ubc.cpsc210.nextbus.model.BusStop)
     */
	@Override
    public void addWaitTimeEstimatesToStop(BusStop stop) throws TranslinkException {
		stop.getWaitTimes().clear();
		StringBuilder uriBuilder = new StringBuilder(
				"//api.translink.ca/RTTIAPI/V1/");
		uriBuilder.append("stops/" + stop.getStopNum() + "/estimates?");
		uriBuilder.append("apikey=" + APIKEY);
		uriBuilder.append("&count=3&timeframe=60");

		try {
			InputSource is = makeXMLQuery(uriBuilder);
			
			parseWaitTimesFromXML(is, stop);
		} finally {
			if(client != null) {
				client.disconnect();
				client = null;
			}
		}
	}

	/* (non-Javadoc)
     * @see ca.ubc.cpsc210.nextbus.translink.ITranslinkService#getBusLocationsForStop(ca.ubc.cpsc210.nextbus.model.BusStop)
     */
	@Override
    public void addBusLocationsForStop(BusStop stop)
			throws TranslinkException {
		stop.getBusLocations().clear();
		StringBuilder uriBuilder = new StringBuilder(
				"//api.translink.ca/RTTIAPI/V1/");
		uriBuilder.append("buses?");
		uriBuilder.append("apikey=" + APIKEY);
		uriBuilder.append("&stopNo=" + stop.getStopNum());

		try {
			InputSource is = makeXMLQuery(uriBuilder);
			
			parseBusLocationsFromXML(is, stop);
		} finally {
			if(client != null) {
				client.disconnect();
				client = null;
			}
		}
	}
	

	/* (non-Javadoc)
     * @see ca.ubc.cpsc210.nextbus.translink.ITranslinkService#getBusStop(java.lang.String)
     */
	@Override
    public BusStop getBusStop(String stopNum) throws TranslinkException {
		StringBuilder uriBuilder = new StringBuilder(
				"//api.translink.ca/RTTIAPI/V1/");
		uriBuilder.append("stops/" + stopNum + "?");
		uriBuilder.append("apikey=" + APIKEY);

		try {
			InputSource is = makeXMLQuery(uriBuilder);
			
			return parseBusStopFromXML(is);
		} finally {
			if(client != null) {
				client.disconnect();
				client = null;
			}
		}
	}
	
	/**
	 * Execute a given XML query 
	 * 
	 * @param urlBuilder The query with everything but http:
	 * @return The input source built from client response 
	 * @throws TranslinkException  
	 * 				when an error occurs trying to connect or get data
	 * 				from Translink service
	 */
	private InputSource makeXMLQuery(StringBuilder urlBuilder) throws TranslinkException {
		
		try {
			checkConnection();
			
			URL url = new URL("http:" + urlBuilder.toString());
			client = (HttpURLConnection) url.openConnection();
			client.setConnectTimeout(CONNECT_TIMEOUT);
			client.setReadTimeout(READ_TIMEOUT);
			client.connect();
			
			InputSource is;
			InputStream err = client.getErrorStream();
			if( err != null )
				is = new InputSource(err);
			else {
				InputStream in = client.getInputStream();
				is = new InputSource(in);
			}
			
			return is;
		} catch (SocketTimeoutException e) {
			throw new TranslinkException(-1, "Unable to connect to Translink at this time");
		} catch (ConnectionException e) {
			throw new TranslinkException(-1, "Data not available: check network connection");
		} catch (Exception e) {
			throw new TranslinkException(-1, "Failed to get data from Translink service");
		}
	}
	

	/**
	 * Get route information from KMZ file at given URL and add to bus route
	 * 
	 * @Param route  bus route to which parsed route data is to be added
	 * 
	 * @throws TranslinkException when an exception occurs obtaining or parsing data from Translink service
	 */
	public void parseKMZ(BusRoute route) throws TranslinkException {

		try {
			URL kmzURL = new URL(route.getMapURL());

			URLConnection conn = kmzURL.openConnection();
			InputStream is = conn.getInputStream();
			ZipArchiveInputStream zis = new ZipArchiveInputStream(is);
			zis.getNextZipEntry();  // assuming only one entry in zip file
			
			InputSource src = new InputSource(zis);

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();

			XMLReader reader = parser.getXMLReader();

			KMLParser kmlParser = new KMLParser(route);
			reader.setContentHandler(kmlParser);
			reader.parse(src);
		} catch (Exception e) {
			// Log it
			Log.e(LOG_TAG, e.getMessage());
			
			// Convert other exception types to TranslinkException so clients do not
			// have to worry about the different possibilities.
			throw new TranslinkException(-1, "Unable to retrieve bus route");
		}
	}
	
	/**
	 * Checks that data connection is available on device
	 * @throws ConnectionException when data services are not available
	 */
	private void checkConnection() throws ConnectionException {
		ConnectivityManager cm = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo == null || !netInfo.isConnected()) {
			throw new ConnectionException("Check network connection");
		}
	}
}
