package tifmo.similarityComp;

import tifmo.allEngine.FeatureSet;

import java.util.Comparator;

/**
 * Created by bdthinh on 10/20/14.
 */
public class GoogleSimilarityComp implements Comparator<FeatureSet> {
	@Override
	public int compare(FeatureSet o1, FeatureSet o2) {
		if (o1.get_ngram() < o2.get_ngram())
			return 1;
		return -1;
	}
}
