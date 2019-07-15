package BigData.BigData_Topic2;

import java.util.*;

public class Data_Exploration {
	
	List<Object> P;
	
	/*
	 * Input: Grafo DG(N,E); Path Exploration X = n1, n2, ..., nm; intero k
	 * 
	 */
	
	public List<Object> Algorithm_1(List<String> DG,String[] X, int k){
		List<Object> A = new ArrayList<Object>();
		double s = Double.MIN_VALUE;
		P = get_Neighbor(X[X.length-1], DG); //vicini del nodo nm di X
		
		for (Object o : P) {
			if (Score(o,X) > s) {
				if(A.size() > k)
					A.remove(A.size()-1);
				A.add(o);
				s = Score(A.get(A.size()-1), X);
			}
		}
		
		return A;
	}
	
	/*
	 * Ritorna tutti i vicini dell'ultimo nodo del Path X
	 */
	private List<Object> get_Neighbor(String string, List<String> dG) {
		// TODO
		return null;
	}

	private double Score(Object o, String[] X) {
		double alpha=0.33, beta=0.33, gamma=0.33;
		
		return (alpha * nDPR(o)) + (beta * Match(o,X)) + (gamma * ADJ(o,X));
	}

	private int ADJ(Object o, String[] X) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int Match(Object o, String[] X) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int nDPR(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
