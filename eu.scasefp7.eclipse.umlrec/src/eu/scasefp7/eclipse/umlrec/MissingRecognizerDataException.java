package eu.scasefp7.eclipse.umlrec;
/**
 * 
 */


/**
 * Exception thrown when the recognizer data files are missing or the recognizer fails to analyse the text of the diagram. 
 * 
 * @author Marin Orlic <marin.orlic@ericsson.com>
 *
 */
public class MissingRecognizerDataException extends Exception {

	
	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = 5384953584300571396L;

	public MissingRecognizerDataException() {
		super();
	}

	
	public MissingRecognizerDataException(String message) {
		super(message);
	}

	
}
