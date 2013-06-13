package org.vizweb.crop;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.vizweb.structure.Block;

public interface PartExtractor {

	/**
	 * The extract method
	 * @param input
	 * @param xydecomposition
	 * @return BufferedImage, roi of the part
	 */
	public abstract BufferedImage extract(BufferedImage input, Block xydecomposition);
	
	/**
	 * The findLocation method searches the location of the part using
	 * a set of heuristics
	 * @param areaOfSearch is the block given by XY-cut decomposition
	 * @return
	 */
	public abstract Rectangle findLocation(Block areaOfSearch);
	
}
