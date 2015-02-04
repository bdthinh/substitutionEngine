package tifmo.allEngine.TheySay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;

public class ResponseParser {
  ResponseParser() {}

  public SentimentDocument outputSentimentDocument(HttpResponse<JsonNode> response, String text) {
	  SentimentDocument ret = null;
	  try {
      JSONObject sentiment = response.getBody().getObject();
		  System.out.println(sentiment.toString());
		  String label = sentiment.getJSONObject("sentiment").getString("label");
		  Double positive = sentiment.getJSONObject("sentiment").getDouble("positive");
		  Double neutral =  sentiment.getJSONObject("sentiment").getDouble("neutral");
		  Double negative = sentiment.getJSONObject("sentiment").getDouble("negative");
		  ret = new SentimentDocument(text,label,positive,neutral,negative);
    } catch (Exception e) {
      e.printStackTrace();
      printResponseError(response);
    }
	  return ret;
  }

  public List<SentimentSentence> outputSentimentSentence(HttpResponse<JsonNode> response, String text) {
    List<SentimentSentence> ret = new ArrayList<>();
	  try {
      JSONArray sentences = response.getBody().getArray();
      StringBuilder print = new StringBuilder();

      for (int i = 0; i < sentences.length(); i++) {
        JSONObject sentence = sentences.getJSONObject(i);
	      String textSentence= sentence.getString("text");
	      String label = sentence.getJSONObject("sentiment").getString("label");
        Double positive = sentence.getJSONObject("sentiment").getDouble("positive");
	      Double neutral =  sentence.getJSONObject("sentiment").getDouble("neutral");
	      Double negative = sentence.getJSONObject("sentiment").getDouble("negative");
				ret.add(new SentimentSentence(textSentence, label, positive, neutral, negative));
      }
      System.out.println(print + "\n");
    } catch (Exception e) {
      e.printStackTrace();
      printResponseError(response);
    }
	  return ret;
  }

  private void printResponseError(HttpResponse<JsonNode> response) {
    System.err.println("Response code: " + response.getStatus());
    System.err.println("Unexpected response: " + response.getBody().toString());
  }
}
