package fileFilters;
import java.io.File;

/**
 * File filter class for PNG files.
 * This is used by JFileChoosers to eliminate all images from the search that are not PNG images
 * 
 */
public class PNGFilter extends javax.swing.filechooser.FileFilter {
	
	/**
	 * Only returns files that end with .png when accepted into the file list
	 */
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}

		if (file.getName().toLowerCase().endsWith("png")) {
			return true;

		}
		return false;

	}

	/**
	 *  Return the description of the file type
	 */
	@Override
	public String getDescription() {
		return ".PNG";
	}

}