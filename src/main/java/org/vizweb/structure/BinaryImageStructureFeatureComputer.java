package org.vizweb.structure;

import static com.googlecode.javacv.cpp.opencv_core.cvAbsDiff;
import static com.googlecode.javacv.cpp.opencv_core.cvAddWeighted;
import static com.googlecode.javacv.cpp.opencv_core.cvCountNonZero;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvRect;
import static com.googlecode.javacv.cpp.opencv_core.cvSet;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GRAY2RGB;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMoments;

import java.awt.Point;

import org.sikuli.core.logging.ImageExplainer;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;

public class BinaryImageStructureFeatureComputer {
	
	static public Point computeMassCenter(IplImage binary){
		CvMoments moments = new CvMoments();
		cvMoments(binary, moments, 1);
		// center of mass of foreground pixels
		int cy = (int) (moments.m01()/moments.m00());
		int cx = (int) (moments.m10()/moments.m00());
		Point massCenter = new Point(cx,cy);
		return massCenter;
	}
	
	static public double computeHorizontalBalance(IplImage binary){
		return computeHorizontalBalanceRelativeToX(binary, binary.width()/2);		
	}
	
	static public double computeVerticalBalance(IplImage binary){
		return computeVerticalBalanceRelativeToY(binary, binary.height()/2);		
	}
	
	static public double computeHorizontalBalanceRelativeToX(IplImage binary, int x){
		
		int w = binary.width();
		int h = binary.height();

		CvRect leftRect = cvRect(0,0,x,h);
		CvRect rightRect = cvRect(x+1,0,w-x,h);

		cvSetImageROI(binary, leftRect);
		int leftWeight = cvCountNonZero(binary);

		cvSetImageROI(binary, rightRect);
		int rightWeight = cvCountNonZero(binary);

		double horizontalBalance = Math.abs(1.0*leftWeight - rightWeight) / Math.max(leftWeight,rightWeight);    
		return horizontalBalance;
	}
	
	static public double computeVerticalBalanceRelativeToY(IplImage binary, int y){
		
		int w = binary.width();
		int h = binary.height();

		CvRect upperRect = cvRect(0,0,w,y);
		CvRect lowerRect = cvRect(0,y,w,h-y);

		cvSetImageROI(binary, upperRect);      
		int upperWeight = cvCountNonZero(binary);

		cvSetImageROI(binary, lowerRect);
		int lowerWeight = cvCountNonZero(binary);

		double verticalBalance = Math.abs(1.0*upperWeight - lowerWeight) / Math.max(upperWeight,lowerWeight);    
		return verticalBalance;
	}
	
	static public double computeSymmetryAlongAxis(IplImage binary, int axis){

		IplImage flipped = binary.clone();

		cvFlip(binary, flipped, axis); // flip along x-axis

		IplImage nonSymMask = IplImage.createCompatible(binary);
		cvAbsDiff(binary, flipped, nonSymMask);

		int numNonZero = cvCountNonZero(nonSymMask);
		double score = 1.0 - 1.0 * numNonZero / (binary.width()*binary.height());



		ImageExplainer vlog = ImageExplainer.getExplainer(BinaryImageStructureFeatureComputer.class);	
		vlog.result(binary,"binary representation");		
		vlog.result(nonSymMask,"non symmetry mask");

		IplImage result = IplImage.create(cvGetSize(binary), 8, 3);
		cvCvtColor(binary, result, CV_GRAY2RGB);

		IplImage greenOverlay = IplImage.create(cvGetSize(binary), 8, 3);
		cvSet(greenOverlay, CvScalar.BLACK, null);
		cvSet(greenOverlay, CvScalar.GREEN, nonSymMask);

		cvAddWeighted(result, 0.5, greenOverlay, 0.5, 0.0, result);		
		vlog.result(result,"non symmteric regions highlighted. Score = " + score);

		return score;
	}




}