package fileFilters;
import java.io.File;

/**
 * File filter class for GIF files.
 * This is used by JFileChoosers to eliminate all images from the search that are not GIF images
 * 
 */
public class GIFFilter extends javax.swing.filechooser.FileFilter {
	/**
	 * Only returns files that have the .gif file extension when accepted into the file list
	 */
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}

		if (file.getName().toLowerCase().endsWith("gif")) {
			return true;
		}
		return false;
	}

	/**
	 *  Return the description of the file type
	 */
	@Override
	public String getDescription() {
		return ".GIF";
	}

}