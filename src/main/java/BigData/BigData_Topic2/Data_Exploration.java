package BigData.BigData_Topic2;

import java.util.*;

import javax.print.attribute.HashAttributeSet;

public class Data_Exploration {

	private List<String> P;

	private Service_Neo4j service;

	public Data_Exploration(Service_Neo4j service) {
		this.service = service;
	}

	/*
	 * Input: Grafo DG(N,E); Path Exploration X = n1, n2, ..., nm; intero k
	 * 
	 */

	public List<String> Algorithm_1(String[] X, int k){
		List<String> A = new ArrayList<String>();
		double s = Double.MIN_VALUE;
		P = this.service.get_Neighbors(X[X.length-1]); //Ritorna tutti i vicini dell'ultimo nodo del Path X

		double maxDPR = this.service.max_Page_Rank_Neo4j();//findMaxDPR();

		for (String o : P) {
			if (Score(o,X,maxDPR) > s) {
				if(A.size() >= k)
					A.remove(A.size()-1);
				A.add(o);
				s = Score(A.get(A.size()-1), X, maxDPR);
				System.out.println("Aggiunto elemento in A: " + A.toString());
			}
		}

		return A;
	}

	private double findMaxDPR() {
		List<String> allNodes = this.service.findAll();
		double max = Double.MIN_VALUE;
		for (String node : allNodes) {
			double result = DPR(node);//this.service.Page_Rank_Neo4j(node);//
			if (max < result)
				max = result;
		}
		return max;
	}

	private double Score(String o, String[] X, double maxDPR) {
		double alpha=0.33, beta=0.33, gamma=0.33;

		return (alpha * nDPR_PageRank(o, maxDPR)) + (beta * Match(o,X)) + (gamma * ADJ(o,X));
	}

	private double nDPR(String o, double max) {
		return DPR(o)/max;
	}
	
	private double nDPR_PageRank(String o, double max) {
		double result = this.service.Page_Rank_Neo4j(o)/max;
		//System.out.println("Node: "+ o + " PageRank: " + result);
		return result;
	}

	private double DPR(String o) {
		double df = 0.85;
		//System.out.println("Node: "+o);
		List<String> neighbors = this.service.get_Neighbors(o);

		double sum = 0.; //da controllare il nodo radice

		for (String neighbor : neighbors) {
			sum += DPR(neighbor) * nDIV(o, neighbor);
		}

		//System.out.println("Node "+ o + " --> "+sum);

		return (1 - df) + df * sum;
	}

	private double nDIV(String u, String v) {
		double div = DIV(u,v);
		double sum = 0.;
		
		for (String i : this.service.get_Neighbors(u)) {
			sum += DIV(u,i);
		}
		
		return div/sum;
	}
	
	private double DIV(String u, String v) {
		Set<String> sU = Sources(u);
		Set<String> sV = Sources(v);
		
		Set<String> s1 = difference(sU, sV);
		Set<String> s2 = difference(sV, sU);
		
		s1.addAll(s2);
		
		return s1.size();
	}
	
	private Set<String> difference(Set<String> s1, Set<String> s2) {
		Set<String> r = new HashSet<>();
		r.clear();
		r.addAll(s1);
		r.removeAll(s2);
		return r;
	}

	private Set<String> Sources(String u) {
		Set<String> result = new HashSet<String>();
		String[] parts = u.split(",");

		for (String part : parts) {
			StringBuilder source = new StringBuilder();
			for (int i=0; i<part.length(); i++) {
				if(Character.isLetter(part.charAt(i)))
					source.append(part.charAt(i));
			}
			
			result.add(source.toString());
		}
		
		return result;
	}

	private double ADJ(String n, String[] X) {
		double a=0.25, b=0.25, c=0.25, d=0.25;
		return a * Loop_Avoid(X,n) + b * Trap_Avoid(X,n) + c * Deroute(X,n) + d * Keep_on_Track(X,n);
	}
	
	private double Loop_Avoid(String[] X, String n) {
		for(int i=0; i<X.length; i++) {
			if (X[i].equals(n))
				return 1/(X.length-(i+1)+1);
		}
		
		return 0.;
	}

	private double Trap_Avoid(String[] X, String n) {
		return localCC(X[X.length-1]) - localCC(n);
	}

	private double localCC(String n) {
		int deg = Deg(n);
		if ((deg == 1) || (deg == 0)) {
			return 0;
		} else {
			return 2 * TriangleCount(n) / (Deg(n) * (Deg(n)-1));
		}
	}

	private int TriangleCount(String n) {
		return this.service.tringlesCount(n);
	}

	private int Deg(String n) {
		return this.service.get_Neighbors(n).size();
	}

	private double Deroute(String[] X, String n) {
		Set<String> nx = new HashSet<String>();
		Set<String> no = new HashSet<String>();
		nx.addAll(this.service.get_Neighbors(X[X.length-1]));
		no.addAll(this.service.get_Neighbors(n));
		nx.retainAll(no);
		return 1 / (nx.size() + 1);
	}

	private double Keep_on_Track(String[] X, String n) {
		Set<String> s1 = Sources(X[0]);
		Set<String> sn = Sources(n);
		Set<String> intersection = new HashSet<String>();
		intersection.addAll(s1);
		intersection.retainAll(sn);
		return intersection.size() / s1.size();
	}

	private double Match(String o, String[] X) {
		return this.service.getWeight(o, X[X.length-1]);
	}

}
