package eu.scasefp7.eclipse.umlrec.ui.papyrus;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.papyrus.infra.core.editor.IMultiDiagramEditor;
import org.eclipse.papyrus.infra.core.resource.ModelMultiException;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.scasefp7.eclipse.umlrec.ui.papyrus.modelmanagers.AbstractPapyrusModelManager;
import eu.scasefp7.eclipse.umlrec.ui.papyrus.utils.EditorUtils;
import eu.scasefp7.eclipse.umlrec.ui.papyrus.utils.FileUtils;
import eu.scasefp7.eclipse.umlrec.ui.papyrus.utils.ProjectUtils;
import eu.scasefp7.eclipse.umlrec.ui.parser.ActivityParser;
import eu.scasefp7.eclipse.umlrec.ui.parser.UseCaseParser;
import eu.scasefp7.eclipse.umlrec.ui.parser.XMIActivityNode;
import eu.scasefp7.eclipse.umlrec.ui.parser.XMIEdge;
import eu.scasefp7.eclipse.umlrec.ui.parser.XMIUseCaseNode;

/**
 * @author tsirelis
 * Generates a Papyrus model from an UML file
 */
public class PapyrusGenerator {	
	// ===========================================================
    // Private Fields
    // ===========================================================
	
	private String projectName;
	private String modelName;
	private String sourceUMLPath;
	private String sourceUMLType;
	private List<XMIActivityNode> sourceUMLActivityNodes = new ArrayList<XMIActivityNode>();
	private List<XMIUseCaseNode> sourceUMLUseCaseNodes = new ArrayList<XMIUseCaseNode>();
	private List<XMIEdge> sourceUMLEdges = new ArrayList<XMIEdge>();
	private AbstractPapyrusModelManager papyrusModelManager;
	private Map<String, Point> layoutController = new HashMap<String, Point>();
	
	// ===========================================================
    // Constructors
    // ===========================================================
	
	/**
	 * The constructor
	 * @param sourceUML - The Eclipse UML2 model 
	 */
	public PapyrusGenerator(IFile sourceUML, Class<? extends AbstractPapyrusModelManager> manager) {
		this.projectName = sourceUML.getProject().getName();
		this.modelName = FileUtils.getFileNameWithOutExtension(sourceUML);
		this.sourceUMLPath = sourceUML.getRawLocationURI().toString();
		this.parseSourceUML(sourceUML);
		SettingsRegistry.setPapyrusModelManager(manager);
	}
	
	// ===========================================================
    // Private Methods
    // ===========================================================
	
	/**
	 * Parses the source UML file reading its edges and nodes.
	 * @param file an {@link IFile} instance of a UML file.
	 */
	private void parseSourceUML(IFile file) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file.getContents());
	
			Element root = doc.getDocumentElement();
			Node packagedElement = root.getFirstChild().getNextSibling();
			if (packagedElement.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) packagedElement;
				sourceUMLType = eElement.getAttribute("xmi:type");
				
				if (sourceUMLType.equalsIgnoreCase("uml:Activity")) {	
					ActivityParser parser = new ActivityParser();
					parser.Parsexmi(doc);
					if(parser!=null) {
						ArrayList<XMIActivityNode> nodes = parser.getNodes();
						ArrayList<XMIEdge> edges = parser.getEdges();
	//					ArrayList<XMIEdge> edgesWithCondition = parser.getEdgesWithCondition();
	//					ArrayList<XMIEdge> edgesWithoutCondition = parser.getEdgesWithoutCondition();
						
						if (parser.checkParsedXmi()) {
							setSourceUMLActivityNodes(nodes);
							setSourceUMLEdges(edges);
						}
					}
					
				} else if (sourceUMLType.equalsIgnoreCase("uml:Use Case")) {	
					UseCaseParser parser = new UseCaseParser();
					parser.Parsexmi(doc);
					if(parser!=null) {
						ArrayList<XMIEdge> edges = parser.getEdges();
						ArrayList<XMIUseCaseNode> nodes = parser.getNodes();
						
						if (parser.checkParsedXmi(false)) {
							setSourceUMLUseCaseNodes(nodes);
							setSourceUMLEdges(edges);
						}
					}
				}
			}
			
		} catch (ParserConfigurationException | SAXException | IOException | CoreException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Generates a Papyrus compliant UML file from the original UML edges and nodes.
	 * @throws ParserConfigurationException 
	 * @throws TransformerException 
	 */
	private void makeSourceUMLPapyrusCompliant() throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		// uml:Model element
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("uml:Model");
		doc.appendChild(rootElement);
		
		rootElement.setAttribute("xmi:version","20131001");
		rootElement.setAttribute("xmlns:xmi","http://www.omg.org/spec/XMI/20131001");
		rootElement.setAttribute("xmlns:uml","http://www.eclipse.org/uml2/5.0.0/UML");
		rootElement.setAttribute("xmi:id","_CHUecI9aEeWta7TZnSTWlg");
		rootElement.setAttribute("name","model");
		
		// packagedElement element
		Element packagedElement = doc.createElement("packagedElement");
		rootElement.appendChild(packagedElement);
		
		packagedElement.setAttribute("xmi:type","uml:Activity");
		packagedElement.setAttribute("xmi:id","_COdBMI9aEeWta7TZnSTWlg");
		packagedElement.setAttribute("name","Activity");
		
		StringJoiner nodesStringJoiner = new StringJoiner(" ");
		if (sourceUMLType.equalsIgnoreCase("uml:Activity")) {
			for (XMIActivityNode node : sourceUMLActivityNodes) {
				nodesStringJoiner.add(node.getId());
			}
		}
		else if (sourceUMLType.equalsIgnoreCase("uml:Use Case")) {
			for (XMIUseCaseNode node : sourceUMLUseCaseNodes) {
				nodesStringJoiner.add(node.getId());
			}
		}
		packagedElement.setAttribute("node",nodesStringJoiner.toString());
		
		// edge elements
		for (XMIEdge edge : sourceUMLEdges) {
			Element edgeElement = doc.createElement("edge");
			packagedElement.appendChild(edgeElement);
			
			edgeElement.setAttribute("xmi:type",edge.getType());
			edgeElement.setAttribute("xmi:id",edge.getId());
			if(edge.getName().isEmpty()==false) {
				edgeElement.setAttribute("name",edge.getName());
			}
			edgeElement.setAttribute("target",edge.getTarget());
			edgeElement.setAttribute("source",edge.getSource());
		}
		
		// node elements
		if (sourceUMLType.equalsIgnoreCase("uml:Activity")) {
			for (XMIActivityNode node : sourceUMLActivityNodes) {
				Element nodeElement = doc.createElement("node");
				packagedElement.appendChild(nodeElement);
				
				nodeElement.setAttribute("xmi:type",node.getType());
				nodeElement.setAttribute("xmi:id",node.getId());
				if(node.getName().isEmpty()==false){
					nodeElement.setAttribute("name",node.getName());
				}
				
				if(node.getIncoming().isEmpty()==false) {
					List<String> incomingEdgeIds = new ArrayList<String>();
					
					for (String incomingNodeId : node.getIncoming()) {
						for (XMIEdge edge : sourceUMLEdges){
							if(incomingNodeId.equals(edge.getSource()) && (edge.getTarget().equals(node.getId()))){
								incomingEdgeIds.add(edge.getId());
							}
						}
					}
	
					StringJoiner incomingStringJoiner = new StringJoiner(" ");
					for (String incomingEdgeId : incomingEdgeIds) {
						incomingStringJoiner.add(incomingEdgeId);
					}
					nodeElement.setAttribute("incoming",incomingStringJoiner.toString());
				}
				
				if(node.getOutgoing().isEmpty()==false){
					List<String> outgoingEdgeIds = new ArrayList<String>();
	
					for (String outgoingNodeId : node.getOutgoing()) {
						for (XMIEdge edge : sourceUMLEdges){
							if(outgoingNodeId.equals(edge.getTarget()) && (edge.getSource().equals(node.getId()))){
								outgoingEdgeIds.add(edge.getId());
							}
						}
					}
					
					StringJoiner outgoingStringJoiner = new StringJoiner(" ");
					for (String outgoingEdgeId : outgoingEdgeIds) {
						outgoingStringJoiner.add(outgoingEdgeId);
					}
					nodeElement.setAttribute("outgoing",outgoingStringJoiner.toString());
				}
				
				List<Point> coordinates = node.getCoordinates();
				if(coordinates.isEmpty()==false) {
					Point nodePoint = coordinates.get(0);
					layoutController.put(node.getId(), nodePoint);
				}
			}
		}
		else if (sourceUMLType.equalsIgnoreCase("uml:Use Case")) {
			// TODO: handle also use-case diagrams
		}
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		
		String sourceUMLFolder = new Path(new Path(sourceUMLPath).toFile().getParent().toString()).toString();
		String newModelName = modelName +"_model";
		String newSourceUMLPath = sourceUMLFolder + "/" + newModelName +".uml";
		File f = new File(newSourceUMLPath.replace("file:", ""));
		StreamResult result = new StreamResult(f);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.transform(source, result);
		
		setModelName(newModelName);
		setSourceUMLPath(newSourceUMLPath);
	}
	
	/**
	 * Creates the Papyrus Model and fills the diagrams.
	 * If the Model already exists, then loads it.
	 * @throws ModelMultiException - If the loading of existing model fails
	 * @throws ServiceException 
	 */
	private void createAndOpenPapyrusModel(IProgressMonitor monitor) throws ModelMultiException, ServiceException {
		monitor.beginTask("Generating Papyrus Model", 100);
		PapyrusModelCreator papyrusModelCreator = new PapyrusModelCreator(projectName + "/" + modelName);
		papyrusModelCreator.setUpUML(sourceUMLPath);
		if(!papyrusModelCreator.diExists()){
			
			monitor.subTask("Generating Papyrus model...");
			papyrusModelCreator.createPapyrusModel();
			IMultiDiagramEditor editor = EditorUtils.openPapyrusEditor(papyrusModelCreator.getDi());
			
			papyrusModelManager = SettingsRegistry.getPapyrusModelManager(editor);
			papyrusModelManager.setLayoutController(layoutController);
			monitor.worked(10);
			
			papyrusModelManager.createAndFillDiagrams(new SubProgressMonitor(monitor, 90));
		} 
	}
	
	// ===========================================================
    // Getters/Setters
    // ===========================================================
	
	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * @param modelName the modelName to set
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * @return the sourceUMLPath
	 */
	public String getSourceUMLPath() {
		return sourceUMLPath;
	}

	/**
	 * @param sourceUMLPath the sourceUMLPath to set
	 */
	public void setSourceUMLPath(String sourceUMLPath) {
		this.sourceUMLPath = sourceUMLPath;
	}
	
	/**
	 * @return the sourceUMLType
	 */
	public String getSourceUMLType() {
		return sourceUMLType;
	}

	/**
	 * @param sourceUMLType the sourceUMLType to set
	 */
	public void setSourceUMLType(String sourceUMLType) {
		this.sourceUMLType = sourceUMLType;
	}

	/**
	 * @return the sourceUMLEdges
	 */
	public List<XMIEdge> getSourceUMLEdges() {
		return sourceUMLEdges;
	}

	/**
	 * @param sourceUMLEdges the sourceUMLEdges to set
	 */
	public void setSourceUMLEdges(List<XMIEdge> sourceUMLEdges) {
		this.sourceUMLEdges = sourceUMLEdges;
	}

	/**
	 * @return the sourceUMLActivityNodes
	 */
	public List<XMIActivityNode> getSourceUMLActivityNodes() {
		return sourceUMLActivityNodes;
	}

	/**
	 * @param sourceUMLActivityNodes the sourceUMLNodes to set
	 */
	public void setSourceUMLActivityNodes(List<XMIActivityNode> sourceUMLActivityNodes) {
		this.sourceUMLActivityNodes = sourceUMLActivityNodes;
	}

	/**
	 * @return the sourceUMLUseCaseNodes
	 */
	public List<XMIUseCaseNode> getSourceUMLUseCaseNodes() {
		return sourceUMLUseCaseNodes;
	}

	/**
	 * @param sourceUMLUseCaseNodes the sourceUMLUseCaseNodes to set
	 */
	public void setSourceUMLUseCaseNodes(List<XMIUseCaseNode> sourceUMLUseCaseNodes) {
		this.sourceUMLUseCaseNodes = sourceUMLUseCaseNodes;
	}

	// ===========================================================
    // Public Methods
    // ===========================================================
	
	/**
	 * Executes the visualization process. 
	 * Creates the project (if not exists) and sets up the Papyrus Model
	 * @param monitor 
	 * @return 
	 * @throws ServiceException 
	 * @throws ModelMultiException 
	 * @throws TransformerException 
	 * @throws ParserConfigurationException 
	 */
	public IStatus run(IProgressMonitor monitor) throws ModelMultiException, ServiceException, ParserConfigurationException, TransformerException {
		monitor.beginTask("Papyrus Model Generation", 100);

		monitor.subTask("Making UML compliant...");
		makeSourceUMLPapyrusCompliant();
		monitor.worked(10);
		
		// TODO: handle use-case diagrams also
		monitor.subTask("Creating new Papyrus project...");
		IProject project = ProjectUtils.createProject(projectName);
		ProjectUtils.openProject(project);
		monitor.worked(20);
		
		createAndOpenPapyrusModel(new SubProgressMonitor(monitor, 80));
		SettingsRegistry.clear();
		
		monitor.subTask("Cleaning up");
		FileUtils.deleteFile(java.nio.file.Paths.get(sourceUMLPath.replace("file:", "")));
		monitor.worked(100);
		
		return Status.OK_STATUS;
	}
}