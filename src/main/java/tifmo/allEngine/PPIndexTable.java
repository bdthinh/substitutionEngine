package tifmo.allEngine;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import tifmo.similarityComp.ParaphraseRecordComp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bdthinh on 11/24/14.
 */
public class PPIndexTable {
	private Multimap<Integer[], Integer[]> _table;

	public PPIndexTable() {
		this._table = ArrayListMultimap.create();
	}

	public PPIndexTable(Multimap<Integer[], Integer[]> _table) {
		this._table = ArrayListMultimap.create(_table);
	}

	public Multimap<Integer[], Integer[]> get_table() {
		return _table;
	}

	public void set_table(Multimap<Integer[], Integer[]> _table) {
		this._table = _table;
	}

	public void put(Integer[] kkey, Integer[] vvalue){
		int textSent = kkey[0];
		int startTokenT = kkey[1];
		int endTokenT = kkey[2];
		int hypoSent = vvalue[0];
		int startToken = vvalue[1];
		int endToken = vvalue[2];
		this.put(textSent,startTokenT,endTokenT,hypoSent,startToken,endToken);
	}

	public void put(int textSent, int startTokenT, int endTokenT, int hypoSent, int startToken, int endToken) {
		boolean flag = true;
		for (Integer[] key : _table.keySet()) {
			if (key[0] == textSent && key[1] == startTokenT && key[2] == endTokenT) {
				flag = false;
				for (Integer[] value : _table.get(key)) {
					if ((hypoSent < value[0]) || (hypoSent == value[0] && startTokenT <= value[1] && value[2] <= endTokenT)) {
						Integer[] tmp = new Integer[]{hypoSent, startToken, endToken};
						_table.put(key, tmp);
						_table.remove(key, value);
						return;
					}
				}
			}
		}
		if (flag) {
			Integer[] key = new Integer[]{textSent, startTokenT, endTokenT};
			Integer[] value = new Integer[]{hypoSent, startToken, endToken};
			_table.put(key, value);
		}
	}

	public void filterLongestPP() {
		Multimap<Integer[],Integer[]> needToFilter = ArrayListMultimap.create();

		for (Integer[] keyI : _table.keySet()) {
			for (Integer[] keyJ : _table.keySet()) {
				if (keyI == keyJ)
					continue;
				if (keyI[0] == keyJ[0] && keyI[1] <= keyJ[1] && keyJ[2] <= keyI[2]) {
					for (Integer[] value : _table.get(keyJ))
						needToFilter.put(keyJ, value);
				}
			}
		}
		for (Integer[] key : needToFilter.keySet()) {
			for (Integer[] value : needToFilter.get(key)) {
				_table.remove(key, value);
			}
		}
		needToFilter.clear();

		for (Integer[] keyI : _table.keySet()) {
			for (Integer[] keyJ : _table.keySet()) {
				if (keyI == keyJ)
					continue;
				if (keyI[0] == keyJ[0] && keyI[2] == keyJ[1])
					for (Integer[] value : _table.get(keyJ))
						needToFilter.put(keyJ, value);
			}
		}

		for (Integer[] key : needToFilter.keySet()) {
			for (Integer[] value : needToFilter.get(key)) {
				_table.remove(key, value);
			}
		}
	}

	public void filterShortestParaphrase(){
		Multimap<Integer[],Integer[]> needToFilter = ArrayListMultimap.create();

		for (Integer[] keyI : _table.keySet()) {
			for (Integer[] keyJ : _table.keySet()) {
				if (keyI == keyJ)
					continue;
				if (keyI[0] == keyJ[0] && keyJ[1] <= keyI[1] && keyI[2] <= keyJ[2]) {
					for (Integer[] value : _table.get(keyJ))
						needToFilter.put(keyJ, value);
				}
				if (keyI[0] == keyJ[0] && keyI[2] == keyJ[1])
					for (Integer[] value : _table.get(keyJ))
						needToFilter.put(keyJ, value);
			}
		}
		for (Integer[] key : needToFilter.keySet()) {
			for (Integer[] value : needToFilter.get(key)) {
				_table.remove(key, value);
			}
		}
	}

	public List<PPIndexTable> filterKeepShorterParaphrase() {
		List<PPIndexTable> ret = new ArrayList<>();
		//TODO
		return ret;
	}

	public List<PPIndexRecord> sort() {
		List<PPIndexRecord> ret = new ArrayList<PPIndexRecord>();
		for (Integer[] key : _table.keySet()) {
			for (Integer[] value : _table.get(key)) {
				PPIndexRecord ppr = new PPIndexRecord(key, value);
				ret.add(ppr);
			}
		}
		Collections.sort(ret, new ParaphraseRecordComp());
		return ret;
	}

	public void printOut() {
		for (Integer[] key : _table.keySet()) {
			for (Integer[] value : _table.get(key))
				System.out.println(key[0] + ",[" + key[1] + " " + key[2] + "] == " + value[0] + ",[" + value[1] + " " + value[2] + "]");
		}
	}
}
