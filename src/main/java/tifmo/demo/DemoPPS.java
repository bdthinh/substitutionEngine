package tifmo.demo;

import org.apache.commons.io.input.ReversedLinesFileReader;
import tifmo.allEngine.PPEngine;
import tifmo.coreNLP.Pair;
import tifmo.en.EnFactory;
import tifmo.en.EnPPDB;
import tifmo.utils.EnUtils;

import java.io.*;
import java.util.*;

/**
 * Created by bdthinh on 10/25/14.
 */
public class DemoPPS {

	public static void main(String[] args) {
		if(args.length < 2)
			System.out.println("USAGE: PPDBpath RTEDataset1.xml RTEDataset2.xml ... RTEDataset3.xml");
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String sourcePath = currentPath.concat("/resources/").concat(args[0]);
		String destinationPath = currentPath.concat("/resources/cdb/").concat(args[0].substring(0,args[0].lastIndexOf(".")).concat(".cdb"));

		EnPPDB.loadCDBFromPPDB(sourcePath, destinationPath);
		EnFactory.loadMikolov();
		EnFactory.loadTurian();
		EnFactory.loadGlove();
		//args[1]: threshold for PPEngine
		PPEngine.set_sgramThres(Double.valueOf(args[1]));
		PPEngine.set_lcsThres(Double.valueOf(args[1]));
		//args[2-n]: name of dataset files
		List<String> files = new ArrayList<>();
		for(int i = 2; i < args.length - 1; i++)
			files.add(args[i]);
//		List<String> chosens = Arrays.asList("mikolov","turian","lcs");
		List<String> chosens = Arrays.asList("mikolov");
//		List<String> types = Arrays.asList("corefFirst");
		List<String> types = Arrays.asList("corefFirst","corefFirstReverse","corefLater","corefLaterReverse");
		boolean runMode = true;
		int keepParaphraseMode = Integer.valueOf(args[args.length-1]);
		if(keepParaphraseMode == 1)
			showTimeLongestParaphrase(files, chosens, types, runMode);
		else if (keepParaphraseMode == 0)
			showTimeShortestParaphrase(files, chosens, types, runMode);
	}

	/**
	 * Excutes substitution between text and hypo in each pair of each file : files with each chosen : chosens, each type : types. With every pair, only the longest paraphrases are kept
	 * @author bdthinh
	 * @param files: List&lt;String&gt;
	 * @param chosens: List&lt;String&gt;
	 * @param types: List&lt;String&gt;
	 * @param runMode: boolean ; true: continue with cached file if available ; false: start from beginning of file.
	 * @return Write all substituted pairs to new file with different names.
	 * @throws
	 */
	public static void showTimeLongestParaphrase(List<String> files, List<String> chosens, List<String> types, boolean runMode) {
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
				File cachedFile = new File(currentPath.concat("/resources/output/cachedLongest/").concat(files.get(i).substring(0, files.get(i).lastIndexOf("."))).concat(".txt"));
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
				List<Integer> sortedKeys = new ArrayList<>(originPairs.keySet());
				Collections.sort(sortedKeys);
				for (Integer id : sortedKeys) {
					if(id <= beginId)
						continue;
					Pair originPair = originPairs.get(id);
					System.out.println(files.get(i).substring(0, files.get(i).indexOf(".")) + "--id: " + id);
					Map<String, Map<String, Pair>> totalPairs = PPEngine.substituteAllLongestPP(originPair, chosens, types);
					List<String> lines = new ArrayList<>();
					for (String type : totalPairs.keySet()) {
						Map<String, Pair> pairsInType = totalPairs.get(type);
						for (String chosen : pairsInType.keySet()) {
							Pair pairTmp = pairsInType.get(chosen);
							pairTmp.set_id(originPair.get_id()).set_entailment(originPair.get_entailment()).set_task(originPair.get_task());
							if(!runMode)
								targetPairs.get(type).get(chosen).put(id, pairTmp);
							lines.add(pairTmp.get_id() + "|||" + type + "|||" + chosen + "|||" + pairTmp.get_entailment() + "|||" + pairTmp.get_task() + "|||" + pairTmp.get_text() + "|||" + pairTmp.get_hypo());
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
							String destinationFilePath = currentPath.concat("/resources/output/") + type + "/" + chosen + "_" + type + "_" + files.get(i);
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
							String destinationFilePath = currentPath.concat("/resources/output/") + type + "/" + chosen + "_" + type + "_" + files.get(i);
							EnUtils.writePairs(destinationFilePath, targetPairs.get(type).get(chosen));
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Excutes substitution between text and hypo in each pair of each file : files with each chosen : chosens, each type : types. With every pair, only the shortest paraphrases are kept
	 * @author bdthinh
	 * @param files: List&lt;String&gt;
	 * @param chosens: List&lt;String&gt;
	 * @param types: List&lt;String&gt;
	 * @param runMode: boolean ; true: continue with cached file if available ; false: start from beginning of file.
	 * @return Write all substituted pairs to new file with different names.
	 * @throws
	 */
	public static void showTimeShortestParaphrase(List<String> files, List<String> chosens, List<String> types, boolean runMode) {
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
				File cachedFile = new File(currentPath.concat("/resources/output/cachedShortest/").concat(files.get(i).substring(0, files.get(i).lastIndexOf("."))).concat(".txt"));
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
					System.out.println(files.get(i).substring(0, files.get(i).indexOf(".")) + "-ss-id: " + id);
					Map<String, Map<String, Pair>> totalPairs = PPEngine.substituteAllShortestPP(originPair, chosens, types);
					List<String> lines = new ArrayList<>();
					for (String type : totalPairs.keySet()) {
						Map<String, Pair> pairsInType = totalPairs.get(type);
						for (String chosen : pairsInType.keySet()) {
							Pair pairTmp = pairsInType.get(chosen);
							pairTmp.set_id(originPair.get_id()).set_entailment(originPair.get_entailment()).set_task(originPair.get_task());
							if(!runMode)
								targetPairs.get(type).get(chosen).put(id, pairTmp);
							lines.add(pairTmp.get_id() + "|||" + type + "|||" + chosen + "|||" + pairTmp.get_entailment() + "|||" + pairTmp.get_task() + "|||" + pairTmp.get_text() + "|||" + pairTmp.get_hypo());
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
							String destinationFilePath = currentPath.concat("/resources/output/") + type + "/ss_" + chosen + "_" + type + "_" + files.get(i);
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
							String destinationFilePath = currentPath.concat("/resources/output/") + type + "/ss_" + chosen + "_" + type + "_" + files.get(i);
							EnUtils.writePairs(destinationFilePath, targetPairs.get(type).get(chosen));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




}
