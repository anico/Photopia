package displayRunner;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;


/**
 * this dialogue is opened when the Settings menu item is selected.  It prompts the user
 * to enter in the desired settings information, and saves that to the appropriate file
 * @author aaron
 *
 */
public class SettingsDialogue implements ActionListener 
{
	JDialog window;
	
	JLabel loadLabel;
	JLabel saveLabel;
	JLabel imgType;
	
	JTextField loadpath;
	JTextField savepath;

	JComboBox imgExtension;
	
	String[] extensions = {"JPG", "GIF", "BMP", "PNG"};
	
	JButton save;
	JButton cancel;
	
	FileOutputStream fout;
	PrintStream print;

	public SettingsDialogue()
	{
		JDialog.setDefaultLookAndFeelDecorated(false);
		window = new JDialog();
		
		loadLabel = new JLabel("Default Load Path:");
		saveLabel = new JLabel("Default Save Path:");
		imgType = new JLabel("Preferred Image Type:");
		
		loadpath = new JTextField();
		savepath = new JTextField();
		imgExtension = new JComboBox(extensions);
		
		save = new JButton("Save");
		cancel = new JButton("Cancel");
		
	}
	
	public void run()
	{
		window.setLayout(null);
		window.setBounds(500, 250, 400, 220);
		window.setMinimumSize(new Dimension(400, 220));
		window.setMaximumSize(new Dimension(400, 220));
		window.setVisible(true);
		
		loadLabel.setBounds(10, 25, 165, 20);
		loadLabel.setVisible(true);
		
		loadpath.setBounds(165, 25, 175, 25);
		loadpath.setMinimumSize(new Dimension(175, 25));
		loadpath.setMaximumSize(new Dimension(175, 25));
		
		loadpath.setEditable(true);
		loadpath.setVisible(true);
		
		saveLabel.setBounds(10, 75, 165, 20);
		saveLabel.setVisible(true);
		
		savepath.setBounds(165, 75, 175, 25);
		savepath.setMinimumSize(new Dimension(175, 25));
		savepath.setMaximumSize(new Dimension(175, 25));
		savepath.setEditable(true);
		savepath.setVisible(true);
		
		imgType.setBounds(10, 125, 175, 20);
		imgType.setVisible(true);
		
		imgExtension.setBounds(175 , 125, 75, 25);
		imgExtension.setMinimumSize(new Dimension(65, 25));
		imgExtension.setMaximumSize(new Dimension(65, 25));
		imgExtension.setVisible(true);
		
		save.setBounds(278, 115, 75, 25);
		save.setMinimumSize(new Dimension(75, 25));
		save.setMaximumSize(new Dimension(75, 25));
		save.addActionListener(this);
		save.setVisible(true);
		
		cancel.setBounds(278, 145, 75, 25);
		cancel.setMinimumSize(new Dimension(75, 25));
		cancel.setMaximumSize(new Dimension(75, 25));
		cancel.addActionListener(this);
		cancel.setVisible(true);
		
		window.getContentPane().add(loadLabel);
		window.getContentPane().add(loadpath);
		window.getContentPane().add(saveLabel);
		window.getContentPane().add(savepath);
		window.getContentPane().add(imgType);
		window.getContentPane().add(imgExtension);
		window.getContentPane().add(save);
		window.getContentPane().add(cancel);
		
		setTextFields();
		
		window.pack();
		window.repaint();	
	}

	/**
	 * this method enters any existing settings information into the text feilds
	 * when the dialogue is opened
	 */
	private void setTextFields()
	{
		FileReader f;
		try 
		{
			f = new FileReader("Settings.txt");
		} 
		catch (FileNotFoundException e1) 
		{
			System.err.println("No Settings Found");
			return;
		}
		Scanner in;

		in = new Scanner(f);
		if(in.hasNextLine())
		{
			loadpath.setText(in.nextLine());
		}
		if(in.hasNextLine())
		{
			savepath.setText(in.nextLine());			
		}
		if(in.hasNextLine())
		{		
			imgExtension.setSelectedItem(in.nextLine());
		}
		in.close();
			
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
	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getActionCommand() == "Cancel")
		{
			dispose();
		}
		else if(arg0.getActionCommand() == "Save")
		{
			try 
			{
				fout = new FileOutputStream("Settings.txt");
			} 
			catch (FileNotFoundException e) 
			{
				System.err.println("fout error - construction");
			}
			print = new PrintStream(fout);
			print.println(loadpath.getText());
			print.println(savepath.getText());
			print.println(imgExtension.getSelectedItem().toString());
			try 
			{
				fout.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			print.close();
			dispose();
		}
		
	}
}
