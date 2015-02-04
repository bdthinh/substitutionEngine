package tifmo.utils;

import tifmo.en.EnFactory;
import tifmo.en.EnTurian;

/**
 * Created by bdthinh on 10/25/14.
 */
public class EnTurianSimilarity extends EnSimilarity {

	public double getSimilarity(String aw, String bw) {
		EnTurian turian = EnFactory.get_turian();
		_dim = turian.get_dim();
		double[] x = (aw.split(" ").length != 1) ? turian.lookUpSequence(aw.split(" ")) : turian.lookUp(aw);
		double[] y = (bw.split(" ").length != 1) ? turian.lookUpSequence(bw.split(" ")) : turian.lookUp(bw);
		if (x != null && y != null)
			return cosine(x, y);
		return -1.0;
	}
}
