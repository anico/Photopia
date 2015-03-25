package fileFilters;
import java.io.File;

/**
 * File filter class for BMP file types.
 * This is used by JFileChoosers to eliminate all images from the search that are not BMP images
 * 
 */
public class BMPFilter extends javax.swing.filechooser.FileFilter {
	
	/**
	 * Only returns files that end with .bmp when accepted into the file list
	 */
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}

		if (file.getName().toLowerCase().endsWith("bmp")) {
			return true;

		}
		return false;

	}
	
	/**
	 *  Return the description of the file type
	 */
	@Override
	public String getDescription() {
		return ".BMP";
	}

}