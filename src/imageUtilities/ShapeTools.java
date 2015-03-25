package imageUtilities;

import java.util.*;
import java.awt.*;


//this implementation takes in a list of "Points" and a Graphics instance to create the desired shape listed below
//The list of points should come from a mouse listeners

public abstract class ShapeTools
{
	public abstract void draw(java.util.List list, Graphics g);

}

class Rectangle extends ShapeTools
{
	Point sPoint = null;
	Point ePoint = null;
	public  void draw(java.util.List list, Graphics g)
	{
		Iterator iter = list.iterator();
		if(list.size()<2)
		{
			return;
		}
	sPoint = (Point)iter.next();
	ePoint = (Point)iter.next();
	if(sPoint == null || ePoint == null)
	{
		return;
	}
	else
	{
		g.fillRect((int)sPoint.getX(),(int)sPoint.getY(), (int)(ePoint.getX()- 

sPoint.getX()), (int)(ePoint.getY()- sPoint.getY()));
	}
	list.clear();
	}
}



class Oval extends ShapeTools
{
	Point sPoint = null;
	Point ePoint = null;
	public  void draw(java.util.List list, Graphics g)
	{
		Iterator iter = list.iterator();
		if(list.size()<2)
		{
			return;
		}
	sPoint = (Point)iter.next();
	ePoint = (Point)iter.next();
	if(sPoint == null || ePoint == null)
	{
		return;
	}
	else
	{
		g.fillOval((int)sPoint.getX(),(int)sPoint.getY(), (int)(ePoint.getX()- 

sPoint.getX()), (int)(ePoint.getY()- sPoint.getY()));
	}
	list.clear();
	}

}



class Line extends ShapeTools
{
	Point sPoint = null;
	Point ePoint = null;
	public  void draw(java.util.List list, Graphics g)
	{
		Iterator iter = list.iterator();
		if(list.size()<2)
		{
			return;
		}
	sPoint = (Point)iter.next();
	ePoint = (Point)iter.next();
	if(sPoint == null || ePoint == null)
	{
		return;
	}
	else
	{
		g.fillOval((int)sPoint.getX(),(int)sPoint.getY(), (int)ePoint.getX(), 

(int)ePoint.getY());
	}
	list.clear();
	}

}


class Triangle extends ShapeTools
{
	public void draw(java.util.List list, Graphics g)
	{
		Point point = null;
		Iterator iter = list.iterator();
		if(list.size()<3)
		{
			return;
		}
		Polygon p = new Polygon();
		for(int i=0;i<3;i++)
		{
			point = (Point)iter.next();
			p.addPoint ((int)point.getX(), (int)point.getY());
		}

		g.fillPolygon(p);
		list.clear();
	}
}
