package fileFilters;
import java.io.File;

/**
 * General File Filter class that shows all readable file types
 * This is used by JFileChoosers to eliminate all images from the search that are not readable by Photopia
 * 
 */
public class ImageFilter extends javax.swing.filechooser.FileFilter {
	private final String[] supportedFileExtensions = new String[] { "jpg", "jpeg", "png",
			"gif", "bmp"};

	/**
	 * Only returns files that end with the .jpg, .jpeg, .png, .gif, or .bmp file extensions when accepted into the file list
	 */
	@Override
	public boolean accept(File file) {
		if(file.isDirectory()){
			return true;
		}
		
		for (String extension : supportedFileExtensions) {
			if (file.getName().toLowerCase().endsWith(extension)) {
				return true;
			}
		}
		return false;

	}

	/**
	 *  Return the description of the file type
	 */
	@Override
	public String getDescription() {
		return "All Supported Image Files";
	}

}