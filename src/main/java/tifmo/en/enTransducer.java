package tifmo.en;

import com.strangegizmo.cdb.Cdb;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import tifmo.coreNLP.Pair;
import tifmo.coreNLP.TreePair;
import tifmo.utils.EnMikolovSimilarity;
import tifmo.utils.EnUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by bdthinh on 12/7/14.
 */
public class EnTransducer {
	private static String _relativeSourPath = "/resources/ppdb-1.0-xl-all.gz";
	private static String _relativeDestPath = "/resources/dict/transducerDict/";

	public static Cdb get_cdb() {
		return _cdb;
	}

	public static void set_cdb(Cdb _cdb) {
		EnTransducer._cdb = _cdb;
	}

	public static String get_destinationPath() {
		return _destinationPath;
	}

	public static void set_destinationPath(String _destinationPath) {
		EnTransducer._destinationPath = _destinationPath;
	}

	private static Cdb _cdb;
	private static String _destinationPath;
	public static void main(String[] args) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//resourcePath
		String sourcePath = currentPath.concat(_relativeSourPath);
		String destinationPath = currentPath.concat(_relativeDestPath);
		List<String> files = Arrays.asList("RTE2_dev.xml","RTE2_test.xml","RTE3_dev.xml","RTE3_test.xml","RTE4_test.xml","RTE5_dev.xml","RTE5_test.xml");
		//writeDictionaryFromPPDB(currentPath.concat(_relativeSourPath), currentPath.concat(_relativeDestPath).concat("paraphrase.db"));
		//writeEntailmentPairToFiles(files, currentPath.concat(_relativeDestPath));
	}
	public static void init(){
		if (_cdb != null)
			return;
		try {
			_destinationPath = new File("").getCanonicalPath().concat(_relativeDestPath).concat("paraphraseDB.cdb");
			if (new File(_destinationPath).exists()) {
				System.out.println("Load Transducer Paraphrase Cdb...");
				_cdb = new Cdb(_destinationPath);
			} else {
				System.out.println("Build Transducer Cdb from pack...");
				loadCDBFromTransducerPPDB(new File("").getCanonicalPath().concat("paraphrase.db"), _destinationPath);
				System.out.println("Load Transducer Cdb...");
				_cdb = new Cdb(_destinationPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void writeEntailmentPairToFiles(List<String> files, String destinationPath){
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(String file : files){
			String filePath = destinationPath.concat(file.substring(0, file.lastIndexOf(".")).concat(".txt"));
			if(!(new File(filePath)).exists()) {
				Map<Integer, Pair> pairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(file));
				Map<Integer, TreePair> treePairs = TreePair.getTreePairs(pairs);
				TreePair.WriteEntailmentFile(filePath, treePairs);
			}
		}
	}
	public static void writeDictionaryFromPPDB(String sourcePath, String destinationPath){
		File destFile = new File(destinationPath);
		if(destFile.exists())
			return;
		try {
			CompressorInputStream cis = new CompressorStreamFactory().createCompressorInputStream(
							new BufferedInputStream(new FileInputStream(sourcePath)));
			BufferedReader br = new BufferedReader(new InputStreamReader(cis));
			EnMikolovSimilarity mikolovSim = new EnMikolovSimilarity();
			String line = "";
			int count = 0;
			List<String> linesToFlush = new ArrayList<>();
			while ((line = br.readLine()) != null) {
				String[] splitted = line.split("\\|\\|\\|");
				String tag = splitted[0].trim();
				String source = splitted[1].trim();
				String target = splitted[2].trim();
				//filterLongestPP
				if(source.toCharArray().length < 1) {
					//System.out.println("...remove "+ source + " ||| " + target);
					continue;
				}
				if(tag.contains("-LRB") || tag.contains("-RRB-")
								|| tag.contains("[S]")
								|| tag.matches("\\[S\\\\.*\\]") || tag.matches("\\[S/.*\\]")
								|| tag.matches("\\[.*/S\\]") || tag.matches("\\[.*\\\\S\\]")
								|| source.contains("[") || source.contains("]")
								|| target.contains("[") || target.contains("]")) {
					//System.out.println("...remove "+ source + " ||| " + target);
					continue;
				}
				//end filterLongestPP
				double similarity = 1.0 - (mikolovSim.getSimilarity(source, target));
				if(similarity == 2.0)
					similarity = 0.0;
				String retValue = source + " ||| " + target + " ||| " + similarity;
				linesToFlush.add(retValue);
				if ((++count) % 50000 == 0) {
					File file = new File(destinationPath);
					BufferedWriter bw;
					if(file.exists())
						bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(),true));
					else
						bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
					for(String lineToFlush : linesToFlush){
						bw.write(lineToFlush);
						bw.newLine();
					}
					bw.flush();
					bw.close();
					linesToFlush.clear();
					System.out.println(count + " lines are flushed to paraphrase.db");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void loadCDBFromTransducerPPDB(String sourcePath, String destinationPath){
		if((new File(destinationPath)).exists())
			return;
	}

}
