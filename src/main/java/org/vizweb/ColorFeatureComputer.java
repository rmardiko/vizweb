package org.vizweb;

import java.awt.image.BufferedImage;
import java.util.Map;

import org.vizweb.color.ColorAnalyzer;
import org.vizweb.color.NamedColor;

public class ColorFeatureComputer {

	// Compute the color distribution
	static public Map<NamedColor, Double> computeColorDistribution(BufferedImage input) {
		return ColorAnalyzer.computeColorDistribution(input);
	}
	
	static public double[] computeAverageHSV(BufferedImage input) {
		return ColorAnalyzer.computeAverageHueSaturationValue(input);
	}
	
	// Compute the colorfulness using the first method
	static public double computeColorfulness(BufferedImage input)  {
		return ColorAnalyzer.computeColorfulness(input);
	}
	
	// The second method for computing colorfulness
	static public double computeColorfulness2(BufferedImage input) {
		return ColorAnalyzer.computeColorfulness2(input);
	}
}
