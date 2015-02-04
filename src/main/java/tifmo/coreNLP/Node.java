package tifmo.coreNLP;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by bdthinh on 10/21/14.
 */
public class Node implements Serializable {
	Token _tokenHead;
	Map<Node, String> _modifiers;

	public Node(Token _tokenHead) {
		this._tokenHead = _tokenHead;
		_modifiers = new HashMap<Node, String>();
	}

	public Map<Node, String> get_modifiers() {
		return _modifiers;
	}

	public void set_modifiers(Map<Node, String> _modifiers) {
		this._modifiers = _modifiers;
	}

	public Token get_tokenHead() {
		return _tokenHead;
	}

	public void set_tokenHead(Token _tokenHead) {
		this._tokenHead = _tokenHead;
	}

	public void printOut() {
		Set<Node> modifierNodes = _modifiers.keySet();
		for (Node node : modifierNodes)
			System.out.println(_tokenHead._word + " - " + _modifiers.get(node) + " -> " + node._tokenHead.get_word());
	}
}
