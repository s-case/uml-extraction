package eu.scasefp7.eclipse.umlrec.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.services.IServiceLocator;

import eu.scasefp7.eclipse.umlrec.papyrus.PapyrusGenerator;
import eu.scasefp7.eclipse.umlrec.ui.wizard.Messages;
import eu.scasefp7.eclipse.umlrec.ui.wizard.MyWizard;

public class PapyrusExportJob extends WorkbenchJob {

	private IFile file;
	private String command;
	
	public PapyrusExportJob(IFile file, String command) {
		super(Messages.FileEditorJobDescription);
		this.file=file;
		this.command = command;
	}
	
	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		ILog log = Platform.getLog(Platform.getBundle(MyWizard.PLUGIN_ID));

		try {
		    
		    executeCommand(this.command, parameters);
		} catch ( Exception e ) {
	        log.log(new Status(IStatus.ERROR, MyWizard.PLUGIN_ID, Messages.PapyrusExportJob_Failed, e)); 
	    }
		

	}

	/**
     * Convenience method to call a command with parameters.
     * 
     * @param commandId ID of the command to execute
     * @param parameters map of command parameters in form (parameterId, value)
     * @throws CommandException if the command execution fails
     */
    protected void executeCommand(String commandId, Map<String, String> parameters) throws CommandException {
        // Obtain IServiceLocator implementer, e.g. from PlatformUI.getWorkbench():
        IServiceLocator serviceLocator = PlatformUI.getWorkbench();
        // or a site from within a editor or view:
        // IServiceLocator serviceLocator = getSite();

        ICommandService commandService = (ICommandService)serviceLocator.getService(ICommandService.class);
        IHandlerService handlerService = (IHandlerService)serviceLocator.getService(IHandlerService.class);
        
        try  { 
            // Lookup command with its ID
            Command command = commandService.getCommand(commandId);

            ArrayList<Parameterization> params = new ArrayList<Parameterization>();
            for(Map.Entry<String, String> entry : parameters.entrySet()) {
                IParameter p = command.getParameter(entry.getKey());
                if(p != null) {
                    Parameterization param = new Parameterization(p, entry.getValue());
                    params.add(param);
                } else {
                    System.out.println("Cannot find parameter: " + entry.getKey() + " of command " + commandId);
                }
            }
            ParameterizedCommand parametrizedCommand = new ParameterizedCommand(command, params.toArray(new Parameterization[params.size()]));
            handlerService.executeCommand(parametrizedCommand, null);
            
        } catch (ExecutionException | NotDefinedException |
                NotEnabledException | NotHandledException ex) {
            
           throw ex;
        }
    }

}
