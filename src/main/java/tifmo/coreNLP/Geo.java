package tifmo.coreNLP;

/**
 * Created by bdthinh on 11/10/14.
 */
public class Geo {
	String _entailing; //entailing ==> entailed
	String _entailed;

	public Geo(String _entailing) {
		this._entailing = _entailing;
	}

	public Geo(String _entailing, String _entailed) {

		this._entailing = _entailing;
		this._entailed = _entailed;
	}

	public String get_entailed() {

		return _entailed;
	}

	public void set_entailed(String _entailed) {
		this._entailed = _entailed;
	}

	public String get_entailing() {
		return _entailing;
	}

	public void set_entailing(String _entailing) {
		this._entailing = _entailing;
	}
}
