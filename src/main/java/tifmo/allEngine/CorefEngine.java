package tifmo.allEngine;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import tifmo.coreNLP.Pair;
import tifmo.coreNLP.Parser;
import tifmo.coreNLP.Token;
import tifmo.similarityComp.CorefPairComp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by bdthinh on 11/6/14.
 * CorefEngine is used to substitute pair by coreference between text and hypothesis in pair.
 * There are 2 applied directions: from hypo to text and vice versa. The remain part is preserved.
 */
public class CorefEngine {

	/**
	 * Returns the coreference pairs between text and Hypo. The corefPair is filtered and sorted as the direction from text to hypo
	 *
	 * @param text: String
	 * @param hypo: String
	 * @return List&lt;CorefPair&gt;
	 * @throws
	 * @author bdthinh
	 */
	public static List<CorefPair> getCorefPairsFromTextToHypo(String text, String hypo) {
		List<CorefPair> pairs = new ArrayList<CorefPair>();
		Map<String, Object> annotation = Parser.parseTextToAnnotation(text + " " + hypo);
		Map<Integer, CorefChain> graph = (Map<Integer, CorefChain>) annotation.get("coref");

		for (Integer key : graph.keySet()) {
			CorefChain chain = graph.get(key);
			List<CorefMention> mentions = chain.getMentionsInTextualOrder();
			if (mentions.size() == 2) {
				if (mentions.get(0).sentNum < mentions.get(1).sentNum)
					pairs.add(new CorefPair(mentions.get(1), mentions.get(0)));
				else if (mentions.get(0).sentNum > mentions.get(1).sentNum)
					pairs.add(new CorefPair(mentions.get(0), mentions.get(1)));
			}
		}
		pairs = CorefPair.filter(pairs);
		Collections.sort(pairs, new CorefPairComp());
		return pairs;

	}

	/**
	 * Returns the coreference pairs between text and hypo. The corefPairs are filtered and sorted as the direction from text to hypo
	 *
	 * @param originAnnotation: Map&lt;String, Object&gt;
	 * @return List&lt;CorefPair&gt;
	 * @throws
	 * @author bdthinh
	 */
	public static List<CorefPair> getCorefPairsFromTextToHypo(Map<String, Object> originAnnotation) {
		List<CorefPair> pairs = new ArrayList<CorefPair>();
		Map<String, Object> annotation = Parser.getDeepCopyAnnotation(originAnnotation);
		Map<Integer, CorefChain> graph = (Map<Integer, CorefChain>) annotation.get("coref");

		for (Integer key : graph.keySet()) {
			CorefChain chain = graph.get(key);
			List<CorefMention> mentions = chain.getMentionsInTextualOrder();
			if (mentions.size() == 2) {
				if (mentions.get(0).sentNum < mentions.get(1).sentNum)
					pairs.add(new CorefPair(mentions.get(1), mentions.get(0)));
				else if (mentions.get(0).sentNum > mentions.get(1).sentNum)
					pairs.add(new CorefPair(mentions.get(0), mentions.get(1)));
			}
		}
		pairs = CorefPair.filter(pairs);
		Collections.sort(pairs, new CorefPairComp());
		return pairs;
	}

	/**
	 * Returns the coreference pairs between text and hypo. The corefPairs are filtered and sorted as the direction from hypo to text
	 *
	 * @param text: String
	 * @param hypo: String
	 * @return List&lt;CorefPair&gt;
	 * @throws
	 * @author bdthinh
	 */
	public static List<CorefPair> getCorefPairsFromHypoToText(String text, String hypo) {
		List<CorefPair> pairs = new ArrayList<CorefPair>();

		Map<String, Object> annotation = Parser.parseTextToAnnotation(text + " " + hypo);
		Map<Integer, CorefChain> graph = (Map<Integer, CorefChain>) annotation.get("coref");

		for (Integer key : graph.keySet()) {
			CorefChain chain = graph.get(key);
			List<CorefMention> mentions = chain.getMentionsInTextualOrder();
			if (mentions.size() == 2) {
				if (mentions.get(0).sentNum < mentions.get(1).sentNum)
					pairs.add(new CorefPair(mentions.get(0), mentions.get(1)));
				else if (mentions.get(0).sentNum > mentions.get(1).sentNum)
					pairs.add(new CorefPair(mentions.get(1), mentions.get(0)));
			}
		}
		pairs = CorefPair.filter(pairs);
		Collections.sort(pairs, new CorefPairComp());
		return pairs;
	}

	/**
	 * Returns the coreference pairs between text and hypo. The corefPairs are filtered and sorted as the direction from hypo to text
	 *
	 * @param originAnnotation: Map&lt;String, Object&gt;
	 * @return List&lt;CorefPair&gt;
	 * @throws
	 * @author bdthinh
	 */
	public static List<CorefPair> getCorefPairsFromHypoToText(Map<String, Object> originAnnotation) {
		List<CorefPair> pairs = new ArrayList<CorefPair>();
		Map<String, Object> annotation = Parser.getDeepCopyAnnotation(originAnnotation);
		Map<Integer, CorefChain> graph = (Map<Integer, CorefChain>) annotation.get("coref");

		for (Integer key : graph.keySet()) {
			CorefChain chain = graph.get(key);
			List<CorefMention> mentions = chain.getMentionsInTextualOrder();
			if (mentions.size() == 2) {
				if (mentions.get(0).sentNum < mentions.get(1).sentNum)
					pairs.add(new CorefPair(mentions.get(0), mentions.get(1)));
				else if (mentions.get(0).sentNum > mentions.get(1).sentNum)
					pairs.add(new CorefPair(mentions.get(1), mentions.get(0)));
			}
		}
		pairs = CorefPair.filter(pairs);
		Collections.sort(pairs, new CorefPairComp());
		return pairs;
	}

	/**
	 * Return a substituted pair by Coreference Engine. Every textSpan in text that is coreference with textSpan in hypo will be substituted. Text would be replace while Hypo is consistent.
	 *
	 * @param originTextAnno: Map&lt;String,Object&gt;
	 * @param originHypoAnno: Map&lt;String,Object&gt;
	 * @param pairAnno:       Map&lt;String,Object&gt;
	 * @return Pair
	 * @throws
	 * @author bdthinh
	 */
	public static Pair substituteCorefFromHypoToText(Map<String, Object> originTextAnno, Map<String, Object> originHypoAnno, Map<String, Object> pairAnno) {
		Map<String, Object> textAnno = Parser.getDeepCopyAnnotation(originTextAnno);
		Map<String, Object> hypoAnno = Parser.getDeepCopyAnnotation(originHypoAnno);

		Map<Integer, List<Token>> textTokens = Parser.getTokensInAnnotation(textAnno);
		Map<Integer, List<Token>> hypoTokens = Parser.getTokensInAnnotation(hypoAnno);

		int textSentNumber = Parser.getNumberOfSent(textAnno);

		List<CorefPair> pairs = getCorefPairsFromHypoToText(pairAnno);
		//System.out.println("---Coref Information---");
		for (CorefPair pair : pairs) {
			if (pair.get_from().sentNum <= textSentNumber
							&& textSentNumber < pair.get_to().sentNum) {
//				System.out.println("[" + pair.get_from().startIndex + "," + pair.get_from().endIndex + "] "
//								+ pair.get_from().mentionSpan+ " --> " + "[" + pair.get_to().startIndex  + "," + pair.get_to().endIndex + "] " + pair.get_to().mentionSpan);
				if(pair.get_from().mentionSpan.equals(pair.get_to().mentionSpan))
					continue;
				int fromStartIndex = pair.get_from().startIndex;
				int fromEndIndex = pair.get_from().endIndex;

				int toStartIndex = pair.get_to().startIndex;
				int toEndIndex = pair.get_to().endIndex;
				textTokens.put(pair.get_from().sentNum,
								Token.replaceTokens(textTokens.get(pair.get_from().sentNum),
												fromStartIndex, fromEndIndex,
												hypoTokens.get(pair.get_to().sentNum - textSentNumber).subList(toStartIndex, toEndIndex)));

			}
		}

		return new Pair(Parser.getTextFromTokens(textTokens), (String) hypoAnno.get("text"));
	}

	/**
	 * Return a substituted pair by Coreference Engine. Every textSpan in hypo that is coreference with textSpan in text will be substituted. Hypo would be replace while Text is consistent.
	 *
	 * @param originTextAnno: Map&lt;String,Object&gt;
	 * @param originHypoAnno: Map&lt;String,Object&gt;
	 * @param pairAnno:       Map&lt;String,Object&gt;
	 * @return Pair
	 * @throws
	 * @author bdthinh
	 */
	public static Pair substituteCorefFromTextToHypo(Map<String, Object> originTextAnno, Map<String, Object> originHypoAnno, Map<String, Object> pairAnno) {
		Map<String, Object> textAnno = Parser.getDeepCopyAnnotation(originTextAnno);
		Map<String, Object> hypoAnno = Parser.getDeepCopyAnnotation(originHypoAnno);

		Map<Integer, List<Token>> textTokens = Parser.getTokensInAnnotation(textAnno);
		Map<Integer, List<Token>> hypoTokens = Parser.getTokensInAnnotation(hypoAnno);

		int textSentNumber = Parser.getNumberOfSent(textAnno);
		List<CorefPair> pairs = getCorefPairsFromTextToHypo(pairAnno);
		int startCache = 0;
		int endCache = 0;
		System.out.println("---Coref Information---");
		for (CorefPair pair : pairs) {
			if (pair.get_from().sentNum > textSentNumber
							&& textSentNumber >= pair.get_to().sentNum) {
				System.out.println("[" + pair.get_from().startIndex + "," + pair.get_from().endIndex + "] "
								+ pair.get_from().mentionSpan+ " --> " + "[" + pair.get_to().startIndex  + "," + pair.get_to().endIndex + "] " + pair.get_to().mentionSpan);
				if(pair.get_from().mentionSpan.equals(pair.get_to().mentionSpan))
					continue;
				int fromStartIndex = pair.get_from().startIndex;
				int fromEndIndex = pair.get_from().endIndex;
				if (fromStartIndex == startCache && fromEndIndex == endCache)
					continue;
				int toStartIndex = pair.get_to().startIndex;
				int toEndIndex = pair.get_to().endIndex;
				hypoTokens.put(pair.get_from().sentNum - textSentNumber,
								Token.replaceTokens(hypoTokens.get(pair.get_from().sentNum - textSentNumber),
												fromStartIndex, fromEndIndex,
												textTokens.get(pair.get_to().sentNum).subList(toStartIndex, toEndIndex)));
				startCache = fromStartIndex;
				endCache = fromEndIndex;
			}
		}
		return new Pair((String) textAnno.get("text"), Parser.getTextFromTokens(hypoTokens));
	}

	/**
	 * Return a substituted pair by Coreference Engine. Every textSpan in text that is coreference with textSpan in hypo will be substituted. Text would be replace while Hypo is consistent.
	 *
	 * @param textPair: Pair
	 * @return Pair
	 * @throws
	 * @author bdthinh
	 */
	public static Pair substituteCorefFromHypoToText(Pair textPair) {
		Map<String, Object> textAnno = Parser.parseTextToAnnotation(textPair.get_text());
		Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(textPair.get_hypo());
		Map<Integer, List<Token>> textTokens = Parser.getTokensInAnnotation(textAnno);
		Map<Integer, List<Token>> hypoTokens = Parser.getTokensInAnnotation(hypoAnno);

		int textSentNumber = Parser.getNumberOfSent(textAnno);

		List<CorefPair> pairs = getCorefPairsFromHypoToText(textPair.get_text(), textPair.get_hypo());
		//System.out.println("---Coref Information---");
		for (CorefPair pair : pairs) {
			if (pair.get_from().sentNum <= textSentNumber
							&& textSentNumber < pair.get_to().sentNum) {
//				System.out.println("Text: [" + pair.get_from().startIndex + "," + (pair.get_from().endIndex - 1) + "] "
//								+ pair.get_from().mentionSpan+ " <-- " + "Hypo: [" + pair.get_to().startIndex  + "," + (pair.get_to().endIndex-1) + "] " + pair.get_to().mentionSpan);
				if(pair.get_from().mentionSpan.equals(pair.get_to().mentionSpan))
					continue;
				int fromStartIndex = pair.get_from().startIndex;
				int fromEndIndex = pair.get_from().endIndex;

				int toStartIndex = pair.get_to().startIndex;
				int toEndIndex = pair.get_to().endIndex;
				textTokens.put(pair.get_from().sentNum,
								Token.replaceTokens(textTokens.get(pair.get_from().sentNum),
												fromStartIndex, fromEndIndex,
												hypoTokens.get(pair.get_to().sentNum - textSentNumber).subList(toStartIndex, toEndIndex)));
			}
		}

		return new Pair(Parser.getTextFromTokens(textTokens), textPair.get_hypo());
	}

	/**
	 * Return a substituted pair by Coreference Engine. Every textSpan in hypo that is coreference with textSpan in text will be substituted. Hypo would be replace while Text is consistent.
	 *
	 * @param textPair: Pair
	 * @return Pair
	 * @throws
	 * @author bdthinh
	 */
	public static Pair substituteCorefFromTextToHypo(Pair textPair) {
		Map<String, Object> textAnno = Parser.parseTextToAnnotation(textPair.get_text());
		Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(textPair.get_hypo());
		Map<Integer, List<Token>> textTokens = Parser.getTokensInAnnotation(textAnno);
		Map<Integer, List<Token>> hypoTokens = Parser.getTokensInAnnotation(hypoAnno);

		int textSentNumber = Parser.getNumberOfSent(textAnno);
		List<CorefPair> pairs = getCorefPairsFromTextToHypo(textPair.get_text(), textPair.get_hypo());
		int startCache = 0;
		int endCache = 0;
		System.out.println("---Coref Information---");
		for (CorefPair pair : pairs) {
			if (pair.get_from().sentNum > textSentNumber
							&& textSentNumber >= pair.get_to().sentNum) {
				System.out.println("Hypo: [" + pair.get_from().startIndex + "," + pair.get_from().endIndex + "] "
								+ pair.get_from().mentionSpan+ " <-- " + "Text: [" + pair.get_to().startIndex  + "," + pair.get_to().endIndex + "] " + pair.get_to().mentionSpan);
				if(pair.get_from().mentionSpan.equals(pair.get_to().mentionSpan))
					continue;
				int fromStartIndex = pair.get_from().startIndex;
				int fromEndIndex = pair.get_from().endIndex;
				if (fromStartIndex == startCache && fromEndIndex == endCache)
					continue;
				int toStartIndex = pair.get_to().startIndex;
				int toEndIndex = pair.get_to().endIndex;
				hypoTokens.put(pair.get_from().sentNum - textSentNumber,
								Token.replaceTokens(hypoTokens.get(pair.get_from().sentNum - textSentNumber),
												fromStartIndex, fromEndIndex,
												textTokens.get(pair.get_to().sentNum).subList(toStartIndex, toEndIndex)));
				startCache = fromStartIndex;
				endCache = fromEndIndex;
			}
		}
		return new Pair(textPair.get_text(), Parser.getTextFromTokens(hypoTokens));
	}

}
