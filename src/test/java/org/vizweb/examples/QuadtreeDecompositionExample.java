package org.vizweb.examples;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.vizweb.QuadtreeFeatureComputer;
import org.vizweb.quadtree.QuadTreeDecomposer;
import org.vizweb.quadtree.Quadtree;

public class QuadtreeDecompositionExample {
	
	public static void main(String[] args) throws IOException {
		
		File folder = new File("E:\\UMD\\Research\\dataset\\english");
		File[] files = folder.listFiles();
		
		for (File file : files) {
			
			BufferedImage inputImage = ImageIO.read(file);
			
			QuadTreeDecomposer qtd = QuadTreeDecomposer.newColorDecomposer();
			Quadtree root = qtd.decompose(inputImage);
			
			double hSymmetry = QuadtreeFeatureComputer.computeHorizontalSymmetry(root),
					vSymmetry = QuadtreeFeatureComputer.computeVerticalSymmetry(root),
					hBalance = QuadtreeFeatureComputer.computeHorizontalBalance(root),
					vBalance = QuadtreeFeatureComputer.computeVerticalBalance(root);
			
			System.out.println(file.getName() + "," + hSymmetry + "," + vSymmetry + "," + hBalance + "," + vBalance);
		}
		
	}

}
