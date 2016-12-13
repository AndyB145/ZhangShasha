/* Created by Andy Baay at Davidson College 2016 */
package zhsh;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

// When creating trees from glycans, the traversal is not quite pre-order or post-order, 
// residues are labels according from the root down based on the bond location of the current sugar
// This means that the residue attached at 3C and all of it's children will be listed before the 
// sugar attached at the 6C. Therefore, a hashing method cannot easily be created to link the 
// RES number to the tree node number or even to infer the shape of the tree without a traversal. 
//// NEXT STEP IS TO COMPLETE THE LINKAGES BASED ON GLYCOCT ////

public final class GlycoCTParser{
	
	//static final File sampleGlyco = new File("/Users/andybaay/Google Drive/Major/Glycoogle/Search Resources/AntigenA.glycoct_condensed");
	static final File sampleGlyco = new File("/Users/andybaay/Google Drive/Major/Glycoogle/Search Resources/gp120A.glycoct_condensed");
	
	//XXX remove print statements
	// Returns the root node of the glycan
	public static Node stringRepresentation(File myfile){
		/////// READ IN DATA ////////
		try (BufferedReader br = new BufferedReader(new FileReader(myfile))) {
		    String line;
		    String linetype = "";
		    Map<Integer, Monosaccharide> residues = new HashMap<Integer, Monosaccharide>();
		    
		    // All string handling takes place here
		    while ((line = br.readLine()) != null && !(line.isEmpty())) {
		    	//System.out.println("Our line is: \"" + line + "\"" + (line.isEmpty()));
		    	// Test the first character. A digit means the line is in a section (RES, LIN, ect.)
		    	if (Character.isDigit(line.charAt(0))){
		    		if (linetype == "RES") {
		    			parseResidue(line, residues);
		    		} else if (linetype == "LIN"){
		    			parseLink(line, residues);
		    		}
		    		continue;
		    	}
		    	//XXX continues aren't necessary here
		    	if (line.substring(0, 3).equals("RES")) { 
		    		linetype = "RES"; 
		    		continue; 
		    	} else if (line.substring(0,3).equals("LIN")) { 
		    		linetype = "LIN"; 
		    		//System.out.println(residues);
		    		continue;
		    	} else if (line.substring(0,3).equals("REP")) { 
		    		linetype = "REP"; 
		    		continue;
		    	} else if (line.substring(0,3).equals("UND")) {
		    		linetype = "UND"; 
		    		continue;
		    	} else if (line.substring(0,3).equals("ALT")) {
		    		linetype = "ALT"; 
		    		continue;
		    	}

		    }
		    
		    
		    return residues.get(1);
		    
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found!!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error reading file");
			e.printStackTrace();
		} 
		
		return null;
	}
	
	private static void parseResidue(String res, Map<Integer, Monosaccharide> residues){
		int resStartIndex;
		int resID; 
		Pair<Integer, Integer> r = getFirstNumber(res);
		resID = r.id;
		resStartIndex = r.start;
		
		String label = res.substring(resStartIndex + 2);
		
		switch (res.charAt(resStartIndex)) {
		case 'b':
			//System.out.println("this is a Basetype: " + label);
			residues.put(resID, new Monosaccharide(label));
			break;
		case 's':
			//System.out.println("this is a Substituent: " + res);
			residues.put(resID, new Monosaccharide(label, true));
			break;
		case 'r':
			System.out.println("this is a Repeating Unit");
			break;
		case 'a':
			System.out.println("this is a Alternative Unit");
			break;
		default: 
			System.out.println("this is not in GlycoCT");
			break;
		}
		
	}

	//XXX remove print statements later
	private static void parseLink(String link, Map<Integer, Monosaccharide> residues){
		// link is the string contain all the linkage info yet to be parses (changes during the method)
		// temp is a Pair object that temporarily holds return values from getFirstLink()
		
		// parse GlycoCT link id number
		Pair<Integer, Integer> temp = getFirstNumber(link);
		//System.out.println("linkage: " + link);
		int linkID = temp.id;
		link = link.substring(temp.start + 1);
		
		// parse first residue id number and link type
		// link is now the string minus the id number and colon
		//System.out.println(link);
		int openParen = link.indexOf("(");
		temp = getFirstNumber(link);
		Monosaccharide res1 = residues.get(temp.id);
		char res1Link = link.charAt(temp.start);
		
		// parse first residue link pos
		// up to the first parenthesis has been parsed
		link = link.substring(openParen + 1);
		temp = getFirstNumber(link);
		int res1LinkPos = temp.id;
		
		// parse second residue link pos
		link = link.substring(temp.start + 1);
		temp = getFirstNumber(link);
		int res2LinkPos = temp.id;
		//System.out.println(link);
		
		// parse second residue if number and link type
		link = link.substring(temp.start + 1);
		//System.out.println(link);
		temp = getFirstNumber(link);
		Monosaccharide res2 = residues.get(temp.id);
		char res2Link = link.charAt(temp.start);
		//System.out.println(res2Link);
		
		// create the link in the tree
		// res1 is the parent
		//System.out.println("Processing link between " + res1 + " and " + res2);
		if (res2.isSubstituent){
			res1.addSubstituent(res2, res2LinkPos, res2Link);
		}
		else{
			//System.out.println(res2 + " is being processed rn (" + res1LinkPos + ")\n");
			
			res1.addChild(res2, res1LinkPos, res2Link);
			res1.syncNode();
		}
	}
	
	// A string parser that returns the number at the beginning of the string passed in
	// 		example: inString = "123lettersin(alphabet)" returns (123, 3)
	// @returns A pair of integers- the first is the number parsed from the front of the string
	// 			and the second is the start index of the substring after that number 
	private static Pair<Integer, Integer> getFirstNumber(String line){
		Integer index = 1;
		while (Character.isDigit(line.charAt(index))){
			index++;
		}
		return new Pair<Integer, Integer>(Integer.parseInt(line.substring(0,index)), index);
	}
	
	public static class Pair<T1,T2>{
		public final T1 id;
		public final T2 start;
		public Pair(T1 id, T2 start){
			this.id = id;
			this.start = start;
		}
	}
	
	public static void main(String[] args){
		GlycoCTParser.stringRepresentation(sampleGlyco);
	}
}


