package org.vizweb.quadtree;

import static com.googlecode.javacv.cpp.opencv_core.CV_FILLED;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_core.cvScalarAll;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

class QuadtreePainter {

	static public BufferedImage paint(BufferedImage input, QuadtreeNode node){
		IplImage canvas = IplImage.createFrom(input);
		quadTreePainter(canvas, node);
		return canvas.getBufferedImage();
	}
	
	static void quadTreePainter(IplImage canvas, QuadtreeNode node){			
		
		if (node == null)
			return;

		Rectangle r = node.roi;
		CvPoint p1 = cvPoint(r.x,r.y);
		CvPoint p2 = cvPoint(r.x+r.width,r.y+r.height);
		cvRectangle(canvas, p1, p2, cvScalar(255,255,20,20), 1, 8, 0);	
		quadTreePainter(canvas, node.q1);
		quadTreePainter(canvas, node.q2);
		quadTreePainter(canvas, node.q3);
		quadTreePainter(canvas, node.q4);		
	}
		

	static public void quadTreeLeafPainter(IplImage canvas, QuadtreeNode node){	
		if (node == null){			
			return;
		}

		Rectangle r = node.roi;
		CvPoint p1 = cvPoint(r.x,r.y);
		CvPoint p2 = cvPoint(r.x+r.width,r.y+r.height);

		if (r.width < 30 || r.height < 30){		
			cvRectangle(canvas, p1, p2, cvScalarAll(255), CV_FILLED, 8, 0);
		}

		quadTreeLeafPainter(canvas, node.q1);
		quadTreeLeafPainter(canvas, node.q2);
		quadTreeLeafPainter(canvas, node.q3);
		quadTreeLeafPainter(canvas, node.q4);		
	}
}
