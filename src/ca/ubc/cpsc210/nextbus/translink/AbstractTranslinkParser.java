package ca.ubc.cpsc210.nextbus.translink;

import org.xml.sax.helpers.DefaultHandler;

/**
 * Abstract class that forms the basis of XML parsers for data received
 * from Translink service.
 */
public abstract class AbstractTranslinkParser extends DefaultHandler {

	protected int code;
	protected String message;
	protected boolean isError;

	public AbstractTranslinkParser() {
		super();
	}

	/**
	 * Did Translink service return an error?
	 * @return  true if error returned by Translink service, false otherwise.
	 */
	public boolean receivedError() {
		return isError;
	}

	/**
	 * Produces error code returned by Translink service
	 * Assumption: receivedError() is true
	 * @return  error code returned by Translink service
	 */
	public int getErrorCode() {
		return code;
	}

	/**
	 * Produces error message returned by Translink service
	 * Assumption: receivedError() is true
	 * @return  error message returned by Translink service
	 */
	public String getMessage() {
		return message;
	}

}