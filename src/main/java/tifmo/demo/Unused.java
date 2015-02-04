package tifmo.demo;

import org.apache.commons.io.input.ReversedLinesFileReader;
import tifmo.allEngine.PPEngine;
import tifmo.allEngine.PPIndexRecord;
import tifmo.allEngine.PPIndexTable;
import tifmo.coreNLP.Pair;
import tifmo.similarityComp.ParaphraseRecordComp;
import tifmo.utils.EnUtils;

import java.io.*;
import java.util.*;

/**
 * Created by bdthinh on 12/18/14.
 */
public class Unused {

	/*
	public static void showOutParaphraseTable() {
		PPIndexTable ptOrigin = new PPIndexTable();
		Integer[] a = {1, 5, 10};
		Integer[] target = {2, 3, 6};
		Integer[] b = {1, 3, 7};
		Integer[] asub = {1, 5, 8};
		Integer[] bsub = {1, 3, 4};
		ptOrigin.get_table().put(a, target);
		ptOrigin.get_table().put(asub, target);
		ptOrigin.get_table().put(b, target);
		ptOrigin.get_table().put(bsub, target);
		List<PPIndexTable> ptOptions = ptOrigin.filterKeepShorterParaphrase();
		for (PPIndexTable pt : ptOptions) {
			System.out.println("---");
			List<PPIndexRecord> pls = pt.sort();
			for (PPIndexRecord ppr : pls)
				System.out.println(ppr.get_textStartTokenNum() + " " + ppr.get_textEndTokenNum());
		}
	}

	public static void showOutSort() {
		PPIndexRecord ppr1 = new PPIndexRecord(1, 11, 12, 1, 3, 5);
		PPIndexRecord ppr2 = new PPIndexRecord(1, 8, 10, 1, 6, 8);
		PPIndexRecord ppr3 = new PPIndexRecord(1, 19, 20, 1, 4, 5);
		List<PPIndexRecord> pls = new ArrayList<>();
		pls.add(ppr1);
		pls.add(ppr2);
		pls.add(ppr3);
		Collections.sort(pls, new ParaphraseRecordComp());
		for (PPIndexRecord ppr : pls)
			System.out.println(ppr.get_textStartTokenNum() + " " + ppr.get_textEndTokenNum());
	}

	public static void showTimeCorefFirst(List<String> files, List<String> prefixes) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < files.size(); i++) {
			Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(files.get(i)));
			Map<String, Map<Integer, Pair>> targetPairs = new HashMap<>();
			for (String prefix : prefixes) {
				targetPairs.put(prefix, new HashMap<Integer, Pair>());
			}
			for (Integer id : originPairs.keySet()) {
				Pair pair = originPairs.get(id);
				System.out.println(files.get(i).substring(0, files.get(i).indexOf(".")) + "--id: " + id);
				Map<String, Pair> allPrefixPairs = PPEngine.substituteCorefFirstLongestPP(pair);
				for (String prefix : allPrefixPairs.keySet()) {
					Pair tmp = allPrefixPairs.get(prefix);
					tmp.set_id(pair.get_id());
					tmp.set_entailment(pair.get_entailment());
					tmp.set_task(pair.get_task());
					targetPairs.get(prefix).put(id, tmp);
				}
			}
			for (String prefix : prefixes) {
				String destinationFilePath = currentPath.concat("/resources/output/corefFirst/").concat(prefix).concat("_corefFirst_").concat(files.get(i));
				EnUtils.writePairs(destinationFilePath, targetPairs.get(prefix));
			}
		}

	}

	public static void showTimeCorefLater(List<String> files, List<String> prefixes) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < files.size(); i++) {
			Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(files.get(i)));
			Map<String, Map<Integer, Pair>> targetPairs = new HashMap<>();
			for (String prefix : prefixes) {
				targetPairs.put(prefix, new HashMap<Integer, Pair>());
			}
			for (Integer id : originPairs.keySet()) {
				Pair pair = originPairs.get(id);
				System.out.println(files.get(i).substring(0, files.get(i).indexOf(".")) + "--id: " + id);
				Map<String, Pair> allPrefixPairs = PPEngine.substituteCorefLaterLongestPP(pair);
				for (String prefix : allPrefixPairs.keySet()) {
					Pair tmp = allPrefixPairs.get(prefix);
					tmp.set_id(pair.get_id());
					tmp.set_entailment(pair.get_entailment());
					tmp.set_task(pair.get_task());
					targetPairs.get(prefix).put(id, tmp);
				}
			}
			for (String prefix : prefixes) {
				String destinationFilePath = currentPath.concat("/resources/output/corefLater/").concat(prefix).concat("_corefLater_").concat(files.get(i));
				EnUtils.writePairs(destinationFilePath, targetPairs.get(prefix));
			}
		}

	}

	public static void showTimeCorefFirstReverse(List<String> files, List<String> prefixes) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < files.size(); i++) {
			Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(files.get(i)));
			Map<String, Map<Integer, Pair>> targetPairs = new HashMap<>();
			for (String prefix : prefixes) {
				targetPairs.put(prefix, new HashMap<Integer, Pair>());
			}
			for (Integer id : originPairs.keySet()) {
				Pair pair = originPairs.get(id);
				System.out.println(files.get(i).substring(0, files.get(i).indexOf(".")) + "--id: " + id);
				Map<String, Pair> allPrefixPairs = PPEngine.substituteCorefFirstReverseLongestPP(pair);
				for (String prefix : allPrefixPairs.keySet()) {
					Pair tmp = allPrefixPairs.get(prefix);
					tmp.set_id(pair.get_id());
					tmp.set_entailment(pair.get_entailment());
					tmp.set_task(pair.get_task());
					targetPairs.get(prefix).put(id, tmp);
				}
			}
			for (String prefix : prefixes) {
				String destinationFilePath = currentPath.concat("/resources/output/corefFirstReverse/").concat(prefix).concat("_corefFirstReverse_").concat(files.get(i));
				EnUtils.writePairs(destinationFilePath, targetPairs.get(prefix));
			}
		}

	}

	public static void showTimeCorefLaterReverse(List<String> files, List<String> prefixes) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < files.size(); i++) {
			Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(files.get(i)));
			Map<String, Map<Integer, Pair>> targetPairs = new HashMap<>();
			for (String prefix : prefixes) {
				targetPairs.put(prefix, new HashMap<Integer, Pair>());
			}
			for (Integer id : originPairs.keySet()) {
				Pair pair = originPairs.get(id);
				System.out.println(files.get(i).substring(0, files.get(i).indexOf(".")) + "--id: " + id);
				Map<String, Pair> allPrefixPairs = PPEngine.substituteCorefLaterReverseLongestPP(pair);
				for (String prefix : allPrefixPairs.keySet()) {
					Pair tmp = allPrefixPairs.get(prefix);
					tmp.set_id(pair.get_id());
					tmp.set_entailment(pair.get_entailment());
					tmp.set_task(pair.get_task());
					targetPairs.get(prefix).put(id, tmp);
				}
			}
			for (String prefix : prefixes) {
				String destinationFilePath = currentPath.concat("/resources/output/corefLaterReverse/").concat(prefix).concat("_corefLaterReverse_").concat(files.get(i));
				EnUtils.writePairs(destinationFilePath, targetPairs.get(prefix));
			}
		}
	}

	public static void showTimeKeepShorterParaphraseAlso(List<String> files, List<String> chosens, List<String> types, boolean runMode) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
			for (int i = 0; i < files.size(); i++) {
				Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(files.get(i)));
				Map<String, Map<String, Map<Integer, Pair>>> targetPairs = new HashMap<>();
				for (String type : types) {
					targetPairs.put(type, new HashMap<String, Map<Integer, Pair>>());
					for (String chosen : chosens) {
						targetPairs.get(type).put(chosen, new HashMap<Integer, Pair>());
					}
				}
				File cachedFile = new File(currentPath.concat("/resources/output/cachedExtend/").concat(files.get(i).substring(0, files.get(i).lastIndexOf("."))).concat(".txt"));
				int beginId = 0;
				if (runMode && cachedFile.exists()) {
					ReversedLinesFileReader rlfr = new ReversedLinesFileReader(cachedFile);
					String line = rlfr.readLine();
					if(line == null) {
						System.out.println(files.get(i) + ": Cache's empty. Let's start at beginning.");
						cachedFile.createNewFile();
					}
					else {
						System.out.println(files.get(i) + ": Cache's loaded.");
						beginId = Integer.valueOf(line.split("\\|\\|\\|")[0]);
					}
				}
				else if (!cachedFile.exists())
					cachedFile.createNewFile();
				for (Integer id : originPairs.keySet()) {
					if(id <= beginId)
						continue;
					Pair originPair = originPairs.get(id);
					System.out.println(files.get(i).substring(0, files.get(i).indexOf(".")) + "--id: " + id);
					Map<String, Map<String, List<Pair>>> totalPairs = PPEngine.substituteAllKeepShorterParaphrase(originPair, chosens, types);
					List<String> lines = new ArrayList<>();
					for (String type : totalPairs.keySet()) {
						Map<String, List<Pair>> pairsInType = totalPairs.get(type);
						for (String chosen : pairsInType.keySet()) {
							List<Pair> pairTmps = pairsInType.get(chosen);
							for(Pair pairTmp : pairTmps) {
								pairTmp.set_id(originPair.get_id()).set_entailment(originPair.get_entailment()).set_task(originPair.get_task());
								if (!runMode)
									targetPairs.get(type).get(chosen).put(id, pairTmp);
								lines.add(pairTmp.get_id() + "|||" + type + "|||" + chosen + "|||" + pairTmp.get_entailment() + "|||" + pairTmp.get_task() + "|||" + pairTmp.get_text() + "|||" + pairTmp.get_hypo());
							}
						}
					}
					BufferedWriter bw = new BufferedWriter(new FileWriter(cachedFile.getAbsoluteFile(), runMode));
					for (String line : lines) {
						bw.write(line);
						bw.newLine();
					}
					bw.flush();
					bw.close();
				}
				if(!runMode) {
					for (String type : types) {
						for (String chosen : chosens) {
							String destinationFilePath = currentPath.concat("/resources/output/") + type + "/all_" + chosen + "_" + type + "_" + files.get(i);
							EnUtils.writePairs(destinationFilePath, targetPairs.get(type).get(chosen));
						}
					}
				}
				else{
					BufferedReader br = new BufferedReader(new FileReader(cachedFile));
					String line;
					while((line = br.readLine()) != null){
						String[] splitted = line.split("\\|\\|\\|");
						Pair pairOfLine = new Pair(Integer.valueOf(splitted[0]),splitted[5],splitted[6],splitted[3],splitted[4]);
						targetPairs.get(splitted[1]).get(splitted[2]).put(pairOfLine.get_id(),pairOfLine);
					}
					for (String type : types) {
						for (String chosen : chosens) {
							String destinationFilePath = currentPath.concat("/resources/output/") + type + "/all_" + chosen + "_" + type + "_" + files.get(i);
							EnUtils.writePairs(destinationFilePath, targetPairs.get(type).get(chosen));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
}
