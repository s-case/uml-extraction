package eu.scasefp7.eclipse.umlrec.papyrus.model;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import eu.scasefp7.eclipse.umlrec.papyrus.modelmanagers.AbstractPapyrusModelManager;
import eu.scasefp7.eclipse.umlrec.papyrus.preferences.PreferencesManager;
import eu.scasefp7.eclipse.umlrec.papyrus.utils.EditorUtils;
import eu.scasefp7.eclipse.umlrec.papyrus.utils.FileUtils;
import eu.scasefp7.eclipse.umlrec.papyrus.utils.ProjectUtils;
import eu.scasefp7.eclipse.umlrec.parser.ActivityParser;
import eu.scasefp7.eclipse.umlrec.parser.UseCaseParser;
import eu.scasefp7.eclipse.umlrec.parser.XMIActivityNode;
import eu.scasefp7.eclipse.umlrec.parser.XMIEdge;
import eu.scasefp7.eclipse.umlrec.parser.XMIUseCaseNode;

/**
 * @author tsirelis
 * Generates a Papyrus model from an UML file
 */
public class PapyrusGenerator {	
	// ===========================================================
    // Private Fields
    // ===========================================================
	
	private String projectName;
	private String folderName;
	private String modelName;
	private String tempModelName;
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
		this.folderName = sourceUML.getParent().getName();
		this.modelName = FileUtils.getFileNameWithOutExtension(sourceUML);
		this.sourceUMLPath = sourceUML.getRawLocationURI().toString();
		this.parseSourceUML(sourceUML);
		this.makeSourceUMLPapyrusCompliant();
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
//						ArrayList<XMIEdge> edgesWithCondition = parser.getEdgesWithCondition();
//						ArrayList<XMIEdge> edgesWithoutCondition = parser.getEdgesWithoutCondition();
						
						if (parser.checkParsedXmiForPapyrus()) {
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
						
						if (parser.checkParsedXmiForPapyrus()) {
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
	private void makeSourceUMLPapyrusCompliant() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// uml:Model element
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("uml:Model");
			doc.appendChild(rootElement);
			
			rootElement.setAttribute("xmi:version","20131001");
			rootElement.setAttribute("xmlns:xmi","http://www.omg.org/spec/XMI/20131001");
			rootElement.setAttribute("xmlns:uml","http://www.eclipse.org/uml2/5.0.0/UML");
			rootElement.setAttribute("xmi:id", getUniqueId());
			rootElement.setAttribute("name","model");
			
			if (sourceUMLType.equalsIgnoreCase("uml:Activity")) {
				// packagedElement element
				Element packagedElement = doc.createElement("packagedElement");
				rootElement.appendChild(packagedElement);
			
				packagedElement.setAttribute("xmi:type", "uml:Activity");
				packagedElement.setAttribute("xmi:id", getUniqueId());
				packagedElement.setAttribute("name", "RootElement");
				
				StringBuilder nodesStringJoiner = new StringBuilder();
				if (sourceUMLType.equalsIgnoreCase("uml:Activity")) {
					for (XMIActivityNode node : sourceUMLActivityNodes) {
						nodesStringJoiner.append(node.getId() + " ");
					}
				}
				else if (sourceUMLType.equalsIgnoreCase("uml:Use Case")) {
					for (XMIUseCaseNode node : sourceUMLUseCaseNodes) {
						nodesStringJoiner.append(node.getId() + " ");
					}
				}
				String nodesString = nodesStringJoiner.toString();
				if (nodesString.length() > 0)
					packagedElement.setAttribute("node", nodesString.substring(0, nodesString.length() - 1));
				else
					packagedElement.setAttribute("node", nodesString);
				
				// edge elements
				for (XMIEdge edge : sourceUMLEdges) {
					Element edgeElement = doc.createElement("edge");
					packagedElement.appendChild(edgeElement);
					
					edgeElement.setAttribute("xmi:type",edge.getType());
					edgeElement.setAttribute("xmi:id",edge.getId());
					String edgeName = edge.getName();
					if(edgeName!=null && edgeName.isEmpty()==false) {
						edgeElement.setAttribute("name", edgeName);
					}
					edgeElement.setAttribute("target",edge.getTarget());
					edgeElement.setAttribute("source",edge.getSource());
				}
			
				// node elements
				for (XMIActivityNode node : sourceUMLActivityNodes) {
					Element nodeElement = doc.createElement("node");
					packagedElement.appendChild(nodeElement);
					
					nodeElement.setAttribute("xmi:type",node.getType());
					String nodeId = node.getId();
					nodeElement.setAttribute("xmi:id", nodeId);
					String nodeName = node.getName();
					if(nodeName!=null && nodeName.isEmpty()==false){
						nodeElement.setAttribute("name", nodeName);
					}
					
					nodeElement.setAttribute("incoming",getNodeIncomingEdgeIds(nodeId, node.getIncoming()));
					nodeElement.setAttribute("outgoing",getNodeOutgoingEdgeIds(nodeId, node.getOutgoing()));
					
					List<Point> coordinates = node.getCoordinates();
					if(coordinates!=null && coordinates.isEmpty()==false  && coordinates.get(0)!=null) {
						Point nodePoint = coordinates.get(0);
						layoutController.put(nodeId, nodePoint);
					}
				}
			}
			else if (sourceUMLType.equalsIgnoreCase("uml:Use Case")) {
				for (XMIUseCaseNode node : sourceUMLUseCaseNodes) {
					Element packagedElement = doc.createElement("packagedElement");
					rootElement.appendChild(packagedElement);
					
					String nodeType = node.getType();
					switch(nodeType) {
						case "uml:UserNode":
							packagedElement.setAttribute("xmi:type", "uml:Actor");
							break;
						case "uml:UseCaseNode":
							packagedElement.setAttribute("xmi:type", "uml:UseCase");
							break;
						default:
							packagedElement.setAttribute("xmi:type", nodeType);
							break;
					}
					
					String nodeId = node.getId();
					packagedElement.setAttribute("xmi:id", nodeId);
					
					String nodeName = node.getName();
					if(nodeName!=null && nodeName.isEmpty()==false){
						packagedElement.setAttribute("name", nodeName);
					}
					
					// generalizations
					List<String> outgoingSolidNodesIds = node.getoutgoingSolidNode();
					if(outgoingSolidNodesIds!=null && outgoingSolidNodesIds.isEmpty()==false) {
						for (String outgoingNodeId : outgoingSolidNodesIds) {
							if(outgoingNodeId.isEmpty()==false) {
								for (XMIEdge edge : sourceUMLEdges){
									if(outgoingNodeId.equals(edge.getTarget()) && edge.getSource().equals(nodeId)) {
										Element generalization = doc.createElement("generalization");
										generalization.setAttribute("xmi:type", "uml:Generalization");
										generalization.setAttribute("xmi:id", getUniqueId());
										generalization.setAttribute("general", outgoingNodeId);
										packagedElement.appendChild(generalization);
									}
								}
							}
						}
					}
					
					// associations
					List<String> connectedSolidNodesIds = node.getconnectedSolidNode();
					if(connectedSolidNodesIds!=null && connectedSolidNodesIds.isEmpty()==false) {
						for (String connectedNodeId : connectedSolidNodesIds) {
							if(connectedNodeId.isEmpty()==false) {
								for (XMIEdge edge : sourceUMLEdges) {
									if(connectedNodeId.equals(edge.getTarget()) && edge.getSource().equals(nodeId)) {
										Element association = doc.createElement("packagedElement");
										association.setAttribute("xmi:type", "uml:Association");
										String associationId = getUniqueId();
										association.setAttribute("xmi:id", associationId);
										
										Element ownedEnd1 = doc.createElement("ownedEnd");
										ownedEnd1.setAttribute("xmi:type","uml:Property");
										String ownedEnd1Id = getUniqueId();
										ownedEnd1.setAttribute("xmi:id", ownedEnd1Id);
										ownedEnd1.setAttribute("name", "");
										ownedEnd1.setAttribute("type", nodeId);
										ownedEnd1.setAttribute("association", associationId);
										
										Element ownedEnd2 = doc.createElement("ownedEnd");
										ownedEnd2.setAttribute("xmi:type","uml:Property");
										String ownedEnd2Id = getUniqueId();
										ownedEnd2.setAttribute("xmi:id", ownedEnd2Id);
										ownedEnd2.setAttribute("name", "");
										ownedEnd2.setAttribute("type", connectedNodeId);
										ownedEnd2.setAttribute("association", associationId);
										
										association.setAttribute("memberEnd", ownedEnd1Id+" "+ownedEnd2Id);
										association.appendChild(ownedEnd1);
										association.appendChild(ownedEnd2);
										rootElement.appendChild(association);
									}
								}
							}
						}
					}
					
					// include - extend
					List<String> outgoingDashedNodesIds = node.getoutgoingDashedNode();
					if(outgoingDashedNodesIds!=null && outgoingDashedNodesIds.isEmpty()==false) {
						for (String outgoingNodeId : outgoingDashedNodesIds) {
							if(outgoingNodeId.isEmpty()==false) {
								for (XMIEdge edge : sourceUMLEdges) {
									if(outgoingNodeId.equals(edge.getTarget()) && edge.getSource().equals(nodeId)) {
										if(edge.getName().equalsIgnoreCase("include")) {
											Element include = doc.createElement("include");
											include.setAttribute("xmi:type", "uml:Include");
											String includeId = getUniqueId();
											include.setAttribute("xmi:id", includeId);
											include.setAttribute("addition", outgoingNodeId);
											packagedElement.appendChild(include);
										}
										else if(edge.getName().equalsIgnoreCase("extend")) {
											Element extend = doc.createElement("extend");
											extend.setAttribute("xmi:type", "uml:Extend");
											String extendId = getUniqueId();
											extend.setAttribute("xmi:id", extendId);
											extend.setAttribute("extendedCase", outgoingNodeId);
											packagedElement.appendChild(extend);
										}
									}
								}
							}
						}
					}
					
					List<Point> coordinates = node.getCoordinates();
					if(coordinates!=null && coordinates.isEmpty()==false && coordinates.get(0)!=null) {
						Point nodePoint = coordinates.get(0);
						layoutController.put(node.getId(), nodePoint);
					}
				}
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
			String sourceUMLFolder = new Path(new Path(sourceUMLPath).toFile().getParent().toString()).toString();
			String newModelName = modelName + "_model";
			tempModelName = modelName + "_model_temp";
			String newSourceUMLPath = sourceUMLFolder + "/" + tempModelName +".uml";
			File f = new File(newSourceUMLPath.replace("file:", ""));
			StreamResult result = new StreamResult(f);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);
			
			setModelName(newModelName);
			setSourceUMLPath(newSourceUMLPath);
		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Generates a unique ID string to use for xmi:id.
	 * @return The resulting unique ID string
	 */
	private String getUniqueId() {
		return "_"+UUID.randomUUID().toString().toUpperCase();
	}
	
	/**
	 * Gets the correct string for the node incoming property, containing 
	 * the incoming edge ids and not the node ids.
	 * @param nodeId - The node's xmi:id
	 * @param incomingNodesIds - The list with the xmi:ids of the incoming nodes
	 * @return incomingEdgeIdsString - The resulting string
	 */
	private String getNodeIncomingEdgeIds(String nodeId, List<String> incomingNodesIds) {
		String incomingEdgeIdsString = "";
		
		if(incomingNodesIds!=null && incomingNodesIds.isEmpty()==false) {
			List<String> incomingEdgeIds = new ArrayList<String>();
			
			for (String incomingNodeId : incomingNodesIds) {
				for (XMIEdge edge : sourceUMLEdges){
					if(incomingNodeId.equals(edge.getSource()) && edge.getTarget().equals(nodeId)) {
						incomingEdgeIds.add(edge.getId());
					}
				}
			}
	
			StringBuilder stringJoiner = new StringBuilder();
			for (String incomingEdgeId : incomingEdgeIds) {
				stringJoiner.append(incomingEdgeId + " ");
			}
			String string = stringJoiner.toString();
			if (string.length() > 0)
				incomingEdgeIdsString = string.substring(0, string.length() - 1);
			else
				incomingEdgeIdsString = string;
		}
		
		return incomingEdgeIdsString;
	}
	
	/**
	 * Gets the correct string for the node outgoing property, containing 
	 * the outgoing edge ids and not the node ids.
	 * @param nodeId - The node's xmi:id
	 * @param outgoingNodesIds - The list with the xmi:ids of the outgoing nodes
	 * @return outgoingEdgeIdsString - The resulting string.
	 */
	private String getNodeOutgoingEdgeIds(String nodeId, List<String> outgoingNodesIds) {
		String outgoingEdgeIdsString = "";
		
		if(outgoingNodesIds!=null && outgoingNodesIds.isEmpty()==false) {
			List<String> outgoingEdgeIds = new ArrayList<String>();

			for (String outgoingNodeId : outgoingNodesIds) {
				for (XMIEdge edge : sourceUMLEdges){
					if(outgoingNodeId.equals(edge.getTarget()) && edge.getSource().equals(nodeId)) {
						outgoingEdgeIds.add(edge.getId());
					}
				}
			}
			
			StringBuilder stringJoiner = new StringBuilder();
			for (String outgoingEdgeId : outgoingEdgeIds) {
				stringJoiner.append(outgoingEdgeId + " ");
			}
			String string = stringJoiner.toString();
			if (string.length() > 0)
				outgoingEdgeIdsString = string.substring(0, string.length() - 1);
			else
				outgoingEdgeIdsString = string;
		}
		
		return outgoingEdgeIdsString;
	}
	
	/**
	 * Creates the Papyrus Model and fills the diagrams.
	 * If the Model already exists, then loads it.
	 * @throws ModelMultiException - If the loading of existing model fails
	 * @throws ServiceException 
	 */
	private void createAndOpenPapyrusModel(IProgressMonitor monitor) throws ModelMultiException, ServiceException {
		monitor.beginTask("Generating Papyrus Model", 100);
		PapyrusModelCreator papyrusModelCreator;
		if (projectName.equals(folderName)) {
			papyrusModelCreator = new PapyrusModelCreator(projectName + "/" + modelName);
		} else {
			papyrusModelCreator = new PapyrusModelCreator(projectName + "/" + folderName + "/" + modelName);
		}
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
		
		monitor.subTask("Creating new Papyrus project...");
		IProject project = ProjectUtils.createProject(projectName);
		ProjectUtils.openProject(project);
		monitor.worked(20);
		
		if(sourceUMLType.equalsIgnoreCase("uml:Activity")) {
			PreferencesManager.setValue(PreferencesManager.ACTIVITY_DIAGRAM_PREF, true);
			PreferencesManager.setValue(PreferencesManager.USE_CASE_DIAGRAM_PREF, false);
		}
		else if (sourceUMLType.equalsIgnoreCase("uml:Use Case")) { 
			PreferencesManager.setValue(PreferencesManager.ACTIVITY_DIAGRAM_PREF, false);
			PreferencesManager.setValue(PreferencesManager.USE_CASE_DIAGRAM_PREF, true);
		}
		createAndOpenPapyrusModel(new SubProgressMonitor(monitor, 80));
		SettingsRegistry.clear();
		
		monitor.subTask("Cleaning up");
		String filenameToDelete = sourceUMLPath.replace("file:/", "");
		filenameToDelete = filenameToDelete.substring(0, filenameToDelete.lastIndexOf('/'))+"/"+tempModelName+".uml";
		FileUtils.deleteFile(java.nio.file.Paths.get(filenameToDelete));
		monitor.worked(100);
		
		return Status.OK_STATUS;
	}
}