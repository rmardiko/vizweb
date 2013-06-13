package org.vizweb.structure;

import static com.googlecode.javacv.cpp.opencv_core.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import org.sikuli.core.logging.ImageExplainer;
import org.vizweb.color.ColorAnalyzer;
import org.vizweb.xycut.XYTreePainter;

public class BackgroundModel {

	public BackgroundModel(Color color) {
		super();
		this.color = color;
	}

	static private final ImageExplainer explainer = ImageExplainer.getExplainer(BackgroundModel.class);
		
	
	final private Color color;
	
	// Learn a background color model for the block and all of its child blocks
	// When this method returns, every block will carry a BackgroundModel object 
	// The background model of leaf nodes are set to be the same as its parent nodes
	static public void learn(BufferedImage input, Block block){
		
		final IplImage colorInput = IplImage.createFrom(input);
			
		IplImage mask = XYTreePainter.createLeafMask(block, new Dimension(input.getWidth(), input.getHeight()));
		explainer.step(mask, "leaf nodes mask");
		
		IplImage foregroundViz = IplImage.createCompatible(colorInput);
		cvSet(foregroundViz, CvScalar.BLACK);
		cvCopy(colorInput, foregroundViz, mask);
		explainer.step(foregroundViz, "foreground (leaf nodes)");

		final IplImage backgroundMask = IplImage.createCompatible(mask);
		cvNot(mask, backgroundMask);

		IplImage backgroundViz = IplImage.createCompatible(colorInput);
		cvSet(backgroundViz, CvScalar.BLACK);
		cvCopy(colorInput, backgroundViz, backgroundMask);
		explainer.step(backgroundViz, "background");
		

		IplImage viz = IplImage.createCompatible(colorInput); 
		cvSet(viz, CvScalar.BLACK);
		
		IplImage occuMask = IplImage.createCompatible(mask); 
		cvSet(occuMask, CvScalar.WHITE);
		
		learnBackgroundModelRecursively(colorInput, occuMask, viz, block);
		
		explainer.step(viz, "background repainted with average color");
		
		
		explainer.step(backgroundMask, "background maks without leaf nodes");
		List<Block> leafNodes = block.getLeaves();
		for (Block leafNode : leafNodes){
			
			// set the leaf node's background color model to that of its ancestor's			
			Block parent = leafNode.getParent();			
			// find an ancestor who is at least 100 pixels larger than this leaf node
			while (parent.getParent() != null && parent.getArea() - leafNode.getArea() < 100){
				parent = parent.getParent();
			}
			leafNode.setBackgroundModel(parent.getBackgroundModel());
		}
		
		@SuppressWarnings("unused")
		ForegroundPixelCounter foregroundPixelCounterByBackgroundSubtraction = new ForegroundPixelCounter(){

			@Override
			public int count(Block leafNode) {
				Color bgColor = leafNode.getBackgroundModel().getColor();
				
				Rectangle r = leafNode.getBounds();
				CvRect roi = cvRect(r.x,r.y,r.width+1,r.height+1);
				cvSetImageROI(colorInput, roi);
				cvSetImageROI(backgroundMask,roi);
				
				IplImage bgMaskforLeaf = ColorAnalyzer.computeMaskOfColor(colorInput, bgColor);
				cvSet(backgroundMask, cvScalarAll(255), bgMaskforLeaf);
				
				int numBgPixels = cvCountNonZero(bgMaskforLeaf);
				int numFgPixels = leafNode.getArea() - numBgPixels;
							
				cvResetImageROI(backgroundMask);
				cvResetImageROI(colorInput);
				return numFgPixels;
			}
			
		};
		
		ForegroundPixelCounter foregroundPixelCounterByEntireLeafNode = new ForegroundPixelCounter(){
			@Override
			public int count(Block leafNode) {
				return leafNode.getArea();
			}
		};
		
		
		for (Block leafNode : leafNodes){
						
			//int numFgPixels = foregroundPixelCounterByBackgroundSubtraction.count(leafNode);
			int numFgPixels = foregroundPixelCounterByEntireLeafNode.count(leafNode);
			leafNode.setNumForegroundPixels(numFgPixels);
			

		}
		
		propagateForegroundPixelCount(block);
		
		explainer.step(backgroundMask, "background maks including leaf nodes");
	}
	
	interface ForegroundPixelCounter {
		int count(Block leafNode);
	}
	
	
	static private void propagateForegroundPixelCount(Block block){
		if (block.isLeaf())
			return;
		
		int cnt = 0;
		for (Block child : block.getChildren()){			
			propagateForegroundPixelCount(child);
			cnt += child.getNumForegroundPixels();
		}
		block.setNumForegroundPixels(cnt);
	}
	
	static private void learnBackgroundModelRecursively(IplImage input, IplImage occuMask, IplImage viz, Block block){
		
		Rectangle r = block.getBounds();
		
		for (Block child : block.getChildren()){
			learnBackgroundModelRecursively(input, occuMask, viz, child);
		}
		
		if (!block.isLeaf()){
			
			// compute average color of the background area of the block		
			CvRect roi = cvRect(r.x,r.y,r.width+1,r.height+1);
			cvSetImageROI(input, roi);
			cvSetImageROI(occuMask, roi);
			cvSetImageROI(viz, roi);

			// TODO: no need to compute average over the entire area
			// can speed up if we only sample pixels from foreground area
			// or we simply downsample both input and masks
			CvScalar avg = cvAvg(input, occuMask);
			cvAddS(viz, avg, viz, occuMask);
			cvResetImageROI(input);
			cvResetImageROI(viz);
			cvResetImageROI(occuMask);
			
			// set the background color model of this node		
			Color c = new Color((int)avg.getVal(2), (int)avg.getVal(1), (int)avg.getVal(0));
			BackgroundModel backgroundModel = new BackgroundModel(c);
			block.setBackgroundModel(backgroundModel);			

		}

		// mark this node to black (so in its parent, all white pixels are background)
		CvPoint p1 = cvPoint(r.x,r.y);
		CvPoint p2 = cvPoint(r.x+r.width,r.y+r.height);
		cvRectangle(occuMask, p1, p2, CvScalar.BLACK, CV_FILLED, 8, 0);

	}

	public Color getColor() {
		return color;
	}
	
	public String toString(){
		return String.format("(%d,%d,%d)", color.getRed(), color.getGreen(), color.getBlue());
	}

}
