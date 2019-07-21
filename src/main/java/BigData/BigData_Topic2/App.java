package BigData.BigData_Topic2;

import java.util.ArrayList;
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
		if (!(args.length == 0))
			System.out.println("FAILED : Missing config.properties path");
		else {
			//Read file properties
			System.out.print("Reading properties...");
			Read_Properties rp = new Read_Properties("input/config.properties"); //input/config.properties
			
			//Build graph
			//buildGraphOnNeo4j(rp);
			
			//Start exploration of graph
			initExploration(rp);

		}
	}
	
	private static void buildGraphOnNeo4j(Read_Properties rp) throws NumberFormatException, IOException {
		System.out.print("Starting...");
		Create_Graph_gmark cGg = new Create_Graph_gmark("input/social-graph.txt0.txt", rp);
		List<String> queryList = cGg.getQuery();
		Service_Neo4j service = new Service_Neo4j();
		System.out.print("Adding nodes...");
		service.add_Data(queryList);
		System.out.println("Finishing...");
		service.closeConnection();
		System.out.print("Completed!");
	}
	private static void initExploration(Read_Properties rp) throws NumberFormatException, IOException {
		
		Service_Neo4j service = new Service_Neo4j();
		Data_Exploration DE = new Data_Exploration(service, rp);
		Metrics met = new Metrics();

		List<String> X = rp.getPathX();

		String mode = rp.getMode();
		
		String outputPath = rp.getOutputPath();
		
		int k = rp.getK();
		
		int sizeExploration = rp.getSizeExploration();

		BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath+"/result-"+mode+".txt"));
		
		System.out.print("Starting...");
		
		DE.Explore(X, k, sizeExploration, mode);
		
//		if(mode.equals("newDPR")) {
//			
//			DE.Explore(X, k, sizeExploration);
//			
//		} else if (mode.equals("standard")) {
//			
//			DE.Explore(X, k, sizeExploration);
//			
//		}else{
//			System.out.println("\nFAILED: Missing DPR mode in config.properties (standard or newDPR)");
//		}

		writer.write("Exploration path: \n");
		for (String node : X) {
			writer.write(node +"\n");
		}
		
		System.out.print("Writing results...");
		writer.write("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1)+"\n");
		
		writer.close();
		System.out.println("Finishing...");
		service.closeConnection();
		System.out.print("Completed!");
	}
}
