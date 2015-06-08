package eu.scasefp7.eclipse.umlrec.ui.wizard;

import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.imgscalr.Scalr;

import eu.scasefp7.eclipse.umlrec.ui.utils.ImageConverter;

public class PageOne extends WizardPage {

	private Text filePathText;
	private static final String[] FILTER_EXTS = { Messages.PageOne_ImageExtensions }; 
	private static final String[] FILTER_NAMES = new String[] { Messages.FilterExtensionName };
	private Label label2;
	private Button[] radios;

	/**
	 * Create the wizard.
	 */
	public PageOne() {
		super("Wizard Page One");  //$NON-NLS-1$
		setTitle(Messages.PageOne_Title);
		setDescription(Messages.PageOne_Description);
		setPageComplete(false);
		
	}
	
	public String getFilePath(){
		return filePathText.getText();
	}
	
	public boolean getIsUseCase(){
		return radios[1].getSelection();
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 5;
		layout.verticalSpacing = 9;
		

		Label label = new Label(container, SWT.NULL);
		label.setText(Messages.PageOne_Label);

		filePathText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan=3;
		filePathText.setLayoutData(gd);
		
		filePathText.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				setPageComplete(validate(container));
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText(Messages.PageOne_Button);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse(parent, container);
			}
		});
		

		Label radioLabel=new Label(container, SWT.NULL);
		radioLabel.setText(Messages.PageOne_DiagramTypeLabel);
			
		radios=new Button[3];
		radios[0]=new Button(container, SWT.RADIO);
		radios[0].setText(Messages.PageOne_OptionOne);
		radios[0].setSelection(true);
		radios[1]=new Button(container, SWT.RADIO);
		radios[1].setText(Messages.PageTwo_OptionTwo);

		label2=new Label(container, SWT.NULL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
	    gridData.horizontalSpan = 5;
	    
	    label2.setLayoutData(gridData);
		label2.setVisible(false);
		
		setControl(container);

	}
	
	private boolean validate(Composite container){
		if (!filePathText.getText().isEmpty() && new File(filePathText.getText()).exists()) {
			setErrorMessage(null);
			viewImage(filePathText.getText(), container);
			return true;
		}
		label2.setVisible(false);
		setErrorMessage(Messages.PageOne_ErrorMessage);
		return false;
		
	}

	private void handleBrowse(Composite parent, Composite container) {

		FileDialog dialog = new FileDialog(getShell());
		dialog.setFilterExtensions(FILTER_EXTS);
		dialog.setFilterNames(FILTER_NAMES);

		String selected = dialog.open();

		if (selected != null) {
			filePathText.setText(selected);
			setPageComplete(true);
			setErrorMessage(null);	
			
			viewImage(selected, container);

		}

	}
	
	private void viewImage(String imagePath, Composite container){
		
		File sourceimage = new File(imagePath);
		BufferedImage originalImage = null;

		try {
			originalImage = ImageIO.read(sourceimage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(!(originalImage.getColorModel() instanceof DirectColorModel) && !(originalImage.getColorModel() instanceof IndexColorModel)){		
			originalImage=ImageConverter.convertToAWT(imagePath);
		}
		
		int newWidth = originalImage.getWidth();
		int newHeight = originalImage.getHeight();
		
		if(newWidth>newHeight && newWidth>500){
			double yScale=(double)500/newWidth;
			newWidth = (int)(newWidth * yScale);
			newHeight = (int)(newHeight * yScale);	
			
			if(newHeight>320){
				double xScale=(double)320/newHeight;		
				newWidth = (int)(newWidth * xScale);
				newHeight = (int)(newHeight * xScale);
			}
			
			originalImage=Scalr.resize(originalImage, Scalr.Method.ULTRA_QUALITY, newWidth, newHeight);
			
		} else if(newHeight>newWidth && newHeight >320){
			double xScale=(double)320/newHeight;		
			newWidth = (int)(newWidth * xScale);
			newHeight = (int)(newHeight * xScale);
			
			originalImage=Scalr.resize(originalImage, Scalr.Method.ULTRA_QUALITY, newWidth, newHeight);
		}
		
		
		Image swtImage=new Image(getShell().getDisplay(), ImageConverter.convertToSWT(originalImage));
		
		label2.setImage(swtImage);
		label2.setVisible(true);
		container.layout();
		
	}
	
}
