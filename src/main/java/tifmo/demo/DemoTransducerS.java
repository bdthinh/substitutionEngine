package tifmo.demo;

import tifmo.allEngine.TransducerEngine;
import tifmo.coreNLP.Pair;
import tifmo.en.EnFactory;
import tifmo.utils.EnUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by bdthinh on 12/18/14.
 */
public class DemoTransducerS {
	public static void main(String[] args) {
		if (args.length < 1)
			System.out.println("USAGE: entailmentPair");
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EnFactory.loadTransducer();
		String entaimentFile = currentPath.concat("/resources/dict/transducerDict/").concat(args[0]);
		if ((new File(entaimentFile)).exists()) {
			Map<Integer, Pair> substitutedPairs = TransducerEngine.substitutePairs(entaimentFile);
			String destinationFilePath = currentPath.concat("/resources/output/transducer/transducer_") + args[0];
			EnUtils.writePairs(destinationFilePath, substitutedPairs);
		}
	}
}
