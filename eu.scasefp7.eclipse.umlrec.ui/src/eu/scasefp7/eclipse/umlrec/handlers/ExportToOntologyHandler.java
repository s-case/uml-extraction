package eu.scasefp7.eclipse.umlrec.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.scasefp7.eclipse.core.ontology.DynamicOntologyAPI;
import eu.scasefp7.eclipse.core.ontology.StaticOntologyAPI;
import eu.scasefp7.eclipse.umlrec.parser.*;

/**
 * A command handler for exporting a uml diagram to the dynamic or static
 * ontology.
 * 
 * @author mkoutli
 */
public class ExportToOntologyHandler extends ProjectAwareHandler {

	/**
	 * This function is called when the user selects the menu item. It reads the
	 * selected resource(s) and populates the dynamic ontology.
	 * 
	 * @param event
	 *            the event containing the information about which file was
	 *            selected.
	 * @return the result of the execution which must be {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			List<Object> selectionList = structuredSelection.toList();
			// Iterate over the selected files
			for (Object object : selectionList) {
				IFile file = (IFile) Platform.getAdapterManager().getAdapter(object, IFile.class);
				if (file == null) {
					if (object instanceof IAdaptable) {
						file = (IFile) ((IAdaptable) object).getAdapter(IFile.class);
					}
				}
				if (file != null) {
					instantiateOntology(file);
				}
			}
		}
		return null;
	}

	/**
	 * Returns the XML document of a file.
	 * 
	 * @param file the file of which the XML document is returned.
	 * @return the XML document of the file, or null if there is a parsing error.
	 */
	private Document getXMIDocOfFile(IFile file) {
		Document doc = null;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			doc = docBuilder.parse(file.getContents());
		} catch (ParserConfigurationException | SAXException | IOException | CoreException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * Returns the diagram type of an XMI file.
	 * 
	 * @param file the file of which diagram the type is returned.
	 * @return the diagram type of the given file, either "UseCaseDiagram" or "ActivityDiagram" (or "Error" if there is
	 *         a parsing error).
	 */
	protected String getDiagramType(IFile file) {
		Document doc = getXMIDocOfFile(file);
		if (doc != null) {
			Node root = doc.getDocumentElement();
			Node packagedElement = root.getFirstChild().getNextSibling();
			if (packagedElement.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) packagedElement;
				String type = eElement.getAttribute("xmi:type");
				if (type.equalsIgnoreCase("uml:Use Case"))
					return "UseCaseDiagram";
				else if (type.equalsIgnoreCase("uml:Activity"))
					return "ActivityDiagram";
			}
		}
		return "Error";
	}

	/**
	 * Instantiates the ontology given the file of a uml diagram.
	 * 
	 * @param file an {@link IFile} instance of a uml diagram.
	 */
	protected void instantiateOntology(IFile file) {
		Document doc = getXMIDocOfFile(file);
		if (doc != null) {
			String diagramType = getDiagramType(file);
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			if (diagramType.equals("ActivityDiagram")) {
				DynamicOntologyAPI ontology = new DynamicOntologyAPI(file.getProject());
				instantiateDynamicOntology(file, ontology);
				ontology.close();
			} else if (diagramType.equals("UseCaseDiagram")) {
				StaticOntologyAPI ontology = new StaticOntologyAPI(file.getProject());
				instantiateStaticOntology(file, ontology);
				ontology.close();
			}
		}
	}

	/**
	 * Instantiates the static ontology given the file of a UML Use Case diagram.
	 * 
	 * @param file an {@link IFile} instance of a UML Use Case diagram.
	 * @param ontology the static ontology instance to be instantiated.
	 */
	protected void instantiateStaticOntology(IFile file, StaticOntologyAPI ontology) {
		Document doc = getXMIDocOfFile(file);
		String filename = file.getName();
		String diagramName = filename.substring(0, filename.lastIndexOf('.'));
		diagramName = diagramName.substring(diagramName.lastIndexOf('\\') + 1) + "_UCdiagram";
		ontology.addRequirement(diagramName);
		UseCaseParser parser = new UseCaseParser();
		parser.Parsexmi(doc);
		ArrayList<XMIEdge> edges = parser.getEdges();
		ArrayList<XMIUseCaseNode> nodes = parser.getNodes();
		if (parser.checkParsedXmi(false)) {
			WriteStaticOntology.modifyOntology(edges, nodes, ontology, diagramName);
		}
	}

	/**
	 * Instantiates the dynamic ontology given the file of a UML Activity diagram.
	 * 
	 * @param file an {@link IFile} instance of a UML Activity diagram.
	 * @param ontology the dynamic ontology instance to be instantiated.
	 */
	protected void instantiateDynamicOntology(IFile file, DynamicOntologyAPI ontology) {
		Document doc = getXMIDocOfFile(file);
		String filename = file.getName();
		String diagramName = filename.substring(0, filename.lastIndexOf('.'));
		diagramName = diagramName.substring(diagramName.lastIndexOf('\\') + 1) + "_ACdiagram";
		ontology.addActivityDiagram(diagramName);
		ActivityParser parser = new ActivityParser();
		parser.Parsexmi(doc);
		ArrayList<XMIEdge> edgesWithCondition = parser.getEdgesWithCondition();
		ArrayList<XMIEdge> edgesWithoutCondition = parser.getEdgesWithoutCondition();
		ArrayList<XMIEdge> edges = parser.getEdges();
		ArrayList<XMIActivityNode> nodes = parser.getNodes();
		if (parser.checkParsedXmi()) {
			WriteDynamicOntology.modifyOntology(edgesWithCondition, edgesWithoutCondition, edges, nodes, ontology,
					diagramName);
		}
	}

}
