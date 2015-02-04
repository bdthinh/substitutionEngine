package tifmo.demo;

import org.apache.commons.io.input.ReversedLinesFileReader;
import tifmo.allEngine.DIRTEngine;
import tifmo.coreNLP.Pair;
import tifmo.coreNLP.Parser;
import tifmo.en.EnFactory;
import tifmo.utils.EnUtils;

import java.io.*;
import java.util.*;

/**
 * Created by bdthinh on 12/15/14.
 */
public class DemoDirtS {

	public static void executor(String[] args){
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EnFactory.loadDIRT();
		Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(args[0]));
		boolean runMode = (Integer.valueOf(args[1]) == 1)?true:false;
		Map<Integer, Pair> substitutedPairs = new HashMap<>();
		List<Integer> sortedKeys = new ArrayList<>(originPairs.keySet());
		Collections.sort(sortedKeys);
		File cachedFile = new File(currentPath.concat("/resources/output/dirt/cache/").concat(args[0].substring(0,args[0].lastIndexOf("."))).concat(".txt"));
		int beginId = 0;
		try {
			if (runMode && cachedFile.exists()) {
				ReversedLinesFileReader rlfr = new ReversedLinesFileReader(cachedFile);
				String line = rlfr.readLine();
				if (line == null) {
					System.out.println(args[0] + ": Cache's empty. Let's start at beginning.");
					cachedFile.createNewFile();
				} else {
					System.out.println(args[0] + ": Cache's loaded.");
					beginId = Integer.valueOf(line.split("\\|\\|\\|")[0]);
				}
			}
			else if(runMode)
				cachedFile.createNewFile();
		} catch (IOException ex){
			ex.printStackTrace();
		}
		Map<Integer, String> coverage = new HashMap<>();
		for(Integer id : sortedKeys){
			if(id <= beginId)
				continue;
			System.out.println("id " + id);
			Map<String, Object> textAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_text());
			Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_hypo());
			Pair substitutedPair = DIRTEngine.substitutePair(textAnno, hypoAnno);
			coverage.put(id, DIRTEngine.getCoverage());
			substitutedPair.set_id(originPairs.get(id).get_id()).set_entailment(originPairs.get(id).get_entailment()).set_task(originPairs.get(id).get_task()).set_fromFile(originPairs.get(id).get_fromFile());
			String line = substitutedPair.get_id() + "|||" + substitutedPair.get_entailment() + "|||" + substitutedPair.get_task() + "|||" + substitutedPair.get_text() + "|||" + substitutedPair.get_hypo();
			substitutedPairs.put(id, substitutedPair);
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(cachedFile.getAbsoluteFile(), runMode));
				bw.write(line);
				bw.newLine();
				bw.flush();
				bw.close();
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		if(runMode){
			try {
				substitutedPairs.clear();
				BufferedReader br = new BufferedReader(new FileReader(cachedFile));
				String line;
				while ((line = br.readLine()) != null) {
					String[] splitted = line.split("\\|\\|\\|");
					Pair pairOfLine = new Pair(Integer.valueOf(splitted[0]), splitted[3], splitted[4], splitted[1], splitted[2]);
					substitutedPairs.put(Integer.valueOf(splitted[0]),pairOfLine);
				}
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		String destinationFilePath = currentPath.concat("/resources/output/dirt/dirt_") + args[0];
		EnUtils.writePairsByDIRTEngine(destinationFilePath, substitutedPairs, coverage);
		String logFilePath = currentPath.concat("/resources/output/dirt/dirt_") + args[0].substring(0,args[0].lastIndexOf(".")).concat(".log");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(logFilePath));
			sortedKeys = new ArrayList<>(coverage.keySet());
			Collections.sort(sortedKeys);
			for (Integer id : sortedKeys) {
				bw.write(coverage.get(id));
				bw.newLine();
			}
			bw.flush();
			bw.close();
			System.out.println("Write log result succesfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void executorExtendPhrase(String[] args){
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EnFactory.loadDIRT();
		Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(args[0]));
		boolean runMode = (Integer.valueOf(args[1]) == 1)?true:false;
		Map<Integer, Pair> substitutedPairs = new HashMap<>();
		List<Integer> sortedKeys = new ArrayList<>(originPairs.keySet());
		Collections.sort(sortedKeys);
		File cachedFile = new File(currentPath.concat("/resources/output/dirt/cache/xp_").concat(args[0].substring(0,args[0].lastIndexOf("."))).concat(".txt"));
		int beginId = 0;
		try {
			if (runMode && cachedFile.exists()) {
				ReversedLinesFileReader rlfr = new ReversedLinesFileReader(cachedFile);
				String line = rlfr.readLine();
				if (line == null) {
					System.out.println(args[0] + ": Cache's empty. Let's start at beginning.");
					cachedFile.createNewFile();
				} else {
					System.out.println(args[0] + ": Cache's loaded.");
					beginId = Integer.valueOf(line.split("\\|\\|\\|")[0]);
				}
			}
			else if(runMode)
				cachedFile.createNewFile();
		} catch (IOException ex){
			ex.printStackTrace();
		}
		Map<Integer, String> coverage = new HashMap<>();
		for(Integer id : sortedKeys){
			if(id <= beginId)
				continue;
			System.out.println("id " + id);
			Map<String, Object> textAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_text());
			Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_hypo());
			Pair substitutedPair = DIRTEngine.substitutePairExtendPhrase(textAnno, hypoAnno);
			coverage.put(id, DIRTEngine.getCoverage());
			substitutedPair.set_id(originPairs.get(id).get_id()).set_entailment(originPairs.get(id).get_entailment()).set_task(originPairs.get(id).get_task()).set_fromFile(originPairs.get(id).get_fromFile());
			String line = substitutedPair.get_id() + "|||" + substitutedPair.get_entailment() + "|||" + substitutedPair.get_task() + "|||" + substitutedPair.get_text() + "|||" + substitutedPair.get_hypo();
			substitutedPairs.put(id, substitutedPair);
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(cachedFile.getAbsoluteFile(), runMode));
				bw.write(line);
				bw.newLine();
				bw.flush();
				bw.close();
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		if(runMode){
			try {
				substitutedPairs.clear();
				BufferedReader br = new BufferedReader(new FileReader(cachedFile));
				String line;
				while ((line = br.readLine()) != null) {
					String[] splitted = line.split("\\|\\|\\|");
					Pair pairOfLine = new Pair(Integer.valueOf(splitted[0]), splitted[3], splitted[4], splitted[1], splitted[2]);
					substitutedPairs.put(Integer.valueOf(splitted[0]),pairOfLine);
				}
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		String destinationFilePath = currentPath.concat("/resources/output/dirt/xp_dirt_") + args[0];
		EnUtils.writePairs(destinationFilePath, substitutedPairs);
		String logFilePath = currentPath.concat("/resources/output/dirt/xp_dirt_") + args[0].substring(0,args[0].lastIndexOf(".")).concat(".log");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(logFilePath));
			sortedKeys = new ArrayList<>(coverage.keySet());
			Collections.sort(sortedKeys);
			for (Integer id : sortedKeys) {
				bw.write(coverage.get(id));
				bw.newLine();
			}
			bw.flush();
			bw.close();
			System.out.println("Write log result succesfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void executorExtendSynset(String[] args){
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EnFactory.loadDIRT();
		Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(args[0]));
		boolean runMode = (Integer.valueOf(args[1]) == 1)?true:false;
		Map<Integer, Pair> substitutedPairs = new HashMap<>();
		List<Integer> sortedKeys = new ArrayList<>(originPairs.keySet());
		Collections.sort(sortedKeys);
		File cachedFile = new File(currentPath.concat("/resources/output/dirt/cache/xs_").concat(args[0].substring(0,args[0].lastIndexOf("."))).concat(".txt"));
		int beginId = 0;
		try {
			if (runMode && cachedFile.exists()) {
				ReversedLinesFileReader rlfr = new ReversedLinesFileReader(cachedFile);
				String line = rlfr.readLine();
				if (line == null) {
					System.out.println(args[0] + ": Cache's empty. Let's start at beginning.");
					cachedFile.createNewFile();
				} else {
					System.out.println(args[0] + ": Cache's loaded.");
					beginId = Integer.valueOf(line.split("\\|\\|\\|")[0]);
				}
			}
			else if(runMode)
				cachedFile.createNewFile();
		} catch (IOException ex){
			ex.printStackTrace();
		}
		Map<Integer, String> coverage = new HashMap<>();
		for(Integer id : sortedKeys){
			if(id <= beginId)
				continue;
			System.out.println("id " + id);
			Map<String, Object> textAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_text());
			Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_hypo());
			Pair substitutedPair = DIRTEngine.substitutePairExtendSynset(textAnno, hypoAnno);
			coverage.put(id, DIRTEngine.getCoverage());

			substitutedPair.set_id(originPairs.get(id).get_id()).set_entailment(originPairs.get(id).get_entailment()).set_task(originPairs.get(id).get_task()).set_fromFile(originPairs.get(id).get_fromFile());
			String line = substitutedPair.get_id() + "|||" + substitutedPair.get_entailment() + "|||" + substitutedPair.get_task() + "|||" + substitutedPair.get_text() + "|||" + substitutedPair.get_hypo();
			substitutedPairs.put(id, substitutedPair);
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(cachedFile.getAbsoluteFile(), runMode));
				bw.write(line);
				bw.newLine();
				bw.flush();
				bw.close();
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		if(runMode){
			try {
				substitutedPairs.clear();
				BufferedReader br = new BufferedReader(new FileReader(cachedFile));
				String line;
				while ((line = br.readLine()) != null) {
					String[] splitted = line.split("\\|\\|\\|");
					Pair pairOfLine = new Pair(Integer.valueOf(splitted[0]), splitted[3], splitted[4], splitted[1], splitted[2]);
					substitutedPairs.put(Integer.valueOf(splitted[0]),pairOfLine);
				}
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		String destinationFilePath = currentPath.concat("/resources/output/dirt/xs_dirt_") + args[0];
		EnUtils.writePairs(destinationFilePath, substitutedPairs);
		String logFilePath = currentPath.concat("/resources/output/dirt/xs_dirt_") + args[0].substring(0,args[0].lastIndexOf(".")).concat(".log");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(logFilePath));
			sortedKeys = new ArrayList<>(coverage.keySet());
			Collections.sort(sortedKeys);
			for (Integer id : sortedKeys) {
				bw.write(coverage.get(id));
				bw.newLine();
			}
			bw.flush();
			bw.close();
			System.out.println("Write log result succesfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void executorExtendPhraseSynset(String[] args){
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EnFactory.loadDIRT();
		Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(args[0]));
		boolean runMode = (Integer.valueOf(args[1]) == 1)?true:false;
		Map<Integer, Pair> substitutedPairs = new HashMap<>();
		List<Integer> sortedKeys = new ArrayList<>(originPairs.keySet());
		Collections.sort(sortedKeys);
		File cachedFile = new File(currentPath.concat("/resources/output/dirt/cache/xps_").concat(args[0].substring(0,args[0].lastIndexOf("."))).concat(".txt"));
		int beginId = 0;
		try {
			if (runMode && cachedFile.exists()) {
				ReversedLinesFileReader rlfr = new ReversedLinesFileReader(cachedFile);
				String line = rlfr.readLine();
				if (line == null) {
					System.out.println(args[0] + ": Cache's empty. Let's start at beginning.");
					cachedFile.createNewFile();
				} else {
					System.out.println(args[0] + ": Cache's loaded.");
					beginId = Integer.valueOf(line.split("\\|\\|\\|")[0]);
				}
			}
			else if(runMode)
				cachedFile.createNewFile();
		} catch (IOException ex){
			ex.printStackTrace();
		}
		Map<Integer, String> coverage = new HashMap<>();
		for(Integer id : sortedKeys){
			if(id <= beginId)
				continue;
			System.out.println("id " + id);
			Map<String, Object> textAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_text());
			Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_hypo());
			Pair substitutedPair = DIRTEngine.substitutePairExtendPhraseSynset(textAnno, hypoAnno);
			coverage.put(id, DIRTEngine.getCoverage());
			substitutedPair.set_id(originPairs.get(id).get_id()).set_entailment(originPairs.get(id).get_entailment()).set_task(originPairs.get(id).get_task()).set_fromFile(originPairs.get(id).get_fromFile());
			String line = substitutedPair.get_id() + "|||" + substitutedPair.get_entailment() + "|||" + substitutedPair.get_task() + "|||" + substitutedPair.get_text() + "|||" + substitutedPair.get_hypo();
			substitutedPairs.put(id, substitutedPair);
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(cachedFile.getAbsoluteFile(), runMode));
				bw.write(line);
				bw.newLine();
				bw.flush();
				bw.close();
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		if(runMode){
			try {
				substitutedPairs.clear();
				BufferedReader br = new BufferedReader(new FileReader(cachedFile));
				String line;
				while ((line = br.readLine()) != null) {
					String[] splitted = line.split("\\|\\|\\|");
					Pair pairOfLine = new Pair(Integer.valueOf(splitted[0]), splitted[3], splitted[4], splitted[1], splitted[2]);
					substitutedPairs.put(Integer.valueOf(splitted[0]),pairOfLine);
				}
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		String destinationFilePath = currentPath.concat("/resources/output/dirt/xps_dirt_") + args[0];
		EnUtils.writePairs(destinationFilePath, substitutedPairs);
		String logFilePath = currentPath.concat("/resources/output/dirt/xps_dirt_") + args[0].substring(0,args[0].lastIndexOf(".")).concat(".log");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(logFilePath));
			sortedKeys = new ArrayList<>(coverage.keySet());
			Collections.sort(sortedKeys);
			for (Integer id : sortedKeys) {
				bw.write(coverage.get(id));
				bw.newLine();
			}
			bw.flush();
			bw.close();
			System.out.println("Write log result succesfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void executorSkippingNode(String[] args){
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EnFactory.loadDIRT();
		Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(args[0]));
		boolean runMode = (Integer.valueOf(args[1]) == 1)?true:false;
		Map<Integer, Pair> substitutedPairs = new HashMap<>();
		List<Integer> sortedKeys = new ArrayList<>(originPairs.keySet());
		Collections.sort(sortedKeys);
		File cachedFile = new File(currentPath.concat("/resources/output/dirt/cache/sn_").concat(args[0].substring(0,args[0].lastIndexOf("."))).concat(".txt"));
		int beginId = 0;
		try {
			if (runMode && cachedFile.exists()) {
				ReversedLinesFileReader rlfr = new ReversedLinesFileReader(cachedFile);
				String line = rlfr.readLine();
				if (line == null) {
					System.out.println(args[0] + ": Cache's empty. Let's start at beginning.");
					cachedFile.createNewFile();
				} else {
					System.out.println(args[0] + ": Cache's loaded.");
					beginId = Integer.valueOf(line.split("\\|\\|\\|")[0]);
				}
			}
			else if(runMode)
				cachedFile.createNewFile();
		} catch (IOException ex){
			ex.printStackTrace();
		}
		Map<Integer, String> coverage = new HashMap<>();
		for(Integer id : sortedKeys){
			if(id <= beginId)
				continue;
			System.out.println("id " + id);
			Map<String, Object> textAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_text());
			Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_hypo());
			Pair substitutedPair = DIRTEngine.substitutePairSkippingNode(textAnno, hypoAnno);
			coverage.put(id, DIRTEngine.getCoverage());
			substitutedPair.set_id(originPairs.get(id).get_id()).set_entailment(originPairs.get(id).get_entailment()).set_task(originPairs.get(id).get_task()).set_fromFile(originPairs.get(id).get_fromFile());
			String line = substitutedPair.get_id() + "|||" + substitutedPair.get_entailment() + "|||" + substitutedPair.get_task() + "|||" + substitutedPair.get_text() + "|||" + substitutedPair.get_hypo();
			substitutedPairs.put(id, substitutedPair);
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(cachedFile.getAbsoluteFile(), runMode));
				bw.write(line);
				bw.newLine();
				bw.flush();
				bw.close();
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		if(runMode){
			try {
				substitutedPairs.clear();
				BufferedReader br = new BufferedReader(new FileReader(cachedFile));
				String line;
				while ((line = br.readLine()) != null) {
					String[] splitted = line.split("\\|\\|\\|");
					Pair pairOfLine = new Pair(Integer.valueOf(splitted[0]), splitted[3], splitted[4], splitted[1], splitted[2]);
					substitutedPairs.put(Integer.valueOf(splitted[0]),pairOfLine);
				}
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		String destinationFilePath = currentPath.concat("/resources/output/dirt/sn_dirt_") + args[0];
		EnUtils.writePairs(destinationFilePath, substitutedPairs);
		String logFilePath = currentPath.concat("/resources/output/dirt/sn_dirt_") + args[0].substring(0,args[0].lastIndexOf(".")).concat(".log");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(logFilePath));
			sortedKeys = new ArrayList<>(coverage.keySet());
			Collections.sort(sortedKeys);
			for (Integer id : sortedKeys) {
				bw.write(coverage.get(id));
				bw.newLine();
			}
			bw.flush();
			bw.close();
			System.out.println("Write log result succesfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static void executorSkippingNodeExtendPhrase(String[] args){
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EnFactory.loadDIRT();
		Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(args[0]));
		boolean runMode = (Integer.valueOf(args[1]) == 1)?true:false;
		Map<Integer, Pair> substitutedPairs = new HashMap<>();
		List<Integer> sortedKeys = new ArrayList<>(originPairs.keySet());
		Collections.sort(sortedKeys);
		File cachedFile = new File(currentPath.concat("/resources/output/dirt/cache/sn_xp_").concat(args[0].substring(0,args[0].lastIndexOf("."))).concat(".txt"));
		int beginId = 0;
		try {
			if (runMode && cachedFile.exists()) {
				ReversedLinesFileReader rlfr = new ReversedLinesFileReader(cachedFile);
				String line = rlfr.readLine();
				if (line == null) {
					System.out.println(args[0] + ": Cache's empty. Let's start at beginning.");
					cachedFile.createNewFile();
				} else {
					System.out.println(args[0] + ": Cache's loaded.");
					beginId = Integer.valueOf(line.split("\\|\\|\\|")[0]);
				}
			}
			else if(runMode)
				cachedFile.createNewFile();
		} catch (IOException ex){
			ex.printStackTrace();
		}
		Map<Integer, String> coverage = new HashMap<>();
		for(Integer id : sortedKeys){
			if(id <= beginId)
				continue;
			System.out.println("id " + id);
			Map<String, Object> textAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_text());
			Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_hypo());
			Pair substitutedPair = DIRTEngine.substitutePairSkippingNodeExtendPhrase(textAnno, hypoAnno);
			coverage.put(id, DIRTEngine.getCoverage());
			substitutedPair.set_id(originPairs.get(id).get_id()).set_entailment(originPairs.get(id).get_entailment()).set_task(originPairs.get(id).get_task()).set_fromFile(originPairs.get(id).get_fromFile());
			String line = substitutedPair.get_id() + "|||" + substitutedPair.get_entailment() + "|||" + substitutedPair.get_task() + "|||" + substitutedPair.get_text() + "|||" + substitutedPair.get_hypo();
			substitutedPairs.put(id, substitutedPair);
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(cachedFile.getAbsoluteFile(), runMode));
				bw.write(line);
				bw.newLine();
				bw.flush();
				bw.close();
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		if(runMode){
			try {
				substitutedPairs.clear();
				BufferedReader br = new BufferedReader(new FileReader(cachedFile));
				String line;
				while ((line = br.readLine()) != null) {
					String[] splitted = line.split("\\|\\|\\|");
					Pair pairOfLine = new Pair(Integer.valueOf(splitted[0]), splitted[3], splitted[4], splitted[1], splitted[2]);
					substitutedPairs.put(Integer.valueOf(splitted[0]),pairOfLine);
				}
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		String destinationFilePath = currentPath.concat("/resources/output/dirt/sn_xp_dirt_") + args[0];
		EnUtils.writePairs(destinationFilePath, substitutedPairs);
		String logFilePath = currentPath.concat("/resources/output/dirt/sn_xp_dirt_") + args[0].substring(0,args[0].lastIndexOf(".")).concat(".log");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(logFilePath));
			sortedKeys = new ArrayList<>(coverage.keySet());
			Collections.sort(sortedKeys);
			for (Integer id : sortedKeys) {
				bw.write(coverage.get(id));
				bw.newLine();
			}
			bw.flush();
			bw.close();
			System.out.println("Write log result succesfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static void executorSkippingNodeExtendSynset(String[] args){
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EnFactory.loadDIRT();
		Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(args[0]));
		boolean runMode = (Integer.valueOf(args[1]) == 1)?true:false;
		Map<Integer, Pair> substitutedPairs = new HashMap<>();
		List<Integer> sortedKeys = new ArrayList<>(originPairs.keySet());
		Collections.sort(sortedKeys);
		File cachedFile = new File(currentPath.concat("/resources/output/dirt/cache/sn_xs_").concat(args[0].substring(0,args[0].lastIndexOf("."))).concat(".txt"));
		int beginId = 0;
		try {
			if (runMode && cachedFile.exists()) {
				ReversedLinesFileReader rlfr = new ReversedLinesFileReader(cachedFile);
				String line = rlfr.readLine();
				if (line == null) {
					System.out.println(args[0] + ": Cache's empty. Let's start at beginning.");
					cachedFile.createNewFile();
				} else {
					System.out.println(args[0] + ": Cache's loaded.");
					beginId = Integer.valueOf(line.split("\\|\\|\\|")[0]);
				}
			}
			else if(runMode)
				cachedFile.createNewFile();
		} catch (IOException ex){
			ex.printStackTrace();
		}
		Map<Integer, String> coverage = new HashMap<>();
		for(Integer id : sortedKeys){
			if(id <= beginId)
				continue;
			System.out.println("id " + id);
			Map<String, Object> textAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_text());
			Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_hypo());
			Pair substitutedPair = DIRTEngine.substitutePairSkippingNodeExtendSynset(textAnno, hypoAnno);
			coverage.put(id, DIRTEngine.getCoverage());
			substitutedPair.set_id(originPairs.get(id).get_id()).set_entailment(originPairs.get(id).get_entailment()).set_task(originPairs.get(id).get_task()).set_fromFile(originPairs.get(id).get_fromFile());
			String line = substitutedPair.get_id() + "|||" + substitutedPair.get_entailment() + "|||" + substitutedPair.get_task() + "|||" + substitutedPair.get_text() + "|||" + substitutedPair.get_hypo();
			substitutedPairs.put(id, substitutedPair);
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(cachedFile.getAbsoluteFile(), runMode));
				bw.write(line);
				bw.newLine();
				bw.flush();
				bw.close();
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		if(runMode){
			try {
				substitutedPairs.clear();
				BufferedReader br = new BufferedReader(new FileReader(cachedFile));
				String line;
				while ((line = br.readLine()) != null) {
					String[] splitted = line.split("\\|\\|\\|");
					Pair pairOfLine = new Pair(Integer.valueOf(splitted[0]), splitted[3], splitted[4], splitted[1], splitted[2]);
					substitutedPairs.put(Integer.valueOf(splitted[0]),pairOfLine);
				}
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		String destinationFilePath = currentPath.concat("/resources/output/dirt/sn_xs_dirt_") + args[0];
		EnUtils.writePairs(destinationFilePath, substitutedPairs);
		String logFilePath = currentPath.concat("/resources/output/dirt/sn_xs_dirt_") + args[0].substring(0,args[0].lastIndexOf(".")).concat(".log");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(logFilePath));
			sortedKeys = new ArrayList<>(coverage.keySet());
			Collections.sort(sortedKeys);
			for (Integer id : sortedKeys) {
				bw.write(coverage.get(id));
				bw.newLine();
			}
			bw.flush();
			bw.close();
			System.out.println("Write log result succesfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static void executorSkippingNodeExtendPhraseSynset(String[] args){
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EnFactory.loadDIRT();
		Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(args[0]));
		boolean runMode = (Integer.valueOf(args[1]) == 1)?true:false;
		Map<Integer, Pair> substitutedPairs = new HashMap<>();
		List<Integer> sortedKeys = new ArrayList<>(originPairs.keySet());
		Collections.sort(sortedKeys);
		File cachedFile = new File(currentPath.concat("/resources/output/dirt/cache/sn_xps_").concat(args[0].substring(0,args[0].lastIndexOf("."))).concat(".txt"));
		int beginId = 0;
		try {
			if (runMode && cachedFile.exists()) {
				ReversedLinesFileReader rlfr = new ReversedLinesFileReader(cachedFile);
				String line = rlfr.readLine();
				if (line == null) {
					System.out.println(args[0] + ": Cache's empty. Let's start at beginning.");
					cachedFile.createNewFile();
				} else {
					System.out.println(args[0] + ": Cache's loaded.");
					beginId = Integer.valueOf(line.split("\\|\\|\\|")[0]);
				}
			}
			else if(runMode)
				cachedFile.createNewFile();
		} catch (IOException ex){
			ex.printStackTrace();
		}
		Map<Integer, String> coverage = new HashMap<>();
		for(Integer id : sortedKeys){
			if(id <= beginId)
				continue;
			System.out.println("id " + id);
			Map<String, Object> textAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_text());
			Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_hypo());
			Pair substitutedPair = DIRTEngine.substitutePairSkippingNodeExtendPhraseSynset(textAnno, hypoAnno);
			coverage.put(id, DIRTEngine.getCoverage());
			substitutedPair.set_id(originPairs.get(id).get_id()).set_entailment(originPairs.get(id).get_entailment()).set_task(originPairs.get(id).get_task()).set_fromFile(originPairs.get(id).get_fromFile());
			String line = substitutedPair.get_id() + "|||" + substitutedPair.get_entailment() + "|||" + substitutedPair.get_task() + "|||" + substitutedPair.get_text() + "|||" + substitutedPair.get_hypo();
			substitutedPairs.put(id, substitutedPair);
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(cachedFile.getAbsoluteFile(), runMode));
				bw.write(line);
				bw.newLine();
				bw.flush();
				bw.close();
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		if(runMode){
			try {
				substitutedPairs.clear();
				BufferedReader br = new BufferedReader(new FileReader(cachedFile));
				String line;
				while ((line = br.readLine()) != null) {
					String[] splitted = line.split("\\|\\|\\|");
					Pair pairOfLine = new Pair(Integer.valueOf(splitted[0]), splitted[3], splitted[4], splitted[1], splitted[2]);
					substitutedPairs.put(Integer.valueOf(splitted[0]),pairOfLine);
				}
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		String destinationFilePath = currentPath.concat("/resources/output/dirt/sn_xps_dirt_") + args[0];
		EnUtils.writePairs(destinationFilePath, substitutedPairs);
		String logFilePath = currentPath.concat("/resources/output/dirt/sn_xps_dirt_") + args[0].substring(0,args[0].lastIndexOf(".")).concat(".log");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(logFilePath));
			sortedKeys = new ArrayList<>(coverage.keySet());
			Collections.sort(sortedKeys);
			for (Integer id : sortedKeys) {
				bw.write(coverage.get(id));
				bw.newLine();
			}
			bw.flush();
			bw.close();
			System.out.println("Write log result succesfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
//		if(args.length != 3)
//			System.err.println("USAGE: fileName runmMode chosenMode //RTE2_dev.xml 1 0");
		//Integer mode = Integer.valueOf(args[args.length-1]);
		List<String> files = Arrays.asList("RTE2_dev.xml","RTE2_test.xml","RTE3_dev.xml","RTE3_test.xml","RTE4_test.xml","RTE5_dev.xml","RTE5_test.xml");
		List<Integer> modes = Arrays.asList(2,3,6,7);
		for(String file : files)
			for(Integer mode : modes){
				String[] arguments = new String[]{file, "0", String.valueOf(mode)};
				switch (mode){
					case 0: executor(arguments);
						break;
					case 1: executorExtendPhrase(arguments);
						break;
					case 2: executorExtendSynset(arguments);
						break;
					case 3: executorExtendPhraseSynset(arguments);
						break;
					case 4: executorSkippingNode(arguments);
						break;
					case 5: executorSkippingNodeExtendPhrase(arguments);
						break;
					case 6: executorSkippingNodeExtendSynset(arguments);
						break;
					case 7: executorSkippingNodeExtendPhraseSynset(arguments);
						break;
					default: executor(arguments);
				}
			}
		System.err.println("Exit with code = 0");
	}
}
