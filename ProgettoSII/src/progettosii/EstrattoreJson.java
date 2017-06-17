/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Rob
 */
public class EstrattoreJson {
	
	private ObjectURL objectURL;
	
	public ObjectURL CreaObjectURL(String json){
		try {
			objectURL = new ObjectURL();

			//estrazione URI
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
			JSONObject jsonObject2 =(JSONObject) jsonObject.get("Envelope");
			JSONObject jsonObject3 =(JSONObject) jsonObject2.get("WARC-Header-Metadata");

			String warcType = (String) jsonObject3.get("WARC-Type");

			if (warcType.equals("response")){
				//estrazione URI
				jsonParser = new JSONParser();
				jsonObject = (JSONObject) jsonParser.parse(json);
				jsonObject2 =(JSONObject) jsonObject.get("Envelope");
				jsonObject3 =(JSONObject) jsonObject2.get("WARC-Header-Metadata");

				String URI = (String) jsonObject3.get("WARC-Target-URI");
				//System.out.println("URI is: " + URI);
				objectURL.setURL(URI);

				//estrazione Actual-Content-Length
				jsonObject2 =(JSONObject) jsonObject.get("Envelope");

				int ActualContentLength = new Integer((String) jsonObject2.get("Actual-Content-Length")).intValue();
				//System.out.println("ActualContentLength is: " + ActualContentLength);
				objectURL.setActualContentLength(ActualContentLength);

				//estrazione segmentWARC
				jsonObject2 =(JSONObject) jsonObject.get("Container");

				String Filename = (String) jsonObject2.get("Filename");
				//System.out.println("Filename is: " + Filename);
				objectURL.setSegmentWARC(Filename);

				//estrazione Offset
				jsonObject2 =(JSONObject) jsonObject.get("Container");

				int Offset = new Integer((String) jsonObject2.get("Offset")).intValue();
				//System.out.println("Offset is: " + Offset);
				objectURL.setOffset(Offset);
				return objectURL;
			}
			else return null;

		} catch (ParseException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
		return objectURL;
	}
}
