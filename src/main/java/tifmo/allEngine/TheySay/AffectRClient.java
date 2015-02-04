package tifmo.allEngine.TheySay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.*;
import tifmo.coreNLP.Pair;

public class AffectRClient {

	private String baseUrl;
	private String userName;
	private String passWord;

  private ResponseParser responseParser = new ResponseParser();

  final String endPointSentiment = "/sentiment";

  AffectRClient(String baseUrl, String userName, String passWord) {
    this.baseUrl = baseUrl;
    this.userName = userName;
    this.passWord = passWord;
  }

	public void testAPI(Pair pair) {
		try {
			HttpResponse<JsonNode> sentiment = callSentimentDocument(pair.get_text());
			responseParser.outputSentimentDocument(sentiment, pair.get_text());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

  public Map<Integer,List<SentimentSentence>> executeSentimentSentenceAPI(Map<Integer, Pair> pairs) {
	  Map<Integer, List<SentimentSentence>> ret = new HashMap<>();
	  try {
	    for(Integer id : pairs.keySet()) {
		    HttpResponse<JsonNode> sentimentSentences = callSentimentSentence(pairs.get(id).get_text());
		    List<SentimentSentence> sss = responseParser.outputSentimentSentence(sentimentSentences, pairs.get(id).get_text());
		    ret.put(id, sss);
	    }
    } catch (Exception e) {
      e.printStackTrace();
    }
	  return ret;
  }

	public Map<Integer, SentimentDocument> executeSentimentDocumentAPI(Map<Integer, Pair> pairs) {
		Map<Integer, SentimentDocument> ret = new HashMap<>();
		try {
			for(Integer id : pairs.keySet()) {
				HttpResponse<JsonNode> sentiment = callSentimentDocument(pairs.get(id).get_text());
				SentimentDocument sentimentDocument = responseParser.outputSentimentDocument(sentiment, pairs.get(id).get_text());
				ret.put(id, sentimentDocument);
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

  public HttpResponse<JsonNode> call(String payLoad, String endPoint) {
	  try {
		  return Unirest
						  .post(this.baseUrl + endPoint)
						  .basicAuth(this.userName, this.passWord)
						  .header("Accept", "application/json")
						  .header("Content-Type", "application/json")
						  .body(payLoad)
						  .asJson();
	  } catch (UnirestException e) {
		  e.printStackTrace();
	  }
	  return null;
  }

  public HttpResponse<JsonNode> callSentimentDocument(String text) {
	  return call(
					  new JSONObject().put("text", text).toString(),
            endPointSentiment
    );
  }
  public HttpResponse<JsonNode> callSentimentSentence(String text) {
    return call(
            new JSONObject().put("text", text).put("level", "sentence").toString(),
            endPointSentiment
    );
  }
}
