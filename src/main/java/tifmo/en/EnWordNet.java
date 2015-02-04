package tifmo.en;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.WordnetStemmer;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by bdthinh on 10/25/14.
 */

public class EnWordNet {
	//noun  n
	//verb  v
	//adjective a
	//adverb  r
	private static String filePath;
	private static Dictionary dict;
	private static WordnetStemmer stemmer;
	private static POS[] allpos;
	private static Class lock = EnWordNet.class;

	private static int minLength(List<String> words){
		int minlength = words.get(0).length();
		for(String word : words){
			minlength = (word.length() < minlength)? word.length():minlength;
		}
		return minlength;
	}

	private static String filterMinimum(List<String> words){
		List<String> mls = new ArrayList<String>();
		for(String word : words){
			if(word.length() == minLength(words))
				mls.add(word);
		}
		return Collections.min(mls);
	}

	public static void main(String[] args) {
		open();
		for(int i = 0 ; i < allpos.length;i++)
			System.out.println(allpos[i].toString());
	}

	public static boolean isOpen(){
		if(dict == null)
			return false;
		return true;
	}

	public static void open(){
		try {
			filePath = new File("").getCanonicalPath().concat("/resources/dict");
			dict = new Dictionary(new File(filePath));
			dict.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stemmer = new WordnetStemmer(dict);
		allpos = POS.values();
	}

	public static boolean hasWord (String word){
		synchronized (lock) {
			if(word.isEmpty())
				return false;
			for(POS pos : allpos){
				List<String> stems = stemmer.findStems(word,pos);
				for(String stem : stems){
					if(dict.getIndexWord(stem,pos)!= null)
						return true;
				}
			}
		return false;
		}
	}

	public static boolean hasWord (String word, String posChar){
		synchronized (lock){
			if(word.isEmpty())
				return false;
			List<String> stems = new ArrayList<String>();
			switch (posChar){
				case "n": stems = stemmer.findStems(word,POS.NOUN);
					break;
				case "v": stems = stemmer.findStems(word,POS.VERB);
					break;
				case "j": stems = stemmer.findStems(word,POS.ADJECTIVE);
					break;
				case "r": stems = stemmer.findStems(word,POS.ADVERB);
					break;
			}
			if(!stems.isEmpty())
				return true;
			return false;
		}
	}

	public static String stem (String word, String posChar){
		synchronized (lock){
			List<String> stems;
			switch (posChar){
				case "n": stems = stemmer.findStems(word,POS.NOUN);
					break;
				case "v": stems = stemmer.findStems(word,POS.VERB);
					break;
				case "j": stems = stemmer.findStems(word,POS.ADJECTIVE);
					break;
				case "r": stems = stemmer.findStems(word,POS.ADVERB);
					break;
				default: stems = new ArrayList<String>();
			}
			if(stems.isEmpty())
				return word;
			return filterMinimum(stems);
		}
	}

	public static List<ISynset> synsets (String lemma, String posChar){
		synchronized (lock){
			List<ISynset> synsets = new ArrayList<ISynset>();
			Map<POS,List<String>> stemMaps = new HashMap<POS, List<String>>();
			POS temp;
			switch(posChar){
				case "n": stemMaps.put(POS.NOUN, stemmer.findStems(lemma, POS.NOUN));
					break;
				case "v": stemMaps.put(POS.VERB,stemmer.findStems(lemma, POS.VERB));
					break;
				case "j": stemMaps.put(POS.ADJECTIVE,stemmer.findStems(lemma, POS.ADJECTIVE));
					break;
				case "r": stemMaps.put(POS.ADVERB,stemmer.findStems(lemma, POS.ADVERB));
					break;
				default: for(POS p : allpos){
					stemMaps.put(p, stemmer.findStems(lemma,p));
				}
			}
			POS[] keys = stemMaps.keySet().toArray(new POS[0]);
			for(POS key : keys){
				List<String> value = stemMaps.get(key);
				for(String each : value){
					if(dict.getIndexWord(each,key)!= null){
						List<IWordID> wids = dict.getIndexWord(each, key).getWordIDs();
						for(IWordID wid: wids)
							synsets.add(dict.getWord(wid).getSynset());
					}
				}
			}
			return synsets;
		}
	}

	public static List<String> getLemmas(List<ISynset> ss){
		synchronized (lock){
			List<String> lemmas = new ArrayList<String>();
			for(ISynset s : ss){
				List<IWord> iws = s.getWords();
				for(IWord iw : iws){
					lemmas.add(iw.getLemma());
				}
			}
			return lemmas;
		}
	}

	public static List<ISynset> getLexical(List<ISynset> ss, Pointer relation){
		synchronized (lock){
			List<IWordID> rws = new ArrayList<IWordID>();
			for(ISynset s : ss){
				List<IWord> iws = s.getWords();
				for(IWord iw : iws){
					rws.addAll(iw.getRelatedWords(relation));
				}
			}
			List<ISynset> iss = new ArrayList<ISynset>();
			for(IWordID rw : rws){
				iss.add(dict.getWord(rw).getSynset());
			}
			return iss;
		}
	}

	public static List<ISynset> getSemantic (List<ISynset> ss, Pointer relation){
		synchronized (lock){
			List<ISynset> iss = new ArrayList<ISynset>();
			for(ISynset s : ss){
				List<ISynsetID> ids = s.getRelatedSynsets(relation);
				for(ISynsetID id: ids) {
					iss.add(dict.getSynset(id));
				}
			}
			return iss;
		}
	}



}
