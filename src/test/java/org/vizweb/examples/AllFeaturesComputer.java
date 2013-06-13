package org.vizweb.examples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.vizweb.color.NamedColor;
import org.vizweb.structure.Block;
import org.vizweb.XYFeatureComputer;

public class AllFeaturesComputer {
	
	public static void main(String[] args) {
		computeXYFeatures("E:/UMD/Research/dataset/xml/wot1000");
	}
	
	public static void computeXYFeatures(String xmlFolderName) {
		// read the folder
		File folder = new File(xmlFolderName);
		File[] files = folder.listFiles();
		
		try {
			
			FileWriter writer = new FileWriter("xyfeatures.csv");
			
			writer.append(
					"filename,textArea,nonTextArea,maxLevel," +
			        "averageLevel,numOfLeaves,numOf1stLevelNodes," + 
		            "numOf2ndLevelNodes,numOf3rdLevelNodes,percentageOfLeafArea," +
                    "percentageOfLeafArea2,numOfTextGroup,numOfImageArea\n");
			
			for (File f:files) {
				
				System.out.println("Processing: " + f.getName());
				
				Block rootWithTextdetected = Block.loadFromXml(f.getAbsolutePath());
				
				int textArea = XYFeatureComputer.
				computeTextArea(rootWithTextdetected);
				
				int nonTextLeavesArea = XYFeatureComputer.
				computeNonTextLeavesArea(rootWithTextdetected);
				
				int maxLevel = XYFeatureComputer.
				computeMaximumDecompositionLevel(rootWithTextdetected);
		
				double avgLevel = XYFeatureComputer.
				computeAverageDecompositionLevel(rootWithTextdetected);
		
				int numOfLeaves = XYFeatureComputer.
				countNumberOfLeaves(rootWithTextdetected);
		
				int numOf1stLevelNodes = XYFeatureComputer.
				countNumberOfNodesInLevel(rootWithTextdetected, 1);
		
				int numOf2ndLevelNodes = XYFeatureComputer.
				countNumberOfNodesInLevel(rootWithTextdetected, 2);
		
				int numOf3rdLevelNodes = XYFeatureComputer.
				countNumberOfNodesInLevel(rootWithTextdetected, 3);
		
				double percentageLeafArea = XYFeatureComputer.
				computePercentageOfLeafArea(rootWithTextdetected);
		
				double percentageLeafArea2 = XYFeatureComputer.
				computePercentageOfLeafArea2(rootWithTextdetected);
		
				int numOfTextGroup = XYFeatureComputer.
				countNumberOfTextGroup(rootWithTextdetected);
		
				int numOfImageArea = XYFeatureComputer.
				countNumberOfImageArea(rootWithTextdetected);
				
				writer.append(
		            "webby_" + f.getName() + "," +
	                textArea + "," +
				    nonTextLeavesArea + "," + 
				    maxLevel + "," +
	                avgLevel + "," +
				    numOfLeaves + "," +
				    numOf1stLevelNodes + "," +
				    numOf2ndLevelNodes + "," +
				    numOf3rdLevelNodes + "," +
				    percentageLeafArea + "," +
				    percentageLeafArea2 + "," +
				    numOfTextGroup + "," +
				    numOfImageArea
	                //numOfTextLeaves + "," +
	                //(numOfLeaves-numOfTextLeaves)
	            );
				
				writer.append("\n");
			}
			
			writer.flush();
			writer.close();
			
		} catch (IOException ioe) {
			
		}
	}

}

class FeaturesExtractionResult {
	// Color features
	double colorfulness1;
	double colorfulness2;
	Map<NamedColor, Double> colorDist;
	double averageHSVHue;
	double averageHSVSaturation;
	double averageHSVValue;
	
	// Quadtree decomposition features
	int quadtreeColorNumberOfLeaves;
	double quadtreeColorHorizontalSymmetry;
	double quadtreeColorVerticalSymmetry;
	double quadtreeColorHorizontalBalance;
	double quadtreeColorVerticalBalance;
	double quadtreeColorEquilibrium;
	int quadtreeIntensityNumberOfLeaves;
	double quadtreeIntensityHorizontalSymmetry;
	double quadtreeIntensityVerticalSymmetry;
	double quadtreeIntensityHorizontalBalance;
	double quadtreeIntensityVerticalBalance;
	double quadtreeIntensityEquilibrium;
	
	// XY Decomposition features
	double textArea;
	double leavesAreaNoText;
	double maximumDepth;
	double averageDepth;
	int numberOfLeaves;
	int numberOf1stLevelNodes;
	int numberOf2ndLevelNodes;
	int numberOf3rdLevelNodes;
	double percentageOfLeavesArea;
	int numberOfTextGroups;
	int numberOfImageGroups;
}
