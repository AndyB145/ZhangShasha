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
		final int INSERT = 1;
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
		DistanceSubArray[][] treeDist = new DistanceSubArray[t1 + 1][t2 + 1];

		for(int i = 0; i < treeDist.length; i++) {
			for(int j = 0; j < treeDist[i].length; j++) {
				treeDist[i][j] = new DistanceSubArray(t1 + 2, t2 + 2);
			}
		}

		// Add elements at position 0 in l1 and l2 so we don't have to reindex every get()
		l1.add(0, -1);
		l2.add(0, -1);

		printL(keyroots1);
		printL(keyroots2);

		System.out.println("\nBegin Phase 1.\n");
		// Phase 1 PARBEGIN
		for (int i = 0; i < keyroots1.size(); i++ ){
			for (int j = 0; j < keyroots2.size(); j++){
				int keyi = keyroots1.get(i);
				int keyj = keyroots2.get(j);

				treeDist[keyi][keyj].tempArray[l1.get(keyi) - 1][l2.get(keyj) - 1] = 0;
			}
		}
		// PAREND

		printA(treeDist[2][2].tempArray);
		printA(treeDist[2][3].tempArray);
		printA(treeDist[3][2].tempArray);
		printA(treeDist[3][3].tempArray);

		System.out.println("\nBegin Phase 2.\n");
		// Phase 2
		for (int k = 0; k <= (t1 - 1); k++){
			// PARBEGIN
			for (int i = 0; i < keyroots1.size(); i++ ){
				for (int j = 0; j < keyroots2.size(); j++){
					int keyi = keyroots1.get(i);
					int keyj = keyroots2.get(j);

					int tempX = l1.get(keyi);
					int tempY = l2.get(keyj);

					treeDist[keyi][keyj].tempArray[tempX + k][tempY - 1] = 
							treeDist[keyi][keyj].tempArray[tempX + k - 1][tempY - 1] + DELETE;
				}
			}
		}
		// PAREND

		printA(treeDist[2][2].tempArray);
		printA(treeDist[2][3].tempArray);
		printA(treeDist[3][2].tempArray);
		printA(treeDist[3][3].tempArray);

		System.out.println("\nBegin Phase 3.\n");

		// Phase 3
		for (int k = 0; k <= (t2 - 1); k++){
			// PARBEGIN
			for (int i = 0; i < keyroots1.size(); i++ ){
				for (int j = 0; j < keyroots2.size(); j++){
					int keyi = keyroots1.get(i);
					int keyj = keyroots2.get(j);

					int tempX = l1.get(keyi);
					int tempY = l2.get(keyj);

					treeDist[keyi][keyj].tempArray[tempX - 1][tempY + k] = 
							treeDist[keyi][keyj].tempArray[tempX - 1][tempY + k - 1] + INSERT;
				}
			}
		}
		// PAREND

		printA(treeDist[2][2].tempArray);
		printA(treeDist[2][3].tempArray);
		printA(treeDist[3][2].tempArray);
		printA(treeDist[3][3].tempArray);

		System.out.println("\nBegin Phase 4.\n");
		for (int k = 0; k <= (t1 + t2 - 2); k++){
			// PARBEGIN
			for (int iprime = 0; iprime < keyroots1.size(); iprime++ ){
				for (int jprime = 0; jprime < keyroots2.size(); jprime++){
					int i = keyroots1.get(iprime);
					int j = keyroots2.get(jprime);

					for (int p = l1.get(i); p <= i; p++) {
						for (int q = l2.get(j); q <= j; q++) {
							if ((p - l1.get(i) + q - l2.get(j)) == k) {
								if (l1.get(i) == l1.get(p) && l2.get(j) == l2.get(q)) {
									//printA(treeDist[i][j].tempArray);

									int a = (treeDist[i][j].tempArray[p-1][q] + DELETE);
									int b = (treeDist[i][j].tempArray[p][q-1] + INSERT);
									int c = (treeDist[i][j].tempArray[p-1][q-1] + RELABEL);

									treeDist[i][j].tempArray[p][q] = Math.min(a, Math.min(b, c)); // minimize over the 3 possible changes
									treeDist[p][q].value = 	treeDist[i][j].tempArray[p][q];
									//printA(treeDist[p][q].tempArray);

								}
								else {
									//printA(treeDist[i][j].tempArray);

									int a = (treeDist[i][j].tempArray[p-1][q] + DELETE);
									int b = (treeDist[i][j].tempArray[p][q-1] + INSERT);
									int c = (treeDist[i][j].tempArray[l1.get(p)-1][l2.get(q)-1] + treeDist[p][q].value);

									treeDist[i][j].tempArray[p][q] = Math.min(a, Math.min(b, c));
									//printA(treeDist[i][j].tempArray);
								}
							}
						}
					}
				}
			}
		}

		printA(treeDist[2][2].tempArray);
		printA(treeDist[2][3].tempArray);
		printA(treeDist[3][2].tempArray);
		printA(treeDist[3][3].tempArray);

		System.out.println(treeDist[t1][t2].value);
		return treeDist[t1][t2].value; // place-holder return value
	}


	/////// De-bugging Print Methods //////
	static <T>void printA (T[][] array){
		for (int i = 0; i < array.length; i++){
			String ps = "";
			for (int j = 0; j < array[0].length; j++){
				ps += "[" + array[i][j] + "]";
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

