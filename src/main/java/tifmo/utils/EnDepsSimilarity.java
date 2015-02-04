package tifmo.utils;

import tifmo.en.EnDEPS;
import tifmo.en.EnFactory;


/**
 * Created by bdthinh on 10/25/14.
 */
public class EnDepsSimilarity extends EnSimilarity {

	public double getSimilarity(String aw, String bw) {
		EnDEPS DEPS = EnFactory.get_DEPS();
		_dim = DEPS.get_dim();
		double[] x = (aw.split(" ").length != 1) ? DEPS.lookUpSequence(aw.split(" ")) : DEPS.lookUp(aw);
		double[] y = (bw.split(" ").length != 1) ? DEPS.lookUpSequence(bw.split(" ")) : DEPS.lookUp(bw);
		if (x != null && y != null)
			return cosine(x, y);
		return -1.0;
	}


}
