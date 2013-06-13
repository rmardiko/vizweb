package org.vizweb.examples;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.vizweb.crop.BannerExtractor;
import org.vizweb.structure.Block;
import org.vizweb.xycut.XYXmlFileProcessor;

public class ExtractTitleBannerExample {

	public static void main(String[] args) throws IOException {
		extractFromDataset("E:\\UMD\\Research\\dataset\\xml\\foreign");
	}
	
	public static void extractFromOneFile(String fileName) throws IOException {
		String xmlSourceFileName = fileName;
		
		File imageSource = new File(
				XYXmlFileProcessor.getImageFileName(xmlSourceFileName));
		BufferedImage input = ImageIO.read(imageSource);
		Block structure = Block.loadFromXml(xmlSourceFileName);
		
		BannerExtractor be = new BannerExtractor();
		BufferedImage titleBannerCrop = be.extract(input, structure);
		
		ImageIO.write(titleBannerCrop, "png", 
				new File("TitleBanner_" + imageSource.getName()));
	}
	
	public static void extractFromDataset(String folderName) throws IOException {
		// read the folder
		File folder = new File(folderName);
		File[] files = folder.listFiles();
		
		for (int ii = 0; ii < 50; ii++) {
			File f = files[ii];
			String xmlSourceFileName = f.getAbsolutePath();
			
			System.out.println(f.getAbsolutePath());
			
			File imageSource = new File(
					XYXmlFileProcessor.getImageFileName(xmlSourceFileName));
			BufferedImage input = ImageIO.read(imageSource);
			Block structure = Block.loadFromXml(xmlSourceFileName);
			
			BannerExtractor be = new BannerExtractor();
			BufferedImage titleBannerCrop = be.extract(input, structure);
			
			ImageIO.write(titleBannerCrop, "png", 
					new File("TitleBanner_foreign" + imageSource.getName()));
		}
	}
}
