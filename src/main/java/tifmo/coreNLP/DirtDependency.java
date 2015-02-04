package tifmo.coreNLP;

/**
 * Created by bdthinh on 11/11/14.
 */
public class DirtDependency {
	String _head;
	String _modifier;
	String _relation;

	public int get_headPosition() {
		return _headPosition;
	}

	public void set_headPosition(int _headPosition) {
		this._headPosition = _headPosition;
	}

	public int get_modifierPosition() {
		return _modifierPosition;
	}

	public void set_modifierPosition(int _modifierPosition) {
		this._modifierPosition = _modifierPosition;
	}

	int _headPosition;
	int _modifierPosition;
	public DirtDependency(String _head, String _modifier, String _relation) {
		this._head = _head.split("-")[0];
		this._modifier = _modifier.split("-")[0];
		this._relation = _relation;
		this._headPosition = Integer.valueOf(_head.split("-")[_head.split("-").length - 1]);
		this._modifierPosition = Integer.valueOf(_modifier.split("-")[_modifier.split("-").length - 1]);
	}

	public DirtDependency() {
		_head = "";
		_modifier = "";
		_relation = "";
	}

	public String get_head() {
		return _head;
	}

	public void set_head(String _head) {
		this._head = _head;
	}

	public String get_modifier() {
		return _modifier;
	}

	public void set_modifier(String _modifier) {
		this._modifier = _modifier;
	}

	public String get_relation() {
		return _relation;
	}

	public void set_relation(String _relation) {
		this._relation = _relation;
	}

	public boolean equals(Dependency obj) {
		if (_head.equals(obj.get_head().get_lemma()) && _modifier.equals(obj.get_modifier().get_lemma())
						&& _relation.equals(obj.get_relation()))
			return true;
		return false;
	}
	public boolean equals(DirtDependency obj){
		if (obj.get_head().equals(_head) && obj.get_modifier().equals(_modifier)
						&& obj.get_relation().equals(_relation))
			return true;
		return false;
	}

	public void printOut() {
		System.out.println(_relation + "(" + _head + "-" + _headPosition + ", " + _modifier + "-" + _modifierPosition + ")");
	}
}
