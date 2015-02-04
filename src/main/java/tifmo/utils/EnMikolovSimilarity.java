package tifmo.utils;

import tifmo.en.EnFactory;
import tifmo.en.EnMikolov;

/**
 * Created by bdthinh on 10/25/14.
 */
public class EnMikolovSimilarity extends EnSimilarity {

	public double getSimilarity(String aw, String bw) {
		EnMikolov mikolov = EnFactory.get_mikolov();
		_dim = mikolov.get_dim();
		double[] x = (aw.split(" ").length != 1) ? mikolov.lookUpSequence(aw.split(" ")) : mikolov.lookUp(aw);
		double[] y = (bw.split(" ").length != 1) ? mikolov.lookUpSequence(bw.split(" ")) : mikolov.lookUp(bw);
		if (x != null && y != null)
			return cosine(x, y);
		return -1.0;
	}
}
