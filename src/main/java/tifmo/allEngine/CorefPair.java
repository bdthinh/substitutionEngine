package tifmo.allEngine;

import edu.stanford.nlp.dcoref.CorefChain.CorefMention;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bdthinh on 11/6/14.
 * CorefPair stores indexes of coreference mention between text and hypothesis (in pair).
 */
public class CorefPair {
	CorefMention _from;
	CorefMention _to;

	public CorefPair(CorefMention _from, CorefMention _to) {
		this._from = _from;
		this._to = _to;
	}

	public CorefPair() {
		this._from = null;
		this._to = null;
	}

	/**
	 * Return collection of CorefPair that is suitable for CorefEngine.
	 * The suitable corefPair has to satisfy that itself is not subsumed by other corePairs within the same sentence.
	 *
	 * @param pairs: List&lt;CorefPair&gt;
	 * @return List&lt;CorefPair&gt;
	 * @throws
	 * @author bdthinh
	 */
	public static List<CorefPair> filter(List<CorefPair> pairs) {
		List<CorefPair> ret = new ArrayList<CorefPair>();
		for (int i = 0; i < pairs.size(); i++) {
			boolean flag = true;
			for (int j = 0; j < pairs.size(); j++) {
				if (j != i)
					if (pairs.get(i).get_from().startIndex >= pairs.get(j).get_from().startIndex &&
									pairs.get(i).get_from().endIndex <= pairs.get(j).get_from().endIndex) {
						flag = false;
						break;
					}
			}
			if (flag)
				ret.add(pairs.get(i));
		}
		return ret;
	}

	public CorefMention get_from() {
		return _from;

	}

	public void set_from(CorefMention _from) {
		this._from = _from;
	}

	public CorefMention get_to() {
		return _to;
	}

	public void set_to(CorefMention _to) {
		this._to = _to;
	}


}
