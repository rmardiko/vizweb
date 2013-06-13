package org.vizweb.crop;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.vizweb.structure.Block;

public class BannerExtractor implements PartExtractor{
	
	private static int MAX_POSSIBLE_LEVEL = 5;

	public BufferedImage extract(BufferedImage input, Block decomposition)
	{
		// Find the block which most likely be the title banner
		Rectangle theLocation = findTitleBannerLocation(decomposition, 0);
		
		if (theLocation != null)
			return input.getSubimage(theLocation.x,
					theLocation.y, theLocation.width, 
					theLocation.height);
		else {
			// run the title banner cut
			Rectangle possiblyTitleBanner = findTitleBannerCut(decomposition,0);
			
			if (possiblyTitleBanner != null) {
				
				Block firstChild = decomposition.getFirstChild();
				int x = firstChild.getBounds().x;
				int width = firstChild.getBounds().width;
				int y = firstChild.getBounds().y;
				
				if (possiblyTitleBanner.height-y <= 0) return input;
				
				// else
				return input.getSubimage(x, y, width, 
						possiblyTitleBanner.height-y);
			}
			else
				return input;
		}
	}
	
	private static Rectangle findTitleBannerLocation(Block node, int level)
	{
		if (level == MAX_POSSIBLE_LEVEL)
			return null;
		
		Rectangle bound = node.getBounds();
		
		//System.out.println("Level: " + level + "; " + bound);
		
		// what are the characteristic of title banner?
		// (1) position
		// (2) size, including length and width
		boolean isTheBanner = bound.x < 160 && // some title banner go 
                                               // longer to the middle
				                bound.y < 100 &&
				                bound.width > 700 &&
				                bound.height > 80 &&
				                bound.height < 160 &&
				                bound.width * bound.height > 70000;
		
		if (isTheBanner) 
			return bound;
		else {
			int nChildren = node.getChildren().size();
			Rectangle bb = null;
			for (int ii = 0; ii < nChildren && (bb == null) ; ii++) {
				bb = findTitleBannerLocation(node.getChild(ii), level+1);
			}
			
			return bb;
		}
	}
	
	private static Rectangle findTitleBannerCut(Block node, int level)
	{
		if (level == MAX_POSSIBLE_LEVEL)
			return null;
		
		Rectangle bound = node.getBounds();
		
		// what are the characteristic of title banner?
		// (1) position
		// (2) size, including length and width
		boolean isTheBanner = bound.x < 160 && // some title banner go 
                                               // longer to the middle
				                bound.y > 90 && bound.y < 200 &&
				                bound.width > 700;
				                //bound.height > 80 &&
				                //bound.height < 160 &&
				                //bound.width * bound.height > 70000;
		
		if (isTheBanner) {
			return new Rectangle(10,0,1000,bound.y);
		}
		else {
			int nChildren = node.getChildren().size();
			Rectangle bb = null;
			for (int ii = 0; ii < nChildren && (bb == null) ; ii++) {
				bb = findTitleBannerCut(node.getChild(ii), level+1);
			}
			return bb;
		}
	}

	@Override
	public Rectangle findLocation(Block areaOfSearch) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

