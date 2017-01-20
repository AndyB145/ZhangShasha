package zhsh;

import java.io.File;
import java.io.IOException;

////////////////////////////
// TODO: in order to run this code on the glycan structure
// the correct path to the file needs to be added (see line 29 & 30)
////////////////////////////


public class Main {
	public static void main(String[] args) throws IOException {
		// Sample trees (in preorder).
		String tree1Str1 = "f(d(a c(b)) e)";
		String tree1Str2 = "f(c(d(a b)) e)";
		// Distance: 2 (main example used in the Zhang-Shasha paper)

		String tree1Str3 = "a(b(c d) e(f g(i)))";
		String tree1Str4 = "a(b(c d) e(f g(h)))";
		// Distance: 1

		String tree1Str5 = "a(b c)";
		String tree1Str6 = "g(b h)";
		// Distance: 2
		
		//Testing with glycans
		// These take in a GlycoCTCondensed file type
		File hugeGlycan = new File("HugeGlycan");
		File hugeGlycan2 = new File("HugeGlycan2");
	
		// Sequential
		Tree tree1 = new Tree(GlycoCTParser.stringRepresentation(hugeGlycan));
		Tree tree2 = new Tree(GlycoCTParser.stringRepresentation(hugeGlycan2));
		//Tree tree1 = new Tree(tree1Str1);
		//Tree tree2 = new Tree(tree1Str2);

		Tree tree3 = new Tree(tree1Str3);
		Tree tree4 = new Tree(tree1Str4);

		Tree tree5 = new Tree(tree1Str5);
		Tree tree6 = new Tree(tree1Str6);
		
		// Parallel
		Tree Ptree1 = new TreeParallel(GlycoCTParser.stringRepresentation(hugeGlycan));
		Tree Ptree2 = new TreeParallel(GlycoCTParser.stringRepresentation(hugeGlycan2));
		//Tree Ptree1 = new TreeParallel(tree1Str1);
		//Tree Ptree2 = new TreeParallel(tree1Str2);

		Tree Ptree3 = new TreeParallel(tree1Str3);
		Tree Ptree4 = new TreeParallel(tree1Str4);

		Tree Ptree5 = new TreeParallel(tree1Str5);
		Tree Ptree6 = new TreeParallel(tree1Str6);
		
		
		// Sequential 
		long startTime = System.nanoTime();
		int distance1 = Tree.ZhangShasha(tree1, tree2);
		int distance2 = Tree.ZhangShasha(tree3, tree4);
		int distance3 = Tree.ZhangShasha(tree5, tree6);
		long estimatedTime = System.nanoTime() - startTime;
		
		// Parallel
		long startTime2 = System.nanoTime();
		int Pdistance1 = TreeParallel.ZhangShasha(Ptree1, Ptree2);
		int Pdistance2 = TreeParallel.ZhangShasha(Ptree3, Ptree4);
		int Pdistance3 = TreeParallel.ZhangShasha(Ptree5, Ptree6);
		long estimatedTimeP = System.nanoTime() - startTime2;
		
		
		System.out.println("Sequential:");
		System.out.println("(Glycan) Expected 1; got " + distance1);
		System.out.println("Expected 1; got " + distance2);
		System.out.println("Expected 2; got " + distance3);
		System.out.println("Time: " + estimatedTime);
		System.out.println();
		
		System.out.println("Parallel:");
		System.out.println("(Glycan) Expected 1; got " + Pdistance1);
		System.out.println("Expected 1; got " + Pdistance2);
		System.out.println("Expected 2; got " + Pdistance3);
		System.out.println("Time: " + estimatedTimeP);
		System.out.println();

		System.out.println("Speedup: " + (estimatedTime / estimatedTimeP) + "X");
	}
}
