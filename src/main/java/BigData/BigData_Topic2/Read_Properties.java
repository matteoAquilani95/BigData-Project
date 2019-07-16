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

public class Read_Properties {

	private static String fileProperties = "config.properties";

	public static synchronized Map<String,String> getProperties() throws IOException {
		Map<String,String> map = new HashMap<>();
		String result = "";
		Object obj = null;
		Properties props = new Properties();
		props.load(new FileInputStream("input/" + fileProperties));
		Enumeration e = props.keys();
		while (e.hasMoreElements()) {
			obj = e.nextElement();
			result = props.getProperty(obj.toString());
			map.put(obj.toString(),result);
		}
		return map;
	}

	public String getDBs() throws NumberFormatException, IOException {
		String result = "";
		try {
			result = getProperties().get("num_db");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static synchronized void setProperties(String key[], String value[]) throws IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(System.getProperty("user.home") + "/" + fileProperties));
		properties.setProperty(key[0], value[0]);
		properties.setProperty(key[1], value[1]);
		properties.setProperty(key[2], value[2]);
		properties.setProperty(key[3], value[3]);
		properties.store(new FileOutputStream(System.getProperty("user.home") + "/" + fileProperties, false), null);
	}

}
