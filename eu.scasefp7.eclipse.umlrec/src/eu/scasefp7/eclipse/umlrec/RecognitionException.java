package eu.scasefp7.eclipse.umlrec;
/**
 * 
 */


/**
 * Exception thrown when the recognizer fails with an unknown error.
 * @author Marin Orlic <marin.orlic@ericsson.com>
 *
 */
public class RecognitionException extends Exception {

	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = 2169072134283568153L;
	
	public RecognitionException() {
		super();
	}

	public RecognitionException(String string) {
		super(string);
	}
}
