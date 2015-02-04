package tifmo.coreNLP;

/**
 * Created by bdthinh on 1/22/15.
 */
public class MSPair {
	public MSPair(int _quality, int _idLeft, int _idRight, String _text, String _hypo) {
		this._hypo = _hypo;
		this._idLeft = _idLeft;
		this._idRight = _idRight;
		this._text = _text;
		this._quality = _quality;
	}

	public String get_hypo() {
		return _hypo;
	}

	public void set_hypo(String _hypo) {
		this._hypo = _hypo;
	}

	public int get_idLeft() {
		return _idLeft;
	}

	public void set_idLeft(int _idLeft) {
		this._idLeft = _idLeft;
	}

	public int get_idRight() {
		return _idRight;
	}

	public void set_idRight(int _idRight) {
		this._idRight = _idRight;
	}

	public String get_text() {
		return _text;
	}

	public void set_text(String _text) {
		this._text = _text;
	}

	public int get_quality() {
		return _quality;
	}

	public void set_quality(int _quality) {
		this._quality = _quality;
	}

	int _idLeft;
	int _idRight;
	String _text;
	String _hypo;
	int _quality;

	public Pair convertToPair(int id) {
		return (new Pair(id, _text, _hypo, (_quality == 1) ? "YES" : "NO", "PPD", "MSRP"));
	}
}
