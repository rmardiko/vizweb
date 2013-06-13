package org.vizweb.xycut;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
//import org.junit.Test;
import org.sikuli.core.logging.ImageExplainer;
import org.vizweb.structure.Block;
import org.vizweb.structure.ImageDecomposer;
import org.vizweb.xycut.DefaultXYDecompositionStrategy;
import org.vizweb.xycut.XYDecomposer;
import org.vizweb.xycut.XYTreePainter;
import org.vizweb.XYFeatureComputer;

public class XYDecomposerTest {
	
	private BufferedImage input;


   //@Test
	public void testXYDecomposerBasic() {
		ImageExplainer explainer = ImageExplainer.getExplainer(XYDecomposer.class);
				
		ImageDecomposer d = new XYDecomposer();		
		explainer.setLevel(ImageExplainer.Level.STEP);
		explainer.step(input, "input for xy decomposition");		

		explainer.setLevel(ImageExplainer.Level.OFF);
		Block root = d.decompose(input, new DefaultXYDecompositionStrategy(){

			@Override
			public int getMaxLevel() {
				return 4;
			}

			@Override
			public boolean isSplittingHorizontally() {
				return true;
			}

			@Override
			public boolean isSplittingVertically() {
				return true;
			}

			@Override
			public int getMinSeperatorSize() {
				return 4;
			}

			
		});
		
		explainer.setLevel(ImageExplainer.Level.STEP);
		BufferedImage image = XYTreePainter.paintOnImage(input, root);
		explainer.step(image, "decomposition result");
		
	}
	
	//@Test
	public void testXYComputer() throws IOException {
		BufferedImage input = ImageIO.read(new File("src/test/resources/awn.png"));
		Block root = XYFeatureComputer.getXYBlockStructure(input);
		System.out.println("Number of leaves: " + XYFeatureComputer.countNumberOfLeaves(root));
	}
}
