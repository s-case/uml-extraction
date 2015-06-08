/**
 * 
 */
package eu.scasefp7.eclipse.umlrec;

/**
 * Unchecked exception thrown when the recognizer libraries (native code) is
 * missing.
 * 
 * @author Marin Orlic <marin.orlic@ericsson.com>
 *
 */
public class MissingRecognizerNativeException extends RuntimeException {

	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = -7139299530869543310L;

	public MissingRecognizerNativeException() {
		super();
	}

	public MissingRecognizerNativeException(String message) {
		super(message);
	}

	public MissingRecognizerNativeException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingRecognizerNativeException(Throwable cause) {
		super(cause);
	}

	protected MissingRecognizerNativeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
