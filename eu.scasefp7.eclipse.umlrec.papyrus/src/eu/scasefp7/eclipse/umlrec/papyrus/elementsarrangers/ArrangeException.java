package eu.scasefp7.eclipse.umlrec.papyrus.elementsarrangers;

/**
 * An exception for any arrangement error
 * @author Andr�s Dobreff
 */
public class ArrangeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The Constructor
	 * @param message
	 */
	public ArrangeException(String message) {
		super(message);
	}
	
	/**
	 * The Constructor
	 * @param exception
	 */
	public ArrangeException(Exception exception){
		super(exception.getMessage());
	}
}
