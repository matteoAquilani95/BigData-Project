package BigData.Main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.spi.DirStateFactory.Result;

public class Read_Properties {
	
	private Map<String,String> mapProp;

	private String fileProperties = "";

	public Read_Properties(String pathFile) throws IOException {
		this.fileProperties = pathFile;
		getProperties();
	}
	
	public synchronized void getProperties() throws IOException {
		mapProp = new HashMap<>();
		String result = "";
		Object obj = null;
		Properties props = new Properties();
		props.load(new FileInputStream(fileProperties));
		Enumeration e = props.keys();
		while (e.hasMoreElements()) {
			obj = e.nextElement();
			result = props.getProperty(obj.toString());
			mapProp.put(obj.toString(),result);
		}
	}

	public String getDBs() throws NumberFormatException, IOException {
		String result = "";
		try {
			result = mapProp.get("num.db").replace(" ", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<String> getParameters_nDPR() throws NumberFormatException, IOException {
		List<String> result = new ArrayList<String>();
		try {
			result.add(mapProp.get("score.alpha").replace(" ", ""));
			result.add(mapProp.get("score.beta").replace(" ", ""));
			result.add(mapProp.get("score.gamma").replace(" ", ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<String> getParameters_ADJ() throws NumberFormatException, IOException {
		List<String> result = new ArrayList<String>();
		try {
			result.add(mapProp.get("Loop_Avoid").replace(" ", ""));
			result.add(mapProp.get("Trap_Avoid").replace(" ", ""));
			result.add(mapProp.get("Deroute").replace(" ", ""));
			result.add(mapProp.get("Keep_on_Track").replace(" ", ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
//	public String[] getPathX() {
//		return mapProp.get("X").toLowerCase().replace(" ", "").split("->");
//	}
	
	public List<String> getPathX(){
		List<String> X = new ArrayList<String>();
		String[] parts = mapProp.get("X").toLowerCase().replace(" ", "").split("->");
		for (int i=0; i<parts.length; i++)
			X.add(parts[i]);
		return X;
	}
	
	public String getInputPathGraph() {
		return mapProp.get("input").replace(" ", "");
	}
	
	public int getK() {
		return Integer.parseInt(mapProp.get("k").replace(" ", ""));
	}
	
	public String getMode() {
		return mapProp.get("DPR").replace(" ", "").toLowerCase();
	}
	
	public String getOutputPath() {
		return mapProp.get("output");
	}
	
	public int getSizeExploration() {
		return Integer.parseInt(mapProp.get("sizeExploration").replace(" ", ""));
	}
	
	public int getBuildGraph() {
		return Integer.parseInt(mapProp.get("build_Graph").replace(" ", ""));
	}
	
	public String getNameDB() {
		return mapProp.get("nameDB");
	}

	public String getPswDB() {
		return mapProp.get("pswDB");
	}

	private synchronized void setProperties(String key[], String value[]) throws IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(System.getProperty("user.home") + "/" + fileProperties));
		properties.setProperty(key[0], value[0]);
		properties.setProperty(key[1], value[1]);
		properties.setProperty(key[2], value[2]);
		properties.setProperty(key[3], value[3]);
		properties.store(new FileOutputStream(System.getProperty("user.home") + "/" + fileProperties, false), null);
	}

}
