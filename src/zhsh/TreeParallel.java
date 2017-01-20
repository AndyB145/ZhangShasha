package zhsh;

import java.io.IOException;
import java.util.ArrayList;

import java.util.concurrent.*;

public class TreeParallel extends Tree{

	// the following constructor handles preorder notation. E.g., f(a b(c))
	public TreeParallel(String s) throws IOException {
		super(s);
	}

	// Constructor for creating custom trees 
	public TreeParallel(Node root){
		super(root);
	}

	
	//////////////////////////
	/// PARALLEL IMPLEMENT ///
	//////////////////////////
	static ExecutorService pool = Executors.newCachedThreadPool();

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
		ArrayList<Future<?>> futures = new ArrayList<Future<?>>();

		int t1 = l1.size();
		int t2 = l2.size();
		
		// Add elements at position 0 in l1 and l2 so we don't have to re-index every get()
		l1.add(0, -1);
		l2.add(0, -1);

		// space complexity of the algorithm
		// Create an N x M sized array where N = |t1| and M = |t2| (both + 1 for 0-based indexing).
		// This array holds an object, DistanceSubArray, that is the tempArray for computing the value at (n,m)
		DistanceSubArray[][] treeDist = new DistanceSubArray[t1 + 1][t2 + 1];

		for(int i = 0; i < treeDist.length; i++) {
			for(int j = 0; j < treeDist[i].length; j++) {
				treeDist[i][j] = new DistanceSubArray(t1 + 2, t2 + 2);
			}
		}

		
		///////////////
		// Phase 1 
		// PARBEGIN
		for (int i = 0; i < keyroots1.size(); i++ ){
			for (int j = 0; j < keyroots2.size(); j++){

				// can't pass mutable indexes to the task
				final int ii = i;
				final int jj = j;

				// Create and submit task to the thread pool
				futures.add(pool.submit(new Runnable() {
					public void run() {

						int keyi = keyroots1.get(ii);
						int keyj = keyroots2.get(jj);

						treeDist[keyi][keyj].tempArray[l1.get(keyi) - 1][l2.get(keyj) - 1] = 0;
					}
				}));
			}
		}
		
		// Sync tasks after initialization
		while (!futures.isEmpty()) {
			try {
				futures.remove(0).get();
			}
			catch(Exception e) {}
			
		}
		// PAREND
		
		//////////////
		// Phase 2
		for (int k = 0; k <= (t1 - 1); k++){
			
			// PARBEGIN
			for (int i = 0; i < keyroots1.size(); i++ ){
				for (int j = 0; j < keyroots2.size(); j++){
					
					// tasks need non-mutable ints 
					final int ii = i;
					final int jj = j;
					final int kk = k;
					

					// Create and submit task to the thread pool
					futures.add(pool.submit(new Runnable() {
						public void run() {

							int keyi = keyroots1.get(ii);
							int keyj = keyroots2.get(jj);

							int tempX = l1.get(keyi);
							int tempY = l2.get(keyj);

							treeDist[keyi][keyj].tempArray[tempX + kk][tempY - 1] = 
									treeDist[keyi][keyj].tempArray[tempX + kk - 1][tempY - 1] + DELETE;

						}
					}));

				}
			}
			
			// Sync tasks after each wave of k
			while (!futures.isEmpty()) {
				try {
					futures.remove(0).get();
				}
				catch(Exception e) {} // ignore
			}
			// PAREND
		}
		
		
		/////////////
		// Phase 3
		for (int k = 0; k <= (t2 - 1); k++){
			
			// PARBEGIN
			for (int i = 0; i < keyroots1.size(); i++ ){
				for (int j = 0; j < keyroots2.size(); j++){
					
					// tasks need non-mutable ints
					final int ii = i;
					final int jj = j;
					final int kk = k;

					// Create and submit task to the thread pool
					futures.add(pool.submit(new Runnable() {
						public void run() {

							int keyi = keyroots1.get(ii);
							int keyj = keyroots2.get(jj);

							int tempX = l1.get(keyi);
							int tempY = l2.get(keyj);

							treeDist[keyi][keyj].tempArray[tempX - 1][tempY + kk] = 
									treeDist[keyi][keyj].tempArray[tempX - 1][tempY + kk - 1] + INSERT;

						}
					}));
				}
			}
			
			// Sync tasks after each wave of k
			while (!futures.isEmpty()) {
				try {
					futures.remove(0).get();
				}
				catch(Exception e) {} // ignore
			}
			// PAREND
		}

		/////////////
		// Phase 4
		for (int k = 0; k <= (t1 + t2 - 2); k++){
			
			// PARBEGIN
			for (int iprime = 0; iprime < keyroots1.size(); iprime++ ){
				for (int jprime = 0; jprime < keyroots2.size(); jprime++){
					
					// tasks need non-mutable ints
					final int ii = iprime;
					final int jj = jprime;
					final int kk = k;
					
					// Create and submit task to the thread pool
					futures.add(pool.submit(new Runnable() {
						public void run() {

							int i = keyroots1.get(ii);
							int j = keyroots2.get(jj);

							for (int p = l1.get(i); p <= i; p++) {
								for (int q = l2.get(j); q <= j; q++) {
									if ((p - l1.get(i) + q - l2.get(j)) == kk) {
										if (l1.get(i) == l1.get(p) && l2.get(j) == l2.get(q)) {

											int a = (treeDist[i][j].tempArray[p-1][q] + DELETE);
											int b = (treeDist[i][j].tempArray[p][q-1] + INSERT);
											int c = (treeDist[i][j].tempArray[p-1][q-1] + relabel(tree1.labels.get(p-1), tree2.labels.get(q-1)));

											treeDist[i][j].tempArray[p][q] = Math.min(a, Math.min(b, c)); // minimize over the 3 possible changes
											treeDist[p][q].value = 	treeDist[i][j].tempArray[p][q];

										}
										else {

											int a = (treeDist[i][j].tempArray[p-1][q] + DELETE);
											int b = (treeDist[i][j].tempArray[p][q-1] + INSERT);
											int c = (treeDist[i][j].tempArray[l1.get(p)-1][l2.get(q)-1] + treeDist[p][q].value);

											treeDist[i][j].tempArray[p][q] = Math.min(a, Math.min(b, c));
										}
									}
								}
							}
						}
					}));
				}
			}
			// Sync tasks after each wave of k
			while (!futures.isEmpty()) {
				try {
					futures.remove(0).get();
				}
				catch(Exception e) {} // ignore
			}
			// PAREND
		}

		pool.shutdown();
		return treeDist[t1][t2].value; 
	}
	
	
	// Scoring method that compares 2 strings
	// returns 0 if identical, 1 if different 
	public static int relabel (String x, String y){
		 
		if (x.equals(y)){
			 return 0;
		 }else{
			 return 1;
		 }
	}

	
	/////// Debugging Print Methods //////
	
	// Formated print for Arrays
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

	// Formatted print for Lists
	static <T>void printL (ArrayList<T> list){
		String p = "[";
		for (T element : list){
			p += element + ", ";
		}
		System.out.println( p + "]");
	}


	// Subclass contained in each entry of the treeDist Array. 
	// Wraps an integer array and a value that ultimately represents the score calculated from the tempArray
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

