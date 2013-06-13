package org.vizweb.color;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.Test;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;

public class ColorAnalyzerTest {
	

	//@Test
	public void testComputeColorDistribution() throws IOException{
		
		BufferedImage input = ImageIO.read(new File("src/test/resources/awn.png"));
		Map<NamedColor, Double> d = ColorAnalyzer.computeColorDistribution(input);
		for (NamedColor c : d.keySet()){			
			System.out.println(" " + c.getName() + ":" + d.get(c));			
		}
	}
	
	//@Test
	public void testComputeColorDistributionFromDataset() {
		
		File folder = new File("E:/UMD/Research/dataset/wot1000");
		File[] files = folder.listFiles();
		
		try {
			FileWriter writer = new FileWriter("test.csv");
			
			StandardColors colorNames = new StandardColors();
			writer.append("filename,width,height,numOfPixels,");
			for (NamedColor c : colorNames) {
				writer.append(c.getName() + ",");
			}
			
			writer.append("hue,saturation,value\n");
			
			for (int ii = 0; ii < files.length; ii++) {
				
				File file = files[ii];
				System.out.println("Processing: " + file.getName());
				
				try {
					BufferedImage input = ImageIO.read(file);
					writer.append("wot1000_" + file.getName() + ",");
					
					int height = input.getHeight();
					int width = input.getWidth();
					int numOfPixels = height * width;
					
					writer.append( width + "," + height + "," + numOfPixels + ",");
					Map<NamedColor, Double> d = ColorAnalyzer.computeColorDistribution(input);

					for (NamedColor c : colorNames.colors){			
						writer.append(d.get(c) + ",");
					}
					
					CvScalar avg = ColorAnalyzer.computeAverageHueSaturationValue(input);
					writer.append(avg.getVal(0) + "," + avg.getVal(1) + "," + avg.getVal(2) + "\n");
					
				} catch (IOException ioe) {
					System.out.println("Failed processing: " + file.getName() 
							+ ". Error: " + ioe.getMessage());
					writer.append("io_failed_"+file.getName() + ",N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,");
				} catch (Exception e) {
					System.out.println("Failed processing: " + file.getName() 
							+ ". Error: " + e.getMessage());
					writer.append("failed_"+file.getName() + ",N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,");
				}
				
				writer.append("\n");
			
			}
			
			writer.flush();
			writer.close();
		} 
		catch (IOException ioe) {
			
		}
		
	}
	
	//@Test
	public void testComputeColorfulness() throws IOException {
		BufferedImage input = ImageIO.read(new File("src/test/resources/puretext.png"));
		System.out.println(ColorAnalyzer.computeColorfulness2(input));
	}
	
	@Test
	public void testComputeColorfulnessFromDataset() {
		
		File folder = new File("E:/UMD/Research/dataset/wot1000");
		File[] files = folder.listFiles();
		
		try {
			
			FileWriter writer = new FileWriter("colorfulness.csv");
			writer.append("filename,colorfulness1,colorfulness2\n");
			
			for (File f : files) {
				
				System.out.println("Processing: " + f.getName());
				
				try {
					
					BufferedImage input = ImageIO.read(f);
					writer.append("wot1000_" + f.getName() + ",");
					writer.append(ColorAnalyzer.computeColorfulness(input) + ",");
					writer.append(ColorAnalyzer.computeColorfulness2(input) + "");
					
				} catch (IOException ioe) {
					System.out.println("Failed processing: " + f.getName() 
							+ ". Error: " + ioe.getMessage());
					writer.append("io_failed_"+ f.getName() + ",N/A,N/A");
				} catch (Exception e) {
					System.out.println("Failed processing: " + f.getName() 
							+ ". Error: " + e.getMessage());
					writer.append("io_failed_"+ f.getName() + ",N/A,N/A");
				}
				
				writer.append("\n");
				
			}
			
			writer.flush();
			writer.close();
			
		} catch (IOException ioe) {
			
		} catch (Exception e) {
			
		}
		
	}

}
