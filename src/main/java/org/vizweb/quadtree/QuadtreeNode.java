package org.vizweb.quadtree;

import java.awt.Rectangle;

public class QuadtreeNode {

	Rectangle roi;
	QuadtreeNode q1;
	QuadtreeNode q2;
	QuadtreeNode q3;
	QuadtreeNode q4;
	
	public Rectangle getROI() {
		return roi;
	}
}
