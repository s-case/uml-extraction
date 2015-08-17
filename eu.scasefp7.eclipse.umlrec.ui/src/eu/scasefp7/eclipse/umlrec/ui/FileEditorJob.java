package eu.scasefp7.eclipse.umlrec.ui;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.WorkbenchJob;

import eu.scasefp7.eclipse.umlrec.ui.wizard.Messages;
import eu.scasefp7.eclipse.umlrec.ui.wizard.MyWizard;

public class FileEditorJob extends WorkbenchJob {

	private String saveDestPath;
	private String destFileName;
	
	public FileEditorJob(String saveDestPath, String destFileName) {
		super(Messages.FileEditorJobDescription);
		this.saveDestPath=saveDestPath;
		this.destFileName=destFileName;
	}


	@Override
	public IStatus runInUIThread(IProgressMonitor arg0) {
		File fileToOpen = new File(saveDestPath+File.separator+destFileName);
		ILog log = Platform.getLog(Platform.getBundle(MyWizard.PLUGIN_ID));

		IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
	    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	    
	    if(!fileToOpen.exists()) return Status.OK_STATUS;
	 
	    try {
	        IDE.openEditorOnFileStore( page, fileStore );
	    } catch ( PartInitException e ) {
	        log.log(new Status(IStatus.ERROR, MyWizard.PLUGIN_ID, Messages.FileEditorJob_EditorOpenFailed, e)); 
	    }
		
		return Status.OK_STATUS;
	}

}
