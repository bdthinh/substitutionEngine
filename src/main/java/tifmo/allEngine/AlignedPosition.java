package tifmo.allEngine;

/**
 * Created by bdthinh on 12/16/14.
 * AlignedPosition is used in DIRTEngine, contains node indexes(begin, end) in text corresponding to node indexs(begin, end) in hypo. Notice that it's applied in dependency tree.
 */
public class AlignedPosition {
	int _beginTokenSource;
	int _endTokenSource;
	int _beginTokenTarget;
	int _endTokenTarget;

	public AlignedPosition() {
		this._beginTokenSource = 0;
		this._endTokenSource = 0;
		this._beginTokenTarget = 0;
		this._endTokenTarget = 0;
	}

	public AlignedPosition(int _beginTokenSource, int _endTokenSource, int _beginTokenTarget, int _endTokenTarget) {
		this._beginTokenSource = _beginTokenSource;
		this._endTokenSource = _endTokenSource;
		this._beginTokenTarget = _beginTokenTarget;
		this._endTokenTarget = _endTokenTarget;
	}

	public int get_beginTokenSource() {

		return _beginTokenSource;
	}

	public void set_beginTokenSource(int _beginTokenSource) {
		this._beginTokenSource = _beginTokenSource;
	}

	public int get_endTokenSource() {
		return _endTokenSource;
	}

	public void set_endTokenSource(int _endTokenSource) {
		this._endTokenSource = _endTokenSource;
	}

	public int get_beginTokenTarget() {
		return _beginTokenTarget;
	}

	public void set_beginTokenTarget(int _beginTokenTarget) {
		this._beginTokenTarget = _beginTokenTarget;
	}

	public int get_endTokenTarget() {
		return _endTokenTarget;
	}

	public void set_endTokenTarget(int _endTokenTarget) {
		this._endTokenTarget = _endTokenTarget;
	}

	public int getLengthSource() {
		return _endTokenSource - _beginTokenSource;
	}

	public int getLengthTarget() {
		return _endTokenTarget - _beginTokenTarget;
	}

	public void printOut() {
		System.out.println("[" + _beginTokenSource + "," + _endTokenSource + "] === [" + _beginTokenTarget + "," + _endTokenTarget + "]");
	}
}
