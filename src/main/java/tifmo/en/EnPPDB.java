package tifmo.en;

import com.strangegizmo.cdb.Cdb;
import com.strangegizmo.cdb.CdbMake;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;
import tifmo.coreNLP.Chunk;
import tifmo.allEngine.FeatureSet;
import tifmo.allEngine.PosFeature;
import tifmo.allEngine.Paraphrase;
import tifmo.utils.EnMikolovSimilarity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bdthinh on 10/28/14.
 */
public class EnPPDB {
	private static File _sourceFile;
	private static String _sourcePath;
	private static String[] _destinationPath;

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
		loadCDBFromPPDB(sourcePath, destinationPath);
	}

	public static boolean checkCDBLoaded(){
		if(_destinationPath == null)
			return false;
		return true;
	}
	public static void loadCDBFromPPDB(String sourcePath, String destinationPath){
		//abcdef
		//ghijklm
		//nopquv
		//rstwxyz
		//[0-9]
		//other

		_sourcePath = sourcePath;
		_destinationPath = new String[6];
		for(int i = 0 ; i < _destinationPath.length ; i++)
			_destinationPath[i] = destinationPath.substring(0,destinationPath.lastIndexOf(".cdb"))+ "_" + i + ".cdb";
		_sourceFile = new File(sourcePath);
		File destination = new File(destinationPath.substring(0,destinationPath.lastIndexOf(".cdb"))+"_0.cdb");
		if(destination.exists())
			return;
		CdbMake[] makers = new CdbMake[6];
		try {
			for(int i = 0 ; i < makers.length ; i++){
				makers[i] = new CdbMake();
				makers[i].start(_destinationPath[i]);
			}
			CompressorInputStream cis = new CompressorStreamFactory().createCompressorInputStream(
							new BufferedInputStream(new FileInputStream(_sourceFile)));
			BufferedReader br = new BufferedReader(new InputStreamReader(cis));
			String line = "";
			//count
			int count = 0;
			while ((line = br.readLine()) != null) {
				String[] splitted = line.split("\\|\\|\\|");

				String tag = splitted[0].trim();
				String source = splitted[1].trim();
				String target = splitted[2].trim();
				String features = splitted[3].trim();
				String alignment = splitted[4].trim();
				if(source.equals(target))
					continue;
				String key = source; // for storing
				//filterLongestPP
				if(source.toCharArray().length < 1) {
					System.out.println("NULL source ID: " + count + " target: " + target);
					continue;
				}
				if(tag.contains("-LRB") || tag.contains("-RRB-")
								|| tag.contains("[S]") || tag.contains("FRAG")
								|| tag.contains("SBARQ") || tag.contains("SQ")
								|| tag.matches("\\[S\\\\.*\\]") || tag.matches("\\[S/.*\\]")
								|| tag.matches("\\[.*/S\\]") || tag.matches("\\[.*\\\\S\\]")
								|| source.contains("-lrb-") || source.contains("-rrb-")
								|| target.contains("-lrb-") || target.contains("-rrb-")
								|| target.matches(".*\\[.*\\\\.*\\].*") || target.matches(".*\\[.*/.*\\].*"))
					continue;
				//end filterLongestPP

				String[] feature = features.split(" ");
				int featureSize = feature.length;
				String gigaSim = "";
				String ngramSim = "";
				if(featureSize == 33) {
					gigaSim = feature[featureSize - 2].split("=")[1];
					ngramSim = feature[featureSize - 1].split("=")[1];
				}
				else{
					gigaSim = "-1";
					ngramSim = "-1";
				}

				String value = target +"||"+ tag +"||"+ alignment + "||" + gigaSim + " " + ngramSim;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				baos.write(value.getBytes("UTF-8"));
				baos.flush();
				baos.close();

				//this part is just temporal
				if(source.toCharArray().length < 1) {
					System.out.println("NULL source ID: " + count + " target: " + target);
					continue;
				}
				//abcdef
				//ghijklm
				//nopquv
				//rstwxyz
				//other
				String matcher = source.toLowerCase();
				if(matcher.matches("^[a-z].*"))
					if(matcher.matches("^[a-f].*"))
						makers[0].add(key.getBytes("UTF-8"), baos.toByteArray());
					else if (matcher.matches("^[g-m].*"))
						makers[1].add(key.getBytes("UTF-8"), baos.toByteArray());
					else if (matcher.matches("^[n-q].*"))
						makers[2].add(key.getBytes("UTF-8"), baos.toByteArray());
					else
						makers[3].add(key.getBytes("UTF-8"), baos.toByteArray());
				else if(matcher.matches("^[0-9].*") || (matcher.matches("^('s).*") && tag.contains("POS")) )
					makers[4].add(key.getBytes("UTF-8"), baos.toByteArray());
				else if(matcher.matches("^\\[.*")){
					makers[5].add(key.getBytes("UTF-8"), baos.toByteArray());
				}
				if ((++count) % 50000 == 0)
					System.out.println(count);
			}
			for(int i = 0 ; i < makers.length; i++)
				makers[i].finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Cdb chooseItSelf(String key){
		Cdb chosenCdb;
		String matcher = key.toLowerCase();
		try {
			if(matcher.matches("^[a-z].*"))
				if(matcher.matches("^[a-f].*"))
					chosenCdb = new Cdb(_destinationPath[0]);
				else if (matcher.matches("^[g-m].*"))
					chosenCdb = new Cdb(_destinationPath[1]);
				else if (matcher.matches("^[n-q].*"))
					chosenCdb = new Cdb(_destinationPath[2]);
				else
					chosenCdb = new Cdb(_destinationPath[3]);
			else if(matcher.matches("^[0-9].*") || (matcher.matches("^('s).*")))
				chosenCdb = new Cdb(_destinationPath[4]);
			else
				chosenCdb = new Cdb(_destinationPath[5]);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return chosenCdb;
	}

	public static Paraphrase lookUp (Chunk keyChunk) {
		//key can be word / phrase <lookUpExactly>
		List<FeatureSet> values = new ArrayList<FeatureSet>();
		String key = keyChunk.get_chunk();
		Cdb usingCdb = chooseItSelf(key);
		if(usingCdb == null)
			return new Paraphrase(key,values);
		try {
			byte[] byteTarget;
			while ((byteTarget = usingCdb.findnext(key.getBytes("UTF-8"))) != null) {
				ByteArrayInputStream bais = new ByteArrayInputStream(byteTarget);
				String line = IOUtils.toString(bais,"UTF-8");
				String[] parts = line.split("\\|\\|");
				//0: target
				//1: pos
				//2: alignment
				//3: gigaSim
				//4: ngramSim
				FeatureSet value = new FeatureSet(parts[0], new PosFeature(parts[1]), parts[2]);
				value.set_giga(Double.valueOf(parts[3].split(" ")[0]));
				value.set_ngram(Double.valueOf(parts[3].split(" ")[1]));
				values.add(value);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		usingCdb.close();
		return new Paraphrase(key, values);
	}

	public static Paraphrase lookUp (String key) {
		//key can be word / phrase <lookUpExactly>
		List<FeatureSet> values = new ArrayList<FeatureSet>();
		Cdb usingCdb = chooseItSelf(key);
		if(usingCdb == null)
			return new Paraphrase(key,values);
		try {
			byte[] byteTarget;
			while ((byteTarget = usingCdb.findnext(key.getBytes("UTF-8"))) != null) {
				ByteArrayInputStream bais = new ByteArrayInputStream(byteTarget);
				String line = IOUtils.toString(bais,"UTF-8");
				String[] parts = line.split("\\|\\|");
				//0: target
				//1: pos
				//2: alignment
				//3: gigaSim
				//4: ngramSim
				FeatureSet value = new FeatureSet(parts[0], new PosFeature(parts[1]), parts[2]);
				value.set_giga(Double.valueOf(parts[3].split(" ")[0]));
				value.set_ngram(Double.valueOf(parts[3].split(" ")[1]));
				values.add(value);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		usingCdb.close();
		return new Paraphrase(key, values);
	}

	public static Paraphrase lookUpChunkWithPos(Chunk ck, String pos){
		String key = ck.get_chunk();
		List<FeatureSet> values = new ArrayList<FeatureSet>();
		Cdb usingCdb = chooseItSelf(key);
		if(usingCdb == null)
			return new Paraphrase(key,values);
		try {
			byte[] byteTarget;
			while ((byteTarget = usingCdb.findnext(key.getBytes("UTF-8"))) != null) {
				InputStream bais = new ByteArrayInputStream(byteTarget);
				String line = IOUtils.toString(bais,"UTF-8");
				String[] parts = line.split("\\|\\|");
				//0: target
				//1: pos
				//2: alignment
				//3: gigaSim
				//4: ngramSim
				PosFeature check = new PosFeature(parts[1]);
				if(pos.toLowerCase().equals(check.get_pos().toLowerCase())) {
					FeatureSet value = new FeatureSet(parts[0], new PosFeature(parts[1]), parts[2]);
					value.set_giga(Double.valueOf(parts[3].split(" ")[0]));
					value.set_ngram(Double.valueOf(parts[3].split(" ")[1]));
					values.add(value);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		usingCdb.close();
		return new Paraphrase(key, values);

	}
	public static Paraphrase lookUpAtNDepthLevel(String key, int level){
		Paraphrase pp = lookUp(key);
		try {
			for (int i = 1; i < level; i++) {
				List<FeatureSet> lfs = pp.get_featureSets();
				int length = lfs.size();
				for (int j = 0; j < length; j++) {
					List<FeatureSet> lfsNextLevel = lookUp(lfs.get(j).get_target()).get_featureSets();
					lfs.remove(j);
					length--;
					j--;
					for (FeatureSet eachFS : lfsNextLevel)
						lfs.add(eachFS);
				}
				pp.set_featureSets(lfs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pp;
	}
}
