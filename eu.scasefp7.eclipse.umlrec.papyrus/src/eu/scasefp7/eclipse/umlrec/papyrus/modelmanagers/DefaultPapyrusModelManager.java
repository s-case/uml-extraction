package eu.scasefp7.eclipse.umlrec.papyrus.modelmanagers;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.papyrus.infra.core.editor.IMultiDiagramEditor;
import org.eclipse.papyrus.uml.diagram.activity.CreateActivityDiagramCommand;
import org.eclipse.papyrus.uml.diagram.usecase.CreateUseCaseDiagramCommand;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;

import eu.scasefp7.eclipse.umlrec.papyrus.elementsarrangers.ActivityDiagramElementsGmfArranger;
import eu.scasefp7.eclipse.umlrec.papyrus.elementsarrangers.ArrangeException;
import eu.scasefp7.eclipse.umlrec.papyrus.elementsarrangers.IDiagramElementsArranger;
import eu.scasefp7.eclipse.umlrec.papyrus.elementsmanagers.AbstractDiagramElementsManager;
import eu.scasefp7.eclipse.umlrec.papyrus.elementsmanagers.ActivityDiagramElementsManager;
import eu.scasefp7.eclipse.umlrec.papyrus.elementsmanagers.UseCaseDiagramElementsManager;
import eu.scasefp7.eclipse.umlrec.papyrus.preferences.PreferencesManager;

/**
 *	The Default representation of Papyrus Model manager
 *
 *	@author Andr�s Dobreff, tsirelis
 */
public class DefaultPapyrusModelManager extends AbstractPapyrusModelManager {
	protected Map<String, Point> layoutController = new HashMap<String, Point>();

	/**
	 * The Constructor
	 * @param editor - The editor
	 */
	public DefaultPapyrusModelManager(IMultiDiagramEditor editor) {
		super(editor);
	}
	

	/*
	 * (non-Javadoc)
	 * @see eu.scasefp7.eclipse.umlrec.papyrus.model.modelmanagers.AbstractPapyrusModelManager#createDiagrams(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void createDiagrams(IProgressMonitor monitor){
		monitor.beginTask("Generating empty diagrams", 100);
		monitor.subTask("Creating empty diagrams...");
		
		if(PreferencesManager.getBoolean(PreferencesManager.ACTIVITY_DIAGRAM_PREF)){
			List<Element> activities = modelManager.getElementsOfTypes(Arrays.asList(Activity.class));
			diagramManager.createDiagrams(activities, new CreateActivityDiagramCommand());
		}
		
		if(PreferencesManager.getBoolean(PreferencesManager.USE_CASE_DIAGRAM_PREF)){
			List<Element> models = modelManager.getElementsOfTypes(Arrays.asList(Model.class));
			diagramManager.createDiagrams(models, new CreateUseCaseDiagramCommand());
		}
		
		monitor.worked(100);
	}

	
	@Override
	@SuppressWarnings("unchecked")
	public void setLayoutController(Object layoutController) {
		this.layoutController = (Map<String, Point>) layoutController;
	}

	/*
	 * (non-Javadoc)
	 * @see eu.scasefp7.eclipse.umlrec.papyrus.model.modelmanagers.AbstractPapyrusModelManager#arrangeElementsOfDiagram(org.eclipse.gmf.runtime.notation.Diagram, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void arrangeElementsOfDiagram(Diagram diagram, IProgressMonitor monitor) throws ArrangeException{
		IDiagramElementsArranger diagramElementsArranger;
		DiagramEditPart diagep = diagramManager.getActiveDiagramEditPart();
		// TODO: add support for use-case diagram
		if(diagram.getType().equals(diagramType_AD)){
			diagramElementsArranger = new ActivityDiagramElementsGmfArranger(diagep);
		}else{
			return;
		}
		
		diagramElementsArranger.arrange(monitor);
	}

	/*
	 * (non-Javadoc)
	 * @see eu.scasefp7.eclipse.umlrec.papyrus.model.modelmanagers.AbstractPapyrusModelManager#addElementsToDiagram(org.eclipse.gmf.runtime.notation.Diagram, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void addElementsToDiagram(Diagram diagram, IProgressMonitor monitor) {
		Element container = diagramManager.getDiagramContainer(diagram);
		AbstractDiagramElementsManager diagramElementsManager;
		DiagramEditPart diagep = diagramManager.getActiveDiagramEditPart();
		
		if(diagram.getType().equals(diagramType_AD)) {
			diagramElementsManager = new ActivityDiagramElementsManager(diagep);
		}
		else if(diagram.getType().equals(diagramType_UC)){
			diagramElementsManager = new UseCaseDiagramElementsManager(diagep);
		}
		else{
			return;
		}
		
		List<Element> baseElements = modelManager.getAllChildrenOfPackage(container);
		if(layoutController.isEmpty()) {
			diagramElementsManager.addElementsToDiagram(baseElements);
		}
		else {
			diagramElementsManager.addElementsToDiagram(baseElements, layoutController);
		}
	}	

}
