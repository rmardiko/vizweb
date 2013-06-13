package org.vizweb.xycut;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.vizweb.structure.Block;
import org.vizweb.structure.BlockType;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XYXmlFileProcessor {
	
	public static void write(Block root, String imageFileName, String xmlFileName) {
		XYXmlFileWriter.writeXmlForBlock(root, imageFileName, xmlFileName);
	}
	
	public static Block read(String inputFileName) {
		return XYXmlFileReader.readFromXml(inputFileName);
	}
	
	public static String getImageFileName(String xmlFileName) {
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse (new File(xmlFileName));
			
			//normalize text representation
	        doc.getDocumentElement().normalize();
	        
	        Node imageTag = doc.getElementsByTagName("image").item(0);
	        return ((Attr)imageTag.getAttributes().getNamedItem("name")).getValue();
	        
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		return "";
	}
	
}

class XYXmlFileWriter {

	public static void writeXmlForBlock(Block root, String imageFileName, String xmlFileName) {
		
		try {
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("XYDecomposition");
			doc.appendChild(rootElement);
			
			Element imgSource = doc.createElement("image");
			imgSource.setAttribute("name", imageFileName);
			rootElement.appendChild(imgSource);
			
			Element tree = recursiveWriteXml(root, doc);
			rootElement.appendChild(tree);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
			StreamResult result = new StreamResult(new File(xmlFileName));
	 
			transformer.transform(source, result);
			
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	private static Element recursiveWriteXml(Block node, Document doc) {
		Element parent = doc.createElement("Block");
		
		// Store attributes		
		parent.setAttribute("x", ""+node.getBounds().x);
		parent.setAttribute("y", ""+node.getBounds().y);
		parent.setAttribute("width", ""+node.getBounds().width);
		parent.setAttribute("height", ""+node.getBounds().height);
		parent.setAttribute("istext", ""+node.isText());
		
		for (Block c : node.getChildren()) {
			Element subTree = recursiveWriteXml(c, doc);
			
			parent.appendChild(subTree);
		}
		
		return parent;
	}
}

class XYXmlFileReader {
	
	public static Block readFromXml(String xmlFileName) {
		Block root = new Block();
		File fXmlFile = new File(xmlFileName);
		
		try {
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			// get the root
			Node docRoot = doc.getDocumentElement();
			NodeList first = docRoot.getChildNodes();
			
			for (int index = 0; index < first.getLength(); index++) {
				Node c = first.item(index);
				if (c.getNodeName().equals("Block"))
					root = recursiveReadXml(c);
			}
		
		} catch(Exception e) { e.printStackTrace(); }
		
		return root;
	}
	
	private static Block recursiveReadXml(Node xmlNode) {
		Block parent = new Block();
		
		NamedNodeMap attrList = xmlNode.getAttributes();
		
		Rectangle bounds = new Rectangle();
		
		bounds.x = Integer.parseInt(((Attr)attrList.getNamedItem("x")).getValue());
		bounds.y = Integer.parseInt(((Attr)attrList.getNamedItem("y")).getValue());
		bounds.width = Integer.parseInt(((Attr)attrList.getNamedItem("width")).getValue());
		bounds.height = Integer.parseInt(((Attr)attrList.getNamedItem("height")).getValue());
		
		boolean textFlag = Boolean
				.parseBoolean(((Attr)attrList.getNamedItem("istext")).getValue());
		
		parent.setBounds(bounds);
		if (textFlag) parent.setType(BlockType.Text);
		
		NodeList children = xmlNode.getChildNodes();
		
		for (int ii = 0; ii < children.getLength(); ii++) {
			
			Node n = children.item(ii);
			
			if (n.getNodeName().equals("Block")) {
				Block c = recursiveReadXml(children.item(ii));
			    parent.addChild(c);
			}
		}
		
		return parent;
	}
	
}
