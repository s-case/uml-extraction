package eu.scasefp7.eclipse.umlrec.ui.parser;

import java.util.ArrayList;

public class XMIUseCaseNode {
	private String type;
	private String id;
	private String name;
	private ArrayList<String> incomingSolid = new ArrayList<String>();
	private ArrayList<String> outgoingSolid = new ArrayList<String>();
	private ArrayList<String> incomingDashed = new ArrayList<String>();
	private ArrayList<String> outgoingDashed = new ArrayList<String>();
	private ArrayList<String> connectedSolid = new ArrayList<String>();
	private ArrayList<String> connectedDashed = new ArrayList<String>();
	private String annotations;

	public XMIUseCaseNode() {
		this.type = "";
		this.id = "";
		this.name = "";
	}

	public XMIUseCaseNode(String type, String id, String name, String annotations, ArrayList<String> incomingSolid,
			ArrayList<String> outgoingSolid, ArrayList<String> incomingDashed, ArrayList<String> outgoingDashed,
			ArrayList<String> connectedSolid, ArrayList<String> connectedDashed) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.annotations = annotations;
		this.incomingSolid = incomingSolid;
		this.outgoingSolid = outgoingSolid;
		this.incomingDashed = incomingDashed;
		this.outgoingDashed = outgoingDashed;
		this.connectedSolid = connectedSolid;
		this.connectedDashed = connectedDashed;
	}

	// Getters and Setters
	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public ArrayList<String> getincomingSolidNode() {
		return this.incomingSolid;
	}

	public ArrayList<String> getoutgoingSolidNode() {
		return this.outgoingSolid;
	}

	public ArrayList<String> getincomingDashedNode() {
		return this.incomingDashed;
	}

	public ArrayList<String> getoutgoingDashedNode() {
		return this.outgoingDashed;
	}

	public ArrayList<String> getconnectedSolidNode() {
		return this.connectedSolid;
	}

	public ArrayList<String> getconnectedDashedNode() {
		return this.connectedDashed;
	}
	
	public String getannotations() {
		return this.annotations;
	}

	public String getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setincomingSolidNode(String incomingSolid) {

		this.incomingSolid.add(incomingSolid);
	}

	public void setoutgoingSolidNode(String outgoingSolid) {

		this.outgoingSolid.add(outgoingSolid);
	}

	public void setincomingDashedNode(String incomingDashed) {

		this.incomingDashed.add(incomingDashed);
	}

	public void setoutgoingDashedNode(String outgoingDashed) {

		this.outgoingDashed.add(outgoingDashed);
	}

	public void setconnectedSolidNode(String connectedSolid) {

		this.connectedSolid.add(connectedSolid);
	}

	public void setconnectedDashedNode(String connectedDashed) {

		this.connectedDashed.add(connectedDashed);
	}
	
	public void setannotations(String annotation) {

		this.annotations=annotation;
	}

	public void setId(String id) {
		this.id = id;
	}

}

