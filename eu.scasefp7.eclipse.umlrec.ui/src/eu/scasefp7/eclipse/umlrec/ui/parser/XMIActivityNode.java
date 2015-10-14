package eu.scasefp7.eclipse.umlrec.ui.parser;

import java.util.ArrayList;

/**
 * A class that represents an activity/condition/initial/final node in the
 * ontology.
 * 
 * @author mkoutli
 *
 */
public class XMIActivityNode {
	private String type;
	private String id;
	private String name;
	private ArrayList<String> incoming = new ArrayList<String>();
	private ArrayList<String> outgoing = new ArrayList<String>();
	private ArrayList<XMIActivityNode> sourceNodes= new ArrayList<XMIActivityNode>();
	private ArrayList<XMIActivityNode> destinationNodes= new ArrayList<XMIActivityNode>();

	public XMIActivityNode() {
		this.type = "";
		this.id = "";
		this.name = "";
	}

	public XMIActivityNode(String type, String id, String name, ArrayList<String> incoming, ArrayList<String> outgoing) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.incoming = incoming;
		this.outgoing = outgoing;
	}

	// Getters and Setters
	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public ArrayList<String> getIncoming() {
		return this.incoming;
	}

	public ArrayList<String> getOutgoing() {
		return this.outgoing;
	}
	public ArrayList<XMIActivityNode> getSourceNodes() {
		return this.sourceNodes;
	}

	public ArrayList<XMIActivityNode> getDestinationNodes() {
		return this.destinationNodes;
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

	public void setIncoming(String oldValue, String newValue) {
		this.incoming.remove(oldValue);
		this.incoming.add(newValue);
	}

	public void setOutgoing(String oldValue, String newValue) {
		this.outgoing.remove(oldValue);
		this.outgoing.add(newValue);
	}

	public void setId(String id) {
		this.id = id;
	}
	public void setSourceNodes(XMIActivityNode node) {
		this.sourceNodes.add(node);
	}
	public void setDestinationNodes(XMIActivityNode node) {
		this.destinationNodes.add(node);
	}

}

