package eu.scasefp7.eclipse.umlrec.ui.parser;

import java.awt.Point;
import java.util.ArrayList;

/**
 * A class that represents a transition in the ontology.
 * 
 * @author mkoutli
 *
 */
public class XMIEdge {
	private String name;
	private String target;
	private String source;
	private Object targetNode;
	private Object sourceNode;
	private String id;
	private String type;
	private String condition;
	private ArrayList<Point> coordinates;

	public XMIEdge() {
		this.name = "";
		this.target = "";
		this.source = "";
		this.id = "";
		this.type = "";
		this.coordinates = new ArrayList<Point>();
	}

	public XMIEdge(String name, String target, String source, String id, String type, ArrayList<Point> coordinates) {
		this.name = name;
		this.target = target;
		this.source = source;
		this.id = id;
		this.type = type;
		this.coordinates = coordinates;
	}

	// Getters and Setters
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getTarget() {
		return this.target;
	}

	public String getSource() {
		return this.source;
	}
	
	public Object getTargetNode() {
		return this.targetNode;
	}
	
	public void setTargetNode(Object target) {
		this.targetNode = target;
	}

	public Object getSourceNode() {
		return this.sourceNode;
	}
	
	public void setSourceNode(Object source) {
		this.sourceNode = source;
	}

	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getCondition() {
		return this.condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	/**
	 * @return the coordinates
	 */
	public ArrayList<Point> getCoordinates() {
		return coordinates;
	}

	/**
	 * @param coordinates the coordinates to set
	 */
	public void setCoordinates(ArrayList<Point> coordinates) {
		this.coordinates = coordinates;
	}
}
