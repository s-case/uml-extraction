package eu.scasefp7.eclipse.umlrec.ui.parser;

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

	public XMIEdge() {
		this.name = "";
		this.target = "";
		this.source = "";
		this.id = "";
		this.type = "";

	}

	public XMIEdge(String name, String target, String source, String id, String type) {
		this.name = name;
		this.target = target;
		this.source = source;
		this.id = id;
		this.type = type;

	}

	// Getters and Setters
	public String getName() {
		return this.name;
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

	public Object getSourceNode() {
		return this.sourceNode;
	}

	public String getId() {
		return this.id;
	}

	public String getType() {
		return this.type;
	}
	public String getCondition() {
		return this.condition;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTargetNode(Object target) {
		this.targetNode = target;
	}

	public void setSourceNode(Object source) {
		this.sourceNode = source;
	}

	public void setId(String id) {
		this.id = id;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setType(String type) {
		this.type = type;
	}
}
