package tifmo.utils;

import tifmo.en.EnFactory;
import tifmo.en.EnGlove;

/**
 * Created by bdthinh on 1/14/15.
 */
public class EnGloveSimilarity extends EnSimilarity {

	public double getSimilarity(String aw, String bw) {
		EnGlove glove = EnFactory.get_glove();
		_dim = glove.get_dim();
		double[] x = (aw.split(" ").length != 1) ? glove.lookUpSequence(aw.split(" ")) : glove.lookUp(aw);
		double[] y = (bw.split(" ").length != 1) ? glove.lookUpSequence(bw.split(" ")) : glove.lookUp(bw);
		if (x != null && y != null)
			return cosine(x, y);
		return -1.0;
	}

}
