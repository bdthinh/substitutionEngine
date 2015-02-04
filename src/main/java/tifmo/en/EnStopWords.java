package tifmo.en;

import tifmo.coreNLP.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bdthinh on 10/25/14.
 */
public class EnStopWords {
	// based on a stopword list used by MySQL fulltext search, moderately modified.
	private static List<String> _lexiconNegationCues = Arrays.asList("hardly", "neither", "nobody", "not", "cannot", "didnt", "havent", "neednt", "wasnt",
					"lack", "nor", "none", "n't", "darent", "hadnt", "isnt", "oughtnt", "wouldnt",
					"lacking", "never", "nothing", "aint", "dont", "hasnt", "mightnt", "shant", "without",
					"lacks", "no", "nowhere", "cant", "doesnt", "havnt", "mustnt", "shouldnt");

	private static List<String> _tobes = new ArrayList<String>(Arrays.asList("be","am","is","was","were","being"));

	private static List<String> _determiners = new ArrayList<String>(Arrays.asList("the","a","an","that","this","these","those","each","every","certain","any",
					"all","some","few","either","little","many","much"));

	private static List<String> _prepInVP = new ArrayList<String>(Arrays.asList("for", "from", "in", "of", "on", "to", "with", "at", "about"));
	private static List<String> _stopws = new ArrayList<String>(Arrays.asList("a's", "able", "about", "above", "according",
					"accordingly", "across", "actually", "after", "afterwards", "again", "against",
					"ain't", "all", "almost", "alone", "along", "already", "also", "although", "always",
					"am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone",
					"anything", "anyway", "anyways", "anywhere", "are", "aren't", "around", "as", "aside",
					"associated", "at", "available", "away", "awfully", "be", "became", "because", "become",
					"becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below",
					"beside", "besides", "between", "beyond", "both", "brief", "but", "by", "c'mon", "c's",
					"can", "can't", "cannot", "cant", "certainly", "clearly", "co", "com", "consequently",
					"corresponding", "could", "couldn't", "course", "currently", "definitely", "described",
					"despite", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "done", "down",
					"downwards", "during", "each", "edu", "eg", "either", "else", "elsewhere", "enough",
					"entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone",
					"everything", "everywhere", "ex", "exactly", "example", "except", "for", "former", "formerly",
					"from", "further", "furthermore", "had", "hadn't", "happens", "hardly", "has", "hasn't", "haven't",
					"having", "he", "he's", "hello", "hence", "her", "here", "here's", "hereafter", "hereby", "herein",
					"hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit",
					"however", "i'd", "i'll", "i'm", "i've", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc",
					"indeed", "inner", "insofar", "instead", "into", "inward", "is", "isn't", "it", "it'd", "it'll", "it's",
					"its", "itself", "just", "lately", "later", "latter", "latterly", "least", "lest", "let", "let's", "like",
					"liked", "likely", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might",
					"more", "moreover", "most", "mostly", "much", "must", "my", "myself", "namely", "nd", "near", "nearly",
					"necessary", "need", "needs", "neither", "never", "nevertheless", "next", "no", "nobody", "non", "none",
					"noone", "nor", "normally", "not", "nothing", "now", "nowhere", "obviously", "of", "off", "often", "oh",
					"ok", "okay", "on", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves",
					"out", "outside", "over", "overall", "particular", "particularly", "per", "perhaps", "please", "possible",
					"presumably", "probably", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding",
					"regardless", "regards", "relatively", "respectively", "said", "saw", "say", "saying", "says", "see",
					"seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "serious", "seriously",
					"seven", "several", "shall", "she", "should", "shouldn't", "since", "so", "some", "somebody", "somehow",
					"someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified",
					"specify", "specifying", "still", "such", "sure", "t's", "tends", "th", "than", "thank", "thanks",
					"thanx", "that", "that's", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence",
					"there", "there's", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these",
					"they", "they'd", "they'll", "they're", "they've", "think", "this", "thorough", "thoroughly", "those",
					"though", "through", "throughout", "thru", "thus", "to", "together", "too", "toward", "towards", "tried",
					"tries", "truly", "try", "trying", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto",
					"up", "upon", "us", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was",
					"wasn't", "way", "we", "we'd", "we'll", "we're", "we've", "welcome", "well", "went", "were", "weren't",
					"what", "what's", "whatever", "when", "whence", "whenever", "where", "where's", "whereafter", "whereas",
					"whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "who's",
					"whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "won't",
					"wonder", "would", "wouldn't", "yes", "yet", "you", "you'd", "you'll", "you're", "you've", "your", "yours",
					"yourself", "yourselves"));

	private static List<String> _pronoun = new ArrayList<String>
					(Arrays.asList("all","another","any","anybody","anyone","anything",
									"both","each","each other", "either", "everybody", "everyone", "everything",
									"few",
									"he", "her", "hers", "herself", "him", "himself", "his",
									"i", "it", "its", "itself",
									"many", "me", "mine", "more","most", "much", "myself",
									"neither", "no one", "nobody", "none", "nothing",
									"one", "one another", "other", "others", "ours", "ourselves",
									"several", "she", "some", "somebody", "someone", "something",
									"that", "their", "theirs", "them", "themselves", "these", "they", "this", "those",
									"us", "we", "what", "whatever", "which", "whichever", "who", "whoever", "whom", "whomever", "whose",
									"you", "your", "yours", "yourself", "yourselves"));

	private static List<String> _prepws = new ArrayList<String>(Arrays.asList(
					"aboard", "about", "above", "across", "after", "against", "along", "alongside", "amid", "amidst", "among", "amongst", "anti", "around", "as", "astride", "at", "atop", "according to", "ahead of", "along with", "apart from", "as for", "aside from", "as per", "as to", "as well as", "away from",
					"bar", "barring", "before", "behind", "below", "beneath", "beside", "besides", "between", "beyond", "but", "by", "because of", "but for", "by means of",
					"circa", "concerning", "considering", "counting", "cum", "close to", "contrary to",
					"despite", "down", "during", "depending on", "due to",
					"except", "excepting", "excluding", "except for",
					"following", "for", "from", "forward of", "further to",
					"given", "gone",
					"in", "including", "inside", "into", "in addition to", "in between", "in case of", "in face of", "in favour of", "in front of", "in lieu of", "in spite of", "instead of", "in view of",
					"less", "like",
					"minus",
					"near", "notwithstanding", "near to", "next to",
					"of", "off", "on", "onto", "opposite", "outside", "over", "on account of", "on behalf of", "on board", "on to", "on top of", "opposite to", "other than", "out of", "outside of", "owing to",
					"past", "pending", "per", "plus", "pro", "preparatory to", "prior to",
					"re", "regarding", "respecting", "round", "regardless of",
					"since", "save for",
					"than", "through", "throughout", "till", "to", "touching", "towards", "thanks to", "together with",
					"under", "underneath", "unlike", "until", "up", "upon", "up against", "up to", "up until",
					"versus", "via",
					"with", "within", "without", "worth", "with reference to", "with regard to"));
	public static boolean isToBe (String word){
		if(_tobes.contains(word.toLowerCase()))
			return true;
		return false;
	}
	public static boolean isDeterminer(String word){
		if(_determiners.contains(word.toLowerCase()))
			return true;
		return false;
	}
	public static boolean isDeterminer(Token tok){
		if(tok.get_pos().equals("DT"))
			return true;
		return false;
	}
	public static boolean isDeterminer (EnWord enw){
		if(enw.get_token().get_pos().equals("DT"))
			return true;
		return false;
	}
	public static boolean isStopWord (String word){
		if(word.length() <= 1 || word.matches("-?[0-9\\.%]+") || _stopws.contains(word))
			return true;
		return false;
	}

	public static boolean isPreposition (String word){
		if(_prepws.contains(word.toLowerCase()))
			return true;
		return false;
	}

	public static boolean isPronoun (String word){
		if(_pronoun.contains(word.toLowerCase()))
			return true;
		return false;
	}
	public static boolean isPronoun (String word, String posTag){
		List<String> pronounPOS = new ArrayList<>(Arrays.asList("prp", "prp$", "wp", "wp$"));
		if(_pronoun.contains(word.toLowerCase()) && pronounPOS.contains(posTag.toLowerCase()))
			return true;
		return false;
	}
	public static boolean isPreposition (String word, String posTag){
		if(_prepws.contains(word.toLowerCase()) && posTag.toLowerCase().equals("in"))
			return true;
		return false;
	}


	public static boolean isLexiconNegationCue(String word) {
		if(_lexiconNegationCues.contains(word.toLowerCase()))
			return true;
		return false;
	}

	public static boolean isPrepositionFollowedVerb(String word) {
		if(_prepInVP.contains(word.toLowerCase()))
			return true;
		return false;
	}
}
