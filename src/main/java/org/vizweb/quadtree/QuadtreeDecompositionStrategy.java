package org.vizweb.quadtree;

import java.awt.image.BufferedImage;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

interface QuadTreeDecompositionStrategy {	
	public IplImage preprocess(BufferedImage image);
	public double computeNodeFeature(IplImage image);
	public boolean isNodeDecomposable(double featureValue);
}
