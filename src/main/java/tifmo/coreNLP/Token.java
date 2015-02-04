package tifmo.coreNLP;

import tifmo.en.EnStopWords;
import tifmo.en.EnWordNet;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by bdthinh on 10/21/14.
 */
public class Token implements Serializable {

	static List<String> _nounTag = new ArrayList<String>(Arrays.asList("NN", "NNS", "NNP", "NNPS", "PRP"));
	static List<String> _verbTag = new ArrayList<String>(Arrays.asList("VB", "VBZ", "VBP", "VBD", "VBN", "VBG"));
	static List<String> _adjTag = new ArrayList<String>(Arrays.asList("JJ", "JJR", "JJS"));
	static List<String> _advTag = new ArrayList<String>(Arrays.asList("RB", "RBR", "RBS", "RP"));

	int _position;
	int _beginOffset;
	int _endOffset;
	String _word;
	String _lemma;
	String _pos;
	String _ne;
	String _posWN;
	public boolean isNamedEntity(){
		List<String> labels = Arrays.asList("time", "person", "money", "percent", "date", "organization", "location");
		if(labels.contains(_ne.toLowerCase()))
			return true;
		if(_pos.equals("NNP") || _pos.equals("NNPS"))
			return true;
		return false;
	}
	public Token(String _word, String _posWN) {
		this._word = _word;
		this._posWN = _posWN;
	}

	public Token(String _posWN) {
		this._posWN = _posWN;
	}

	public Token(int _position, int _beginOffset, int _endOffset, String _word, String _pos, String _ne) {
		this._position = _position;
		this._beginOffset = _beginOffset;
		this._endOffset = _endOffset;
		this._word = _word;
		this._pos = _pos;
		this._ne = _ne;
		this._posWN = "";
		this._lemma = _word;
	}

	public Token(Token tok) {
		this._position = tok.get_position();
		this._beginOffset = tok.get_beginOffset();
		this._endOffset = tok.get_endOffset();
		this._word = tok.get_word();
		this._pos = tok.get_pos();
		this._ne = tok.get_ne();
		this._posWN = tok.get_posWN();
		this._lemma = tok.get_lemma();
	}

	public Token(String _word, String _pos, String _ne) {
		this._word = _word;
		this._pos = _pos;
		this._ne = _ne;
	}

	public static List<Token> replaceTokens(List<Token> oldTokens, int from, int to, List<Token> newTokens) {
		List<Token> ret = new ArrayList<Token>(oldTokens);
		try {
			for (int i = from; i < to; ) {
				ret.remove(i);
				to--;
			}
			for (Token token : newTokens)
				ret.add(from++, token);

		} catch (Exception e){
			System.out.println("Error on replacing tokens because oldTokens' size is " + oldTokens.size() + ", we have to replace" +
							" from "+ from + " to " + (to - 1) +" by " + newTokens.size() + " tokens");
		}
		return ret;
	}

	public String get_lemma() {
		return _lemma;
	}

	public void set_lemma(String _lemma) {
		this._lemma = _lemma;
	}

	public String get_posWN() {
		return _posWN;
	}

	public void set_posWN(String _posWN) {
		this._posWN = _posWN;
	}

	public void setPosWNAndLemma() {
		_posWN = "";
		_lemma = _word;
		List<String> specialChars = Arrays.asList("_","-");
		for(String specialChar : specialChars)
			if(_word.contains(specialChar))
				return;
		//n v j r
		Map<Integer, List<String>> mapWN = new HashMap<Integer, List<String>>();
		mapWN.put(0, _nounTag);
		mapWN.put(1, _verbTag);
		mapWN.put(2, _adjTag);
		mapWN.put(3, _advTag);
		Integer keyPOS = -1;
		for (Integer key : mapWN.keySet())
			if (mapWN.get(key).contains(_pos)) {
				keyPOS = key;
				break;
			}
		if (keyPOS == -1)
			if (EnStopWords.isPreposition(_word))
				keyPOS = 4;
		if (!EnWordNet.isOpen())
			EnWordNet.open();
		try {
			switch (keyPOS) {
				case 0:
					_posWN = "n";
					_lemma = EnWordNet.stem(_word, _posWN);
					break;
				case 1:
					_posWN = "v";
					_lemma = EnWordNet.stem(_word, _posWN);
					break;
				case 2:
					_posWN = "j";
					_lemma = EnWordNet.stem(_word, _posWN);
					break;
				case 3:
					_posWN = "r";
					_lemma = EnWordNet.stem(_word, _posWN);
					break;
				case 4:
					_posWN = "p";
					_lemma = _word;
					break;
				default:
					_posWN = "";
					_lemma = _word;
			}
		} catch (Exception ex){
			_posWN = "";
			_lemma = _word;
			ex.printStackTrace();
		}
	}

	public int get_beginOffset() {
		return _beginOffset;
	}

	public void set_beginOffset(int _beginOffset) {
		this._beginOffset = _beginOffset;
	}

	public int get_endOffset() {
		return _endOffset;
	}

	public void set_endOffset(int _endOffset) {
		this._endOffset = _endOffset;
	}

	public String get_word() {

		return _word;
	}

	public void set_word(String _word) {
		this._word = _word;
	}

	public String get_pos() {
		return _pos;
	}

	public void set_pos(String _pos) {
		this._pos = _pos;
	}

	public String get_ne() {
		return _ne;
	}

	public void set_ne(String _ne) {
		this._ne = _ne;
	}

	public int get_position() {
		return _position;
	}

	public void set_position(int _position) {
		this._position = _position;
	}

	public void printOut() {
		if (this._word != "ROOT")
			System.out.println((_position) + " " + _word + " " + _pos + " " + _ne);
	}
}
