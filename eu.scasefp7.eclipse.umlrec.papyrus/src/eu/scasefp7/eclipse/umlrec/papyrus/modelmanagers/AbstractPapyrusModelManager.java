package eu.scasefp7.eclipse.umlrec.papyrus.modelmanagers;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.papyrus.infra.core.editor.IMultiDiagramEditor;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.uml.tools.model.UmlModel;
import org.eclipse.ui.IEditorPart;

import eu.scasefp7.eclipse.umlrec.papyrus.elementsarrangers.ArrangeException;
import eu.scasefp7.eclipse.umlrec.papyrus.model.DiagramManager;
import eu.scasefp7.eclipse.umlrec.papyrus.model.UMLModelManager;
import eu.scasefp7.eclipse.umlrec.papyrus.preferences.PreferencesManager;
import eu.scasefp7.eclipse.umlrec.papyrus.utils.DialogUtils;

/**
 * Controls the Papyrus Model
 *
 * @author Andr�s Dobreff, tsirelis
 */
public abstract class AbstractPapyrusModelManager {

	/**
	 * The activity diagram type
	 */
	protected final static String diagramType_AD = "PapyrusUMLActivityDiagram";
	
	/**
	 * The use-case diagram type
	 */
	protected final static String diagramType_UC = "UseCase";
	
	/**
	 * The DiagramManager controls the diagrams
	 */
	protected DiagramManager diagramManager;
	
	/**
	 * The ModelManager controls the model elements
	 */
	protected UMLModelManager modelManager;
	
	/**
	 * The Editor in which the the visualization is performed
	 */
	protected IEditorPart editor;
	
	/**
	 * The resource were the elements are stored
	 */
	protected UmlModel model;
	
	/**
	 * The Constructor
	 * @param editor - The Editor to which the PapyrusModelManager will be attached
	 * @param model - The Uml Model manager
	 */
	public AbstractPapyrusModelManager(IMultiDiagramEditor editor){
		try{
			this.model = (UmlModel) editor.getServicesRegistry().getService(ModelSet.class)
					.getModel(UmlModel.MODEL_ID);
			this.modelManager = new UMLModelManager(model);
			this.diagramManager = new DiagramManager(editor);
			this.editor = editor;
		}catch(ServiceException e){
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * Creates the diagrams and adds the elements to them
	 * @param monitor - The monitor that listens the progress
	 */
	public void createAndFillDiagrams(IProgressMonitor monitor) {
		monitor.beginTask("Generating Diagrams", 100);
		createDiagrams(new SubProgressMonitor(monitor, 20));
		addElementsToDiagrams(new SubProgressMonitor(monitor, 80));
		this.editor.doSave(new NullProgressMonitor());
	}
	
	/**
	 * Adds the elements to the diagrams
	 * @param monitor
	 */
	protected void addElementsToDiagrams(IProgressMonitor monitor){
		
		List<Diagram> diags =  diagramManager.getDiagrams();
		int diagNum = diags.size();
		monitor.beginTask("Filling diagrams", diagNum*2);
		
		for(int i=0; i<diagNum*2; i=i+2){
			Diagram diagram = diags.get(i/2);
			diagramManager.openDiagram(diagram);
			monitor.subTask("Filling diagrams "+(i+2)/2+"/"+diagNum);
			addElementsToDiagram(diagram, monitor);
			monitor.worked(1);
			
			if(PreferencesManager.getBoolean(PreferencesManager.ARRANGE_DIAGRAM_ELEMENTS_PREF)) {
				try{
					arrangeElementsOfDiagram(diagram, monitor);
				}catch(Throwable e){
					DialogUtils.errorMsgb("Arrange error",
							"Error occured during arrangement of diagram '" +diagram.getName()+"'.", e);
				}
			}
		}
	}
	
	/**
	 * Arranges the diagram elements 
	 * @param diagram - The diagram 
	 * @param monitor - The progress monitor
	 * @throws ArrangeException
	 */
	protected abstract void arrangeElementsOfDiagram(Diagram diagram, IProgressMonitor monitor) throws ArrangeException;
	
	/**
	 * Adds the suitable elements to the diagram
	 * @param diagram - The diagram 
	 * @param monitor - The progress monitor
	 */
	protected abstract void addElementsToDiagram(Diagram diagram, IProgressMonitor monitor);
	
	/**
	 * Creates the diagrams
	 * @param monitor - The progress monitor
	 */
	protected abstract void createDiagrams(IProgressMonitor monitor);
	
	/**
	 * Sets the layout controlling object. This object will affect the
	 * suitable elements and arrage of the diagram
	 * @param layoutcontroller - The controller object
	 */
	public abstract void setLayoutController(Object layoutcontroller);
}
