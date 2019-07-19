package BigData.BigData_Topic2;

import java.util.List;
import java.io.IOException;
public class App 
{
	public static void main( String[] args ) throws IOException
	{
		//Read file properties
		Read_Properties rp = new Read_Properties("input/config.properties"); //TODO da usare args
		
//		Create_Graph_gmark cGg = new Create_Graph_gmark("input/social-graph.txt0.txt", rp); //TODO mettere args
//		List<String> queryList = cGg.getQuery();

		Service_Neo4j service = new Service_Neo4j();
//		service.add_Data(queryList);
		Data_Exploration DE = new Data_Exploration(service, rp);
		Metrics met = new Metrics();
		
//		String[] X = {"d4", "v1,d1", "v2,d2", "p1,d3,v3"};
		
		String[] X = rp.getPathX();
		
//		List<String> resultAlgo = DE.Algorithm_1(X, 10);
		List<String> resultAlgo = DE.Algorithm_1_Mod(X, 10);
//		
		for (String res : resultAlgo) {
			System.out.println("nDDCG(["+res+"]): " + met.nDDCG(X, res, X.length+1));
		}
		
//		System.out.println(service.max_Page_Rank_Neo4j());

		service.closeConnection();


	}
}
