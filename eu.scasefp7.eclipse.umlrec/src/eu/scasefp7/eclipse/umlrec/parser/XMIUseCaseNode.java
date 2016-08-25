package eu.scasefp7.eclipse.umlrec.parser;

import java.awt.Point;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMIUseCaseNode extends XMINode {
	public static final String ACTOR_TYPE = "uml:Actor";
	public static final String USE_CASE_TYPE = "uml:UseCase";
// following are not yet parsed
//	public static final String PACKAGE_TYPE = "uml:Package";
//	public static final String INTERFACE_TYPE = "uml:Interface";
	
	private ArrayList<String> incomingSolid = new ArrayList<String>();
	private ArrayList<String> outgoingSolid = new ArrayList<String>();
	private ArrayList<String> incomingDashed = new ArrayList<String>();
	private ArrayList<String> outgoingDashed = new ArrayList<String>();
	private ArrayList<String> connectedSolid = new ArrayList<String>();
	private ArrayList<String> connectedDashed = new ArrayList<String>();
	
	public XMIUseCaseNode() {
		super();
	}
	
	public XMIUseCaseNode(String id) {
		super(id);
	}
	
	public XMIUseCaseNode(String type, String id, String name, String annotations, ArrayList<Point> coordinates, ArrayList<String> incomingSolid,
			ArrayList<String> outgoingSolid, ArrayList<String> incomingDashed, ArrayList<String> outgoingDashed,
			ArrayList<String> connectedSolid, ArrayList<String> connectedDashed) {
		super(type, id, name, annotations, coordinates);
		this.incomingSolid = incomingSolid;
		this.outgoingSolid = outgoingSolid;
		this.incomingDashed = incomingDashed;
		this.outgoingDashed = outgoingDashed;
		this.connectedSolid = connectedSolid;
		this.connectedDashed = connectedDashed;
	}

	public static boolean isUseCaseNodeType(Element e) {
		switch (e.getAttribute("xmi:type")) {
			case ACTOR_TYPE: return true;
			case USE_CASE_TYPE: return true;
// following are not yet parsed
//			case PACKAGE_TYPE: return true;
//			case INTERFACE_TYPE: return true;
			default: return false;
		}
	}
	
	public void connectEdge(XMIEdge e) {
		boolean thisNodeIsSource = e.getSource().equals(this.id);
		boolean thisNodeIsTarget = e.getTarget().equals(this.id);
		String idOfNodeAtOtherEnd = thisNodeIsSource ? e.getTarget() : (thisNodeIsTarget ? e.getSource() : ""); 
		switch (e.getType()) {
			case XMIEdge.ASSOCIATION_TYPE:
				addConnectedSolidNode(idOfNodeAtOtherEnd);
				break;
			case XMIEdge.GENERALIZATION_TYPE:
				if (thisNodeIsSource)
					addOutgoingSolidNode(idOfNodeAtOtherEnd);
				else if (thisNodeIsTarget)
					addIncomingSolidNode(idOfNodeAtOtherEnd);
				break;
			case XMIEdge.INCLUDE_TYPE:
				if (thisNodeIsSource)
					addOutgoingDashedNode(idOfNodeAtOtherEnd);
				else if (thisNodeIsTarget)
					addIncomingDashedNode(idOfNodeAtOtherEnd);
				break;
			case XMIEdge.EXTEND_TYPE:
				if (thisNodeIsSource)
					addOutgoingDashedNode(idOfNodeAtOtherEnd);
				else if (thisNodeIsTarget)
					addIncomingDashedNode(idOfNodeAtOtherEnd);
				break;
		}
	}
	
	// Getters and Setters
	public ArrayList<String> getIncomingSolidNodes() {
		return this.incomingSolid;
	}

	public ArrayList<String> getIncomingDashedNodes() {
		return this.incomingDashed;
	}
	
	public ArrayList<String> getOutgoingSolidNodes() {
		return this.outgoingSolid;
	}

	public ArrayList<String> getOutgoingDashedNodes() {
		return this.outgoingDashed;
	}
	
	public ArrayList<String> getConnectedSolidNodes() {
		return this.connectedSolid;
	}
	
	public ArrayList<String> getConnectedDashedNodes() {
		return this.connectedDashed;
	}

	public void addIncomingSolidNode(String incomingSolid) {
		this.incomingSolid.add(incomingSolid);
	}
	
	public void addIncomingDashedNode(String incomingDashed) {
		this.incomingDashed.add(incomingDashed);
	}
	
	public void addOutgoingSolidNode(String outgoingSolid) {
		this.outgoingSolid.add(outgoingSolid);
	}

	public void addOutgoingDashedNode(String outgoingDashed) {
		this.outgoingDashed.add(outgoingDashed);
	}

	public void addConnectedSolidNode(String connectedSolid) {
		this.connectedSolid.add(connectedSolid);
	}

	public void addConnectedDashedNode(String connectedDashed) {
		this.connectedDashed.add(connectedDashed);
	}
}
