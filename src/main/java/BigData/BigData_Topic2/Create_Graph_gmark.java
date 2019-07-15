package BigData.BigData_Topic2;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/* 
 * This method takes as input the graph generated by gmark and returns a list of strings in this case Cypher queries 
 * that will be useful after inserting them in the neo4j database
 * 
 */

public class Create_Graph_gmark {
	
	private File file = null;
	private List<String> result = null;
	private BufferedReader buffer;
	
	public Create_Graph_gmark() {
		this.file = new File("input/test-graph.txt0.txt");
		this.result = new ArrayList<String>();
	}
	
	public void loadFile_buildQuery(int DBs) throws IOException {
		buffer = new BufferedReader(new FileReader(this.file));
		
		int[] DBsArray = new int[DBs];
		for (int i=0;i<DBsArray.length;i++)
			DBsArray[i]=1;
		
		Map<String, String> nodes = new HashMap<>();
		
		String st; 
		while ((st = buffer.readLine()) != null) {
			
			String[] parts = st.split(" ");
			String node1 = parts[0];
			String link = String.valueOf(Math.random()).substring(0,4);
			String node2 = parts[2];
			
			String label1, label2;
			if (!nodes.containsKey(node1)){
				label1 = generateNode(nodes, node1, DBs, DBsArray);
			}else{
				label1 = nodes.get(node1); 
			}
			
			if (!nodes.containsKey(node2)){
				label2 = generateNode(nodes, node2, DBs, DBsArray);
			}else{
				label2 = nodes.get(node2); 
			}
			
			String query = "MERGE (n1:Node {num:'"+label1+"'}) MERGE (n2:Node {num:'"+label2+"'}) CREATE (n1)-[:p0 {v:"+link+"}]->(n2)";
			
			result.add(query);
		}
		
	}
	
	private String generateNode(Map<String, String> nodes, String node1, int DBs, int[] DBsArray) {
		int dbs = (int) (Math.random()*(DBs)+1);
		int[] indexes = new int[DBs];
		for (int i=0;i<DBs;i++){
			indexes[i] = i;
		}
		shuffle(indexes);
		StringBuilder s = new StringBuilder();
		for (int i=0;i<dbs;i++){
			s.append(letterGenerator(indexes[i]));
			s.append(String.valueOf(DBsArray[indexes[i]]));
			DBsArray[indexes[i]]++;
			if (i!=dbs-1){
				s.append(",");
			}
		}
		String label = s.toString();
		nodes.put(node1, label);
		return label;
	}

	private String letterGenerator(int i) {
		StringBuilder s = new StringBuilder();
		String[] letters = {"a","b","c","d","e","f","g","h","i","j","k",
				"l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
		while(i>=0){
			s.append(letters[(i%26)]);
			i-=26;
		}
		return s.toString();
	}

	private void shuffle(int[] indexes) {
		for (int i=0; i<indexes.length; i++){
			int m = (int) (Math.random()*(indexes.length));
			int n = (int) (Math.random()*(indexes.length));
			int temp = indexes[m];
			indexes[m] = indexes[n];
			indexes[n] = temp;
		}
	}

	public List<String> getQuery(){
		return this.result;
	}

}