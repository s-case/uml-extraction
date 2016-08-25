package eu.scasefp7.eclipse.umlrec.ui.wizard;

import eu.scasefp7.eclipse.umlrec.UMLRecognizer;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class PageTwo extends WizardNewFileCreationPage {

	private boolean isChecked = false;
	private boolean isShowImages = UMLRecognizer.SHOW_IMAGES;
	private int thresh = UMLRecognizer.THRESH;
	private double sizeRate = UMLRecognizer.SIZE_RATE;
	private double distNeighborObjects = UMLRecognizer.DIST_NEIGHBOR_OBJECTS;
	private double coverAreaThr = UMLRecognizer.COVER_AREA_THR;

	private Composite advancedParametersComposite;
	private Button advancedButton;
	private Composite advancedParametersParent;

	private Text threshText;
	private Text sizeRateText;
	private Text distNeighborObjectsText;
	private Text coverAreaThrText;
	private Button checkbox;
	private MyWizard wizard;
	
	private int lineWidth=35;
	private List<IProject> scaseProjectList;
	
	public PageTwo(IStructuredSelection selection, List<IProject> scaseProjectList, MyWizard wizard) {
		super("Wizard Page Two", selection);  //$NON-NLS-1$
		setTitle(Messages.PageTwo_Title);
		setDescription(Messages.PageTwo_Description);
		this.scaseProjectList = scaseProjectList;
		this.wizard = wizard;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		Composite container = (Composite) getControl();
		Button checkbox = new Button(container, SWT.CHECK);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);

		gd.horizontalSpan = 2;
		checkbox.setText(Messages.PageTwo_Checkbox);
		checkbox.setLayoutData(gd);
		checkbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                isChecked = !isChecked;
            }
        });

		if(WizardUtilities.papyrusCommandExists()) {		    
			checkbox.setVisible(false);
			setFileExtension("di");  // AZ - if papyrus plugin is present, impose .di as the file extension
		} else {
			setFileExtension("uml");  //  otherwise, impose .uml
		}
	}

	@Override
	protected void createAdvancedControls(Composite parent) {

		advancedParametersParent = new Composite(parent, SWT.NONE);
		advancedParametersParent.setFont(parent.getFont());
		advancedParametersParent.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginBottom = 2;
		layout.numColumns = 3;

		advancedParametersParent.setLayout(layout);

		advancedButton = new Button(advancedParametersParent, SWT.PUSH);
		advancedButton.setFont(advancedParametersParent.getFont());
		advancedButton.setText(Messages.PageTwo_AdvancedButtonShowText);
		GridData data = setButtonLayoutData(advancedButton);
		data.horizontalAlignment = GridData.BEGINNING;
		data.horizontalSpan = 3;
		advancedButton.setLayoutData(data);
		advancedButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleAdvancedButtonSelect();
			}
		});

	};

	@Override
	protected void handleAdvancedButtonSelect() {

		Composite composite = (Composite) getControl();

		if (advancedParametersComposite != null) {
			advancedParametersComposite.dispose();
			advancedParametersComposite = null;
			composite.layout();

			advancedButton.setText(Messages.PageTwo_AdvancedButtonShowText);
		} else {
			advancedParametersComposite = new Composite(
					advancedParametersParent, SWT.NONE);

			GridLayout layout = new GridLayout();
			advancedParametersComposite.setLayout(layout);
			advancedParametersComposite.setLayoutData(new GridData(
					GridData.VERTICAL_ALIGN_FILL | GridData.FILL_HORIZONTAL));
			layout.numColumns = 2;

			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;

			checkbox = new Button(advancedParametersComposite, SWT.CHECK);
			checkbox.setText(Messages.PageTwo_ShowImagesCheckboxLabel);
			checkbox.setLayoutData(data);
			
			if(isShowImages){
				checkbox.setSelection(true);
			}
			
			checkbox.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					isShowImages = !isShowImages;
				}

			});
			


			Label threshLabel = new Label(advancedParametersComposite, SWT.NULL);
			threshLabel.setText(Messages.PageTwo_ThreshLabel);
			threshText = new Text(advancedParametersComposite, SWT.BORDER
					| SWT.SINGLE);
			
			GridData grid=new GridData(lineWidth, threshText.getLineHeight());

			threshText.setText(String.valueOf(thresh));
			threshText.setLayoutData(grid);
			
			threshText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent arg0) {
					try {
						thresh = !threshText.getText().isEmpty() ? Integer.valueOf(threshText.getText()): thresh;
						setPageComplete(validateAdvanced() && validatePage());
					} catch (Exception e) {
						setErrorMessage(Messages.PageTwo_ThreshError); 
						setPageComplete(false);
					}
					
				}
			});

			Label sizeRateLabel = new Label(advancedParametersComposite,
					SWT.NULL);
			sizeRateLabel.setText(Messages.PageTwo_SizeRateLabel);
			sizeRateText = new Text(advancedParametersComposite,
					SWT.BORDER | SWT.SINGLE);
			
			sizeRateText.setText(String.valueOf(sizeRate));
			sizeRateText.setLayoutData(grid);
			
			sizeRateText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent arg0) {
					try {
						sizeRate = !sizeRateText.getText().isEmpty() ? Double.valueOf(sizeRateText.getText()) : sizeRate;
						setPageComplete(validateAdvanced() && validatePage());
					} catch (Exception e) {
						setErrorMessage(Messages.PageTwo_SizeRateError); 
						setPageComplete(false);
					}
				}
			});
					

			Label distNeigborObjectsLabel = new Label(
					advancedParametersComposite, SWT.NULL);
			distNeigborObjectsLabel.setText(Messages.PageTwo_DistNeighborObjectsLabel);
			distNeighborObjectsText = new Text(advancedParametersComposite,
					SWT.BORDER | SWT.SINGLE);
			
			distNeighborObjectsText.setText(String.valueOf(distNeighborObjects));
			distNeighborObjectsText.setLayoutData(grid);
			
			distNeighborObjectsText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent arg0) {
					try {
						distNeighborObjects = !distNeighborObjectsText.getText().isEmpty() ? Double.valueOf(distNeighborObjectsText.getText()): distNeighborObjects;
						setPageComplete(validateAdvanced() && validatePage());
					} catch (Exception e) {
						setErrorMessage(Messages.PageTwo_DistNeighborError); 
						setPageComplete(false);
					}
				}
			});
			
			

			Label coverAreaThrLabel = new Label(advancedParametersComposite,
					SWT.NULL);
			coverAreaThrLabel.setText(Messages.PageTwo_CoverAreaThrLabel);
			coverAreaThrText = new Text(advancedParametersComposite,
					SWT.BORDER | SWT.SINGLE);
			
			coverAreaThrText.setText(String.valueOf(coverAreaThr));
			coverAreaThrText.setLayoutData(grid);
			
			coverAreaThrText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent arg0) {
					try {
						coverAreaThr = !coverAreaThrText.getText().isEmpty() ? Double.valueOf(coverAreaThrText.getText()) : coverAreaThr;
						setPageComplete(validateAdvanced() && validatePage());
					} catch (Exception e) {
						setErrorMessage(Messages.PageTwo_CoverAreaThrError); 
						setPageComplete(false);
					}
				}
			});

			composite.layout();
			advancedButton.setText(Messages.PageTwo_AdvancedButtonHideText);
		}
	}

	@Override
	protected void createLinkTarget() {
		return;
	}

	@Override
	protected IStatus validateLinkedResource() {
		return Status.OK_STATUS;
	}
	
	@Override
	public void handleEvent(Event event) {
		if(validateAdvanced() ){
			if(validatePage()){
				setPageComplete(true);
			}else {
				setPageComplete(false);
			}
			return;
		} 
		
		if(validatePage()){
			if(!validateAdvanced()){
				setPageComplete(false);
			}
			return;
		}
		
		setPageComplete(validatePage());

	}
	
	
	private boolean validateAdvanced(){
		// AZ - added isDisposed() checks to solve Bug #173
		if(coverAreaThrText!=null && !coverAreaThrText.isDisposed() && !coverAreaThrText.getText().isEmpty()){
			try {
				coverAreaThr = Double.valueOf(coverAreaThrText.getText());
				if(coverAreaThr<0 || coverAreaThr>100) throw new IllegalArgumentException();
			} catch (Exception e) {
				setErrorMessage(Messages.PageTwo_CoverAreaThrError);
				return false;
			}
		}
		if(distNeighborObjectsText!=null && !distNeighborObjectsText.isDisposed() && !distNeighborObjectsText.getText().isEmpty()){
			try {
				distNeighborObjects = Double.valueOf(distNeighborObjectsText.getText());
				if(distNeighborObjects<0) throw new IllegalArgumentException();
			} catch (Exception e) {
				setErrorMessage(Messages.PageTwo_DistNeighborError);
				return false;
			}
		}
		if(sizeRateText!=null && !sizeRateText.isDisposed() && !sizeRateText.getText().isEmpty()){
			try {
				sizeRate = Double.valueOf(sizeRateText.getText());
				if(sizeRate<0 || sizeRate>100) throw new IllegalArgumentException();
			} catch (Exception e) {
				setErrorMessage(Messages.PageTwo_SizeRateError);
				return false;
			}
		}
		if(threshText!=null && !threshText.isDisposed() && !threshText.getText().isEmpty()){
			try {
				thresh = Integer.valueOf(threshText.getText());
				if(thresh<0 || thresh>255) throw new IllegalArgumentException();
			} catch (Exception e) {
				setErrorMessage(Messages.PageTwo_ThreshError);
				return false;
			}
		}
		return true;
	}

	public boolean getIsChecked() {
		return isChecked;
	}

	public boolean isShowImages() {
		return isShowImages;
	}

	public int getTresh() {
		return thresh;
	}

	public double getSizeRate() {
		return sizeRate;
	}

	public double getDistNeigborObjects() {
		return distNeighborObjects;
	}

	public double getCoverAreaThr() {
		return coverAreaThr;
	}
	
	// AZ - validate page only if selected project is an S-CASE project
	@Override
	protected boolean validatePage() {
		boolean superReturn = super.validatePage(),
				pathFound = false;
		IPath myPath = getContainerFullPath();
		for (IProject ip : scaseProjectList) {
			if (myPath == null) break;
			if (myPath.lastSegment().equals(ip.getFullPath().lastSegment())) {
				wizard.setSelectedProject(ip);
				pathFound = true;
				break;
			}
		}
		return superReturn && pathFound;
	}
}
