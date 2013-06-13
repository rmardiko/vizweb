package org.vizweb.xycut;

import static com.googlecode.javacv.cpp.opencv_core.cvAvgSdv;
import static com.googlecode.javacv.cpp.opencv_core.cvCountNonZero;
import static com.googlecode.javacv.cpp.opencv_core.cvRect;
import static com.googlecode.javacv.cpp.opencv_core.cvResetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_SHAPE_RECT;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCanny;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvDilate;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvErode;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.sikuli.core.cv.ImagePreprocessor;
import org.sikuli.core.cv.VisionUtils;
import org.sikuli.core.draw.ImageRenderer;
import org.sikuli.core.draw.PiccoloImageRenderer;
import org.sikuli.core.logging.ImageExplainer;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.googlecode.javacv.cpp.opencv_core.CvArr;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;

import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.nodes.PPath;

abstract class SeparatorExtractor {
	
	final static ImageExplainer explainer = ImageExplainer.getExplainer(SeparatorExtractor.class);
	final private BufferedImage input; 
	
	public SeparatorExtractor(BufferedImage input) {
		super();
		this.input = input;
	}
	
	public abstract List<SeparatorModel> extractFromRegion(Rectangle roi);
	
	
	protected List<SeparatorModel> createSeparatorModelsFromMasks(final Rectangle roi,
			List<Boolean> horizontalMask, List<Boolean> verticalMask) {
		
		
		final List<Segment> horizontalSpaceSegments = BooleanListSegmenter.segment(horizontalMask);
		final List<Segment> verticalSpaceSegments = BooleanListSegmenter.segment(verticalMask);
		
		ImageRenderer ir = new PiccoloImageRenderer(input){

			@Override
			protected void addContent(PLayer layer) {
				for (Segment segment : horizontalSpaceSegments){					
					PPath l = PPath.createRectangle(roi.x, roi.y + segment.position, roi.width, segment.size);
					l.setStrokePaint(Color.red);
					layer.addChild(l);
				}
				
				// draw roi
				Rectangle r = roi;
				PPath l = PPath.createRectangle(r.x,r.y,r.width,r.height);
				l.setStrokePaint(Color.green);
				l.setPaint(null);
				layer.addChild(l);
				
			}			
			
		};
		explainer.step(ir, "horizontal space segments");
		
		ir = new PiccoloImageRenderer(input){

			@Override
			protected void addContent(PLayer layer) {
				for (Segment segment : verticalSpaceSegments){					
					PPath l = PPath.createRectangle(roi.x + segment.position, roi.y, segment.size, roi.height);
					l.setStrokePaint(Color.red.darker());
					l.setPaint(Color.red);
					l.setTransparency(0.2f);
					layer.addChild(l);
				}
				
				// draw roi
				Rectangle r = roi;
				PPath l = PPath.createRectangle(r.x,r.y,r.width,r.height);
				l.setStrokePaint(Color.green);
				l.setPaint(null);
				layer.addChild(l);
			}			
			
		};
		explainer.step(ir, "vertical space segments");
		
		
		final List<SeparatorModel> separatorModels = new ArrayList<SeparatorModel>();
		for (Segment segment : verticalSpaceSegments){
			Rectangle r = new Rectangle(roi.x + segment.position, roi.y, segment.size, roi.height);
			SeparatorModel m = new SeparatorModel(r, SeparatorModel.VERTICAL_SEPERATOR);
			separatorModels.add(m);
		}
		
		for (Segment segment : horizontalSpaceSegments){			
			Rectangle r =  new Rectangle(roi.x, roi.y + segment.position, roi.width, segment.size);
			SeparatorModel m = new SeparatorModel(r, SeparatorModel.HORIZONTAL_SEPERATOR);
			separatorModels.add(m);
		}

		ir = new PiccoloImageRenderer(input){

			@Override
			protected void addContent(PLayer layer) {
				for (SeparatorModel separator : separatorModels){
					Rectangle r = separator.getBounds();
					PPath l = PPath.createRectangle(r.x,r.y,r.width,r.height);
					
					
					if (separator.isHorizontal()){
						l.setStrokePaint(Color.red.darker());
						l.setPaint(Color.red);
					}else{
						l.setStrokePaint(Color.blue.darker());
						l.setPaint(Color.blue);						
					}
					l.setTransparency(0.2f);
					layer.addChild(l);
				}
				
				// draw roi
				Rectangle r = roi;
				PPath l = PPath.createRectangle(r.x,r.y,r.width,r.height);
				l.setStrokePaint(Color.green);
				l.setPaint(null);
				layer.addChild(l);
				
			}			
			
		};
		explainer.result(ir, "separator models returned");
		return separatorModels;
	}
	
}


class SpaceSeparatorExtractor extends SeparatorExtractor {
	
	final private IplImage input;
	
	SpaceSeparatorExtractor(BufferedImage inputImage){
		super(inputImage);
		explainer.step(inputImage, "input");
		input = ImagePreprocessor.createGrayscale(inputImage);
	}
	
	public List<SeparatorModel> extractFromRegion(final Rectangle roi){
		
		Function<CvArr, Double> stdevFunc = new Function<CvArr, Double>(){

			@Override
			public Double apply(CvArr array) {
				CvScalar mean = new CvScalar();
				CvScalar stdDev = new CvScalar();
				cvAvgSdv(array, mean, stdDev, null);							
				return stdDev.getVal(0);
			}						
		};

		cvSetImageROI(input, cvRect(roi.x,roi.y,roi.width,roi.height));
		Double[] hs = VisionUtils.mapOverRows(input, Double.class, stdevFunc);
		Double[] vs = VisionUtils.mapOverCols(input, Double.class, stdevFunc);
		cvResetImageROI(input);

		Function<Double, Boolean> isCloseToZero = new Function<Double, Boolean>(){
			@Override
			public Boolean apply(Double element) {
				return element < 10;
			}

		};
		
		List<Boolean> horizontalSpaceLines = Lists.transform(Arrays.asList(hs), isCloseToZero);
		List<Boolean> verticalSpaceLines = Lists.transform(Arrays.asList(vs), isCloseToZero);
		
		final List<SeparatorModel> separatorModels = createSeparatorModelsFromMasks(roi,
				horizontalSpaceLines, verticalSpaceLines);

		return separatorModels;		
	}
}

class Segment {
	int size;
	int position;
}

class BooleanListSegmenter {
	
	static public List<Segment> segment(List<Boolean> booleans){
		List<Segment> segments = new ArrayList<Segment>();
		
		boolean previousValue = false;
		Segment currentSegment = null;
		for (int i = 0 ; i < booleans.size(); ++i){
							
			boolean currentValue = booleans.get(i);
			
			if (!previousValue && currentValue){
				
				currentSegment = new Segment();
				currentSegment.position = i;
				currentSegment.size = 1;
			}
			
			if (previousValue && currentValue){					
				currentSegment.size += 1;					
			}
			
			if (previousValue && !currentValue){					
				segments.add(currentSegment);
				currentSegment = null;
			}
			
			previousValue = currentValue;				
		}
		
		if (currentSegment != null){
			segments.add(currentSegment);
		}
		
		return segments;			
	}
	
	
	
}

class IndexedOrdering {
	static public <T> List<IndexedItem<T>> greatestOf(Iterable<T> iterable, final Comparator<T> comparator, int k){
		
		int i = 0;
		Iterator<T> it = iterable.iterator();
		List<IndexedItem<T>> indexedItems = Lists.newArrayList(); 
		while (it.hasNext()){
			T t = it.next();
			indexedItems.add(new IndexedItem<T>(i, t));
			i = i + 1;
		}
		
		Comparator<IndexedItem<T>> indexedComparator = new Comparator<IndexedItem<T>>(){
			@Override
			public int compare(IndexedItem<T> arg0, IndexedItem<T> arg1) {				
				return comparator.compare(arg0.getItem(),arg1.getItem());
			}			
		};
		return Ordering.from(indexedComparator).greatestOf(indexedItems, k);
	}	
}

class IndexedItem<T>{
	public IndexedItem(int index, T item) {
		super();
		this.index = index;
		this.item = item;
	}
	final private int index;
	final private T item;
	public int getIndex() {
		return index;
	}
	public T getItem() {
		return item;
	}
}

class LineSeparatorExtractor extends SeparatorExtractor {
	
	//private static final float LINE_PIXEL_RATIO_THRESHOLD = 0.85f;
	private static final float LINE_PIXEL_RATIO_THRESHOLD = 0.85f;
	private static final float RATIO_DECREMENT = 0.1f;
	
	private int retryCounter;

	final IplImage edgeMap;	
	LineSeparatorExtractor(BufferedImage input){
		super(input);
		explainer.step(input, "input");
		IplImage gray = ImagePreprocessor.createGrayscale(input);
		IplImage mask = IplImage.createCompatible(gray);
		cvCanny(gray,mask,0.66*50,1.33*50,3);
		IplConvKernel kernel = IplConvKernel.create(3,3,1,1,CV_SHAPE_RECT,null);
		cvDilate(mask,mask,kernel,1);
		cvErode(mask,mask,kernel,1);
		explainer.step(mask, "edge map");
		edgeMap = mask;
	}
	
	public List<SeparatorModel> tryExtractFromRegion(final Rectangle roi, final int retry) {
		CvRect cvroi = cvRect(roi.x,roi.y,roi.width,roi.height);
		cvSetImageROI(edgeMap, cvroi);
		
		
		Function<CvArr, Integer> op = new Function<CvArr, Integer>(){
			@Override
			public Integer apply(CvArr arg0) {
				return cvCountNonZero(arg0);
			}
		};
		Integer[] vs = VisionUtils.mapOverCols(edgeMap, Integer.class, op);
		Integer[] hs = VisionUtils.mapOverRows(edgeMap, Integer.class, op);
		
		cvResetImageROI(edgeMap);
		
		
		Function<Integer, Boolean> isPossibleHorizontalLine = new Function<Integer, Boolean>(){
			@Override
			public Boolean apply(Integer element) {
				return (1.0 * element / roi.width) > 
				LINE_PIXEL_RATIO_THRESHOLD - retry * RATIO_DECREMENT;
			}
		};
		
		Function<Integer, Boolean> isPossibleVerticalLine = new Function<Integer, Boolean>(){
			@Override
			public Boolean apply(Integer element) {
				return (1.0 * element / roi.height) > 
				LINE_PIXEL_RATIO_THRESHOLD - retry * RATIO_DECREMENT;
			}
		};
 
		List<Boolean> horizontalLinePositionMask = Lists.transform(Arrays.asList(hs), isPossibleHorizontalLine);
		List<Boolean> verticalLinePositionMask = Lists.transform(Arrays.asList(vs), isPossibleVerticalLine);
			
		final List<SeparatorModel> separatorModels = createSeparatorModelsFromMasks(roi,
				horizontalLinePositionMask, verticalLinePositionMask);

		
		// verify that the line responses before and after the line separator position are relatively low
		// this step is necessary for removing false positives (text that looks like lines)
		final List<SeparatorModel> verifiedSeparatorModels = Lists.newArrayList();
		
		for (SeparatorModel separatorModel : separatorModels){
			
			int beforeLineResponse;
			int afterLineResponse;
			int lineResponse;
			boolean isLineResponseLowBeforeAndAfter = false;
			
			if (separatorModel.isHorizontal()){
				
				int before   = Math.max(separatorModel.getY() - 1 - roi.y,0);
				int current  = separatorModel.getY() - roi.y;
				int after    = Math.min(separatorModel.getY() + separatorModel.getHeight() - roi.y, hs.length-1);
				
				beforeLineResponse = hs[before];
				afterLineResponse = hs[after];
				lineResponse = hs[current];
				
			}else{
				
				int before   = Math.max(separatorModel.getX() - 1 - roi.x,0);
				int current  = separatorModel.getX() - roi.x;
				int after    = Math.min(separatorModel.getX() + separatorModel.getWidth() - roi.x, vs.length-1);
				
				beforeLineResponse = vs[before];
				afterLineResponse = vs[after];
				lineResponse = vs[current];
			}
			
			isLineResponseLowBeforeAndAfter = beforeLineResponse < 0.2*lineResponse && afterLineResponse < 0.2*lineResponse;
			
			if (isLineResponseLowBeforeAndAfter){
				verifiedSeparatorModels.add(separatorModel);
			}
		}
		
		ImageRenderer ir = new PiccoloImageRenderer(edgeMap.getBufferedImage()){

			@Override
			protected void addContent(PLayer layer) {
				for (SeparatorModel separator : verifiedSeparatorModels){
					Rectangle r = separator.getBounds();
					PPath l = PPath.createRectangle(r.x,r.y,r.width,r.height);
					
					
					if (separator.isHorizontal()){
						l.setStrokePaint(Color.red.darker());
						l.setPaint(Color.red);
					}else{
						l.setStrokePaint(Color.blue.darker());
						l.setPaint(Color.blue);						
					}
					l.setTransparency(0.2f);
					layer.addChild(l);
					
				}
				
				// draw roi
				Rectangle r = roi;
				PPath l = PPath.createRectangle(r.x,r.y,r.width,r.height);
				l.setStrokePaint(Color.green);
				l.setPaint(null);
				layer.addChild(l);
			}			
			
		};
		explainer.result(ir, "verified line separator models");
		
		return verifiedSeparatorModels;
	}
	
	public List<SeparatorModel> extractFromRegion(final Rectangle roi){
		
		return tryExtractFromRegion(roi, retryCounter);		
	}

	public int getRetryCounter() {
		return retryCounter;
	}

	public void setRetryCounter(int retryCounter) {
		this.retryCounter = retryCounter;
	}
	
}
