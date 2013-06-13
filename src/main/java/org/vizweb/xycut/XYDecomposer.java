package org.vizweb.xycut;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.sikuli.core.cv.ImagePreprocessor;
import org.sikuli.core.cv.Space;
import org.sikuli.core.draw.ImageRenderer;
import org.sikuli.core.draw.PiccoloImageRenderer;
import org.sikuli.core.logging.ImageExplainer;
import org.vizweb.structure.Block;
import org.vizweb.structure.DecompositionStrategy;
import org.vizweb.structure.ImageDecomposer;

import com.google.common.collect.Lists;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class XYDecomposer extends ImageDecomposer{


	static ImageExplainer explainer = ImageExplainer.getExplainer(XYDecomposer.class);

	static class DecomposeHelper {
		
		public DecomposeHelper(IplImage input, XYDecompositionStrategy strategy) {
			super();
			this.gray = input;
			this.strategy = strategy;
			this.lineSeparatorExtractor = new LineSeparatorExtractor(input.getBufferedImage());
			this.spaceSeparatorExtractor = new SpaceSeparatorExtractor(input.getBufferedImage());
			this.spaceAnalyzer = new Space(gray);
		}

		final IplImage gray;
		final XYDecompositionStrategy strategy;
		final SeparatorExtractor lineSeparatorExtractor;
		final SeparatorExtractor spaceSeparatorExtractor;
		final Space spaceAnalyzer;

		private List<Rectangle> splitRectangleIntoRegions(Rectangle roi, List<SeparatorModel> separators){
			List<Rectangle> childRegions = Lists.newArrayList();
			
			List<SeparatorModel> separatorsSortedByPosition = Lists.newArrayList(separators);
			Collections.sort(separatorsSortedByPosition, new Comparator<SeparatorModel>(){
				@Override
				public int compare(SeparatorModel a, SeparatorModel b) {
					return a.isHorizontal() ? b.getY() - a.getY() : b.getX() - a.getX();
				}				
			});
			
			Rectangle remainingRegionToSplit = new Rectangle(roi);
			
			Iterator<SeparatorModel> it = separatorsSortedByPosition.iterator();
			while(it.hasNext()){
				
				SeparatorModel separatorToApply = it.next();
				List<Rectangle> twoRegions = splitRectangleIntoTwoRegions(remainingRegionToSplit, separatorToApply);
				childRegions.add(twoRegions.get(1));
				
				if (it.hasNext()){
					remainingRegionToSplit = twoRegions.get(0);
				}else{
					childRegions.add(twoRegions.get(0));
				}
			}
			
			
			return childRegions;			
		}
		
		private List<Rectangle> splitRectangleIntoTwoRegions(Rectangle roi, SeparatorModel seperator){
			List<Rectangle> childRegions = new ArrayList<Rectangle>();
			if (seperator.isHorizontal()){
				// divide into top and bottom
				int y = seperator.getY();
				int s = seperator.getThickness();

				Rectangle topROI = new Rectangle(roi.x, roi.y, roi.width, y - roi.y);						
				Rectangle bottomROI = new Rectangle(roi.x, y + s, roi.width, roi.height - y + roi.y - s);

				childRegions.add(topROI);
				childRegions.add(bottomROI);
			}else{
				// divide into left and right
				int x = seperator.getX();
				int s = seperator.getThickness();

				Rectangle leftROI = new Rectangle(roi.x,      roi.y, x - roi.x,                     roi.height);
				Rectangle rightROI = new Rectangle(x + s, roi.y, roi.width - x + roi.x - s, roi.height);

				childRegions.add(leftROI);
				childRegions.add(rightROI);
			}
			return childRegions;			
		}

		private interface SeparatorExtractAndSelector {
			List<SeparatorModel> run();
		}

		private Block decomposeRecursively(final Rectangle roi, int level){
			
			Block node = new Block();
			node.setBounds(roi);
						
			if (roi.width * roi.height < strategy.getMinArea() ||
					roi.width < strategy.getMinWidth() || 
					roi.height < strategy.getMinHeight()){
				return node;
			}
			
			if (level > strategy.getMaxLevel()){
				return node;
			}
			
			
			if (strategy.isRemovingBorder()){
				
				Rectangle regionInsideMargin = spaceAnalyzer.findContentBounds(roi);
				
				if (roi.width > regionInsideMargin.width || roi.height > regionInsideMargin.height){					
					
					
					if (level == 0){
						Block regionInside = decomposeRecursively(regionInsideMargin, 1);
						node.addChild(regionInside);
						return node;						
					}else{
						Block regionInside = decomposeRecursively(regionInsideMargin, level);	
						return regionInside;
					}
				}
			}

			
			SeparatorExtractAndSelector lineSeparatorExtractAndSelector = new SeparatorExtractAndSelector(){
				@Override
				public List<SeparatorModel> run() {
					List<SeparatorModel> lineSeparators = lineSeparatorExtractor.extractFromRegion(roi);
					List<SeparatorModel> selectedSeparators = Lists.newArrayList();
					for (SeparatorModel lineSeparator : lineSeparators){
						if (lineSeparator.getLength() > 100 && roi.width > 100 && roi.height > 100)
							selectedSeparators.add(lineSeparator);
					}
					return selectedSeparators;
				}				
			};
			
			SeparatorExtractAndSelector spaceSeparatorExtractAndSelector = new SeparatorExtractAndSelector(){
				@Override
				public List<SeparatorModel> run() {
					List<SeparatorModel> spaceSeparators = spaceSeparatorExtractor.extractFromRegion(roi);
					if (spaceSeparators.isEmpty())
						return spaceSeparators;
					else{
						List<SeparatorModel> selectedSeparators = strategy.getSeparatorSelectionStrategy().select(spaceSeparators);			
						return selectedSeparators;
					}
				}				
			};
			
			
			List<SeparatorExtractAndSelector> separatorExtractAndSelectors;
			
			if (roi.width > 100 && roi.height > 100)
				separatorExtractAndSelectors = Lists.newArrayList(lineSeparatorExtractAndSelector, spaceSeparatorExtractAndSelector);
			else
				separatorExtractAndSelectors = Lists.newArrayList(spaceSeparatorExtractAndSelector);
			
			Iterator<SeparatorExtractAndSelector> iter = separatorExtractAndSelectors.iterator();
			while (iter.hasNext()){

				SeparatorExtractAndSelector separatorExtractAndSelector = iter.next();
				final List<SeparatorModel> separators = separatorExtractAndSelector.run();

				if (separators.size() > 0){
					ImageRenderer viz = new PiccoloImageRenderer(gray.getBufferedImage()){

						@Override
						protected void addContent(PLayer layer) {
							for (SeparatorModel separator : separators){
								Rectangle r = separator.getBounds();
								PPath l = PPath.createRectangle(r.x,r.y,r.width,r.height);								
								l.setStrokePaint(Color.red.darker());
								l.setPaint(Color.red);
								layer.addChild(l);
								
								// test
								layer.addChild(new PText("Text"));

								// draw roi
								r = roi;
								l = PPath.createRectangle(r.x,r.y,r.width,r.height);
								l.setStrokePaint(Color.green);
								l.setPaint(null);
								layer.addChild(l);
								
							}
						}
					};					
					explainer.step(viz, "separators (green), best separator (cyan) (level = " + level + ")");
					
					List<Rectangle> childRegions = splitRectangleIntoRegions(roi, separators);
					for (Rectangle childRegion : childRegions){
						node.addChild(decomposeRecursively(childRegion, level + 1));
					}
					
					// the return statement here implies that if we already have line
					// separator(s), we don't need to extract space separators.
					return node;
				}
			}
			
			// if no separator found and the current roi is >= 100000,
			// reduce the LINE_PIXEL_RATIO_THRESHOLD
			
			// no separator found
			return node;
		}
	}

			
	public Block decompose(final IplImage input, XYDecompositionStrategy strategy){
		DecomposeHelper helper = new DecomposeHelper(input, strategy);
		Rectangle roi = new Rectangle(0,0,input.width(),input.height());
		return helper.decomposeRecursively(roi, 0);
	}
	
	
	public Block decompose(final IplImage input, Rectangle roi){
		DecomposeHelper helper = new DecomposeHelper(input, new DefaultXYDecompositionStrategy());		
		return helper.decomposeRecursively(roi, 0);
	}


	@Override
	public Block decompose(BufferedImage image, DecompositionStrategy strategy) {
		IplImage gray = ImagePreprocessor.createGrayscale(image);
		return decompose(gray, (XYDecompositionStrategy) strategy);
	}
}


