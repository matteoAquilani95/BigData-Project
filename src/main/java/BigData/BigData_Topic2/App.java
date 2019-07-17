package BigData.BigData_Topic2;

import java.util.List;
import java.io.IOException;
public class App 
{
	public static void main( String[] args ) throws IOException
	{
//		Create_Graph_gmark cGg = new Create_Graph_gmark();
//		cGg.loadFile_buildQuery();
//		List<String> queryList = cGg.getQuery();

		Service_Neo4j service = new Service_Neo4j();
//		service.add_Data(queryList);
		Data_Exploration DE = new Data_Exploration(service);
		Metrics met = new Metrics();
		
//		String[] X = {"d4", "v1,d1", "v2,d2", "p1,d3,v3"};
		
		String[] X = {"g256,b288,f261,d285,c298,e276,a292", "f3", "d637,e629,f590,c658,g613,a690,b666", "a103,e95"};
		
		List<String> resultAlgo = DE.Algorithm_1(X, 10);
		
		for (String res : resultAlgo) {
			System.out.println(met.nDDCG(X, res, X.length+1));
		}
		
//	
//		String node = "v1,d1";
//		String node2 = "v2,d2";
//		System.out.println(service.getWeight(node, node2));
//
		service.closeConnection();


	}
}
