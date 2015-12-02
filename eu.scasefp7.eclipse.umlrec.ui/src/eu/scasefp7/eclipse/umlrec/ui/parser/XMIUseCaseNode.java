package eu.scasefp7.eclipse.umlrec.ui.parser;

import java.awt.Point;
import java.util.ArrayList;

public class XMIUseCaseNode extends XMINode {
	private ArrayList<String> incomingSolid = new ArrayList<String>();
	private ArrayList<String> outgoingSolid = new ArrayList<String>();
	private ArrayList<String> incomingDashed = new ArrayList<String>();
	private ArrayList<String> outgoingDashed = new ArrayList<String>();
	private ArrayList<String> connectedSolid = new ArrayList<String>();
	private ArrayList<String> connectedDashed = new ArrayList<String>();

	public XMIUseCaseNode() {
		super();
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

	// Getters and Setters
	public ArrayList<String> getincomingSolidNode() {
		return this.incomingSolid;
	}
	
	public void setincomingSolidNode(String incomingSolid) {

		this.incomingSolid.add(incomingSolid);
	}

	public ArrayList<String> getoutgoingSolidNode() {
		return this.outgoingSolid;
	}
	
	public void setoutgoingSolidNode(String outgoingSolid) {

		this.outgoingSolid.add(outgoingSolid);
	}

	public ArrayList<String> getincomingDashedNode() {
		return this.incomingDashed;
	}

	public void setincomingDashedNode(String incomingDashed) {

		this.incomingDashed.add(incomingDashed);
	}
	
	public ArrayList<String> getoutgoingDashedNode() {
		return this.outgoingDashed;
	}
	
	public void setoutgoingDashedNode(String outgoingDashed) {

		this.outgoingDashed.add(outgoingDashed);
	}

	public ArrayList<String> getconnectedSolidNode() {
		return this.connectedSolid;
	}
	
	public void setconnectedSolidNode(String connectedSolid) {

		this.connectedSolid.add(connectedSolid);
	}

	public ArrayList<String> getconnectedDashedNode() {
		return this.connectedDashed;
	}

	public void setconnectedDashedNode(String connectedDashed) {

		this.connectedDashed.add(connectedDashed);
	}
}

