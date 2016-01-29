package eu.scasefp7.eclipse.umlrec.papyrus.elementsarrangers;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * An Interface for arranging elements of a diagram 
 *
 * @author Andr�s Dobreff
 */
public interface IDiagramElementsArranger {
	
	/**
	 * Arranges the elements of the diagram
	 * @param monitor 
	 * @throws ArrangeException - The arranging algorithms may throw this exception
	 */
	public void arrange(IProgressMonitor monitor) throws ArrangeException;
}
