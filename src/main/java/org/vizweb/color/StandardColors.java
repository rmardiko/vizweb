package org.vizweb.color;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

public class StandardColors implements NamedColors{

	List<NamedColor> colors = Lists.newArrayList();
	StandardColors(){	
		colors.add(new NamedColor("black",0,0,0));
		colors.add(new NamedColor("silver",192,192,192));
		colors.add(new NamedColor("gray",128,128,128));
		colors.add(new NamedColor("white",255,255,255));
		colors.add(new NamedColor("maroon",128,0,0));
		colors.add(new NamedColor("red",255,0,0));
		colors.add(new NamedColor("purple",128,0,128));
		colors.add(new NamedColor("fuchsia",255,0,255));
		colors.add(new NamedColor("green",0,128,0));
		colors.add(new NamedColor("lime",0,255,0));
		colors.add(new NamedColor("olive",128,128,0));
		colors.add(new NamedColor("yellow",255,255,0));
		colors.add(new NamedColor("navy",0,0,128));
		colors.add(new NamedColor("blue",0,0,255));
		colors.add(new NamedColor("teal",0,128,128));
		colors.add(new NamedColor("aqua",0,255,255));		
	}
	
	public Iterator<NamedColor> iterator() {
		return colors.iterator();
	}
}
