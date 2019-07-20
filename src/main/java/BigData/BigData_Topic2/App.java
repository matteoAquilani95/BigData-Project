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
		if (args.length == 0)
			System.out.println("FAILED : Missing config.properties path");
		else {
			//Read file properties
			System.out.print("Reading properties...");
			Read_Properties rp = new Read_Properties(args[0]); //input/config.properties
			//Create_Graph_gmark cGg = new Create_Graph_gmark("input/social-graph.txt0.txt", rp); //TODO mettere args
			//List<String> queryList = cGg.getQuery();

			Service_Neo4j service = new Service_Neo4j();
			//service.add_Data(queryList);
			Data_Exploration DE = new Data_Exploration(service, rp);
			Metrics met = new Metrics();

			String[] X = rp.getPathX();

			String mode = rp.getMode();
			
			String outputPath = rp.getOutputPath();
			
			int k = rp.getK();

			BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath+"/result-"+mode+".txt"));
			
			System.out.print("Starting...");
			List<String> resultAlgo = new ArrayList<String>();
			
			if(mode.equals("newDPR")) {
				
				resultAlgo = DE.Algorithm_1(X, k);
				
			} else if (mode.equals("standard")) {
				
				resultAlgo = DE.Algorithm_1_Mod(X, k);
				
			}else{
				System.out.println("\nFAILED: Missing DPR mode in config.properties (standard or newDPR)");
			}
			
			if (resultAlgo.size() != 0) {
				System.out.print("Writing results...");
				for (String res : resultAlgo) {
					writer.write("nDDCG('"+res+"'): " + met.nDDCG(X, res, X.length+1)+"\n");
				}
			}
			
			writer.close();
			System.out.println("Finishing...");
			service.closeConnection();

		}
	}
}
