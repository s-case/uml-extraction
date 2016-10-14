package eu.scasefp7.eclipse.umlrec.ui.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;

import eu.scasefp7.eclipse.umlrec.MissingRecognizerDataException;
import eu.scasefp7.eclipse.umlrec.RecognitionException;
import eu.scasefp7.eclipse.umlrec.UMLRecognizer;
import eu.scasefp7.eclipse.umlrec.ui.wizard.Messages;
import eu.scasefp7.eclipse.umlrec.ui.wizard.MyWizard;
import eu.scasefp7.eclipse.umlrec.ui.wizard.WizardUtilities;

public class UMLrecognizerJob extends WorkspaceJob {

	private String srcFilePath;
	private String destFileName;
	private String saveDestPath;
	private boolean isUseCase;
	private boolean showImages;
	private int threshold;
	private double sizeRate;
	private double distNeighborObjects;
	private double coverAreaThreshold;
	private IProject project;
	private Display disp;

	public UMLrecognizerJob(String srcFilePath, String saveDestPath, String destFileName, boolean isUseCase, boolean showImages, int threshold, double sizeRate, double distNeighborObjects, double coverAreaThreshold, IProject project, Display disp) {
		super(Messages.UMLrecognizerJob_UMLrecognizerJobName); 
		this.srcFilePath=srcFilePath;
		this.saveDestPath=saveDestPath;
		this.destFileName=destFileName;
		this.isUseCase = isUseCase;
		this.showImages = showImages;
		this.threshold = threshold;
		this.sizeRate = sizeRate;
		this.distNeighborObjects = distNeighborObjects;
		this.coverAreaThreshold = coverAreaThreshold;
		this.project = project;
		this.disp = disp;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		
		ILog log = Platform.getLog( Platform.getBundle(MyWizard.PLUGIN_ID));
		monitor.beginTask(null, 10);
		
		UMLRecognizer uml;
		try {
			uml = new UMLRecognizer(disp);
		} catch (Exception mrne) {
			mrne.printStackTrace();
			log.log(new Status(IStatus.ERROR, MyWizard.PLUGIN_ID, Messages.UMLrecognizerJob_JNIFail));
			return Status.CANCEL_STATUS;
		} finally {
			monitor.done();
		}
		
		uml.setParameters(this.isUseCase, this.showImages, this.threshold, this.sizeRate, this.distNeighborObjects, this.coverAreaThreshold);

		log.log(new Status(IStatus.INFO, MyWizard.PLUGIN_ID, Messages.UMLrecognizerJob_RecognStart));
		
		monitor.subTask(Messages.UMLrecognizerJob_SetImageDescription);
		
		try {
			uml.setImage(srcFilePath);
		} catch (RecognitionException re) {
			re.printStackTrace();
			log.log(new Status(IStatus.ERROR, MyWizard.PLUGIN_ID, Messages.UMLrecognizerJob_RecognitionFail, re));
			return Status.OK_STATUS;
		} catch(Exception e){
			e.printStackTrace();
			log.log(new Status(IStatus.ERROR, MyWizard.PLUGIN_ID, Messages.UMLrecognizerJob_RecognitionFailGeneral, e));
			return Status.OK_STATUS;
		} finally {
			monitor.done();
		}
		
		monitor.worked(2);
		monitor.subTask(Messages.UMLrecognizerJob_ProcessDescription);

		try {			
			uml.process();			
		} catch (MissingRecognizerDataException | RecognitionException mrde) {
			mrde.printStackTrace();
			log.log(new Status(IStatus.ERROR, MyWizard.PLUGIN_ID, mrde.getMessage()));
			return Status.OK_STATUS;
		} catch(Exception e){
			e.printStackTrace();
			log.log(new Status(IStatus.ERROR, MyWizard.PLUGIN_ID, e.getMessage()));
			return Status.OK_STATUS;
		}
		
		monitor.worked(6);
		monitor.subTask(Messages.UMLrecognizerJob_GetXMIContentDescription);

		String content=uml.getXMIcontent();
		
		if(content.isEmpty()){
			return Status.OK_STATUS;
		}
		monitor.done();
		String umlDestFilename = destFileName.substring(destFileName.lastIndexOf('.')) == ".uml" ? destFileName : destFileName.substring(0, destFileName.lastIndexOf('.')) + ".uml";
		umlDestFilename=checkIfFileExists(umlDestFilename);
		File umlFile = writeInFile(content, log,umlDestFilename);
		if (WizardUtilities.papyrusCommandExists()) {
			createPapyrusFile(umlFile);

			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			try {
				workspace.getRoot().refreshLocal(org.eclipse.core.resources.IFolder.DEPTH_INFINITE,null);
			} catch (CoreException e) {
				log.log(new Status(IStatus.ERROR, MyWizard.PLUGIN_ID, e.getMessage()));
			}
		}
	
		return Status.OK_STATUS;
	}
	
	private String checkIfFileExists(String fileName){
		File file=new File(saveDestPath+File.separator+fileName);
		if(file.exists()){			
			String str=fileName.split("\\.")[0]+randomString(2)+".uml";
			return str;
		}
			else
				return fileName;
	}
	
	private String randomString(int length) {
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
	    return sb.toString();
	}

	private File writeInFile(String content, ILog log, String umlDestFilename) {
		
		File fileToOpen = new File(saveDestPath+File.separator+umlDestFilename);
		try {
			if(!fileToOpen.exists()){
				fileToOpen.createNewFile();	
			}
		} catch (IOException e) {
			log.log(new Status(IStatus.ERROR, MyWizard.PLUGIN_ID, e.getMessage()));
		}

			
		try {
			Files.write(fileToOpen.toPath(),Arrays.asList(content.split("\\r?\\n")),Charset.defaultCharset());
		} catch (IOException e) {
			log.log(new Status(IStatus.ERROR, MyWizard.PLUGIN_ID, e.getMessage()));
		}
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		
		try {
			workspace.getRoot().refreshLocal(org.eclipse.core.resources.IFolder.DEPTH_INFINITE,null);
		} catch (CoreException e) {
			log.log(new Status(IStatus.ERROR, MyWizard.PLUGIN_ID, e.getMessage()));
		}
		return fileToOpen;
	}

	private void createPapyrusFile(File umlFile) {
		IFile file = project.getFile(new Path(saveDestPath.substring(saveDestPath.lastIndexOf(File.separatorChar))+File.separator+destFileName.substring(0, destFileName.lastIndexOf('.'))+".uml"));
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    PapyrusExportJob pjob = new PapyrusExportJob(file, umlFile);
		pjob.setRule(workspace.getRoot());
		pjob.schedule();
	}
}
