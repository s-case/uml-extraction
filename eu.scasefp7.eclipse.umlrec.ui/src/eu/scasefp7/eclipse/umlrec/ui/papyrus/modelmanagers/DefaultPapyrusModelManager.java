package eu.scasefp7.eclipse.umlrec.ui.papyrus.modelmanagers;

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
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.Element;

import eu.scasefp7.eclipse.umlrec.ui.papyrus.elementsarrangers.ActivityDiagramElementsGmfArranger;
import eu.scasefp7.eclipse.umlrec.ui.papyrus.elementsarrangers.ArrangeException;
import eu.scasefp7.eclipse.umlrec.ui.papyrus.elementsarrangers.IDiagramElementsArranger;
import eu.scasefp7.eclipse.umlrec.ui.papyrus.elementsmanagers.AbstractDiagramElementsManager;
import eu.scasefp7.eclipse.umlrec.ui.papyrus.elementsmanagers.ActivityDiagramElementsManager;
import eu.scasefp7.eclipse.umlrec.ui.papyrus.preferences.PreferencesManager;

/**
 *	The Default representation of Papyrus Model manager
 *
 * @author Andr�s Dobreff
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
	 * @see eu.scasefp7.eclipse.umlrec.ui.papyrus.modelmanagers.AbstractPapyrusModelManager#createDiagrams(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void createDiagrams(IProgressMonitor monitor){
		monitor.beginTask("Generating empty diagrams", 100);
		monitor.subTask("Creating empty diagrams...");
		
		// TODO: handle also use-case diagrams
		if(PreferencesManager.getBoolean(PreferencesManager.ACTIVITY_DIAGRAM_PREF)){
			List<Element> activities = modelManager.getElementsOfTypes(Arrays.asList(Activity.class));
			diagramManager.createDiagrams(activities, new CreateActivityDiagramCommand());
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
	 * @see eu.scasefp7.eclipse.umlrec.ui.papyrus.modelmanagers.AbstractPapyrusModelManager#arrangeElementsOfDiagram(org.eclipse.gmf.runtime.notation.Diagram, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void arrangeElementsOfDiagram(Diagram diagram, IProgressMonitor monitor) throws ArrangeException{
		IDiagramElementsArranger diagramElementsArranger;
		DiagramEditPart diagep = diagramManager.getActiveDiagramEditPart();
		// TODO: add support for use-case diagram
		if(diagram.getType().equals("PapyrusUMLActivityDiagram")){
			diagramElementsArranger = new ActivityDiagramElementsGmfArranger(diagep);
		}else{
			return;
		}
		
		diagramElementsArranger.arrange(monitor);
	}

	/*
	 * (non-Javadoc)
	 * @see eu.scasefp7.eclipse.umlrec.ui.papyrus.modelmanagers.AbstractPapyrusModelManager#addElementsToDiagram(org.eclipse.gmf.runtime.notation.Diagram, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void addElementsToDiagram(Diagram diagram, IProgressMonitor monitor) {
		Element container = diagramManager.getDiagramContainer(diagram);
		AbstractDiagramElementsManager diagramElementsManager;
		DiagramEditPart diagep = diagramManager.getActiveDiagramEditPart();
		// TODO: add support for use-case diagram
		if(diagram.getType().equals("PapyrusUMLActivityDiagram")){
			diagramElementsManager = new ActivityDiagramElementsManager(diagep);
		}else{
			return;
		}
		
		List<Element> baseElements = modelManager.getAllChildrenOfPackage(container);
//		diagramElementsManager.addElementsToDiagram(baseElements);
		diagramElementsManager.addElementsToDiagram(baseElements, layoutController);
	}	

}
