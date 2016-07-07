package eu.scasefp7.eclipse.umlrec.papyrus.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Responsible for basic functionalities of an Eclipse Project
 *
 * @author András Dobreff
 */

public class ProjectUtils {
	
	/**
	 * Creates project or returns the existing.
	 * @param name - The Name of the Project
	 * @return Existing or created project
	 */
	public static IProject createProject(String name){
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = root.getProject(name);
        if (!project.exists()){
	        try {
				project.create(null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
        }
        return project;
	}
	
	/** 
	 * Opens a project 
	 * @param project - The Project to be opened
	 */
	public static void openProject(IProject project){
		try {
			project.open(new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes a project
	 * @param project - The Project to be deleted
	 */
	public static void deleteProject(IProject project){
		 if (project.exists()){
			try{
				project.delete(true, true, new NullProgressMonitor());
			}catch(CoreException e){
				e.printStackTrace();
			}
		 }
	}

	/**
	 * Deletes a Project by Name
	 * @param projectname - The Name of the Project to be deleted
	 */
	public static void deleteProject(String projectname){
		IProject project = getProject(projectname);
		deleteProject(project);
	}
	
	/**
	 * @param projectName - Name of the project
	 * @return a project resource handle 
	 * @see IWorkspaceRoot#getProject(String)
	 */
	public static IProject getProject(String projectName){
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		return root.getProject(projectName);
	}
}
