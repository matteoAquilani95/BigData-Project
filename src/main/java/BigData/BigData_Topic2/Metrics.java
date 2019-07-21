package BigData.BigData_Topic2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Metrics {
	
	public double nDDCG(List<String> X, String newNode, int r) {
		
		if (r > X.size() + 1)
			return -1;
		
		List<String> ranking = new ArrayList<>();
		for(String node: X) {
			ranking.add(node);
		}
		ranking.add(newNode);
		
		double sum = 0;
//		System.out.println("\nNode: " + newNode + " DDCG ----- ");
		for(int i=0; i<r; i++) {
			sum += (Rel(ranking.get(i),newNode)/(Math.log(i+2)/Math.log(2)));
//			System.out.println(sum);
		}
		
		//System.out.println("Node: "+ newNode + "\n" + sum + " " + IDDCG(ranking,r));
		
		return sum/IDDCG(ranking,r);
	}

	private double IDDCG(List<String> ranking, int r) {
		double[] rel = new double[ranking.size()];
		
		int i = 0;
		for (String rank : ranking) {
			rel[i] = (Rel(rank, ranking.get(ranking.size()-1)));
			i++;
		}
		
		Arrays.sort(rel);
		rel = reverseArray(rel);
		
		double sum = 0.;
//		System.out.println("\nIDDCG --- ");
		for(i=0; i<r; i++) {
			sum += rel[i]/(Math.log(i+2)/Math.log(2));
//			System.out.println(sum);
		}
		
		return sum;
	}

	private double Rel(String n0, String newNode) {
		Set<String> s0 = Sources(n0);
		Set<String> s1 = Sources(newNode);
		Set<String> diff = difference(s0, s1);
		return diff.size();
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
	
	private double[] reverseArray(double[] array) {
		double[] newArray = new double[array.length];
		
		for (int i=0; i<array.length; i++) {
			newArray[i] = array[array.length-(i+1)];
		}
		
		return newArray;
	}

}
