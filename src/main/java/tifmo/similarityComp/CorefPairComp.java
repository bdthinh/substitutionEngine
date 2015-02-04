package tifmo.similarityComp;

import tifmo.allEngine.CorefPair;

import java.util.Comparator;

/**
 * Created by bdthinh on 11/7/14.
 */
public class CorefPairComp implements Comparator<CorefPair> {
	@Override
	public int compare(CorefPair o1, CorefPair o2) {
		if (o1.get_from().startIndex < o2.get_from().startIndex)
			return 1;
		return -1;
	}
}
