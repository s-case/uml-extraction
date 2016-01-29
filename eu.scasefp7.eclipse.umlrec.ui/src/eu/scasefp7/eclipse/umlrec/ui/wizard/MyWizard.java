package eu.scasefp7.eclipse.umlrec.ui.wizard;

import org.eclipse.core.commands.Command;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.services.IServiceLocator;

import eu.scasefp7.eclipse.umlrec.papyrus.PapyrusGenerator;
import eu.scasefp7.eclipse.umlrec.ui.FileEditorJob;
import eu.scasefp7.eclipse.umlrec.ui.PapyrusExportJob;
import eu.scasefp7.eclipse.umlrec.ui.UMLrecognizerJob;

public class MyWizard extends Wizard implements IImportWizard{

	private PageOne pageOne;
	private PageTwo pageTwo;
	private IStructuredSelection selection;
    private boolean papyrusPresent = false;
	
	public static String PLUGIN_ID = "eu.scasefp7.eclipse.umlrec.ui"; //$NON-NLS-1$
	public static String PAPYRUS_COMMAND = "eu.scasefp7.eclipse.umlrec.commands.convertToPapyrus"; //$NON-NLS-1$
	
	public MyWizard() {
		super();
		
		this.papyrusPresent  = checkForPapyrusCommand();
	}

	private boolean checkForPapyrusCommand() {
        // Obtain IServiceLocator implementer, e.g. from PlatformUI.getWorkbench():
        IServiceLocator serviceLocator = PlatformUI.getWorkbench();
        // or a site from within a editor or view:
        // IServiceLocator serviceLocator = getSite();

        ICommandService commandService = (ICommandService)serviceLocator.getService(ICommandService.class);
        
        // Execute command via its ID
        Command cmd = commandService.getCommand(this.PAPYRUS_COMMAND);
        return cmd.isDefined();
	}
	
	private boolean checkForPapyrus() {
	    try {
	        Class.forName(PapyrusGenerator.class.getName());
	        return true;
	    } catch(ClassNotFoundException cnfe) {
	        return false;
	    }
	}

    @Override
	public void addPages() {
		pageOne=new PageOne();
		pageTwo=new PageTwo(selection, this.papyrusPresent);
		addPage(pageOne);	
		addPage(pageTwo);
	}

	@Override
	public boolean performFinish() {
		
		System.out.println(pageTwo.getTresh());
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile file = pageTwo.createNewFile();
		        
		UMLrecognizerJob job = new UMLrecognizerJob(pageOne.getFilePath(),
				workspace.getRoot().getLocation().toOSString() + pageTwo.getContainerFullPath().toOSString(),
				pageTwo.getFileName(), pageOne.getIsUseCase(), 
				pageTwo.isShowImages(), pageTwo.getTresh(), pageTwo.getSizeRate(),
				pageTwo.getDistNeigborObjects(), pageTwo.getCoverAreaThr());
		job.setRule(file);
		job.schedule();
		
		if(pageTwo.getExportPapyrusModel()) {
    		PapyrusExportJob job2 = new PapyrusExportJob(file);
    		job2.setRule(file);
    		job2.schedule();
		}
		
		FileEditorJob job3;
		
		if(pageTwo.getIsChecked()){
			job3=new FileEditorJob(workspace.getRoot().getLocation().toOSString() +
			pageTwo.getContainerFullPath().toOSString(), pageTwo.getFileName());	
			job3.setRule(file);
			job3.schedule();
		}
		
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(Messages.MyWizard);
		this.selection=selection;
	}

}
