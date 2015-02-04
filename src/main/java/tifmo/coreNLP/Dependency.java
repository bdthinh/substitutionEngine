package tifmo.coreNLP;

import java.io.Serializable;

/**
 * Created by bdthinh on 10/24/14.
 */
public class Dependency implements Serializable {
	String _relation;
	Token _head;
	Token _modifier;

	public Dependency(String _relation, Token _head, Token _modifier) {
		this._relation = _relation;
		this._head = _head;
		this._modifier = _modifier;
	}

	public String get_relation() {

		return _relation;
	}

	public void set_relation(String _relation) {
		this._relation = _relation;
	}

	public Token get_head() {
		return _head;
	}

	public void set_head(Token _head) {
		this._head = _head;
	}

	public Token get_modifier() {
		return _modifier;
	}

	public void set_modifier(Token _modifier) {
		this._modifier = _modifier;
	}

	public void printOut() {
		System.out.println(_relation + "(" + _head.get_word() + "-" + _head.get_position() + "," + _modifier.get_word() + "-" + _modifier.get_position() + ")");
//		System.out.println(_head.get_word() + "-"+_head.get_position()+"-"
//						+_relation+"-"+_modifier.get_position()+"->"+_modifier.get_word());
	}
	public boolean equals(DirtDependency obj) {
		if (obj.get_head().equals(_head.get_lemma()) && obj.get_modifier().equals(_modifier.get_lemma())
						&& obj.get_relation().equals(_relation))
			return true;
		return false;
	}
	public boolean equals(Dependency obj){
		if (obj.get_head().get_lemma().equals(_head.get_lemma()) && obj.get_modifier().get_lemma().equals(_modifier.get_lemma())
						&& obj.get_relation().equals(_relation))
			return true;
		return false;
	}
}
