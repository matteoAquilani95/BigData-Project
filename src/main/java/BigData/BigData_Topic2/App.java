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
			//Create_Graph_gmark cGg = new Create_Graph_gmark("input/social-graph.txt0.txt", rp); //TODO mettere args
			//List<String> queryList = cGg.getQuery();

			Service_Neo4j service = new Service_Neo4j();
			//service.add_Data(queryList);
			Data_Exploration DE = new Data_Exploration(service, rp);
			Metrics met = new Metrics();

			List<String> X = rp.getPathX();

			String mode = rp.getMode();
			
			String outputPath = rp.getOutputPath();
			
			int k = rp.getK();
			
			int sizeExploration = rp.getSizeExploration();

			BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath+"/result-"+mode+".txt"));
//			BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath+"/result2.txt"));
			
			System.out.print("Starting...");
			
			DE.Explore(X, k, sizeExploration, mode);
			
//			if(mode.equals("newDPR")) {
//				
//				DE.Explore(X, k, sizeExploration);
//				
//			} else if (mode.equals("standard")) {
//				
//				DE.Explore(X, k, sizeExploration);
//				
//			}else{
//				System.out.println("\nFAILED: Missing DPR mode in config.properties (standard or newDPR)");
//			}
			
			//TODO stampa path nel writer
			writer.write("Exploration path: \n");
			for (String node : X) {
				writer.write(node +"\n");
			}
			
			System.out.print("Writing results...");
			writer.write("\nnDDCG: " + met.nDDCG(X, X.get(X.size()-1),X.size()+1)+"\n");
			
			writer.close();
			System.out.println("Finishing...");
			service.closeConnection();

		}
	}
}
