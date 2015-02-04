package tifmo.similarityComp;

import tifmo.allEngine.PPIndexRecord;

import java.util.Comparator;

/**
 * Created by bdthinh on 12/2/14.
 */
public class ParaphraseRecordComp implements Comparator<PPIndexRecord> {
	@Override
	public int compare(PPIndexRecord o1, PPIndexRecord o2) {
		if (o1.get_textSent() < o2.get_textSent())
			return 1;
		else if (o1.get_textSent() == o2.get_textSent() && o1.get_textEndTokenNum() < o2.get_textEndTokenNum())
			return 1;
		return -1;
	}
}
