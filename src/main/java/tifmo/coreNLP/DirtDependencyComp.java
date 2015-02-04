package tifmo.coreNLP;

import java.util.Comparator;

/**
 * Created by bdthinh on 12/15/14.
 */
public class DirtDependencyComp implements Comparator<DirtDependency> {
	@Override
	public int compare(DirtDependency o1, DirtDependency o2) {
		if (o1.get_modifierPosition() < o2.get_modifierPosition())
			return -1;
		return 1;
	}
}
