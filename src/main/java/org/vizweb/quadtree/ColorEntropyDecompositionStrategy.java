package org.vizweb.quadtree;

import java.awt.image.BufferedImage;

import org.sikuli.core.cv.ImagePreprocessor;
import org.vizweb.structure.EntropyComputer;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ColorEntropyDecompositionStrategy implements QuadTreeDecompositionStrategy{

	@Override
	public double computeNodeFeature(IplImage image) {
		return EntropyComputer.computeColorEntropy(image);
	}

	@Override
	public boolean isNodeDecomposable(double featureValue) {
		return featureValue < 300;
	}
	
	@Override
	public IplImage preprocess(BufferedImage image) {
		return ImagePreprocessor.createHSV(image);
	}	

}
