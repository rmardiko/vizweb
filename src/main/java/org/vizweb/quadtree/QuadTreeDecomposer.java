/*******************************************************************************
 * Copyright 2011 sikuli.org
 * Released under the MIT license.
 * 
 * Contributors:
 *     Tom Yeh - initial API and implementation
 ******************************************************************************/
package org.vizweb.quadtree;

import static com.googlecode.javacv.cpp.opencv_core.*;
import com.google.common.base.Function;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class QuadTreeDecomposer {

	final private QuadTreeDecompositionStrategy strategy; 

	public QuadTreeDecomposer(QuadTreeDecompositionStrategy strategy) {
		super();
		this.strategy = strategy;
	}
	
	// temporary (will be deleted later)
	public QuadTreeDecomposer() {
		super();
		this.strategy = new IntensityEntropyDecompositionStrategy();
	}
	
	public static QuadTreeDecomposer newColorDecomposer() {
		QuadTreeDecomposer d = new QuadTreeDecomposer(
				new ColorEntropyDecompositionStrategy());
		
		return d;
	}
	
	public static QuadTreeDecomposer newIntensityDecomposer() {
		QuadTreeDecomposer d = new QuadTreeDecomposer(
				new IntensityEntropyDecompositionStrategy());
		
		return d;
	}

	public Quadtree decompose(final BufferedImage input){
		IplImage preprocessed_image = strategy.preprocess(input);
		QuadtreeNode root = decompose(preprocessed_image, new Rectangle(0,0,
				preprocessed_image.width(),
				preprocessed_image.height()));
		
		return new Quadtree(root);
	}

	public QuadtreeNode decompose(final IplImage hsv, Rectangle roi){

		final Function<Rectangle, QuadtreeNode> decomposeHelper = 
			new Function<Rectangle, QuadtreeNode>(){

			@Override
			public QuadtreeNode apply(Rectangle roi) {

				if (roi.width < 10 && roi.height < 10){
					return null;
				}

				cvSetImageROI(hsv, cvRect(roi.x,roi.y,roi.width,roi.height));
				double feature = strategy.computeNodeFeature(hsv);
				cvResetImageROI(hsv);

				// force split when the image is large (width > 500)
				if (roi.width > 500 || strategy.isNodeDecomposable(feature)){						

					//System.out.println("split");
					Rectangle q1roi = new Rectangle(roi.x + roi.width/2, roi.y, roi.width/2, roi.height/2);
					Rectangle q2roi = new Rectangle(roi.x, roi.y, roi.width/2, roi.height/2);
					Rectangle q3roi = new Rectangle(roi.x, roi.y + roi.height/2, roi.width/2, roi.height/2);
					Rectangle q4roi = new Rectangle(roi.x + roi.width/2, roi.y + roi.height/2, roi.width/2, roi.height/2);

					QuadtreeNode node = new QuadtreeNode();
					node.roi = roi;
					node.q1 = apply(q1roi); 
					node.q2 = apply(q2roi);
					node.q3 = apply(q3roi);
					node.q4 = apply(q4roi);
					
					return node; 
				} else {
					return null;
				}		
			}
		};

		return decomposeHelper.apply(roi);
	}
}

