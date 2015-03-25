package displayRunner;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * this is opened when the New menu item is selected, it opens a dialogue to determine the size
 * of the new image
 * @author aaron
 *
 */
public class NewDialogue implements ActionListener 
{
	JDialog window;
	
	JLabel widthLabel;
	JLabel heightLabel;
	
	JTextField width;
	JTextField height;

	JButton ok;
	JButton cancel;

	BufferedImage newImg;
	
	public NewDialogue()
	{
		JDialog.setDefaultLookAndFeelDecorated(false);
		window = new JDialog();
		
		widthLabel = new JLabel("Image Width (pixels): ");
		heightLabel = new JLabel("Image Height (pixels): ");
		
		width = new JTextField();
		height = new JTextField();
		
		ok = new JButton("OK");
		cancel = new JButton("Cancel");
		
	}
	
	public void run()
	{
		window.setTitle("New");
		window.setLayout(null);
		window.setBounds(500, 250, 185, 200);
		window.setMinimumSize(new Dimension(185, 200));
		window.setMaximumSize(new Dimension(185, 200));
		window.setVisible(true);
		
		widthLabel.setBounds(10, 25, 125, 20);
		widthLabel.setVisible(true);
		
		width.setBounds(125, 25, 35, 25);
		width.setMinimumSize(new Dimension(35, 25));
		width.setMaximumSize(new Dimension(35, 25));
		
		width.setEditable(true);
		width.setVisible(true);
		
		heightLabel.setBounds(10, 75, 125, 20);
		heightLabel.setVisible(true);
		
		height.setBounds(125, 75, 35, 25);
		height.setMinimumSize(new Dimension(35, 25));
		height.setMaximumSize(new Dimension(35, 25));
		height.setEditable(true);
		height.setVisible(true);
		
		ok.setBounds(10, 125, 75, 25);
		ok.setMinimumSize(new Dimension(75, 25));
		ok.setMaximumSize(new Dimension(75, 25));
		ok.addActionListener(this);
		ok.setVisible(true);
		
		cancel.setBounds(90, 125, 75, 25);
		cancel.setMinimumSize(new Dimension(75, 25));
		cancel.setMaximumSize(new Dimension(75, 25));
		cancel.addActionListener(this);
		cancel.setVisible(true);
		
		window.getContentPane().add(widthLabel);
		window.getContentPane().add(width);
		window.getContentPane().add(heightLabel);
		window.getContentPane().add(height);
		window.getContentPane().add(ok);
		window.getContentPane().add(cancel);
		
		Point p = DisplayRunner.accessFrame.getLocation();
		p.setLocation(p.getX() + 95, p.getY() + 85);
		window.setLocation(p);
		
		window.pack();
		window.repaint();	
	}

	/**
	 * disposes the frame
	 */
	private void dispose()
	{
		window.setVisible(false);
		window.dispose();
		return;
	}
	
	/**
	 * makes the entire image white, the default color
	 */
	private void makeWhite()
	{
		int[] whitePixel = {255, 255, 255};
		WritableRaster newData = newImg.getData().createCompatibleWritableRaster(newImg.getWidth(), newImg.getHeight());;
		
		for(int i = 0; i < newImg.getWidth(); i++)
		{
			for(int j = 0; j < newImg.getHeight(); j++)
			{
				newData.setPixel(i, j, whitePixel);
			}
		}
		newImg = new BufferedImage(newImg.getColorModel(), newData, newImg.isAlphaPremultiplied(), null);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getActionCommand() == "Cancel")
		{
			dispose();
		}
		else if(arg0.getActionCommand() == "OK")
		{
			String fname = "New Image(" + DisplayRunner.newImageCount + ")";
			DisplayRunner.newImageCount++;
			newImg = new BufferedImage(Integer.parseInt(width.getText()), Integer.parseInt(height.getText()), 1);
			makeWhite();
			ImagePanel tmpImgPan = new ImagePanel(newImg);
			tmpImgPan.setImage(newImg);
			DisplayRunner.tabbedPane.addTab(fname, new JScrollPane(tmpImgPan));
			DisplayRunner.tabbedPane.setTabComponentAt(DisplayRunner.tabbedPane.indexOfTab(fname), new CloseButton(DisplayRunner.tabbedPane));	
			DisplayRunner.imgPans.add(DisplayRunner.tabbedPane.indexOfTab(fname), tmpImgPan);
			DisplayRunner.imgPans.get(DisplayRunner.tabbedPane.indexOfTab(fname)).setScaleFactor(1);
			dispose();
		}
		
	}
}
