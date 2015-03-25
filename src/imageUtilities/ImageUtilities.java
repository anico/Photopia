package imageUtilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import displayRunner.DisplayRunner;

public class ImageUtilities extends JPanel implements ActionListener, AdjustmentListener, MouseListener, MouseMotionListener, ChangeListener
{
	private static final long serialVersionUID = 1L;
	JDialog window = new JDialog();
	public static JColorChooser colorPalette = new JColorChooser();
	
	public ImageUtilities()
	{
		super(new BorderLayout());
		colorPalette.getSelectionModel().addChangeListener(this);
		colorPalette.setBorder(BorderFactory.createTitledBorder("Choose Color"));
		add(colorPalette, BorderLayout.CENTER);
		colorPalette.setPreviewPanel(new JPanel());
	}
	
	public Color run()
	{
		window.setLayout(null);
		window.setBounds(500, 250, 400, 220);
		window.setMinimumSize(new Dimension(400, 220));
		window.setMaximumSize(new Dimension(400, 220));
		window.setTitle("Color Palette");
		window.setVisible(true);
		
		JComponent newContentPane = new ImageUtilities();
		newContentPane.setOpaque(true);
		window.setContentPane(newContentPane);
		
		window.pack();
		window.repaint();
		return colorPalette.getColor();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateChanged(ChangeEvent arg0)
	{
		Color newColor = colorPalette.getColor();
		DisplayRunner.colorPreview.setBackground(newColor);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}	
}
