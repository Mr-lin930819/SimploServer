package thesis.logic;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonTool {
	public static JSONObject convMap2Json(Map<String,String> data,String nodeName){
		JSONObject dataJson = new JSONObject();
		JSONObject retJson = new JSONObject();
		try {
			for(Map.Entry<String, String> entry:data.entrySet()){
				dataJson.put(entry.getKey(), entry.getValue());	
			}
			retJson.put(nodeName, dataJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retJson;
		
	}

}
