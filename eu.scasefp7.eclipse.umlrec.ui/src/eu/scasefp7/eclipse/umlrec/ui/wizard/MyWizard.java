package eu.scasefp7.eclipse.umlrec.ui.wizard;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import eu.scasefp7.eclipse.umlrec.ui.FileEditorJob;
import eu.scasefp7.eclipse.umlrec.ui.UMLrecognizerJob;

public class MyWizard extends Wizard implements IImportWizard{

	private PageOne pageOne;
	private PageTwo pageTwo;
	private IStructuredSelection selection;
	
	public static String PLUGIN_ID = "eu.scasefp7.eclipse.umlrec.ui"; //$NON-NLS-1$
	
	public MyWizard() {
		super();
	}

	@Override
	public void addPages() {
		pageOne=new PageOne();
		pageTwo=new PageTwo(selection);
		addPage(pageOne);	
		addPage(pageTwo);
	}

	@Override
	public boolean performFinish() {
		
		System.out.println(pageTwo.getTresh());
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		UMLrecognizerJob job = new UMLrecognizerJob(pageOne.getFilePath(),
				workspace.getRoot().getLocation().toOSString() + pageTwo.getContainerFullPath().toOSString(),
				pageTwo.getFileName(), pageOne.getIsUseCase(), 
				pageTwo.isShowImages(), pageTwo.getTresh(), pageTwo.getSizeRate(),
				pageTwo.getDistNeigborObjects(), pageTwo.getCoverAreaThr());
		job.setRule(workspace.getRoot()); // TODO: Course locking of the workspace, use finer locking (file location?)
		
		job.schedule();
		
		FileEditorJob job2;
		
		if(pageTwo.getIsChecked()){
			job2=new FileEditorJob(workspace.getRoot().getLocation().toOSString() +
			pageTwo.getContainerFullPath().toOSString(), pageTwo.getFileName());	
			job2.setRule(workspace.getRoot());
			job2.schedule();
		}
		
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(Messages.MyWizard);
		this.selection=selection;
	}

}
