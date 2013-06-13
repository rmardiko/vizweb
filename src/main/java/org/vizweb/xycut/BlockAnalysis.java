package org.vizweb.xycut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.vizweb.structure.Block;

public class BlockAnalysis
{	
	public static Statistics computeStatistics(Block node) {
		Statistics result = new Statistics();
		
		double _mean = 0;
		double _stdev = 0;
		int _min = Integer.MAX_VALUE; 
		int _max = Integer.MIN_VALUE;
		
		List<Block> data = node.getChildren();
		
		double[] normalized = new double[data.size()];
		double denom = node.getArea();
		
		// normalized the area, dividing by the parent block area
		for (int ii = 0; ii < data.size(); ii++)
			normalized[ii] = (double)data.get(ii).getArea() / denom;
		
		double sum = 0, 
				maxValue = Double.MIN_VALUE,
				minValue = Double.MAX_VALUE;
		for (double area : normalized) {	
			sum += area;
			if (area < minValue) minValue = area;
			if (area > maxValue) maxValue = area;
		}
		
		_mean = sum / data.size();
		_max = (int) maxValue;
		_min = (int) minValue;
		
		sum = 0;
		
		for (double area : normalized) {
			double temp = area - _mean;
			sum += temp * temp;
		}
		
		_stdev = Math.sqrt(sum / data.size());
		
		result.max = _max;
		result.min = _min;
		result.mean = _mean;
		result.stdev = _stdev;
		
		return result;
	}
	
	public static double getAverageSpacing(List<Block> input) {
		List<Block> blocks = new ArrayList<Block>(input);
		Collections.sort(blocks, new Comparator<Block>(){
			@Override
			public int compare(Block arg0, Block arg1) {
				return (int) (arg0.getBounds().getMaxX() - arg1.getBounds().getMaxX());
			}
		});
		
		double sumSpace = 0, avgSpace = 0;
		for (int i = 0; i < blocks.size() - 1; i++) {
			sumSpace += blocks.get(i+1).getBounds().getMinX() - 
					blocks.get(i).getBounds().getMaxX();
		}
		
		avgSpace = sumSpace / (blocks.size() - 1);
		
		return Math.max(0, avgSpace);
	}
}

class Statistics {
	double mean;
	double stdev;
	int min;
	int max;
}
