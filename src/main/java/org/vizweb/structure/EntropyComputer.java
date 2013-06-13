package org.vizweb.structure;

import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvLog;
import static com.googlecode.javacv.cpp.opencv_core.cvMul;
import static com.googlecode.javacv.cpp.opencv_core.cvSplit;
import static com.googlecode.javacv.cpp.opencv_core.cvSum;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_HIST_ARRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCalcHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetMinMaxHistValue;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvNormalizeHist;

import com.googlecode.javacv.cpp.opencv_core.CvMatND;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvHistogram;

public class EntropyComputer {

	static public double computeIntensityEntropy(IplImage lab){

		int l_bins = 20;
		int hist_size[] = {l_bins};
		
		float v_ranges[] = { 0, 100 };
		float ranges[][] = { v_ranges};

		IplImage l_plane = IplImage.create( cvGetSize(lab), 8, 1 );
		IplImage s_plane = IplImage.create( cvGetSize(lab), 8, 1 );
		IplImage v_plane = IplImage.create( cvGetSize(lab), 8, 1 );
		IplImage planes[] = {l_plane};

		CvHistogram hist = CvHistogram.create(1, hist_size, CV_HIST_ARRAY, ranges, 1);

		cvSplit(lab, l_plane, s_plane, v_plane, null );
		cvCalcHist(planes, hist, 0, null);

		float[] min_value = {0};
		float[] max_value = {0};
		
		cvNormalizeHist(hist, 100.0);
		cvGetMinMaxHistValue(hist, min_value, max_value, null, null);      

		//new CvArrArray()
		//new CvArray()
		CvMatND p = hist.mat().clone();
		CvMatND s = hist.mat().clone();

		// compute s <- log(p)
		cvLog(p, s);       

		// compute s <- p * log(p) as p * s
		cvMul(p, s, s, 1.0);

		// compute sum(p * log(p)) as sum(s)
		CvScalar sum = cvSum(s); 

		return sum.val(0);
	}

	static public double computeColorEntropy(IplImage hsv){

		int h_bins = 30, s_bins = 32;
		int hist_size[] = {h_bins, s_bins};
		
		// hue varies from 0 (~0 deg red) to 180 (~360 deg red again)
		float h_ranges[] = { 0, 180 };
		
		// saturation varies from 0 (black-gray-white) to
        // 255 (pure spectrum color)
		float s_ranges[] = { 0, 255 };
		float ranges[][] = { h_ranges, s_ranges };

		IplImage h_plane = IplImage.create( cvGetSize(hsv), 8, 1 );
		IplImage s_plane = IplImage.create( cvGetSize(hsv), 8, 1 );
		IplImage v_plane = IplImage.create( cvGetSize(hsv), 8, 1 );
		IplImage planes[] = { h_plane, s_plane};

		CvHistogram hist = CvHistogram.create(2, hist_size, CV_HIST_ARRAY, ranges, 1);

		cvSplit(hsv, h_plane, s_plane, v_plane, null );
		cvCalcHist(planes, hist, 0, null);

		float[] min_value = {0};
		float[] max_value = {0};
		cvGetMinMaxHistValue(hist, min_value, max_value, null, null);

		cvNormalizeHist(hist, 100.0);
		cvGetMinMaxHistValue(hist, min_value, max_value, null, null);

		//new CvArrArray()
		//new CvArray()
		CvMatND p = hist.mat().clone();
		CvMatND s = hist.mat().clone();

		// compute s <- log(p)
		cvLog(p, s);       

		// compute s <- p * log(p) as p * s
		cvMul(p, s, s, 1.0);

		// compute sum(p * log(p)) as sum(s)
		CvScalar sum = cvSum(s); 

		return sum.val(0);

	}
}