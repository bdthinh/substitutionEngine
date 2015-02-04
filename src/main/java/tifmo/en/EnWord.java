package tifmo.en;

import tifmo.coreNLP.Token;

import java.util.Arrays;
import java.util.List;

/**
 * Created by bdthinh on 10/25/14.
 */
public class EnWord {
	Token _token;

	public Token get_token() {
		return _token;
	}
	public String get_lemma(){ return _token.get_lemma();}
	public void set_lemma(String _lemma){ _token.set_lemma(_lemma);}
	public void set_token(Token _token) {
		this._token = _token;
	}

	public EnWord(String _word, String _pos, String _ne) {
		this._token = new Token(_word,_pos,_ne);
		this._token.setPosWNAndLemma();
	}
	public EnWord(Token _token) {
		this._token = _token;
	}

	public boolean isStopWord(){
		return (Arrays.asList("O","PERCENT","MONEY", "DATE", "TIME").contains(_token.get_ne()))
						&& EnStopWords.isStopWord(_token.get_word());
	}
	public boolean isNamedEntity(){
		return (!Arrays.asList("O","DATE","TIME","PERCENT","MONEY").contains(_token.get_ne()));
	}
	@Override
	public String toString(){
		return _token.get_word() + "_" + _token.get_lemma() + "_" + _token.get_pos();
	}
}
