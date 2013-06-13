package org.vizweb.xycut;

import static com.googlecode.javacv.cpp.opencv_core.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.sikuli.core.draw.PiccoloImageRenderer;
import org.vizweb.structure.Block;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.nodes.PPath;


public class XYTreePainter {

	static public BufferedImage paint(BufferedImage input, Block node){
		IplImage canvas = IplImage.createFrom(input);
		paint(canvas, node);
		return canvas.getBufferedImage();
	}
	
	static private void paintNodeRecursively(PLayer layer, Block node){
		Rectangle r = node.getBounds();
		PPath rect = PPath.createRectangle(r.x,r.y,r.width,r.height);
		
		//Color randomColor = new Color(numGen.nextInt(256), numGen.nextInt(256), numGen.nextInt(256));					
		//rect.setStrokePaint(randomColor);	
		//if (node.isLeaf()){
		if (node.isText()){
			rect.setStrokePaint(Color.red);
			rect.setPaint(null);
		}else 
		if (node.isLeaf()){
			rect.setStrokePaint(Color.green);
			//rect.setStrokePaint(Color.red);
			rect.setPaint(Color.green);
			rect.setTransparency(0.3f);
		}else {
			rect.setStrokePaint(Color.cyan);
			rect.setPaint(null);
		}
		
		layer.addChild(rect);
		
		for (Block child : node.getChildren()){
			paintNodeRecursively(layer, child);
		}		
	}
	
	static private void paintLeafMaskRecursively(IplImage mask, Block node){
		if (node.isLeaf()){			
			Rectangle r = node.getBounds();
			CvPoint p1 = cvPoint(r.x,r.y);
			CvPoint p2 = cvPoint(r.x+r.width,r.y+r.height);
			cvRectangle(mask, p1, p2, CvScalar.WHITE, CV_FILLED, 8, 0);				
		}
		for (Block child : node.getChildren()){
			paintLeafMaskRecursively(mask, child);
		}
	}
	
	static public IplImage createLeafMask(Block root, Dimension imageSize){		
		IplImage mask = IplImage.create(cvSize(imageSize.width, imageSize.height), 8, 1);
		cvSet(mask, CvScalar.BLACK);
		paintLeafMaskRecursively(mask, root);
		return mask;
	}
	
	
	static public BufferedImage paintOnImage(BufferedImage input, final Block node){
		PiccoloImageRenderer painter = new PiccoloImageRenderer(input){
			@Override
			protected void addContent(PLayer layer) {
				paintNodeRecursively(layer, node);
			}			
		};
		return painter.render();		
	}

	static public IplImage paint(IplImage canvas, Block node){			

		if (node == null)
			return canvas;
		
		Rectangle r = node.getBounds();
		CvPoint p1 = cvPoint(r.x,r.y);
		CvPoint p2 = cvPoint(r.x+r.width,r.y+r.height);
		cvRectangle(canvas, p1, p2, cvScalar(255,255,20,20), 1, 8, 0);
				
		for (Block child : node.getChildren()){
			paint(canvas, child);
		}
		return canvas;
	}
	
}