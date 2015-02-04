package tifmo.allEngine.TheySay;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mashape.unirest.http.Unirest;
import tifmo.coreNLP.Pair;

public class SentimentEngine {


	private static String _baseUrl = "https://api.theysay.io/v1";
	private static String _userName = "bdthinh@fit.hcmus.edu.vn";
	private static String _passWord = "Yohtohzait9o";
	private static String _otherUserName = "jinsherlock@gmail.com";
	private static String _otherPassWord = "jahR2yoo9AiN";

	public static void main(String[] args) {
    try {
	    AffectRClient apiClient = new AffectRClient(_baseUrl, _userName, _passWord);
	    String text = "I like it.";
	    String hypo = "Robert Tuttle works for the BBC.";
	    Pair pair = new Pair(text, hypo);

	    apiClient.testAPI(pair);
      Unirest.shutdown();

	    System.out.println("Finished.");
      System.exit(0);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

	public static Map<Integer, String> getTendency(Map<Integer, Pair> pairs){
		Map<Integer, String> ret = new HashMap<>();
		try {
			AffectRClient apiClient = new AffectRClient(_baseUrl, _userName, _passWord);
			Map<Integer, SentimentDocument> sentiments = apiClient.executeSentimentDocumentAPI(pairs);

			for(Integer id : sentiments.keySet()){
				if(sentiments.get(id).get_label().equals("NEGATIVE"))
					ret.put(id, id + ",false" + "," + sentiments.get(id).toString());
				else
					ret.put(id, id+",true" + "," + sentiments.get(id).toString());
			}
			System.out.println("Finished.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

}
