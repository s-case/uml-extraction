package eu.scasefp7.eclipse.umlrec.parser;

import java.util.ArrayList;

import eu.scasefp7.eclipse.core.ontology.StaticOntologyAPI;

/**
 * Add a uml use case diagram to the ontology.
 * 
 * @author mkoutli
 *
 */
public class WriteStaticOntology {

	public static void modifyOntology(ArrayList<XMIEdge> edges, ArrayList<XMIUseCaseNode> nodes,
			StaticOntologyAPI ontology, String diagramName) {

		for (XMIUseCaseNode node : nodes) {
			if (node.getType().equals(XMIUseCaseNode.ACTOR_TYPE)) {
				ontology.addActor(node.getName());
				ontology.connectRequirementToConcept(diagramName, node.getName());

			} else if (node.getType().equals(XMIUseCaseNode.USE_CASE_TYPE)) {
				if (!node.getAnnotations().equals("")) {
					String[] actionAndObject = WriteDynamicOntology.getActionAndObject(node.getName(),
							node.getAnnotations());
					if (actionAndObject != null) {
						String action = actionAndObject[0];
						String object1 = actionAndObject[1];
						ontology.addAction(action);
						ontology.connectRequirementToOperation(diagramName, action);
						ontology.addObject(object1);
						ontology.connectRequirementToConcept(diagramName, object1);
						ontology.connectActionToObject(action, object1);
					}
				}
			}

		}

		for (XMIUseCaseNode node : nodes) {
			if (node.getType().equals(XMIUseCaseNode.USE_CASE_TYPE)) {
				if (!node.getAnnotations().equals("")) {

					String[] actionAndObject = WriteDynamicOntology.getActionAndObject(node.getName(),
							node.getAnnotations());
					if (actionAndObject != null) {
						String action = actionAndObject[0];
						String object1 = actionAndObject[1];

						// has actor or connected use case
						boolean nodeIsConnectedToActor = false;

						for (String id : node.getIncomingSolidNodes()) {
							for (XMIUseCaseNode connectedNode : nodes) {
								if (connectedNode.getId().equals(id) && connectedNode.getType().equals(XMIUseCaseNode.ACTOR_TYPE)) {
									ontology.connectActorToAction(connectedNode.getName(), action);
									nodeIsConnectedToActor = true;
								} else if (connectedNode.getId().equals(id)
										&& connectedNode.getType().equals(XMIUseCaseNode.USE_CASE_TYPE)) {
									if (!connectedNode.getAnnotations().equals("")) {
										String[] actionAndObject2 = WriteDynamicOntology.getActionAndObject(
												connectedNode.getName(), connectedNode.getAnnotations());
										if (actionAndObject2 != null) {
											String object2 = actionAndObject2[1];
											ontology.connectObjectToObject(object1, object2);
										}
									}
								}
							}
						}

						for (String id : node.getConnectedSolidNodes()) {
							for (XMIUseCaseNode connectedNode : nodes) {
								if (connectedNode.getId().equals(id) && connectedNode.getType().equals(XMIUseCaseNode.ACTOR_TYPE)) {
									ontology.connectActorToAction(connectedNode.getName(), action);
									nodeIsConnectedToActor = true;
								}
							}
						}
						for (String id : node.getConnectedSolidNodes()) {
							for (XMIUseCaseNode connectedNode : nodes) {
								if (connectedNode.getId().equals(id) && connectedNode.getType().equals(XMIUseCaseNode.USE_CASE_TYPE)) {
									if (nodeIsConnectedToActor) {
										if (!connectedNode.getAnnotations().equals("")) {
											String[] actionAndObject2 = WriteDynamicOntology.getActionAndObject(
													connectedNode.getName(), connectedNode.getAnnotations());
											if (actionAndObject2 != null) {
												String object2 = actionAndObject2[1];
												ontology.connectObjectToObject(object1, object2);
											}
										}
									}
								}
							}
						}

						// extends or includes
						for (String id : node.getIncomingDashedNodes()) {
							for (XMIUseCaseNode connectedNode : nodes) {
								if (connectedNode.getId().equals(id) && connectedNode.getType().equals(XMIUseCaseNode.USE_CASE_TYPE)) {
									for (XMIEdge edge : edges) {
										if (((XMIUseCaseNode) edge.getSourceNode()).getId().equals(id)
												&& ((XMIUseCaseNode) edge.getTargetNode()).getId().equals(node.getId())) {
											if (!connectedNode.getAnnotations().equals("")) {
												String[] actionAndObject2 = WriteDynamicOntology.getActionAndObject(
														connectedNode.getName(), connectedNode.getAnnotations());
												if (actionAndObject2 != null) {
													String object2 = actionAndObject2[1];
													if (edge.getName().equalsIgnoreCase("extend")) {
														ontology.connectObjectToObject(object1, object2);
													} else if (edge.getName().equalsIgnoreCase("include")) {
														ontology.connectObjectToObject(object2, object1);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}

			}
		}

		System.out.println("\nFinish!");

	}

}
