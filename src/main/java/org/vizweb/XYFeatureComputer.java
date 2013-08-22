package org.vizweb;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.vizweb.structure.Block;
import org.vizweb.xycut.DefaultXYDecompositionStrategy;
import org.vizweb.xycut.XYDecomposer;
import org.vizweb.xycut.XYTextDetector;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class XYFeatureComputer {
	
	static public Block getXYBlockStructure(BufferedImage input) {
		XYDecomposer d = new XYDecomposer();
		Block root = d.decompose(input, new DefaultXYDecompositionStrategy());
		root.filterOutSmallBlocks();
		
		XYTextDetector td = new XYTextDetector(root,input);
		Block rootWithTextdetected = td.detect();
		
		return rootWithTextdetected;
	}
	
	/**
	 * @return the number of pixels of non text blocks
	 */
	static public int computeNonTextBlockArea(Block node) {
		// Just go to the leaves and check if the block not isText()
		if (node.isLeaf()) {
			if (!node.isText())
				return node.getArea();
			else
				return 0;
		}
		
		int area = 0;
		List<Block> children = node.getChildren();
		for (Block childNode : children) area += computeNonTextBlockArea(childNode);
		
		return area;
	}
	
	public static int countNumberOfLeaves(Block root) {
		return recursiveCountNumberOfLeaves(root);
	}
	
	static public double computePercentageOfLeafArea(Block root){						
		List<Block> allLeafNodes = getAllLeaves(root);
		
		int totalLeafArea = 0;
		for (Block leafNode : allLeafNodes){
			totalLeafArea += leafNode.getArea();
		}
		
		double totalArea = root.getArea();
		
		//if (countNumberOfNodesInLevel(root,1) == 1) {
		//	totalArea = root.getFirstChild().getArea();
		//}
		
		double percentageLeafArea = 100.0 * totalLeafArea / totalArea;
		
		return percentageLeafArea;
	}
	
	static public double computePercentageOfLeafArea2(Block root){						
		List<Block> allLeafNodes = getAllLeaves(root);
		
		int totalLeafArea = 0;
		for (Block leafNode : allLeafNodes){
			totalLeafArea += leafNode.getArea();
		}
		
		double totalArea = root.getArea();
		
		if (countNumberOfNodesInLevel(root,1) == 1) {
			totalArea = root.getFirstChild().getArea();
		}
		
		double percentageLeafArea = 100.0 * totalLeafArea / totalArea;
		
		return percentageLeafArea;
	}
	
	private static int recursiveCountNumberOfLeaves(Block node) {
		if (node.isLeaf())
			return 1;
		
		// else
		int count = 0;
		for (Block c: node.getChildren())
			count += recursiveCountNumberOfLeaves(c);
		
		return count;
	}
	
    public static double computeAverageDecompositionLevel(Block root) {
		
		List<Block> leafNodes = getAllLeaves(root);
		
		List<Integer> leafDepths = Lists.transform(leafNodes, new Function<Block, Integer>() {
			@Override
			public Integer apply(Block node) {
				return computeNodeDepth(node);
			}	
		});
		
		double sum = 0, n = leafDepths.size();
		for(int i : leafDepths) sum += i;
		
		return sum/n;
	}

    private static int computeNodeDepth(Block node) {
	    int depth = 0;
	    Block currentNode = node.getParent();
	
	    while(currentNode != null) {
		    depth = depth + 1;
		    currentNode = currentNode.getParent();
	    }
	   
	    return depth;
	
    }
    
	static List<Block> getAllLeaves(Block root){
	 	Queue<Block> q = new LinkedBlockingQueue<Block>();
	 	List<Block> leaves = new ArrayList<Block>();
	 	Block item;
	 	if(root != null){
	 		q.offer(root);
	 		while(!q.isEmpty())
	 		{
	 			item = q.remove();
	 			if (item.isLeaf())
	 				leaves.add(item);		 			
	 			
	 			if(item != null)
	 			{
	 				for (Block child : item.getChildren()){		 				
	 					q.offer(child);
	 				}
	 			}
	 		}
	 	}
	 	
	 	return leaves;
	}
	
	public static int computeMaximumDecompositionLevel(Block root) {
		return XYFeatureComputer.computeNodeHeight(root);
	}
	
	private static int computeNodeHeight(Block node) {
		if (node.isLeaf())
			return 0;
		else {
			// find the maximum height of its children
			int max = Integer.MIN_VALUE;
			for (Block c : node.getChildren()) {
				
				int height = XYFeatureComputer.computeNodeHeight(c);
				
				if (max < height)
					max = height;
			}
			
			return 1 + max;
		}
	}
	
	public static int computeTextArea(Block root) {
		
		// remove the leaves that are not text
		
		return XYFeatureComputer.computeTextAreaRecursive(root);
	}
	
	private static int computeTextAreaRecursive(Block node) {
		// Just go to the leaves and check if the block isText()
		if (node.isLeaf()) {
			if (node.isText())
				return node.getArea();
			else
				return 0;
		}
		
		int area = 0;
		for (Block c : node.getChildren()) 
			area += computeTextAreaRecursive(c);
		
		return area;
	}
	
	public static int countNumberOfNodesInLevel(Block root, int level) {
		return recursiveCountNumberOfNodes(root,level,0);
	}
	
	private static int recursiveCountNumberOfNodes(Block node, int level, int currentLevel) {
		
		if(currentLevel == level) { 
			return 1;
		}
		else {
			int count = 0;
			for (Block c : node.getChildren())
				count += recursiveCountNumberOfNodes(c,level,currentLevel + 1);
			return count;
		}
	}
	
	public static int countNumberOfTextGroup(Block root) {
		return recursiveCountNumberOfTextGroup(root);
	}
	
	private static int recursiveCountNumberOfTextGroup(Block node) {
		if (node.isLeaf())
			return 0;
		
		int count = 0;
		if (blockContainTextChildren(node))
			count += 1;
		
		// recursively count the number from its descendant
		for (Block b : node.getChildren())
			count += recursiveCountNumberOfTextGroup(b);
		
		return count;
	}
	
	private static boolean blockContainTextChildren(Block node) {
		List<Block> children = node.getChildren();
		
		boolean found = false;
		for(int ii = 0; ii < children.size() && !found; ii++) {
			if (children.get(ii).isTextBlock())
				found = true;
		}
		
		return found;
	}
	
	public static int countNumberOfImageArea(Block root) {
		return recursiveCountNumberOfImageArea(root);
	}
	
	private static int recursiveCountNumberOfImageArea(Block node) {
		if (node.isLeaf())
			return 0;
		
		int count = 0;
		if (blockContainImageChildren(node))
			count += 1;
		
		// recursively count the number from its descendant
		for (Block b : node.getChildren())
			count += recursiveCountNumberOfImageArea(b);
		
		return count;
	}
	
	private static boolean blockContainImageChildren(Block node) {
        List<Block> children = node.getChildren();
		
		boolean found = false;
		for(int ii = 0; ii < children.size() && !found; ii++) {
			Block c = children.get(ii);
			if (c.isLeaf() && !c.isTextBlock() && c.getArea() >= 800)
				found = true;
		}
		
		return found;
	}
	
	public static int computeNonTextLeavesArea(Block root) {
		return XYFeatureComputer.computeNonTextLeavesRecursive(root);
	}
	
	private static int computeNonTextLeavesRecursive(Block node) {
		// Just go to the leaves and check if the block not isText()
		if (node.isLeaf()) {
			if (!node.isText())
				return node.getArea();
			else
				return 0;
		}
		
		int area = 0;
		for (Block c : node.getChildren()) 
			area += XYFeatureComputer.computeNonTextLeavesRecursive(c);
		
		return area;
	}
}
