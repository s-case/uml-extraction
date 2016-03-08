package eu.scasefp7.eclipse.umlrec.ui.papyrus.api;

import java.awt.Point;
import java.util.Collection;
import java.util.Map;

import org.eclipse.papyrus.uml.diagram.usecase.edit.parts.UseCaseDiagramEditPart;
import org.eclipse.uml2.uml.Element;

import eu.scasefp7.eclipse.umlrec.ui.papyrus.utils.ElementsManagerUtils;

/**
 * A class for controlling elements of UseCaseDiagrams.
 *
 * @author tsirelis
 */
public class UseCaseDiagramElementsController {
	/**
	 * @param diagramEditPart
	 * @param element
	 */
	public static void addElementToUseCaseDiagram(UseCaseDiagramEditPart diagramEditPart, Element element){
		ElementsManagerUtils.addElementToEditPart(diagramEditPart, element);
	}
	
	/**
	 * @param diagramEditPart
	 * @param elements 
	 */
	public static void addElementsToUseCaseDiagram(UseCaseDiagramEditPart diagramEditPart,Collection<Element> elements){
		ElementsManagerUtils.addElementsToEditPart(diagramEditPart, elements);
	}
	
	/**
	 * @param diagramEditPart
	 * @param elements 
	 * @param coordinates 
	 */
	public static void addElementsToUseCaseDiagram(UseCaseDiagramEditPart diagramEditPart,Collection<Element> elements, Map<String, Point> coordinates){
		ElementsManagerUtils.addElementsToEditPart(diagramEditPart, elements, coordinates);
	}
}
