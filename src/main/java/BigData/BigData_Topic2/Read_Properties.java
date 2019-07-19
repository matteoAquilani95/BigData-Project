package BigData.BigData_Topic2;

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
			result = mapProp.get("num.db");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<String> getParameters_nDPR() throws NumberFormatException, IOException {
		List<String> result = new ArrayList<String>();
		try {
			result.add(mapProp.get("score.alpha"));
			result.add(mapProp.get("score.beta"));
			result.add(mapProp.get("score.gamma"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<String> getParameters_ADJ() throws NumberFormatException, IOException {
		List<String> result = new ArrayList<String>();
		try {
			result.add(mapProp.get("Loop_Avoid"));
			result.add(mapProp.get("Trap_Avoid"));
			result.add(mapProp.get("Deroute"));
			result.add(mapProp.get("Keep_on_Track"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String[] getPathX() {
		return mapProp.get("X").toLowerCase().replace(" ", "").split("->");
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
