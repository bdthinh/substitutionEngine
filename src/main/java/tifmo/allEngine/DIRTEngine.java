package tifmo.allEngine;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import tifmo.coreNLP.*;
import tifmo.en.EnDIRT;
import tifmo.en.EnFactory;
import tifmo.en.EnResourceWN;
import tifmo.en.EnWord;
import tifmo.utils.EnDepsContextSimilarity;
import tifmo.utils.EnGloveSimilarity;
import tifmo.utils.EnMikolovSimilarity;

import java.util.*;

/**
 * Created by bdthinh on 11/10/14.
 * DIRTEngine could substitute pairs that their text and hypothesis is paraphrased via DIRT database
 */
public class DIRTEngine {
	//_dependencyRelation stores all dependency relations that are defined in Stanford Parser
	private static double _threshold = 0.4;

	public static String getCoverage(){
		return _coverage;
	}

	private static String _coverage;

	private static List<String> _dependencyRelation = new ArrayList<>(Arrays.asList("acomp", "advcl", "advmod", "agent", "amod", "appos",
					"aux", "auxpass", "cc", "ccomp", "conj", "cop", "csubj", "csubjpass", "dep", "det", "discourse",
					"dobj", "expl", "goeswith", "iobj", "mark", "mwe", "neg", "nn", "npadvmod", "nsubj", "nsubjpass",
					"num", "number", "parataxis", "pcomp", "pobj", "poss", "possessive", "preconj", "predet", "prep",
					"prepc", "prt", "punct", "quantmod", "ref", "root", "tmod", "vmod", "xcomp", "xsubj"));

	public static void main(String[] args) {
	}
	/**
	 * Returns the substituted pair via DIRTEngine
	 *
	 * @param originTextAnno: Map&lt;String,Object&gt;
	 * @param originHypoAnno: Map&lt;String,Object&gt;
	 * @return substituted Pair: Pair
	 * @throws
	 * @author bdthinh
	 */
	public static Pair substitutePair(Map<String, Object> originTextAnno, Map<String, Object> originHypoAnno) {
		Map<String, Object> textAnno = Parser.getDeepCopyAnnotation(originTextAnno);
		Map<String, Object> hypoAnno = Parser.getDeepCopyAnnotation(originHypoAnno);
		int textSentNumber = Parser.getNumberOfSent(textAnno);
		int hypoSentNumber = Parser.getNumberOfSent(hypoAnno);
		Map<Integer, List<Token>> textTokenOfSent = (Map<Integer, List<Token>>) textAnno.get("tokens");
		Map<Integer, List<Token>> hypoTokensOfSent = (Map<Integer, List<Token>>) hypoAnno.get("tokens");
		Map<Integer,List<Dependency>> textDependenciesOfSent = Parser.getDependency(textAnno);
		Map<Integer,List<Dependency>> hypoDependenciesOfSent = Parser.getDependency(hypoAnno);
		List<Integer> textSentChosen = new ArrayList<>();
		for (int hypoSent = 1; hypoSent <= hypoSentNumber; hypoSent++) {
			for (int textSent = 1; textSent <= textSentNumber && !textSentChosen.contains(textSent); textSent++) {
				List<Dependency> textDependencies = textDependenciesOfSent.get(textSent);
				List<Dependency> hypoDependencies = hypoDependenciesOfSent.get(hypoSent);
				if(textDependencies.size() != 0 && hypoDependencies.size() != 0) {
					AlignedPosition alignment = getAlignment(textDependencies, hypoDependencies);
					if (alignment != null) {
						textTokenOfSent.put(textSent, Token.replaceTokens(textTokenOfSent.get(textSent), alignment.get_beginTokenSource(), alignment.get_endTokenSource() + 1,
										hypoTokensOfSent.get(hypoSent).subList(alignment.get_beginTokenTarget(), alignment.get_endTokenTarget() + 1)));
						textSentChosen.add(textSent);
					}
				}
			}
		}
		if((Parser.getTextFromTokens(textTokenOfSent)).equals(Parser.getTextFromTokens((Map<Integer,List<Token>>)originTextAnno.get("tokens")))) {
			_coverage = _coverage + "," + "EQUAL";
			System.out.println("EQUAL");
		}
		else {
			_coverage = _coverage + "," + "UNEQUAL";
			System.out.println("UNEQUAL");
		}
		return new Pair(Parser.getTextFromTokens(textTokenOfSent), Parser.getTextFromTokens(hypoTokensOfSent));
	}
	public static Pair substitutePairExtendSynset(Map<String, Object> originTextAnno, Map<String, Object> originHypoAnno) {
		Map<String, Object> textAnno = Parser.getDeepCopyAnnotation(originTextAnno);
		Map<String, Object> hypoAnno = Parser.getDeepCopyAnnotation(originHypoAnno);
		int textSentNumber = Parser.getNumberOfSent(textAnno);
		int hypoSentNumber = Parser.getNumberOfSent(hypoAnno);
		Map<Integer, List<Token>> textTokenOfSent = (Map<Integer, List<Token>>) textAnno.get("tokens");
		Map<Integer, List<Token>> hypoTokensOfSent = (Map<Integer, List<Token>>) hypoAnno.get("tokens");
		Map<Integer,List<Dependency>> textDependenciesOfSent = Parser.getDependency(textAnno);
		Map<Integer,List<Dependency>> hypoDependenciesOfSent = Parser.getDependency(hypoAnno);
		List<Integer> textSentChosen = new ArrayList<>();
		for (int hypoSent = 1; hypoSent <= hypoSentNumber; hypoSent++) {
			for (int textSent = 1; textSent <= textSentNumber && !textSentChosen.contains(textSent); textSent++) {
				List<Dependency> textDependencies = textDependenciesOfSent.get(textSent);
				List<Dependency> hypoDependencies = hypoDependenciesOfSent.get(hypoSent);
				if(textDependencies.size() != 0 && hypoDependencies.size() != 0) {
					AlignedPosition alignment = getAlignmentExtendSynset(textDependencies, hypoDependencies);
					if (alignment != null) {
						textTokenOfSent.put(textSent, Token.replaceTokens(textTokenOfSent.get(textSent), alignment.get_beginTokenSource(), alignment.get_endTokenSource() + 1,
										hypoTokensOfSent.get(hypoSent).subList(alignment.get_beginTokenTarget(), alignment.get_endTokenTarget() + 1)));
						textSentChosen.add(textSent);
					}
				}
			}
		}
		if((Parser.getTextFromTokens(textTokenOfSent)).equals(Parser.getTextFromTokens((Map<Integer,List<Token>>)originTextAnno.get("tokens")))) {
			_coverage = _coverage + "," + "EQUAL";
			System.out.println("EQUAL");
		}
		else {
			_coverage = _coverage + "," + "UNEQUAL";
			System.out.println("UNEQUAL");
		}
		return new Pair(Parser.getTextFromTokens(textTokenOfSent), Parser.getTextFromTokens(hypoTokensOfSent));
	}
	public static Pair substitutePairExtendPhrase(Map<String, Object> originTextAnno, Map<String, Object> originHypoAnno) {
		Map<String, Object> textAnno = Parser.getDeepCopyAnnotation(originTextAnno);
		Map<String, Object> hypoAnno = Parser.getDeepCopyAnnotation(originHypoAnno);
		int textSentNumber = Parser.getNumberOfSent(textAnno);
		int hypoSentNumber = Parser.getNumberOfSent(hypoAnno);
		Map<Integer, List<Token>> textTokenOfSent = (Map<Integer, List<Token>>) textAnno.get("tokens");
		Map<Integer, List<Token>> hypoTokensOfSent = (Map<Integer, List<Token>>) hypoAnno.get("tokens");
		Map<Integer,List<Dependency>> textDependenciesOfSent = Parser.getDependency(textAnno);
		Map<Integer,List<Dependency>> hypoDependenciesOfSent = Parser.getDependency(hypoAnno);
		List<Integer> textSentChosen = new ArrayList<>();
		for (int hypoSent = 1; hypoSent <= hypoSentNumber; hypoSent++) {
			for (int textSent = 1; textSent <= textSentNumber && !textSentChosen.contains(textSent); textSent++) {
				List<Dependency> textDependencies = textDependenciesOfSent.get(textSent);
				List<Dependency> hypoDependencies = hypoDependenciesOfSent.get(hypoSent);
				if(textDependencies.size() != 0 && hypoDependencies.size() != 0) {
					AlignedPosition alignment = getAlignmentExtendPhrase(textDependencies, hypoDependencies, textAnno, hypoAnno, textSent, hypoSent);
					if (alignment != null) {
						textTokenOfSent.put(textSent, Token.replaceTokens(textTokenOfSent.get(textSent), alignment.get_beginTokenSource(), alignment.get_endTokenSource() + 1,
										hypoTokensOfSent.get(hypoSent).subList(alignment.get_beginTokenTarget(), alignment.get_endTokenTarget() + 1)));
						textSentChosen.add(textSent);
					}
				}
			}
		}
		if((Parser.getTextFromTokens(textTokenOfSent)).equals(Parser.getTextFromTokens((Map<Integer,List<Token>>)originTextAnno.get("tokens")))) {
			_coverage = _coverage + "," + "EQUAL";
			System.out.println("EQUAL");
		}
		else {
			_coverage = _coverage + "," + "UNEQUAL";
			System.out.println("UNEQUAL");
		}
		return new Pair(Parser.getTextFromTokens(textTokenOfSent), Parser.getTextFromTokens(hypoTokensOfSent));
	}
	public static Pair substitutePairExtendPhraseSynset(Map<String, Object> originTextAnno, Map<String, Object> originHypoAnno) {
		Map<String, Object> textAnno = Parser.getDeepCopyAnnotation(originTextAnno);
		Map<String, Object> hypoAnno = Parser.getDeepCopyAnnotation(originHypoAnno);
		int textSentNumber = Parser.getNumberOfSent(textAnno);
		int hypoSentNumber = Parser.getNumberOfSent(hypoAnno);
		Map<Integer, List<Token>> textTokenOfSent = (Map<Integer, List<Token>>) textAnno.get("tokens");
		Map<Integer, List<Token>> hypoTokensOfSent = (Map<Integer, List<Token>>) hypoAnno.get("tokens");
		Map<Integer,List<Dependency>> textDependenciesOfSent = Parser.getDependency(textAnno);
		Map<Integer,List<Dependency>> hypoDependenciesOfSent = Parser.getDependency(hypoAnno);
		List<Integer> textSentChosen = new ArrayList<>();
		for (int hypoSent = 1; hypoSent <= hypoSentNumber; hypoSent++) {
			for (int textSent = 1; textSent <= textSentNumber && !textSentChosen.contains(textSent); textSent++) {
				List<Dependency> textDependencies = textDependenciesOfSent.get(textSent);
				List<Dependency> hypoDependencies = hypoDependenciesOfSent.get(hypoSent);
				if(textDependencies.size() != 0 && hypoDependencies.size() != 0) {
					AlignedPosition alignment = getAlignmentExtendPhraseSynset(textDependencies, hypoDependencies, textAnno, hypoAnno, textSent, hypoSent);
					if (alignment != null) {
						textTokenOfSent.put(textSent, Token.replaceTokens(textTokenOfSent.get(textSent), alignment.get_beginTokenSource(), alignment.get_endTokenSource() + 1,
										hypoTokensOfSent.get(hypoSent).subList(alignment.get_beginTokenTarget(), alignment.get_endTokenTarget() + 1)));
						textSentChosen.add(textSent);
					}
				}
			}
		}
		if((Parser.getTextFromTokens(textTokenOfSent)).equals(Parser.getTextFromTokens((Map<Integer,List<Token>>)originTextAnno.get("tokens")))) {
			_coverage = _coverage + "," + "EQUAL";
			System.out.println("EQUAL");
		}
		else {
			_coverage = _coverage + "," + "UNEQUAL";
			System.out.println("UNEQUAL");
		}
		return new Pair(Parser.getTextFromTokens(textTokenOfSent), Parser.getTextFromTokens(hypoTokensOfSent));
	}

	public static Pair substitutePairSkippingNode(Map<String, Object> originTextAnno, Map<String, Object> originHypoAnno) {
		Map<String, Object> textAnno = Parser.getDeepCopyAnnotation(originTextAnno);
		Map<String, Object> hypoAnno = Parser.getDeepCopyAnnotation(originHypoAnno);
		int textSentNumber = Parser.getNumberOfSent(textAnno);
		int hypoSentNumber = Parser.getNumberOfSent(hypoAnno);
		Map<Integer, List<Token>> textTokenOfSent = (Map<Integer, List<Token>>) textAnno.get("tokens");
		Map<Integer, List<Token>> hypoTokensOfSent = (Map<Integer, List<Token>>) hypoAnno.get("tokens");
		Map<Integer,List<Dependency>> textDependenciesOfSent = Parser.getDependency(textAnno);
		Map<Integer,List<Dependency>> hypoDependenciesOfSent = Parser.getDependency(hypoAnno);
		List<Integer> textSentChosen = new ArrayList<>();
		for (int hypoSent = 1; hypoSent <= hypoSentNumber; hypoSent++) {
			for (int textSent = 1; textSent <= textSentNumber && !textSentChosen.contains(textSent); textSent++) {
				List<Dependency> textDependencies = textDependenciesOfSent.get(textSent);
				List<Dependency> hypoDependencies = hypoDependenciesOfSent.get(hypoSent);
				if(textDependencies.size() != 0 && hypoDependencies.size() != 0) {
					AlignedPosition alignment = getAlignmentSkippingNode(textDependencies, hypoDependencies);
					if (alignment != null) {
						textTokenOfSent.put(textSent, Token.replaceTokens(textTokenOfSent.get(textSent), alignment.get_beginTokenSource(), alignment.get_endTokenSource() + 1,
										hypoTokensOfSent.get(hypoSent).subList(alignment.get_beginTokenTarget(), alignment.get_endTokenTarget() + 1)));
						textSentChosen.add(textSent);
					}
				}
			}
		}
		if((Parser.getTextFromTokens(textTokenOfSent)).equals(Parser.getTextFromTokens((Map<Integer,List<Token>>)originTextAnno.get("tokens")))) {
			_coverage = _coverage + "," + "EQUAL";
			System.out.println("EQUAL");
		}
		else {
			_coverage = _coverage + "," + "UNEQUAL";
			System.out.println("UNEQUAL");
		}
		return new Pair(Parser.getTextFromTokens(textTokenOfSent), Parser.getTextFromTokens(hypoTokensOfSent));
	}
	public static Pair substitutePairSkippingNodeExtendSynset(Map<String, Object> originTextAnno, Map<String, Object> originHypoAnno) {
		Map<String, Object> textAnno = Parser.getDeepCopyAnnotation(originTextAnno);
		Map<String, Object> hypoAnno = Parser.getDeepCopyAnnotation(originHypoAnno);
		int textSentNumber = Parser.getNumberOfSent(textAnno);
		int hypoSentNumber = Parser.getNumberOfSent(hypoAnno);
		Map<Integer, List<Token>> textTokenOfSent = (Map<Integer, List<Token>>) textAnno.get("tokens");
		Map<Integer, List<Token>> hypoTokensOfSent = (Map<Integer, List<Token>>) hypoAnno.get("tokens");
		Map<Integer,List<Dependency>> textDependenciesOfSent = Parser.getDependency(textAnno);
		Map<Integer,List<Dependency>> hypoDependenciesOfSent = Parser.getDependency(hypoAnno);
		List<Integer> textSentChosen = new ArrayList<>();
		for (int hypoSent = 1; hypoSent <= hypoSentNumber; hypoSent++) {
			for (int textSent = 1; textSent <= textSentNumber && !textSentChosen.contains(textSent); textSent++) {
				List<Dependency> textDependencies = textDependenciesOfSent.get(textSent);
				List<Dependency> hypoDependencies = hypoDependenciesOfSent.get(hypoSent);
				if(textDependencies.size() != 0 && hypoDependencies.size() != 0) {
					AlignedPosition alignment = getAlignmentSkippingNodeExtendSynset(textDependencies, hypoDependencies);
					if (alignment != null) {
						textTokenOfSent.put(textSent, Token.replaceTokens(textTokenOfSent.get(textSent), alignment.get_beginTokenSource(), alignment.get_endTokenSource() + 1,
										hypoTokensOfSent.get(hypoSent).subList(alignment.get_beginTokenTarget(), alignment.get_endTokenTarget() + 1)));
						textSentChosen.add(textSent);
					}
				}
			}
		}
		if((Parser.getTextFromTokens(textTokenOfSent)).equals(Parser.getTextFromTokens((Map<Integer,List<Token>>)originTextAnno.get("tokens")))) {
			_coverage = _coverage + "," + "EQUAL";
			System.out.println("EQUAL");
		}
		else {
			_coverage = _coverage + "," + "UNEQUAL";
			System.out.println("UNEQUAL");
		}
		return new Pair(Parser.getTextFromTokens(textTokenOfSent), Parser.getTextFromTokens(hypoTokensOfSent));
	}
	public static Pair substitutePairSkippingNodeExtendPhrase(Map<String, Object> originTextAnno, Map<String, Object> originHypoAnno) {
		Map<String, Object> textAnno = Parser.getDeepCopyAnnotation(originTextAnno);
		Map<String, Object> hypoAnno = Parser.getDeepCopyAnnotation(originHypoAnno);
		int textSentNumber = Parser.getNumberOfSent(textAnno);
		int hypoSentNumber = Parser.getNumberOfSent(hypoAnno);
		Map<Integer, List<Token>> textTokenOfSent = (Map<Integer, List<Token>>) textAnno.get("tokens");
		Map<Integer, List<Token>> hypoTokensOfSent = (Map<Integer, List<Token>>) hypoAnno.get("tokens");
		Map<Integer,List<Dependency>> textDependenciesOfSent = Parser.getDependency(textAnno);
		Map<Integer,List<Dependency>> hypoDependenciesOfSent = Parser.getDependency(hypoAnno);
		List<Integer> textSentChosen = new ArrayList<>();
		for (int hypoSent = 1; hypoSent <= hypoSentNumber; hypoSent++) {
			for (int textSent = 1; textSent <= textSentNumber && !textSentChosen.contains(textSent); textSent++) {
				List<Dependency> textDependencies = textDependenciesOfSent.get(textSent);
				List<Dependency> hypoDependencies = hypoDependenciesOfSent.get(hypoSent);
				if(textDependencies.size() != 0 && hypoDependencies.size() != 0) {
					AlignedPosition alignment = getAlignmentSkippingNodeExtendPhrase(textDependencies, hypoDependencies, textAnno, hypoAnno, textSent, hypoSent);
					if (alignment != null) {
						textTokenOfSent.put(textSent, Token.replaceTokens(textTokenOfSent.get(textSent), alignment.get_beginTokenSource(), alignment.get_endTokenSource() + 1,
										hypoTokensOfSent.get(hypoSent).subList(alignment.get_beginTokenTarget(), alignment.get_endTokenTarget() + 1)));
						textSentChosen.add(textSent);
					}
				}
			}
		}
		if((Parser.getTextFromTokens(textTokenOfSent)).equals(Parser.getTextFromTokens((Map<Integer,List<Token>>)originTextAnno.get("tokens")))) {
			_coverage = _coverage + "," + "EQUAL";
			System.out.println("EQUAL");
		}
		else {
			_coverage = _coverage + "," + "UNEQUAL";
			System.out.println("UNEQUAL");
		}
		return new Pair(Parser.getTextFromTokens(textTokenOfSent), Parser.getTextFromTokens(hypoTokensOfSent));
	}
	public static Pair substitutePairSkippingNodeExtendPhraseSynset(Map<String, Object> originTextAnno, Map<String, Object> originHypoAnno) {
		Map<String, Object> textAnno = Parser.getDeepCopyAnnotation(originTextAnno);
		Map<String, Object> hypoAnno = Parser.getDeepCopyAnnotation(originHypoAnno);
		int textSentNumber = Parser.getNumberOfSent(textAnno);
		int hypoSentNumber = Parser.getNumberOfSent(hypoAnno);
		Map<Integer, List<Token>> textTokenOfSent = (Map<Integer, List<Token>>) textAnno.get("tokens");
		Map<Integer, List<Token>> hypoTokensOfSent = (Map<Integer, List<Token>>) hypoAnno.get("tokens");
		Map<Integer,List<Dependency>> textDependenciesOfSent = Parser.getDependency(textAnno);
		Map<Integer,List<Dependency>> hypoDependenciesOfSent = Parser.getDependency(hypoAnno);
		List<Integer> textSentChosen = new ArrayList<>();
		for (int hypoSent = 1; hypoSent <= hypoSentNumber; hypoSent++) {
			for (int textSent = 1; textSent <= textSentNumber && !textSentChosen.contains(textSent); textSent++) {
				List<Dependency> textDependencies = textDependenciesOfSent.get(textSent);
				List<Dependency> hypoDependencies = hypoDependenciesOfSent.get(hypoSent);
				if(textDependencies.size() != 0 && hypoDependencies.size() != 0) {
					AlignedPosition alignment = getAlignmentSkippingNodeExtendPhraseSynset(textDependencies, hypoDependencies, textAnno, hypoAnno, textSent, hypoSent);
					if (alignment != null) {
						textTokenOfSent.put(textSent, Token.replaceTokens(textTokenOfSent.get(textSent), alignment.get_beginTokenSource(), alignment.get_endTokenSource() + 1,
										hypoTokensOfSent.get(hypoSent).subList(alignment.get_beginTokenTarget(), alignment.get_endTokenTarget() + 1)));
						textSentChosen.add(textSent);
					}
				}
			}
		}
		if((Parser.getTextFromTokens(textTokenOfSent)).equals(Parser.getTextFromTokens((Map<Integer,List<Token>>)originTextAnno.get("tokens")))) {
			_coverage = _coverage + "," + "EQUAL";
			System.out.println("EQUAL");
		}
		else {
			_coverage = _coverage + "," + "UNEQUAL";
			System.out.println("UNEQUAL");
		}
		return new Pair(Parser.getTextFromTokens(textTokenOfSent), Parser.getTextFromTokens(hypoTokensOfSent));
	}
	/**
	 * Returns root node of a Dependency tree
	 */
	public static Dependency getDependencyRoot(List<Dependency> dependencies) {
		for (Dependency dep : dependencies)
			if (dep.get_relation().equals("root"))
				return dep;
		return null;
	}

	/**
	 * Returns root node of a DirtDependency tree
	 */
	public static DirtDependency getDirtDependencyRoot(List<DirtDependency> dirtDs) {
		for (DirtDependency dirtD : dirtDs)
			if (dirtD.get_relation().equals("root"))
				return dirtD;
		return null;
	}

	/**
	 * Returns a collection of DirtDependecy in suitable format (as dependency tree) from a dirtElement.
	 *
	 * @param dirtElement: String, is returned as a left part or right part record of DIRT database
	 * @return List&lt;DirtDependency&gt;, a collection of DirtDependency
	 * @throws
	 * @author bdthinh
	 * @returnUnExpected .size() == 0
	 */
	public static List<DirtDependency> parseDirtElementToDirtDependency(String dirtElement) {
		List<DirtDependency> ret = new ArrayList<DirtDependency>();
		try {
			char[] tmp = dirtElement.toCharArray();
			List<String> parts = new ArrayList<>();
			int begin = 0;
			for (int i = 0; i < tmp.length; i++) {
				if (tmp[i] == '<' || tmp[i] == '>') {
					String pre = String.copyValueOf(tmp, begin, i - begin);
					parts.add(pre);
					parts.add(String.copyValueOf(tmp, i, 1));
					begin = i + 1;
				}
			}
			String sub = String.copyValueOf(tmp, begin, tmp.length - begin);
			parts.add(sub);

			for (int i = 0; i < parts.size(); i++) {
				if (!parts.get(i).contains(":") && _dependencyRelation.contains(parts.get(i))) {
					String relation = parts.get(i);
					String head = "";
					String modifier = "";
					if (parts.get(i - 1).equals("<") && parts.get(i + 1).equals("<")) {
						head = parts.get(i + 2) + "-" + ((i + 2) / 4 + 1);
						modifier = parts.get(i - 2) + "-" + ((i - 2) / 4 + 1);
					} else if (parts.get(i - 1).equals(">") && parts.get(i + 1).equals(">")) {
						head = parts.get(i - 2) + "-" + ((i - 2) / 4 + 1);
						modifier = parts.get(i + 2) + "-" + ((i + 2) / 4 + 1);
					}
					DirtDependency dep = new DirtDependency(head, modifier, relation);
					ret.add(dep);
				} else if (parts.get(i).contains(":") && parts.get(i - 1).equals("<") && parts.get(i + 1).equals(">")) {
					ret.add(new DirtDependency("ROOT-0", parts.get(i) + "-" + (i / 4 + 1), "root"));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}


	private static Integer getBeginPositionOfDirtElement(List<DirtDependency> dirtDependencies){
		Collections.sort(dirtDependencies, new DirtDependencyComp());
		return dirtDependencies.get(0).get_modifierPosition();
	}
	private static Integer getEndPositionOfDirtElement(List<DirtDependency> dirtDependencies){
		Collections.sort(dirtDependencies, new DirtDependencyComp());
		return dirtDependencies.get(dirtDependencies.size() - 1).get_modifierPosition();
	}
	/**
	 *
	 * @author bdthinh
	 * @param
	 * @param
	 * @param
	 * @param
	 * @return
	 * @returnUnExpected
	 * @throws
	 */

	private static String chosen = "mikolov";

	private static Map<Integer, Integer> getMatchedPath(DirtDependency dirtDep, Dependency dep){
		if(chosen.equals("mikolov"))
			return getMatchedPathMikolov(dirtDep, dep);
		else if(chosen.equals("deps"))
			return getMatchedPathDEPSContext(dirtDep, dep);
		else if(chosen.equals("glove"))
			return getMatchedPathGlove(dirtDep, dep);
		return getMatchedPathMikolov(dirtDep, dep);
	}
	private static Map<Integer, Integer> getMatchedPathMikolov(DirtDependency dirtDep, Dependency dep){
		Map<Integer, Integer> ret = new HashMap<>();
		EnMikolovSimilarity mikolovSim = new EnMikolovSimilarity();
		if (!dep.get_relation().equals(dirtDep.get_relation()))
			return ret;
		if (dep.get_relation().equals("root")) {
			String[] splitted = dirtDep.get_modifier().split(":");
			if (splitted.length == 3) {
				String posWN = splitted[0];
				String lemma = splitted[1];
				if (lemma.equals(dep.get_modifier().get_lemma()) && posWN.equals(dep.get_modifier().get_posWN()))
					ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
			}
		} else { //the same dependency relation
			boolean flagHeadMatch = false;
			String[] splittedHead = dirtDep.get_head().split(":");
			String[] splittedMod = dirtDep.get_modifier().split(":");
			if (splittedHead.length == 3) {
				String posWN = splittedHead[0];
				String lemma = splittedHead[1];
				if (lemma.equals(dep.get_head().get_lemma()) && posWN.equals(dep.get_head().get_posWN())) {
					flagHeadMatch = true;
				}
				else if(posWN.equals(dep.get_head().get_posWN())){
					if(posWN.equals("p") || mikolovSim.getSimilarity(lemma, dep.get_head().get_lemma()) >= _threshold)
						flagHeadMatch = true;
				}
				else if(lemma.equals(dep.get_head().get_lemma())){
					List<String> suitablePOSWN = Arrays.asList("n","v");
					if(suitablePOSWN.contains(posWN) && suitablePOSWN.contains(dep.get_head().get_posWN()))
						flagHeadMatch = true;
				}
			} else {
				String posWN = dirtDep.get_head();
				if (posWN.equals(dep.get_head().get_posWN())) {
					flagHeadMatch = true;
				}
			}
			if (flagHeadMatch) {
				if (splittedMod.length == 3) {
					String posWN = splittedMod[0];
					String lemma = splittedMod[1];
					if (lemma.equals(dep.get_modifier().get_lemma()) && posWN.equals(dep.get_modifier().get_posWN()))
						ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					else if(posWN.equals(dep.get_modifier().get_posWN())){
						if(posWN.equals("p") || mikolovSim.getSimilarity(lemma, dep.get_modifier().get_lemma()) >= _threshold)
							ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					}
					else if(lemma.equals(dep.get_modifier().get_lemma())){
						List<String> suitablePOSWN = Arrays.asList("n","v");
						if(suitablePOSWN.contains(posWN) && suitablePOSWN.contains(dep.get_modifier().get_posWN()))
							ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					}
				} else {
					String posWN = dirtDep.get_modifier();
					if (posWN.equals(dep.get_modifier().get_posWN()))
						ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
				}
			}
		}
		return ret;
	}
	private static Map<Integer, Integer> getMatchedPathGlove(DirtDependency dirtDep, Dependency dep){
		Map<Integer, Integer> ret = new HashMap<>();
		EnGloveSimilarity gloveSim = new EnGloveSimilarity();
		if (!dep.get_relation().equals(dirtDep.get_relation()))
			return ret;
		if (dep.get_relation().equals("root")) {
			String[] splitted = dirtDep.get_modifier().split(":");
			if (splitted.length == 3) {
				String posWN = splitted[0];
				String lemma = splitted[1];
				if (lemma.equals(dep.get_modifier().get_lemma()) && posWN.equals(dep.get_modifier().get_posWN()))
					ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
			}
		} else { //the same dependency relation
			boolean flagHeadMatch = false;
			String[] splittedHead = dirtDep.get_head().split(":");
			String[] splittedMod = dirtDep.get_modifier().split(":");
			if (splittedHead.length == 3) {
				String posWN = splittedHead[0];
				String lemma = splittedHead[1];
				if (lemma.equals(dep.get_head().get_lemma()) && posWN.equals(dep.get_head().get_posWN())) {
					flagHeadMatch = true;
				}
				else if(posWN.equals(dep.get_head().get_posWN())){
					if(posWN.equals("p") || gloveSim.getSimilarity(lemma, dep.get_head().get_lemma()) >= _threshold)
						flagHeadMatch = true;
				}
				else if(lemma.equals(dep.get_head().get_lemma())){
					List<String> suitablePOSWN = Arrays.asList("n","v");
					if(suitablePOSWN.contains(posWN) && suitablePOSWN.contains(dep.get_head().get_posWN()))
						flagHeadMatch = true;
				}
			} else {
				String posWN = dirtDep.get_head();
				if (posWN.equals(dep.get_head().get_posWN())) {
					flagHeadMatch = true;
				}
			}
			if (flagHeadMatch) {
				if (splittedMod.length == 3) {
					String posWN = splittedMod[0];
					String lemma = splittedMod[1];
					if (lemma.equals(dep.get_modifier().get_lemma()) && posWN.equals(dep.get_modifier().get_posWN()))
						ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					else if(posWN.equals(dep.get_modifier().get_posWN())){
						if(posWN.equals("p") || gloveSim.getSimilarity(lemma, dep.get_modifier().get_lemma()) >= _threshold)
							ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					}
					else if(lemma.equals(dep.get_modifier().get_lemma())){
						List<String> suitablePOSWN = Arrays.asList("n","v");
						if(suitablePOSWN.contains(posWN) && suitablePOSWN.contains(dep.get_modifier().get_posWN()))
							ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					}
				} else {
					String posWN = dirtDep.get_modifier();
					if (posWN.equals(dep.get_modifier().get_posWN()))
						ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
				}
			}
		}
		return ret;
	}
	private static Map<Integer, Integer> getMatchedPathDEPSContext(DirtDependency dirtDep, Dependency dep){
		Map<Integer, Integer> ret = new HashMap<>();
		EnDepsContextSimilarity depsContextSimilarity = new EnDepsContextSimilarity();
		if (!dep.get_relation().equals(dirtDep.get_relation()))
			return ret;
		if (dep.get_relation().equals("root")) {
			String[] splitted = dirtDep.get_modifier().split(":");
			if (splitted.length == 3) {
				String posWN = splitted[0];
				String lemma = splitted[1];
				if (lemma.equals(dep.get_modifier().get_lemma()) && posWN.equals(dep.get_modifier().get_posWN()))
					ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
			}
		} else { //the same dependency relation
			boolean flagHeadMatch = false;
			String[] splittedHead = dirtDep.get_head().split(":");
			String[] splittedMod = dirtDep.get_modifier().split(":");
			if (splittedHead.length == 3) {
				String posWN = splittedHead[0];
				String lemma = splittedHead[1];
				if (lemma.equals(dep.get_head().get_lemma()) && posWN.equals(dep.get_head().get_posWN())) {
					flagHeadMatch = true;
				}
				else if(posWN.equals(dep.get_head().get_posWN())){
					if(posWN.equals("p") || depsContextSimilarity.getSimilarity(lemma, dep.get_head().get_lemma(), dirtDep.get_modifier().concat("I"), dep.get_relation().concat("I")) >= _threshold)
						flagHeadMatch = true;
				}
				else if(lemma.equals(dep.get_head().get_lemma())){
					List<String> suitablePOSWN = Arrays.asList("n","v");
					if(suitablePOSWN.contains(posWN) && suitablePOSWN.contains(dep.get_head().get_posWN()))
						flagHeadMatch = true;
				}
			} else {
				String posWN = dirtDep.get_head();
				if (posWN.equals(dep.get_head().get_posWN())) {
					flagHeadMatch = true;
				}
			}
			if (flagHeadMatch) {
				if (splittedMod.length == 3) {
					String posWN = splittedMod[0];
					String lemma = splittedMod[1];
					if (lemma.equals(dep.get_modifier().get_lemma()) && posWN.equals(dep.get_modifier().get_posWN()))
						ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					else if(posWN.equals(dep.get_modifier().get_posWN())){
						if(posWN.equals("p") || depsContextSimilarity.getSimilarity(lemma, dep.get_modifier().get_lemma(), dirtDep.get_modifier(), dep.get_relation()) >= _threshold)
							ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					}
					else if(lemma.equals(dep.get_modifier().get_lemma())){
						List<String> suitablePOSWN = Arrays.asList("n","v");
						if(suitablePOSWN.contains(posWN) && suitablePOSWN.contains(dep.get_modifier().get_posWN()))
							ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					}
				} else {
					String posWN = dirtDep.get_modifier();
					if (posWN.equals(dep.get_modifier().get_posWN()))
						ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
				}
			}
		}
		return ret;
	}

	private static Map<Integer, Integer> getMatchedPathExtendPhrase(DirtDependency dirtDep, Dependency dep, List<DirtDependency> originDirtDeps, List<Chunk> chunks){
		if(chosen.equals("mikolov"))
			return getMatchedPathExtendPhraseMikolov(dirtDep, dep, originDirtDeps, chunks);
		else if(chosen.equals("deps"))
			return getMatchedPathExtendPhraseDEPSContext(dirtDep, dep, originDirtDeps, chunks);
		else if(chosen.equals("glove"))
			return getMatchedPathExtendPhraseGlove(dirtDep, dep, originDirtDeps, chunks);
		return getMatchedPathExtendPhraseMikolov(dirtDep, dep, originDirtDeps, chunks);
	}
	private static Map<Integer, Integer> getMatchedPathExtendPhraseMikolov(DirtDependency dirtDep, Dependency dep, List<DirtDependency> originDirtDeps, List<Chunk> chunks) {
		Map<Integer, Integer> ret = new HashMap<>();
		EnMikolovSimilarity mikolovSim = new EnMikolovSimilarity();
		if (!dep.get_relation().equals(dirtDep.get_relation()))
			return ret;
		if (dep.get_relation().equals("root")) {
			String[] splitted = dirtDep.get_modifier().split(":");
			if (splitted.length == 3) {
				String posWN = splitted[0];
				String lemma = splitted[1];
				if (lemma.equals(dep.get_modifier().get_lemma()) && posWN.equals(dep.get_modifier().get_posWN()))
					ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
			}
		} else { //the same dependency relation
			boolean flagHeadMatch = false;
			String[] splittedHead = dirtDep.get_head().split(":");
			String[] splittedMod = dirtDep.get_modifier().split(":");
			if (splittedHead.length == 3) {
				String posWN = splittedHead[0];
				String lemma = splittedHead[1];
				if (lemma.equals(dep.get_head().get_lemma()) && posWN.equals(dep.get_head().get_posWN())) {
					flagHeadMatch = true;
				}
				else if(posWN.equals(dep.get_head().get_posWN())){
					if(posWN.equals("p") || mikolovSim.getSimilarity(lemma, dep.get_head().get_lemma()) >= _threshold)
						flagHeadMatch = true;
				}
				else if(lemma.equals(dep.get_head().get_lemma())){
					List<String> suitablePOSWN = Arrays.asList("n","v");
					if(suitablePOSWN.contains(posWN) && suitablePOSWN.contains(dep.get_head().get_posWN()))
						flagHeadMatch = true;
				}
			} else {
				String posWN = dirtDep.get_head();
				if (posWN.equals(dep.get_head().get_posWN())) {
					flagHeadMatch = true;
				}
			}
			if (flagHeadMatch) {
				if (splittedMod.length == 3) {
					String posWN = splittedMod[0];
					String lemma = splittedMod[1];
					if (lemma.equals(dep.get_modifier().get_lemma()) && posWN.equals(dep.get_modifier().get_posWN()))
						ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					else if(posWN.equals(dep.get_modifier().get_posWN())){
						if(posWN.equals("p") || mikolovSim.getSimilarity(lemma, dep.get_modifier().get_lemma()) >= _threshold)
							ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					}
					else if(lemma.equals(dep.get_modifier().get_lemma())){
						List<String> suitablePOSWN = Arrays.asList("n","v");
						if(suitablePOSWN.contains(posWN) && suitablePOSWN.contains(dep.get_modifier().get_posWN()))
							ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					}
				} else {
					String posWN = dirtDep.get_modifier();
					if (posWN.equals(dep.get_modifier().get_posWN()))
						ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					else{
						if(dirtDep.get_modifierPosition() == getBeginPositionOfDirtElement(originDirtDeps)) {
							if (posWN.equals("n")) {
								//find NP
								List<Integer> beginPositionOfChunk = new ArrayList<>();
								for(Chunk chunk : chunks){
									if(chunk.get_tag().equals("NP") && chunk.get_end() == dep.get_modifier().get_position())
										beginPositionOfChunk.add(chunk.get_begin());
								}
								if(beginPositionOfChunk.size() != 0)
									ret.put(dirtDep.get_modifierPosition(), Collections.max(beginPositionOfChunk));
							} else if (posWN.equals("v")) {
								//find VP
								List<Integer> beginPositionOfChunk = new ArrayList<>();
								for(Chunk chunk : chunks){
									if((chunk.get_tag().equals("VP") || chunk.get_tag().equals("VPS")) && chunk.get_end() == dep.get_modifier().get_position())
										beginPositionOfChunk.add(chunk.get_begin());
								}
								if(beginPositionOfChunk.size() != 0)
									ret.put(dirtDep.get_modifierPosition(), Collections.max(beginPositionOfChunk));
							}
						}
						else if (dirtDep.get_modifierPosition() == getEndPositionOfDirtElement(originDirtDeps)){
							if(posWN.equals("n")){
								//find NP
								List<Integer> endPositionOfChunk = new ArrayList<>();
								for(Chunk chunk : chunks){
									if(chunk.get_tag().equals("NP") && chunk.get_begin() == dep.get_modifier().get_position())
										endPositionOfChunk.add(chunk.get_end());
								}
								if(endPositionOfChunk.size() != 0)
									ret.put(dirtDep.get_modifierPosition(), Collections.min(endPositionOfChunk));
							} else if(posWN.equals("v")){
								//find VP
								List<Integer> endPositionOfChunk = new ArrayList<>();
								for(Chunk chunk : chunks){
									if((chunk.get_tag().equals("VP") || chunk.get_tag().equals("VPS")) && chunk.get_begin() == dep.get_modifier().get_position())
										endPositionOfChunk.add(chunk.get_end());
								}
								if(endPositionOfChunk.size() != 0)
									ret.put(dirtDep.get_modifierPosition(), Collections.min(endPositionOfChunk));
							}
						}
					}
				}
			}
		}
		return ret;
	}
	private static Map<Integer, Integer> getMatchedPathExtendPhraseGlove(DirtDependency dirtDep, Dependency dep, List<DirtDependency> originDirtDeps, List<Chunk> chunks) {
		Map<Integer, Integer> ret = new HashMap<>();
		EnGloveSimilarity gloveSim = new EnGloveSimilarity();
		if (!dep.get_relation().equals(dirtDep.get_relation()))
			return ret;
		if (dep.get_relation().equals("root")) {
			String[] splitted = dirtDep.get_modifier().split(":");
			if (splitted.length == 3) {
				String posWN = splitted[0];
				String lemma = splitted[1];
				if (lemma.equals(dep.get_modifier().get_lemma()) && posWN.equals(dep.get_modifier().get_posWN()))
					ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
			}
		} else { //the same dependency relation
			boolean flagHeadMatch = false;
			String[] splittedHead = dirtDep.get_head().split(":");
			String[] splittedMod = dirtDep.get_modifier().split(":");
			if (splittedHead.length == 3) {
				String posWN = splittedHead[0];
				String lemma = splittedHead[1];
				if (lemma.equals(dep.get_head().get_lemma()) && posWN.equals(dep.get_head().get_posWN())) {
					flagHeadMatch = true;
				}
				else if(posWN.equals(dep.get_head().get_posWN())){
					if(posWN.equals("p") || gloveSim.getSimilarity(lemma, dep.get_head().get_lemma()) >= _threshold)
						flagHeadMatch = true;
				}
				else if(lemma.equals(dep.get_head().get_lemma())){
					List<String> suitablePOSWN = Arrays.asList("n","v");
					if(suitablePOSWN.contains(posWN) && suitablePOSWN.contains(dep.get_head().get_posWN()))
						flagHeadMatch = true;
				}
			} else {
				String posWN = dirtDep.get_head();
				if (posWN.equals(dep.get_head().get_posWN())) {
					flagHeadMatch = true;
				}
			}
			if (flagHeadMatch) {
				if (splittedMod.length == 3) {
					String posWN = splittedMod[0];
					String lemma = splittedMod[1];
					if (lemma.equals(dep.get_modifier().get_lemma()) && posWN.equals(dep.get_modifier().get_posWN()))
						ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					else if(posWN.equals(dep.get_modifier().get_posWN())){
						if(posWN.equals("p") || gloveSim.getSimilarity(lemma, dep.get_modifier().get_lemma()) >= _threshold)
							ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					}
					else if(lemma.equals(dep.get_modifier().get_lemma())){
						List<String> suitablePOSWN = Arrays.asList("n","v");
						if(suitablePOSWN.contains(posWN) && suitablePOSWN.contains(dep.get_modifier().get_posWN()))
							ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					}
				} else {
					String posWN = dirtDep.get_modifier();
					if (posWN.equals(dep.get_modifier().get_posWN()))
						ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					else{
						if(dirtDep.get_modifierPosition() == getBeginPositionOfDirtElement(originDirtDeps)) {
							if (posWN.equals("n")) {
								//find NP
								List<Integer> beginPositionOfChunk = new ArrayList<>();
								for(Chunk chunk : chunks){
									if(chunk.get_tag().equals("NP") && chunk.get_end() == dep.get_modifier().get_position())
										beginPositionOfChunk.add(chunk.get_begin());
								}
								if(beginPositionOfChunk.size() != 0)
									ret.put(dirtDep.get_modifierPosition(), Collections.max(beginPositionOfChunk));
							} else if (posWN.equals("v")) {
								//find VP
								List<Integer> beginPositionOfChunk = new ArrayList<>();
								for(Chunk chunk : chunks){
									if((chunk.get_tag().equals("VP") || chunk.get_tag().equals("VPS")) && chunk.get_end() == dep.get_modifier().get_position())
										beginPositionOfChunk.add(chunk.get_begin());
								}
								if(beginPositionOfChunk.size() != 0)
									ret.put(dirtDep.get_modifierPosition(), Collections.max(beginPositionOfChunk));
							}
						}
						else if (dirtDep.get_modifierPosition() == getEndPositionOfDirtElement(originDirtDeps)){
							if(posWN.equals("n")){
								//find NP
								List<Integer> endPositionOfChunk = new ArrayList<>();
								for(Chunk chunk : chunks){
									if(chunk.get_tag().equals("NP") && chunk.get_begin() == dep.get_modifier().get_position())
										endPositionOfChunk.add(chunk.get_end());
								}
								if(endPositionOfChunk.size() != 0)
									ret.put(dirtDep.get_modifierPosition(), Collections.min(endPositionOfChunk));
							} else if(posWN.equals("v")){
								//find VP
								List<Integer> endPositionOfChunk = new ArrayList<>();
								for(Chunk chunk : chunks){
									if((chunk.get_tag().equals("VP") || chunk.get_tag().equals("VPS")) && chunk.get_begin() == dep.get_modifier().get_position())
										endPositionOfChunk.add(chunk.get_end());
								}
								if(endPositionOfChunk.size() != 0)
									ret.put(dirtDep.get_modifierPosition(), Collections.min(endPositionOfChunk));
							}
						}
					}
				}
			}
		}
		return ret;
	}
	private static Map<Integer, Integer> getMatchedPathExtendPhraseDEPSContext(DirtDependency dirtDep, Dependency dep, List<DirtDependency> originDirtDeps, List<Chunk> chunks) {
		Map<Integer, Integer> ret = new HashMap<>();
		EnDepsContextSimilarity depsContextSimilarity = new EnDepsContextSimilarity();
		if (!dep.get_relation().equals(dirtDep.get_relation()))
			return ret;
		if (dep.get_relation().equals("root")) {
			String[] splitted = dirtDep.get_modifier().split(":");
			if (splitted.length == 3) {
				String posWN = splitted[0];
				String lemma = splitted[1];
				if (lemma.equals(dep.get_modifier().get_lemma()) && posWN.equals(dep.get_modifier().get_posWN()))
					ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
			}
		} else { //the same dependency relation
			boolean flagHeadMatch = false;
			String[] splittedHead = dirtDep.get_head().split(":");
			String[] splittedMod = dirtDep.get_modifier().split(":");
			if (splittedHead.length == 3) {
				String posWN = splittedHead[0];
				String lemma = splittedHead[1];
				if (lemma.equals(dep.get_head().get_lemma()) && posWN.equals(dep.get_head().get_posWN())) {
					flagHeadMatch = true;
				}
				else if(posWN.equals(dep.get_head().get_posWN())){
					if(posWN.equals("p") || depsContextSimilarity.getSimilarity(lemma, dep.get_head().get_lemma(), dirtDep.get_modifier().concat("I"), dep.get_relation().concat("I")) >= _threshold)
						flagHeadMatch = true;
				}
				else if(lemma.equals(dep.get_head().get_lemma())){
					List<String> suitablePOSWN = Arrays.asList("n","v");
					if(suitablePOSWN.contains(posWN) && suitablePOSWN.contains(dep.get_head().get_posWN()))
						flagHeadMatch = true;
				}
			} else {
				String posWN = dirtDep.get_head();
				if (posWN.equals(dep.get_head().get_posWN())) {
					flagHeadMatch = true;
				}
			}
			if (flagHeadMatch) {
				if (splittedMod.length == 3) {
					String posWN = splittedMod[0];
					String lemma = splittedMod[1];
					if (lemma.equals(dep.get_modifier().get_lemma()) && posWN.equals(dep.get_modifier().get_posWN()))
						ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					else if(posWN.equals(dep.get_modifier().get_posWN())){
						if(posWN.equals("p") || depsContextSimilarity.getSimilarity(lemma, dep.get_modifier().get_lemma(), dirtDep.get_modifier(), dep.get_relation()) >= _threshold)
							ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					}
					else if(lemma.equals(dep.get_modifier().get_lemma())){
						List<String> suitablePOSWN = Arrays.asList("n","v");
						if(suitablePOSWN.contains(posWN) && suitablePOSWN.contains(dep.get_modifier().get_posWN()))
							ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					}
				} else {
					String posWN = dirtDep.get_modifier();
					if (posWN.equals(dep.get_modifier().get_posWN()))
						ret.put(dirtDep.get_modifierPosition(), dep.get_modifier().get_position());
					else{
						if(dirtDep.get_modifierPosition() == getBeginPositionOfDirtElement(originDirtDeps)) {
							if (posWN.equals("n")) {
								//find NP
								List<Integer> beginPositionOfChunk = new ArrayList<>();
								for(Chunk chunk : chunks){
									if(chunk.get_tag().equals("NP") && chunk.get_end() == dep.get_modifier().get_position())
										beginPositionOfChunk.add(chunk.get_begin());

								}
								if(beginPositionOfChunk.size() != 0)
									ret.put(dirtDep.get_modifierPosition(), Collections.max(beginPositionOfChunk));
							} else if (posWN.equals("v")) {
								//find VP
								List<Integer> beginPositionOfChunk = new ArrayList<>();
								for(Chunk chunk : chunks){
									if((chunk.get_tag().equals("VP") || chunk.get_tag().equals("VPS")) && chunk.get_end() == dep.get_modifier().get_position())
										beginPositionOfChunk.add(chunk.get_begin());
								}
								if(beginPositionOfChunk.size() != 0)
									ret.put(dirtDep.get_modifierPosition(), Collections.max(beginPositionOfChunk));
							}
						}
						else if (dirtDep.get_modifierPosition() == getEndPositionOfDirtElement(originDirtDeps)){
							if(posWN.equals("n")){
								//find NP
								List<Integer> endPositionOfChunk = new ArrayList<>();
								for(Chunk chunk : chunks){
									if(chunk.get_tag().equals("NP") && chunk.get_begin() == dep.get_modifier().get_position())
										endPositionOfChunk.add(chunk.get_end());
								}
								if(endPositionOfChunk.size() != 0)
									ret.put(dirtDep.get_modifierPosition(), Collections.min(endPositionOfChunk));
							} else if(posWN.equals("v")){
								//find VP
								List<Integer> endPositionOfChunk = new ArrayList<>();
								for(Chunk chunk : chunks){
									if((chunk.get_tag().equals("VP") || chunk.get_tag().equals("VPS")) && chunk.get_begin() == dep.get_modifier().get_position())
										endPositionOfChunk.add(chunk.get_end());
								}
								if(endPositionOfChunk.size() != 0)
									ret.put(dirtDep.get_modifierPosition(), Collections.min(endPositionOfChunk));
							}
						}
					}
				}
			}
		}
		return ret;
	}

	public static Map<Integer, Integer> getAlignmentSubTree(List<Dependency> dependencies, List<DirtDependency> dirtDependencies) {
		Map<Integer, Integer> ret = new HashMap<>();
		Collections.sort(dependencies, new DepedencyComp());
		Collections.sort(dirtDependencies, new DirtDependencyComp());
		if (getMatchedPath(getDirtDependencyRoot(dirtDependencies), getDependencyRoot(dependencies)).size() != 0) {
			int beginJ = 0;
			for (int i = 0; i < dirtDependencies.size(); i++) {
				boolean flagFound = false;
				for (int j = beginJ; j < dependencies.size(); j++) {
					Map<Integer, Integer> alignedPosition = getMatchedPath(dirtDependencies.get(i), dependencies.get(j));
					if (alignedPosition.size() != 0) {
						ret.putAll(alignedPosition);
						beginJ = j;
						flagFound = true;
						break;
					}
				}
				if (!flagFound)
					return (new HashMap<Integer, Integer>());
			}
		}
		return ret;
	}
	public static Map<Integer, Integer> getAlignmentSubTreeExtendPhrase(List<Dependency> dependencies, List<DirtDependency> dirtDependencies,Map<String,Object> annotation, int sent) {
		Map<Integer, Integer> ret = new HashMap<>();
		Collections.sort(dependencies, new DepedencyComp());
		Collections.sort(dirtDependencies, new DirtDependencyComp());
		if (getMatchedPath(getDirtDependencyRoot(dirtDependencies), getDependencyRoot(dependencies)).size() != 0) {
			int beginJ = 0;
			for (int i = 0; i < dirtDependencies.size(); i++) {
				boolean flagFound = false;
				for (int j = beginJ; j < dependencies.size(); j++) {
					Map<Integer, Integer> alignedPosition = getMatchedPathExtendPhrase(dirtDependencies.get(i), dependencies.get(j), dirtDependencies, Parser.getAllChunkForDirtEngine(annotation).get(sent));
					if (alignedPosition.size() != 0) {
						ret.putAll(alignedPosition);
						beginJ = j;
						flagFound = true;
						break;
					}
				}
				if (!flagFound)
					return (new HashMap<Integer, Integer>());
			}
		}
		return ret;
	}
	public static Map<Integer, Integer> getAlignmentSubTreeSkippingNode(List<Dependency> dependencies, List<DirtDependency> dirtDependencies) {
		Map<Integer, Integer> ret = new HashMap<>();
		Collections.sort(dependencies, new DepedencyComp());
		Collections.sort(dirtDependencies, new DirtDependencyComp());
		List<Integer> matchedPosition = new ArrayList<>();
		if (getMatchedPath(getDirtDependencyRoot(dirtDependencies), getDependencyRoot(dependencies)).size() != 0) {
			int beginJ = 0;
			for (int i = 0; i < dirtDependencies.size(); i++) {
				boolean flagFound = false;
				for (int j = beginJ; j < dependencies.size(); j++) {
						Map<Integer, Integer> alignedPosition = getMatchedPath(dirtDependencies.get(i), dependencies.get(j));
						if (alignedPosition.size() != 0) {
							ret.putAll(alignedPosition);
							matchedPosition.add(j);
							beginJ = j;
							flagFound = true;
							break;
						}
				}
				if (!flagFound) {
					for(int j = 0; j < dependencies.size(); j++) {
						for(int k = 0 ; k < dependencies.size() && k!= j; k++)
							if(dependencies.get(j).get_modifier() == dependencies.get(k).get_head()) {
								Map<Integer, Integer> alignedPosition = getMatchedPath(dirtDependencies.get(i), dependencies.get(j));
								if (alignedPosition.size() != 0) {
									ret.putAll(alignedPosition);
									matchedPosition.add(k);
									flagFound = true;
								}
							}
					}
				}
				if(!flagFound)
					return (new HashMap<Integer, Integer>());
			}
		}
		return ret;
	}
	public static Map<Integer, Integer> getAlignmentSubTreeSkippingNodeExtendPhrase(List<Dependency> dependencies, List<DirtDependency> dirtDependencies, Map<String,Object> annotation, int sent) {
		Map<Integer, Integer> ret = new HashMap<>();
		Collections.sort(dependencies, new DepedencyComp());
		Collections.sort(dirtDependencies, new DirtDependencyComp());
		List<Integer> matchedPosition = new ArrayList<>();
		if (getMatchedPath(getDirtDependencyRoot(dirtDependencies), getDependencyRoot(dependencies)).size() != 0) {
			int beginJ = 0;
			for (int i = 0; i < dirtDependencies.size(); i++) {
				boolean flagFound = false;
				for (int j = beginJ; j < dependencies.size(); j++) {
					Map<Integer, Integer> alignedPosition = getMatchedPathExtendPhrase(dirtDependencies.get(i), dependencies.get(j), dirtDependencies, Parser.getAllChunkForDirtEngine(annotation).get(sent));
					if (alignedPosition.size() != 0) {
						ret.putAll(alignedPosition);
						matchedPosition.add(j);
						beginJ = j;
						flagFound = true;
						break;
					}
				}
				if (!flagFound) {
					for(int j = 0; j < dependencies.size(); j++) {
						for(int k = 0 ; k < dependencies.size() && k!= j; k++)
							if(dependencies.get(j).get_modifier() == dependencies.get(k).get_head()) {
								Map<Integer, Integer> alignedPosition = getMatchedPathExtendPhrase(dirtDependencies.get(i), dependencies.get(j), dirtDependencies, Parser.getAllChunkForDirtEngine(annotation).get(sent));
								if (alignedPosition.size() != 0) {
									ret.putAll(alignedPosition);
									matchedPosition.add(k);
									flagFound = true;
								}
							}
					}
				}
				if(!flagFound)
					return (new HashMap<Integer, Integer>());
			}
		}
		return ret;
	}

	/**
	 * Returns AlignedPosition between dependency trees of text and hypothesis , via DIRT data
	 *
	 * @param textDependencies: List&lt;Dependency&gt;
	 * @param hypoDependencies: List&lt;Dependency&gt;
	 * @return AlignedPosition
	 * @throws
	 * @author bdthinh
	 */
	public static AlignedPosition getAlignment(List<Dependency> textDependencies, List<Dependency> hypoDependencies) {
		try {
			List<AlignedPosition> ret = new ArrayList<>();
			EnDIRT enDIRT = EnFactory.get_DIRT();
			Token rootText = getDependencyRoot(textDependencies).get_modifier();
			Token rootHypo = getDependencyRoot(hypoDependencies).get_modifier();
			System.out.println(rootText.get_lemma() + " <> " + rootHypo.get_lemma());
			Multimap<String, String> dirtElements = enDIRT.lookUp(rootText.get_lemma(), rootHypo.get_lemma());
			Integer countFound = dirtElements.size();
			System.out.println("Number of found: " + countFound);
			int countMatched = 0;
			for (String sourceDirtElement : dirtElements.keySet()) {
				for (String targetDirtElement : dirtElements.get(sourceDirtElement)) {
					List<DirtDependency> sourceDirtDependencies = parseDirtElementToDirtDependency(sourceDirtElement);
					Map<Integer, Integer> alignmenText = getAlignmentSubTree(textDependencies, sourceDirtDependencies);
//					System.out.println("text size "+alignmenText.size());
					List<DirtDependency> targetDirtDependencies = parseDirtElementToDirtDependency(targetDirtElement);
					Map<Integer, Integer> alignmentHypo = getAlignmentSubTree(hypoDependencies, targetDirtDependencies);
					//System.out.println("hypo size "+alignmentHypo.size());
					if (alignmenText.size() != 0 && alignmentHypo.size() != 0) {
						System.out.println(sourceDirtElement + "==>" + targetDirtElement);
						countMatched++;
						int beginTokenSource = Collections.min(alignmenText.values());
						int endTokenSource = Collections.max(alignmenText.values());
						int beginTokenTarget = Collections.min(alignmentHypo.values());
						int endTokenTarget = Collections.max(alignmentHypo.values());
						AlignedPosition o2 = new AlignedPosition(beginTokenSource, endTokenSource, beginTokenTarget, endTokenTarget);
						boolean flag = true;
						for (AlignedPosition o1 : ret) {
							if (isExcluded(o1, o2)) {
								ret.remove(o1);
								ret.add(o2);
								flag = false;
								break;
							}
						}
						if (flag)
							ret.add(o2);
					}
				}
			}
			_coverage = countFound + "," + countMatched;
			if(countMatched != 0 )
				System.out.println("Number of matched: "+ countMatched);
			if (ret.size() != 0)
				return ret.get(0);
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	public static AlignedPosition getAlignmentExtendPhrase(List<Dependency> textDependencies, List<Dependency> hypoDependencies, Map<String, Object> textAnnotation, Map<String, Object> hypoAnnotation, int textSent, int hypoSent) {
		try {
			List<AlignedPosition> ret = new ArrayList<>();
			EnDIRT enDIRT = EnFactory.get_DIRT();
			Token rootText = getDependencyRoot(textDependencies).get_modifier();
			Token rootHypo = getDependencyRoot(hypoDependencies).get_modifier();
			System.out.println(rootText.get_lemma() + " <> " + rootHypo.get_lemma());
			Multimap<String, String> dirtElements = enDIRT.lookUp(rootText.get_lemma(), rootHypo.get_lemma());
			Integer countFound = dirtElements.size();
			System.out.println("Number of found: " + countFound);
			int countMatched = 0;
			for (String sourceDirtElement : dirtElements.keySet()) {
				for (String targetDirtElement : dirtElements.get(sourceDirtElement)) {
					List<DirtDependency> sourceDirtDependencies = parseDirtElementToDirtDependency(sourceDirtElement);
					Map<Integer, Integer> alignmenText = getAlignmentSubTreeExtendPhrase(textDependencies, sourceDirtDependencies, textAnnotation, textSent);
					//System.out.println("text size "+alignmenText.size());
					List<DirtDependency> targetDirtDependencies = parseDirtElementToDirtDependency(targetDirtElement);
					Map<Integer, Integer> alignmentHypo = getAlignmentSubTreeExtendPhrase(hypoDependencies, targetDirtDependencies, hypoAnnotation, hypoSent);
					//System.out.println("hypo size "+alignmentHypo.size());
					if (alignmenText.size() != 0 && alignmentHypo.size() != 0) {
						//System.out.println(sourceDirtElement + "==>" + targetDirtElement);
						countMatched++;
						int beginTokenSource = Collections.min(alignmenText.values());
						int endTokenSource = Collections.max(alignmenText.values());
						int beginTokenTarget = Collections.min(alignmentHypo.values());
						int endTokenTarget = Collections.max(alignmentHypo.values());
						AlignedPosition o2 = new AlignedPosition(beginTokenSource, endTokenSource, beginTokenTarget, endTokenTarget);
						boolean flag = true;
						for (AlignedPosition o1 : ret) {
							if (isExcluded(o1, o2)) {
								ret.remove(o1);
								ret.add(o2);
								flag = false;
								break;
							}
						}
						if (flag)
							ret.add(o2);
					}
				}
			}
			_coverage = countFound + "," + countMatched;
			if(countMatched != 0 )
				System.out.println("Number of matched: "+ countMatched);
			if (ret.size() != 0)
				return ret.get(0);
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	public static AlignedPosition getAlignmentExtendSynset(List<Dependency> textDependencies, List<Dependency> hypoDependencies) {
		try {
			List<AlignedPosition> ret = new ArrayList<>();
			EnDIRT enDIRT = EnFactory.get_DIRT();
			Token rootText = getDependencyRoot(textDependencies).get_modifier();
			Token rootHypo = getDependencyRoot(hypoDependencies).get_modifier();
			System.out.println(rootText.get_lemma() + " <> " + rootHypo.get_lemma());
			Multimap<String, String> dirtElements = ArrayListMultimap.create();
			EnResourceWN wn = new EnResourceWN();
			EnWord ewLeft = new EnWord(rootText);
			List<String> leftwords = new ArrayList<>();
			leftwords.add(rootText.get_lemma());
			List<ISynset> synsets = wn.lookupWordNetSyn(ewLeft);
			for(ISynset synset : synsets) {
				for (IWord iw : synset.getWords()) {
					leftwords.add(iw.getLemma().toLowerCase());
				}
			}
			for(String leftword : leftwords){
				dirtElements.putAll(enDIRT.lookUp(leftword, rootHypo.get_lemma()));
			}
			Integer countFound = dirtElements.size();
			System.out.println("Number of found: " + countFound);
			int countMatched = 0;
			for (String sourceDirtElement : dirtElements.keySet()) {
				for (String targetDirtElement : dirtElements.get(sourceDirtElement)) {
					List<DirtDependency> sourceDirtDependencies = parseDirtElementToDirtDependency(sourceDirtElement);
					Map<Integer, Integer> alignmenText = getAlignmentSubTree(textDependencies, sourceDirtDependencies);
					//System.out.println("text size "+alignmenText.size());
					List<DirtDependency> targetDirtDependencies = parseDirtElementToDirtDependency(targetDirtElement);
					Map<Integer, Integer> alignmentHypo = getAlignmentSubTree(hypoDependencies, targetDirtDependencies);
					//System.out.println("hypo size "+alignmentHypo.size());
					if (alignmenText.size() != 0 && alignmentHypo.size() != 0) {
						//System.out.println(sourceDirtElement + "==>" + targetDirtElement);
						countMatched++;
						int beginTokenSource = Collections.min(alignmenText.values());
						int endTokenSource = Collections.max(alignmenText.values());
						int beginTokenTarget = Collections.min(alignmentHypo.values());
						int endTokenTarget = Collections.max(alignmentHypo.values());
						AlignedPosition o2 = new AlignedPosition(beginTokenSource, endTokenSource, beginTokenTarget, endTokenTarget);
						boolean flag = true;
						for (AlignedPosition o1 : ret) {
							if (isExcluded(o1, o2)) {
								ret.remove(o1);
								ret.add(o2);
								flag = false;
								break;
							}
						}
						if (flag)
							ret.add(o2);
					}
				}
			}
			_coverage = countFound + "," + countMatched;
			if(countMatched != 0 )
				System.out.println("Number of matched: "+ countMatched);
			if (ret.size() != 0)
				return ret.get(0);
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	public static AlignedPosition getAlignmentExtendPhraseSynset(List<Dependency> textDependencies, List<Dependency> hypoDependencies, Map<String, Object> textAnnotation, Map<String, Object> hypoAnnotation, int textSent, int hypoSent) {
		try {
			List<AlignedPosition> ret = new ArrayList<>();
			EnDIRT enDIRT = EnFactory.get_DIRT();
			Token rootText = getDependencyRoot(textDependencies).get_modifier();
			Token rootHypo = getDependencyRoot(hypoDependencies).get_modifier();
			System.out.println(rootText.get_lemma() + " <> " + rootHypo.get_lemma());
//			Multimap<String, String> dirtElements = enDIRT.lookUpSynset(rootText, rootHypo);
			Multimap<String, String> dirtElements = ArrayListMultimap.create();
			EnResourceWN wn = new EnResourceWN();
			EnWord ewLeft = new EnWord(rootText);
			List<String> leftwords = new ArrayList<>();
			leftwords.add(rootText.get_lemma());
			List<ISynset> synsets = wn.lookupWordNetSyn(ewLeft);
			for(ISynset synset : synsets) {
				for (IWord iw : synset.getWords()) {
					leftwords.add(iw.getLemma().toLowerCase());
				}
			}
			for(String leftword : leftwords){
				dirtElements.putAll(enDIRT.lookUp(leftword, rootHypo.get_lemma()));
			}
			Integer countFound = dirtElements.size();
			System.out.println("Number of found: " + countFound);
			int countMatched = 0;
			for (String sourceDirtElement : dirtElements.keySet()) {
				for (String targetDirtElement : dirtElements.get(sourceDirtElement)) {
					List<DirtDependency> sourceDirtDependencies = parseDirtElementToDirtDependency(sourceDirtElement);
					Map<Integer, Integer> alignmenText = getAlignmentSubTreeExtendPhrase(textDependencies, sourceDirtDependencies, textAnnotation, textSent);
					//System.out.println("text size "+alignmenText.size());
					List<DirtDependency> targetDirtDependencies = parseDirtElementToDirtDependency(targetDirtElement);
					Map<Integer, Integer> alignmentHypo = getAlignmentSubTreeExtendPhrase(hypoDependencies, targetDirtDependencies, hypoAnnotation, hypoSent);
					//System.out.println("hypo size "+alignmentHypo.size());
					if (alignmenText.size() != 0 && alignmentHypo.size() != 0) {
						//System.out.println(sourceDirtElement + "==>" + targetDirtElement);
						countMatched++;
						int beginTokenSource = Collections.min(alignmenText.values());
						int endTokenSource = Collections.max(alignmenText.values());
						int beginTokenTarget = Collections.min(alignmentHypo.values());
						int endTokenTarget = Collections.max(alignmentHypo.values());
						AlignedPosition o2 = new AlignedPosition(beginTokenSource, endTokenSource, beginTokenTarget, endTokenTarget);
						boolean flag = true;
						for (AlignedPosition o1 : ret) {
							if (isExcluded(o1, o2)) {
								ret.remove(o1);
								ret.add(o2);
								flag = false;
								break;
							}
						}
						if (flag)
							ret.add(o2);
					}
				}
			}
			_coverage = countFound + "," + countMatched;
			if(countMatched != 0 )
				System.out.println("Number of matched: "+ countMatched);
			if (ret.size() != 0)
				return ret.get(0);
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	public static AlignedPosition getAlignmentSkippingNode(List<Dependency> textDependencies, List<Dependency> hypoDependencies) {
		try {
			List<AlignedPosition> ret = new ArrayList<>();
			EnDIRT enDIRT = EnFactory.get_DIRT();
			Token rootText = getDependencyRoot(textDependencies).get_modifier();
			Token rootHypo = getDependencyRoot(hypoDependencies).get_modifier();
			System.out.println(rootText.get_lemma() + " <> " + rootHypo.get_lemma());
			Multimap<String, String> dirtElements = enDIRT.lookUp(rootText.get_lemma(), rootHypo.get_lemma());
			Integer countFound = dirtElements.size();
			System.out.println("Number of found: " + countFound);
			int countMatched = 0;
			for (String sourceDirtElement : dirtElements.keySet()) {
				for (String targetDirtElement : dirtElements.get(sourceDirtElement)) {
					List<DirtDependency> sourceDirtDependencies = parseDirtElementToDirtDependency(sourceDirtElement);
					Map<Integer, Integer> alignmenText = getAlignmentSubTreeSkippingNode(textDependencies, sourceDirtDependencies);
					//System.out.println("text size "+alignmenText.size());
					List<DirtDependency> targetDirtDependencies = parseDirtElementToDirtDependency(targetDirtElement);
					Map<Integer, Integer> alignmentHypo = getAlignmentSubTreeSkippingNode(hypoDependencies, targetDirtDependencies);
					//System.out.println("hypo size "+alignmentHypo.size());
					if (alignmenText.size() != 0 && alignmentHypo.size() != 0) {
						//System.out.println(sourceDirtElement + "==>" + targetDirtElement);
						countMatched++;
						int beginTokenSource = Collections.min(alignmenText.values());
						int endTokenSource = Collections.max(alignmenText.values());
						int beginTokenTarget = Collections.min(alignmentHypo.values());
						int endTokenTarget = Collections.max(alignmentHypo.values());
						AlignedPosition o2 = new AlignedPosition(beginTokenSource, endTokenSource, beginTokenTarget, endTokenTarget);
						boolean flag = true;
						for (AlignedPosition o1 : ret) {
							if (isExcluded(o1, o2)) {
								ret.remove(o1);
								ret.add(o2);
								flag = false;
								break;
							}
						}
						if(flag)
							ret.add(o2);
					}
				}
			}
			_coverage = countFound + "," + countMatched;
			if(countMatched != 0 )
				System.out.println("Number of matched: "+ countMatched);
			if (ret.size() != 0)
				return ret.get(0);
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	public static AlignedPosition getAlignmentSkippingNodeExtendSynset(List<Dependency> textDependencies, List<Dependency> hypoDependencies) {
		try {
			List<AlignedPosition> ret = new ArrayList<>();
			EnDIRT enDIRT = EnFactory.get_DIRT();
			Token rootText = getDependencyRoot(textDependencies).get_modifier();
			Token rootHypo = getDependencyRoot(hypoDependencies).get_modifier();
			System.out.println(rootText.get_lemma() + " <> " + rootHypo.get_lemma());
//			Multimap<String, String> dirtElements = enDIRT.lookUpSynset(rootText, rootHypo);
			Multimap<String, String> dirtElements = ArrayListMultimap.create();
			EnResourceWN wn = new EnResourceWN();
			EnWord ewLeft = new EnWord(rootText);
			List<String> leftwords = new ArrayList<>();
			leftwords.add(rootText.get_lemma());
			List<ISynset> synsets = wn.lookupWordNetSyn(ewLeft);
			for(ISynset synset : synsets) {
				for (IWord iw : synset.getWords()) {
					leftwords.add(iw.getLemma().toLowerCase());
				}
			}
			for(String leftword : leftwords){
				dirtElements.putAll(enDIRT.lookUp(leftword, rootHypo.get_lemma()));
			}
			Integer countFound = dirtElements.size();
			System.out.println("Number of found: " + countFound);
			int countMatched = 0;
			for (String sourceDirtElement : dirtElements.keySet()) {
				for (String targetDirtElement : dirtElements.get(sourceDirtElement)) {
					List<DirtDependency> sourceDirtDependencies = parseDirtElementToDirtDependency(sourceDirtElement);
					Map<Integer, Integer> alignmenText = getAlignmentSubTreeSkippingNode(textDependencies, sourceDirtDependencies);
					//System.out.println("text size "+alignmenText.size());
					List<DirtDependency> targetDirtDependencies = parseDirtElementToDirtDependency(targetDirtElement);
					Map<Integer, Integer> alignmentHypo = getAlignmentSubTreeSkippingNode(hypoDependencies, targetDirtDependencies);
					//System.out.println("hypo size "+alignmentHypo.size());
					if (alignmenText.size() != 0 && alignmentHypo.size() != 0) {
						//System.out.println(sourceDirtElement + "==>" + targetDirtElement);
						countMatched++;
						int beginTokenSource = Collections.min(alignmenText.values());
						int endTokenSource = Collections.max(alignmenText.values());
						int beginTokenTarget = Collections.min(alignmentHypo.values());
						int endTokenTarget = Collections.max(alignmentHypo.values());
						AlignedPosition o2 = new AlignedPosition(beginTokenSource, endTokenSource, beginTokenTarget, endTokenTarget);
						boolean flag = true;
						for (AlignedPosition o1 : ret) {
							if (isExcluded(o1, o2)) {
								ret.remove(o1);
								ret.add(o2);
								flag = false;
								break;
							}
						}
						if (flag)
							ret.add(o2);
					}
				}
			}
			_coverage = countFound + "," + countMatched;
			if(countMatched != 0 )
				System.out.println("Number of matched: "+ countMatched);
			if (ret.size() != 0)
				return ret.get(0);
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	public static AlignedPosition getAlignmentSkippingNodeExtendPhrase(List<Dependency> textDependencies, List<Dependency> hypoDependencies, Map<String, Object> textAnnotation, Map<String, Object> hypoAnnotation, int textSent, int hypoSent) {
		try {
			List<AlignedPosition> ret = new ArrayList<>();
			EnDIRT enDIRT = EnFactory.get_DIRT();
			Token rootText = getDependencyRoot(textDependencies).get_modifier();
			Token rootHypo = getDependencyRoot(hypoDependencies).get_modifier();
			System.out.println(rootText.get_lemma() + " <> " + rootHypo.get_lemma());
			Multimap<String, String> dirtElements = enDIRT.lookUp(rootText.get_lemma(), rootHypo.get_lemma());
			Integer countFound = dirtElements.size();
			System.out.println("Number of found: " + countFound);
			int countMatched = 0;
			for (String sourceDirtElement : dirtElements.keySet()) {
				for (String targetDirtElement : dirtElements.get(sourceDirtElement)) {
					List<DirtDependency> sourceDirtDependencies = parseDirtElementToDirtDependency(sourceDirtElement);
					Map<Integer, Integer> alignmenText = getAlignmentSubTreeSkippingNodeExtendPhrase(textDependencies, sourceDirtDependencies, textAnnotation, textSent);
					List<DirtDependency> targetDirtDependencies = parseDirtElementToDirtDependency(targetDirtElement);
					Map<Integer, Integer> alignmentHypo = getAlignmentSubTreeSkippingNodeExtendPhrase(hypoDependencies, targetDirtDependencies, hypoAnnotation, hypoSent);
					if (alignmenText.size() != 0 && alignmentHypo.size() != 0) {
						countMatched++;
						int beginTokenSource = Collections.min(alignmenText.values());
						int endTokenSource = Collections.max(alignmenText.values());
						int beginTokenTarget = Collections.min(alignmentHypo.values());
						int endTokenTarget = Collections.max(alignmentHypo.values());
						AlignedPosition o2 = new AlignedPosition(beginTokenSource, endTokenSource, beginTokenTarget, endTokenTarget);
						boolean flag = true;
						for (AlignedPosition o1 : ret) {
							if (isExcluded(o1, o2)) {
								ret.remove(o1);
								ret.add(o2);
								flag = false;
								break;
							}
						}
						if (flag)
							ret.add(o2);
					}
				}
			}
			_coverage = countFound + "," + countMatched;
			if(countMatched != 0 )
				System.out.println("Number of matched: "+ countMatched);
			if (ret.size() != 0)
				return ret.get(0);
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	public static AlignedPosition getAlignmentSkippingNodeExtendPhraseSynset(List<Dependency> textDependencies, List<Dependency> hypoDependencies, Map<String, Object> textAnnotation, Map<String, Object> hypoAnnotation, int textSent, int hypoSent) {
		try {
			List<AlignedPosition> ret = new ArrayList<>();
			EnDIRT enDIRT = EnFactory.get_DIRT();
			Token rootText = getDependencyRoot(textDependencies).get_modifier();
			Token rootHypo = getDependencyRoot(hypoDependencies).get_modifier();
			System.out.println(rootText.get_lemma() + " <> " + rootHypo.get_lemma());
//			Multimap<String, String> dirtElements = enDIRT.lookUpSynset(rootText, rootHypo);
			Multimap<String, String> dirtElements = ArrayListMultimap.create();
			EnResourceWN wn = new EnResourceWN();
			EnWord ewLeft = new EnWord(rootText);
			List<String> leftwords = new ArrayList<>();
			leftwords.add(rootText.get_lemma());
			List<ISynset> synsets = wn.lookupWordNetSyn(ewLeft);
			for(ISynset synset : synsets) {
				for (IWord iw : synset.getWords()) {
					leftwords.add(iw.getLemma().toLowerCase());
				}
			}
			for(String leftword : leftwords){
				dirtElements.putAll(enDIRT.lookUp(leftword, rootHypo.get_lemma()));
			}
			Integer countFound = dirtElements.size();
			System.out.println("Number of found: " + countFound);
			int countMatched = 0;
			for (String sourceDirtElement : dirtElements.keySet()) {
				for (String targetDirtElement : dirtElements.get(sourceDirtElement)) {
					List<DirtDependency> sourceDirtDependencies = parseDirtElementToDirtDependency(sourceDirtElement);
					Map<Integer, Integer> alignmenText = getAlignmentSubTreeSkippingNodeExtendPhrase(textDependencies, sourceDirtDependencies, textAnnotation, textSent);
					List<DirtDependency> targetDirtDependencies = parseDirtElementToDirtDependency(targetDirtElement);
					Map<Integer, Integer> alignmentHypo = getAlignmentSubTreeSkippingNodeExtendPhrase(hypoDependencies, targetDirtDependencies, hypoAnnotation, hypoSent);
					if (alignmenText.size() != 0 && alignmentHypo.size() != 0) {
						countMatched++;
						int beginTokenSource = Collections.min(alignmenText.values());
						int endTokenSource = Collections.max(alignmenText.values());
						int beginTokenTarget = Collections.min(alignmentHypo.values());
						int endTokenTarget = Collections.max(alignmentHypo.values());
						AlignedPosition o2 = new AlignedPosition(beginTokenSource, endTokenSource, beginTokenTarget, endTokenTarget);
						boolean flag = true;
						for (AlignedPosition o1 : ret) {
							if (isExcluded(o1, o2)) {
								ret.remove(o1);
								ret.add(o2);
								flag = false;
								break;
							}
						}
						if (flag)
							ret.add(o2);
					}
				}
			}
			_coverage = countFound + "," + countMatched;
			if(countMatched != 0 )
				System.out.println("Number of matched: "+ countMatched);
			if (ret.size() != 0)
				return ret.get(0);
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns true or false if AlignedPosition o1 is excluded or not.
	 *
	 * @param o1: AlignedPosition
	 * @param o2: AlignedPosition
	 * @return true or false if AlignedPosition o1 is excluded or not.
	 * @throws
	 * @author bdthinh
	 */
	private static boolean isExcluded(AlignedPosition o1, AlignedPosition o2) {
		boolean flag;
		if (o1.get_beginTokenTarget() <= o2.get_beginTokenTarget() && o2.get_endTokenTarget() <= o1.get_endTokenTarget()) {
			flag = true;
			if (o1.get_beginTokenTarget() == o2.get_beginTokenTarget() && o1.get_endTokenTarget() == o2.get_endTokenTarget()) {
				if (o1.get_beginTokenSource() <= o2.get_beginTokenSource() && o2.get_endTokenSource() <= o1.get_endTokenSource())
					flag = true;
				else if (o1.getLengthSource() > o2.getLengthSource())
					flag = true;
				else
					flag = false;
			}
		} else if (o1.getLengthTarget() > o2.getLengthTarget())
			flag = true;
		else
			flag = false;
		return flag;
	}


}
