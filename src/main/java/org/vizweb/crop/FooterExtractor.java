package org.vizweb.crop;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.vizweb.structure.Block;

public class FooterExtractor implements PartExtractor {

	private static int MAX_POSSIBLE_LEVEL = 10;
	private static int MAX_POSSIBLE_FOOTER_SIZE = 100000;
	
	@Override
	public BufferedImage extract(BufferedImage input, Block decomposition) {
		
		// Find the block which most likely be the title banner
		Rectangle theLocation = findLocation(decomposition);
		if (theLocation == null ||
			(double)theLocation.height/(double)theLocation.width > 0.3 ||
			theLocation.height * theLocation.width < 7000
		   ) {
			theLocation = findCut(decomposition);
		}
		
		if (theLocation != null) {
			
			// check if the location is in the half bottom of the page
			if (theLocation.y > (input.getHeight()/2))			
				return input.getSubimage(theLocation.x,
					theLocation.y, theLocation.width, 
					theLocation.height);
		}
		
		//if(theLocation != null) return

        // if not found, just return the whole image
		return input;
	}
	
	@Override
	public Rectangle findLocation(Block area)
	{
		return recursiveFindLocation(area, 0);
	}
	
	private static Rectangle recursiveFindLocation(Block roi, int level) {
		
		if (level == MAX_POSSIBLE_LEVEL)
			return null;
		
		double bottom = roi.getBounds().getMaxY();
		
		boolean found = false;
		Block c = null;
		int numChildren = roi.getChildren().size();
		
		for (int ii = 0; ii < numChildren && !found; ii++) {
			c = roi.getChild(ii);
			
			double cBottom = c.getBounds().getMaxY();
			if ((bottom - cBottom) < 20) {
				
				if (isPossibleFooter(c)) {
					found = true;
				}
			}
			
		}
		
		if (found) 
			return c.getBounds();
		else {
			// go to the next level
			Rectangle ret = null;
			for (int jj = 0; jj < numChildren && !found; jj++) {
				
				ret = recursiveFindLocation(roi.getChild(jj), level+1);
				found = (ret != null);
			}
			
			return ret;
		}
		
	}
	
	private static boolean isPossibleFooter(Block b) {
		
		// does it contain text?
		int numChildren = b.getChildren().size();
		boolean foundText = false;
		
		for (int i = 0; i < numChildren && !foundText; i++) {
			foundText = b.getChild(i).isTextBlock();
		}
		
		return foundText && (b.getArea() <= MAX_POSSIBLE_FOOTER_SIZE);
	}
	
	private static Rectangle findCut(Block root) {
		return recursiveFindCut(root, 0);
	}
	
	private static Rectangle recursiveFindCut(Block b, int level) {
		
		if (level == MAX_POSSIBLE_LEVEL || b.isLeaf())
			return null;
		
		// inside a block check if there is a child block at the bottom
		// or near the bottom
		List<Block> children = new ArrayList<Block>();
		children.addAll(b.getChildren());
		
		if (children.size() < 2) return recursiveFindCut(children.get(0), level+1);
		
		Collections.sort(children, new Comparator<Block>(){
				@Override
				public int compare(Block a, Block b) {
					return (int) -(a.getBounds().getMaxY() - b.getBounds().getMaxY());
				}
		});
		
		Rectangle lowest = children.get(0).getBounds();
		
		//System.out.println("level = " + level + ";" + "parent = " + b.getBounds() + "; lowest = " + lowest);
		
		if (b.getBounds().getMaxY()-lowest.getMaxY() > 20) {
			// go to the biggest child
			Collections.sort(children, new Comparator<Block>(){
				@Override
				public int compare(Block a, Block b) {
					return (int) -(a.getArea() - b.getArea());
				}
		    });
						
			return recursiveFindCut(children.get(0), level+1);
		}
		
		// Then find the nearest block above
		
		// The minX should be less than minX of the block and
		// the maxX should be greater than maxX of the block
		// if not satisfied, find the next nearest element
		boolean found = false;
		int upperIndex = 1;
		Rectangle upper = children.get(upperIndex).getBounds();
		
		while (!found && 
				upper.getBounds().getMaxY() > b.getBounds().getHeight()/2 &&
				upperIndex < children.size()) {
			
			upper = children.get(upperIndex).getBounds();
			//System.out.println("upper = " + upper);
			
			found = upper.getMinX() <= lowest.getMinX() &&
					upper.getMaxX() >= lowest.getMaxX();
			
			upperIndex++;
		}
		
		if (found) {
			upper = children.get(upperIndex-1).getBounds();
			return new Rectangle((int)upper.getMinX(), (int)upper.getMaxY(), 
					upper.width, (int)(lowest.getMaxY() - upper.getMaxY()));
		}
		else {
			// go to the biggest child
			Collections.sort(children, new Comparator<Block>(){
				@Override
				public int compare(Block a, Block b) {
					return (int) -(a.getArea() - b.getArea());
				}
		    });
						
			return recursiveFindCut(children.get(0), level+1);
		}
	}
}
