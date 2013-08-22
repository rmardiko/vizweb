package org.vizweb.examples;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.vizweb.XYFeatureComputer;
import org.vizweb.structure.Block;

public class XYFeatureComputerExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		// the input is in the form of BufferedImage object
		BufferedImage input = ImageIO.read(new File("src/test/resources/awn.png"));
		
		Block root = XYFeatureComputer.getXYBlockStructure(input);
		ImageIO.write(root.toImage(input), "png", new File("src/test/resources/output.png"));
		
		System.out.println("Number of leaves: " 
		           + XYFeatureComputer.countNumberOfLeaves(root));
		System.out.println("Area of text: " 
		           + XYFeatureComputer.computeTextArea(root));
		System.out.println("Area of non-text: " 
		           + XYFeatureComputer.computeNonTextLeavesArea(root));
		System.out.println("Number of group of text: " 
		           + XYFeatureComputer.countNumberOfTextGroup(root));

	}

}
