package tifmo.demo;

import tifmo.allEngine.DIRTEngine;
import tifmo.allEngine.PPEngine;
import tifmo.coreNLP.*;
import tifmo.en.*;
import tifmo.utils.EnUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bdthinh on 12/13/14.
 */
public class Demo {

	public static void main(String[] args) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String sourcePath = currentPath.concat("/resources/").concat(args[0]);
		String destinationPath = currentPath.concat("/resources/cdb/").concat(args[0].substring(0, args[0].lastIndexOf(".")).concat(".cdb"));
		EnPPDB.loadCDBFromPPDB(sourcePath, destinationPath);
		EnFactory.loadMikolov();
		EnFactory.loadGlove();
		if (!EnWordNet.isOpen())
			EnWordNet.open();
//		System.out.println(EnResourceWN.isMeronym("Japan", "Tokyo"));
//		System.out.println(EnResourceWN.isMeronym("Tokyo", "Japan"));
//		System.out.println(EnResourceWN.isMeronym("United Kingdom", "Europe"));
//		System.out.println(EnResourceWN.isMeronym("Europe", "United Kingdom"));
//		String text = "New US Secretary of State Condoleezza Rice says attacking Iran is not on the US agenda \"at this point in time\".";
//		String hypo = "Rice defends Bush.";
//		Pair pair = new Pair(text, hypo);
//		List<String> chosens = Arrays.asList("mikolov");
//		PPEngine.substituteCorefFirstLongestPP(pair,chosens);
//
//		PPEngine.getTendencyOfPair(pair, Parser.parseTextToAnnotation(text), Parser.parseTextToAnnotation(hypo));
		List<String> files = Arrays.asList("msrp_test.txt", "msrp_train.txt");
		for(int i = 0 ; i < files.size() ; i++){

		}
//		for (int i = 0; i < files.size(); i++) {
//			Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(files.get(i)));
//			Map<Integer, String> ret = PPEngine.getTendency(originPairs);
//			String logFilePath = currentPath.concat("/resources/output/ne/log_") + files.get(i).substring(0, files.get(i).lastIndexOf(".")).concat(".txt");
//			try {
//				BufferedWriter bw = new BufferedWriter(new FileWriter(logFilePath));
//				List<Integer> sortedKeys = new ArrayList<>(ret.keySet());
//				Collections.sort(sortedKeys);
//				for (Integer id : sortedKeys) {
//					bw.write(id + ", " + ret.get(id));
//					bw.newLine();
//				}
//				bw.flush();
//				bw.close();
//				System.out.println("Write log result succesfully!");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		System.err.println("Exit code = 0");
	}
}
