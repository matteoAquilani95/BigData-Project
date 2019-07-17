package BigData.BigData_Topic2;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

public class Service_Neo4j {

	private Driver driver;

	public Service_Neo4j() {
		driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "PasSW0rd"));
	}

	public void add_Data(List<String> queryList) {
		try (Session session = driver.session()) {
			for (String query : queryList) {
				session.run(query);
			}

			session.close();
		}
	}

	public List<Record> find_Data(String query){
		
		List<Record> result;
		
		try (Session session = driver.session()) {
			StatementResult rs = session.run(query);
			result = rs.list();
			session.close();
		}
		
		return result;
	}
	
	public List<String> get_Neighbors(String node){
		List<String> result = new ArrayList<String>();
		
		String query = "MATCH (n:Node{num:'"+node+"'}) MATCH (n)-->(m) return m";
		
		try (Session session = driver.session()) {
			StatementResult rs = session.run(query);
			
			for (Record record : rs.list()) {
				//System.out.println("Result Record: " + record.get(0).get("num"));
				result.add(record.get(0).get("num").asString());
			}
			session.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public List<String> findAll(){
		List<String> result = new ArrayList<String>();
		
		String query = "MATCH (n:Node) return n";
		
		try (Session session = driver.session()) {
			StatementResult rs = session.run(query);
			for (Record record : rs.list()) {
				//System.out.println("Result Record: " + record.get(0).get("num"));
				result.add(record.get(0).get("num").asString());
			}
			
			session.close();
		}
		
		return result;
	}
	
	public int tringlesCount(String node) {
		int result = 0;
		String query = "CALL algo.triangleCount.stream('Node', 'p0', {concurrency:4}) " + 
						"YIELD nodeId, triangles " + 
						"WHERE algo.asNode(nodeId).name = '"+node+"' " + 
						"RETURN algo.asNode(nodeId).name AS name, triangles";
		
		try (Session session = driver.session()) {
			StatementResult rs = session.run(query);
			for (Record record : rs.list()) {
				result = Integer.parseInt(record.get("triangles").asString());
			}
			
			session.close();
		}
		
		return result;
	}
	
	public Driver getDriver() {
		return this.driver;
	}
	
	public void closeConnection() {
		driver.close();
	}

	public double getWeight(String n1, String n2) {
		double result = 0;
		String query = "MATCH (n1:Node{num:'"+n1+"'})-[rel:p0]-(n2:Node{num:'"+n2+"'}) return rel.v as weight";
		try (Session session = driver.session()) {
			StatementResult rs = session.run(query);
			for (Record record : rs.list()) {
				result = record.get("weight").asDouble();
			}
			
			session.close();
		}
		return result;
	}

}
