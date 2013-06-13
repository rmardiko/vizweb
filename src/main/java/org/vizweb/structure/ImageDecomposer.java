package org.vizweb.structure;

import java.awt.image.BufferedImage;


public abstract class ImageDecomposer {
	public abstract Block decompose(BufferedImage image, DecompositionStrategy strategy);
}
