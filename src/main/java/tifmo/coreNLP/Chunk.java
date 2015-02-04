package tifmo.coreNLP;

import edu.mit.jwi.item.POS;
import tifmo.en.EnStopWords;
import tifmo.en.EnWord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bdthinh on 10/23/14.
 */
public class Chunk implements Serializable {
	int _begin;
	int _end;
	List<Token> _tokens;
	String _tag;
	String _chunk;

	public Chunk(Chunk ck) {
		this._tag = ck.get_tag();
		this._tokens = new ArrayList<Token>(ck.get_tokens());
		this._begin = ck.get_begin();
		this._end = ck.get_end();
		this._chunk = ck.get_chunk();
	}

	public Chunk(List<Token> _tokens, String _tag, int _begin, int end) {
		this._tag = _tag;
		this._tokens = new ArrayList<>(_tokens);
		this._begin = _begin;
		this._end = end;
		this._chunk = "";
		for (Token token : this._tokens)
			this._chunk = this._chunk + token.get_word() + " ";
		this._chunk.trim();
	}

	public Chunk(String _tag, String _chunk) {
		this._tag = _tag;
		this._chunk = _chunk;
		this._begin = 0;
		this._end = 0;
	}

	public static boolean containPhraseTag(String[] words) {
		String[] phraseTag = new String[]{"NP", "VP", "PP", "ADVP", "ADJP", "SBAR", "INTJ", "QP"};
		for (String word : words) {
			for (int i = 0; i < phraseTag.length; i++) {
				if (word.contains("[") && word.contains(phraseTag[i]) && word.contains("]"))
					return true;
			}
		}
		return false;
	}

	public int get_end() {
		return _end;
	}

	public void set_end(int _end) {
		this._end = _end;
	}

	public List<Token> get_tokens() {

		return _tokens;
	}

	public void set_tokens(List<Token> tokens) {
		this._tokens = tokens;
		this._chunk = "";
		for (Token token : this._tokens)
			this._chunk = this._chunk + token.get_word() + " ";
		this._chunk.trim();
	}

	public int get_begin() {
		return _begin;
	}

	public void set_begin(int _begin) {
		this._begin = _begin;
	}

	public String get_tag() {

		return _tag;
	}

	public void set_tag(String _tag) {
		this._tag = _tag;
	}

	public String get_chunk() {
		return _chunk;
	}

	public void set_chunk(String _chunk) {
		this._chunk = _chunk;
	}

	public void printOut() {
		System.out.println("   " + _tag + "[" + _begin + "-" + _end + "] " + _chunk);
	}

	public Chunk trim(){
		_chunk = _chunk.trim();
		return this;
	}

	public boolean containAcronyms() {
		if (get_tag().equals("NP") || get_tag().equals("PP"))
			for (Token tok : get_tokens())
				if (tok.get_word().matches("([A-Z])([A-Z.])*"))
					return true;
		return false;
	}

	public boolean containEscapeCharacters() {
		String tmp = get_chunk();
		List<String> escapeCharacter = new ArrayList<>(Arrays.asList("-LRB-", "-RRB", "-LCB", "-RCB", "-LSB", "-RSB", ".", ",", "\"", "\'", ":"));
		for (String esc : escapeCharacter)
			if (tmp.contains(esc))
				return true;
		return false;
	}

	public boolean startWithTo() {
		if ((get_tag().equals("VP") || get_tag().equals("VPS")) && get_tokens().get(0).get_word().toLowerCase().equals("to"))
			return true;
		return false;
	}

	public boolean isAllCardinalNumber() {
		boolean flag = true;
		for (Token tok : get_tokens())
			if (!tok.get_pos().equals("CD") && !tok.get_pos().equals("$"))
				flag = false;
		return flag;
	}

	public boolean isNPOneToken() {
		if (get_tag().equals("NP") && get_tokens().size() == 1)
			return true;
		return false;
	}

	public boolean isNNPWithPOS() {
		if (!get_tag().equals("NP"))
			return false;
		else {
			boolean flag = true;
			for (int i = 0; i < get_tokens().size() - 1; i++)
				if (!get_tokens().get(i).get_pos().equals("NNP") && !get_tokens().get(i).get_pos().equals("NNPS"))
					return false;
			if (!get_tokens().get(get_tokens().size() - 1).get_pos().equals("POS"))
				return false;
		}
		return true;
	}

	public boolean containAllNNP() {
		if (!get_tag().equals("NP"))
			return false;
		else
			for (Token tok : get_tokens())
				if (!tok.get_pos().equals("NNP") && !tok.get_pos().equals("NNPS") && !EnStopWords.isStopWord(tok.get_word().toLowerCase()))
					return false;
		return true;
	}

	public boolean isAllFunctionWords() {
		List<String> namedEntity = new ArrayList<>(Arrays.asList("time", "person", "money", "percent", "date", "organization", "location"));
		boolean flag = true;
		for (Token tok : get_tokens())
			if (!namedEntity.contains(tok.get_ne().toLowerCase()) && !EnStopWords.isStopWord(tok.get_word().toLowerCase())
							&& !tok.get_pos().equals("NNP") && !tok.get_pos().equals("NNPS"))
				flag = false;
		return flag;
	}

	public boolean startWithNE() {
		List<String> namedEntity = new ArrayList<>(Arrays.asList("time", "person", "money", "percent", "date", "organization", "location"));
		boolean flag = false;
		if (namedEntity.contains(get_tokens().get(0).get_ne()))
			flag = true;
		return flag;
	}

	public boolean containLexiconNegationCue(){
		boolean flag = false;
		for (Token tok: get_tokens())
			if(EnStopWords.isLexiconNegationCue(tok.get_word()))
				flag = true;
		return flag;
	}

	public int getDTPositionInNPBeginWithDT() {
		int begin = -1;
		for(int i = 0, n = get_tokens().size() ; i < n ; i++){
			if(!get_tokens().get(i).get_pos().equals("DT"))
				return ++begin;
			else
				begin = i;
		}
		return ++begin;
	}

	public Chunk getChunkFilterDTPositionInNPBeginWithDT(int DTPosition){
		return (new Chunk(get_tokens().subList(DTPosition, get_end() - get_begin() + 1), "NPDT", get_begin() + DTPosition, get_end()));
	}

	public int getINPositionInVPEndWithIN(){
		int end = get_tokens().size();
		List<String> INExcluding = Arrays.asList("that", "which", "who", "whom", "when", "why", "where", "what");
		for(int n = get_tokens().size(), i = n - 1; i >= 0; i--){
			if(!get_tokens().get(i).get_pos().equals("IN") || !INExcluding.contains(get_tokens().get(i).get_word()))
				return --end;
			else
				end = i;
		}
		return --end;
	}

	public boolean isVPEndWithIN(){
		if(get_tag().contains("VP") && get_tokens().get(get_tokens().size()-1).get_pos().equals("IN"))
			return true;
		return false;
	}

	public Chunk getChunkFilterINPositionInVPEndWithIN(int INPosition){
		return (new Chunk(get_tokens().subList(0, INPosition), "VPIN", get_begin(), get_begin() + INPosition - 1));
	}

	public int getPOSPositionInNPEndWithNoun(){
		int begin = get_tokens().size();
		if(get_tokens().get(begin - 1).get_pos().equals("NN") || get_tokens().get(begin - 1).get_pos().equals("NNS")) {
			for (int n = get_tokens().size(), i = n - 2; i >= 0; i--) {
				if(get_tokens().get(i).get_pos().equals("POS"))
					return i;
				else
					begin = i;
			}
		}
		return begin;
	}

	public Chunk getChunkFilterPOSPositionInNPEndWithPOS(int POSPosition){
		return (new Chunk(get_tokens().subList(POSPosition, get_end() - get_begin() + 1), "NPPOS", get_begin() + POSPosition - 1, get_end()));
	}

	public boolean contains(Chunk otherChunk) {
		if(get_chunk().contains(otherChunk.get_chunk()))
			return true;
		return false;
	}

	public boolean subsumed(Chunk otherChunk){
		if(otherChunk.get_begin() <= get_begin() && get_end() <= otherChunk.get_end())
			return true;
		return false;
	}

	public boolean isVerbPhrase(){
		if(get_tag().matches("^VP"))
			return true;
		return false;
	}

	public boolean isNounPhrase(){
		if(get_tag().matches("^NP"))
			return true;
		return false;
	}

	public boolean isPrepositionPhrase(){
		if(get_tag().matches("^PP"))
			return true;
		return false;
	}
}
