package eu.scasefp7.eclipse.umlrec.ui.wizard;

import org.eclipse.core.commands.Command;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.services.IServiceLocator;

import eu.scasefp7.eclipse.umlrec.ui.jobs.PapyrusExportJob;

public class WizardUtilities {

	public static boolean papyrusCommandExists() {
        // Obtain IServiceLocator implementer, e.g. from PlatformUI.getWorkbench():
        IServiceLocator serviceLocator = PlatformUI.getWorkbench();
        // or a site from within a editor or view:
        // IServiceLocator serviceLocator = getSite();

        ICommandService commandService = (ICommandService)serviceLocator.getService(ICommandService.class);
        
        // Execute command via its ID
        Command cmd = commandService.getCommand(PapyrusExportJob.PAPYRUS_COMMAND);
        return cmd.isHandled();
	}

}
