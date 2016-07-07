package eu.scasefp7.eclipse.umlrec.papyrus.utils;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import org.eclipse.core.resources.IFile;

/**
 * 
 * @author tsirelis
 *
 */

public class FileUtils {

	/**
	 * Gets the Filename without extension
	 * @param file - The File
	 * @return - Filename without extension
	 */
	public static String getFileNameWithOutExtension(IFile file){
		String name = file.getName();
		int pos = name.lastIndexOf(".");
		if (pos > 0) {
		    name = name.substring(0, pos);
		}
		return name;
	}
	
	/**
	 * Deletes a file
	 * @param path - The file path
	 */
	public static void deleteFile(Path path) {
		try {
		    Files.delete(path);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", path);
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", path);
		} catch (IOException x) {
		    // File permission problems are caught here.
		    System.err.println(x);
		}
	}
}
