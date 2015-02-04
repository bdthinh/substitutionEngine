package tifmo.coreNLP;

/**
 * Created by bdthinh on 11/7/14.
 */
public class Pair {
	public String get_fromFile() {
		return _fromFile;
	}

	public Pair set_fromFile(String _fromFile) {
		this._fromFile = _fromFile;
		return this;
	}

	String _fromFile;
	String _text;
	String _entailment;
	String _task;
	int _id;
	String _hypo;

	public Pair(Pair pair) {
		this._text = pair._text;
		this._entailment = pair._entailment;
		this._task = pair._task;
		this._id = pair._id;
		this._hypo = pair._hypo;
		this._fromFile = pair._fromFile;
	}

	public Pair(String _text, String _hypo) {
		this._text = _text;
		this._hypo = _hypo;
	}

	public Pair(int id, String text, String hypo, String entailment, String task) {
		this._id = id;
		this._text = text;
		this._hypo = hypo;
		this._entailment = entailment;
		this._task = task;
	}

	public Pair(int id, String text, String hypo, String entailment, String task, String fromFile) {
		this._id = id;
		this._text = text;
		this._hypo = hypo;
		this._entailment = entailment;
		this._task = task;
		this._fromFile = fromFile;
	}

	public String get_text() {
		return _text;
	}

	public Pair set_text(String _text) {
		this._text = _text;
		return this;
	}

	public String get_entailment() {
		return _entailment;
	}

	public Pair set_entailment(String _entailment) {
		this._entailment = _entailment;
		return this;
	}

	public String get_task() {
		return _task;
	}

	public Pair set_task(String _task) {
		this._task = _task;
		return this;
	}

	public int get_id() {
		return _id;
	}

	public Pair set_id(int _id) {
		this._id = _id;
		return this;
	}

	public String get_hypo() {
		return _hypo;
	}

	public Pair set_hypo(String _hypo) {
		this._hypo = _hypo;
		return this;
	}
}
