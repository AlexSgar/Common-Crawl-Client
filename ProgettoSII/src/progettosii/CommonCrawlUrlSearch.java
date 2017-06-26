package progettosii;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;


/***
 * Utility class to interface with Common Crawl URL index
 * @author Alex,Nicholas
 *
 */
public class CommonCrawlUrlSearch {

	/***
	 * Method that use Common crawl URL Index to get WARC filename and offset with for the pageUrl passed as parameter,
	 * this method is called when the pageUrl is not in current WAT database index because of the correlated WAT is not in file wath.path due to memory optimization
	 * @param pageUrl
	 * @param crawlArchive
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	public static String getWarcInfoFromCommonCrawlURLIndex(String pageUrl,String crawlArchive) throws JSONException, IOException {

		//pageUrl = pageUrl.replaceAll("&","%26");
		//pageUrl = pageUrl.replaceAll("=","%3D");

		String pageUrlEcoded = URLEncoder.encode(pageUrl,"UTF-8");

		String url = "http://index.commoncrawl.org/" + crawlArchive + "-index" + "?url=" + pageUrlEcoded + "&output=json";
		List<String> warcsInfo = new LinkedList<>();
		List<JSONObject> jsons = readJsonFromUrl(url);
		
		for (JSONObject json : jsons) {

			String warcFilename = (String) json.get("filename");
			
			if(!warcFilename.contains("/crawldiagnostics/") && !warcFilename.contains("/robotstxt/")){
				warcsInfo.add(warcFilename +","+json.get("offset"));
			}
		}
		printWarcsInfo(pageUrl,warcsInfo);

		return warcsInfo.get(0);
	}
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	private static List<JSONObject> readJsonFromUrl(String url) throws IOException, JSONException {

		InputStream is = new URL(url).openStream();
		try {

			List<JSONObject> jsons = new LinkedList<JSONObject>();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);

			Pattern p = Pattern.compile("\\{.*\\}");
			Matcher m = p.matcher(jsonText);

			while(m.find())
				jsons.add(new JSONObject(m.group()));
			return jsons;
		} finally {
			is.close();
		}
	}

	private static void printWarcsInfo(String pageUrl,List<String> warcsInfo){

		File f = new File("urls_not_in_WAT_index.txt");
		
		try(PrintWriter pw = new PrintWriter(new FileOutputStream(f,true))){
			
			if(!f.exists()){
				pw.println("warc filename,offset");
				pw.println();
			}
			
			pw.println(pageUrl);

			for (String warcInfo : warcsInfo) {
				pw.println(warcInfo);
			}
			pw.println("=====================\n");
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
}
