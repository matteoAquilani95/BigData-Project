package BigData.BigData_Topic2;

import java.io.IOException;
import java.util.List;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.Record;
/**
 * Hello world!
 *
 */
public class App 
{
	public static void main( String[] args ) throws IOException
	{
		//Create_Graph_gmark rGg = new Create_Graph_gmark();
		//rGg.loadFile_buildQuery(5);

		//List<String> queryList = rGg.getQuery();

		Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "PasSW0rd"));
		
		try (Session session = driver.session()) {
			/*for (String query : queryList) {
				
				StatementResult rs = session.run(query);
				
			}*/
			String query = "MATCH (n:Node{num:'d4,a4,c3'}) MATCH p=(n)--(m) RETURN m";
			StatementResult rs = session.run(query);
			//System.out.println(rs.keys());
			for (Record record : rs.list()) {
    	    		//Value v = record.get(0);
    	    		//System.out.println("Result get(0): " + v.get("name").asString() + " Record: " + record);
				
					
    	    		System.out.println("Result Record: " + record.get(0).get("num"));
    	    }
		
		
			driver.close();
		}


	}
}
