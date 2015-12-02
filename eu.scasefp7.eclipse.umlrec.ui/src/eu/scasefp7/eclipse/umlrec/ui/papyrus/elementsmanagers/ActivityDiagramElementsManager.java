package eu.scasefp7.eclipse.umlrec.ui.papyrus.elementsmanagers;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.papyrus.uml.diagram.activity.edit.parts.ActivityDiagramEditPart;
import org.eclipse.uml2.uml.AcceptEventAction;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.ActivityFinalNode;
import org.eclipse.uml2.uml.AddStructuralFeatureValueAction;
import org.eclipse.uml2.uml.AddVariableValueAction;
import org.eclipse.uml2.uml.BroadcastSignalAction;
import org.eclipse.uml2.uml.CallBehaviorAction;
import org.eclipse.uml2.uml.CallOperationAction;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.ControlFlow;
import org.eclipse.uml2.uml.CreateObjectAction;
import org.eclipse.uml2.uml.DecisionNode;
import org.eclipse.uml2.uml.DestroyObjectAction;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.FinalNode;
import org.eclipse.uml2.uml.FlowFinalNode;
import org.eclipse.uml2.uml.ForkNode;
import org.eclipse.uml2.uml.InitialNode;
import org.eclipse.uml2.uml.JoinNode;
import org.eclipse.uml2.uml.MergeNode;
import org.eclipse.uml2.uml.ObjectFlow;
import org.eclipse.uml2.uml.OpaqueAction;
import org.eclipse.uml2.uml.ReadSelfAction;
import org.eclipse.uml2.uml.ReadStructuralFeatureAction;
import org.eclipse.uml2.uml.ReadVariableAction;
import org.eclipse.uml2.uml.SendObjectAction;
import org.eclipse.uml2.uml.SendSignalAction;
import org.eclipse.uml2.uml.ValueSpecificationAction;

import eu.scasefp7.eclipse.umlrec.ui.papyrus.UMLModelManager;
import eu.scasefp7.eclipse.umlrec.ui.papyrus.api.ActivityDiagramElementsController;
import eu.scasefp7.eclipse.umlrec.ui.papyrus.preferences.PreferencesManager;

/**
 * An abstract class for adding/removing elements to ActivityDiagrams.
 *
 * @author tsirelis
 */
public class ActivityDiagramElementsManager extends AbstractDiagramElementsManager{
	
	private List<java.lang.Class<? extends Element>> nodesToBeAdded;
	private List<java.lang.Class<? extends Element>> connectorsToBeAdded;
	
	/**
	 * The Constructor
	 * @param diagramEditPart - The DiagramEditPart of the diagram which is to be handled
	 */
	public ActivityDiagramElementsManager(DiagramEditPart diagramEditPart) {
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
				AcceptEventAction.class,
				Activity.class,
				ActivityFinalNode.class,
				AddStructuralFeatureValueAction.class,
				AddVariableValueAction.class,
				BroadcastSignalAction.class,
				CallBehaviorAction.class, 
				CallOperationAction.class,
				CreateObjectAction.class,
				DecisionNode.class,
				DestroyObjectAction.class,
				FinalNode.class,
				FlowFinalNode.class,
				ForkNode.class,
				InitialNode.class,
				JoinNode.class,
				MergeNode.class,
				OpaqueAction.class,
				ReadSelfAction.class,
				ReadStructuralFeatureAction.class,
				ReadVariableAction.class,
				SendObjectAction.class,
				SendSignalAction.class,
				ValueSpecificationAction.class
			));
		
		if(PreferencesManager.getBoolean(PreferencesManager.ACTIVITY_DIAGRAM_COMMENT_PREF)) {
			nodes.add(Comment.class);
		}
		
		return nodes;
	}
	
	/**
	 * Returns the types of connectors that are to be added
	 * @return Returns the types of connectors that are to be added 
	 */
	private List<java.lang.Class<? extends Element>> generateConnectorsToBeAdded() {
		List<java.lang.Class<? extends Element>> connectors = Arrays.asList(ControlFlow.class, ObjectFlow.class);
		return connectors;
	}

	/*
	 * (non-Javadoc)
	 * @see eu.scasefp7.eclipse.umlrec.ui.papyrus.elementsmanagers.AbstractDiagramElementsManager#addElementsToDiagram(java.util.List)
	 */
	@Override
	public void addElementsToDiagram(List<Element> elements) {
		List<Element> diagramelements = UMLModelManager.getElementsOfTypesFromList(elements, nodesToBeAdded);
		List<Element> diagramconnections = UMLModelManager.getElementsOfTypesFromList(elements, connectorsToBeAdded);

		ActivityDiagramElementsController.addElementsToActivityDiagram((ActivityDiagramEditPart) diagramEditPart, diagramelements);
		ActivityDiagramElementsController.addElementsToActivityDiagram((ActivityDiagramEditPart) diagramEditPart, diagramconnections);
	}
	
	/*
	 * (non-Javadoc)
	 * @see eu.scasefp7.eclipse.umlrec.ui.papyrus.elementsmanagers.AbstractDiagramElementsManager#addElementsToDiagram(java.lang.Object)
	 */
	@Override
	public void addElementsToDiagram(List<Element> elements, Object layoutController) {
		List<Element> diagramelements = UMLModelManager.getElementsOfTypesFromList(elements, nodesToBeAdded);
		List<Element> diagramconnections = UMLModelManager.getElementsOfTypesFromList(elements, connectorsToBeAdded);

		@SuppressWarnings("unchecked")
		Map<String, Point> coordinates = (Map<String, Point>)layoutController;
		ActivityDiagramElementsController.addElementsToActivityDiagram((ActivityDiagramEditPart) diagramEditPart, diagramelements, coordinates);
		ActivityDiagramElementsController.addElementsToActivityDiagram((ActivityDiagramEditPart) diagramEditPart, diagramconnections);
	}
}
