package eu.scasefp7.eclipse.umlrec.ui.parser;

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

	ArrayList<XMIEdge> edges = new ArrayList<XMIEdge>();
	ArrayList<XMIUseCaseNode> nodes = new ArrayList<XMIUseCaseNode>();
	String type= "use-case";

	public void Parsexmi(Document doc) {

		NodeList edgeList = doc.getElementsByTagName("edge");
		NodeList nodeList = doc.getElementsByTagName("node");

		for (int temp = 0; temp < edgeList.getLength(); temp++) {

			Node nNode = edgeList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				XMIEdge edge = new XMIEdge(eElement.getAttribute("name"), eElement.getAttribute("target"),
						eElement.getAttribute("source"), eElement.getAttribute("xmi:id"),
						eElement.getAttribute("xmi:type"));
				System.out.println("\nCurrent Element :" + edge.getName() + ", type: " + edge.getType());
				edges.add(edge);
			}
		}

		for (int temp = 0; temp < nodeList.getLength(); temp++) {

			Node nNode = nodeList.item(temp);

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

				incomingSolid = substring(incomingSolidString, incomingSolid);
				outgoingSolid = substring(outgoingSolidString, outgoingSolid);
				incomingDashed = substring(incomingDashedString, incomingDashed);
				outgoingDashed = substring(outgoingDashedString, outgoingDashed);
				connectedSolid = substring(connectedSolidString, connectedSolid);
				connectedDashed = substring(connectedDashedString, connectedDashed);

				XMIUseCaseNode node = new XMIUseCaseNode(eElement.getAttribute("xmi:type"), eElement.getAttribute("xmi:id"),
						eElement.getAttribute("name"), eElement.getAttribute("annotations"), incomingSolid, outgoingSolid, incomingDashed, outgoingDashed,
						connectedSolid, connectedDashed);
				System.out.println("\nCurrent Element :" + node.getName() + ", type: " + node.getType());
				nodes.add(node);
			}
		}
		System.out.println("\nFinish!");

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

	private ArrayList<String> substring(String s, ArrayList<String> list) {
		int previousOccurance = 0;
		int counter = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '_') {
				counter++;
				if (counter > 1 && previousOccurance < i) {
					list.add(s.substring(previousOccurance, i - 1));
				}
				previousOccurance = i;
			}
		}
		list.add(s.substring(previousOccurance, s.length()));

		return list;
	}
	
	public boolean checkParsedXmi() {
		final Display disp = Display.getCurrent();
		boolean xmiIsOk = true;
		boolean edgesOk = true;
		boolean annotationsOk = true;
		for (XMIEdge edge:edges){
			if (edge.getSource().isEmpty() || edge.getTarget().isEmpty()){
				edgesOk=false;
				
				disp.syncExec(new Runnable() {
					@Override
					public void run() {
						MessageDialog.openInformation(disp.getActiveShell(), "Error occured",
								"Edge with id "+ edge.getId() +" should have both source and target nodes!");
					}
				});
				return false;
			}
		}
		for (XMIUseCaseNode node: nodes){
			if (node.getType().equals("uml:UseCaseNode")){
				if (node.getannotations().equals("")) {
					annotationsOk= false;
					
					disp.syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openInformation(disp.getActiveShell(), "Error occured",
									"Provide annotations for all use cases first!");
						}
					});
					return false;
				}
			}
		}
		xmiIsOk=edgesOk && annotationsOk;
		return xmiIsOk;
	}
	
	/**
	 * 
	 * @return the diagram's type
	 */

	public String getType(){
		return type;
	}

	
}
