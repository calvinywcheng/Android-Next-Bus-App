package ca.ubc.cpsc210.exception;

@SuppressWarnings("serial")
/**
 * Represents exception raised when:
 * 
 *  -- Translink returns an error code & message in response to a request
 *  -- some other exception occurs when a request is sent to Translink service
 */
public class TranslinkException extends Exception {
	private int code;
	
	/**
	 * Constructor 
	 * 
	 * @param code   the error code received from Translink (-1 if exception is not related
	 * to a specific Translink error)
	 * @param message   message received from Translink (other appropriate message if not
	 * related to a specific Translink error)
	 */
	public TranslinkException(int code, String message) {
		super(message);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
