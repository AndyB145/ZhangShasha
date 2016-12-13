/* Created by Andy Baay at Davidson College */

package zhsh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class Monosaccharide extends Node{
	
	// Members // 
	public Boolean isSubstituent = false;
	public Boolean hasSubstituent = false; // is this monosaccharide decorated?
	public HashMap<Integer, Node> substituent;
	public SortedMap<Integer, Node> childrenMap = new TreeMap<Integer, Node>();
	
	// Linkage between parent and child is stored in the child node. This means that the root will 
	// have an unknown linkage (denoted by "?"). 
	public char linkage = '?';
	public Node parent = null;
	
	public Monosaccharide(String label){
		super(label);
		this.children = new ArrayList(childrenMap.values());
	}
	
	public Monosaccharide(String label, Boolean isSub){
		super(label);
		this.isSubstituent = isSub;
	}
	
	public void addChild(Monosaccharide child, Integer position, char link){
		this.childrenMap.put(position, child);
		child.parent = this;
		child.linkage = link;
	}
	
	public void addSubstituent(Monosaccharide sub, Integer position, char link){
		this.hasSubstituent = true;
		this.substituent = new HashMap<Integer, Node>();
		this.substituent.put(position, sub);
		sub.linkage = link;
		this.label += "+" + sub.label;
	}
	
	public void syncNode(){
		this.children = new ArrayList(childrenMap.values());
	}
	
	
	//override toString()
	public String toString(){
		if (this.isSubstituent) {
			return ("SUB{Label: " + label + "}");
		}
		return ("MONO{Label: " + label + "}");
	}
}