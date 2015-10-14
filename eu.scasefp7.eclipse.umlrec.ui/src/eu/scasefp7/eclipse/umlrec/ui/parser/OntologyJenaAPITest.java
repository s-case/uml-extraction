package eu.scasefp7.eclipse.umlrec.ui.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import eu.scasefp7.eclipse.core.ontology.DynamicOntologyAPI;
import eu.scasefp7.eclipse.umlrec.ui.parser.XMIActivityNode;
import eu.scasefp7.eclipse.umlrec.ui.parser.XMIEdge;


/**
 * An example instantiation of the ontology.
 * 
 * @author themis
 */
public class OntologyJenaAPITest {

	/**
	 * Instantiates the ontology. Note that the ontology OWL file is expected to
	 * be in the directory ./ontology/
	 * 
	 * @param args
	 *            unused parameter.
	 * @throws IOException
	 *             if the ontology file cannot be found.
	 */
	public static void modifyOntology(ArrayList<XMIEdge> edgesWithCondition, ArrayList<XMIEdge> edgesWithoutCondition,
			ArrayList<XMIEdge> edges, ArrayList<XMIActivityNode> nodes, DynamicOntologyAPI ontology, String diagramName) throws IOException {

		// Create a new file for the dynamic ontology and instantiate it
		//String SOURCE = "http://www.owl-ontologies.com/Ontology1273059028.owl";
//		Files.copy(new File(".\\ontology\\DynamicOntology.owl").toPath(),
//				new File(".\\ontology\\Restmarks.owl").toPath(), StandardCopyOption.REPLACE_EXISTING);
		//OntologyJenaAPI ontology = new OntologyJenaAPI(".\\ontology\\Restmarks.owl", SOURCE);

		// Add a new project individual
		//ontology.addIndividual("Project", "Restmarks");

		// Add a new diagram
//		ontology.addIndividual("ActivityDiagram", "Add_Bookmark");
//		ontology.addPropertyAndReverseBetweenIndividuals("Restmarks", "project_has_diagram", "Add_Bookmark");

		for (XMIActivityNode node : nodes) {
			if (node.getType().equals("uml:InitialNode")) {
				ontology.addInitialActivity(node.getName());
				ontology.connectActivityDiagramToElement(diagramName, node.getName());

			} else if (node.getType().equals("uml:ActivityFinalNode")) {
				ontology.addFinalActivity(node.getName());
				ontology.connectActivityDiagramToElement(diagramName, node.getName());

			} else if (node.getType().equals("uml:OpaqueAction")) {
				ontology.addActivity(node.getName());
				ontology.connectActivityDiagramToElement(diagramName, node.getName());
			} else if (node.getType().equals("uml:DecisionNode")) {


			}

		}

		for (XMIEdge edge : edgesWithCondition) {

			ontology.addTransition(edge.getSourceNode().getName(), edge.getTargetNode().getName());
			ontology.connectActivityDiagramToTransition(diagramName, edge.getSourceNode().getName(),edge.getTargetNode().getName());
			if (edge.getCondition() != null) {
				ontology.addConditionToTransition(edge.getCondition(), edge.getSourceNode().getName() , edge.getTargetNode().getName());
				ontology.connectActivityDiagramToElement(diagramName, edge.getCondition());
			}
		}

		for (XMIEdge edge : edgesWithoutCondition) {

			ontology.addTransition(edge.getSourceNode().getName(), edge.getTargetNode().getName());
			ontology.connectActivityDiagramToTransition(diagramName,edge.getSourceNode().getName(), edge.getTargetNode().getName());
		}

		

		// Close and save the ontology
		ontology.close();
		System.out.println("\nFinish!");
	}

}
