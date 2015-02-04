package tifmo.similarityComp;

import tifmo.allEngine.FeatureSet;

import java.util.Comparator;

/**
 * Created by bdthinh on 10/20/14.
 */
public class GigaSimilarityComp implements Comparator<FeatureSet> {
	@Override
	public int compare(FeatureSet o1, FeatureSet o2) {
		if (o1.get_giga() < o2.get_giga())
			return 1;
		return -1;
	}

}
