package org.vizweb.examples;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.vizweb.crop.FooterExtractor;
import org.vizweb.structure.Block;
import org.vizweb.xycut.XYXmlFileProcessor;

public class ExtractFooterExample {
	
	public static void main(String[] args) throws IOException {
		extractFromDataset("E:\\UMD\\Research\\dataset\\xml\\english\\footer", 
				"Footer_english");
		
		extractFromDataset("E:\\UMD\\Research\\dataset\\xml\\foreign\\footer", 
				"Footer_foreign");
	}
	
	public static void extractFromOneFile(String fileName) throws IOException {
		String xmlSourceFileName = fileName;
		
		File imageSource = new File(
				XYXmlFileProcessor.getImageFileName(xmlSourceFileName));
		BufferedImage input = ImageIO.read(imageSource);
		Block structure = Block.loadFromXml(xmlSourceFileName);
		
		FooterExtractor fe = new FooterExtractor();
		BufferedImage titleBannerCrop = fe.extract(input, structure);
		
		ImageIO.write(titleBannerCrop, "png", 
				new File("Footer_Example" + imageSource.getName()));
	}
	
	public static void extractFromDataset(String folderName, String outputPrefix) throws IOException {
		// read the folder
		File folder = new File(folderName);
		File[] files = folder.listFiles();
		
		for (int ii = 0; ii < files.length; ii++) {
			File f = files[ii];
			String xmlSourceFileName = f.getAbsolutePath();
			
			System.out.println(f.getAbsolutePath());
			
			File imageSource = new File(
					XYXmlFileProcessor.getImageFileName(xmlSourceFileName));
			BufferedImage input = ImageIO.read(imageSource);
			Block structure = Block.loadFromXml(xmlSourceFileName);
			
			FooterExtractor fe = new FooterExtractor();
			BufferedImage footerCrop = fe.extract(input, structure);

			ImageIO.write(footerCrop, "png", 
					new File(outputPrefix + imageSource.getName()));
		}
	}

}
