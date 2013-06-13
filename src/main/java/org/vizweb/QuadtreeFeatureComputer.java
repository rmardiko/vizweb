package org.vizweb;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.vizweb.quadtree.ColorEntropyDecompositionStrategy;
import org.vizweb.quadtree.IntensityEntropyDecompositionStrategy;
import org.vizweb.quadtree.QuadTreeDecomposer;
import org.vizweb.quadtree.Quadtree;
import org.vizweb.quadtree.QuadtreeFeatures;
import org.vizweb.structure.BinaryImageStructureFeatureComputer;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class QuadtreeFeatureComputer {

	static public Quadtree getQuadtreeColorEntropy(BufferedImage input) {
		QuadTreeDecomposer cDecomposer = new QuadTreeDecomposer(
				new ColorEntropyDecompositionStrategy());
		
		return cDecomposer.decompose(input);
	}
	
	static public Quadtree getQuadtreeIntensityEntropy(BufferedImage input) {
		QuadTreeDecomposer iDecomposer = new QuadTreeDecomposer(
				new IntensityEntropyDecompositionStrategy());
		
		return iDecomposer.decompose(input);
	}
	
	static public double computeHorizontalSymmetry(Quadtree root){		
		return computeSymmetryAlongAxis(root,1);
	}
	
	static public double computeVerticalSymmetry(Quadtree root){		
		return computeSymmetryAlongAxis(root,0);
	}
	
	static private double computeSymmetryAlongAxis(Quadtree root, int axis){
		IplImage binary = root.createBinaryRepresentation();
		return BinaryImageStructureFeatureComputer.computeSymmetryAlongAxis(binary,axis);
	}
	
	
	static public double computeVerticalBalance(Quadtree root){
		IplImage binary = root.createBinaryRepresentation();
		return BinaryImageStructureFeatureComputer.computeVerticalBalance(binary);
	}
	
	static public double computeHorizontalBalance(Quadtree root){
		IplImage binary = root.createBinaryRepresentation();
		return BinaryImageStructureFeatureComputer.computeHorizontalBalance(binary);
	}
	
	static public double computeEquilibrium(Quadtree qt) {
		Rectangle roi = qt.getRoot().getROI();
		Point center = new Point((int)roi.getCenterX(), (int)roi.getCenterY());
		
		return computeEquilibrium(qt, center);
	}
	
	static public double computeEquilibrium(Quadtree root, Point centerOfImage) {
		IplImage binary = root.createBinaryRepresentation();
		Point centerOfMass = BinaryImageStructureFeatureComputer.computeMassCenter(binary);
		
		double width = centerOfImage.x * 2;
		double height = centerOfImage.y * 2;
		
		double equilibrium = 1 - (
				(2*(Math.abs(centerOfMass.x-centerOfImage.x)))/width + 
				(2*(Math.abs(centerOfMass.y-centerOfImage.y)))/height
				) / 2;
		
		return equilibrium;
	}

	
	static public QuadtreeFeatures computeAllFeatures(Quadtree root) {
		QuadtreeFeatures f = new QuadtreeFeatures();
		
		f.horizontalSymmetry = computeHorizontalSymmetry(root);
		f.verticalSymmetry = computeVerticalSymmetry(root);
		f.horizontalBalance = computeHorizontalBalance(root);
		f.verticalBalance = computeVerticalBalance(root);
		f.equilibrium = computeEquilibrium(root);
		f.numLeafs = root.countLeaves();
		
		return f;
	}
}

