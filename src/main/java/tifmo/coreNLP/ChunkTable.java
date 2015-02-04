package tifmo.coreNLP;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bdthinh on 11/5/14.
 */
public class ChunkTable {
	private Map<Integer[], String> _table = new HashMap<Integer[], String>();

	public Map<Integer[], String> get_table() {
		return _table;
	}

	public ChunkTable set_table(List<Chunk> chunks) {
		for (Chunk ck : chunks) {
			Integer[] offset = new Integer[2];
			offset[0] = ck.get_begin();
			offset[1] = ck.get_end();
			this._table.put(offset, ck.get_tag());
		}
		return this;
	}
}
