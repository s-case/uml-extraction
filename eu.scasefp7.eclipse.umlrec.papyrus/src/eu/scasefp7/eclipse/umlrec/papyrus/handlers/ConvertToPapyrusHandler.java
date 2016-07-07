package eu.scasefp7.eclipse.umlrec.papyrus.handlers;

//import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
//import java.util.ArrayList;
//import java.util.List;

//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;





import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
//import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
//import org.eclipse.core.runtime.Platform;
//import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
//import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
//import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.IProgressService;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.xml.sax.SAXException;





import eu.scasefp7.eclipse.umlrec.papyrus.model.PapyrusGenerator;
import eu.scasefp7.eclipse.umlrec.papyrus.modelmanagers.DefaultPapyrusModelManager;
import eu.scasefp7.eclipse.umlrec.papyrus.utils.DialogUtils;

/**
 * A command handler for converting generated xmi files to Papyrus compatible files
 * @author mkoutli, tsirelis
 *
 */

public class ConvertToPapyrusHandler extends AbstractHandler{
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
	    String paramFileName = event.getParameter("fileName");
	    IFile file;
        
	    // If we have the parameter set, don't use current selection
	    if(paramFileName != null) {
	        Path path = new Path(paramFileName);
            file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
	    } else {
    		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
    		ISelectionService service = window.getSelectionService();
    		IStructuredSelection structured = (IStructuredSelection) service.getSelection();
    		file = (IFile) structured.getFirstElement();		
	    }
	    
		IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		
		PapyrusGenerator pg = new PapyrusGenerator(file, DefaultPapyrusModelManager.class);
		try {
			progressService.runInUI(progressService, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) {
					try {
						pg.run(monitor);
					} catch (Exception e) {
						DialogUtils.errorMsgb("Papyrus Model generation Error", "Error occured during the generation process.", e);
						monitor.done();
					}
				}
			},ResourcesPlugin.getWorkspace().getRoot());
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
