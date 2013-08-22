package org.vizweb.structure;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vizweb.xycut.SeparatorModel;
import org.vizweb.xycut.XYTreePainter;
import org.vizweb.xycut.XYXmlFileProcessor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

// Provides an abstraction for a block of visual content on a web page
public class Block {
	final private List<Block> children = new ArrayList<Block>();
	
	private Rectangle bounds;
	private Block parent = null;
	private BackgroundModel backgroundModel;
	private int numForegroundPixels;
	private BlockType type;
	
	public static final int MINIMUM_AREA = 10;
	public static final int MINIMUM_HEIGHT = 2;
	public static final int MINIMUM_WIDTH = 2;
	
	public Block() {
		type = BlockType.Unknown;
	}
	
	public Block(Block other) {
		this.bounds = other.bounds;
		this.parent = other.parent;
		this.backgroundModel = other.backgroundModel;
		this.numForegroundPixels = other.numForegroundPixels;
		this.type = other.type;
		this.children.addAll(other.children);
	}
	
	public BlockType getType() {
		return type;
	}
	
	public void setType(BlockType type) {
		this.type = type;
	}
	
	public Block getParent(){
		return parent;
	}
	
	public void setParent(Block parent){
		this.parent = parent;
	}
	
	public void addChild(Block child){
		child.setParent(this);
		children.add(child);
	}
	
	public Block getFirstChild(){
		return children.size() >= 1 ? children.get(0) : null;
	}
	public Block getSecondChild(){
		return children.size() >= 2 ? children.get(1) : null;		
	}
	public Block getChild(int index){
		return children.get(index);
	}
	
	private SeparatorModel separator;

	public List<Block> getChildren(){
		return ImmutableList.copyOf(children);
	}
	
	public boolean isLeaf(){
		return children.isEmpty();
	}
	
	public boolean isText(){
		
		// removing children, optional
		if (this.isTextBlock() && children.size() != 0) {
			children.clear();
		}
		
		return isTextBlock();
	}
	
	public boolean isOneLineText() {
		return false;
	}

	public int getArea() {
		return bounds.height * bounds.width;
	}
	
	Map<Object,Object> properties = new HashMap<Object,Object>();
	public void addProperty(Object key, Object value){
		properties.put(key,value);
	}

	public Object getProperty(Object key) {
		return properties.get(key);
	}

	public Rectangle getBounds() {		
		return bounds;
	}
	
	// TODO: more efficient implementation of this
	public List<Block> getLeaves(){
		if (isLeaf())
			return Lists.newArrayList(this);
		else{
			List<Block> leaves = Lists.newArrayList();
			for (Block n : getChildren()){
				leaves.addAll(n.getLeaves());
			}
			return leaves;
		}
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	@Deprecated
	public void setSeparator(SeparatorModel separator) {
		this.separator = separator;
	}

	@Deprecated
	public SeparatorModel getSeparator() {
		return separator;
	}

	public void setBackgroundModel(BackgroundModel backgroundModel) {
		this.backgroundModel = backgroundModel;
	}
	
	public BackgroundModel getBackgroundModel() {
		return backgroundModel;
	}

	public void setNumForegroundPixels(int numForegroundPixels) {
		this.numForegroundPixels = numForegroundPixels;
	}

	public int getNumForegroundPixels() {
		return numForegroundPixels;
	}
	
	// Remove blocks of size smaller than MINIMUM_AREA or
	// those whose the height or weight is 1
	public void filterOutSmallBlocks() {
		
		if (this.isLeaf()) return;
		
		// Recursively visit the blocks and see if the size is small
		for (Iterator<Block> it = children.iterator(); it.hasNext(); ) {
			Block c = it.next();
			if (
					c.getArea() < Block.MINIMUM_AREA || 
					c.getBounds().width < Block.MINIMUM_WIDTH ||
					c.getBounds().height < Block.MINIMUM_HEIGHT
				) it.remove();
		}
		
		// down to the children
		for (Block c : children) c.filterOutSmallBlocks();
	}
	
	public void removeChildrenOfTextBlocks() {
		
		if (this.isLeaf()) {
			return;
		}
		
		if (this.isTextBlock()) {
			children.clear();
		}
		else {
			// down to the children
			for (Block c : children) c.removeChildrenOfTextBlocks();
		}
		
	}

	public boolean isTextBlock() {
		return type == BlockType.Text;
	}
	
	public BufferedImage toImage(BufferedImage inputImage){
		return XYTreePainter.paintOnImage(inputImage, this);
	}
	
	public void toXML(String imageFileName, String xmlFileName) {
		XYXmlFileProcessor.write(this, imageFileName, xmlFileName);
	}
	
	public static Block loadFromXml(String xmlFileName) {
		return XYXmlFileProcessor.read(xmlFileName);
	}
}
