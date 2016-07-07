/**
 * 
 */
package eu.scasefp7.eclipse.umlrec.papyrus.elementsmanagers;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.papyrus.uml.diagram.usecase.edit.parts.UseCaseDiagramEditPart;
import org.eclipse.uml2.uml.Abstraction;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Extend;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Include;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.PackageMerge;
import org.eclipse.uml2.uml.Realization;
import org.eclipse.uml2.uml.Usage;
import org.eclipse.uml2.uml.UseCase;

import eu.scasefp7.eclipse.umlrec.papyrus.api.UseCaseDiagramElementsController;
import eu.scasefp7.eclipse.umlrec.papyrus.model.UMLModelManager;
import eu.scasefp7.eclipse.umlrec.papyrus.preferences.PreferencesManager;

/**
 * A class for adding/removing elements to UseCaseDiagrams.
 * 
 * @author tsirelis
 *
 */
public class UseCaseDiagramElementsManager extends AbstractDiagramElementsManager {
	private List<java.lang.Class<? extends Element>> nodesToBeAdded;
	private List<java.lang.Class<? extends Element>> connectorsToBeAdded;

	/**
	 * @param diagramEditPart
	 */
	public UseCaseDiagramElementsManager(DiagramEditPart diagramEditPart) {
		super(diagramEditPart);
		nodesToBeAdded = generateNodesToBeAdded();
		connectorsToBeAdded = generateConnectorsToBeAdded();
	}
	
	/**
	 * Returns the types of nodes that are to be added
	 * @return Returns the types of nodes that are to be added
	 */
	private List<java.lang.Class<? extends Element>> generateNodesToBeAdded() {
		List<java.lang.Class<? extends Element>> nodes = new LinkedList<>(Arrays.asList(
				Package.class,
				Actor.class,
				UseCase.class,
				Constraint.class
			));
		
		if(PreferencesManager.getBoolean(PreferencesManager.USE_CASE_DIAGRAM_COMMENT_PREF)) {
			nodes.add(Comment.class);
		}
		
		return nodes;
	}
	
	/**
	 * Returns the types of connectors that are to be added
	 * @return Returns the types of connectors that are to be added 
	 */
	private List<java.lang.Class<? extends Element>> generateConnectorsToBeAdded() {
		List<java.lang.Class<? extends Element>> connectors = Arrays.asList(
				Association.class, 
				Generalization.class, 
				Dependency.class, 
				Abstraction.class,
				Realization.class,
				Usage.class,
				PackageMerge.class,
				PackageImport.class,
				Include.class,
				Extend.class
			);
		return connectors;
	}

	/* (non-Javadoc)
	 * @see eu.scasefp7.eclipse.umlrec.papyrus.model.elementsmanagers.AbstractDiagramElementsManager#addElementsToDiagram(java.util.List)
	 */
	@Override
	public void addElementsToDiagram(List<Element> elements) {
		List<Element> diagramelements = UMLModelManager.getElementsOfTypesFromList(elements, nodesToBeAdded);
		List<Element> diagramconnections = UMLModelManager.getElementsOfTypesFromList(elements, connectorsToBeAdded);

		UseCaseDiagramElementsController.addElementsToUseCaseDiagram((UseCaseDiagramEditPart) diagramEditPart, diagramelements);
		UseCaseDiagramElementsController.addElementsToUseCaseDiagram((UseCaseDiagramEditPart) diagramEditPart, diagramconnections);
	}

	/* (non-Javadoc)
	 * @see eu.scasefp7.eclipse.umlrec.papyrus.model.elementsmanagers.AbstractDiagramElementsManager#addElementsToDiagram(java.util.List, java.lang.Object)
	 */
	@Override
	public void addElementsToDiagram(List<Element> elements, Object layoutController) {
		List<Element> diagramelements = UMLModelManager.getElementsOfTypesFromList(elements, nodesToBeAdded);
		List<Element> diagramconnections = UMLModelManager.getElementsOfTypesFromList(elements, connectorsToBeAdded);

		@SuppressWarnings("unchecked")
		Map<String, Point> coordinates = (Map<String, Point>)layoutController;
		UseCaseDiagramElementsController.addElementsToUseCaseDiagram((UseCaseDiagramEditPart) diagramEditPart, diagramelements, coordinates);
		UseCaseDiagramElementsController.addElementsToUseCaseDiagram((UseCaseDiagramEditPart) diagramEditPart, diagramconnections);
	}

}
