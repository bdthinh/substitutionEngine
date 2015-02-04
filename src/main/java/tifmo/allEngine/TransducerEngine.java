package tifmo.allEngine;

import tifmo.coreNLP.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bdthinh on 12/18/14.
 */
public class TransducerEngine {
	public static Map<Integer, Pair> substitutePairs(String entailmentFile) {
		Map<Integer, Pair> ret = new HashMap<>();
		Map<Integer, String> pairInfo = execute(entailmentFile);
		for (Integer id : pairInfo.keySet()) {
			String pair = pairInfo.get(id);
			String[] splitted = pair.split("\\|\\|");
			ret.put(id, new Pair(splitted[0], splitted[1]));
		}
		return ret;
	}

	public static Map<Integer, String> execute(String entaimentFile) {
		Map<Integer, String> ret = new HashMap<>();
		//TODO
		//Look for output of python process and parse to matched phrases
		return ret;
	}
}
