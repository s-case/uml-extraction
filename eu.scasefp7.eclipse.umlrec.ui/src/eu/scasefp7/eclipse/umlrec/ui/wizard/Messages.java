package eu.scasefp7.eclipse.umlrec.ui.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "eu.scasefp7.eclipse.umlrec.ui.wizard.messages"; //$NON-NLS-1$
	public static String PageOne_DiagramTypeLabel;
	public static String FileEditorJob_EditorOpenFailed;
	public static String FileEditorJobDescription;
	public static String FilterExtensionName;
	public static String MyWizard;
	public static String PageOne_OptionOne;
	public static String PageTwo_OptionTwo;
	public static String PageOne_Button;
	public static String PageOne_Description;
	public static String PageOne_ErrorMessage;
	public static String PageOne_ImageExtensions;
	public static String PageOne_Label;
	public static String PageOne_Title;
	public static String PageTwo_AdvancedButtonHideText;
	public static String PageTwo_AdvancedButtonShowText;
	public static String PageTwo_CoverAreaThrError;
	public static String PageTwo_CoverAreaThrLabel;
	public static String PageTwo_DistNeighborError;
	public static String PageTwo_DistNeighborObjectsLabel;
	public static String PageTwo_ShowImagesCheckboxLabel;
	public static String PageTwo_SizeRateError;
	public static String PageTwo_SizeRateLabel;
	public static String PageTwo_ThreshError;
	public static String PageTwo_ThreshLabel;
	public static String PageTwo_Checkbox;
	public static String PageTwo_Description;
	public static String PageTwo_Title;
	
	public static String UMLrecognizerJob_GetXMIContentDescription;
	public static String UMLrecognizerJob_JNIFail;
	public static String UMLrecognizerJob_ProcessDescription;
	public static String UMLrecognizerJob_RecognitionFail;
	public static String UMLrecognizerJob_RecognitionFailGeneral;
	public static String UMLrecognizerJob_RecognStart;
	public static String UMLrecognizerJob_SetImageDescription;
	public static String UMLrecognizerJob_UMLrecognizerJobName;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
