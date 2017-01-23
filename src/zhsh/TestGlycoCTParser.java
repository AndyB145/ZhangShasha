package zhsh;

import java.io.File;
import java.io.IOException;

public class TestGlycoCTParser {
	
	public static void main(String[] args) throws IOException {
		
		File antigenA = new File("/Users/andybaay/Google Drive/Major/Glycoogle/Search Resources/AntigenA.glycoct_condensed");
		File antigenAsim = new File("/Users/andybaay/Google Drive/Major/Glycoogle/Search Resources/AntigenASimilar.glycoct_condensed");
		File antigenB = new File("/Users/andybaay/Google Drive/Major/Glycoogle/Search Resources/AntigenB.glycoct_condensed");
		File hugeGlycan = new File("/Users/andybaay/Google Drive/Major/Glycoogle/Search Resources/HugeGlycan");
		File hugeGlycan2 = new File("/Users/andybaay/Google Drive/Major/Glycoogle/Search Resources/HugeGlycan2");
		

		//File sampleGlyco = new File("/Users/andybaay/Google Drive/Major/Glycoogle/Search Resources/gp120A.glycoct_condensed");
		
		Node root = GlycoCTParser.stringRepresentation(antigenA);
		Node root2 = GlycoCTParser.stringRepresentation(antigenAsim);
		Node root3 = GlycoCTParser.stringRepresentation(antigenB);
		Node root4 = GlycoCTParser.stringRepresentation(hugeGlycan);
		Node root5 = GlycoCTParser.stringRepresentation(hugeGlycan2);
		

		Tree tree1 = new Tree(root);
		Tree tree2 = new Tree(root2);
		Tree tree3 = new Tree(root3);
		Tree tree4 = new Tree(root4);
		Tree tree5 = new Tree(root5);
		
		
		int distance1 = Tree.ZhangShasha(tree1, tree2);
		System.out.println("Expected 2; got " + distance1);
		
		int distance2 = Tree.ZhangShasha(tree5, tree4);
		System.out.println("Expected 1; got " + distance2);
		System.out.println(tree5.labels);
		
		/*
		System.out.println(tree1.labels);
		Monosaccharide c1 = (Monosaccharide) tree1.root.children.get(0);
		System.out.println(root);
		System.out.println(c1);
		System.out.println(((Monosaccharide) c1.children.get(0)).childrenMap);
		System.out.println(c1.children.get(0).children.get(0));
		System.out.println(tree2.labels);
		*/
		
		/*
		//int distance2 = Tree.ZhangShasha(tree3, tree4);
		//System.out.println("Expected 1; got " + distance2);

		//int distance3 = Tree.ZhangShasha(tree5, tree6);
		//System.out.println("Expected 2; got " + distance3);
		
		TreeParallel ptree1 = new TreeParallel(tree1Str1);
		TreeParallel ptree2 = new TreeParallel(tree1Str2);
		
		int pdistance1 = TreeParallel.ZhangShasha(ptree1, ptree2);
		System.out.println("Expected 2; got " + pdistance1);
		*/
	}
}
