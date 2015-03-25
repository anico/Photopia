package displayRunner;

import imageUtilities.ImageUtilities;
import imgFilters.*;
import fileFilters.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.UIManager;
 
public class DisplayRunner extends JFrame implements ActionListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = (long) 1.0;

	//DisplayRunner Swing components
	private final JFileChooser openfc = new JFileChooser();
	private final JFileChooser savefc = new JFileChooser();
	private static JMenuBar myMenuBar = new JMenuBar();
	private JPanel myPanel = new JPanel();	
	public static JPanel colorPreview = new JPanel();
	private JToolBar toolBox = new JToolBar(JToolBar.VERTICAL);
	private JComboBox zoomScale;
	private JMenu openRecDoc;
	public static ImageTabs tabbedPane = new ImageTabs();
	private boolean eyedropper = false;
	private JComboBox brushSize;

	//unseen variables
	public static ArrayList<ImagePanel> imgPans = new ArrayList<ImagePanel>(10);
	public static DisplayRunner accessFrame = null;
	private Filters op = new Filters();
	private BufferedImage img = null;
	private boolean drawn = false;
	private int picNumber = 0;
	public static int newImageCount = 0;
	private int x = 0, y = 0;

	//used in with the drawing tools, empty string for drawState means no button is active
	public static String drawState = "";
	public static Color selectedColor = Color.BLACK;
	
	//used for the settings file
	FileOutputStream fout;
	PrintStream print;
	FileReader f;

	/**
	 * Create and run an instance of Photopia.
	 */
	public static void main(String[] args) 
	{	
		//Attempts to give Photopia the appearance of the user's operating system
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e) {}

		//Create instance of DisplayRunner 
		DisplayRunner myframe = new DisplayRunner();
		//Create way to access the frame for classes in other packages
		accessFrame = myframe;
		//Initialize the Swing Components
		myframe.initialize();
	}

	/**
	 * Build and initialize the Swing components used by DisplayRunner
	 */
	private void initialize() 
	{
		//Initialize layouts and apply file filters to JFileChoosers
		applyFileFilters();
		setLayout(new BorderLayout());
		myPanel.setLayout(new BorderLayout());

		//Construct the major components
		createMenuBar();
		createToolbar();
		createToolbox();
		createWorkspace();

		//Add Components together and finish initializing the Photopia frame
		add(myPanel, BorderLayout.NORTH);
		setTitle("Photopia");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		drawn = true;

		//Loading the open recent menu from the settings file if applicable
		File settings = new File("Settings.txt");
		Scanner scan = null;
		String history;

		try 
		{
			scan = new Scanner(settings);
			System.out.println(scan.nextLine());
			System.out.println(scan.nextLine());
			System.out.println(scan.nextLine());
			while (scan.hasNextLine())
			{
				history = scan.nextLine();
				
				JMenuItem recentDoc = new JMenuItem(history);
				recentDoc.addActionListener(this);
				recentDoc.setActionCommand("recentDoc" + history);
				openRecDoc.add(recentDoc);
			}
			scan.close();
		} 
		catch (FileNotFoundException e) 
		{
			System.err.println("No Settings Found");
		}

	}

	/**
	 * Applies FileFilters for Photopia's supported file types.
	 */
	private void applyFileFilters() 
	{
		openfc.setAcceptAllFileFilterUsed(false);
		openfc.addChoosableFileFilter(new ImageFilter());
		openfc.addChoosableFileFilter(new GIFFilter());
		openfc.addChoosableFileFilter(new JPGFilter());
		openfc.addChoosableFileFilter(new PNGFilter());
		openfc.addChoosableFileFilter(new BMPFilter());
		openfc.setFileFilter(new ImageFilter());

		savefc.setAcceptAllFileFilterUsed(false);
		savefc.addChoosableFileFilter(new ImageFilter());
		savefc.addChoosableFileFilter(new GIFFilter());
		savefc.addChoosableFileFilter(new JPGFilter());
		savefc.addChoosableFileFilter(new PNGFilter());
		savefc.addChoosableFileFilter(new BMPFilter());
		savefc.setFileFilter(new ImageFilter());
	}

	/**
	 * Builds Photopia's workspace, which is the area where images are displayed and edited.
	 */
	private void createWorkspace() 
	{
		tabbedPane.setPreferredSize(new Dimension(500, 500));
		add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.addMouseListener(this);
		tabbedPane.addMouseMotionListener(this);
	}

	/**
	 * Constructs the tool box (located on the left side of the program), imports icon resources, and
	 * builds and initializes tool box buttons.
	 */
	private void createToolbox() 
	{

		//Builds ClassLoader to import icons from the resources folder
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		//Get an input stream for each icon
		InputStream brushStream = classLoader.getResourceAsStream("resources/brush.png");
		InputStream pencilStream = classLoader.getResourceAsStream("resources/pencil.png");
		InputStream eraserStream = classLoader.getResourceAsStream("resources/eraser.png");
		InputStream cropStream = classLoader.getResourceAsStream("resources/crop.png");
		InputStream handStream = classLoader.getResourceAsStream("resources/hand.png");
		InputStream colorpaletteStream = classLoader.getResourceAsStream("resources/colorpalette.png");
		InputStream eyedropperStream = classLoader.getResourceAsStream("resources/eyedropper.png");
		InputStream fillStream = classLoader.getResourceAsStream("resources/fill.png");
		InputStream lineStream = classLoader.getResourceAsStream("resources/line.png");
		InputStream rectangleStream = classLoader.getResourceAsStream("resources/rect.png");
		InputStream circleStream = classLoader.getResourceAsStream("resources/circle.png");
		InputStream triangleStream = classLoader.getResourceAsStream("resources/triangle.png");

		//Create Icon objects to hold button icons
		Icon brushIcon = null;
		Icon pencilIcon = null;
		Icon eraserIcon = null;
		Icon cropIcon = null;
		Icon handIcon = null;
		Icon colorpaletteIcon = null;
		Icon eyedropperIcon = null;
		Icon fillIcon = null;
		Icon lineIcon = null;
		Icon rectangleIcon = null;
		Icon circleIcon = null;
		Icon triangleIcon = null;

		//Attempt to load the icons with the images from the resources folder
		try 
		{
			brushIcon = new ImageIcon(ImageIO.read(brushStream));
			pencilIcon = new ImageIcon(ImageIO.read(pencilStream));
			eraserIcon = new ImageIcon(ImageIO.read(eraserStream));
			cropIcon = new ImageIcon(ImageIO.read(cropStream));
			handIcon = new ImageIcon(ImageIO.read(handStream));
			colorpaletteIcon = new ImageIcon(ImageIO.read(colorpaletteStream));
			eyedropperIcon = new ImageIcon(ImageIO.read(eyedropperStream));
			fillIcon = new ImageIcon(ImageIO.read(fillStream));
			lineIcon = new ImageIcon(ImageIO.read(lineStream));
			rectangleIcon = new ImageIcon(ImageIO.read(rectangleStream));
			circleIcon = new ImageIcon(ImageIO.read(circleStream));
			triangleIcon = new ImageIcon(ImageIO.read(triangleStream));
		} 
		catch (Exception e) 
		{
			System.err.println("Error Loading Icons");
		}

		//Initialize the toolBox 
		toolBox.setFloatable(false);
		toolBox.setRollover(true);

		//create array of the icons
		Icon[] icons = {brushIcon, pencilIcon, eraserIcon, cropIcon, handIcon, colorpaletteIcon, eyedropperIcon,
				fillIcon, lineIcon, rectangleIcon, circleIcon, triangleIcon};

		//create array of actionCommands corresponding to the icon in the same element from the icons array
		String[] actionCommands = {"brush", "pencil", "eraser", "crop", "hand", "color palette", "eye dropper", 
				"fill", "line", "rectangle", "circle", "triangle"};

		//create array of tool tip text corresponding to the icon in the same element from the icons array
		String[] toolTips = {"Brush Tool", "Pencil Tool", "Eraser", "Crop", "Hand Tool", "Color Palette", 
				"Eye Drop Tool", "Fill Tool", "Draw Line", "Draw Rectangle", "Draw Circle", "Draw Triangle"};

		/*
		 * Add each button to the toolBox, giving it the correct icon, actionCommand, and toolTipText 
		 */
		for (int i = 0; i < 12; i += 2) 
		{
			JToolBar row = new JToolBar(JToolBar.HORIZONTAL);
			row.setFloatable(false);
			row.setRollover(true);

			JButton button1 = new JButton(icons[i]);
			button1.setActionCommand(actionCommands[i]);
			button1.setToolTipText(toolTips[i]);
			button1.addActionListener(this);

			JButton button2 = new JButton(icons[i+1]);
			button2.setActionCommand(actionCommands[i+1]);
			button2.setToolTipText(toolTips[i+1]);
			button2.addActionListener(this);

			row.add(button1);
			row.add(button2);
			toolBox.add(row);
		}

		//Label for the brush size selection tool
		JLabel sizeLabel = new JLabel("Brush Size");
		sizeLabel.setMaximumSize(new Dimension(100, 20));
		toolBox.add(sizeLabel);
		sizeLabel.setVisible(true);
		
		//used to select the size of variable width tools such as brush and eraser.  the size is measured in #*2 pixels
		Integer[] sizes = {1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 100};
		brushSize = new JComboBox(sizes);
		brushSize.addActionListener(this);
		brushSize.setActionCommand("brushSize");
		brushSize.setSelectedIndex(4);
		brushSize.setMaximumSize(new Dimension(60, 20));
		toolBox.add(brushSize);
		
		toolBox.addSeparator();
		
		//Construct a color preview area to interact with various tools in the toolBox
		JLabel previewText = new JLabel("Selected Color");
		previewText.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		toolBox.add(previewText);
		colorPreview.setMaximumSize(new Dimension(60, 60));
		colorPreview.setPreferredSize(new Dimension(60, 60));
		
		colorPreview.setBackground(Color.BLACK);
		colorPreview.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		toolBox.add(colorPreview);

		//add the toolBox to the instance of DisplayRunner
		add(toolBox, BorderLayout.WEST);
	}

	private void createToolbar() 
	{
		//Create ClassLoader to import the icons from the resources folder
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		//Get an input stream for each icon
		InputStream saveStream = classLoader.getResourceAsStream("resources/save.png");
		InputStream openStream = classLoader.getResourceAsStream("resources/open.png");
		InputStream rotateLStream = classLoader.getResourceAsStream("resources/rotateL.png");
		InputStream rotateRStream = classLoader.getResourceAsStream("resources/rotateR.png");
		InputStream flipStream = classLoader.getResourceAsStream("resources/flip.png");
		InputStream zoomStream = classLoader.getResourceAsStream("resources/zoom.png");

		//Create Icon objects to hold button icons
		Icon saveIcon = null;
		Icon openIcon = null;
		Icon rotateLIcon = null;
		Icon rotateRIcon = null;
		Icon flipIcon = null;
		Icon zoomIcon = null;

		//Attempt to load the icons with the images from the resources folder
		try 
		{
			saveIcon = new ImageIcon(ImageIO.read(saveStream));
			openIcon = new ImageIcon(ImageIO.read(openStream));
			rotateLIcon = new ImageIcon(ImageIO.read(rotateLStream));
			rotateRIcon = new ImageIcon(ImageIO.read(rotateRStream));
			flipIcon = new ImageIcon(ImageIO.read(flipStream));
			zoomIcon = new ImageIcon(ImageIO.read(zoomStream));
		} 
		catch (Exception e) {}

		//Create and initialize the JToolBar
		setSize(800, 800);
		JToolBar tools = new JToolBar(JToolBar.HORIZONTAL);
		tools.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		tools.setFloatable(false);
		tools.setRollover(true);

		//Create buttons for the tool bar, assigning ActionCommands and ToolTip Text where necessary
		JButton open = new JButton(openIcon);
		open.setActionCommand("Open");
		open.setToolTipText("Open");
		open.addActionListener(this);

		JButton save = new JButton(saveIcon);
		save.setActionCommand("Save");
		save.setToolTipText("Save")	;
		save.addActionListener(this);

		JButton rotateR = new JButton(rotateRIcon);
		rotateR.setActionCommand("Rotate Right");
		rotateR.setToolTipText("Rotate Right");
		rotateR.addActionListener(this);

		JButton rotateL = new JButton(rotateLIcon);
		rotateL.setActionCommand("Rotate Left");
		rotateL.setToolTipText("Rotate Left");
		rotateL.addActionListener(this);

		JButton flip = new JButton(flipIcon);
		flip.setActionCommand("Flip");
		flip.setToolTipText("Flip");
		flip.addActionListener(this);

		JButton sharpen = new JButton("Sharpen");
		sharpen.addActionListener(this);

		JButton blur = new JButton("Blur");
		blur.addActionListener(this);

		JLabel zoom = new JLabel(zoomIcon);
		zoom.setToolTipText("Zoom");

		//initialize the zoom JComboBox
		zoomScale = new JComboBox();
		zoomScale.addItem("20%");
		zoomScale.addItem("40%");
		zoomScale.addItem("60%");
		zoomScale.addItem("80%");
		zoomScale.addItem("100%");
		zoomScale.addItem("150%");
		zoomScale.addItem("200%");
		zoomScale.addItem("400%");
		zoomScale.addActionListener(this);
		zoomScale.setActionCommand("Zoom");
		zoomScale.setSelectedIndex(4);
		zoomScale.setMaximumSize(new Dimension(61, 20));

		//Add all the tool bar components to the tool bar
		tools.add(open);
		tools.add(save);
		tools.addSeparator();
		tools.add(rotateL);
		tools.add(rotateR);
		tools.addSeparator();
		tools.add(flip);
		tools.addSeparator();
		tools.add(sharpen);
		tools.addSeparator();
		tools.add(blur);
		tools.addSeparator();
		tools.add(zoom);
		tools.add(zoomScale);
		tools.addSeparator();
		myPanel.add(tools, BorderLayout.PAGE_START);
	}

	/**
	 * Creates and initialize Photopia's menus and their menu items
	 */
	private void createMenuBar() 
	{
		myMenuBar.setBorderPainted(false);

		// Create File Menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenuItem newDoc = new JMenuItem("New", KeyEvent.VK_N);
		newDoc.addActionListener(this);
		fileMenu.add(newDoc);

		JMenuItem openDoc = new JMenuItem("Open", KeyEvent.VK_O);
		openDoc.addActionListener(this);
		fileMenu.add(openDoc);

		openRecDoc = new JMenu("Open Recent");
		openRecDoc.addActionListener(this);
		fileMenu.add(openRecDoc);

		JMenuItem saveDoc = new JMenuItem("Save", KeyEvent.VK_S);
		saveDoc.addActionListener(this);
		fileMenu.add(saveDoc);

		JMenuItem saveDocAs = new JMenuItem("Save As");
		saveDocAs.addActionListener(this);
		fileMenu.add(saveDocAs);

		fileMenu.add(new JSeparator());
		JMenuItem exitProg = new JMenuItem("Exit", KeyEvent.VK_X);
		exitProg.addActionListener(this);
		fileMenu.add(exitProg);

		//Create Settings Menu
		JMenu settingsMenu = new JMenu("Settings");
		JMenuItem userSettings = new JMenuItem("User Settings");
		userSettings.addActionListener(this);
		userSettings.setActionCommand("Settings");
		settingsMenu.add(userSettings);

		//Create Help Menu
		JMenu helpMenu = new JMenu("Help");
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(this);
		helpMenu.add(about);

		//Add menus to the menu bar
		myMenuBar.add(fileMenu);
		myMenuBar.add(settingsMenu);
		myMenuBar.add(helpMenu);
		this.setJMenuBar(myMenuBar);

	}

	/**
	 * Loads the image currently in the workspace for use in image manipulation
	 * @param e ActionEvent of the Object requesting the loaded image
	 * @return Returns true if there is a usable image in the workspace, else false.
	 */
	private boolean loadCurrentImage(ActionEvent e)
	{
		try
		{
			img = imgPans.get(tabbedPane.getSelectedIndex()).getImg();
			return true;
		}
		catch(Exception e1)
		{
			JOptionPane.showMessageDialog(tabbedPane, "No Images Loaded.", e.getActionCommand()+" Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	/**
	 * Performs actions related to the various buttons and menus in the program
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getActionCommand() == "Open") 
		{
			openFile();
		}
		//this is a "Quick Save", it will automatically save the current image over the file it was originally opened with
		else if(e.getActionCommand() == "Save")
		{
			try 
			{
				loadCurrentImage(e);
				ImageIO.write(img,  "JPG", new File(imgPans.get(tabbedPane.getSelectedIndex()).getPath()));		
			} 
			catch (Exception e1) 
			{
				System.err.println(imgPans.get(tabbedPane.getSelectedIndex()).getPath());
				JOptionPane.showMessageDialog(tabbedPane, e1.getMessage(), "Image Write Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		//save the current image as an image file chosen by the user
		else if(e.getActionCommand() == "Save As")
		{
			saveFile(e);
		}
		else if(e.getActionCommand() == "Blur")
		{	
			if(loadCurrentImage(e) == true)
			{
				img = op.Blur(img);
				imgPans.get(tabbedPane.getSelectedIndex()).setImage(img);
			}
		}
		else if(e.getActionCommand() == "Sharpen")
		{
			if(loadCurrentImage(e) == true)
			{
				img = op.Sharpen(img);
				imgPans.get(tabbedPane.getSelectedIndex()).setImage(img);
			}
		}
		else if(e.getActionCommand() == "Rotate Right")
		{
			if(loadCurrentImage(e) == true)
			{
				img = op.Rotate(img, 90);
				imgPans.get(tabbedPane.getSelectedIndex()).setImage(img);
				repaint();
			}
		}
		else if(e.getActionCommand() == "Rotate Left")
		{
			if(loadCurrentImage(e) == true)
			{
				img = op.Rotate(img, -90);
				imgPans.get(tabbedPane.getSelectedIndex()).setImage(img);
				repaint();
			}
		}
		else if(e.getActionCommand() == "Flip")
		{
			if(img == null)
			{
				JOptionPane.showMessageDialog(tabbedPane, "No Images Loaded.", "Flip Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			FlipDialogue flip = new FlipDialogue(tabbedPane.getSelectedIndex(), x, y);
			flip.run();
		}
		else if(e.getActionCommand() == "Zoom" && drawn == true)
		{
			if(loadCurrentImage(e) == true)
			{
				String value = ((String) zoomScale.getSelectedItem()).substring(0, ((String)zoomScale.getSelectedItem()).length() - 1);
				double zoomVal = Double.parseDouble(value)/100.0;

				imgPans.get(tabbedPane.getSelectedIndex()).setScaleFactor(zoomVal);
				repaint();
			}
			else
			{
				zoomScale.setSelectedIndex(4);
			}
		}
		else if(e.getActionCommand().contains("recentDoc"))
		{
			openFile(e.getActionCommand().substring(9, e.getActionCommand().length()));
		}
		else if(e.getActionCommand() == "Settings")
		{
			SettingsDialogue settings = new SettingsDialogue();
			settings.run();
		}
		else if(e.getActionCommand() == "Exit")
		{
			System.exit(0);
		}
		else if(e.getActionCommand() == "New")
		{
			NewDialogue newPic = new NewDialogue();
			newPic.run();
		}
		else if(e.getActionCommand() == "About")
		{
			JOptionPane.showMessageDialog(null, "Photopia developed for Com Sci 309 Fall 2011\nNot for Commercial Use.\n\nAuthors:\n" +
					"Wallace Davis\nChristopher Farrington\nZachary Lensing\nAaron Nicoletto", "About", JOptionPane.INFORMATION_MESSAGE);
		}
		
		/*
		 * these conditionals will set the static variable drawState to indicate which button is currently selected
		 * this is then used by the motionlistener to perform draw tools and related functions
		 */
		else if(e.getActionCommand() == "brush")
		{
			if(drawState.equals("brush"))
			{
				drawState = "";
			}
			else
			{
				drawState = "brush";
			}
		}
		else if(e.getActionCommand() == "pencil")
		{
			if(drawState.equals("pencil"))
			{
				drawState = "";
			}
			else
			{
				drawState = "pencil";
			}
		}
		else if(e.getActionCommand() == "eraser")
		{
			if(drawState.equals("eraser"))
			{
				drawState = "";
			}
			else
			{
				drawState = "eraser";
			}
		}
		else if(e.getActionCommand() == "selection")
		{
			if(drawState.equals("selection"))
			{
				drawState = "";
			}
			else
			{
				drawState = "selection";
			}
		}
		else if(e.getActionCommand() == "crop")
		{
			if(drawState.equals("crop"))
			{
				drawState = "";
			}
			else
			{
				imgPans.get(tabbedPane.getSelectedIndex()).setScaleFactor(1);
				drawState = "crop";
			}
		}
		else if(e.getActionCommand() == "hand")
		{
			if(drawState.equals("hand"))
			{
				drawState = "";
			}
			else
			{
				drawState = "hand";
			}
		}
		else if(e.getActionCommand() == "eye dropper")
		{
			if(drawState.equals("eye dropper"))
			{
				drawState = "";
				eyedropper = false;
			}
			else
			{
				eyedropper = true;
				drawState = "eye dropper";
			}
		}
		else if(e.getActionCommand() == "color palette")
		{
			ImageUtilities palette = new ImageUtilities();
			palette.run();
			imgPans.get(tabbedPane.getSelectedIndex()).setColor(selectedColor);			
		}
		else if(e.getActionCommand() == "fill")
		{
			if(drawState.equals("fill"))
			{
				drawState = "";
			}
			else
			{
				drawState = "fill";
			}
		}
		else if(e.getActionCommand() == "textbox")
		{
			if(drawState.equals("textbox"))
			{
				drawState = "";
			}
			else
			{
				drawState = "textbox";
			}
		}
		else if(e.getActionCommand() == "line")
		{
			if(drawState.equals("line"))
			{
				drawState = "";
			}
			else
			{
				drawState = "line";
			}
		}
		else if(e.getActionCommand() == "rectangle")
		{
			if(drawState.equals("rectangle"))
			{
				drawState = "";
			}
			else
			{
				drawState = "rectangle";
			}
		}
		else if(e.getActionCommand() == "circle")
		{
			if(drawState.equals("circle"))
			{
				drawState = "";
			}
			else
			{
				drawState = "circle";
			}
		}
		else if(e.getActionCommand() == "triangle")
		{
			if(drawState.equals("triangle"))
			{
				drawState = "";
			}
			else
			{
				drawState = "triangle";
			}
		}
		else if(e.getActionCommand() == "brushSize")
		{
			if(picNumber >= 1 || newImageCount >= 1)
				imgPans.get(tabbedPane.getSelectedIndex()).setWidth(Integer.parseInt(brushSize.getSelectedItem().toString()));
		}
	}

	/**
	 *Opens a file selected by the User
	 */
	private void openFile() 
	{
		setPaths();
		int returnVal = openfc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) 
		{			
			File f = openfc.getSelectedFile();

			try 
			{
				img = ImageIO.read(f);
			} 
			catch (IOException e) 
			{
				JOptionPane.showMessageDialog(tabbedPane, "Error reading image.\nCheck image file type", "Image Read Error", JOptionPane.ERROR_MESSAGE);
			}
			String fname = null;
			//create a unique name for the tab
			fname = f.getName() + "(" + picNumber + ")";

			picNumber++;

			ImagePanel tmpImgPan = new ImagePanel(f);
			tmpImgPan.setImage(img);

			tmpImgPan.addMouseListener(this);
			tmpImgPan.addMouseMotionListener(this);

			tabbedPane.addTab(fname, new JScrollPane(tmpImgPan));
			tabbedPane.setTabComponentAt(tabbedPane.indexOfTab(fname), new CloseButton(tabbedPane));	
			imgPans.add(tabbedPane.indexOfTab(fname), tmpImgPan);
			imgPans.get(tabbedPane.indexOfTab(fname)).setScaleFactor(1);
			imgPans.get(tabbedPane.indexOfTab(fname)).setWidth(Integer.parseInt(brushSize.getSelectedItem().toString()));

			//Open Recent menu item size is limited to 5
			if(openRecDoc.getItemCount() >= 5)
			{
				openRecDoc.remove(0); //remove the oldest item
			}
			
			//adding the opened file to the menu
			JMenuItem recentDoc = new JMenuItem(f.getAbsolutePath());
			recentDoc.addActionListener(this);
			recentDoc.setActionCommand("recentDoc" + f.getAbsolutePath());
			openRecDoc.add(recentDoc);
			
			//Rewriting teh settings file to accommodate the changed image history
			File settings = new File("Settings.txt");
			Scanner scan = null;

			String loadpath = "";
			String savepath = "";		
			String imgExtension  = "";

			if(settings.canRead())
			{
				try 
				{
					scan = new Scanner(settings);
				} 
				catch (FileNotFoundException e) 
				{
					JOptionPane.showMessageDialog(tabbedPane, "Settings Write Error.", "Settings Write Error", JOptionPane.ERROR_MESSAGE);
				}
				
				if(scan.hasNextLine())
				{
					loadpath = scan.nextLine();
				}
				else 
				{
					loadpath = ""; //if a settings option has not been chosen, it is printed as a black line
				}
				if(scan.hasNextLine())
				{
					savepath = scan.nextLine();
				}
				else
				{
					savepath = "";
				}
				if(scan.hasNextLine())
				{
					imgExtension = scan.nextLine();
				}		
				scan.close();
			}

			
			try 
			{
				fout = new FileOutputStream("Settings.txt");
			} 
			catch (FileNotFoundException e) //if there is no saved settings, make a new file
			{
				File set = new File("Settings.txt");
				try {fout = new FileOutputStream(set);} 
				catch (FileNotFoundException e1) 
				{JOptionPane.showMessageDialog(tabbedPane, "Settings Write Error.", "Settings Write Error", JOptionPane.ERROR_MESSAGE);}
			}
			print = new PrintStream(fout);
			
			print.println(loadpath);
			print.println(savepath);
			print.println(imgExtension);
			
			//printing the history
			for(int i = 0; i < openRecDoc.getItemCount(); i++)
			{
				print.println(openRecDoc.getItem(i).getText());
			}
			
			print.close();
			try 
			{
				fout.close();
			} 
			catch (IOException e) 
			{
				System.err.println("Settings write error");
			}
			
			zoomScale.setSelectedIndex(4);  //default zoom

			imgPans.get(tabbedPane.getSelectedIndex()).setColor(selectedColor);  //currently selected color is shared between the tabs
		}
	}
	
	/**
	 * same as openFile() except the file path is provided.  See openFil() for additional comments
	 * @param path
	 * the system path of the image file
	 */
	private void openFile(String path)
	{	

		File f = new File(path);
		try 
		{
			img = ImageIO.read(f);
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(tabbedPane, "Error reading image.\nCheck image file type", "Image Read Error", JOptionPane.ERROR_MESSAGE);
		}

		String fname = null;
		fname = f.getName() + "(" + picNumber + ")";

		picNumber++;

		ImagePanel tmpImgPan = new ImagePanel(f);
		tmpImgPan.setImage(img);

		tmpImgPan.addMouseListener(this);
		tmpImgPan.addMouseMotionListener(this);

		tabbedPane.addTab(fname, new JScrollPane(tmpImgPan));
		tabbedPane.setTabComponentAt(tabbedPane.indexOfTab(fname), new CloseButton(tabbedPane));
		
		imgPans.get(tabbedPane.indexOfTab(fname)).setName(fname);
		imgPans.add(tabbedPane.indexOfTab(fname), tmpImgPan);
		imgPans.get(tabbedPane.indexOfTab(fname)).setScaleFactor(1);
		imgPans.get(tabbedPane.getSelectedIndex()).setColor(selectedColor);
		imgPans.get(tabbedPane.indexOfTab(fname)).setWidth(Integer.parseInt(brushSize.getSelectedItem().toString()));
	}

	/**
	 * opens the save file dialogue so the user can save the current image
	 * @param e
	 */
	private void saveFile(ActionEvent e)
	{
		setPaths();
		int retVal = savefc.showSaveDialog(this);

		if(retVal == JFileChooser.APPROVE_OPTION)
		{
			String suffix = savefc.getFileFilter().getDescription().substring(1);
			if(suffix.substring(0, 2).equals("ll"))
			{
				suffix = "JPG";  //default image type if none is selected
			}

			File f;
			if(savefc.getSelectedFile().getPath().toLowerCase().endsWith(suffix.toLowerCase()))
			{
				f = new File(savefc.getSelectedFile().getPath());
			}
			else
			{
				f = new File(savefc.getSelectedFile().getPath() + "." + suffix);  //if the user did not type in the file extension, add it for them
			}

			try 
			{
				tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), (f.getName() + "(" + picNumber + ")"));
				imgPans.get(tabbedPane.getSelectedIndex()).setPath(f.getPath().toString());
				picNumber++;

				loadCurrentImage(e);
				ImageIO.write(img, suffix, f);		
			} 
			catch (IOException e1) 
			{
				JOptionPane.showMessageDialog(tabbedPane, e1.getMessage(), "Write Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	
	/**
	 * this method applies the stored settings to the open and save dialogues
	 */
	private void setPaths()
	{
		File settings = new File("Settings.txt");
		Scanner scan;

		String loadpath;
		String savepath;		
		String imgExtension;

		try 
		{
			scan = new Scanner(settings);
		} 
		catch (FileNotFoundException e) 
		{
			System.err.println("No Settings Found");
			return;
		}

		
		
		if(scan.hasNextLine())
		{
			loadpath = scan.nextLine();
		}
		else 
		{
			loadpath = "";
		}
		if(scan.hasNextLine())
		{
			savepath = scan.nextLine();
		}
		else
		{
			savepath = "";
		}
		if(scan.hasNextLine())
		{
			imgExtension = scan.nextLine();
		}
		else
		{
			imgExtension = "JPG";
		}

		if(imgExtension.equals("JPG"))
		{
			savefc.setFileFilter(new JPGFilter());
		}
		else if(imgExtension.equals("GIF"))
		{
			savefc.setFileFilter(new GIFFilter());
		}
		else if(imgExtension.equals("BMP"))
		{
			savefc.setFileFilter(new BMPFilter());
		}
		else if(imgExtension.equals("PNG"))
		{
			savefc.setFileFilter(new PNGFilter());
		}

		openfc.setCurrentDirectory(new File(loadpath));
		savefc.setCurrentDirectory(new File(savepath));
		
		scan.close();
	}

	/*
	 * the following mouse events are used with the drawing tools to actively creat images
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		if(eyedropper == true)
		{
			Color sample = new Color(imgPans.get(tabbedPane.getSelectedIndex()).getImg().getRGB(e.getX(), e.getY()));
			colorPreview.setBackground(sample);
			selectedColor = sample;
			eyedropper = false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{
		x = e.getXOnScreen();
		y = e.getYOnScreen();
	}

	@Override
	public void mouseExited(MouseEvent e) 
	{
		x = 0;
		y = 0;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if(eyedropper == true)
		{
			Color sample = new Color(imgPans.get(tabbedPane.getSelectedIndex()).getImg().getRGB(e.getX(), e.getY()));
			colorPreview.setBackground(sample);
			selectedColor = sample;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		eyedropper = false;
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if(eyedropper == true)
		{
			Color sample = new Color(imgPans.get(tabbedPane.getSelectedIndex()).getImg().getRGB(e.getX(), e.getY()));
			colorPreview.setBackground(sample);
			selectedColor = sample;
			
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}
}

