package org.vizweb.color;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

class HTMLColors implements NamedColors{

	List<NamedColor> colors;

	HTMLColors(){
		try {
			importFromFile(new File("colors.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void importFromFile(File file) throws IOException{
		colors = Files.readLines(file, Charset.defaultCharset(), new LineProcessor<List<NamedColor>>(){

			List<NamedColor> colors = Lists.newArrayList();
			public List<NamedColor> getResult() {
				return colors;
			}

			public boolean processLine(String line) throws IOException {
				Iterable<String> toks = Splitter.on(" ").omitEmptyStrings().split(line);
				Iterator<String> it = toks.iterator();


				// AliceBlue   F0F8FF   240,248,255
				String colorName = it.next();	
				@SuppressWarnings("unused")
				String hex = it.next();
				String rgbString = it.next();		

				Iterable<String> rgbToks = Splitter.on(",").split(rgbString);
				Iterator<String> rgbIt = rgbToks.iterator();
				int r = Integer.parseInt(rgbIt.next());
				int g = Integer.parseInt(rgbIt.next());
				int b = Integer.parseInt(rgbIt.next());

				NamedColor color = new NamedColor(colorName, r, g, b);
				colors.add(color);
				return true;
			}
		});


		for (NamedColor color : colors){
			System.out.println(color);
		}
	}

	public Iterator<NamedColor> iterator() {
		return colors.iterator();
	}

}
