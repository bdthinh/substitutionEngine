package tifmo.allEngine;

import com.mashape.unirest.http.Unirest;
import tifmo.allEngine.TheySay.SentimentEngine;
import tifmo.coreNLP.Experiment;
import tifmo.coreNLP.Pair;
import tifmo.utils.EnUtils;

import java.io.*;
import java.util.*;

/**
 * Created by bdthinh on 12/3/14.
 * This class is used to analyze the results from lattice approach in TIFMO. Apply some heuristics and rules to filter the negation pairs.
 */
public class ResultAnalyzer {

	public static void executor(String[] args){
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		List<String> baselines = Arrays.asList("tian2dev.txt", "tian2test.txt", "tian3dev.txt", "tian3test.txt", "tian4test.txt", "tian5dev.txt", "tian5test.txt");
		List<String> baselines = Arrays.asList("msrp_train.txt","msrp_test.txt");
//		List<String> files = Arrays.asList("RTE2_dev.txt","RTE2_test.txt","RTE3_dev.txt","RTE3_test.txt","RTE4_test.txt","RTE5_dev.txt","RTE5_test.txt");
		List<String> files = Arrays.asList("msrp_train.txt","msrp_test.txt");
//		List<String> chosens = Arrays.asList("mikolov", "turian", "lcs");
		List<String> chosens = Arrays.asList("mikolov");
		List<String> types = Arrays.asList("corefFirst","corefFirstReverse","corefLater","corefLaterReverse");
//		List<String> types = Arrays.asList("corefFirst");
		//List<String> types = Arrays.asList("corefLaterReverse");
		for(String chosen : chosens) {
//			List<Double> thresholds = Arrays.asList(0.4,0.5,0.6,0.7,1.0);
			List<Double> thresholds = Arrays.asList(0.4);
			List<String> retFiles = new ArrayList<>();
			for (int i = 0; i < baselines.size(); i++) {
				List<String> paths = new ArrayList<>();
				paths.add(currentPath.concat("/resources/input/result/").concat(baselines.get(i)));
				for (String type : types){
					paths.add(currentPath.concat("/resources/input/result/").concat(type).concat("/").concat(chosen+"_"+type+"_").concat(files.get(i)));
				}
				Map<Integer, String> bestRecords = getTheBestScoreForLattice(paths);
				String latticePath = currentPath.concat("/resources/input/result/").concat(chosen).concat("_").concat("lattice").concat(files.get(i));
				if(writeFile(latticePath, bestRecords)) {
					String destinationCSV = currentPath.concat("/resources/input/csv/").concat(chosen + "_" + files.get(i).substring(0, files.get(i).lastIndexOf("."))).concat(".csv");
					writeEachToCSV(Arrays.asList(currentPath.concat("/resources/input/result/").concat(baselines.get(i)), latticePath), thresholds, destinationCSV);
					retFiles.add(currentPath.concat("/resources/input/result/").concat(baselines.get(i)));
					retFiles.add(latticePath);
				}
			}
			String destinationCSV = currentPath.concat("/resources/input/csv/").concat(chosen + "_RTE.csv");
			writeAllToCSV(retFiles, thresholds, destinationCSV);
		}

		System.out.println("DONE!!!");
	}

	public static void executorWithTendency(String[] args){
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> baselines = Arrays.asList("tian2dev.txt", "tian2test.txt", "tian3dev.txt", "tian3test.txt", "tian4test.txt", "tian5dev.txt", "tian5test.txt");
		List<String> baselineTendencys = Arrays.asList("RTE2_dev_tendency.txt", "RTE2_test_tendency.txt", "RTE3_dev_tendency.txt", "RTE3_test_tendency.txt", "RTE4_test_tendency.txt", "RTE5_dev_tendency.txt", "RTE5_test_tendency.txt");
		List<String> files = Arrays.asList("RTE2_dev.txt","RTE2_test.txt","RTE3_dev.txt","RTE3_test.txt","RTE4_test.txt", "RTE5_dev.txt", "RTE5_test.txt");
		List<String> chosens = Arrays.asList("mikolov");
		List<String> types = Arrays.asList("corefFirst","corefFirstReverse","corefLater","corefLaterReverse");
		for(String chosen : chosens) {
			List<Double> thresholds = Arrays.asList(0.4, 0.5, 0.6, 0.7, 1.0);
			List<String> retFiles = new ArrayList<>();
			for (int i = 0; i < baselines.size(); i++) {
				List<String> paths = new ArrayList<>();
				paths.add(currentPath.concat("/resources/input/result/").concat(baselines.get(i)));
				for (String type : types){
					paths.add(currentPath.concat("/resources/input/result/").concat(type).concat("/").concat(chosen+"_"+type+"_").concat(files.get(i)));
				}
				Map<Integer, String> originalTendency = readTendency(currentPath.concat("/resources/input/result/").concat(baselineTendencys.get(i)));
				Map<Integer, String> originalPairs = readFile(currentPath.concat("/resources/input/result/").concat(baselines.get(i)));
				for(Integer id : originalTendency.keySet())
					System.out.println(id + "," + originalPairs.get(id).split(",")[2] + "," + originalTendency.get(id).split(",")[1]);
				Map<Integer, String> bestRecords = getTheBestScoreForLatticeWithTendency(paths, originalTendency);
				String latticePath = currentPath.concat("/resources/input/result/").concat(chosen).concat("_").concat("lattice").concat(files.get(i));
				if(writeFile(latticePath, bestRecords)) {
					String destinationCSV = currentPath.concat("/resources/input/csv/").concat(chosen + "_" + files.get(i).substring(0, files.get(i).lastIndexOf("."))).concat(".csv");
					writeEachToCSV(Arrays.asList(currentPath.concat("/resources/input/result/").concat(baselines.get(i)), latticePath), thresholds, destinationCSV);
					retFiles.add(currentPath.concat("/resources/input/result/").concat(baselines.get(i)));
					retFiles.add(latticePath);
				}
			}
			String destinationCSV = currentPath.concat("/resources/input/csv/").concat(chosen + "_RTE.csv");
			writeAllToCSV(retFiles, thresholds, destinationCSV);
		}

		System.out.println("DONE!!!");
	}

	private static Map<Integer, String> readTendency(String filePath) {
		return readFile(filePath);
	}

	public static void executorWriteTendency(String[] args){
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			//List<String> files = Arrays.asList("mikolov_corefFirst_RTE2_dev.xml","mikolov_corefFirst_RTE2_test.xml","mikolov_corefFirst_RTE3_dev.xml","mikolov_corefFirst_RTE3_test.xml");
			//List<String> files = Arrays.asList("RTE2_dev.xml","RTE2_test.xml","RTE3_dev.xml","RTE3_test.xml","RTE4_test.xml");
			List<String> files = Arrays.asList("RTE5_dev.xml","RTE5_test.xml");
			//List<String> files = Arrays.asList("mikolov_corefFirst_RTE5_dev.xml","mikolov_corefFirst_RTE5_test.xml");
			for(String file : files)
				writeTendencyToFile(currentPath.concat("/resources/input/").concat(file));
			Unirest.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void executorWriteFalsePositive(String[] args){
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> srcFilePathAs = Arrays.asList("RTE2_dev.xml","RTE2_test.xml","RTE3_dev.xml","RTE3_test.xml","RTE4_test.xml","RTE5_dev.xml","RTE5_test.xml");
		List<String> retFileAs = Arrays.asList("tian2dev.txt", "tian2test.txt", "tian3dev.txt", "tian3test.txt", "tian4test.txt", "tian5dev.txt", "tian5test.txt");
		List<String> retFileBs = Arrays.asList("RTE2_dev.txt","RTE2_test.txt","RTE3_dev.txt","RTE3_test.txt","RTE4_test.txt","RTE5_dev.txt","RTE5_test.txt");
		//mikolov_latticeRTE2_dev
		for(int i = 0 ; i < srcFilePathAs.size() ; i ++){
			writeFalsePositiveToFile(currentPath.concat("/resources/input/").concat(srcFilePathAs.get(i))
							, currentPath.concat("/resources/input/").concat("mikolov_corefFirst_").concat(srcFilePathAs.get(i))
							, currentPath.concat("/resources/input/").concat("result/").concat(retFileAs.get(i))
							, currentPath.concat("/resources/input/").concat("result/mikolov_lattice").concat(retFileBs.get(i))
							, currentPath.concat("/resources/input/").concat("FP_").concat(retFileAs.get(i).substring(0,retFileAs.get(i).lastIndexOf(".")).concat(".xml")));
			System.out.println(srcFilePathAs.get(i) + "...doned!");
		}
	}

	public static void main(String[] args) {
//		executorWithTendency(args);
		executor(args);
//		executorWriteTendency(args);
//		executorWriteFalsePositive(args);
//		executor(args);
//		String currentPath = "";
//		try {
//			currentPath = new File("").getCanonicalPath();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		List<String> filePaths = Arrays.asList(currentPath.concat("/resources/output/msrp/test.txt"), currentPath.concat("/resources/output/msrp/ss_test.txt"));
//		writeFile(currentPath.concat("/resources/output/msrp/lattice.txt"),getTheBestScoreForLattice(filePaths));
		System.err.println("Exit with code = 0");
	}

	public static boolean writeFile(String filePath, Map<Integer, String> records) {
		File file = new File(filePath);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			List<Integer> sortedKeys = new ArrayList<>(records.keySet());
			Collections.sort(sortedKeys);
			for (Integer id : sortedKeys) {
				bw.write(records.get(id));
				bw.newLine();
			}
			bw.flush();
			bw.close();
			System.out.println("Write result succesfully!");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static Map<Integer, String> readFile(String filePath) {
		Map<Integer, String> tmp = new HashMap<Integer, String>();
		Map<Integer, String> ret = new HashMap<Integer, String>();
		File file = new File(filePath);
		if (file.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					String[] parts = line.split(",");
					tmp.put(Integer.valueOf(parts[0]), line);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		List<Integer> sortedKeys = new ArrayList<>(tmp.keySet());
		Collections.sort(sortedKeys);
		for(Integer id : sortedKeys)
			ret.put(id, tmp.get(id));
		return ret;
	}

	public static Map<Integer, String> getTendency(String filePath){
		Map<Integer, String> ret = new HashMap<>();
		try {
			Map<Integer, Pair> pairs = EnUtils.readPairs(filePath);
			List<Integer> sortedKeys = new ArrayList<>(pairs.keySet());
			Collections.sort(sortedKeys);
			ret = SentimentEngine.getTendency(pairs);
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return ret;
	}

	public static void writeTendencyToFile(String filePath){
		Map<Integer,String> ret = getTendency(filePath);
		Map<Integer,Pair> pairs = EnUtils.readPairs(filePath);
		for(Integer id : ret.keySet())
			System.out.println(id + "," + pairs.get(id).get_entailment() + "," + ret.get(id));
		writeFile(filePath.substring(0,filePath.lastIndexOf(".")).concat("_tendency.txt"), ret);
	}

	public static Map<Integer, String> getTheBestScoreForLattice(List<String> filePaths) {
		Map<Integer, String> ret = new HashMap<>();
		List<Map<Integer, String>> results = new ArrayList<>();
		for (String filePath : filePaths) {
			results.add(readFile(filePath));
		}
		//rets.get(0) is represented for original RTE dataset
		for (Integer id : results.get(0).keySet()) {
			List<Double> scores = new ArrayList<>();
			for (int i = 0; i < results.size(); i++) {
				String line = results.get(i).get(id);
				if(line != null)
					scores.add(Double.valueOf(results.get(i).get(id).split(",")[3]));
			}
			Double bestScore;
			bestScore = Collections.max(scores);
			String bestScoreLine = id
							+ "," + results.get(0).get(id).split(",")[1]
							+ "," + results.get(0).get(id).split(",")[2]
							+ "," + bestScore;
			ret.put(id, bestScoreLine);
		}
		return ret;
	}

	public static Map<Integer, String> getTheBestScoreForLatticeWithTendency(List<String> filePaths, Map<Integer, String> originalTendency) {
		Map<Integer, String> ret = new HashMap<>();
		List<Map<Integer, String>> results = new ArrayList<>();
		for (String filePath : filePaths) {
			results.add(readFile(filePath));
		}
		//rets.get(0) is represented for original RTE dataset
		for (Integer id : results.get(0).keySet()) {
			List<Double> scores = new ArrayList<>();
			for (int i = 0; i < results.size(); i++) {
				scores.add(Double.valueOf(results.get(i).get(id).split(",")[3]));
			}
			Double bestScore;
			String tendency = originalTendency.get(id).split(",")[1];
			if (tendency.equals("true"))
				bestScore = Collections.max(scores);
			else
				bestScore = Collections.min(scores);
			String bestScoreLine = id
							+ "," + results.get(0).get(id).split(",")[1]
							+ "," + results.get(0).get(id).split(",")[2]
							+ "," + bestScore;
			ret.put(id, bestScoreLine);
		}
		return ret;
	}

	public static Map<String,Integer> getEvaluationRTE(Map<Integer, String> records, double thres) {
		Map<String, Integer> ret = new HashMap<>();
		int total = 0;
		int correct = 0;
		int y_gold = 0;
		int y_sys = 0;
		int y_correct = 0;
		int falsePositive = 0;
		int falseNegative = 0;
		int truePositive = 0;
		int trueNegative = 0;
		for (Integer id : records.keySet()) {
			String[] parts = records.get(id).split(",");
			String gold = parts[2];
			String sys;
			sys = Double.valueOf(parts[3]) >= thres ? "Y" : "N";
			total++;
			if (gold.equals(sys))
				correct++;
			if (gold.equals("Y"))
				y_gold++;
			if (sys.equals("Y"))
				y_sys++;
			if (gold.equals("Y") && sys.equals("Y")) {
				y_correct++;
				truePositive++;
			}
			if(gold.equals("Y") && sys.equals("N"))
				falseNegative++;
			if(gold.equals("N") && sys.equals("N"))
				trueNegative++;
			if(gold.equals("N") && sys.equals("Y"))
				falsePositive++;
		}
		ret.put("total",total);
		ret.put("correct",correct);
		ret.put("ygold",y_gold);
		ret.put("ysys",y_sys);
		ret.put("ycorrect",y_correct);

		ret.put("falsePositive", falsePositive);
		ret.put("falseNegative", falseNegative);
		ret.put("truePositive", truePositive);
		ret.put("trueNegative", trueNegative);
		return ret;

	}

	public static double getMatthewsCorrelationCoefficient(Map<String, Integer> result){
		int TN = result.get("trueNegative");
		int TP = result.get("truePositive");
		int FN = result.get("falseNegative");
		int FP = result.get("falsePositive");
		double MCC = (double)((TP * TN) - (FP * FN)) / (Math.sqrt((double)(TP + FP)) * Math.sqrt((double)(TP + FN)) * Math.sqrt((double)(TN + FP)) * Math.sqrt((double)(TN + FN)));
		return MCC;
	}

	public static double getFalseDiscoveryRate(Map<String, Integer> result){
		int FP = result.get("falsePositive");
		int ysys = result.get("ysys");
		double FDR = (double)FP / ysys;
		return FDR;
	}

	public static Experiment getExperimentInstance(Map<String, Integer> result){
		Experiment ret = new Experiment();
		int TN = result.get("trueNegative");
		int TP = result.get("truePositive");
		int FN = result.get("falseNegative");
		int FP = result.get("falsePositive");
		ret.set_truePositive(TP).set_trueNegative(TN).set_falsePositive(FP).set_falseNegative(FN);
		return ret;
	}

	public static void writeAllToCSV(List<String> filePaths, List<Double> thresholds, String destFilePath) {
		//Map<Double,Map<String,String>> linesOfSolution = new HashMap<>();
		List<String> linesToPrint = new ArrayList<>();
		for(Double thres: thresholds){
			linesToPrint.add("Threshold,"+ String.valueOf(thres));
			linesToPrint.add("Filename, Correct/ Total, Accuracy, Precision, Recall, F1-score");
			//, MCC, FDR, LRplus, LRminus");
			for (String filePath : filePaths) {
				Map<String, Integer> result = getEvaluationRTE(readFile(filePath), thres);
				String total = String.valueOf(result.get("total"));
				String correct = String.valueOf(result.get("correct"));
				String accuracy = String.valueOf(Double.valueOf(result.get("correct")) / result.get("total"));
				String precision = String.valueOf((Double.valueOf(result.get("ycorrect")) / result.get("ysys")));
				String recall = String.valueOf(Double.valueOf(result.get("ycorrect")) / result.get("ygold"));
				String f1score = String.valueOf(2.0 * Double.valueOf(precision) * Double.valueOf(recall) / (Double.valueOf(precision) + Double.valueOf(recall)));
				String MCC = String.valueOf(getMatthewsCorrelationCoefficient(result));
				String FDR = String.valueOf(getFalseDiscoveryRate(result));
				Experiment experiment = getExperimentInstance(result);
				String LRplus = String.valueOf(experiment.getPositiveLikelihoodRatio());
				String LRminus = String.valueOf(experiment.getNegativeLikelihoodRatio());
				String line = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length())
								+ "," + correct + "/ " + total
								+ "," + accuracy
								+ "," + precision
								+ "," + recall
								+ "," + f1score;
//								+ "," + MCC
//								+ "," + FDR
//								+ "," + LRplus
//								+ "," + LRminus;
				linesToPrint.add(line);
			}
		}

		File file = new File(destFilePath);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			for (String line : linesToPrint) {
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			System.out.println("Write accuracy in CSV format succesfully: " + destFilePath.substring(destFilePath.lastIndexOf("/") + 1, destFilePath.length()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeEachToCSV(List<String> filePaths, List<Double> thresholds, String destFilePath) {
		List<String> lines = new ArrayList<String>();
		lines.add("Filename, Threshold of OTF, Correct/ Total, Accuracy, Precision, Recall, F1-score, MCC, FDR, LRplus, LRminus");
		for (String filePath : filePaths) {
			for (Double thres : thresholds) {
				Map<String,Integer> result = getEvaluationRTE(readFile(filePath), thres);
				String total = String.valueOf(result.get("total"));
				String correct = String.valueOf(result.get("correct"));
				String accuracy = String.valueOf(Double.valueOf(result.get("correct")) / result.get("total"));
				String precision = String.valueOf((Double.valueOf(result.get("ycorrect")) / result.get("ysys")));
				String recall = String.valueOf(Double.valueOf(result.get("ycorrect")) / result.get("ygold"));
				String f1score = String. valueOf(2.0 * Double.valueOf(precision) * Double.valueOf(recall) / (Double.valueOf(precision)+Double.valueOf(recall)));
				String MCC = String.valueOf(getMatthewsCorrelationCoefficient(result));
				String FDR = String.valueOf(getFalseDiscoveryRate(result));
				Experiment experiment = getExperimentInstance(result);
				String LRplus = String.valueOf(experiment.getPositiveLikelihoodRatio());
				String LRminus = String.valueOf(experiment.getNegativeLikelihoodRatio());
				String line = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length())
								+ "," + thres
								+ "," + correct + "/ " + total
								+ "," + accuracy
								+ "," + precision
								+ "," + recall
								+ "," + f1score
								+ "," + MCC
								+ "," + FDR
								+ "," + LRplus
								+ "," + LRminus;
				lines.add(line);
			}
		}
		File file = new File(destFilePath);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			for (String line : lines) {
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			System.out.println("Write accuracy in CSV format succesfully: " + destFilePath.substring(destFilePath.lastIndexOf("/") + 1, destFilePath.length()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeFalsePositiveToFile(String srcFilePathA, String srcFilePathB, String retFileA, String retFileB, String destFilePath){
		Map<Integer, Pair[]> pairs = new HashMap<>();
		Map<Integer, String> retA = readFile(retFileA);
		Map<Integer, String> retB = readFile(retFileB);
		Map<Integer, Pair> pairsA = EnUtils.readPairs(srcFilePathA);
		Map<Integer, Pair> pairsB = EnUtils.readPairs(srcFilePathB);
		for(Integer id : retA.keySet()){
			if(retA.get(id).split(",")[2].equals("N") && Double.valueOf(retA.get(id).split(",")[3]) < 1.0)
				if(Double.valueOf(retB.get(id).split(",")[3]) == 1.0){
					Pair tian = pairsA.get(id);
					Pair jin = pairsB.get(id);
					pairs.put(id, new Pair[]{tian, jin});
				}
		}
		EnUtils.writeDoublePairs(destFilePath, pairs);
	}
}
