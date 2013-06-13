package org.vizweb.xycut;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
//import org.junit.Test;
import org.vizweb.xycut.LineSeparatorExtractor;
import org.vizweb.xycut.SeparatorExtractor;

public class SeparatorModelTest {
	private BufferedImage input;	
	
	//@Test
	public void testIdentifyLineSeparators(){
		SeparatorExtractor se = new LineSeparatorExtractor(input);
		se.extractFromRegion(new Rectangle(120,60,input.getWidth()/2,input.getHeight()-330));
		
	}
}
