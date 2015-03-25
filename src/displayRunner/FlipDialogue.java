package displayRunner;

import imgFilters.*;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * This dialogue is opened when the flip button is pressed, it is used
 * to determine whether to flip about the horizontal or verticle axis
 * @author aaron
 *
 */
public class FlipDialogue implements ActionListener
{
	private JDialog flipDialog;
	private JButton vert;
	private JButton horiz;
	private JButton cancel;

	private BufferedImage img;
	
	private int tab;
	private int x, y;
	
	public FlipDialogue(int n, int x, int y)
	{
		tab = n;
		img = null;
		
		this.x = x;
		this.y = y;

		flipDialog = new JDialog();
		vert = new JButton("Vertical");
		horiz = new JButton("Horizontal");	
		cancel = new JButton("Cancel");
	}
	public void run()
	{
		try
		{
			img = DisplayRunner.imgPans.get(tab).getImg();
		}
		catch(Exception e)
		{
			flipDialog.dispose();
			return;
		}
		
		/*
		 * setting the position of the dialogue frame
		 */
		Point p = DisplayRunner.accessFrame.getLocation();
		p.setLocation(p.getX() + 95, p.getY() + 85);
		flipDialog.setLocation(p);
		
		flipDialog.setMinimumSize(new Dimension(200, 100));
		flipDialog.setMaximumSize(new Dimension(200, 100));
		flipDialog.setTitle("Flip");
		flipDialog.setVisible(true);
		
		JPanel pan = new JPanel();
		
		vert.setMaximumSize(new Dimension(100, 25));
		vert.addActionListener(this);
		
		horiz.setMaximumSize(new Dimension(100, 25));
		horiz.addActionListener(this);

		cancel.setMaximumSize(new Dimension(100, 25));
		cancel.addActionListener(this);
		
		pan.add(vert);
		pan.add(horiz);
		pan.add(cancel);
		
		flipDialog.add(pan);
		
		vert.setVisible(true);
		horiz.setVisible(true);
		cancel.setVisible(true);

		flipDialog.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Filters op = new Filters();
		
		if(e.getActionCommand() == "Vertical")
		{
			img = op.Flip(img, 1);
			DisplayRunner.imgPans.get(tab).setImage(img);
			
			flipDialog.dispose();
		}
		else if(e.getActionCommand() == "Horizontal")
		{
			img = op.Flip(img, 0);
			DisplayRunner.imgPans.get(tab).setImage(img);
			
			flipDialog.dispose();
		}	
		else if(e.getActionCommand() == "Cancel")
		{
			flipDialog.dispose();
		}
	}

}
