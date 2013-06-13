package org.vizweb.structure;

import java.awt.Rectangle;

import org.junit.Test;
import org.vizweb.XYFeatureComputer;
import org.vizweb.structure.Block;
import org.vizweb.structure.BlockType;

import static org.junit.Assert.assertTrue;

public class BlockTest {
	
	private Block root;
	
	@Test
	public void testCalculateTextArea() {
		root = new Block();
		root.setBounds(new Rectangle(1000,500));
		
		Block child1 = new Block(), 
				child2 = new Block(), 
				child3 = new Block();
		child1.setBounds(new Rectangle(10,10,100,50)); // 5000
		child2.setBounds(new Rectangle(200,10,100,100));
		child3.setBounds(new Rectangle(400,20,200,60));
		
		child1.setType(BlockType.Text);
		
		Block gChild1 = new Block(), 
				gChild2 = new Block(), 
				gChild3 = new Block(); 
		
		gChild1.setBounds(new Rectangle(210,15,70,30));
		gChild2.setBounds(new Rectangle(210,60,50,30)); // 1500
		gChild3.setBounds(new Rectangle(400,20,100,40)); // 4000
		
		gChild2.setType(BlockType.Text);
		gChild3.setType(BlockType.Text);
		
		child2.addChild(gChild1);
		child2.addChild(gChild2);
		child3.addChild(gChild3);
		
		root.addChild(child1);
		root.addChild(child2);
		root.addChild(child3);
		
		assertTrue( XYFeatureComputer.computeTextArea(root) == 10500 );
	}
}
