package eu.scasefp7.eclipse.umlrec.handlers;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.scasefp7.eclipse.core.ontology.DynamicOntologyAPI;
import eu.scasefp7.eclipse.core.ontology.StaticOntologyAPI;
import eu.scasefp7.eclipse.umlrec.ui.parser.*;


/**
 * A command handler for exporting a uml diagram to the dynamic or static ontology.
 * 
 * @author mkoutli
 */
public class ExportToOntologyHandler extends AbstractHandler {

	/**
	 * This function is called when the user selects the menu item. It reads the selected resource(s) and populates the
	 * dynamic ontology.
	 * 
	 * @param event the event containing the information about which file was selected.
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
	 * Instantiates the dynamic ontology given the file of a uml diagram.
	 * 
	 * @param file an {@link IFile} instance of a uml diagram.
	 */
	private void instantiateOntology(IFile file) {
		try {
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();

			Document doc;
			doc = docBuilder.parse(file.getContents());
			// Get the root element

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			Node root = doc.getDocumentElement();
			Node packagedElement = root.getFirstChild().getNextSibling();
			if (packagedElement.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) packagedElement;
				String type = eElement.getAttribute("xmi:type");
				boolean xmiIsOk = false;
				final Display disp = Display.getCurrent();
				if (type.equalsIgnoreCase("uml:Activity")) {
					DynamicOntologyAPI ontology = new DynamicOntologyAPI(file.getProject());
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
					xmiIsOk = parser.checkParsedXmi();
					if (xmiIsOk) {
						WriteDynamicOntology.modifyOntology(edgesWithCondition, edgesWithoutCondition, edges, nodes, ontology, diagramName);
						disp.syncExec(new Runnable() {
							@Override
							public void run() {
								MessageDialog.openInformation(disp.getActiveShell(), "Info",
										"Export finished!");
							}
						});
					}
				} else if (type.equalsIgnoreCase("uml:Use Case")) {
					StaticOntologyAPI ontology = new StaticOntologyAPI(file.getProject());
					String filename = file.getName();
					String diagramName = filename.substring(0, filename.lastIndexOf('.'));
					diagramName = diagramName.substring(diagramName.lastIndexOf('\\') + 1) + "_UCdiagram";
					ontology.addRequirement(diagramName);
					UseCaseParser parser = new UseCaseParser();
					parser.Parsexmi(doc);
					ArrayList<XMIEdge> edges = parser.getEdges();
					ArrayList<XMIUseCaseNode> nodes = parser.getNodes();
					xmiIsOk = parser.checkParsedXmi();
					if (xmiIsOk) {
					WriteStaticOntology.modifyOntology(edges, nodes, ontology, diagramName);
					disp.syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openInformation(disp.getActiveShell(), "Info",
									"Export finished!");
						}
					});
					}

				}
			}

		} catch (ParserConfigurationException | SAXException | IOException | CoreException e) {
			e.printStackTrace();
		}
	}



	


}

