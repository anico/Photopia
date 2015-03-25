package imgFilters;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class Filters 
{	
	public BufferedImage Rotate(BufferedImage img, int deg)
	{
		Raster data = img.getData();
		WritableRaster newData = null;
		
		int width = data.getWidth();
		int height = data.getHeight();
		int[] pixel = new int[3];
		int i, j;
		
		if(deg == 180)
		{
			newData = data.createCompatibleWritableRaster(width, height);
		}
		else
		{
			newData = data.createCompatibleWritableRaster(height, width);
		}
		
		
		if(deg == 180)
		{
			for(i = 0; i < height; i++)
			{
				for(j = 0; j < width; j++)
				{
					data.getPixel((width-1 - j), (height-1 - i), pixel);
					newData.setPixel(j, i, pixel);	
				}
			}
		}
		else if(deg == 90)
		{
			int x, y;
			x = 0;
			for(i = 0; i < width; i++)
			{
				y = height - 1;
				for(j = 0; j <height; j++)
				{
					data.getPixel(x, y, pixel);
					newData.setPixel(j, i, pixel);
					y--;
				}
				x++;
			}
		}
		else
		{
			int x, y;
			x = width - 1;
			for(i = 0; i < width; i++)
			{
				y = 0;
				for(j = 0; j <height; j++)
				{
					data.getPixel(x, y, pixel);
					newData.setPixel(j, i, pixel);
					y++;
				}
				x--;
			}
		}
		BufferedImage nimg = new BufferedImage(img.getColorModel(), newData, true, null);
		return nimg;
	}

	public BufferedImage Flip(BufferedImage img, int axis)
	{
		Raster data = img.getData();
		WritableRaster newData = data.createCompatibleWritableRaster();
		
		int width = data.getWidth();
		int height = data.getHeight();
		int[] pixel = new int[3];
		int i, j;
		
		for(i = 0; i < height; i++)
		{
			for(j = 0; j < width; j++)
			{
				if(axis == 0)
				{
					data.getPixel(j, (height-1 - i), pixel);
					newData.setPixel(j, i, pixel);
				}
				else
				{
					data.getPixel(width-1 - j, i, pixel);
					newData.setPixel(j, i, pixel);
				}
			}
		}
		
		BufferedImage nimg = new BufferedImage(img.getColorModel(), newData, true, null);
		return nimg;
	}

	public BufferedImage Tint(BufferedImage img, double x, int op)
	{
		Raster data = img.getData();
		WritableRaster newData = data.createCompatibleWritableRaster();
		
		int width = data.getWidth();
		int height = data.getHeight();
		int[] pixel = new int[3];
		int i, j, a, cv;
		cv = (int) (255*x);
		if(op == 1)
			cv = cv*-1;
		
		for(i = 0; i < height; i++)
		{
			for(j = 0; j < width; j++)
			{
				data.getPixel(j, i, pixel);
				a =pixel[0] + cv;
				if(a > 255)
				{
					a = 255;
				}
				else if(a < 0)
				{
					a = 0;
				}
				pixel[0] = a;
				a = pixel[1] + cv;
				if(a > 255)
				{
					a = 255;
				}
				else if(a < 0)
				{
					a = 0;
				}
				pixel[1] = a;
				a = pixel[2] + cv;
				if(a > 255)
				{
					a = 255;
				}
				else if(a < 0)
				{
					a = 0;
				}
				pixel[2] = a;
				newData.setPixel(j, i, pixel);
			}
		}
		
		BufferedImage nimg = new BufferedImage(img.getColorModel(), newData, true, null);
		return nimg;
	}

	public BufferedImage Blur(BufferedImage img)
	{
		BufferedImage nimg = new BufferedImage(img.getColorModel(), img.getData().createCompatibleWritableRaster(), img.isAlphaPremultiplied(), null);
		
		float[] matrix = 
		{
			0.111f, 0.111f, 0.111f,
			0.111f, 0.111f, 0.111f,
			0.111f, 0.111f, 0.111f
		};
		
		BufferedImageOp op = new ConvolveOp( new Kernel(3, 3, matrix) );
		img = op.filter(img, nimg);
		
		return nimg;
	}

	public BufferedImage Sharpen(BufferedImage img)
	{
		int height = img.getHeight();
		int width = img.getWidth();
		int i, j;
		int[] pixel = new int[3];
		
		BufferedImage orig = img;
		int[] origPixel = new int[3];
		
		BufferedImage blur = new BufferedImage(img.getColorModel(), img.getData().createCompatibleWritableRaster(), img.isAlphaPremultiplied(), null);
		blur = Blur(img);
		int[] blurPixel = new int[3];
		
		Raster origData = orig.getData();
		Raster blurData = blur.getData();
		WritableRaster differenceData = origData.createCompatibleWritableRaster();
		int[] differencePixel = new int[3];
		
		for(i = 0; i < height; i++)
		{
			for(j = 0; j < width; j++)
			{
				origPixel = origData.getPixel(j, i, origPixel);
				blurPixel = blurData.getPixel(j, i, blurPixel);
				
				pixel[0] = origPixel[0] - blurPixel[0];
				if(pixel[0] < 0)
				{
					pixel[0] = 0;
				}
				
				pixel[1] = origPixel[1] - blurPixel[1];
				if(pixel[1] < 0)
				{
					pixel[1] = 0;
				}
				
				pixel[2] = origPixel[2] - blurPixel[2];
				if(pixel[2] < 0)
				{
					pixel[2] = 0;
				}
				
				differenceData.setPixel(j, i, pixel);
			}
		}
		
		
		WritableRaster nimgData = orig.getData().createCompatibleWritableRaster();
		int[] nimgPixel = new int[3];
		
		for(i = 0; i < height; i++)
		{
			for(j = 0; j < width; j++)
			{
				origPixel = origData.getPixel(j, i, origPixel);
				differencePixel = differenceData.getPixel(j, i, differencePixel);
				
				nimgPixel[0] = origPixel[0] + differencePixel[0];
				if(nimgPixel[0] > 255)
				{
					nimgPixel[0] = 255;
				}
				nimgPixel[1] = origPixel[1] + differencePixel[1];
				if(nimgPixel[1] > 255)
				{
					nimgPixel[1] = 255;
				}
				nimgPixel[2] = origPixel[2] + differencePixel[2];
				if(nimgPixel[2] > 255)
				{
					nimgPixel[2] = 255;
				}
				
				nimgData.setPixel(j, i, nimgPixel);
				
			}
		}
		
		BufferedImage nimg = new BufferedImage(orig.getColorModel(), nimgData, orig.isAlphaPremultiplied(), null);
		return nimg;	
	}
}