package org.vizweb.color;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.sikuli.core.logging.ImageExplainer;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;



public class ColorAnalyzer {
	
	private static final int COLOR_SIMILARITY_THRESHOLD = 200;
	
	static ImageExplainer logger = ImageExplainer.getExplainer(ColorAnalyzer.class);
	
	static public Map<NamedColor, Double> computeColorDistribution(BufferedImage input){	
		return computeColorDistribution(IplImage.createFrom(input), new StandardColors());
	}
	
	static private Map<NamedColor, Double> computeColorDistribution(IplImage input,
			NamedColors colors){
	
		Map<NamedColor, Double> distribution = new HashMap<NamedColor, Double>();
		
	
		int n = input.width() * input.height();
		for (NamedColor c : colors){			
			double m = 1.0f * countNumberOfPixelsOfColor(input, c)/n;	
			distribution.put(c, m);
		}
		return distribution;
	}
	
	static public IplImage computeMaskOfColor(IplImage input, Color color){
		IplImage diff = IplImage.create(cvGetSize(input),8,3);
		CvScalar scalar = cvScalar(color.getBlue(),color.getGreen(),color.getRed(),0);
		cvAbsDiffS(input, diff, scalar);

		IplImage r = IplImage.create(cvGetSize(input),8,1);
		IplImage g = IplImage.create(cvGetSize(input),8,1);
		IplImage b = IplImage.create(cvGetSize(input),8,1);
		cvSplit(diff,b,g,r,null);		
		diff.release();

		IplImage sumDiff = IplImage.create(cvGetSize(input),8,1);
		cvAdd(sumDiff, b, sumDiff, null);
		cvAdd(sumDiff, g, sumDiff, null);
		cvAdd(sumDiff, r, sumDiff, null);
		r.release();
		g.release();
		b.release();

		cvCmpS(sumDiff, COLOR_SIMILARITY_THRESHOLD, sumDiff, CV_CMP_LE);
		return sumDiff;
	}
	
	static public int countNumberOfPixelsOfColor(IplImage input, Color color){

		IplImage diff = IplImage.create(cvGetSize(input),8,3);
		CvScalar scalar = cvScalar(color.getBlue(),color.getGreen(),color.getRed(),0);
		cvAbsDiffS(input, diff, scalar);

		IplImage r = IplImage.create(cvGetSize(input),8,1);
		IplImage g = IplImage.create(cvGetSize(input),8,1);
		IplImage b = IplImage.create(cvGetSize(input),8,1);
		cvSplit(diff,b,g,r,null);		
		diff.release();

		IplImage sumDiff = IplImage.create(cvGetSize(input),8,1);
		cvAdd(sumDiff, b, sumDiff, null);
		cvAdd(sumDiff, g, sumDiff, null);
		cvAdd(sumDiff, r, sumDiff, null);
		r.release();
		g.release();
		b.release();

		cvCmpS(sumDiff, COLOR_SIMILARITY_THRESHOLD, sumDiff, CV_CMP_LE);
				
		logger.step(sumDiff, "color = " + color);		
		
		int n = cvCountNonZero(sumDiff);
		sumDiff.release();

		return n;
	}
	
	static public CvScalar computeAverageHueSaturationValue(BufferedImage input) {
		
		IplImage color = IplImage.createFrom(input);
		IplImage hsv = IplImage.createCompatible(color);
		cvCvtColor(color, hsv, CV_BGR2HSV );
		
		CvScalar returnValue = cvAvg(hsv,null);
		color.release();
		hsv.release();
		
		//CvScalar average = cvAvg(hsv,null);
		
		return returnValue;
	}
	
	static public double computeColorfulness(BufferedImage input) {
		IplImage color = IplImage.createFrom(input);
		
		IplImage r = IplImage.create(cvGetSize(color),8,1);
		IplImage g = IplImage.create(cvGetSize(color),8,1);
		IplImage b = IplImage.create(cvGetSize(color),8,1);
		
		cvSplit(color,b,g,r,null);
		color.release();
		
		int rows = input.getHeight(), cols = input.getWidth();
		CvMat rMat = new CvMat(r),
				gMat = new CvMat(g),
				bMat = new CvMat(b);
		
		CvMat alpha = CvMat.create(rows, cols),
				beta = CvMat.create(rows, cols);
		
		cvSub(rMat, gMat, alpha, null);
		
		cvAdd(rMat,gMat,beta,null);
		cvScale(beta,beta,0.5,0);
		cvSub(beta,bMat,beta,null);
		
		CvScalar alphaMean = new CvScalar(), alphaStdDev = new CvScalar(),
				betaMean = new CvScalar(), betaStdDev = new CvScalar();
		
		cvAvgSdv(alpha, alphaMean, alphaStdDev, null);
		cvAvgSdv(beta, betaMean, betaStdDev, null);
		
		double metric = Math.sqrt( alphaStdDev.getVal(0)*alphaStdDev.getVal(0) +
				        betaStdDev.getVal(0)*betaStdDev.getVal(0) ) 
				+ 0.3 * Math.sqrt( alphaMean.getVal(0)*alphaMean.getVal(0) + 
						betaMean.getVal(0)*betaMean.getVal(0) );
		
		r.release(); g.release(); b.release();
		
		return metric;
	}
	
	static public double computeColorfulness2(BufferedImage input) {
		
        IplImage color = IplImage.createFrom(input);
        
        IplImage luv = IplImage.createCompatible(color);
		cvCvtColor(color, luv, CV_BGR2HSV );
		
		IplImage l = IplImage.create(cvGetSize(color),8,1);
		IplImage u = IplImage.create(cvGetSize(color),8,1);
		IplImage v = IplImage.create(cvGetSize(color),8,1);
		
		cvSplit(luv,l,u,v,null);
		
		color.release(); luv.release(); 
		
		// compute the chroma matrix
		CvMat lMat = new CvMat(l),
				uMat = new CvMat(u),
				vMat = new CvMat(v);
		int rows = input.getHeight();
		int cols = input.getWidth();
		
		CvMat chroma = CvMat.create(rows,cols);
		CvMat temp = CvMat.create(rows,cols);
		
		cvMul(uMat, uMat, chroma, 1);
		cvMul(vMat, vMat, temp, 1);
		cvAdd(chroma,temp,chroma, null);
		
		cvPow(chroma, chroma, 0.5);
		
		CvMat saturation = CvMat.create(rows,cols);
		cvDiv(chroma, lMat, saturation, 1);
		
		CvScalar saturationMean = new CvScalar(),
				saturationStdDev = new CvScalar();
		
		cvAvgSdv(saturation, saturationMean, saturationStdDev, null);
		
		l.release(); u.release(); v.release();
		
		return saturationMean.getVal(0) + saturationStdDev.getVal(0);
	}
}