package org.vizweb.xycut;

import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRect;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_core.cvResetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_core.cvSet;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class SeparatorModel {

	static public final int HORIZONTAL_SEPERATOR = 1;
	static public final int VERTICAL_SEPERATOR = 2;

	private int direction;
	
	@SuppressWarnings("unused")
	private int position;
	
	private Rectangle bounds;
	public int getHeight() {
		return (int) bounds.getHeight();
	}

	public Point getLocation() {
		return bounds.getLocation();
	}

	public int getWidth() {
		return (int) bounds.getWidth();
	}

	public int getX() {
		return (int) bounds.getX();
	}

	public int getY() {
		return (int) bounds.getY();
	}

	public Rectangle roi;
	private Color color = Color.green;

	public SeparatorModel(int position, int direction, Rectangle roi){
		this.direction = direction;
		this.setPosition(position);
		this.roi = roi;
	}
	
	public SeparatorModel(Rectangle bounds, int direction){
		this.bounds = bounds;
		this.direction = direction;
	}
	
	// relative to (0,0) of the source image
	public Rectangle getBounds(){		
		return bounds;
	}

	public boolean isHorizontal(){
		return SeparatorModel.HORIZONTAL_SEPERATOR == direction;
	}
	
	public boolean isVertical(){
		return SeparatorModel.VERTICAL_SEPERATOR == direction;
	}
	
//	public boolean isTopBorder(){
//		return (SeparatorModel.HORIZONTAL_SEPERATOR == direction && getPosition() == 0);
//	}
//	
//	public boolean isBottomBorder(){
//		return false;//(SeparatorModel.HORIZONTAL_SEPERATOR == direction && (getPosition() + getSize()) == roi.height);
//	}
//	
//	public boolean isLeftBorder(){
//		return (SeparatorModel.HORIZONTAL_SEPERATOR != direction && getPosition() == 0);
//	}
//	
//	public boolean isRightBorder(){
//		return false;//(SeparatorModel.HORIZONTAL_SEPERATOR != direction && (getPosition() + getSize()) == roi.width);
//	}
//	
//	public boolean isBorder(){
//		return isTopBorder() || isBottomBorder() || isLeftBorder() || isRightBorder();
//	}
	
	public int getLength(){
		return isHorizontal() ? bounds.width : bounds.height;	
	}
	
	public int getThickness(){
		return isHorizontal() ? bounds.height : bounds.width;	
	}
	
	public IplImage paint(IplImage input){

		CvRect cutROI = cvRect(bounds.x,bounds.y,bounds.width,bounds.height);
		
		cvSetImageROI(input, cutROI);
		cvSet(input,cvScalar(color.getBlue(),color.getGreen(),color.getRed(),0),null);
				
		CvPoint p1 = cvPoint(0,0);
		CvPoint p2 = cvPoint(cutROI.width()-1, cutROI.height()-1);
		cvRectangle(input, p1, p2, CvScalar.BLUE, 1, 8, 0);
		
		cvResetImageROI(input);		
		return input;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}

}


class GraphBuilder {
	
	static GraphBuilder rectangle(){
		return null;
	}
	
	BackgroundOption background(){
		return null;
	}
	
	void transparency(){
		
	}
	
	static class BackgroundOption{
		GraphBuilder red(){
			return null;
		}
		GraphBuilder blue(){
			return null;				
		}
	};
	
	
}

class Example{
	
	void useGraphBuilder(){
		GraphBuilder.rectangle().background().blue();
	}
}




