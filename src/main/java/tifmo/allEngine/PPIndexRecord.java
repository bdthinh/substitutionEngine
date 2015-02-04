package tifmo.allEngine;

import tifmo.coreNLP.Chunk;

import java.util.List;
import java.util.Map;

/**
 * Created by bdthinh on 12/2/14.
 */
public class PPIndexRecord {
	int _textSent;
	int _textStartTokenNum;
	int _textEndTokenNum;
	int _hypoSent;
	int _hypoStartTokenNum;
	int _hypoEndTokenNum;

	public PPIndexRecord(int _textSent, int _textStartTokenNum, int _textEndTokenNum, int _hypoSent, int _hypoStartTokenNum, int _hypoEndTokenNum) {
		this._textSent = _textSent;
		this._textStartTokenNum = _textStartTokenNum;
		this._textEndTokenNum = _textEndTokenNum;
		this._hypoSent = _hypoSent;
		this._hypoStartTokenNum = _hypoStartTokenNum;
		this._hypoEndTokenNum = _hypoEndTokenNum;
	}

	public PPIndexRecord(Integer[] key, Integer[] value) {
		this._textSent = key[0];
		this._textStartTokenNum = key[1];
		this._textEndTokenNum = key[2];
		this._hypoSent = value[0];
		this._hypoStartTokenNum = value[1];
		this._hypoEndTokenNum = value[2];
	}

	public int get_textSent() {
		return _textSent;
	}

	public void set_textSent(int _textSent) {
		this._textSent = _textSent;
	}

	public int get_textStartTokenNum() {
		return _textStartTokenNum;
	}

	public void set_textStartTokenNum(int _textStartTokenNum) {
		this._textStartTokenNum = _textStartTokenNum;
	}

	public int get_textEndTokenNum() {
		return _textEndTokenNum;
	}

	public void set_textEndTokenNum(int _textEndTokenNum) {
		this._textEndTokenNum = _textEndTokenNum;
	}

	public int get_hypoSent() {
		return _hypoSent;
	}

	public void set_hypoSent(int _hypoSent) {
		this._hypoSent = _hypoSent;
	}

	public int get_hypoStartTokenNum() {
		return _hypoStartTokenNum;
	}

	public void set_hypoStartTokenNum(int _hypoStartTokenNum) {
		this._hypoStartTokenNum = _hypoStartTokenNum;
	}

	public int get_hypoEndTokenNum() {
		return _hypoEndTokenNum;
	}

	public void set_hypoEndTokenNum(int _hypoEndTokenNum) {
		this._hypoEndTokenNum = _hypoEndTokenNum;
	}

	public Integer[] getTextPart() {
		Integer[] ret = {_textSent, _textStartTokenNum, _textEndTokenNum};
		return ret;
	}

	public Integer[] getHypoPart() {
		Integer[] ret = {_hypoSent, _hypoStartTokenNum, _hypoEndTokenNum};
		return ret;
	}

	public void printOut() {
		System.out.println(_textSent + ",[" + _textStartTokenNum + " " + _textEndTokenNum + "] <== "
						+ _hypoSent + ",[" + _hypoStartTokenNum + " " + _hypoEndTokenNum + "]");
	}

	public void printOut(Map<Integer, List<Chunk>> allTextChunk, Map<Integer, List<Chunk>> allHypoChunk) {
		Chunk textChunk = null;
		Chunk hypoChunk = null;
		for(Chunk ck : allTextChunk.get(_textSent))
			if(ck.get_begin() == _textStartTokenNum && ck.get_end() == _textEndTokenNum) {
				textChunk = ck;
				break;
			}
		for(Chunk ck : allHypoChunk.get(_hypoSent))
			if(ck.get_begin() == _hypoStartTokenNum && ck.get_end() == _hypoEndTokenNum) {
				hypoChunk = ck;
				break;
			}

		System.out.println(_textSent + ",[" + _textStartTokenNum + " " + _textEndTokenNum + "] <== "
						+ _hypoSent + ",[" + _hypoStartTokenNum + " " + _hypoEndTokenNum + "] : "
						+ textChunk.get_chunk() + " <== " + hypoChunk.get_chunk());
	}
}
