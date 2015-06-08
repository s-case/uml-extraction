package eu.scasefp7.eclipse.umlrec.internal;

import org.eclipse.osgi.util.NLS;

public class Libraries extends NLS {
	private static final String BUNDLE_NAME = "eu.scasefp7.eclipse.umlrec.internal.libraries"; //$NON-NLS-1$
	public static String UMLRecognizerJNI_LibrariesList;
	public static String UMLRecognizerJNI_Separator;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Libraries.class);
	}

	private Libraries() {
	}
}
