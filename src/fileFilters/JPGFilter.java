package fileFilters;
import java.io.File;

/**
 * File filter class for JPG files.
 * This is used by JFileChoosers to eliminate all images from the search that are not JPG images
 * 
 */
public class JPGFilter extends javax.swing.filechooser.FileFilter {
	
	/**
	 * Only returns files that end with .jpg when accepted into the file list
	 */
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
		if (file.getName().toLowerCase().endsWith("jpg") || file.getName().toLowerCase().endsWith("jpeg")) {
			return true;

		}
		return false;

	}

	/**
	 *  Return the description of the file type
	 */
	@Override
	public String getDescription() {
		return ".JPG";
	}

}