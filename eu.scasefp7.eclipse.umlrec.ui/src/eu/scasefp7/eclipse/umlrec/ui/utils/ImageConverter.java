package eu.scasefp7.eclipse.umlrec.ui.utils;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

public class ImageConverter {
	
	public static BufferedImage convertToAWT(String imagePath){
		ColorModel colorModel = null;
		BufferedImage bufferedImg=null;
		ImageData nData=new ImageData(imagePath);
		if(nData.palette.isDirect){
			colorModel = new DirectColorModel(nData.depth, nData.palette.redMask, nData.palette.greenMask, nData.palette.blueMask);
			bufferedImg = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(nData.width, nData.height), false, null);
			for (int y = 0; y < nData.height; y++) {
				for (int x = 0; x < nData.width; x++) {
					int pixel = nData.getPixel(x, y);
					RGB rgb = nData.palette.getRGB(pixel);
					bufferedImg.setRGB(x, y,  rgb.red << 16 | rgb.green << 8 | rgb.blue);
				}
			}
		} else {
			RGB[] rgbs = nData.palette.getRGBs();
			byte[] red = new byte[rgbs.length];
			byte[] green = new byte[rgbs.length];
			byte[] blue = new byte[rgbs.length];
			for (int i = 0; i < rgbs.length; i++) {
				RGB rgb = rgbs[i];
				red[i] = (byte)rgb.red;
				green[i] = (byte)rgb.green;
				blue[i] = (byte)rgb.blue;
			}
			if (nData.transparentPixel != -1) {
				colorModel = new IndexColorModel(nData.depth, rgbs.length, red, green, blue, nData.transparentPixel);
			} else {
				colorModel = new IndexColorModel(nData.depth, rgbs.length, red, green, blue);
			}		
			bufferedImg = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(nData.width, nData.height), false, null);
			WritableRaster raster = bufferedImg.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < nData.height; y++) {
				for (int x = 0; x < nData.width; x++) {
					int pixel = nData.getPixel(x, y);
					pixelArray[0] = pixel;
					raster.setPixel(x, y, pixelArray);
				}
			}
		}
		
		return bufferedImg;
	}
	
	public static ImageData convertToSWT (BufferedImage bufferedImage) {
		
	    if (bufferedImage.getColorModel() instanceof DirectColorModel) {
	        DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
	        PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
	        ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
	        WritableRaster raster = bufferedImage.getRaster();
	        int[] pixelArray = new int[3];
	        for (int y = 0; y < data.height; y++) {
	            for (int x = 0; x < data.width; x++) {
	                raster.getPixel(x, y, pixelArray);
	                int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
	                data.setPixel(x, y, pixel);
	            }
	        }
	        
	        return data;
	    } else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
	        IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
	        int size = colorModel.getMapSize();
	        byte[] reds = new byte[size];
	        byte[] greens = new byte[size];
	        byte[] blues = new byte[size];
	        colorModel.getReds(reds);
	        colorModel.getGreens(greens);
	        colorModel.getBlues(blues);
	        RGB[] rgbs = new RGB[size];
	        for (int i = 0; i < rgbs.length; i++) {
	            rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
	        }
	        PaletteData palette = new PaletteData(rgbs);
	        ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
	        data.transparentPixel = colorModel.getTransparentPixel();
	        WritableRaster raster = bufferedImage.getRaster();
	        int[] pixelArray = new int[1];
	        for (int y = 0; y < data.height; y++) {
	            for (int x = 0; x < data.width; x++) {
	                raster.getPixel(x, y, pixelArray);
	                data.setPixel(x, y, pixelArray[0]);
	            }
	        }
	        return data;
	    }
	    return null;
	}

}
