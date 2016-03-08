package eu.scasefp7.eclipse.umlrec;

import java.io.IOException;

import java.net.URL;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.MultiPart;

import javax.imageio.ImageIO;
import javax.ws.rs.core.MediaType;

import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class UMLRecognizer {

	/** Store the image file name */
	protected String _fileName = null;

	/** The UML Server url */
	final String BASE_URI = "http://uml.scasefp7.com:8080/UMLServer/";
	private Image image = new Image();
	private String xmi;
	private boolean isUseCase;
	public static final boolean SHOW_IMAGES = false;
	public static final int THRESH = 230;
	public static final double SIZE_RATE = 1.0;
	public static final double DIST_NEIGHBOR_OBJECTS = 20.0;
	public static final double COVER_AREA_THR = 1.0;

	/**
	 * 
	 */
	public UMLRecognizer() {

	}

	public void setIsUseCase(boolean isUseCase) {
		this.isUseCase = isUseCase;
	}

	public void setImage(String fileName) throws RecognitionException {
		image.setName(fileName);
		String[] split = fileName.split("\\.");
		image.setFormat(split[split.length - 1]);

	}

	public void process() throws MissingRecognizerDataException, RecognitionException, IOException {
		// The process function returns
		// 0 for a successful run,
		// -2 if TessData files are missing OR the program failed to analyse the
		// text of the diagram and
		// -1 if an unknown error occurred.

		Client c = Client.create();

		WebResource service = c.resource(BASE_URI);

		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		URL url = new File(image.getName()).toURI().toURL();
		BufferedImage bi = ImageIO.read(url);
		ImageIO.write(bi, image.getFormat(), bas);
		byte[] logo = bas.toByteArray();

		// Construct a MultiPart with two body parts
		MultiPart multiPart = new MultiPart().bodyPart(new BodyPart(image, MediaType.APPLICATION_XML_TYPE))
				.bodyPart(new BodyPart(logo, MediaType.APPLICATION_OCTET_STREAM_TYPE));

		// POST the request

		if (isUseCase) {

			ClientResponse response = service.path("/usecase").type("multipart/mixed").post(ClientResponse.class,
					multiPart);
			xmi = response.getEntity(String.class);
		} else {

			ClientResponse response = service.path("/activity").type("multipart/mixed").post(ClientResponse.class,
					multiPart);
			xmi = response.getEntity(String.class);
		}

		return;
	}

	public String getXMIcontent() {
		return this.xmi;
	}

	public void setParameters(boolean isUseCase, boolean showImages, int thresh, double sizeRate,
			double distNeightborObjects, double coverAreaThr) {
		this.isUseCase = isUseCase;
		image.setImageThreshold(thresh);
		image.setRectangleRate(sizeRate);
		image.setMinDistance(distNeightborObjects);
		image.setMinRate(coverAreaThr);

	}

	public Image getImage() {
		return image;
	}

	public void setXmi(String xmi) {
		this.xmi = xmi;
	}

}
