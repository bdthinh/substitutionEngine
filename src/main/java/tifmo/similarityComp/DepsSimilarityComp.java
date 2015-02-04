package tifmo.similarityComp;

import tifmo.allEngine.FeatureSet;

import java.util.Comparator;

/**
 * Created by bdthinh on 10/24/14.
 */
public class DepsSimilarityComp implements Comparator<FeatureSet> {
	@Override
	public int compare(FeatureSet o1, FeatureSet o2) {
		if (o1.get_deps() < o2.get_deps())
			return 1;
		return -1;
	}
}
