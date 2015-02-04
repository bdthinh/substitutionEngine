package tifmo.allEngine;

/**
 * Created by bdthinh on 10/18/14.
 * PosFeature store the POS and missingPOS along with the side missing (left or right side), which is predefined in PPDB
 */
public class PosFeature {
	String _pos;
	String _missingPos;
	int _onTheSide; //0 no 1 right -1 left

	public PosFeature() {
		this._pos = "";
		this._missingPos = "";
		this._onTheSide = 0;
	}

	public PosFeature(String Tag) {
		this._pos = "";
		this._missingPos = "";
		this._onTheSide = 0;
		Tag = Tag.substring(1, Tag.length() - 1);
		if (Tag.contains("/")) {
			this._pos = Tag.split("/")[0];
			this._missingPos = Tag.split("/")[1];
			this._onTheSide = 1;
		} else if (Tag.contains("\\")) {
			this._pos = Tag.split("\\\\")[0];
			this._missingPos = Tag.split("\\\\")[1];
			this._onTheSide = -1;
		} else {
			this._pos = Tag;
		}
	}

	public String get_pos() {
		return _pos;
	}

	public void set_pos(String _pos) {
		this._pos = _pos;
	}

	public String get_missingPos() {
		return _missingPos;
	}

	public void set_missingPos(String _missingPos) {
		this._missingPos = _missingPos;
	}

	public int get_onTheSide() {
		return _onTheSide;
	}

	public void set_onTheSide(int _onTheSide) {
		this._onTheSide = _onTheSide;
	}

	@Override
	public String toString() {
		if (_onTheSide == 1)
			return "[" + _pos + "/" + _missingPos + "]";
		else if (_onTheSide == -1)
			return "[" + _pos + "\\" + _missingPos + "]";
		return "[" + _pos + "]";
	}
}
