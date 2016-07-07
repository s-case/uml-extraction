package eu.scasefp7.eclipse.umlrec.papyrus.elementsarrangers;

import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;


/**
 * Abstract class for arranging the elements of a diagram.
 *
 * @author Andr�s Dobreff
 */
public abstract class AbstractDiagramElementsArranger implements
		IDiagramElementsArranger {
	
	/**
	 * The EditPart of the diagram which elements is to arranged.
	 */
	protected DiagramEditPart diagep;
	
	/**
	 * The Constructor 
	 * @param diagramEditPart - The EditPart of the diagram which elements is to arranged.
	 */
	public AbstractDiagramElementsArranger(DiagramEditPart diagramEditPart) {
		diagep = diagramEditPart;
	}


}
