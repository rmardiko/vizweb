package org.vizweb.xycut;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.vizweb.structure.Block;

public class XYTextDetector {
	
	private BufferedImage image;
	private Block root;
	
	public XYTextDetector(Block rootNode, BufferedImage inputImage) {
		root = rootNode;
		image = inputImage;
	}
	
	public Block detect() {
		
		recursiveDetect(root, image);
		
		return root;
	}
	
	private void recursiveDetect(Block node, BufferedImage blockImage) {
		BlockTextDetector d = new BlockTextDetector(node, blockImage);
		d.detect();
		
		for (Block b : node.getChildren()) {
			Rectangle roi = b.getBounds();
			BufferedImage crop = image.getSubimage(roi.x, roi.y, roi.width, roi.height);
			
			recursiveDetect(b, crop);
		}
	}
}

// not used yet
class XYTextDetectionStrategy {
	
	boolean acceptLeafAsPossibleText;
	boolean removeChildrenOfTextBlock;
	boolean allowColorAnalysis;
	int minimumHeight;
	int maximumHeight;
	int minimumWidthToHeightRatio;
	int maxAreaStdDevAmongChildren;
	
}
