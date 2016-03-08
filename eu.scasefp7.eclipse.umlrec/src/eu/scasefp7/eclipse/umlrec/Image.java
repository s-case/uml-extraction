package eu.scasefp7.eclipse.umlrec;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Image {
	String name;
	String format;
	int imageThreshold;
	double rectangleRate;
	double minDistance;
	double minRate;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getImageThreshold() {
		return imageThreshold;
	}

	public void setImageThreshold(int imageThreshold) {
		this.imageThreshold = imageThreshold;
	}

	public double getRectangleRate() {
		return rectangleRate;
	}

	public void setRectangleRate(double rectangleRate) {
		this.rectangleRate = rectangleRate;
	}

	public double getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(double minDistance) {
		this.minDistance = minDistance;
	}

	public double getMinRate() {
		return minRate;
	}

	public void setMinRate(double minRate) {
		this.minRate = minRate;
	}

	

}
