/**
 * 
 */
package eu.scasefp7.eclipse.umlrec.ui.parser;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tsirelis
 *
 */
public class XMINode {
	private String type;
	private String id;
	private String name;
	private String annotations;
	private List<Point> coordinates =  new ArrayList<Point>();
	
	/**
	 * The constructors
	 */
	public XMINode() {
		this.type = "";
		this.id = "";
		this.name = "";
		this.annotations = "";
	}
	
	public XMINode(String type, String id, String name, String annotations, ArrayList<Point> coordinates) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.annotations = annotations;
		this.coordinates = coordinates;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the annotations
	 */
	public String getAnnotations() {
		return annotations;
	}
	/**
	 * @param annotations the annotations to set
	 */
	public void setAnnotations(String annotations) {
		this.annotations = annotations;
	}
	/**
	 * @return the coordinates
	 */
	public List<Point> getCoordinates() {
		return coordinates;
	}
	/**
	 * @param coordinates the coordinates to set
	 */
	public void setCoordinates(ArrayList<Point> coordinates) {
		this.coordinates = coordinates;
	}
}
