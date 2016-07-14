package eu.scasefp7.eclipse.umlrec.ui.wizard;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import eu.scasefp7.eclipse.umlrec.Activator;
import eu.scasefp7.eclipse.umlrec.ui.jobs.FileEditorJob;
import eu.scasefp7.eclipse.umlrec.ui.jobs.UMLrecognizerJob;

public class MyWizard extends Wizard implements IImportWizard{

	private PageOne pageOne;
	private PageTwo pageTwo;
	private IStructuredSelection selection;
	private List<IProject> sCASEProjectList;
	
	public static String PLUGIN_ID = Activator.PLUGIN_ID;
	
    @Override
	public void addPages() {
		pageOne=new PageOne();
		pageTwo=new PageTwo(selection, sCASEProjectList);
		addPage(pageOne);	
		addPage(pageTwo);
	}

	@Override
	public boolean performFinish() {
		
		System.out.println(pageTwo.getTresh());
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		UMLrecognizerJob job = new UMLrecognizerJob(pageOne.getFilePath(),
		        workspace.getRoot().getLocation().toOSString() + pageTwo.getContainerFullPath().toOSString() + File.separator + getRequirementsFolderName(pageTwo.getContainerFullPath()),
				pageTwo.getFileName(), pageOne.getIsUseCase(), 
				pageTwo.isShowImages(), pageTwo.getTresh(), pageTwo.getSizeRate(),
				pageTwo.getDistNeigborObjects(), pageTwo.getCoverAreaThr(), (IProject)selection.getFirstElement());
		job.setRule(workspace.getRoot()); // TODO: Course locking of the workspace, use finer locking (file location?)
		job.schedule();
		
		if(pageTwo.getIsChecked()){
		    FileEditorJob job2;
		    job2=new FileEditorJob(workspace.getRoot().getLocation().toOSString() + pageTwo.getContainerFullPath().toOSString(), pageTwo.getFileName());	
			job2.setRule(workspace.getRoot());
			job2.schedule();
		}
		
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(Messages.MyWizard);
		this.selection=selection;
		sCASEProjectList = getSCASEProjects();
	}

	// TODO - move this to ProjectUtils in core
	private List<IProject> getSCASEProjects() {
	      List<IProject> projectList = new LinkedList<IProject>();
	      try {
	         IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
	         IProject[] projects = workspaceRoot.getProjects();
	         for(int i = 0; i < projects.length; i++) {
	            IProject project = projects[i];
	            if(project.hasNature("eu.scasefp7.eclipse.core.scaseNature")) {
	               projectList.add(project);
	            }
	         }
	      }
	      catch(CoreException ce) {
	         ce.printStackTrace();
	      }
	      
	      return projectList;
	}
	
	// AZ - returns the predefined "requirements" folder for S-CASE projects,
	//      or creates it, if it's not present
	private String getRequirementsFolderName(IPath projectPath) {
		String folderName = "";
		IProject project = null;
		
		for (IProject ip : sCASEProjectList) {
			if (ip.getFullPath().equals(projectPath)) {
				project = ip;
				break;
			}
		}
		if (project != null) {
	        try {
	        	folderName = project.getPersistentProperty(new QualifiedName("", "eu.scasefp7.eclipse.core.ui.rqsFolder"));
	        	if (folderName == null || folderName.isEmpty()) {
	        		IFolder requirements = project.getFolder("requirements");
	        		IProgressMonitor monitor = new NullProgressMonitor();
					try {
						requirements.create(true, true, monitor);
					} catch (CoreException e) {
					    Activator.log("Unable to create project folders.", e);
					}
					try {
						project.setPersistentProperty(new QualifiedName("", "eu.scasefp7.eclipse.core.ui.rqsFolder"), requirements.getProjectRelativePath().toPortableString());
					} catch (CoreException e) {
						Activator.log("Unable to set project properties.", e);
					}
	        		folderName = "requirements";
	        	}
	        } catch (Exception e) {
				Activator.log("Unable to get project property: eu.scasefp7.eclipse.core.ui.rqsFolder. Project does not exist, is not local or is not open.", e);
	        }
		}
        
        return folderName;
	}
}
