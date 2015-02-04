package tifmo.utils;

import tifmo.en.EnDEPS;
import tifmo.en.EnDEPSContext;
import tifmo.en.EnDEPSContextInverse;
import tifmo.en.EnFactory;

/**
 * Created by bdthinh on 12/27/14.
 */
public class EnDepsContextSimilarity extends EnSimilarity {
	public double getSimilarity(String aw, String bw, String ar, String br) {
		double[] x;
		double[] y;
		EnDEPSContext enDEPSContext = EnFactory.get_DEPSContext();
		EnDEPSContextInverse enDEPSContextInverse = EnFactory.get_DEPSContextInverse();
		_dim = enDEPSContext.get_dim();
		if(ar.charAt(ar.length()-1) == 'I'){
			x = enDEPSContextInverse.lookUp(ar + "_" + aw);
		}
		else {
			x = enDEPSContext.lookUp(ar + "_" + aw);
		}
		if(br.charAt(ar.length()-1) == 'I'){
			y = enDEPSContextInverse.lookUp(ar + "_" + aw);
		}
		else {
			y = enDEPSContext.lookUp(ar + "_" + aw);
		}
		if (x != null && y != null)
			return cosine(x, y);
		return -1.0;
	}
}
