package displayRunner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class ImagePanel extends JComponent implements MouseMotionListener
{
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	Graphics2D graphics2D;
	private String imgPath;
	private double scaleFactor;
	int maxUnitIncrement = 100;
	
	int startx, starty, endx, endy;
	int newW, newH;
	int[] trianglePoints;
	int pointCount;
	int brushSize;

	int left, right, top, bottom;
	
	Queue<Point> todo = new LinkedList<Point>();
	Point currentNode = new Point(0,0);
	
	public ImagePanel()
	{
		setDoubleBuffered(false);
		startx = 0; starty = 0; endx = 0; endy = 0; brushSize = 20;
		trianglePoints = new int[6];
		pointCount = 0;
		
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e)
			{
				if(!(DisplayRunner.drawState.equals("triangle")))
				{
					clearPoints();
				}
				if(DisplayRunner.drawState.equals("crop"))
				{
					setScaleFactor(1);
					left = e.getX();
					right = e.getX();
					top = e.getY();
					bottom = e.getY();
				}

				startx = e.getX();
				starty = e.getY();
			}
			public void mouseReleased(MouseEvent e)
			{
				endx = e.getX();
				endy = e.getY();
				
				if(DisplayRunner.drawState.equals("line"))
				{
					drawLine();
				}
				if(DisplayRunner.drawState.equals("rectangle"))
				{
					drawSquare();
				}
				if(DisplayRunner.drawState.equals("circle"))
				{
					drawCircle();
				}
				if(DisplayRunner.drawState.equals("fill"))
				{
					fill();
				}
				if(DisplayRunner.drawState.equals("crop"))
				{
					cropImage();
					DisplayRunner.drawState = "";
				}
			}
			public void mouseClicked(MouseEvent e)
			{
				if(DisplayRunner.drawState.equals("triangle"))
				{
					if(pointCount == 0)
					{
						trianglePoints[0] = e.getX();
						trianglePoints[1] = e.getY();
						pointCount++;
					}
					else if(pointCount == 1)
					{
						trianglePoints[2] = e.getX();
						trianglePoints[3] = e.getY();
						pointCount++;
					}
					else if(pointCount ==2)
					{
						trianglePoints[4] = e.getX();
						trianglePoints[5] = e.getY();
						pointCount = 0;
						drawTriangle();
					}
				}
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){
				if(DisplayRunner.drawState.equals("pencil"))
				{
					drawPencil(e);
				}
				else if(DisplayRunner.drawState.equals("eraser"))
				{
					erase(e);
				}
				else if(DisplayRunner.drawState.equals("brush"))
				{
					brush(e);
				}
			}
		});
		
		setImage(new BufferedImage(0,0,0));
		graphics2D = (Graphics2D) image.getGraphics();
		setAutoscrolls(true);
		addMouseMotionListener(this);
		scaleFactor = 1;
	}
	
	public ImagePanel(File f)
	{
		setDoubleBuffered(false);
		startx = 0; starty = 0; endx = 0; endy = 0; brushSize = 20;
		trianglePoints = new int[6];
		pointCount = 0;
		
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e)
			{
				if(!(DisplayRunner.drawState.equals("triangle")))
				{
					clearPoints();
				}
				if(DisplayRunner.drawState.equals("crop"))
				{
					setScaleFactor(1);
					left = e.getX();
					right = e.getX();
					top = e.getY();
					bottom = e.getY();
				}

				startx = e.getX();
				starty = e.getY();
			}
			public void mouseReleased(MouseEvent e)
			{
				endx = e.getX();
				endy = e.getY();
				
				if(DisplayRunner.drawState.equals("line"))
				{
					drawLine();
				}
				if(DisplayRunner.drawState.equals("rectangle"))
				{
					drawSquare();
				}
				if(DisplayRunner.drawState.equals("circle"))
				{
					drawCircle();
				}
				if(DisplayRunner.drawState.equals("fill"))
				{
					fill();
				}
				if(DisplayRunner.drawState.equals("crop"))
				{
					cropImage();
					DisplayRunner.drawState = "";
				}
			}
			public void mouseClicked(MouseEvent e)
			{
				if(DisplayRunner.drawState.equals("triangle"))
				{
					if(pointCount == 0)
					{
						trianglePoints[0] = e.getX();
						trianglePoints[1] = e.getY();
						pointCount++;
					}
					else if(pointCount == 1)
					{
						trianglePoints[2] = e.getX();
						trianglePoints[3] = e.getY();
						pointCount++;
					}
					else if(pointCount ==2)
					{
						trianglePoints[4] = e.getX();
						trianglePoints[5] = e.getY();
						pointCount = 0;
						drawTriangle();
					}
				}
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){
				if(DisplayRunner.drawState.equals("pencil"))
				{
					drawPencil(e);
				}
				else if(DisplayRunner.drawState.equals("eraser"))
				{
					erase(e);
				}
				else if(DisplayRunner.drawState.equals("brush"))
				{
					brush(e);
				}
			}
		});
		
		try 
		{
			image = ImageIO.read(f);
			imgPath = f.getPath().toString();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
		this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		this.setSize(new Dimension(image.getWidth(), image.getHeight()));
		this.setBackground(Color.DARK_GRAY);
		
		setAutoscrolls(true);
		addMouseMotionListener(this);
	}
	
	public ImagePanel(BufferedImage i)
	{
		setDoubleBuffered(false);
		startx = 0; starty = 0; endx = 0; endy = 0; brushSize = 20;
		trianglePoints = new int[6];
		pointCount = 0;
		
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e)
			{
				if(!(DisplayRunner.drawState.equals("triangle")))
				{
					clearPoints();
				}
				if(DisplayRunner.drawState.equals("crop"))
				{
					setScaleFactor(1);
					left = e.getX();
					right = e.getX();
					top = e.getY();
					bottom = e.getY();
				}

				startx = e.getX();
				starty = e.getY();
			}
			public void mouseReleased(MouseEvent e)
			{
				endx = e.getX();
				endy = e.getY();
				
				if(DisplayRunner.drawState.equals("line"))
				{
					drawLine();
				}
				if(DisplayRunner.drawState.equals("rectangle"))
				{
					drawSquare();
				}
				if(DisplayRunner.drawState.equals("circle"))
				{
					drawCircle();
				}
				if(DisplayRunner.drawState.equals("fill"))
				{
					fill();
				}
				if(DisplayRunner.drawState.equals("crop"))
				{
					cropImage();
					DisplayRunner.drawState = "";
				}
			}
			public void mouseClicked(MouseEvent e)
			{
				if(DisplayRunner.drawState.equals("triangle"))
				{
					if(pointCount == 0)
					{
						trianglePoints[0] = e.getX();
						trianglePoints[1] = e.getY();
						pointCount++;
					}
					else if(pointCount == 1)
					{
						trianglePoints[2] = e.getX();
						trianglePoints[3] = e.getY();
						pointCount++;
					}
					else if(pointCount ==2)
					{
						trianglePoints[4] = e.getX();
						trianglePoints[5] = e.getY();
						pointCount = 0;
						drawTriangle();
					}
				}
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){
				if(DisplayRunner.drawState.equals("pencil"))
				{
					drawPencil(e);
				}
				else if(DisplayRunner.drawState.equals("eraser"))
				{
					erase(e);
				}
				else if(DisplayRunner.drawState.equals("brush"))
				{
					brush(e);
				}
			}
		});
		
		image = i;
		setAutoscrolls(true);
		addMouseMotionListener(this);
	}
	
	private void erase(MouseEvent e)
	{
		endx = e.getX();
		endy = e.getY();
		if(graphics2D != null)
		{
			graphics2D.setColor(Color.WHITE);
		
			for(int i = 0; i < brushSize; i++)
			{
				widthDraw(i);	
			}
		}
		repaint();
		
		startx = endx;
		starty = endy;
		
		graphics2D.setColor(DisplayRunner.colorPreview.getBackground());
	}
	
	private void brush(MouseEvent e)
	{
		graphics2D.setColor(DisplayRunner.colorPreview.getBackground());
		
		endx = e.getX();
		endy = e.getY();
		
		if(graphics2D != null)
		{
			for(int i = 0; i < brushSize; i++)
			{
				widthDraw(i);
			}
		}
		repaint();
		
		startx = endx;
		starty = endy;
	}
	
	private void widthDraw(int i)
	{
		graphics2D.drawLine(startx, starty, endx, endy);
		
		graphics2D.drawLine(startx, starty, endx, endy + i);
		graphics2D.drawLine(startx, starty, endx + i, endy);
		graphics2D.drawLine(startx, starty, endx + i, endy + i);
		graphics2D.drawLine(startx, starty + i, endx, endy);
		graphics2D.drawLine(startx, starty + i, endx, endy + i);
		graphics2D.drawLine(startx, starty + i, endx + i, endy);
		graphics2D.drawLine(startx, starty + i, endx + i, endy + i);
		graphics2D.drawLine(startx + i, starty, endx, endy);
		graphics2D.drawLine(startx + i, starty, endx, endy + i);
		graphics2D.drawLine(startx + i, starty, endx + i, endy);
		graphics2D.drawLine(startx + i, starty, endx + i, endy + i);
		graphics2D.drawLine(startx + i, starty + i, endx, endy);
		graphics2D.drawLine(startx + i, starty + i, endx, endy + i);
		graphics2D.drawLine(startx + i, starty + i, endx + i, endy);
		graphics2D.drawLine(startx + i, starty + i, endx + i, endy + i);
		
		graphics2D.drawLine(startx, starty, endx, endy - i);
		graphics2D.drawLine(startx, starty, endx - i, endy);
		graphics2D.drawLine(startx, starty, endx - i, endy - i);
		graphics2D.drawLine(startx, starty - i, endx, endy);
		graphics2D.drawLine(startx, starty - i, endx, endy - i);
		graphics2D.drawLine(startx, starty - i, endx - i, endy);
		graphics2D.drawLine(startx, starty - i, endx - i, endy - i);
		graphics2D.drawLine(startx - i, starty, endx, endy);
		graphics2D.drawLine(startx - i, starty, endx, endy - i);
		graphics2D.drawLine(startx - i, starty, endx - i, endy);
		graphics2D.drawLine(startx - i, starty, endx - i, endy - i);
		graphics2D.drawLine(startx - i, starty - i, endx, endy);
		graphics2D.drawLine(startx - i, starty - i, endx, endy - i);
		graphics2D.drawLine(startx - i, starty - i, endx - i, endy);
		graphics2D.drawLine(startx - i, starty - i, endx - i, endy - i);
		
		graphics2D.drawLine(startx + i, starty + i, endx + i, endy - i);
		graphics2D.drawLine(startx + i, starty + i, endx - i, endy + i);
		graphics2D.drawLine(startx + i, starty + i, endx - i, endy - i);
		graphics2D.drawLine(startx + i, starty - i, endx + i, endy + i);
		graphics2D.drawLine(startx + i, starty - i, endx + i, endy - i);
		graphics2D.drawLine(startx + i, starty - i, endx - i, endy + i);
		graphics2D.drawLine(startx + i, starty - i, endx - i, endy - i);
		graphics2D.drawLine(startx - i, starty + i, endx + i, endy + i);
		graphics2D.drawLine(startx - i, starty + i, endx + i, endy - i);
		graphics2D.drawLine(startx - i, starty + i, endx - i, endy + i);
		graphics2D.drawLine(startx - i, starty + i, endx - i, endy - i);
		graphics2D.drawLine(startx - i, starty - i, endx + i, endy + i);
		graphics2D.drawLine(startx - i, starty - i, endx + i, endy - i);
		graphics2D.drawLine(startx - i, starty - i, endx - i, endy + i);
		graphics2D.drawLine(startx - i, starty - i, endx - i, endy - i);
		
		graphics2D.drawLine(startx - i, starty - i, endx - i, endy + i);
		graphics2D.drawLine(startx - i, starty - i, endx + i, endy - i);
		graphics2D.drawLine(startx - i, starty - i, endx + i, endy + i);
		graphics2D.drawLine(startx - i, starty + i, endx - i, endy - i);
		graphics2D.drawLine(startx - i, starty + i, endx - i, endy + i);
		graphics2D.drawLine(startx - i, starty + i, endx + i, endy - i);
		graphics2D.drawLine(startx - i, starty + i, endx + i, endy + i);
		graphics2D.drawLine(startx + i, starty - i, endx - i, endy - i);
		graphics2D.drawLine(startx + i, starty - i, endx - i, endy + i);
		graphics2D.drawLine(startx + i, starty - i, endx + i, endy - i);
		graphics2D.drawLine(startx + i, starty - i, endx + i, endy + i);
		graphics2D.drawLine(startx + i, starty + i, endx - i, endy - i);
		graphics2D.drawLine(startx + i, starty + i, endx - i, endy + i);
		graphics2D.drawLine(startx + i, starty + i, endx + i, endy - i);
		graphics2D.drawLine(startx + i, starty + i, endx + i, endy + i);
		
		graphics2D.drawLine(startx + i, starty + i, endx + i, endy + i);
		graphics2D.drawLine(startx - i, starty - i, endx - i, endy - i);
	}
	
	private void fill()
	{
		if(graphics2D != null)
		{
			Color target = new Color(getImg().getRGB(startx, starty));
			Color replacement = DisplayRunner.colorPreview.getBackground();
			if(target.getRGB() == replacement.getRGB()){
				todo.clear();
			}
			else{
				todo.clear();
				floodfill(startx,starty,target,replacement);
				todo.clear(); 	
				repaint();
			}
		}
	}
	
	private void floodfill(int x, int y, Color target, Color replacement){
	if(x<0 || y<0){
		return;
	}
	else if(x > getImg().getWidth()-1 || y>getImg().getHeight()-1){
		return;
	}
	todo.clear();
	currentNode.move(x,y);
	int wx;
	int wy;
	int ex;
	int ey;
	int mx;
	int my;
	int currentRGBl1;
	int currentRGBl2;
	int currentRGBr1;
	int currentRGBr2;
	
	Color node = new Color(getImg().getRGB(x, y));
	if(node.getRGB() != target.getRGB()){
		todo.clear();
		return;
	}
	else
	{
		todo.offer(currentNode);
	}
		while(todo.peek() != null)
		{
			if(getImg().getRGB(todo.peek().x,todo.peek().y) == target.getRGB())
			{
				
				wx= todo.peek().x-1;
				wy = todo.peek().y;
				ex = todo.peek().x+1;
				ey = todo.peek().y;
				mx = todo.peek().x;
				my = todo.peek().y;
				
				graphics2D.setColor(replacement);
				graphics2D.drawLine(mx,my,mx,my);
				repaint();
				
				if(wx+1 > getImg().getWidth()-1 || wx-1 < 0 || ex+1 > getImg().getWidth()-1 || ex-1 < 0){
					todo.poll();
				}
				else{
				while(getImg().getRGB(wx,wy) == target.getRGB() ){
					
					
						graphics2D.setColor(replacement);
						graphics2D.drawLine(wx,wy,wx,wy);
						repaint();
						if(wy+1 > getImg().getHeight()-1 || wy-1 < 0){
							
						}
						else{
							
						
							currentRGBl1 = getImg().getRGB(wx,wy+1);
							if(currentRGBl1 == target.getRGB())
							{
								currentNode = new Point(wx,wy+1);
								todo.offer(currentNode);
							}
							currentRGBl2 = getImg().getRGB(wx,wy-1);
							if(currentRGBl2 == target.getRGB())
							{
								currentNode = new Point(wx,wy-1);
								todo.offer(currentNode);
							}
						}
						wx = wx-1;
						if(wx<0 || wy<0){
							break;
						}
						else if(wx > getImg().getWidth()-1 || wy>getImg().getHeight()-1){
							break;
						}
						
					}
				
				while(getImg().getRGB(ex,ey) == target.getRGB() ){
					
									
						graphics2D.setColor(replacement);
						graphics2D.drawLine(ex,ey,ex,ey);
						repaint();
						
						if(ey+1 > getImg().getHeight()-1 || ey-1 < 0){
			
						}
						else{
							currentRGBr1 = getImg().getRGB(ex,ey+1);
							if(currentRGBr1 == target.getRGB())
							{
								currentNode = new Point(ex,ey+1);
								todo.offer(currentNode);
							}
							currentRGBr2 = getImg().getRGB(ex,ey-1);
							if(currentRGBr2 == target.getRGB())
							{
								currentNode = new Point(ex,ey-1);
								todo.offer(currentNode);
							}
						}
						ex = ex+1;				
					if(ex<0 || ey<0){
						break;
					}
					else if(ex > getImg().getWidth()-1 || ey>getImg().getHeight()-1){
						break;
					}
					
				}
		}
}
		else{
			todo.poll();
		}
	}
	todo.clear();
	return;
}
	
	private void drawLine()
	{
		if(graphics2D != null)
		{
			graphics2D.setColor(DisplayRunner.colorPreview.getBackground());
			graphics2D.drawLine(startx, starty, endx, endy);
			repaint();
		}
	}
	
	private void drawSquare()
	{
		if(graphics2D != null)
		{
			graphics2D.setColor(DisplayRunner.colorPreview.getBackground());
			graphics2D.drawLine(startx, starty, startx, endy);
			graphics2D.drawLine(startx, starty, endx, starty);
			graphics2D.drawLine(endx, starty, endx, endy);
			graphics2D.drawLine(startx, endy, endx, endy);
			repaint();

		}
	}
	
	private void drawPencil(MouseEvent e)
	{
		endx = e.getX();
		endy = e.getY();
		if(graphics2D != null)
		{
			graphics2D.setColor(DisplayRunner.colorPreview.getBackground());
		
			graphics2D.drawLine(startx, starty, endx, endy);
		}
		repaint();
		
		startx = endx;
		starty = endy;
	}
	
	private void drawTriangle()
	{
		if(graphics2D != null)
		{
			graphics2D.setColor(DisplayRunner.colorPreview.getBackground());
			graphics2D.drawLine(trianglePoints[0], trianglePoints[1], trianglePoints[2], trianglePoints[3]);
			graphics2D.drawLine(trianglePoints[2], trianglePoints[3], trianglePoints[4], trianglePoints[5]);
			graphics2D.drawLine(trianglePoints[4], trianglePoints[5], trianglePoints[0], trianglePoints[1]);

			repaint();
		}
	}
	
	private void drawCircle()
	{
		if(graphics2D != null)
		{
			graphics2D.setColor(DisplayRunner.colorPreview.getBackground());
			graphics2D.drawOval(startx, starty, endx - startx, endy - starty);
		}
		
		repaint();
	}
	
	private void cropImage()
	{
		if(startx < endx)
		{
			left = startx;
			right = endx;
		}
		else
		{
			left = endx;
			right = startx;
		}
		
		if(starty < endy)
		{
			bottom = starty;
			top = endy;
		}
		else
		{
			bottom = endy;
			top = starty;
		}
		
		if(startx > 0 && endx < image.getWidth() && starty > 0 && endy < image.getHeight())
			image = image.getSubimage(left, bottom, right - left, top - bottom);
			repaint();
	}
	
	private void clearPoints()
	{
		trianglePoints = new int[6];
		pointCount = 0;
	}
	
	public void setColor(Color c)
	{
		graphics2D.setColor(c);
	}
	
	public void setWidth(int width)
	{
		brushSize = width;
	}

	public void setImage(BufferedImage img)
	{
		image = img;
		graphics2D = (Graphics2D) img.getGraphics();
		repaint();
	}
	
	public void setPath(String path)
	{
		imgPath = path;
	}
	
	public String getPath()
	{
		return imgPath;
	}
	
	public BufferedImage getImg()
	{
		return image;
	}
	
	public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
        this.repaint();
    }
 
    @Override
    public void paintComponent(Graphics g) 
    {
    	super.paintComponents(g);
    	Graphics2D g2 = (Graphics2D) g;
    	newW = (int) image.getWidth();
    	newH = (int) image.getHeight();
    	if (this.image != null) 
    	{
            newW = (int) (image.getWidth() * scaleFactor);
            newH = (int) (image.getHeight() * scaleFactor);
            this.setPreferredSize(new Dimension(newW, newH));
            this.revalidate();   
        }
    	g2.drawImage(image, 0, 0, newW, newH, null);
    }
    
	@Override
	public void mouseDragged(MouseEvent e) {	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{
		Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
		scrollRectToVisible(r);	
	}
}