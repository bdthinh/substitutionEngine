package tifmo.coreNLP;

import java.util.Comparator;

/**
 * Created by bdthinh on 11/20/14.
 */
public class DepedencyComp implements Comparator<Dependency> {
	@Override
	public int compare(Dependency o1, Dependency o2) {
		if (o1.get_modifier().get_position() < o2.get_modifier().get_position())
			return -1;
		return 1;
	}
}
