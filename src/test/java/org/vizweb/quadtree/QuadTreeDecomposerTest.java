/*******************************************************************************
 * Copyright 2011 sikuli.org
 * Released under the MIT license.
 * 
 * Contributors:
 *     Tom Yeh - initial API and implementation
 ******************************************************************************/
package org.vizweb.quadtree;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
//import org.junit.Test;
import org.sikuli.core.logging.ImageExplainer;
import org.vizweb.QuadtreeFeatureComputer;
import org.vizweb.quadtree.ColorEntropyDecompositionStrategy;
import org.vizweb.quadtree.IntensityEntropyDecompositionStrategy;
import org.vizweb.quadtree.QuadTreeDecomposer;
import org.vizweb.quadtree.QuadTreeDecompositionStrategy;
import org.vizweb.quadtree.Quadtree;
import org.vizweb.structure.BinaryImageStructureFeatureComputer;

public class QuadTreeDecomposerTest {

	
	static public BufferedImage createComponentImage(Component component) {
		Dimension size = component.getSize();
		BufferedImage image = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		component.paint(g2);
		g2.dispose();
		return image;
	}
	
	//@Test
	public void testComputeSymmetryScoresForABatchOfImages(){

		ImageExplainer logger = ImageExplainer.getExplainer(BinaryImageStructureFeatureComputer.class);
		logger.setLevel(ImageExplainer.Level.STEP);
		
		File folder = new File("E:/UMD/Research/dataset/wot1000");
		File[] files = folder.listFiles();
		
		try {
			
			FileWriter writer = new FileWriter("quadtree.csv");
			writer.append("filename,colorNumQuadTreeLeaves," +
					        "intensityNumQuadTreeLeaves," +
					        "colorHorizontalSymmetry," +
					        "colorVerticalSymmetry," +
					        "colorHorizontalBalance," +
					        "colorVerticalBalance," +
					        "intensityHorizontalSymmetry," +
					        "intensityVerticalSymmetry," +
					        "intensityHorizontalBalance," +
					        "intensityVerticalBalance," +
					        "colorEquilibrium," +
					        "intensityEquilibrium\n");
			
			for (int i = 1350; i < files.length; i++){
				File f = files[i];
				System.out.println("Processing: " + f.getName());
				try {
					
					BufferedImage input = ImageIO.read(f);
					
					QuadTreeDecompositionStrategy iStrategy = new IntensityEntropyDecompositionStrategy();
					QuadTreeDecompositionStrategy cStrategy = new ColorEntropyDecompositionStrategy();
					
					QuadTreeDecomposer cDecomposer = new QuadTreeDecomposer(cStrategy);
					Quadtree cRoot = cDecomposer.decompose(input);
					
					QuadTreeDecomposer iDecomposer = new QuadTreeDecomposer(iStrategy);
					Quadtree iRoot = iDecomposer.decompose(input);
					
					Point centerOfImage = new Point(input.getWidth()/2,input.getHeight()/2);
					double colorEquilibrium = QuadtreeFeatureComputer
							.computeEquilibrium(cRoot, centerOfImage);
					double intensityEquilibrium = QuadtreeFeatureComputer
							.computeEquilibrium(iRoot, centerOfImage);
					
					double colorHorizontalSymmetry = QuadtreeFeatureComputer
							.computeHorizontalSymmetry(cRoot);
					double colorVerticalSymmetry = QuadtreeFeatureComputer
							.computeVerticalSymmetry(cRoot);
					double colorHorizontalBalance = QuadtreeFeatureComputer
							.computeHorizontalBalance(cRoot);
					double colorVerticalBalance = QuadtreeFeatureComputer
							.computeVerticalBalance(cRoot);
					
					double intensityHorizontalSymmetry = QuadtreeFeatureComputer
							.computeHorizontalSymmetry(iRoot);
					double intensityVerticalSymmetry = QuadtreeFeatureComputer
							.computeVerticalSymmetry(iRoot);
					double intensityHorizontalBalance = QuadtreeFeatureComputer
							.computeHorizontalBalance(iRoot);
					double intensityVerticalBalance = QuadtreeFeatureComputer
							.computeVerticalBalance(iRoot);
					
					int colorNumQuadTreeLeaves = cRoot.countLeaves()/4;
					int intensityNumQuadTreeLeaves = iRoot.countLeaves()/4;
					
					writer.append("wot1000_" + f.getName() + "," +
					        colorNumQuadTreeLeaves + "," +
					        intensityNumQuadTreeLeaves + "," +
					        colorHorizontalSymmetry + "," +
					        colorVerticalSymmetry + "," +
					        colorHorizontalBalance + "," +
					        colorVerticalBalance + "," +
					        intensityHorizontalSymmetry + "," +
					        intensityVerticalSymmetry + "," +
					        intensityHorizontalBalance + "," +
					        intensityVerticalBalance + "," +
					        colorEquilibrium + "," +
					        intensityEquilibrium);
					
				} catch (IOException ioe) {
					System.out.println("Failed processing: " + f.getName() 
							+ ". Error: " + ioe.getMessage());
					writer.append("io_failed_"+ f.getName() + ",,,,,,,,,,,,");
				} catch (Exception e) {
					System.out.println("Failed processing: " + f.getName() 
							+ ". Error: " + e.getMessage());
					writer.append("failed_"+ f.getName() + ",,,,,,,,,,,,");
				}
				
				writer.append("\n");
				
			}
			
			writer.flush();
			writer.close();
			
		} catch (IOException ioe) {
			
		}
	}
}
