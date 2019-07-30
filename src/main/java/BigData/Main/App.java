package BigData.Main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
public class App 
{
	public static void main( String[] args ) throws IOException
	{
		if ((args.length == 0))
			System.out.println("FAILED : Missing config.properties path");
		else {
			//Read file properties
			System.out.println("Reading properties...");
			Read_Properties rp = new Read_Properties(args[0]); //"input/config.properties"
			
			if(rp.getBuildGraph() == 1) {
				//Build graph
				buildGraphOnNeo4j(rp);
			}
			else {
				//Start exploration of graph
				initExploration(rp);
			}
			
			//Testing
			//testing(rp);

		}
	}

	private static void buildGraphOnNeo4j(Read_Properties rp) throws NumberFormatException, IOException {
		System.out.print("Starting...");
		Create_Graph_gmark cGg = new Create_Graph_gmark(rp.getInputPathGraph(), rp.getDBs());	//"input/social-graph.6k.txt"
		List<String> queryList = cGg.getQuery();
		Service_Neo4j service = new Service_Neo4j(rp.getNameDB(), rp.getPswDB());
		
		System.out.print("Adding nodes...");
		service.add_Data(queryList);
		
		System.out.println("Finishing...");
		service.closeConnection();
		System.out.println("Completed!");
	}
	
	private static void initExploration(Read_Properties rp) throws NumberFormatException, IOException {

		Service_Neo4j service = new Service_Neo4j(rp.getNameDB(), rp.getPswDB());
		Data_Exploration DE = new Data_Exploration(service, rp);
		Metrics met = new Metrics();

		List<String> X = rp.getPathX();

		String mode = rp.getMode();

		String outputPath = rp.getOutputPath();

		int k = rp.getK();

		int sizeExploration = rp.getSizeExploration();

		BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath+"/result-"+mode+".txt"));

		System.out.println("Starting...");

		DE.Explore(X, k, sizeExploration, mode);

		writer.write("Exploration path: \n");
		for (String node : X) {
			writer.write(node +"\n");
		}

		System.out.print("Writing results...");
		writer.write("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1)+"\n");

		writer.close();
		System.out.println("Finishing...");
		service.closeConnection();
		System.out.println("Completed!");
	}
	
	private static void testing(Read_Properties rp) throws IOException {
		
		Service_Neo4j service = new Service_Neo4j(rp.getNameDB(), rp.getPswDB());
		Data_Exploration DE = new Data_Exploration(service, rp);
		Metrics met = new Metrics();

		List<String> X = rp.getPathX();

		String mode = rp.getMode();

		int k = rp.getK();

		int sizeExploration = rp.getSizeExploration();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("output/result-test.txt"));
		
		Map<String,String> param = new HashMap<String, String>();
		param.put("alpha", "1,0,0.5,0.5,0.5,0.5,0.5");
		param.put("beta",  "0,1,0,0,0,0,0");
		param.put("gamma", "0,0,0.5,0.5,0.5,0.5,0.5");
		param.put("a", 	"0,0,1,0,0,0,0.25");
		param.put("b", 	"0,0,0,0,1,0,0.25");
		param.put("c", 	"0,0,0,1,0,0,0.25");
		param.put("d", 	"0,0,0,0,0,1,0.25");
		
		//TODO resettare X ogni volta
		
		System.out.println("------- only DPR -------");
		writer.write("---- only DPR ----\n");
		
		String[] score = {param.get("alpha").split(",")[0], param.get("beta").split(",")[0],
				param.get("gamma").split(",")[0]};
		String[] ADJ = {param.get("a").split(",")[0], param.get("b").split(",")[0],
				param.get("c").split(",")[0], param.get("c").split(",")[0]};
		
		DE.SetParameters2(score, ADJ);
		DE.Explore(X, k, sizeExploration, mode);
		
		stampPath(writer, X);
		
		writer.write("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1)+"\n");
		System.out.println("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1));
		writer.write("\n\n");
		
		System.out.println("------- only Match -------");
		writer.write("---- only Match ----\n");
		
		X = rp.getPathX();
		
		String[] score2 = {param.get("alpha").split(",")[1], param.get("beta").split(",")[1],
				param.get("gamma").split(",")[1]};
		String[] ADJ2 = {param.get("a").split(",")[1], param.get("b").split(",")[1],
				param.get("c").split(",")[1], param.get("c").split(",")[1]};
		
		DE.SetParameters2(score2, ADJ2);
		DE.Explore(X, k, sizeExploration, mode);
		
		stampPath(writer, X);
		
		writer.write("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1)+"\n");
		System.out.println("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1));
		writer.write("\n\n");
		
		System.out.println("------- DPR + LOOP_Avoid -------");
		writer.write("---- DPR + LOOP_Avoid ----\n");
		
		X = rp.getPathX();
		
		String[] score3 = {param.get("alpha").split(",")[2], param.get("beta").split(",")[2],
				param.get("gamma").split(",")[2]};
		String[] ADJ3 = {param.get("a").split(",")[2], param.get("b").split(",")[2],
				param.get("c").split(",")[2], param.get("c").split(",")[2]};
		
		DE.SetParameters2(score3, ADJ3);
		DE.Explore(X, k, sizeExploration, mode);
		
		stampPath(writer, X);
		
		writer.write("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1)+"\n");
		System.out.println("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1));
		writer.write("\n\n");
		
		System.out.println("------- DPR + DEROUTE -------");
		writer.write("---- DPR + DEROUTE ----\n");
		
		X = rp.getPathX();
		
		String[] score4 = {param.get("alpha").split(",")[3], param.get("beta").split(",")[3],
				param.get("gamma").split(",")[3]};
		String[] ADJ4 = {param.get("a").split(",")[3], param.get("b").split(",")[3],
				param.get("c").split(",")[3], param.get("c").split(",")[3]};
		
		DE.SetParameters2(score4, ADJ4);
		DE.Explore(X, k, sizeExploration, mode);
		
		stampPath(writer, X);
		
		writer.write("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1)+"\n");
		System.out.println("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1));
		writer.write("\n\n");
		
		System.out.println("------- DPR + Trap_Avoid -------");
		writer.write("---- DPR + Trap_Avoid ----\n");
		
		X = rp.getPathX();
		
		String[] score5 = {param.get("alpha").split(",")[4], param.get("beta").split(",")[4],
				param.get("gamma").split(",")[4]};
		String[] ADJ5 = {param.get("a").split(",")[4], param.get("b").split(",")[4],
				param.get("c").split(",")[4], param.get("c").split(",")[4]};
		
		DE.SetParameters2(score5, ADJ5);
		DE.Explore(X, k, sizeExploration, mode);
		
		stampPath(writer, X);
		
		writer.write("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1)+"\n");
		System.out.println("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1));
		writer.write("\n\n");
		
		System.out.println("------- DPR + Keep on Track -------");
		writer.write("---- DPR + Keep on Track ----\n");
		
		X = rp.getPathX();
		
		String[] score6 = {param.get("alpha").split(",")[5], param.get("beta").split(",")[5],
				param.get("gamma").split(",")[5]};
		String[] ADJ6 = {param.get("a").split(",")[5], param.get("b").split(",")[5],
				param.get("c").split(",")[5], param.get("c").split(",")[5]};
		
		DE.SetParameters2(score6, ADJ6);
		DE.Explore(X, k, sizeExploration, mode);
		
		stampPath(writer, X);
		
		writer.write("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1)+"\n");
		System.out.println("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1));
		writer.write("\n\n");
		
		System.out.println("------- DPR + ADJ -------");
		writer.write("---- DPR + ADJ ----\n");
		
		X = rp.getPathX();
		
		String[] score7 = {param.get("alpha").split(",")[6], param.get("beta").split(",")[6],
				param.get("gamma").split(",")[6]};
		String[] ADJ7 = {param.get("a").split(",")[6], param.get("b").split(",")[6],
				param.get("c").split(",")[6], param.get("c").split(",")[6]};
		
		DE.SetParameters2(score7, ADJ7);
		DE.Explore(X, k, sizeExploration, mode);
		
		stampPath(writer, X);
		
		writer.write("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1)+"\n");
		System.out.println("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1));
		writer.write("\n\n");
		
		writer.close();
		service.closeConnection();
	}
	
	private static void stampPath(BufferedWriter writer, List<String> X) throws IOException {
		writer.write("Exploration path: \n");
		//System.out.println("Exploration path: \n");
		for (String node : X) {
			//System.out.println(node +"\n");
			writer.write(node +"\n");
		}
	}
}
