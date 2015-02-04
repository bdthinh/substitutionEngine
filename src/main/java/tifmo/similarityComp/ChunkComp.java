package tifmo.similarityComp;

import tifmo.coreNLP.Chunk;

import java.util.Comparator;

/**
 * Created by bdthinh on 12/2/14.
 */
public class ChunkComp implements Comparator<Chunk> {
	@Override
	public int compare(Chunk o1, Chunk o2) {
		if (o1.get_end() < o2.get_end())
			return -1;
		else if (o1.get_end() == o2.get_end() && o1.get_begin() >= o2.get_begin())
			return -1;
		return 1;
	}
}
