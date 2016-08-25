package eu.scasefp7.eclipse.umlrec.parser;

import java.awt.Point;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parses the xmi use-case uml diagram.
 * 
 * @author mkoutli
 *
 */
public class UseCaseParser {

	String type = "use-case";
	ArrayList<XMIEdge> edges = new ArrayList<XMIEdge>();
	ArrayList<XMIUseCaseNode> nodes = new ArrayList<XMIUseCaseNode>();
	
	private void extractAttributes(Element e, XMIUseCaseNode node, XMIEdge edge) {
		if ((node == null && edge == null) ||(node != null && edge != null)) {
			throw new IllegalArgumentException("Either 'node' or 'edge' should be provided; the other one should be null.");
		}
		// if element is of type node
		if (node != null) {
			node.setId(e.getAttribute("xmi:id"));
			node.setType(e.getAttribute("xmi:type"));
			node.setName(e.getAttribute("name"));
			node.setAnnotations(e.getAttribute("annotations"));
			
			// put additional parsing, particular to each case, below
			switch (e.getAttribute("xmi:type")) {
				case XMIUseCaseNode.ACTOR_TYPE:
					break;
				case XMIUseCaseNode.USE_CASE_TYPE:
					break;
// not yet parsed
//				case XMIUseCaseNode.PACKAGE_TYPE:
//					break;
//				case XMIUseCaseNode.INTERFACE_TYPE:
//					break;
				default:
			}
		//else if element is of type edge
		} else if (edge != null) {
			edge.setId(e.getAttribute("xmi:id"));
			edge.setType(e.getAttribute("xmi:type"));
			edge.setName(e.getAttribute("name"));

			// put additional parsing, particular to each case, below
			switch (e.getAttribute("xmi:type")) {
				case XMIEdge.GENERALIZATION_TYPE:
					edge.setTarget(e.getAttribute("general"));
					Node generalizationParent = e.getParentNode();
					if (generalizationParent.getNodeType() == Node.ELEMENT_NODE) {
						Element parentElement = (Element)generalizationParent;
						edge.setSource(parentElement.getAttribute("xmi:id"));
					}
					break;
				case XMIEdge.INCLUDE_TYPE:
					edge.setTarget(e.getAttribute("addition"));
					Node includeParent = e.getParentNode();
					if (includeParent.getNodeType() == Node.ELEMENT_NODE) {
						Element parentElement = (Element)includeParent;
						edge.setSource(parentElement.getAttribute("xmi:id"));
					}
					break;
				case XMIEdge.EXTEND_TYPE:
					edge.setTarget(e.getAttribute("extendedCase"));
					Node extendParent = e.getParentNode();
					if (extendParent.getNodeType() == Node.ELEMENT_NODE) {
						Element parentElement = (Element)extendParent;
						edge.setSource(parentElement.getAttribute("xmi:id"));
					}
					break;
				case XMIEdge.ASSOCIATION_TYPE:
					NodeList children = e.getChildNodes();
					for (int i=0; i<children.getLength(); i++) {
						Node childNode = children.item(i);
						if (childNode.getNodeType() == Node.ELEMENT_NODE && ((Element)childNode).getAttribute("xmi:type").equals("uml:Property")) {
							if (edge.getSource().isEmpty()) {
								edge.setSource(((Element)childNode).getAttribute("type"));
							} else if (edge.getTarget().isEmpty()) {
								edge.setTarget(((Element)childNode).getAttribute("type"));
							}
						}
					}
					break;
// not yet parsed
//			case XMIEdge.REALIZATION_TYPE:
//				break;
//			case XMIEdge.ABSTRACTION_TYPE:
//				break;
//			case XMIEdge.USAGE_TYPE:
//				break;
//			case XMIEdge.DEPENDENCY_TYPE:
//				break;
				default:
				
			}
		}
	}
	
	/**
	 * Parses the given xmi document to fill the edges and nodes lists of this UseCaseParser object.
	 * @param doc
	 * The xmi document to be parsed.
	 */
	public void Parsexmi(Document doc) {

		NodeList edgeList = doc.getElementsByTagName("edge");
		NodeList nodeList = doc.getElementsByTagName("node");

		// adds all edges
		for (int i = 0; i < edgeList.getLength(); i++) {

			Node nNode = edgeList.item(i);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				String coordinates = eElement.getAttribute("coordinates");
				ArrayList<Point> coordinatesList = new ArrayList<Point>();
				coordinatesList = ActivityParser.substringCoordinates(coordinates, coordinatesList);
				XMIEdge edge = new XMIEdge(eElement.getAttribute("name"), eElement.getAttribute("target"),
						eElement.getAttribute("source"), eElement.getAttribute("xmi:id"),
						eElement.getAttribute("xmi:type"), coordinatesList);
				System.out.println("\nCurrent Element :" + edge.getName() + ", type: " + edge.getType());
				edges.add(edge);
			}
		}

		for (int i = 0; i < nodeList.getLength(); i++) {

			Node nNode = nodeList.item(i);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				String incomingSolidString = eElement.getAttribute("incomingSolid");
				String outgoingSolidString = eElement.getAttribute("outgoingSolid");
				String incomingDashedString = eElement.getAttribute("incomingDashed");
				String outgoingDashedString = eElement.getAttribute("outgoingDashed");
				String connectedSolidString = eElement.getAttribute("connectedSolid");
				String connectedDashedString = eElement.getAttribute("connectedDashed");
				ArrayList<String> incomingSolid = new ArrayList<String>();
				ArrayList<String> outgoingSolid = new ArrayList<String>();
				ArrayList<String> incomingDashed = new ArrayList<String>();
				ArrayList<String> outgoingDashed = new ArrayList<String>();
				ArrayList<String> connectedSolid = new ArrayList<String>();
				ArrayList<String> connectedDashed = new ArrayList<String>();

				incomingSolid = ActivityParser.substring(incomingSolidString, incomingSolid);
				outgoingSolid = ActivityParser.substring(outgoingSolidString, outgoingSolid);
				incomingDashed = ActivityParser.substring(incomingDashedString, incomingDashed);
				outgoingDashed = ActivityParser.substring(outgoingDashedString, outgoingDashed);
				connectedSolid = ActivityParser.substring(connectedSolidString, connectedSolid);
				connectedDashed = ActivityParser.substring(connectedDashedString, connectedDashed);
				
				String coordinates = eElement.getAttribute("coordinates");
				ArrayList<Point> coordinatesList = new ArrayList<Point>();
				coordinatesList = ActivityParser.substringCoordinates(coordinates, coordinatesList);

				XMIUseCaseNode node = new XMIUseCaseNode(eElement.getAttribute("xmi:type"),
						eElement.getAttribute("xmi:id"), eElement.getAttribute("name"),
						eElement.getAttribute("annotations"), coordinatesList, incomingSolid, outgoingSolid, incomingDashed,
						outgoingDashed, connectedSolid, connectedDashed);
				System.out.println("\nCurrent Element :" + node.getName() + ", type: " + node.getType());
				nodes.add(node);
			}
		}

		for (XMIEdge e : edges) {
			for (XMIUseCaseNode n : nodes) {
				if (e.getSource().equals(n.getId())) {
					e.setSourceNode(n);
				}
				if (e.getTarget().equals(n.getId())) {
					e.setTargetNode(n);
				}
			}
		}

		System.out.println("\nFinish!");
	}

	public void parsePapyrusXMI(Document doc) {

		NodeList edgeList = doc.getElementsByTagName("edge");
		NodeList nodeList = doc.getElementsByTagName("node");
		
		NodeList packElementList = doc.getElementsByTagName("packagedElement");
		NodeList generalizationList = doc.getElementsByTagName("generalization");
		NodeList extendList = doc.getElementsByTagName("extend");
		NodeList includeList = doc.getElementsByTagName("include");
		
/*
 * Types of packaged elements:
 * 		xmi:type="uml:Actor"				UseCaseNode			parsed
 * 		xmi:type="uml:Package"				UseCaseNode
 * 		xmi:type="uml:UseCase"				UseCaseNode			parsed
 * 		xmi:type="uml:Realization"			Edge
 * 		xmi:type="uml:Abstraction"			Edge
 * 		xmi:type="uml:Usage"				Edge
 * 		xmi:type="uml:Interface"			UseCaseNode
 * 		xmi:type="uml:Association"			Edge				parsed
 * 		xmi:type="uml:Dependency"			Edge
 */

/* OTHER ELEMENTS
		ownedComment												xmi:type="uml:Comment"
		ownedRule													xmi:type="uml:Constraint"
		extensionPoint 	(only in the context of use cases)			xmi:type="uml:ExtensionPoint"
		generalization 	(only in the context of use cases)			xmi:type="uml:Generalization"		parsed
		extend 			(only in the context of use cases)			xmi:type="uml:Extend"				parsed
		include			(only in the context of use cases)			xmi:type="uml:Include"				parsed
		ownedUseCase	(only in the context of interfaces)			xmi:type="uml:UseCase"
		ownedEnd		(only in the context of associations)		xmi:type="uml:Property"
		packageImport	(only in the context of packages)			xmi:type="uml:PackageImport"
		packageMerge	(only in the context of packages)			xmi:type="uml:PackageMerge"
		
*/

		for (int i=0; i<generalizationList.getLength(); i++) {
			Node generalizationNode = generalizationList.item(i);

			if (generalizationNode.getNodeType() == Node.ELEMENT_NODE) {
				Element generalizationElement = (Element)generalizationNode;

				if (!generalizationElement.getAttribute("xmi:type").isEmpty() && !generalizationElement.getAttribute("xmi:id").isEmpty()) {
					XMIEdge edge = new XMIEdge();
					extractAttributes(generalizationElement, null, edge);
					System.out.println("\nCurrent Element :" + edge.getName() + ", type: " + edge.getType());
					edges.add(edge);
				}
			}
		}

		for (int i=0; i<extendList.getLength(); i++) {
			Node extendNode = extendList.item(i);

			if (extendNode.getNodeType() == Node.ELEMENT_NODE) {
				Element extendElement = (Element)extendNode;

				if (!extendElement.getAttribute("xmi:type").isEmpty() && !extendElement.getAttribute("xmi:id").isEmpty()) {
					XMIEdge edge = new XMIEdge();
					extractAttributes(extendElement, null, edge);
					System.out.println("\nCurrent Element :" + edge.getName() + ", type: " + edge.getType());
					edges.add(edge);
				}
			}
		}

		for (int i=0; i<includeList.getLength(); i++) {
			Node includeNode = includeList.item(i);

			if (includeNode.getNodeType() == Node.ELEMENT_NODE) {
				Element includeElement = (Element)includeNode;

				if (!includeElement.getAttribute("xmi:type").isEmpty() && !includeElement.getAttribute("xmi:id").isEmpty()) {
					XMIEdge edge = new XMIEdge();
					extractAttributes(includeElement, null, edge);
					System.out.println("\nCurrent Element :" + edge.getName() + ", type: " + edge.getType());
					edges.add(edge);
				}
			}
		}

		for (int i=0; i<packElementList.getLength(); i++) {
			Node packElement = packElementList.item(i);

			if (packElement.getNodeType() == Node.ELEMENT_NODE) {
				Element pElement = (Element)packElement;

				if (!pElement.getAttribute("xmi:type").isEmpty() && !pElement.getAttribute("xmi:id").isEmpty()) {
					if (XMIEdge.isEdgeType(pElement)) {
						XMIEdge edge = new XMIEdge();
						extractAttributes(pElement, null, edge);
						System.out.println("\nCurrent Element :" + edge.getName() + ", type: " + edge.getType());
						edges.add(edge);
					}
	
					if (XMIUseCaseNode.isUseCaseNodeType(pElement)) {
						XMIUseCaseNode node = new XMIUseCaseNode();
						extractAttributes(pElement, node, null);
						System.out.println("\nCurrent Element :" + node.getName() + ", type: " + node.getType());
						nodes.add(node);
					}
				}
			}
			
		}

		for (XMIEdge e : edges) {
			if (!e.getSource().isEmpty()) {
				int index = nodes.indexOf(new XMIUseCaseNode(e.getSource()));
				if (index >= 0) {
					XMIUseCaseNode sourceNode = nodes.get(index);
					e.setSourceNode(sourceNode);
					sourceNode.connectEdge(e);
				}
			}
			if (!e.getTarget().isEmpty()) {
				int index = nodes.indexOf(new XMIUseCaseNode(e.getTarget()));
				if (index >= 0) {
					XMIUseCaseNode targetNode = nodes.get(index);
					e.setTargetNode(targetNode);
					targetNode.connectEdge(e);
				}
			}
		}

		System.out.println("\nFinish!");
	}
	
	/**
	 * 
	 * @return the nodes of the diagram.
	 */
	public ArrayList<XMIUseCaseNode> getNodes() {
		return nodes;
	}

	/**
	 * 
	 * @return the edges of the diagram
	 */
	public ArrayList<XMIEdge> getEdges() {
		return edges;
	}

//	private ArrayList<String> substring(String s, ArrayList<String> list) {
//		int previousOccurance = 0;
//		int counter = 0;
//		for (int i = 0; i < s.length(); i++) {
//			if (s.charAt(i) == '_') {
//				counter++;
//				if (counter > 1 && previousOccurance < i) {
//					list.add(s.substring(previousOccurance, i - 1));
//				}
//				previousOccurance = i;
//			}
//		}
//		list.add(s.substring(previousOccurance, s.length()));
//
//		return list;
//	}

	public boolean checkParsedXmi(boolean checkAnnotations, String diagramName) {
		final Display disp = Display.getCurrent();
		boolean xmiIsOk = true;
		boolean edgesOk = true;
		boolean annotationsOk = true;
		boolean actorNamesOk = true;
		for (XMIEdge edge : edges) {
			if (edge.getSource().isEmpty() || edge.getTarget().isEmpty()) {
				edgesOk = false;

				disp.syncExec(new Runnable() {
					@Override
					public void run() {
						MessageDialog.openInformation(disp.getActiveShell(), "Error occured",
								"Edge with id " + edge.getId() + " should have both source and target nodes!");
					}
				});
				return false;
			}
		}
		
		for (XMIUseCaseNode node : nodes) {
			String id = node.getId();
			if (node.getType().equals("uml:UseCaseNode")) {
				if (checkAnnotations) {
					if (node.getAnnotations().equals("")) {
						annotationsOk = false;

						disp.syncExec(new Runnable() {
							@Override
							public void run() {
								MessageDialog.openInformation(disp.getActiveShell(), "Error occured",
										"The use case node with id \"" + id + "\" should be annotated! Please annotate and re-try exporting diagram \"" + diagramName + "\"to the ontology.");
							}
						});
						return false;
					}
				}
			}
			if (node.getType().equals(XMIUseCaseNode.ACTOR_TYPE) && node.getName().isEmpty()){
				actorNamesOk = false;
				
				disp.syncExec(new Runnable() {
					@Override
					public void run() {
						
						MessageDialog.openInformation(disp.getActiveShell(), "Error occured",
								"The actor with id \"" + id + "\" should have a name! Please specify one and re-try exporting diagram \"" + diagramName + "\"to the ontology.");
					}
				});
				return false;
			}
		}
		xmiIsOk = edgesOk && annotationsOk && actorNamesOk;
		return xmiIsOk;
	}

	// If this method is called from a non-UI thread, the display parameter must be set
	// otherwise, it can be null
	public boolean checkParsedXmiForPapyrus(Display display) {
		final Display disp;
		if (display != null) {
			disp = display;
		} else  {
			disp = Display.getCurrent();
		}
		
		ArrayList<String> edgeIDsToDelete = new ArrayList<String>();
		boolean edgesOk = true;
		
		for (int i=edges.size()-1; i >= 0; i--) {
			if (edges.get(i).getSource().isEmpty() || edges.get(i).getTarget().isEmpty()) {
				edgeIDsToDelete.add(edges.get(i).getId());
				edges.remove(edges.get(i));
			}			
		}
		if (!edgeIDsToDelete.isEmpty()) {
			disp.syncExec(new Runnable() {
				@Override
				public void run() {
					String deletedIDs = "";
					boolean firstLine = true;
					for (String id : edgeIDsToDelete) {
						if (!firstLine)
							deletedIDs += "\n";
						else
							firstLine = false;
						deletedIDs += id;
					}
					MessageDialog.openInformation(disp.getActiveShell(), "Problematic edges found and removed",
							"The edges with the following IDs were removed, as they were missing a source node, target node or both:\n" + deletedIDs);
				}
			});
		}
		return edgesOk;
	}
	
	/**
	 * 
	 * @return the diagram's type
	 */

	public String getType() {
		return type;
	}

}
