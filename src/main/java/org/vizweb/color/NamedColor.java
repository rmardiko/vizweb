package org.vizweb.color;

import java.awt.Color;

@SuppressWarnings("serial")
public class NamedColor extends Color{

	final private String name;
	public NamedColor(String name, int r, int g, int b){
		super(r,g,b);
		this.name = name;
	}
	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		return "NamedColor [" + name + " (" + getRed()
		+ ", " + getGreen() + ", " + getBlue()
		+ ")]";
	}	
	public String toCSS(){
		return "rgb(" + getRed() + "," + getGreen() + "," + getBlue() + ")";
	}

}

interface NamedColors extends Iterable<NamedColor>{

}

