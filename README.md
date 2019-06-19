# Welcome

This is the code to produce Web aesthetic scores as described in the paper:

K Reinecke, T Yeh, L Miratrix, R Mardiko, Y Zhao, J Liu, KZ Gajos  (2013). [Predicting users' first impressions of website aesthetics with a quantification of perceived visual complexity and colorfulness](https://dash.harvard.edu/bitstream/handle/1/12561368/Predicting%20Users%20First%20Impressions.pdf?sequence=1&isAllowed=y) In proceedings of CHI'13.

# Prerequisite

This instruction assumes that you are familiar with Java (JDK 1.7) and Eclipse IDE.

# Maven users

If you are familiar with [Maven](http://maven.apache.org/) environment, then you can just clone (or download) the repository and run ```mvn install``` to get vizweb-1.0-SNAPSHOT. See below on how to use the library.

# Quick start

If you just need to run the compiled jar file and you are not interested in looking at the code, you can download the eclipse project [here](https://bitbucket.org/rmardiko/vizweb/downloads/vizweb-test.zip). Note that we use JDK 1.7 and we have not tested if it works with other Java versions.

# Using the library

Before using the code, you should already have an image of a web page screenshot which will become the input. 

There are three classes that provide methods for extracting the image features, namely ```ColorFeatureComputer```, ```QuadtreeFeatureComputer``` and ```XYFeatureComputer```. First, you need to import them.

```
#!java
import org.vizweb.ColorFeatureComputer;
import org.vizweb.XYFeatureComputer;
import org.vizweb.QuadtreeFeatureComputer;
import org.vizweb.quadtree.Quadtree;
import org.vizweb.structure.Block;
```

The following lines of code shows how to use them.

```
#!java
// the input is in the form of BufferedImage object
BufferedImage input = ImageIO.read(new File("images/awn.png"));

/*******************************
 * Compute color features
 *******************************/
double col = ColorFeatureComputer.computeColorfulness(input);
System.out.println("Colorfulness (method 1): " + col);

double col2 = ColorFeatureComputer.computeColorfulness2(input);
System.out.println("Colorfulness (method 2): " + col2);

/*******************************
 * Compute xy decomposition features (it may take a while)
 *******************************/
Block root = XYFeatureComputer.getXYBlockStructure(input);
System.out.println("Number of leaves: " + XYFeatureComputer.countNumberOfLeaves(root));
System.out.println("Number of group of text: " + XYFeatureComputer.countNumberOfTextGroup(root));

/*******************************
 * Compute quadtree features
 *******************************/
Quadtree qtColor = QuadtreeFeatureComputer.getQuadtreeColorEntropy(input);
System.out.println("Horizontal Balance: " + QuadtreeFeatureComputer.computeHorizontalBalance(qtColor));
System.out.println("Horizontal Symmetry: " + QuadtreeFeatureComputer.computeHorizontalSymmetry(qtColor));
```

# Contact
Please send any feedback or question to mardiko at gmail dot com. Thanks!
