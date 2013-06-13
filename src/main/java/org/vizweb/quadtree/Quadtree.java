package org.vizweb.quadtree;

import static com.googlecode.javacv.cpp.opencv_core.cvScalarAll;
import static com.googlecode.javacv.cpp.opencv_core.cvSet;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;

import java.awt.image.BufferedImage;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Quadtree {

	QuadtreeNode root;
	
	public Quadtree(QuadtreeNode root) {
		this.root = root;
	}
	
	public IplImage createBinaryRepresentation() {
		IplImage binary = IplImage.create(cvSize(root.roi.width,root.roi.height), 8, 1);
		cvSet(binary,cvScalarAll(0),null);
		QuadtreePainter.quadTreeLeafPainter(binary, root);
		
		return binary;
	}
	
	public BufferedImage createBinaryBufferedImage() {		
		BufferedImage img = createBinaryRepresentation().getBufferedImage();
		return img;
	}
	
	public int countLeaves() {
		return countLeafNodes(root);
	}
	
	private int countLeafNodes(QuadtreeNode node) {
		if (node == null)
			return 1;

		int cnt = 0;
		
		cnt += countLeafNodes(node.q1);
		cnt += countLeafNodes(node.q2);
		cnt += countLeafNodes(node.q3);
		cnt += countLeafNodes(node.q4);
		
		return cnt;
	}
	
	public QuadtreeNode getRoot() {
		return root;
	}
}

