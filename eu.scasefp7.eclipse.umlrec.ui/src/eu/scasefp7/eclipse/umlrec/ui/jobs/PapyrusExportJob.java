package eu.scasefp7.eclipse.umlrec.ui.jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterType;
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

import eu.scasefp7.eclipse.umlrec.ui.wizard.Messages;
import eu.scasefp7.eclipse.umlrec.ui.wizard.MyWizard;

public class PapyrusExportJob extends WorkbenchJob {

    public static String PAPYRUS_COMMAND = "eu.scasefp7.eclipse.umlrec.ui.commands.convertToPapyrus"; //$NON-NLS-1$   

	private IFile file;
	
	public PapyrusExportJob(IFile file) {
		super(Messages.PapyrusExportJobDescription);
		this.file=file;
	}
	
	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		ILog log = Platform.getLog(Platform.getBundle(MyWizard.PLUGIN_ID));

		try {
		    Map<String, Object> params = new HashMap<String,Object>();
		    params.put("fileName", this.file);
		    executeCommand(PAPYRUS_COMMAND, params);
		} catch ( Exception e ) {
		    IStatus result = new Status(IStatus.ERROR, MyWizard.PLUGIN_ID, Messages.PapyrusExportJob_Failed, e); 
	        log.log(result);
	        return result;
	    }
        return Status.OK_STATUS;
		

	}

	/**
     * Convenience method to call a command with parameters.
     * 
     * @param commandId ID of the command to execute
     * @param parameters map of command parameters in form (parameterId, value)
     * @throws CommandException if the command execution fails
     */
    protected void executeCommand(String commandId, Map<String, Object> parameters) throws CommandException {
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
            for(Map.Entry<String, Object> entry : parameters.entrySet()) {
                IParameter p = command.getParameter(entry.getKey());
                ParameterType type = command.getParameterType(entry.getKey());
                if(p != null) {
                    String value = entry.getValue().toString();
                
                    if(type != null) {
                        AbstractParameterValueConverter converter = type.getValueConverter();
                        if(converter != null) {
                            value = converter.convertToString(entry.getValue());
                        }
                    }

                    Parameterization param = new Parameterization(p, value);
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
