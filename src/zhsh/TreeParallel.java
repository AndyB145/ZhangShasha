package zhsh;

import java.io.IOException;
import java.util.ArrayList;


public class TreeParallel extends Tree{

	// the following constructor handles preorder notation. E.g., f(a b(c))
	public TreeParallel(String s) throws IOException {
		 super(s);
	}
	
	// Constructor for creating custom trees 
	public TreeParallel(Node root){
		super(root);
	}
	
	ArrayList<Integer> x = new ArrayList<Integer>();

	public void lset() {
		super.l();
		printL(super.l);
		for (int i = 0; i < super.l.size(); i++){
			l.set(i, super.l.get(i) - 1);
		}
		printL(super.l);
		
	}

	//////////////////////////
	/// PARALLEL IMPLEMENT ///
	//////////////////////////
	//ExecutorService pool = Executors.newCachedThreadPool();
	
	public static int ZhangShasha(TreeParallel tree1, TreeParallel tree2) {
		final int DELETE = 1;
		final int RELABEL = 1;
		tree1.index();
		tree1.l();
		tree1.keyroots();
		tree1.traverse();
		tree2.index();
		tree2.l();
		tree2.keyroots();
		tree2.traverse();
		
		ArrayList<Integer> l1 = tree1.l;
		ArrayList<Integer> keyroots1 = tree1.keyroots;
		ArrayList<Integer> l2 = tree2.l;
		ArrayList<Integer> keyroots2 = tree2.keyroots;
		int t1 = l1.size();
		int t2 = l2.size();
		
		// space complexity of the algorithm
		// Create an N x M sized array where N = |t1| and M = |t2| (both + 1 for 0-based indexing).
		// This array holds an object, DistanceSubArray, that is the tempArray for computing the value at (n,m)
		DistanceSubArray[][] treeDist = new DistanceSubArray[t1][t2];
		
		printL(keyroots1);
		printL(keyroots2);
		//tree1.lset(); // decrement each element in l (for zero based indexing)
		//tree2.lset(); 
		// Phase 1 PARBEGIN
		for (int i = 0; i < keyroots1.size(); i++ ){
			for (int j = 0; j < keyroots2.size(); j++){
				int keyi = keyroots1.get(i) - 1; // account for zero-based indexing
				int keyj = keyroots2.get(j) - 1;
				System.out.println("Get l of: " + keyi + " and " + keyj);
				System.out.println("Current indexing is: (" + keyi + ", " + keyj + ") [" + (l1.get(keyi) - 1) + ", " + (l2.get(keyj) - 1) + "]");
				treeDist[keyi][keyj] = new DistanceSubArray(t1 + 2, t2 + 2);
				treeDist[keyi][keyj].tempArray[l1.get(keyi) - 1][l2.get(keyj) - 1] = 0;
			}
		}
		// PAREND
		
		System.out.println("\nBegin Phase 2.\n");
		// Phase 2
		for (int k = 0; k <= (t1 - 1); k++){
			// PARBEGIN
			for (int i = 0; i < keyroots1.size(); i++ ){
				for (int j = 0; j < keyroots2.size(); j++){
					int keyi = keyroots1.get(i) - 1; // account for zero-based indexing
					int keyj = keyroots2.get(j) - 1;
					int tempX = l1.get(keyi) + k; //iterating on k
					int tempY = l2.get(keyj) - 1;
					
					System.out.println("Current indexing is: (" + keyi + ", " + keyj + ") [" + tempX + ", " + tempY + "]");
					
					Integer save = treeDist[keyi][keyj].tempArray[tempX][tempY]; 
					System.out.println("Relies on: (" + keyi + ", " + keyj + ") [" + (tempX - 1) + ", " + tempY + "]");
					
					save = treeDist[keyi][keyj].tempArray[tempX - 1][tempY] + DELETE; //Scoring function
					
					// this is the final update, save is for debugging *****
					treeDist[keyi][keyj].tempArray[tempX][tempY] = 
							treeDist[keyi][keyj].tempArray[tempX - 1][tempY] + DELETE;
				}
			}
			System.out.println();
		}
		// PAREND
		System.out.println("\nBegin Phase 3.\n");
		
		// Phase 3
		for (int k = 0; k <= (t2 - 1); k++){
			// PARBEGIN
			for (int i = 0; i < keyroots1.size(); i++ ){
				for (int j = 0; j < keyroots2.size(); j++){
					int keyi = keyroots1.get(i) - 1; //accounting for zero-based indexing
					int keyj = keyroots2.get(j) - 1;
					int tempX = l1.get(keyi) - 1;
					int tempY = l2.get(keyj) + k; //iterating on k
					
					System.out.println("Current indexing is: (" + keyi + ", " + keyj + ") [" + tempX + ", " + tempY + "]");
					Integer save = treeDist[keyi][keyj].tempArray[tempX][tempY]; 
					
					System.out.println("Relies on: (" + keyi + ", " + keyj + ") [" + (tempX) + ", " + (tempY - 1) + "]");
				
					if (keyi == 2 && keyj == 1 && tempX == 1 && tempY == 3){
						printA(treeDist[2][1].tempArray);
					}
					save = treeDist[keyi][keyj].tempArray[tempX][tempY - 1] + DELETE; //Scoring function
					treeDist[keyi][keyj].tempArray[tempX][tempY] = 
							treeDist[keyi][keyj].tempArray[tempX][tempY - 1] + DELETE;
				}
			}
			System.out.println();
		}
		// PAREND
		printA(treeDist);
		System.out.println();
		printA(treeDist[2][1].tempArray);
		
		// Check the condition of key tempArrays
		System.out.println("\nFinal Phase. /////////// Should be correct up until this point. ////////////\n");
		printA(treeDist[1][1].tempArray);
		printA(treeDist[1][2].tempArray);
		printA(treeDist[2][1].tempArray);
		printA(treeDist[2][2].tempArray);
		
		//l1 = tree1.l;
		//l2 = tree2.l;
		//XXX By-hand the algorithm seems correct to this point
		// for 0 -> N + M -2
		for (int k = 0; k <= ((t1 - 1) + (t2 - 1) - 2); k++){
			// PARBEGIN
			for (int iprime = 0; iprime < keyroots1.size(); iprime++ ){
				for (int jprime = 0; jprime < keyroots2.size(); jprime++){
					
					System.out.println("iprime: " + iprime + " jprime: " + jprime);
					
					int i = keyroots1.get(iprime) - 1;
					int j = keyroots2.get(jprime) - 1;
					
					System.out.println("		i: " + i + " j: " + j);
					
					// p and q are i(subscript)1 and j(subscript)1 respectively 
					// implementation of "for i,j satisfying ..." line in the paper
					for (int p = l1.get(i) - 1; p <= i; p++){
						for (int q = l2.get(j) - 1; q <= j; q++){ //XXX the indexing of l() is most likely off
							
							System.out.println("Trying inner with i = " + i + ", j = " + j + ", i1 = " + p + ", j1 = " + q);
							printL(tree1.l);
							System.out.println("l(i) = " + l1.get(i) + " and l(j) = " + l2.get(j));
							
							if ((p - l1.get(i) + q - l2.get(j)) == k){ // k + 2 because the original equation is with +1 indexing
								if (l1.get(i) == l1.get(p) && l2.get(j) == l2.get(q)) {
									
									System.out.println("We MADE IT TO THE INNER [" + i + "," + j + "](" + p + "," + q + "); K = " + k);
									printA(treeDist[1][1].tempArray);
									
									treeDist[i][j].tempArray[p][q] = Math.min( // minimize over the 3 possible changes
											(treeDist[i][j].tempArray[p-1][q] + DELETE), Math.min(
											(treeDist[i][j].tempArray[p][q-1] + DELETE),
											(treeDist[i][j].tempArray[p-1][q-1] + RELABEL)));
									treeDist[p][q].value = 	treeDist[i][j].tempArray[p][q];
								
								} else {
									System.out.println("["+ p + "," + q + "]\n");
									printA(treeDist[i][j].tempArray);
									
									treeDist[i][j].tempArray[p][q] = Math.min( // minimize over the 3 possible changes
											(treeDist[i][j].tempArray[p-1][q] + DELETE), Math.min(
											(treeDist[i][j].tempArray[p][q-1] + DELETE),
											(treeDist[i][j].tempArray[p-1][q-1] + treeDist[p][q].value)));
								}
							}
						}
					}
				}
			}
		}
		System.out.println(treeDist[2][2].value);
		
		printA(treeDist[1][1].tempArray);
		printA(treeDist[1][2].tempArray);
		printA(treeDist[2][1].tempArray);
		printA(treeDist[2][2].tempArray);
		return 1; // place-holder return value
	}
	
	
	/////// De-bugging Print Methods //////
	static <T>void printA (T[][] array){
		for (int i = 0; i < array.length; i++){
			String ps = "";
			for (int j = 0; j < array[0].length; j++){
				ps += "[" + array[j][i] + "]";
			}
			System.out.println(ps);
		}
		System.out.println();
	}
	
	static <T>void printL (ArrayList<T> list){
		String p = "[";
		for (T element : list){
			p += element + ", ";
		}
		System.out.println( p + "]");
	}
	
	
	// Subclass contained in each entry of the treeDist Array. 
	// Wraps an integer array and a value that ultimately represents the score from the tempArray
	static class DistanceSubArray {
		
		public int value;
		public Integer[][] tempArray;
		private int x;
		private int y;

		public DistanceSubArray(int value){
			this.value = value;
			tempArray = null;
		}
			
		public DistanceSubArray(int x, int y){
			this.value = -1;
			this.x = x;
			this.y = y;
			tempArray = new Integer[x][y];
		}
		
		public String toString(){
			return ("(" + x + "," + y + ")");
		}
	}
	
}

