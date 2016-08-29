package eu.scasefp7.eclipse.umlrec;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.MultiPart;

import eu.scasefp7.eclipse.umlrec.parser.ActivityParser;
import eu.scasefp7.eclipse.umlrec.parser.UseCaseParser;
import eu.scasefp7.eclipse.umlrec.parser.XMIActivityNode;
import eu.scasefp7.eclipse.umlrec.parser.XMIEdge;
import eu.scasefp7.eclipse.umlrec.parser.XMIUseCaseNode;

import javax.imageio.ImageIO;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.awt.Point;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class UMLRecognizer {

	/** Store the image file name */
	protected String _fileName = null;

	/** The UML Server url */
	final String BASE_URI = "http://109.231.126.105:8080/UMLServer/"; // TODO - get this from preferences
	private Image image = new Image();
	private String xmi;
	private boolean isUseCase;
	private String sourceUMLType;
	private Display disp;
	private List<XMIActivityNode> sourceUMLActivityNodes = new ArrayList<XMIActivityNode>();
	private List<XMIUseCaseNode> sourceUMLUseCaseNodes = new ArrayList<XMIUseCaseNode>();
	private List<XMIEdge> sourceUMLEdges = new ArrayList<XMIEdge>();
	private static Map<String, Point> layoutController = new HashMap<String, Point>();
	
	public static final boolean SHOW_IMAGES = false;
	public static final int THRESH = 200;
	public static final double SIZE_RATE = 15.0;
	public static final double DIST_NEIGHBOR_OBJECTS = 5.0;
	public static final double COVER_AREA_THR = 40.0;

	/**
	 * 
	 */
	public UMLRecognizer(Display disp) {
		this.disp = disp;
	}

	public void setIsUseCase(boolean isUseCase) {
		this.isUseCase = isUseCase;
	}

	public void setImage(String fileName) throws RecognitionException {
		image.setName(fileName);
		String[] split = fileName.split("\\.");
		image.setFormat(split[split.length - 1]);

	}

	public void process() throws MissingRecognizerDataException, RecognitionException, IOException {
		// The process function returns
		// 0 for a successful run,
		// -2 if TessData files are missing OR the program failed to analyse the
		// text of the diagram and
		// -1 if an unknown error occurred.

		Client c = Client.create();

		WebResource service = c.resource(BASE_URI);

		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		URL url = new File(image.getName()).toURI().toURL();
		BufferedImage bi = ImageIO.read(url);
		ImageIO.write(bi, image.getFormat(), bas);
		byte[] logo = bas.toByteArray();

		// Construct a MultiPart with two body parts
		MultiPart multiPart = new MultiPart().bodyPart(new BodyPart(image, MediaType.APPLICATION_XML_TYPE))
				.bodyPart(new BodyPart(logo, MediaType.APPLICATION_OCTET_STREAM_TYPE));

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			if (isUseCase) {
				sourceUMLType = "uml:Use Case";
				// POST the request
				ClientResponse response = service.path("/usecase").type("multipart/mixed").post(ClientResponse.class,
						multiPart);
				xmi = response.getEntity(String.class);

				Document doc = docBuilder.parse(new InputSource(new StringReader(xmi)));
				UseCaseParser uCaseParser = new UseCaseParser();
				uCaseParser.Parsexmi(doc);
				ArrayList<XMIUseCaseNode> nodes = uCaseParser.getNodes();
				ArrayList<XMIEdge> edges = uCaseParser.getEdges();
				
				if (uCaseParser.checkParsedXmiForPapyrus(disp)) {
					sourceUMLUseCaseNodes = nodes;
					sourceUMLEdges = edges;
				}
			} else {
				sourceUMLType = "uml:Activity";
				// POST the request
				ClientResponse response = service.path("/activity").type("multipart/mixed").post(ClientResponse.class,
						multiPart);
				xmi = response.getEntity(String.class);
				
				Document doc = docBuilder.parse(new InputSource(new StringReader(xmi)));
				ActivityParser activityParser = new ActivityParser();
				activityParser.Parsexmi(doc);
				ArrayList<XMIActivityNode> nodes = activityParser.getNodes();
				ArrayList<XMIEdge> edges = activityParser.getEdges();
				
				if (activityParser.checkParsedXmiForPapyrus(disp)) {
					sourceUMLActivityNodes = nodes;
					sourceUMLEdges = edges;
				}

			}
			makePapyrusCompliantUML();

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return;
	}

	public String getXMIcontent() {
		return this.xmi;
	}

	public void setParameters(boolean isUseCase, boolean showImages, int thresh, double sizeRate,
			double distNeightborObjects, double coverAreaThr) {
		this.isUseCase = isUseCase;
		image.setImageThreshold(thresh);
		image.setRectangleRate(sizeRate);
		image.setMinDistance(distNeightborObjects);
		image.setMinRate(coverAreaThr);

	}

	public Image getImage() {
		return image;
	}

	public void setXmi(String xmi) {
		this.xmi = xmi;
	}

	/**
	 * Generates a Papyrus compliant UML file from the original UML edges and nodes.
	 * @throws ParserConfigurationException 
	 * @throws TransformerException 
	 */
	private void makePapyrusCompliantUML() {
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
					List<String> outgoingSolidNodesIds = node.getOutgoingSolidNodes();
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
					List<String> connectedSolidNodesIds = node.getConnectedSolidNodes();
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
					List<String> outgoingDashedNodesIds = node.getOutgoingDashedNodes();
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
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(2));

			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc.getDocumentElement());
			transformer.transform(source, result);
			xmi = sw.toString();

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

	public static Map<String, Point> getLayoutController() {
		return layoutController;
	}

}
