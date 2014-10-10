package ca.ubc.cpsc210.exception;

@SuppressWarnings("serial")
/**
 * Represents exception raised when there is a network problem when
 * trying to connect to Translink service.
 */
public class ConnectionException extends Exception {

	public ConnectionException(String msg) {
		super(msg);
	}
}
