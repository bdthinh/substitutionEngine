package tifmo.coreNLP;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import tifmo.similarityComp.ChunkComp;
import tifmo.utils.EnUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by bdthinh on 10/21/14.
 */
public class Parser {
	private static StanfordCoreNLP pipeline = null;

	private static void initPipeline() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		pipeline = new StanfordCoreNLP(props);
	}

	private static List<Integer> getSortedTokenIndexesOfChunk(String indexedChunk) {
		List<Integer> numbers = new ArrayList<Integer>();
		char[] cString = indexedChunk.toCharArray();
		for (int i = 0; i < cString.length; i++) {
			if (Character.isDigit(cString[i])) {
				int j;
				for (j = i + 1; j < cString.length; j++)
					if (!Character.isDigit(cString[j]))
						break;
				String newNumber = new String(cString, i, j - i);
				numbers.add(Integer.valueOf(newNumber));
				i = j;
			}
		}
		Collections.sort(numbers);//ascending
		return numbers;
	}

	private static String getIndexedTree(String tree) {
		int i = 1;
		List<String> lv1 = new ArrayList<String>();
		tree = tree.replaceAll("[\\$]", "CUR").replaceAll(Pattern.quote("+"), "PLUS").replaceAll(Pattern.quote("$"), "CD");

		Pattern pattern = Pattern.compile("\\([^()]*\\)");
		Matcher matcher = pattern.matcher(tree);
		while (matcher.find()) {
			lv1.add(matcher.group());
		}
		for (String each : lv1) {
			tree = tree.replaceFirst("\\(" + each.substring(1, each.length() - 1) + "\\)", String.valueOf(i++));
		}
		return tree;
	}

	private static List<Chunk> parseSentenceToChunk(List<Token> tokens, Tree tree) {
		//String[] chunktag = new String[]{"NP", "VP", "PP", "ADVP", "ADJP", "SBAR", "INTJ", "QP", "NP-TMP"};
		String[] chunktag = new String[]{"NP", "VP", "PP"};
		List<Chunk> chunks = new ArrayList<Chunk>();

		String parseTree = getIndexedTree(tree.toString());
		char[] treeCharArray = parseTree.toCharArray();
		int beginOffset = 1;
		int endOffset = 0;
		int count = 0;
		for (int i = 0; i < treeCharArray.length; i++) {
			if (treeCharArray[i] == '(') {
				beginOffset = i;
				count = 1;
				for (int j = i + 1; j < treeCharArray.length; j++) {
					if (treeCharArray[j] == '(') {
						count++;
					} else if (treeCharArray[j] == ')')
						count--; //when meeting ')'
					if (count == 0) {
						endOffset = j++;
						break;
					}
				}
				String chunkString = new String(treeCharArray, beginOffset + 1, endOffset - beginOffset - 1);

				String tagString = chunkString.split(" ")[0];

				for (String eachTag : chunktag) {
					if (tagString.equals(eachTag)) {
						List<Integer> indexes = getSortedTokenIndexesOfChunk(chunkString);
						int begin = indexes.get(0);
						int end = indexes.get(indexes.size() - 1);
						List<Token> tokenChunk = new ArrayList<Token>();
						for (int k = begin; k <= end; k++) {
							tokenChunk.add(tokens.get(k));
						}
						chunks.add(new Chunk(tokenChunk, tagString, begin, end));
						break;
					}
				}
			}
		}
		return chunks;
	}

	private static List<Dependency> parseSentenceToDependency(List<Token> tokens, SemanticGraph deptree) {
		List<Dependency> dependencies = new ArrayList<Dependency>();
		String[] lines = deptree.toList().split("\n");
		int i = 0;
		try {
			for (i = 0; i < lines.length; i++) {
				String[] elements = lines[i].split("\\(|\\,|\\)");
				String relation = elements[0];
				Token tokenHead = null;
				Token tokenModifier = null;
				if(elements.length == 3){
					tokenHead = tokens.get(Integer.parseInt(elements[1].trim().split("-")[elements[1].trim().split("-").length - 1]));
					tokenModifier = tokens.get(Integer.parseInt(elements[2].trim().split("-")[elements[2].trim().split("-").length - 1]));
				}
				else{
					String patternHeadPosition = "-(([0-9])+),";
					Pattern pattern = Pattern.compile(patternHeadPosition);
					Matcher matcher = pattern.matcher(lines[i]);
					if(matcher.find())
						tokenHead = tokens.get(Integer.parseInt(matcher.group(1)));
					String patternModifierPosition = "-(([1-9])+)\\)";
					pattern = Pattern.compile(patternModifierPosition);
					matcher = pattern.matcher(lines[i]);
					if(matcher.find())
						tokenModifier = tokens.get(Integer.parseInt(matcher.group(1)));
				}
				if(tokenHead != null && tokenModifier != null)
					dependencies.add(new Dependency(relation, tokenHead, tokenModifier));
			}
		} catch(IndexOutOfBoundsException ex){
			System.err.println("At line :"+lines[i]);
			ex.printStackTrace();
		}
		return dependencies;
	}

	private static List<Node> parseSentenceToNode(List<Token> tokens, SemanticGraph deptree) {
		List<Node> nodes = new ArrayList<Node>();
		for (Token token : tokens) {
			Node node = new Node(token);
			nodes.add(node);
		}
		String[] lines = deptree.toList().split("\n");
		for (int i = 0; i < lines.length; i++) {
			String[] elements = lines[i].split("\\(|\\,|\\)");
			Token tokenHead = tokens.get(Integer.parseInt(elements[1].trim().split("-")[elements[1].trim().split("-").length - 1]));
			Token tokenModifier = tokens.get(Integer.parseInt(elements[2].trim().split("-")[elements[2].trim().split("-").length - 1]));
			for (int j = 0; j < nodes.size(); j++) {
				if (nodes.get(j).get_tokenHead() == tokenHead)
					for (int k = 0; k < nodes.size(); k++)
						if (nodes.get(k).get_tokenHead() == tokenModifier) {
							Map<Node, String> mns = nodes.get(j).get_modifiers();
							mns.put(nodes.get(k), elements[0].trim());
							nodes.get(j).set_modifiers(mns);
						}
			}
		}
		return nodes;
	}

	public static Map<Integer, List<Token>> getDeepCopyTokens(Map<Integer, List<Token>> tokensOfSent) {
		Map<Integer, List<Token>> copy = new HashMap<>();
		for(Integer sentNumber : tokensOfSent.keySet()){
			List<Token> tokens = tokensOfSent.get(sentNumber);
			List<Token> copyOfTokens = new ArrayList<>();
			for(Token token : tokens)
				copyOfTokens.add(new Token(token));
			copy.put(sentNumber, copyOfTokens);
		}
		return copy;
	}

	public static Map<String, Object> getDeepCopyAnnotation(Map<String, Object> source) {
		Map<String, Object> target = new HashMap<>();
		for (String type : source.keySet()) {
			if (type.equals("tokens")) {
				Map<Integer, List<Token>> tokensOfSent = (Map<Integer, List<Token>>) source.get(type);
				Map<Integer, List<Token>> copyOfTokensOfSent = new HashMap<>();
				for (Integer sentNum : tokensOfSent.keySet()) {
					List<Token> tokens = tokensOfSent.get(sentNum);
					List<Token> copyOfTokens = new ArrayList<>();
					for (int i = 0; i < tokens.size(); i++) {
						copyOfTokens.add(new Token(tokens.get(i)));
					}
					copyOfTokensOfSent.put(sentNum, copyOfTokens);
				}
				target.put(type, copyOfTokensOfSent);
			} else
				target.put(type, source.get(type));
		}
		return target;
	}

	public static Map<String, Object> parseTextToAnnotation(String text) {
		if (pipeline == null)
			initPipeline();
		Annotation document = new Annotation(text);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		Map<Integer, Tree> treesOfSent = new HashMap<>();
		Map<Integer, SemanticGraph> dependencyOfSent = new HashMap<>();
		Map<Integer, SemanticGraph> collapsedDependencyOfSent = new HashMap<>();
		Map<Integer, List<Token>> tokensOfSent = new HashMap<>();
		Map<Integer, List<Chunk>> chunksOfSent = new HashMap<>();
		Map<Integer, ChunkTable> chunkTablesOfSent = new HashMap<>();
		int iSent = 1;
		for (CoreMap sentence : sentences) {
			int iToken = 1;
			List<Token> tokens = new ArrayList<>();
			tokens.add(0, new Token(0, 0, 0, "ROOT", "", ""));

			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				int beginOffset = token.beginPosition();
				int endOffset = token.endPosition();
				String word = token.get(TextAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);
				String ne = token.get(NamedEntityTagAnnotation.class);
				Token tmp = new Token(iToken++, beginOffset, endOffset, word, pos, ne);
				tmp.setPosWNAndLemma();
				tokens.add(tmp);

				tokensOfSent.put(iSent, tokens);
			}
			treesOfSent.put(iSent, sentence.get(TreeCoreAnnotations.TreeAnnotation.class).children()[0]);
			dependencyOfSent.put(iSent, sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
			collapsedDependencyOfSent.put(iSent, sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class));
			List<Chunk> chunks = parseSentenceToChunk(tokens, treesOfSent.get(iSent));
			chunksOfSent.put(iSent, chunks);
			chunkTablesOfSent.put(iSent, new ChunkTable().set_table(chunks));
			iSent++;
		}
		Map<Integer, CorefChain> graph = document.get(CorefCoreAnnotations.CorefChainAnnotation.class);

		Map<String, Object> ret = new HashMap<>();

		String[] keys = new String[]{"tokens", "chunks", "trees", "dependencies", "collapsedDependencies", "coref", "chunkTable", "text"};
		Object[] values = new Object[]{tokensOfSent, chunksOfSent, treesOfSent, dependencyOfSent, collapsedDependencyOfSent, graph, chunkTablesOfSent, text};

		for (int i = 0; i < keys.length; i++)
			ret.put(keys[i], values[i]);
		return ret;
	}

	public static Map<Integer, List<Token>> getTokensInAnnotation(Map<String, Object> annotation) {
		return ((Map<Integer, List<Token>>) annotation.get("tokens"));
	}

	public static Map<Integer, List<Chunk>> getChunksInAnnotation(Map<String, Object> annotation) {
		return (Map<Integer, List<Chunk>>) annotation.get("chunks");
	}

	public static Map<Integer, Tree> getTreesInAnnotation(Map<String, Object> annotation) {
		return (Map<Integer, Tree>) annotation.get("trees");
	}

	public static Map<Integer, SemanticGraph> getDependenciesInAnnotation(Map<String, Object> annotation) {
		return (Map<Integer, SemanticGraph>) annotation.get("dependencies");
	}

	public static Map<Integer, SemanticGraph> getCollapsedDependenciesInAnnotation(Map<String, Object> annotation) {
		return (Map<Integer, SemanticGraph>) annotation.get("collapsedDependencies");
	}

	public static Map<Integer, String> getStringOfTrees(Map<String, Object> annotation) {
		Map<Integer, String> ret = new HashMap<>();
		Map<Integer, Tree> trees = getTreesInAnnotation(annotation);
		for (Integer sent : trees.keySet()) {
			ret.put(sent, trees.get(sent).toString());
		}
		return ret;
	}

	public static int getNumberOfSent(Map<String, Object> annotation) {
		return ((Map<Integer, List<Token>>) annotation.get("tokens")).size();
	}

	public static String getTextFromTokens(Map<Integer, List<Token>> tokens) {
		String ret = "";
		for (int i = 1; i <= tokens.keySet().size(); i++) {
			List<Token> tmp = tokens.get(i);
			for (int j = 1; j < tmp.size(); j++)
				ret = ret + tmp.get(j).get_word() + " ";
		}
		return ret.trim();
	}

	public static String getTextFromTokens(List<Token> tokens) {
		String ret = "";
		for (int i = 1; i < tokens.size(); i++)
			ret = ret + tokens.get(i).get_word() + " ";
		return ret.trim();
	}

	public static Map<Integer, List<Dependency>> getDependency(Map<String, Object> annotation) {
		Map<Integer, List<Dependency>> ret = new HashMap<>();
		Map<Integer, List<Token>> tokens = (Map<Integer, List<Token>>) annotation.get("tokens");
		Map<Integer, SemanticGraph> dependencyOfSent = getDependenciesInAnnotation(annotation);
		for (int i = 1; i <= tokens.keySet().size(); i++) {
			List<Dependency> tmp = parseSentenceToDependency(tokens.get(i), dependencyOfSent.get(i));
			ret.put(i, tmp);
		}
		return ret;
	}

	public static Map<Integer, List<Dependency>> getCollapsedDependency(Map<String, Object> annotation){
		Map<Integer, List<Dependency>> ret = new HashMap<>();
		Map<Integer, List<Token>> tokens = (Map<Integer, List<Token>>) annotation.get("tokens");
		Map<Integer, SemanticGraph> dependencyOfSent = (Map<Integer, SemanticGraph>) annotation.get("collapsedDependencies");
		for (int i = 1; i <= tokens.keySet().size(); i++) {
			List<Dependency> tmp = parseSentenceToDependency(tokens.get(i), dependencyOfSent.get(i));
			ret.put(i, tmp);
		}
		return ret;
	}

	public static Map<Integer, List<Chunk>> getDeepCopyAllChunkForPPEngine(Map<Integer, List<Chunk>> chunks) {
		Map<Integer, List<Chunk>> ret = new HashMap<Integer, List<Chunk>>();
		for (Integer sentNumber : chunks.keySet()) {
			List<Chunk> chunk = chunks.get(sentNumber);
			List<Chunk> copyOfChunk = new ArrayList<Chunk>();
			for (Chunk ck : chunk)
				copyOfChunk.add(new Chunk(ck));
			ret.put(sentNumber, copyOfChunk);
		}
		return ret;
	}

	public static List<Chunk> getShallowCopyListChunk(List<Chunk> chunks) {
		List<Chunk> ret= new ArrayList<>();
			for (Chunk ck : chunks)
				ret.add(ck);
		return ret;
	}

	public static List<Chunk> getDeepCopyListChunk(List<Chunk> chunks) {
		List<Chunk> ret= new ArrayList<>();
		for (Chunk ck : chunks)
			ret.add(new Chunk(ck));
		return ret;
	}

	public static Map<Integer, List<Chunk>> getAllChunkForDirtEngine(Map<String, Object> originAnno){
		Map<String, Object> annotation = getDeepCopyAnnotation(originAnno);
		Map<Integer, List<Chunk>> ret = new HashMap<Integer, List<Chunk>>();
		Map<Integer, List<Chunk>> chunksOfSent = (Map<Integer, List<Chunk>>) annotation.get("chunks");
		List<String> acceptedTags = new ArrayList<>(Arrays.asList("NP", "VP", "PP"));
		for (Integer sent : chunksOfSent.keySet()) {
			List<Chunk> tmp = new ArrayList<>();
			List<Chunk> chunks = chunksOfSent.get(sent);

			for (Chunk chunk : chunks) {
				if (acceptedTags.contains(chunk.get_tag()))
					tmp.add(chunk);

				if (chunk.get_tag().equals("VP")) {
					for (Chunk target : chunks) {
						if (target.get_tag().equals("NP")
										&& (chunk.get_begin() < target.get_begin() && chunk.get_end() >= target.get_end())) {
							Chunk ck = new Chunk(chunk.get_tokens().subList(0, target.get_begin() - chunk.get_begin()), "VPS", chunk.get_begin(), target.get_begin() - 1);
							tmp.add(ck);
							break;
						}
					}
				}
			}
			ret.put(sent, tmp);
			Collections.sort(tmp, new ChunkComp());
		}
		return ret;
	}

	public static Map<Integer, List<Chunk>> getAllChunkForPPEngine (Map<String, Object> originAnno) {
		Map<String, Object> annotation = getDeepCopyAnnotation(originAnno);
		Map<Integer, List<Chunk>> ret = new HashMap<Integer, List<Chunk>>();
		Map<Integer, List<Chunk>> chunksOfSent = (Map<Integer, List<Chunk>>) annotation.get("chunks");
		List<String> acceptedTags = new ArrayList<>(Arrays.asList("NP", "VP", "PP"));

		for (Integer sent : chunksOfSent.keySet()) {
			List<Chunk> subret = new ArrayList<>();
			List<Chunk> chunksEachSent = chunksOfSent.get(sent);

//			System.out.println("Begin--------");
//			for(Chunk ck : chunksEachSent)
//				ck.printOut();
//			System.out.println("--------");
			for (Chunk c1 : chunksEachSent) {
				if (acceptedTags.contains(c1.get_tag())){
						subret.add(c1.trim());
				}
			}
			chunksEachSent = Parser.getShallowCopyListChunk(subret);
			for(Chunk chunk : chunksEachSent) {
				if (chunk.get_tag().equals("VP")) {
					boolean flag = false;
					for (Chunk target : chunksEachSent) {
						if (target.get_tag().equals("PP")
										&& (chunk.get_begin() < target.get_begin() && target.get_end() <= chunk.get_end())) {
							flag = true;
							Chunk ck = new Chunk(chunk.get_tokens().subList(0, target.get_begin() - chunk.get_begin()), "VPP", chunk.get_begin(), target.get_begin() - 1);
							boolean flagTmp = true;
							for (Chunk eachChunk : subret) {
								if (eachChunk.get_tag().equals("VPP"))
									if (eachChunk.get_begin() == ck.get_begin() && eachChunk.get_end() == ck.get_end()) {
										flagTmp = false;
										break;
									}
							}
							if (flagTmp)
								subret.add(ck.trim());
						}
						else if (target.get_tag().equals("NP")
											&& (chunk.get_begin() < target.get_begin() && target.get_end() <= chunk.get_end())){
							flag = true;
							Chunk ck = new Chunk(chunk.get_tokens().subList(0, target.get_begin() - chunk.get_begin()), "VPN", chunk.get_begin(), target.get_begin() - 1);
							boolean flagTmp = true;
							for (Chunk eachChunk : subret) {
								if (eachChunk.get_tag().equals("VPN"))
									if (eachChunk.get_begin() == ck.get_begin() && eachChunk.get_end() == ck.get_end()) {
										flagTmp = false;
										break;
									}
							}
							if (flagTmp)
								subret.add(ck.trim());
						}
					}
//					if(flag && (chunk.get_tokens().size() -1 ) >= 5)
//						subret.remove(chunk);
				}
			}
//			chunksEachSent = Parser.getShallowCopyListChunk(subret);
//			for(Chunk chunk : chunksEachSent){
//				if(chunk.get_tag().equals("VP") || chunk.get_tag().equals("VPP") || chunk.get_tag().equals("VPN")) {
//					int position = chunk.getINPositionInVPEndWithIN();
//					if (position != (chunk.get_tokens().size() - 1)) {
//						Chunk ck = chunk.getChunkFilterINPositionInVPEndWithIN(position).trim();
//						subret.remove(chunk);
//						boolean flagTmp = true;
//						for(Chunk eachChunk : subret)
//							if(eachChunk.get_begin() == ck.get_begin() && eachChunk.get_end() == ck.get_end())
//								flagTmp = false;
//						if(flagTmp)
//							subret.add(ck);
//					}
//				}
//
////				else if (chunk.get_tag().equals("NP")){
////					int position = chunk.getDTPositionInNPBeginWithDT();
////					if(position != 0){
////						subret.add(chunk.getChunkFilterDTPositionInNPBeginWithDT(position).trim());
////						subret.remove(chunk);
////					}
////					else if ((position = chunk.getPOSPositionInNPEndWithNoun()) != 0){
////						subret.add(chunk.getChunkFilterPOSPositionInNPEndWithPOS(position).trim());
////						subret.remove(chunk);
////					}
////				}
//			}
			chunksEachSent = Parser.getShallowCopyListChunk(subret);
			for (Chunk c1 : chunksEachSent) {
				for (Chunk c2 : chunksEachSent) {
					if (c1 != c2 && c1.get_tag().equals(c2.get_tag()) && isExcluded(c1, c2)) {
						subret.remove(c1);
						break;
					}
				}
			}
			ret.put(sent, subret);
		}

		for (Integer sent : ret.keySet()) {
			List<Chunk> values = ret.get(sent);
			for (int i = 0; i < values.size(); i++) {
				if (isFiltered(values.get(i))) {
					values.remove(i);
					i--;
				}
			}
			Collections.sort(values, new ChunkComp());
//			for(Chunk ck : values)
//				ck.printOut();
			ret.put(sent, values);
		}
		return ret;
	}

	private static boolean isExcluded(Chunk c1, Chunk c2) {
		if(c1.get_begin() <= c2.get_begin() && c2.get_end() <= c1.get_end())
			return true;
		return false;
	}

	private static boolean isFiltered(Chunk chunk) {
		if (chunk.isAllCardinalNumber()
						|| chunk.containAllNNP()
						|| chunk.containEscapeCharacters()
						|| chunk.isAllFunctionWords()
						|| chunk.startWithNE()
						|| chunk.startWithTo()
						|| chunk.containAcronyms()
						|| chunk.containLexiconNegationCue()
						|| chunk.isNNPWithPOS()
						|| chunk.isNPOneToken()
						|| chunk.isPrepositionPhrase())
			return true;
		return false;
	}
}
