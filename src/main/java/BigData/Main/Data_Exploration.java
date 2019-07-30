package BigData.Main;

import java.io.IOException;
import java.util.*;

import javax.print.attribute.HashAttributeSet;

public class Data_Exploration {

	private List<String> P;
	private List<String> nodes;
	private Map<String,Double> mapDPR;
	private Service_Neo4j service;
	private double alphaRP=0, betaRP=0, gammaRP=0, aRP=0, bRP=0, cRP=0, dRP=0; 

	public Data_Exploration(Service_Neo4j service, Read_Properties readProp) throws NumberFormatException, IOException {
		this.service = service;
		SetParameters(readProp.getParameters_nDPR(), readProp.getParameters_ADJ());
	}
	
	public List<String> Explore(List<String> X, int k, int sizeExploration, String mode){
		List<String> finalPath = new ArrayList<>();
		
		while(sizeExploration>0){
			List<String> A;
			
			if(mode.equals("standard"))
				A = Algorithm_1_Mod(X, k);
			else
				A = Algorithm_1(X, k);
			
			if(A.size() == 0)
				break;
			String bestNode = A.get(A.size()-1);
			if (X.contains(bestNode))
				break;
			X.add(bestNode);
			finalPath.add(bestNode);
			sizeExploration--;
		}
		
		return finalPath;
	}

	/*
	 * Input: Grafo DG(N,E); Path Exploration X = n1, n2, ..., nm; intero k
	 */

	public List<String> Algorithm_1(List<String> X, int k){
		List<String> A = new ArrayList<String>();
		double s = Double.MIN_VALUE;
		P = this.service.get_Neighbors(X.get(X.size()-1)); //Ritorna tutti i vicini dell'ultimo nodo del Path X

		double maxDPR = this.service.max_Page_Rank_Neo4j();

		for (String o : P) {
			if (Score(o,X,maxDPR) > s) {
				if(A.size() >= k)
					A.remove(0);
				A.add(o);
				s = Score(A.get(A.size()-1), X, maxDPR);
				//System.out.println("Aggiunto elemento in A: [" + o + "] Score: " + s);
			}
		}

		return A;
	}
	
	/*
	 * For Daniele's Version
	 * 
	 * Usare -> score2
	 */
	
	public List<String> Algorithm_1_Mod(List<String> X, int k){
		List<String> A = new ArrayList<String>();
		double s = Double.MIN_VALUE;
		P = this.service.get_Neighbors(X.get(X.size()-1)); //Ritorna tutti i vicini dell'ultimo nodo del Path X
		
		if(this.mapDPR == null)
			this.mapDPR = nDPR_Mod();
		
		for (String o : P) {
			if (Score2(o,X) > s) {
				if(A.size() >= k)
					A.remove(0);
				A.add(o);
				s = Score2(A.get(A.size()-1), X);
				//System.out.println("Aggiunto elemento in A: [" + o + "] Score: " + s);
			}
		}

		return A;
	}

	private double Score(String o, List<String> X, double maxDPR) {
		return (this.alphaRP * nDPR_Neo4j(o, maxDPR)) + (this.betaRP * Match(o,X)) + (this.gammaRP * ADJ(o,X));
	}
	
	private double Score2(String o, List<String> X) {
		return (this.alphaRP * this.mapDPR.get(o)) + (this.betaRP * Match(o,X)) + (this.gammaRP * ADJ(o,X));
	}
	
	/*
	 * Neo4j version
	 * 
	 * per trovare il DPR massimo da usare in allegato -> this.service.max_Page_Rank_Neo4j();
	 * 
	 */

	private double nDPR_Neo4j(String o, double max) {
		if(this.alphaRP != 0.0)
			return newDPR(o)/max;
		else
			return 0.;
	}
	
	private double newDPR(String node) {
		double df = 0.85;
		List<String> neighbors = this.service.get_Neighbors(node);
		double sum = 0.;
		for (String ng: neighbors) {
			double aPR = (this.service.Page_Rank_Neo4j(ng) - 1 + df)/df;
			sum += (aPR * nDIV(node, ng));
		}
		
		return (1-df)+df*sum;
	}
	
	/*
	 * Daniele's Version
	 * Da usare con -> score2 nell'algoritmo1_mod
	 * 
	 */

	public Map<String,Double> nDPR_Mod(){

		this.nodes = this.service.findAll();
		Map<String,Double> PRs = pr();

		//cerco il massimo DPR
		double max = Double.MIN_VALUE;
		for (String n: PRs.keySet()){
			double pr = PRs.get(n);
			if (pr>max)
				max = pr;
		}

		//divido tutti i page rank dei nodi per valore massimo
		for (String n: PRs.keySet()){
			double newPR = PRs.get(n)/max;
			PRs.put(n, newPR);
		}

		return PRs;
	}

	private Map<String,Double> pr(){
		
		double df = 0.85;

		//per semplicità il numero di iterazioni è fissato al numero di nodi nel grafo + 1
		int iterations = 20;//this.nodes.size()+1;

		//chiave=nodo, valore=page rank di quel nodo
		//ogni volta viene aggiornata con il nuovo page rank
		Map<String, Double> PRs = new HashMap<String, Double>();

		//inizializzamo i nodi con valori tutti uguali tra loro
		initialization(PRs,this.nodes.size());

		/*finchè non converge */
		while(iterations>0){

			//mappa con i nuovi valori di page rank che vengono calcolati
			Map<String,Double> newPRs = new HashMap<String,Double>();
			//viene calcolato il page rank di ciascun nodo nel grafo
			for(String n: this.nodes){

				//lista dei nodi vicini
				List<String> neighbors = this.service.get_Neighbors(n);
				//per ciascun nodo vicino viene calcolata la sommatoria
				double sum = 0.;
				for (String neighbor: neighbors){
					sum += (PRs.get(neighbor) * nDIV(n,neighbor));
				}
				//calcolo del page rank del nodo e aggiornamento della mappa con i nuovi pr
				double pr = (1. - df) + df * sum;
				newPRs.put(n, pr);
			}

			//aggiornamento di PRs con la mappa calcolata precedentemente
			PRs=newPRs;
			iterations--;
			System.out.println("Iterations left: "+ iterations);
		}

		return PRs;

	}
	
	private void initialization(Map<String,Double> map, int size){
		double initial_pr = 1./size;
		for (String n: this.nodes)
			map.put(n, initial_pr);
	}
	
	/*
	 * 
	 * Old version
	 * per trovare il DPR massimo da usare in allegato -> findMaxDPR();
	 * 
	 */
	
	private double nDPR(String o, double max) {
		return DPR(o)/max;
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
	
	private double findMaxDPR() {
		List<String> allNodes = this.service.findAll();
		double max = Double.MIN_VALUE;
		for (String node : allNodes) {
			double result = DPR(node);
			if (max < result)
				max = result;
		}
		return max;
	}

	private double ADJ(String n, List<String> X) {
		if(this.gammaRP != 0.0)
			return this.aRP * Loop_Avoid(X,n) + this.bRP * Trap_Avoid(X,n) + 
					this.cRP * Deroute(X,n) + this.dRP * Keep_on_Track(X,n);
		else
			return 0.;
	}

	private double Loop_Avoid(List<String> X, String n) {
		for(int i=0; i<X.size(); i++) {
			if (X.get(i).equals(n))
				return 1/(X.size()-(i+1)+1);
		}

		return 0.;
	}

	private double Trap_Avoid(List<String> X, String n) {
		return localCC(X.get(X.size()-1)) - localCC(n);
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

	private double Deroute(List<String> X, String n) {
		Set<String> nx = new HashSet<String>();
		Set<String> no = new HashSet<String>();
		nx.addAll(this.service.get_Neighbors(X.get(X.size()-1)));
		no.addAll(this.service.get_Neighbors(n));
		nx.retainAll(no);
		return 1 / (nx.size() + 1);
	}

	private double Keep_on_Track(List<String> X, String n) {
		Set<String> s1 = Sources(X.get(0));
		Set<String> sn = Sources(n);
		Set<String> intersection = new HashSet<String>();
		intersection.addAll(s1);
		intersection.retainAll(sn);
		return intersection.size() / s1.size();
	}

	private double Match(String o, List<String> X) {
		return this.service.getWeight(o, X.get(X.size()-1));
	}
	
	private void SetParameters(List<String> parameters_nDPR, List<String> parameters_ADJ) {
		this.alphaRP = Double.parseDouble(parameters_nDPR.get(0));
		this.betaRP = Double.parseDouble(parameters_nDPR.get(1));
		this.gammaRP = Double.parseDouble(parameters_nDPR.get(2));
		
		this.aRP = Double.parseDouble(parameters_ADJ.get(0));
		this.bRP = Double.parseDouble(parameters_ADJ.get(1));
		this.cRP = Double.parseDouble(parameters_ADJ.get(2));
		this.dRP = Double.parseDouble(parameters_ADJ.get(3));
		
	}
	
	public void SetParameters2(String[] score, String[] ADJ) {
		this.alphaRP = Double.parseDouble(score[0]);
		this.betaRP = Double.parseDouble(score[1]);
		this.gammaRP = Double.parseDouble(score[2]);
		
		this.aRP = Double.parseDouble(ADJ[0]);
		this.bRP = Double.parseDouble(ADJ[1]);
		this.cRP = Double.parseDouble(ADJ[2]);
		this.dRP = Double.parseDouble(ADJ[3]);
		
	}

}
