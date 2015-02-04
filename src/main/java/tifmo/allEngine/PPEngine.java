package tifmo.allEngine;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import tifmo.coreNLP.*;
import tifmo.en.EnPPDB;
import tifmo.en.EnResourceWN;
import tifmo.en.EnWordNet;
import tifmo.utils.*;

import java.io.*;
import java.util.*;

/**
 * Created by bdthinh on 10/17/14.
 * PPEngine substitute input pairs via PPDB
 */
public class PPEngine {
	private static double _sgramThres = 0.7;
	private static double _lcsThres = 0.7;
	private static int _maxTokenNumber = 5;
	private static double _gigaThres = 0.0;
	private static double _ngramThres = 0.0;


	public static void set_gigaThres(double _gigaThres) {
		PPEngine._gigaThres = _gigaThres;
	}

	public static void set_ngramThres(double _ngramThres) {
		PPEngine._ngramThres = _ngramThres;
	}

	public static void set_sgramThres(double _sgramThres) {
		PPEngine._sgramThres = _sgramThres;
	}

	public static void set_lcsThres(double _lcsThres) {
		PPEngine._lcsThres = _lcsThres;
	}

	public static void main(String[] args) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//resourcePath
		String sourcePath = currentPath.concat("/resources/").concat(args[0]);
		//destinationPath to put on cdb file
		String destinationPath = currentPath.concat("/resources/cdb/").concat(args[1]);
		EnPPDB.loadCDBFromPPDB(sourcePath, destinationPath);

		String text = "A powerful bomb, hidden in a truck, exploded outside the headquarters of Colombia's secret police, Wednesday, causing severe damage, Bogota mayor Andres Pastrana said.";
		String hypo = "An attempt on Andres Pastrana's life was carried out using a powerful bomb.";
	}

	public static Map<String, Map<String, Pair>> substituteAllLongestPP(Pair pair, List<String> chosens, List<String> types) {
		Map<String, Map<String, Pair>> ret = new HashMap<String, Map<String, Pair>>();
		Map<String, Object> textAnno = Parser.parseTextToAnnotation(pair.get_text());
		Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(pair.get_hypo());
		Map<Integer, List<Chunk>> allTextChunk = Parser.getAllChunkForPPEngine(textAnno);
		Map<Integer, List<Chunk>> allHypoChunk = Parser.getAllChunkForPPEngine(hypoAnno);
		Map<String, Object> pairAnno = Parser.parseTextToAnnotation(pair.get_text() + " " + pair.get_hypo());

		//create factory for substituting coref first then ppdb
		Map<String, Map<String, Object>> substitutedWithCorefFeaturePool = new HashMap<>();
		{
			Pair tmpHypoToText = CorefEngine.substituteCorefFromHypoToText(textAnno, hypoAnno, pairAnno);
			Map<String, Object> subHypoToText = new HashMap<>();
			subHypoToText.put("text", Parser.parseTextToAnnotation(tmpHypoToText.get_text()));
			subHypoToText.put("hypo", Parser.parseTextToAnnotation(tmpHypoToText.get_hypo()));
			subHypoToText.put("textChunk", Parser.getAllChunkForPPEngine((Map<String, Object>) subHypoToText.get("text")));
			subHypoToText.put("hypoChunk", Parser.getAllChunkForPPEngine((Map<String, Object>) subHypoToText.get("hypo")));
			substitutedWithCorefFeaturePool.put("hypoToText", subHypoToText);
			Pair tmpTextToHypo = CorefEngine.substituteCorefFromTextToHypo(textAnno, hypoAnno, pairAnno);
			Map<String, Object> subTextToHypo = new HashMap<>();
			subTextToHypo.put("text", Parser.parseTextToAnnotation(tmpTextToHypo.get_text()));
			subTextToHypo.put("hypo", Parser.parseTextToAnnotation(tmpTextToHypo.get_hypo()));
			subTextToHypo.put("textChunk", Parser.getAllChunkForPPEngine((Map<String, Object>) subTextToHypo.get("text")));
			subTextToHypo.put("hypoChunk", Parser.getAllChunkForPPEngine((Map<String, Object>) subTextToHypo.get("hypo")));
			substitutedWithCorefFeaturePool.put("textToHypo", subTextToHypo);
		}
		//create factory for substituting ppdb first then coref
		Map<String, Map<String, Map<String, Object>>> substitutedWithPPFeaturePool = new HashMap<>();
		for (String chosen : chosens) {
			Pair substitutedPair = getSubstitutionLongestPP(new Pair(pair), textAnno, hypoAnno, allTextChunk, allHypoChunk, chosen);
			Map<String, Map<String, Object>> tmp = new HashMap<>();
			tmp.put("text", Parser.parseTextToAnnotation(substitutedPair.get_text()));
			tmp.put("hypo", Parser.parseTextToAnnotation(substitutedPair.get_hypo()));
			tmp.put("pair", Parser.parseTextToAnnotation(substitutedPair.get_text() + " " + substitutedPair.get_hypo()));
			substitutedWithPPFeaturePool.put(chosen, tmp);
		}

		for (String type : types) {
			switch (type) {
				case "corefFirst": {
					Map<String, Pair> subret = new HashMap<String, Pair>();
					for (String chosen : chosens) {
						System.out.println(type + "-" + chosen);
						subret.put(chosen, getSubstitutionLongestPP(new Pair(pair)
										, (Map<String, Object>) substitutedWithCorefFeaturePool.get("hypoToText").get("text")
										, (Map<String, Object>) substitutedWithCorefFeaturePool.get("hypoToText").get("hypo")
										, (Map<Integer, List<Chunk>>) substitutedWithCorefFeaturePool.get("hypoToText").get("textChunk")
										, (Map<Integer, List<Chunk>>) substitutedWithCorefFeaturePool.get("hypoToText").get("hypoChunk")
										, chosen));
					}
					ret.put(type, subret);
					break;
				}
				case "corefFirstReverse": {
					Map<String, Pair> subret = new HashMap<String, Pair>();
					for (String chosen : chosens) {
						System.out.println(type + "-" + chosen);
						subret.put(chosen, getSubstitutionLongestPP(new Pair(pair)
										, (Map<String, Object>) substitutedWithCorefFeaturePool.get("textToHypo").get("text")
										, (Map<String, Object>) substitutedWithCorefFeaturePool.get("textToHypo").get("hypo")
										, (Map<Integer, List<Chunk>>) substitutedWithCorefFeaturePool.get("textToHypo").get("textChunk")
										, (Map<Integer, List<Chunk>>) substitutedWithCorefFeaturePool.get("textToHypo").get("hypoChunk")
										, chosen));
					}
					ret.put(type, subret);
					break;
				}
				case "corefLater": {
					Map<String, Pair> subret = new HashMap<String, Pair>();
					for (String chosen : chosens) {
						System.out.println(type + "-" + chosen);
						subret.put(chosen
										, CorefEngine.substituteCorefFromHypoToText(substitutedWithPPFeaturePool.get(chosen).get("text")
										, substitutedWithPPFeaturePool.get(chosen).get("hypo")
										, substitutedWithPPFeaturePool.get(chosen).get("pair")));
					}
					ret.put(type, subret);
					break;
				}
				case "corefLaterReverse": {
					Map<String, Pair> subret = new HashMap<String, Pair>();
					for (String chosen : chosens) {
						System.out.println(type + "-" + chosen);
						subret.put(chosen
										, CorefEngine.substituteCorefFromTextToHypo(substitutedWithPPFeaturePool.get(chosen).get("text")
										, substitutedWithPPFeaturePool.get(chosen).get("hypo")
										, substitutedWithPPFeaturePool.get(chosen).get("pair")));
					}
					ret.put(type, subret);
					break;
				}
			}
		}
		return ret;
	}

	public static Map<String, Map<String, Pair>> substituteAllShortestPP(Pair pair, List<String> chosens, List<String> types) {
		Map<String, Map<String, Pair>> ret = new HashMap<String, Map<String, Pair>>();
		Map<String, Object> textAnno = Parser.parseTextToAnnotation(pair.get_text());
		Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(pair.get_hypo());
		Map<Integer, List<Chunk>> allTextChunk = Parser.getAllChunkForPPEngine(textAnno);
		Map<Integer, List<Chunk>> allHypoChunk = Parser.getAllChunkForPPEngine(hypoAnno);
		Map<String, Object> pairAnno = Parser.parseTextToAnnotation(pair.get_text() + " " + pair.get_hypo());

		//create factory for substituting coref first then ppdb
		Map<String, Map<String, Object>> substitutedWithCorefFeaturePool = new HashMap<>();
		{
			Pair tmpHypoToText = CorefEngine.substituteCorefFromHypoToText(textAnno, hypoAnno, pairAnno);
			Map<String, Object> subHypoToText = new HashMap<>();
			subHypoToText.put("text", Parser.parseTextToAnnotation(tmpHypoToText.get_text()));
			subHypoToText.put("hypo", Parser.parseTextToAnnotation(tmpHypoToText.get_hypo()));
			subHypoToText.put("textChunk", Parser.getAllChunkForPPEngine((Map<String, Object>) subHypoToText.get("text")));
			subHypoToText.put("hypoChunk", Parser.getAllChunkForPPEngine((Map<String, Object>) subHypoToText.get("hypo")));
			substitutedWithCorefFeaturePool.put("hypoToText", subHypoToText);
			Pair tmpTextToHypo = CorefEngine.substituteCorefFromTextToHypo(textAnno, hypoAnno, pairAnno);
			Map<String, Object> subTextToHypo = new HashMap<>();
			subTextToHypo.put("text", Parser.parseTextToAnnotation(tmpTextToHypo.get_text()));
			subTextToHypo.put("hypo", Parser.parseTextToAnnotation(tmpTextToHypo.get_hypo()));
			subTextToHypo.put("textChunk", Parser.getAllChunkForPPEngine((Map<String, Object>) subTextToHypo.get("text")));
			subTextToHypo.put("hypoChunk", Parser.getAllChunkForPPEngine((Map<String, Object>) subTextToHypo.get("hypo")));
			substitutedWithCorefFeaturePool.put("textToHypo", subTextToHypo);
		}
		//create factory for substituting ppdb first then coref
		Map<String, Map<String, Map<String, Object>>> substitutedWithPPFeaturePool = new HashMap<>();
		for (String chosen : chosens) {
			Pair substitutedPair = getSubstitutionShortestPP(new Pair(pair), textAnno, hypoAnno, allTextChunk, allHypoChunk, chosen);
			Map<String, Map<String, Object>> tmp = new HashMap<>();
			tmp.put("text", Parser.parseTextToAnnotation(substitutedPair.get_text()));
			tmp.put("hypo", Parser.parseTextToAnnotation(substitutedPair.get_hypo()));
			tmp.put("pair", Parser.parseTextToAnnotation(substitutedPair.get_text() + " " + substitutedPair.get_hypo()));
			substitutedWithPPFeaturePool.put(chosen, tmp);
		}

		for (String type : types) {
			switch (type) {
				case "corefFirst": {
					Map<String, Pair> subret = new HashMap<String, Pair>();
					for (String chosen : chosens) {
						System.out.println(type + "-ss-" + chosen);
						subret.put(chosen, getSubstitutionShortestPP(new Pair(pair)
										, (Map<String, Object>) substitutedWithCorefFeaturePool.get("hypoToText").get("text")
										, (Map<String, Object>) substitutedWithCorefFeaturePool.get("hypoToText").get("hypo")
										, (Map<Integer, List<Chunk>>) substitutedWithCorefFeaturePool.get("hypoToText").get("textChunk")
										, (Map<Integer, List<Chunk>>) substitutedWithCorefFeaturePool.get("hypoToText").get("hypoChunk")
										, chosen));
					}
					ret.put(type, subret);
					break;
				}
				case "corefFirstReverse": {
					Map<String, Pair> subret = new HashMap<String, Pair>();
					for (String chosen : chosens) {
						System.out.println(type + "-ss-" + chosen);
						subret.put(chosen, getSubstitutionShortestPP(new Pair(pair)
										, (Map<String, Object>) substitutedWithCorefFeaturePool.get("textToHypo").get("text")
										, (Map<String, Object>) substitutedWithCorefFeaturePool.get("textToHypo").get("hypo")
										, (Map<Integer, List<Chunk>>) substitutedWithCorefFeaturePool.get("textToHypo").get("textChunk")
										, (Map<Integer, List<Chunk>>) substitutedWithCorefFeaturePool.get("textToHypo").get("hypoChunk")
										, chosen));
					}
					ret.put(type, subret);
					break;
				}
				case "corefLater": {
					Map<String, Pair> subret = new HashMap<String, Pair>();
					for (String chosen : chosens) {
						System.out.println(type + "-ss-" + chosen);
						subret.put(chosen
										, CorefEngine.substituteCorefFromHypoToText(substitutedWithPPFeaturePool.get(chosen).get("text")
										, substitutedWithPPFeaturePool.get(chosen).get("hypo")
										, substitutedWithPPFeaturePool.get(chosen).get("pair")));
					}
					ret.put(type, subret);
					break;
				}
				case "corefLaterReverse": {
					Map<String, Pair> subret = new HashMap<String, Pair>();
					for (String chosen : chosens) {
						System.out.println(type + "-ss-" + chosen);
						subret.put(chosen
										, CorefEngine.substituteCorefFromTextToHypo(substitutedWithPPFeaturePool.get(chosen).get("text")
										, substitutedWithPPFeaturePool.get(chosen).get("hypo")
										, substitutedWithPPFeaturePool.get(chosen).get("pair")));
					}
					ret.put(type, subret);
					break;
				}
			}
		}
		return ret;
	}

	public static Map<String, Pair> substituteCorefFirstLongestPP(Pair pair, List<String> chosens) {
		Map<String, Pair> ret = new HashMap<String, Pair>();

		Pair substitutedPair = CorefEngine.substituteCorefFromHypoToText(pair);
		Map<String, Object> textAnno = Parser.parseTextToAnnotation(substitutedPair.get_text());
		Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(substitutedPair.get_hypo());
		System.out.println("Text: " + pair.get_text());
		System.out.println("Hypo: " + pair.get_hypo());
		System.out.println("Phrase{T}:");
		Map<Integer, List<Chunk>> allTextChunk = Parser.getAllChunkForPPEngine(textAnno);
		System.out.println("Phrase{H}:");
		Map<Integer, List<Chunk>> allHypoChunk = Parser.getAllChunkForPPEngine(hypoAnno);
		for (String chosen : chosens) {
			Pair pairTmp = new Pair(substitutedPair);
			System.out.println("--- Use " + chosen + " Similarity (threshold = " + _sgramThres + ") to get PPhrase{H} and Compare (full search) with Phrase{T}---");
			ret.put(chosen, getSubstitutionLongestPP(pairTmp, textAnno, hypoAnno, allTextChunk, allHypoChunk, chosen));
			System.out.println("-----");
			System.out.println("Text: " + ret.get(chosen).get_text());
			System.out.println("Hypo: " + ret.get(chosen).get_hypo());
			System.out.println("-----");
		}
		return ret;
	}

	public static Map<String, Pair> substituteCorefLaterLongestPP(Pair pair, List<String> chosens) {
		Map<String, Pair> ret = new HashMap<String, Pair>();
		List<Pair> pairEachChosen = new ArrayList<>();
		Map<String, Object> textAnno = Parser.parseTextToAnnotation(pair.get_text());
		Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(pair.get_hypo());
		System.out.println("Text: " + pair.get_text());
		System.out.println("Hypo: " + pair.get_hypo());
		System.out.println("Phrase{T}:");
		Map<Integer, List<Chunk>> allTextChunk = Parser.getAllChunkForPPEngine(textAnno);
		System.out.println("Phrase{H}:");
		Map<Integer, List<Chunk>> allHypoChunk = Parser.getAllChunkForPPEngine(hypoAnno);
		for (String chosen : chosens) {
			Pair pairTmp = new Pair(pair);
			System.out.println(chosen);
			System.out.println("--- Use " + chosen + " Similarity (threshold = " + _sgramThres + ") to get PPhrase{H} and Compare (full search) with Phrase{T}---");
			pairEachChosen.add(getSubstitutionLongestPP(pairTmp, textAnno, hypoAnno, allTextChunk, allHypoChunk, chosen));
		}
		for (int i = 0; i < pairEachChosen.size(); i++) {
			System.out.println(chosens.get(i) + "...");
			ret.put(chosens.get(i), CorefEngine.substituteCorefFromHypoToText(pairEachChosen.get(i)));
			System.out.println("-----");
			System.out.println("Text: " + ret.get(chosens.get(i)).get_text());
			System.out.println("Hypo: " + ret.get(chosens.get(i)).get_hypo());
			System.out.println("-----");
		}
		return ret;
	}

	public static Map<String, Pair> substituteCorefFirstReverseLongestPP(Pair pair, List<String> chosens) {
		Map<String, Pair> ret = new HashMap<String, Pair>();

		Pair substitutedPair = CorefEngine.substituteCorefFromTextToHypo(pair);
		Map<String, Object> textAnno = Parser.parseTextToAnnotation(substitutedPair.get_text());
		Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(substitutedPair.get_hypo());
		System.out.println("Text: " + pair.get_text());
		System.out.println("Hypo: " + pair.get_hypo());
		System.out.println("Phrase{T}:");
		Map<Integer, List<Chunk>> allTextChunk = Parser.getAllChunkForPPEngine(textAnno);
		System.out.println("Phrase{H}:");
		Map<Integer, List<Chunk>> allHypoChunk = Parser.getAllChunkForPPEngine(hypoAnno);
		for (String chosen : chosens) {
			Pair pairTmp = new Pair(substitutedPair);
			System.out.println("--- Use " + chosen + " Similarity (threshold = " + _sgramThres + ") to get PPhrase{H} and Compare (full search) with Phrase{T}---");
			ret.put(chosen, getSubstitutionLongestPP(pairTmp, textAnno, hypoAnno, allTextChunk, allHypoChunk, chosen));
			System.out.println("-----");
			System.out.println("Text: " + ret.get(chosen).get_text());
			System.out.println("Hypo: " + ret.get(chosen).get_hypo());
			System.out.println("-----");
		}
		return ret;
	}

	public static Map<String, Pair> substituteCorefLaterReverseLongestPP(Pair pair, List<String> chosens) {
		Map<String, Pair> ret = new HashMap<String, Pair>();
		List<Pair> pairEachChosen = new ArrayList<>();
		Map<String, Object> textAnno = Parser.parseTextToAnnotation(pair.get_text());
		Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(pair.get_hypo());
		System.out.println("Text: " + pair.get_text());
		System.out.println("Hypo: " + pair.get_hypo());
		System.out.println("Phrase{T}:");
		Map<Integer, List<Chunk>> allTextChunk = Parser.getAllChunkForPPEngine(textAnno);
		System.out.println("Phrase{H}:");
		Map<Integer, List<Chunk>> allHypoChunk = Parser.getAllChunkForPPEngine(hypoAnno);
		for (String chosen : chosens) {
			Pair pairTmp = new Pair(pair);
			System.out.println(chosen);
			System.out.println("--- Use " + chosen + " Similarity (threshold = " + _sgramThres + ") to get PPhrase{H} and Compare (full search) with Phrase{T}---");
			pairEachChosen.add(getSubstitutionLongestPP(pairTmp, textAnno, hypoAnno, allTextChunk, allHypoChunk, chosen));
		}
		for (int i = 0; i < pairEachChosen.size(); i++) {
			System.out.println(chosens.get(i) + "...");
			ret.put(chosens.get(i), CorefEngine.substituteCorefFromTextToHypo(pairEachChosen.get(i)));
			System.out.println("-----");
			System.out.println("Text: " + ret.get(chosens.get(i)).get_text());
			System.out.println("Hypo: " + ret.get(chosens.get(i)).get_hypo());
			System.out.println("-----");
		}
		return ret;

	}

	public static Map<String, Pair> substituteCorefFirstShortestPP(Pair pair, List<String> chosens) {
		Map<String, Pair> ret = new HashMap<String, Pair>();

		Pair substitutedPair = CorefEngine.substituteCorefFromHypoToText(pair);
		Map<String, Object> textAnno = Parser.parseTextToAnnotation(substitutedPair.get_text());
		Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(substitutedPair.get_hypo());
		System.out.println("Text: " + pair.get_text());
		System.out.println("Hypo: " + pair.get_hypo());
		System.out.println("Phrase{T}:");
		Map<Integer, List<Chunk>> allTextChunk = Parser.getAllChunkForPPEngine(textAnno);
		System.out.println("Phrase{H}:");
		Map<Integer, List<Chunk>> allHypoChunk = Parser.getAllChunkForPPEngine(hypoAnno);
		for (String chosen : chosens) {
			Pair pairTmp = new Pair(substitutedPair);
			System.out.println("--- Use " + chosen + " Similarity (threshold = " + _sgramThres + ") to get PPhrase{H} and Compare (full search) with Phrase{T}---");
			ret.put(chosen, getSubstitutionShortestPP(pairTmp, textAnno, hypoAnno, allTextChunk, allHypoChunk, chosen));
			System.out.println("-----");
			System.out.println("Text: " + ret.get(chosen).get_text());
			System.out.println("Hypo: " + ret.get(chosen).get_hypo());
			System.out.println("-----");
		}
		return ret;
	}

	public static Map<String, Pair> substituteCorefLaterShortestPP(Pair pair, List<String> chosens) {
		Map<String, Pair> ret = new HashMap<String, Pair>();
		List<Pair> pairEachChosen = new ArrayList<>();
		Map<String, Object> textAnno = Parser.parseTextToAnnotation(pair.get_text());
		Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(pair.get_hypo());
		System.out.println("Text: " + pair.get_text());
		System.out.println("Hypo: " + pair.get_hypo());
		System.out.println("Phrase{T}:");
		Map<Integer, List<Chunk>> allTextChunk = Parser.getAllChunkForPPEngine(textAnno);
		System.out.println("Phrase{H}:");
		Map<Integer, List<Chunk>> allHypoChunk = Parser.getAllChunkForPPEngine(hypoAnno);
		for (String chosen : chosens) {
			Pair pairTmp = new Pair(pair);
			System.out.println(chosen);
			System.out.println("--- Use " + chosen + " Similarity (threshold = " + _sgramThres + ") to get PPhrase{H} and Compare (full search) with Phrase{T}---");
			pairEachChosen.add(getSubstitutionShortestPP(pairTmp, textAnno, hypoAnno, allTextChunk, allHypoChunk, chosen));
		}
		for (int i = 0; i < pairEachChosen.size(); i++) {
			System.out.println(chosens.get(i) + "...");
			ret.put(chosens.get(i), CorefEngine.substituteCorefFromHypoToText(pairEachChosen.get(i)));
			System.out.println("-----");
			System.out.println("Text: " + ret.get(chosens.get(i)).get_text());
			System.out.println("Hypo: " + ret.get(chosens.get(i)).get_hypo());
			System.out.println("-----");
		}
		return ret;
	}

	public static Map<String, Pair> substituteCorefFirstReverseShortestPP(Pair pair, List<String> chosens) {
		Map<String, Pair> ret = new HashMap<String, Pair>();

		Pair substitutedPair = CorefEngine.substituteCorefFromTextToHypo(pair);
		Map<String, Object> textAnno = Parser.parseTextToAnnotation(substitutedPair.get_text());
		Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(substitutedPair.get_hypo());
		System.out.println("Text: " + pair.get_text());
		System.out.println("Hypo: " + pair.get_hypo());
		System.out.println("Phrase{T}:");
		Map<Integer, List<Chunk>> allTextChunk = Parser.getAllChunkForPPEngine(textAnno);
		System.out.println("Phrase{H}:");
		Map<Integer, List<Chunk>> allHypoChunk = Parser.getAllChunkForPPEngine(hypoAnno);
		for (String chosen : chosens) {
			Pair pairTmp = new Pair(substitutedPair);
			System.out.println("--- Use " + chosen + " Similarity (threshold = " + _sgramThres + ") to get PPhrase{H} and Compare (full search) with Phrase{T}---");
			ret.put(chosen, getSubstitutionShortestPP(pairTmp, textAnno, hypoAnno, allTextChunk, allHypoChunk, chosen));
			System.out.println("-----");
			System.out.println("Text: " + ret.get(chosen).get_text());
			System.out.println("Hypo: " + ret.get(chosen).get_hypo());
			System.out.println("-----");
		}
		return ret;
	}

	public static Map<String, Pair> substituteCorefLaterReverseShortestPP(Pair pair, List<String> chosens) {
		Map<String, Pair> ret = new HashMap<String, Pair>();
		List<Pair> pairEachChosen = new ArrayList<>();
		Map<String, Object> textAnno = Parser.parseTextToAnnotation(pair.get_text());
		Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(pair.get_hypo());
		System.out.println("Text: " + pair.get_text());
		System.out.println("Hypo: " + pair.get_hypo());
		System.out.println("Phrase{T}:");
		Map<Integer, List<Chunk>> allTextChunk = Parser.getAllChunkForPPEngine(textAnno);
		System.out.println("Phrase{H}:");
		Map<Integer, List<Chunk>> allHypoChunk = Parser.getAllChunkForPPEngine(hypoAnno);
		for (String chosen : chosens) {
			Pair pairTmp = new Pair(pair);
			System.out.println(chosen);
			System.out.println("--- Use " + chosen + " Similarity (threshold = " + _sgramThres + ") to get PPhrase{H} and Compare (full search) with Phrase{T}---");
			pairEachChosen.add(getSubstitutionShortestPP(pairTmp, textAnno, hypoAnno, allTextChunk, allHypoChunk, chosen));
		}
		for (int i = 0; i < pairEachChosen.size(); i++) {
			System.out.println(chosens.get(i) + "...");
			ret.put(chosens.get(i), CorefEngine.substituteCorefFromTextToHypo(pairEachChosen.get(i)));
			System.out.println("-----");
			System.out.println("Text: " + ret.get(chosens.get(i)).get_text());
			System.out.println("Hypo: " + ret.get(chosens.get(i)).get_hypo());
			System.out.println("-----");
		}
		return ret;

	}

	private static Pair getSubstitutionLongestPP(Pair pair, Map<String, Object> originTextAnno, Map<String, Object> originHypoAnno, Map<Integer, List<Chunk>> allTextChunk, Map<Integer, List<Chunk>> allHypoChunk, String chosen) {
		Map<String, Object> textAnno = Parser.getDeepCopyAnnotation(originTextAnno);
		Map<String, Object> hypoAnno = Parser.getDeepCopyAnnotation(originHypoAnno);
		Map<Integer, List<Token>> textTokens = Parser.getTokensInAnnotation(textAnno);
		Map<Integer, List<Token>> hypoTokens = Parser.getTokensInAnnotation(hypoAnno);
		List<PPIndexRecord> pprs = getParaphraseTableLongestPP(hypoAnno
						, Parser.getDeepCopyAllChunkForPPEngine(allTextChunk)
						, Parser.getDeepCopyAllChunkForPPEngine(allHypoChunk)
						, chosen);
		if (pprs.size() == 0)
			return pair;
		try {
			for (PPIndexRecord ppr : pprs) {
				textTokens.put(ppr.get_textSent(), Token.replaceTokens(textTokens.get(ppr.get_textSent()),
								ppr.get_textStartTokenNum(), ppr.get_textEndTokenNum() + 1,
								hypoTokens.get(ppr.get_hypoSent()).subList(ppr.get_hypoStartTokenNum(), ppr.get_hypoEndTokenNum() + 1)));
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return new Pair(Parser.getTextFromTokens(textTokens), pair.get_hypo());
	}

	private static Pair getSubstitutionShortestPP(Pair pair, Map<String, Object> originTextAnno, Map<String, Object> originHypoAnno, Map<Integer, List<Chunk>> allTextChunk, Map<Integer, List<Chunk>> allHypoChunk, String chosen) {
		Map<String, Object> textAnno = Parser.getDeepCopyAnnotation(originTextAnno);
		Map<String, Object> hypoAnno = Parser.getDeepCopyAnnotation(originHypoAnno);
		Map<Integer, List<Token>> textTokens = Parser.getTokensInAnnotation(textAnno);
		Map<Integer, List<Token>> hypoTokens = Parser.getTokensInAnnotation(hypoAnno);
		List<PPIndexRecord> pprs = getParaphraseTableShortestPP(hypoAnno
						, Parser.getDeepCopyAllChunkForPPEngine(allTextChunk)
						, Parser.getDeepCopyAllChunkForPPEngine(allHypoChunk)
						, chosen);
		if (pprs.size() == 0)
			return pair;
		try {
			for (PPIndexRecord ppr : pprs) {
				textTokens.put(ppr.get_textSent(), Token.replaceTokens(textTokens.get(ppr.get_textSent()),
								ppr.get_textStartTokenNum(), ppr.get_textEndTokenNum() + 1,
								hypoTokens.get(ppr.get_hypoSent()).subList(ppr.get_hypoStartTokenNum(), ppr.get_hypoEndTokenNum() + 1)));
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return new Pair(Parser.getTextFromTokens(textTokens), pair.get_hypo());
	}

	private static List<PPIndexRecord> getParaphraseTableLongestPP(Map<String, Object> hypoAnno, Map<Integer, List<Chunk>> allTextChunk, Map<Integer, List<Chunk>> allHypoChunk, String chosen) {
		PPIndexTable pptable = new PPIndexTable();
		for (int sentHypo : allHypoChunk.keySet()) {
			List<Chunk> hypoChunks = allHypoChunk.get(sentHypo);
			for (int j = hypoChunks.size() - 1; j >= 0; j--) {
				for (int sentText : allTextChunk.keySet()) {
					List<Chunk> textChunks = allTextChunk.get(sentText);
					for (int i = 0; i < textChunks.size(); i++) {
						Object[] scorePP;
						double thres;
						switch (chosen) {
							case "exactly":
								scorePP = PPEngine.getScoreParaphrasedExactly(hypoChunks.get(j), textChunks.get(i), hypoAnno, sentHypo);
								thres = 1.0;
								break;
							case "lcs":
								scorePP = PPEngine.getScoreParaphrasedWithLCS(hypoChunks.get(j), textChunks.get(i), hypoAnno, sentHypo);
								thres = _lcsThres;
								break;
							case "mikolov":
								scorePP = PPEngine.getScoreParaphrasedWithMikolov(hypoChunks.get(j), textChunks.get(i), hypoAnno, sentHypo);
								thres = _sgramThres;
								break;
							case "turian":
								scorePP = PPEngine.getScoreParaphrasedWithTurian(hypoChunks.get(j), textChunks.get(i), hypoAnno, sentHypo);
								thres = _sgramThres;
								break;
							case "deps":
								scorePP = PPEngine.getScoreParaphrasedWithDEPS(hypoChunks.get(j), textChunks.get(i), hypoAnno, sentHypo);
								thres = _sgramThres;
								break;
							case "glove":
								scorePP = PPEngine.getScoreParaphrasedWithGlove(hypoChunks.get(j), textChunks.get(i), hypoAnno, sentHypo);
								thres = _sgramThres;
							default:
								return new ArrayList<>();
						}
						double score = (double) scorePP[0];
						if (score >= thres) {
							pptable.put(sentText, textChunks.get(i).get_begin(), textChunks.get(i).get_end(), sentHypo, hypoChunks.get(j).get_begin(), hypoChunks.get(j).get_end());
						}
					}
				}
			}
		}

		System.out.println("Paraphrase routing table: ");
		List<PPIndexRecord> tmpRet = pptable.sort();
		for(PPIndexRecord record : tmpRet)
			record.printOut(allTextChunk, allHypoChunk);

		pptable.filterLongestPP();
		List<PPIndexRecord> ret = pptable.sort();
		System.out.println("Accepted routing table (Keep Longest): ");
		for(PPIndexRecord record : ret)
			record.printOut(allTextChunk, allHypoChunk);
		return ret;
	}

	private static List<PPIndexRecord> getParaphraseTableShortestPP(Map<String, Object> hypoAnno, Map<Integer, List<Chunk>> allTextChunk, Map<Integer, List<Chunk>> allHypoChunk, String chosen) {
		PPIndexTable pptable = new PPIndexTable();
		for (int sentHypo : allHypoChunk.keySet()) {
			List<Chunk> hypoChunks = allHypoChunk.get(sentHypo);
			for (int j = hypoChunks.size() - 1; j >= 0; j--) {
				for (int sentText : allTextChunk.keySet()) {
					List<Chunk> textChunks = allTextChunk.get(sentText);
					for (int i = 0; i < textChunks.size(); i++) {
						Object[] scorePP;
						double thres;
						switch (chosen) {
							case "exactly":
								scorePP = PPEngine.getScoreParaphrasedExactly(hypoChunks.get(j), textChunks.get(i), hypoAnno, sentHypo);
								thres = 1.0;
								break;
							case "lcs":
								scorePP = PPEngine.getScoreParaphrasedWithLCS(hypoChunks.get(j), textChunks.get(i), hypoAnno, sentHypo);
								thres = _lcsThres;
								break;
							case "mikolov":
								scorePP = PPEngine.getScoreParaphrasedWithMikolov(hypoChunks.get(j), textChunks.get(i), hypoAnno, sentHypo);
								thres = _sgramThres;
								break;
							case "turian":
								scorePP = PPEngine.getScoreParaphrasedWithTurian(hypoChunks.get(j), textChunks.get(i), hypoAnno, sentHypo);
								thres = _sgramThres;
								break;
							case "deps":
								scorePP = PPEngine.getScoreParaphrasedWithDEPS(hypoChunks.get(j), textChunks.get(i), hypoAnno, sentHypo);
								thres = _sgramThres;
								break;
							default:
								return new ArrayList<>();
						}
						double score = (double) scorePP[0];
						if (score >= thres) {
							pptable.put(sentText, textChunks.get(i).get_begin(), textChunks.get(i).get_end(), sentHypo, hypoChunks.get(j).get_begin(), hypoChunks.get(j).get_end());
						}
					}
				}
			}
		}
		pptable.filterShortestParaphrase();
		List<PPIndexRecord> ret = pptable.sort();
		System.out.println("Paraphrase routing table:");
		for(PPIndexRecord record : ret)
			record.printOut();
		return ret;
	}

	public static boolean shouldBeCompared(Chunk hypoChunk, Chunk textChunk){
		if(hypoChunk.isVPEndWithIN()){
			if(textChunk.isVerbPhrase() && !textChunk.isVPEndWithIN() && hypoChunk.contains(textChunk) )
				return false;
		}
		if(textChunk.isVPEndWithIN()){
			if(hypoChunk.isVerbPhrase() && !hypoChunk.isVPEndWithIN() && textChunk.contains(hypoChunk))
				return false;
		}
		return true;
	}

	private static Object[] getScoreParaphrasedWithDEPS(Chunk hypoChunk, Chunk textChunk, Map<String, Object> hypoAnno, int sentNumber) {
		boolean flag = false;
		Double maxScore = 0.0;
		String chosenPP = "";
		Object[] ret = new Object[2];
		if(hypoChunk.get_chunk().toLowerCase().equals(textChunk.get_chunk().toLowerCase())) {
			ret[0] = 1.0;
			ret[1] = hypoChunk.get_chunk();
			System.out.println("   xFound: " + hypoChunk.get_chunk() + " <> " + textChunk.get_chunk() + " == " + "1.0");
			return ret;
		}
		if(!shouldBeCompared(hypoChunk, textChunk)) {
			ret[0] = maxScore;
			ret[1] = chosenPP;
			return ret;
		}
		EnDepsSimilarity DepsSim = new EnDepsSimilarity();
		//phrasal paraphrase

		Paraphrase pp = EnPPDB.lookUp(hypoChunk.get_chunk());
		for (FeatureSet fs : pp.get_featureSets()) {
			double tmpScore = DepsSim.getSimilarity(fs.get_target(), textChunk.get_chunk());
			if (tmpScore >= maxScore) {
				maxScore = tmpScore;
				chosenPP = fs.get_target();
				if(maxScore >= _sgramThres)
					break;
			}
		}
		if (maxScore >= _sgramThres) {
			System.out.println("   Found: " + chosenPP + " <> " + textChunk.get_chunk() + " == " + maxScore);
			flag = true;
		}
		//ccg paraphrase
		if (flag == false) {
			Map<String, Map<Integer, String>> hypoCCGs = changeToCCG(hypoChunk, hypoAnno, sentNumber);
			for (String ccg : hypoCCGs.keySet()) {
				Paraphrase ppccg = EnPPDB.lookUp(ccg);
				for (FeatureSet fs : ppccg.get_featureSets()) {
					String hypoParaphrase = getAlignTextFromPattern(hypoCCGs.get(ccg), fs.get_target(), fs.get_alignment());
					double tmpScore = DepsSim.getSimilarity(hypoParaphrase, textChunk.get_chunk());
					if (tmpScore >= maxScore && tmpScore >= _sgramThres) {
						maxScore = tmpScore;
						chosenPP = hypoParaphrase;
						System.out.println("   Phrase(H): " + hypoChunk.get_chunk());
						System.out.println("       >>> CCG: " + ccg);
						System.out.println("       >>> PPDB: " + hypoParaphrase);
						System.out.println("       <> Phrase(T): " + textChunk.get_chunk() + " == " + tmpScore);
						break;
					}
				}
				if (maxScore >= _sgramThres)
					break;
			}
		}
		ret[0] = maxScore;
		ret[1] = chosenPP;
		return ret;
	}

	private static Object[] getScoreParaphrasedWithMikolov(Chunk hypoChunk, Chunk textChunk, Map<String, Object> hypoAnno, int sentNumber) {
		boolean flag = false;
		Double maxScore = 0.0;
		String chosenPP = "";
		Object[] ret = new Object[2];
		if(hypoChunk.get_chunk().toLowerCase().equals(textChunk.get_chunk().toLowerCase())) {
			ret[0] = 1.0;
			ret[1] = hypoChunk.get_chunk();
			System.out.println("   xFound: " + hypoChunk.get_chunk() + " <> " + textChunk.get_chunk() + " == " + "1.0");
			return ret;
		}
		if(!shouldBeCompared(hypoChunk, textChunk)) {
			ret[0] = maxScore;
			ret[1] = chosenPP;
			return ret;
		}
		EnMikolovSimilarity mikolovSim = new EnMikolovSimilarity();
		//phrasal paraphrase

		Paraphrase pp = EnPPDB.lookUp(hypoChunk.get_chunk());
		for (FeatureSet fs : pp.get_featureSets()) {
			double tmpScore = mikolovSim.getSimilarity(fs.get_target(), textChunk.get_chunk());
			if (tmpScore >= maxScore) {
				maxScore = tmpScore;
				chosenPP = fs.get_target();
				if(maxScore >= _sgramThres)
					break;
			}
		}
		if (maxScore >= _sgramThres) {
			System.out.println("   Found: " + hypoChunk.get_chunk() + ">>>" + chosenPP + " <> " + textChunk.get_chunk() + " == " + maxScore);
			flag = true;
		}
		//ccg paraphrase
		if (flag == false) {
				Map<String, Map<Integer, String>> hypoCCGs = changeToCCG(hypoChunk, hypoAnno, sentNumber);
				for (String ccg : hypoCCGs.keySet()) {
					Paraphrase ppccg = EnPPDB.lookUp(ccg);
					for (FeatureSet fs : ppccg.get_featureSets()) {
						String hypoParaphrase = getAlignTextFromPattern(hypoCCGs.get(ccg), fs.get_target(), fs.get_alignment());
						double tmpScore = mikolovSim.getSimilarity(hypoParaphrase, textChunk.get_chunk());
						if (tmpScore >= maxScore && tmpScore >= _sgramThres) {
							maxScore = tmpScore;
							chosenPP = hypoParaphrase;
							System.out.println("   Phrase(H): " + hypoChunk.get_chunk());
							System.out.println("       >>> CCG: " + ccg);
							System.out.println("       >>> PPDB: " + hypoParaphrase);
							System.out.println("       <> Phrase(T): " + textChunk.get_chunk() + " == " + tmpScore);
							break;
						}
					}
					if (maxScore >= _sgramThres)
						break;
				}
		}
		ret[0] = maxScore;
		ret[1] = chosenPP;
		return ret;
	}

	private static Object[] getScoreParaphrasedWithGlove(Chunk hypoChunk, Chunk textChunk, Map<String, Object> hypoAnno, int sentNumber) {
		boolean flag = false;
		Double maxScore = 0.0;
		String chosenPP = "";
		Object[] ret = new Object[2];
		if(hypoChunk.get_chunk().toLowerCase().equals(textChunk.get_chunk().toLowerCase())) {
			ret[0] = 1.0;
			ret[1] = hypoChunk.get_chunk();
			System.out.println("   xFound: " + hypoChunk.get_chunk() + " <> " + textChunk.get_chunk() + " == " + "1.0");
			return ret;
		}
		if(!shouldBeCompared(hypoChunk, textChunk)) {
			ret[0] = maxScore;
			ret[1] = chosenPP;
			return ret;
		}
		EnGloveSimilarity gloveSim = new EnGloveSimilarity();
		//phrasal paraphrase

		Paraphrase pp = EnPPDB.lookUp(hypoChunk.get_chunk());
		for (FeatureSet fs : pp.get_featureSets()) {
			double tmpScore = gloveSim.getSimilarity(fs.get_target(), textChunk.get_chunk());
			if (tmpScore >= maxScore) {
				maxScore = tmpScore;
				chosenPP = fs.get_target();
				if(maxScore >= _sgramThres)
					break;
			}
		}
		if (maxScore >= _sgramThres) {
			System.out.println("   Found: " + chosenPP + " <> " + textChunk.get_chunk() + " == " + maxScore);
			flag = true;
		}
		//ccg paraphrase
		if (flag == false) {
			Map<String, Map<Integer, String>> hypoCCGs = changeToCCG(hypoChunk, hypoAnno, sentNumber);
			for (String ccg : hypoCCGs.keySet()) {
				Paraphrase ppccg = EnPPDB.lookUp(ccg);
				for (FeatureSet fs : ppccg.get_featureSets()) {
					String hypoParaphrase = getAlignTextFromPattern(hypoCCGs.get(ccg), fs.get_target(), fs.get_alignment());
					double tmpScore = gloveSim.getSimilarity(hypoParaphrase, textChunk.get_chunk());
					if (tmpScore >= maxScore && tmpScore >= _sgramThres) {
						maxScore = tmpScore;
						chosenPP = hypoParaphrase;
						System.out.println("   Phrase(H): " + hypoChunk.get_chunk());
						System.out.println("       >>> CCG: " + ccg);
						System.out.println("       >>> PPDB: " + hypoParaphrase);
						System.out.println("       <> Phrase(T): " + textChunk.get_chunk() + " == " + tmpScore);
						break;
					}
				}
				if (maxScore >= _sgramThres)
					break;
			}
		}
		ret[0] = maxScore;
		ret[1] = chosenPP;
		return ret;
	}

	private static Object[] getScoreParaphrasedWithTurian(Chunk hypoChunk, Chunk textChunk, Map<String, Object> hypoAnno, int sentNumber) {
		boolean flag = false;
		Double maxScore = 0.0;
		String chosenPP = "";
		Object[] ret = new Object[2];
		if(hypoChunk.get_chunk().toLowerCase().equals(textChunk.get_chunk().toLowerCase())) {
			ret[0] = 1.0;
			ret[1] = hypoChunk.get_chunk();
			System.out.println("   xFound: " + hypoChunk.get_chunk() + " <> " + textChunk.get_chunk() + " == " + "1.0");
			return ret;
		}
		if(!shouldBeCompared(hypoChunk, textChunk)) {
			ret[0] = maxScore;
			ret[1] = chosenPP;
			return ret;
		}
		EnTurianSimilarity turianSim = new EnTurianSimilarity();
		//phrasal paraphrase

		Paraphrase pp = EnPPDB.lookUp(hypoChunk.get_chunk());
		for (FeatureSet fs : pp.get_featureSets()) {
			double tmpScore = turianSim.getSimilarity(fs.get_target(), textChunk.get_chunk());
			if (tmpScore >= maxScore) {
				maxScore = tmpScore;
				chosenPP = fs.get_target();
				if(maxScore >= _sgramThres)
					break;
			}
		}
		System.out.println("   ssFound: " + chosenPP + " <> " + textChunk.get_chunk() );
		if (maxScore >= _sgramThres) {
			System.out.println("   Found: " + chosenPP + " <> " + textChunk.get_chunk() + " == " + maxScore);
			flag = true;
		}
		//ccg paraphrase
		if (flag == false) {
			Map<String, Map<Integer, String>> hypoCCGs = changeToCCG(hypoChunk, hypoAnno, sentNumber);
			for (String ccg : hypoCCGs.keySet()) {
				Paraphrase ppccg = EnPPDB.lookUp(ccg);
				for (FeatureSet fs : ppccg.get_featureSets()) {
					String hypoParaphrase = getAlignTextFromPattern(hypoCCGs.get(ccg), fs.get_target(), fs.get_alignment());
					double tmpScore = turianSim.getSimilarity(hypoParaphrase, textChunk.get_chunk());
					if (tmpScore >= maxScore && tmpScore >= _sgramThres) {
						maxScore = tmpScore;
						chosenPP = hypoParaphrase;
						System.out.println("   Phrase(H): " + hypoChunk.get_chunk());
						System.out.println("       >>> CCG: " + ccg);
						System.out.println("       >>> PPDB: " + hypoParaphrase);
						System.out.println("       <> Phrase(T): " + textChunk.get_chunk() + " == " + tmpScore);
						break;
					}
				}
				if (maxScore >= _sgramThres)
					break;
			}
		}
		ret[0] = maxScore;
		ret[1] = chosenPP;
		return ret;
	}

	private static Object[] getScoreParaphrasedWithLCS(Chunk hypoChunk, Chunk textChunk, Map<String, Object> hypoAnno, int sentNumber) {
		boolean flag = false;
		Double maxScore = 0.0;
		String chosenPP = "";
		Object[] ret = new Object[2];
		if(!shouldBeCompared(hypoChunk, textChunk))
			return ret;
		EnMikolovSimilarity mikolovSim = new EnMikolovSimilarity();
		//phrasal paraphrase
		if(hypoChunk.get_chunk().toLowerCase().equals(textChunk.get_chunk().toLowerCase())) {
			ret[0] = 1.0;
			ret[1] = hypoChunk.get_chunk();
			System.out.println("   xFound: " + hypoChunk.get_chunk() + " <> " + textChunk.get_chunk() + " == " + "1.0");
			return ret;
		}
		Paraphrase pp = EnPPDB.lookUp(hypoChunk.get_chunk());
		for (FeatureSet fs : pp.get_featureSets()) {
			double tmpScore = mikolovSim.getSimilarity(fs.get_target(), textChunk.get_chunk());
			if (tmpScore >= maxScore) {
				maxScore = tmpScore;
				chosenPP = fs.get_target();
				if(maxScore >= _sgramThres)
					break;
			}
		}
		System.out.println("   ssFound: " + chosenPP + " <> " + textChunk.get_chunk() );
		if (maxScore >= _sgramThres) {
			System.out.println("   Found: " + chosenPP + " <> " + textChunk.get_chunk() + " == " + maxScore);
			flag = true;
		}
		//ccg paraphrase
		if (flag == false) {
			Map<String, Map<Integer, String>> hypoCCGs = changeToCCG(hypoChunk, hypoAnno, sentNumber);
			for (String ccg : hypoCCGs.keySet()) {
				Paraphrase ppccg = EnPPDB.lookUp(ccg);
				for (FeatureSet fs : ppccg.get_featureSets()) {
					String hypoParaphrase = getAlignTextFromPattern(hypoCCGs.get(ccg), fs.get_target(), fs.get_alignment());
					double tmpScore = mikolovSim.getSimilarity(hypoParaphrase, textChunk.get_chunk());
					if (tmpScore >= maxScore && tmpScore >= _sgramThres) {
						maxScore = tmpScore;
						chosenPP = hypoParaphrase;
						System.out.println("   Phrase(H): " + hypoChunk.get_chunk());
						System.out.println("       >>> CCG: " + ccg);
						System.out.println("       >>> PPDB: " + hypoParaphrase);
						System.out.println("       <> Phrase(T): " + textChunk.get_chunk() + " == " + tmpScore);
						break;
					}
				}
				if (maxScore >= _sgramThres)
					break;
			}
		}
		ret[0] = maxScore;
		ret[1] = chosenPP;
		return ret;
	}

	private static Object[] getScoreParaphrasedExactly(Chunk hypoChunk, Chunk textChunk, Map<String, Object> hypoAnno, int sentNumber) {
		boolean flag = false;
		Double maxScore = 0.0;
		String chosenPP = "";
		Object[] ret = new Object[2];
		if(!shouldBeCompared(hypoChunk, textChunk))
			return ret;
		EnMikolovSimilarity mikolovSim = new EnMikolovSimilarity();
		//phrasal paraphrase
		if(hypoChunk.get_chunk().toLowerCase().equals(textChunk.get_chunk().toLowerCase())) {
			ret[0] = 1.0;
			ret[1] = hypoChunk.get_chunk();
			System.out.println("   xFound: " + hypoChunk.get_chunk() + " <> " + textChunk.get_chunk() + " == " + "1.0");
			return ret;
		}
		Paraphrase pp = EnPPDB.lookUp(hypoChunk.get_chunk());
		for (FeatureSet fs : pp.get_featureSets()) {
			double tmpScore = mikolovSim.getSimilarity(fs.get_target(), textChunk.get_chunk());
			if (tmpScore >= maxScore) {
				maxScore = tmpScore;
				chosenPP = fs.get_target();
				if(maxScore >= _sgramThres)
					break;
			}
		}
		System.out.println("   ssFound: " + chosenPP + " <> " + textChunk.get_chunk() );
		if (maxScore >= _sgramThres) {
			System.out.println("   Found: " + chosenPP + " <> " + textChunk.get_chunk() + " == " + maxScore);
			flag = true;
		}
		//ccg paraphrase
		if (flag == false) {
			Map<String, Map<Integer, String>> hypoCCGs = changeToCCG(hypoChunk, hypoAnno, sentNumber);
			for (String ccg : hypoCCGs.keySet()) {
				Paraphrase ppccg = EnPPDB.lookUp(ccg);
				for (FeatureSet fs : ppccg.get_featureSets()) {
					String hypoParaphrase = getAlignTextFromPattern(hypoCCGs.get(ccg), fs.get_target(), fs.get_alignment());
					double tmpScore = mikolovSim.getSimilarity(hypoParaphrase, textChunk.get_chunk());
					if (tmpScore >= maxScore && tmpScore >= _sgramThres) {
						maxScore = tmpScore;
						chosenPP = hypoParaphrase;
						System.out.println("   Phrase(H): " + hypoChunk.get_chunk());
						System.out.println("       >>> CCG: " + ccg);
						System.out.println("       >>> PPDB: " + hypoParaphrase);
						System.out.println("       <> Phrase(T): " + textChunk.get_chunk() + " == " + tmpScore);
						break;
					}
				}
				if (maxScore >= _sgramThres)
					break;
			}
		}
		ret[0] = maxScore;
		ret[1] = chosenPP;
		return ret;
	}

	private static String getAlignTextFromPattern(Map<Integer, String> indexedCCG, String targetCCG, String alignment) {
		String[] tWords = targetCCG.split(" ");
		List<String> tAlignments = new ArrayList<String>();
		Collections.addAll(tAlignments, alignment.split(" "));
		Collections.sort(tAlignments);
		//String[] hypoTokens = hypoCCG.split(" ");
		List<String> ret = new ArrayList<String>();
		for (int i = 0; i < tWords.length; i++) {
			if (tWords[i].contains("[") && tWords[i].contains("]")) {
				for (int j = 0; j < tAlignments.size(); j++) {
					int sourceIndex = Integer.valueOf(tAlignments.get(j).split("-")[0]);
					int targetIndex = Integer.valueOf(tAlignments.get(j).split("-")[1]);
					if (targetIndex == i) {
						int index = Integer.valueOf(tWords[i].substring(1, tWords[i].length() - 1).split(",")[1]);
						ret.add(indexedCCG.get(index));
					}
				}
			} else
				ret.add(tWords[i]);
		}
		return EnUtils.mkString(ret.toArray(new String[0]));
	}

	private static Map<String, Map<Integer, String>> changeToCCG(Chunk hypoChunk, Map<String, Object> hypoAnno, int sentNumber) {
		Map<String, Map<Integer, String>> ret = new HashMap<>();
		if (hypoChunk.get_tokens().size() < _maxTokenNumber) {
			ret.putAll(changeToOneCCG(hypoChunk));
			ret.putAll(changeToTwoCCG(hypoChunk));
		}
		ret.putAll(changeToOneCCGPhrase(hypoChunk, hypoAnno, sentNumber));
		ret.putAll(changeToTwoCCGPhrase(hypoChunk, hypoAnno, sentNumber));
		return ret;
	}

	private static Map<String, Map<Integer, String>> changeToOneCCG(Chunk hypoChunk) {
		Map<String, Map<Integer, String>> ret = new HashMap<>();
		List<Token> tokens = new ArrayList<Token>(hypoChunk.get_tokens());
		for (int i = 0; i < tokens.size(); i++) {
			Map<Integer, String> ccgTmp = new HashMap<>();
			String firstMissing = "[" + tokens.get(i).get_pos() + ",1]";
			ccgTmp.put(1, tokens.get(i).get_word());
			String tmp = "";
			for (int j = 0; j < tokens.size(); j++)
				if (j == i)
					tmp = tmp + firstMissing + " ";
				else
					tmp = tmp + tokens.get(j).get_word() + " ";
			ret.put(tmp.trim(), ccgTmp);
		}
		return ret;
	}

	private static Map<String, Map<Integer, String>> changeToTwoCCG(Chunk hypoChunk) {
		Map<String, Map<Integer, String>> ret = new HashMap<>();
		List<Token> tokens = new ArrayList<Token>(hypoChunk.get_tokens());
		for (int i = 0; i < tokens.size() - 1; i++) {
			for (int j = i + 1; j < tokens.size(); j++) {
				Map<Integer, String> ccgTmp = new HashMap<>();
				String firstMissing = "[" + tokens.get(i).get_pos() + ",1]";
				String secondMissing = "[" + tokens.get(j).get_pos() + ",2]";
				ccgTmp.put(1, tokens.get(i).get_word());
				ccgTmp.put(2, tokens.get(j).get_word());
				String tmp = "";
				for (int k = 0; k < tokens.size(); k++)
					if (k == i)
						tmp = tmp + firstMissing + " ";
					else if (k == j)
						tmp = tmp + secondMissing + " ";
					else
						tmp = tmp + tokens.get(k).get_word() + " ";
				ret.put(tmp.trim(), ccgTmp);
			}
		}
		return ret;
	}

	private static Map<String, Map<Integer, String>> changeToOneCCGPhrase(Chunk hypoChunk, Map<String, Object> hypoAnno, int sentNumber) {
		Map<Integer[], String> table = ((Map<Integer, ChunkTable>) hypoAnno.get("chunkTable")).get(sentNumber).get_table();
		Map<String, Map<Integer, String>> ret = new HashMap<>();

		List<Token> tokens = new ArrayList<Token>(hypoChunk.get_tokens());
		int begin = hypoChunk.get_begin();
		int end = hypoChunk.get_end();

		for (Integer[] offset : table.keySet()) {
			if (offset[0] >= begin && offset[1] <= end && !(offset[0] == begin && offset[1] == end)) {
				String tmp = "";
				for (int i = begin; i < offset[0]; i++)
					tmp = tmp + tokens.get(i - begin).get_word() + " ";
				tmp = tmp + "[" + table.get(offset) + ",1]" + " "; //missing pattern
				Map<Integer, String> ccgTmp = new HashMap<>();
				String ccgMissing = "";
				for (int i = offset[0]; i <= offset[1]; i++) {
					ccgMissing = ccgMissing + tokens.get(i - begin).get_word() + " ";
				}
				ccgTmp.put(1, ccgMissing.trim());
				for (int i = offset[1] + 1; i <= end; i++)
					tmp = tmp + tokens.get(i - begin).get_word() + " ";
				ret.put(tmp.trim(), ccgTmp);
			}
		}
		return ret;
	}

	private static Map<String, Map<Integer, String>> changeToTwoCCGPhrase(Chunk hypoChunk, Map<String, Object> hypoAnno, int sentNumber) {
		Map<Integer[], String> table = ((Map<Integer, ChunkTable>) hypoAnno.get("chunkTable")).get(sentNumber).get_table();
		Map<String, Map<Integer, String>> ret = new HashMap<>();


		List<Token> tokens = new ArrayList<Token>(hypoChunk.get_tokens());
		int begin = hypoChunk.get_begin();
		int end = hypoChunk.get_end();
		for (int i = begin; i <= end; i++) {

			String firstMissing = "[" + tokens.get(i - begin).get_pos() + ",1]";

			for (Integer[] offset : table.keySet()) {
				if ((offset[0] >= (i + 1) && offset[1] <= end) || (offset[0] >= begin && offset[1] <= (i - 1))) {
					Map<Integer, String> ccgTmp = new HashMap<>();
					ccgTmp.put(1, tokens.get(i - begin).get_word());
					String secondMissing = "[" + table.get(offset) + ",2]"; //missing pattern
					String tmp = "";
					for (int j = begin; j < offset[0]; j++)
						if (j == i)
							tmp = tmp + firstMissing + " ";
						else
							tmp = tmp + tokens.get(j - begin).get_word() + " ";
					tmp = tmp + secondMissing + " "; //missing pattern
					String ccgMissing = "";
					for (int j = offset[0]; j <= offset[1]; j++)
						ccgMissing = ccgMissing + tokens.get(j - begin).get_word() + " ";
					ccgTmp.put(2, ccgMissing);
					for (int j = offset[1] + 1; j <= end; j++)
						if (j == i)
							tmp = tmp + firstMissing + " ";
						else
							tmp = tmp + tokens.get(j - begin).get_word() + " ";
					if (tmp.indexOf("2]") < tmp.indexOf("1]")) {
						String rev = ccgTmp.get(1);
						ccgTmp.put(1, ccgTmp.get(2));
						ccgTmp.put(2, rev);
						tmp = tmp.replace("2]", "3]");
						tmp = tmp.replace("1]", "2]");
						tmp = tmp.replace("3]", "1]");
					}
					ret.put(tmp.trim(), ccgTmp);
				}
			}
		}
		return ret;
	}

	public static List<NamedEntityChunk> extractNamedEntity(Map<String, Object> annotation){
		Map<Integer, List<Token>> tokensOfSent = Parser.getTokensInAnnotation(annotation);
		List<NamedEntityChunk> ret = new ArrayList<>();
		List<Token> entities = new ArrayList<>();
		try {
			for (Integer sent : tokensOfSent.keySet()) {
				List<Token> tokens = tokensOfSent.get(sent);
				if(tokens.size() < 1)
					return ret;
				if(tokens.size() >= 2) {
					if ((tokens.get(1).get_pos().equals("NNP") || tokens.get(1).get_pos().equals("NNPS")) && !tokens.get(2).get_ne().toLowerCase().equals("o"))
						tokens.get(1).set_ne(tokens.get(2).get_ne());
				}
				for (int i = 1, n = tokens.size(); i < n; i++) {
					if (!tokens.get(i).get_ne().toLowerCase().equals("o")) {
						entities.add(tokens.get(i));
					}
				}

				for (int i = 0, n = entities.size(); i < n; i++) {
					List<String> subret = new ArrayList<>();
					String ne = entities.get(i).get_ne();
					subret.add(entities.get(i).get_word());
					int j = i + 1;
					for (; j < n; j++) {
						if (entities.get(j).get_ne().equals(entities.get(i).get_ne()))
							if (entities.get(j).get_position() == (entities.get(j - 1).get_position() + 1)) {
								subret.add(entities.get(j).get_word());
								continue;
							}
						break;
					}
					String chunkOfEntity = EnUtils.mkString(subret.toArray(new String[0]));
					subret.clear();
					ret.add(new NamedEntityChunk(ne, chunkOfEntity));
					i = j - 1;
				}
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}

	public static Map<Integer, String> getTendency(Map<Integer, Pair> pairs){
		Map<Integer, String> ret = new HashMap<>();
		for(Integer id : pairs.keySet()){
			if(pairs.get(id).get_entailment().equals("NO") || pairs.get(id).get_entailment().equals("CONTRADICTION") || pairs.get(id).get_entailment().equals("UNKNOWN"))
				ret.put(id, getTendencyOfPair(pairs.get(id), Parser.parseTextToAnnotation(pairs.get(id).get_text()), Parser.parseTextToAnnotation(pairs.get(id).get_hypo())));
		}
		return ret;
	}

	public static String getTendencyOfPair(Pair pair, Map<String, Object> textAnnotation, Map<String, Object> hypoAnnotation){
		boolean flag = true;
		Map<String, Object> pairAnno = Parser.parseTextToAnnotation(pair.get_text() + " " + pair.get_hypo());
		Pair corefPair = CorefEngine.substituteCorefFromHypoToText(textAnnotation, hypoAnnotation, pairAnno);
		System.out.println(pair.get_id());
		System.out.println("Text: " + corefPair.get_text());
		List<NamedEntityChunk> namedEntityChunksOfText = extractNamedEntity(textAnnotation);
		List<NamedEntityChunk> tmpChunks = extractNamedEntity(Parser.parseTextToAnnotation(corefPair.get_text()));
		for(NamedEntityChunk newChunk : tmpChunks){
			boolean innerFlag = false;
			for(NamedEntityChunk oldChunk : namedEntityChunksOfText)
				if(oldChunk.equals(newChunk)) {
					innerFlag = true;
					break;
				}
			if(!innerFlag)
				namedEntityChunksOfText.add(newChunk);
		}
		for(NamedEntityChunk chunk : namedEntityChunksOfText)
			System.out.println(chunk.get_label() + ", " + chunk.get_chunk());
		System.out.println("Hypo: " + corefPair.get_hypo());
		List<NamedEntityChunk> namedEntityChunksOfHypo = extractNamedEntity(hypoAnnotation);
		for(NamedEntityChunk chunk : namedEntityChunksOfHypo)
			System.out.println(chunk.get_label() + ", " + chunk.get_chunk());
		if (!EnWordNet.isOpen())
			EnWordNet.open();

		for(NamedEntityChunk hypoChunk : namedEntityChunksOfHypo){
				boolean innerFlag = false;
				for(NamedEntityChunk textChunk : namedEntityChunksOfText) {
					if (textChunk.contains(hypoChunk) || (hypoChunk.get_label().equals("LOCATION") && EnResourceWN.isMeronym(hypoChunk.get_chunk(), textChunk.get_chunk())) ) {
						innerFlag = true;
						break;
					}
				}
				if (!innerFlag)
					flag = false;
		}
		String line = pair.get_id() + ", " + pair.get_entailment() + ", " + flag;
		System.out.println(line);
		System.out.println(" ");
		return line;
	}
}
