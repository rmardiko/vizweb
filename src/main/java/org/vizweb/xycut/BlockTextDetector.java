package org.vizweb.xycut;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.vizweb.quadtree.ColorEntropyDecompositionStrategy;
import org.vizweb.quadtree.QuadTreeDecomposer;
import org.vizweb.quadtree.Quadtree;
import org.vizweb.structure.Block;
import org.vizweb.structure.BlockType;

public class BlockTextDetector {
	
	private Block block;
	private BufferedImage roi;
	private boolean detectedPositive;
	
	public BlockTextDetector(Block block) {
		this.block = block;
		this.detectedPositive = false;
	}
	
	public BlockTextDetector(Block block, BufferedImage image) {
		this.block = block;
		this.detectedPositive = false;
		this.roi = image;
	}
	
	public void detect() {
		
		// if this block is a leaf, for now we don't consider at all
		// will be removed later
		if (block.isLeaf()) {
			detectedPositive = false;
		}	
		else {
			
			Rectangle bounds = block.getBounds();
			Statistics stat = BlockAnalysis.computeStatistics(block);
			List<Block> children = new ArrayList<Block>(block.getChildren());
			//Collections.copy(children, block.getChildren());
		
			double ratio = (1.0 * bounds.height) / (1.0 * bounds.width);
			
			if (ratio < 0.5 && isReasonableToBeText(block) && stat.stdev < 0.5) {
				
				if (children.size() >= 5) {
					detectedPositive = true;
				} else if (children.size() >= 3) {
					detectedPositive = BlockAnalysis.getAverageSpacing(children) <= 25 
							&& stat.max < 5000;
				} else if (children.size() >= 2) {	
					boolean allChildrenOkay = true;
					int ii = 0;
					do {
						allChildrenOkay = allChildrenOkay && isReasonableToBeText(children.get(ii));
						ii++;
					} while (allChildrenOkay && ii < children.size());
					
					if (allChildrenOkay) {
						// Get the grandchildren
						List<Block> grandChildren = new ArrayList<Block>();
						for (Block b : children)
							grandChildren.addAll(b.getChildren());
						if (grandChildren.size() > 1) {
							detectedPositive = BlockAnalysis.getAverageSpacing(grandChildren) <= 15;
						}
					}
				}
			}
		}
		
		// re-evaluate the detection for large blocks
		if (detectedPositive && block.getArea() > 8000 && block.getBounds().height > 50) {
			Quadtree qt = new QuadTreeDecomposer(
					new ColorEntropyDecompositionStrategy()).decompose(roi);	
			if (qt.countLeaves() > 10) detectedPositive = false;
		}
		
		// set the block property
		if (detectedPositive) block.setType(BlockType.Text);
	}
	
	private boolean isReasonableToBeText(Block node) {
		boolean isAccepted = true;
		// Put here all constraints for a block to be a text
		
		// Maximum height of a text
		isAccepted = isAccepted && node.getBounds().height <= 200;
		
		List<Block> children = node.getChildren();
		
		// Maximum size of each child
		for (int i = 0; i < children.size() && isAccepted; i++)
			isAccepted = isAccepted && children.get(i).getArea() <= 50000;
		
		List<Block> copyOfChildren = new ArrayList<Block>(children);
		Collections.copy(copyOfChildren,children);
		
		// vertical overlap between children
		Collections.sort(copyOfChildren, new Comparator<Block>(){
				@Override
				public int compare(Block arg0, Block arg1) {
					return (int) (arg0.getBounds().getMaxX() - arg1.getBounds().getMaxX());
				}
		});
		
		for (int i = 0; i < copyOfChildren.size()-1 && isAccepted; i++) {
			Block first = copyOfChildren.get(i);
			Block second = copyOfChildren.get(i+1);
			
			double  firstMinY = first.getBounds().getMinY(),
					firstMaxY = first.getBounds().getMaxY(),
					secondMinY = second.getBounds().getMinY(),
					secondMaxY = second.getBounds().getMaxY();
			
			if (first.getBounds().y < second.getBounds().y)
				isAccepted = isAccepted &&  (firstMaxY-secondMinY >= 3);
			else
				isAccepted = isAccepted &&  (secondMaxY-firstMinY >= 3);
		}
		
		return isAccepted;
	}
	
	public boolean isDetectedPositive() {
		return detectedPositive;
	}

	public void setDetectedPositive(boolean detectedPositive) {
		this.detectedPositive = detectedPositive;
	}
}
