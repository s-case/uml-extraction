package eu.scasefp7.eclipse.umlrec.parser;

import java.awt.Point;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parses the xmi activity uml diagram.
 * 
 * @author mkoutli
 *
 */
public class ActivityParser {

	ArrayList<XMIEdge> edges = new ArrayList<XMIEdge>();
	ArrayList<XMIEdge> edgesWithCondition = new ArrayList<XMIEdge>();
	ArrayList<XMIEdge> edgesWithoutCondition = new ArrayList<XMIEdge>();
	ArrayList<XMIActivityNode> nodes = new ArrayList<XMIActivityNode>();
	String type = "activity";

	public void Parsexmi( Document doc) {

		NodeList edgeList = doc.getElementsByTagName("edge");
		NodeList nodeList = doc.getElementsByTagName("node");

		for (int temp = 0; temp < edgeList.getLength(); temp++) {

			Node nNode = edgeList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				
				String coordinates = eElement.getAttribute("coordinates");
				ArrayList<Point> coordinatesList = new ArrayList<Point>();
				coordinatesList = substringCoordinates(coordinates, coordinatesList);
				
				XMIEdge edge = new XMIEdge(eElement.getAttribute("name"), eElement.getAttribute("target"),
						eElement.getAttribute("source"), eElement.getAttribute("xmi:id"),
						eElement.getAttribute("xmi:type"), coordinatesList);
				System.out.println("\nCurrent Element :" + edge.getName() + ", type: " + edge.getType());
				edges.add(edge);
			}
		}

		for (int temp = 0; temp < nodeList.getLength(); temp++) {

			Node nNode = nodeList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				String incomingString = eElement.getAttribute("incoming");
				String outgoingString = eElement.getAttribute("outgoing");
				ArrayList<String> incoming = new ArrayList<String>();
				ArrayList<String> outgoing = new ArrayList<String>();

				incoming = substring(incomingString, incoming);
				outgoing = substring(outgoingString, outgoing);
				
				String coordinates = eElement.getAttribute("coordinates");
				ArrayList<Point> coordinatesList = new ArrayList<Point>();
				coordinatesList = substringCoordinates(coordinates, coordinatesList);

				XMIActivityNode node = new XMIActivityNode(eElement.getAttribute("xmi:type"),
						eElement.getAttribute("xmi:id"), eElement.getAttribute("name"), eElement.getAttribute("annotations"), coordinatesList, incoming, outgoing);
				
				System.out.println("\nCurrent Element :" + node.getName() + ", type: " + node.getType());
				nodes.add(node);
			}
		}
		

		for (XMIEdge e : edges) {
			for (XMIActivityNode n : nodes) {
				if (e.getSource().equals(n.getId())) {
					e.setSourceNode(n);
				}
				if (e.getTarget().equals(n.getId())) {
					e.setTargetNode(n);
				}
			}
		}

		for (XMIActivityNode n : nodes) {
			ArrayList<String> incomingEdgesIds = n.getIncoming();
			ArrayList<String> outgoingEdgesIds = n.getOutgoing();

			for (String s : incomingEdgesIds) {
				for (XMIActivityNode nn : nodes) {
					if (s.equals(nn.getId())) {
						n.setSourceNodes(nn);
					}
				}
			}
			for (String s : outgoingEdgesIds) {
				for (XMIActivityNode nn : nodes) {
					if (s.equals(nn.getId())) {
						n.setDestinationNodes(nn);
					}

				}
			}

			// for (String s : incomingEdgesIds) {
			// for (XMIEdge e : edges) {
			// if (s.equals(e.getId())) {
			// n.setSourceNodes(e.getSourceNode());
			// }
			// }
			// }
			// for (String s : outgoingEdgesIds) {
			// for (XMIEdge e : edges) {
			// if (s.equals(e.getId())) {
			// n.setDestinationNodes(e.getTargetNode());
			// }
			//
			// }
			// }
		}

		boolean sourceOK = false;
		boolean targetOK = false;
		for (XMIEdge e : edges) {
			if (((XMIActivityNode) e.getSourceNode()) != null) {
				if (!(((XMIActivityNode) e.getSourceNode()).getType().equals("uml:DecisionNode")
						|| ((XMIActivityNode) e.getSourceNode()).getType().equals("uml:ForkNode")
						|| ((XMIActivityNode) e.getSourceNode()).getType().equals("uml:JoinNode"))) {

					sourceOK = true;

				} else {
					sourceOK = false;
				}
			}
			if (((XMIActivityNode) e.getTargetNode()) != null) {
				if (!(((XMIActivityNode) e.getTargetNode()).getType().equals("uml:DecisionNode")
						|| ((XMIActivityNode) e.getTargetNode()).getType().equals("uml:ForkNode")
						|| ((XMIActivityNode) e.getTargetNode()).getType().equals("uml:JoinNode"))) {

					targetOK = true;
				} else {
					targetOK = false;
				}
			}

			if (sourceOK && targetOK) {
				if (!edgesWithoutCondition.contains(e)) {
					edgesWithoutCondition.add(e);
				}
			}

		}
		for (XMIActivityNode n : nodes) {
			if (n.getType().equals("uml:DecisionNode") || n.getType().equals("uml:ForkNode")
					|| n.getType().equals("uml:JoinNode")) {
				ArrayList<XMIActivityNode> sourcesOfConditionNode = n.getSourceNodes();
				ArrayList<XMIActivityNode> destinationsOfConditionNode = n.getDestinationNodes();
				int i = 1;
				for (XMIActivityNode source : sourcesOfConditionNode) {
					for (XMIActivityNode destination : destinationsOfConditionNode) {
						XMIEdge e = new XMIEdge("", destination.getId(), source.getId(), "", "uml:ControlFlow", null);
						e.setSourceNode(source);
						e.setTargetNode(destination);
						if (n.getType().equals("uml:DecisionNode")) {
							// for (String s:n.getOutgoing()){
							for (XMIEdge edge : edges) {
								if (destination.getId().equals(edge.getTarget())
										&& n.getId().equals(edge.getSource())) {
									// if (s.equals(edge.getId())&&
									// edge.getTarget().equals(destination.getId())){
									if (edge.getName().isEmpty()) {
										edge.setCondition(n.getName() + "__" + i);
										e.setCondition(n.getName() + "__" + i);
										i++;
									} else {
										edge.setCondition(n.getName() + "__" + edge.getName());
										e.setCondition(n.getName() + "__" + edge.getName());
									}
								}

							}
							// }
						}
						edgesWithCondition.add(e);
					}
				}
			}
		}

	}

	/**
	 * 
	 * @return the nodes of the diagram.
	 */
	public ArrayList<XMIActivityNode> getNodes() {
		return nodes;
	}

	/**
	 * 
	 * @return the edges of the diagram
	 */
	public ArrayList<XMIEdge> getEdgesWithoutCondition() {
		return edgesWithoutCondition;
	}

	/**
	 * 
	 * @return the edges of the diagram
	 */
	public ArrayList<XMIEdge> getEdges() {
		return edges;
	}

	/**
	 * 
	 * @return the edges of the diagram
	 */
	public ArrayList<XMIEdge> getEdgesWithCondition() {
		return edgesWithCondition;
	}

	/**
	 * 
	 * @return the diagram's type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Separates a string into different strings every time "_" character occurs
	 * and puts the strings in a list.
	 * 
	 * @param s:
	 *            the initial string
	 * @param list:
	 *            the empty list
	 * @return the list with the string values
	 */
	protected static ArrayList<String> substring(String s, ArrayList<String> list) {
		int startOfSubstring = 0;
		char delimeter = ' ';
		String trimmedS = s.trim();
		for (int i = 0; i < trimmedS.length(); i++) {
			if (trimmedS.charAt(i) == delimeter) {
				if (!trimmedS.substring(startOfSubstring, i).isEmpty()) {
					list.add(trimmedS.substring(startOfSubstring, i));
				} else {
					break;
				}
				startOfSubstring = i+1;
			}
		}
		list.add(trimmedS.substring(startOfSubstring, trimmedS.length()));

		return list;
	}
	/**
	 * Split a string into coordinates.
	 * @param s
	 * @param list
	 * @return
	 */
	protected static ArrayList<Point> substringCoordinates(String s, ArrayList<Point> list) {
		Pattern p = Pattern.compile("\\(([^)]+)\\)");
		Matcher m = p.matcher(s);
		String x="";
		String y="";
		while(m.find()) {
			String c = m.group(1);
			//System.out.println(m.group(1));   
			for (int i = 0; i < c.length(); i++) {
				if (c.charAt(i) == ',') {
					x= c.substring(0, i);
					y = c.substring(i+1);
				}
			}
			Point coor = new Point(Integer.valueOf(x), Integer.valueOf(y));
		    list.add(coor);
		}

		return list;
	}

	public boolean checkParsedXmi() {
		final Display disp = Display.getCurrent();
		boolean xmiIsOk = true;
		boolean decisionNodesOk = true;
		boolean initialNodeOk = true;
		boolean finalNodeOk = true;
		boolean actionNodesOk = true;
		boolean joinNodesOk = true;
		boolean forkNodesOk = true;
		boolean edgesOk = true;
		
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
		for (XMIActivityNode n : nodes) {
			if (n.getType().equals("uml:DecisionNode")) {
				for (int i = 0; i < n.getIncoming().size(); i++) {
					if (n.getIncoming().get(i).isEmpty()) {
						decisionNodesOk = false;
						
						disp.syncExec(new Runnable() {
							@Override
							public void run() {
								MessageDialog.openInformation(disp.getActiveShell(), "Error occured",
										"Decision node with id "+ n.getId() +" should have incoming node!");
							}
						});
						return false;
					}
				}
				for (int i = 0; i < n.getOutgoing().size(); i++) {
					if (n.getOutgoing().get(i).isEmpty()) {
						decisionNodesOk = false;
						
						disp.syncExec(new Runnable() {
							@Override
							public void run() {
								MessageDialog.openInformation(disp.getActiveShell(), "Error occured",
										"Decision node with id "+ n.getId() +" should have outgoing node!");
							}
						});
						return false;
					}

				}
				if (n.getOutgoing().size() < 2) {
					decisionNodesOk = false;
					
					disp.syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openInformation(disp.getActiveShell(), "Error occured",
									"Decision node with id "+ n.getId() +" should have at least two outgoing nodes!");
						}
					});
					return false;
				}
			} else if (n.getType().equals("uml:InitialNode")) {
				if (n.getOutgoing().get(0).isEmpty()) {
					initialNodeOk = false;
					
					disp.syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openInformation(disp.getActiveShell(), "Error occured",
									"Initial node with id "+ n.getId() +" should have outgoing node!");
						}
					});
					return false;
				}
			} else if (n.getType().equals("uml:ActivityFinalNode")) {

				if (n.getIncoming().get(0).isEmpty()) {
					finalNodeOk = false;
					
					disp.syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openInformation(disp.getActiveShell(), "Error occured",
									"Final node with id "+ n.getId() +" should have incoming node!");
						}
					});
					return false;
				}

			} else if (n.getType().equals("uml:OpaqueAction")) {
				if (n.getIncoming().get(0).isEmpty() || n.getOutgoing().get(0).isEmpty()) {
					actionNodesOk = false;
					
					disp.syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openInformation(disp.getActiveShell(), "Error occured",
									"Opaque Action node with id "+ n.getId() +" should have both incoming and outgoing node!");
						}
					});
					return false;
				}
			} else if (n.getType().equals("uml:JoinNode")) {
				if (n.getIncoming().get(0).isEmpty() || n.getOutgoing().get(0).isEmpty()) {
					joinNodesOk = false;
					
					disp.syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openInformation(disp.getActiveShell(), "Error occured",
									"Join node with id "+ n.getId() +" should have both incoming and outgoing node!");
						}
					});
					return false;
				}
				if (n.getIncoming().size() < 2) {
					joinNodesOk = false;
					
					disp.syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openInformation(disp.getActiveShell(), "Error occured",
									"Join node with id "+ n.getId() +" should have at least two incoming nodes!");
						}
					});
					return false;
				}
			} else if (n.getType().equals("uml:ForkNode")) {
				if (n.getIncoming().get(0).isEmpty() || n.getOutgoing().get(0).isEmpty()) {
					forkNodesOk = false;
					
					disp.syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openInformation(disp.getActiveShell(), "Error occured",
									"Fork node with id "+ n.getId() +" should have both incoming and outgoing node!");
						}
					});
					return false;
				}
				if (n.getOutgoing().size() < 2) {
					forkNodesOk = false;
					
					disp.syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openInformation(disp.getActiveShell(), "Error occured",
									"Fork node with id "+ n.getId() +" should have at least two outgoing nodes!");
						}
					});
					return false;
				}
			}

		}
		xmiIsOk = actionNodesOk && finalNodeOk && initialNodeOk && decisionNodesOk && joinNodesOk && forkNodesOk && edgesOk;
		return xmiIsOk;
	}

	public boolean checkParsedXmiForPapyrus() {
		final Display disp = Display.getCurrent();
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

}
