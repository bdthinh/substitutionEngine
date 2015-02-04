package tifmo.allEngine;

import java.util.Comparator;

/**
 * Created by bdthinh on 12/18/14.
 * AlignedPositionComp is used to sort Collection&lt;AlignedPosition&gt; in DESCENDING ORDER.
 */
public class AlignedPositionComp implements Comparator<AlignedPosition> {
	@Override
	public int compare(AlignedPosition o1, AlignedPosition o2) {
		if (o1.getLengthSource() < o2.getLengthSource())
			return 1;
		return -1;
	}
}
