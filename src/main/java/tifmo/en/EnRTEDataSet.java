package tifmo.en;

import com.strangegizmo.cdb.Cdb;
import com.strangegizmo.cdb.CdbMake;

import tifmo.coreNLP.Pair;
import tifmo.coreNLP.Parser;
import tifmo.utils.EnUtils;

import java.io.*;
import java.util.*;

/**
 * Created by bdthinh on 11/22/14.
 */
public class EnRTEDataSet {
	private static String[] _destinationPath;
	private static CdbMake _maker;
	public static void main(String[] args) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String prefixPath = currentPath.concat("/resources/cdb/");


		List<String> files = new ArrayList<>(Arrays.asList("RTE2_dev", "RTE2_test", "RTE3_dev", "RTE3_test","RTE4_test","RTE5_dev","RTE5_test"));
		_destinationPath = new String[files.size()];
		try {
		for(int i = 0 ; i<files.size();i++) {
			_maker = new CdbMake();
			_maker.start(prefixPath.concat(files.get(i)).concat(".cdb"));
			_destinationPath[i] = prefixPath.concat(files.get(i)).concat(".cdb");
			Map<Integer, Pair> pairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(files.get(i)).concat(".xml"));
			for (Integer id : pairs.keySet()) {
				Pair pair = pairs.get(id);
				AddToRTECdb(pair.get_text(),prefixPath);
				AddToRTECdb(pair.get_hypo(),prefixPath);
				System.out.println(id);
			}
		}
		_maker.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void AddToRTECdb (String text, String destinationPath){
		try {
//			if(lookUp(text).size() != 0) {
//				System.out.println("found");
//				return;
//			}
			Map<String, Object> annotation = Parser.parseTextToAnnotation(text);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(annotation);
			oos.close();
			_maker.add(text.getBytes("UTF-8"), baos.toByteArray());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static Map<String,Object> lookUp (String textKey, int datasetNumber){
		Map<String, Object> ret = new HashMap<>();
		if(!new File(_destinationPath[datasetNumber]).exists())
			return ret;
		else{
			try {
				Cdb usingCdb = new Cdb(_destinationPath[datasetNumber]);
				if(usingCdb != null){
					byte[] byteTarget = usingCdb.find(textKey.getBytes("UTF-8"));
					if(byteTarget != null){
						ByteArrayInputStream bais = new ByteArrayInputStream(byteTarget);
						ObjectInputStream ois = new ObjectInputStream(bais);
						ret = (Map<String,Object>)ois.readObject();
					}
					usingCdb.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
}
