package org.vizweb.examples;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.vizweb.structure.Block;

public class LoadDecompositionResultFromXml {
	
	public static void main(String[] args) throws IOException {
		
		// read the folder
		File folder = new File("E:\\UMD\\Research\\dataset\\footer");
		File[] files = folder.listFiles();
		
		for (int i=0; i < files.length; i++) {
			
			File f = files[i];
			
			String[] temp;
			
			temp = f.getName().split("_");
			
			String sourceFileName = "E:\\UMD\\Research\\dataset\\xml\\" + temp[0]
					+ "\\" + temp[1].replace('.', '-') +".xml";
			
			Block result = Block.loadFromXml(sourceFileName);
		/*	BufferedImage inputImage = ImageIO.read(new File(
					XYXmlFileProcessor.getImageFileName(sourceFileName)));*/
			BufferedImage inputImage = ImageIO.read(f);
			
			BufferedImage display = result.toImage(inputImage);
			
			temp = f.getName().split(".");
			
			String destinationFileName = 
					"E:\\UMD\\Research\\dataset\\footer\\fromXml\\FromXml_" 
			         + f.getName();
			
			ImageIO.write(display, "png", new File(destinationFileName));
			
		}
				
		String sourceFileName = "E:\\UMD\\Research\\dataset\\xml\\0-png.xml";
		String imageFileName = "E:\\UMD\\Research\\dataset\\english\\0.png";
		
		Block result = Block.loadFromXml(sourceFileName);
		BufferedImage inputImage = ImageIO.read(new File(imageFileName));
		
		BufferedImage display = result.toImage(inputImage);
		
		ImageIO.write(display, "png", new File("loadFromXmlResult.png"));
	}
}
