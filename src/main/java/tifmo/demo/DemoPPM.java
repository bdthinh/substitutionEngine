package tifmo.demo;

import tifmo.allEngine.PPEngine;
import tifmo.en.EnFactory;
import tifmo.en.EnPPDB;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bdthinh on 12/12/14.
 */
public class DemoPPM {
	public static void main(String[] args) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//args[0]:name of PPDB

		String sourcePath = currentPath.concat("/resources/").concat(args[0]);
		String destinationPath = currentPath.concat("/resources/cdb/").concat(args[0].substring(0,args[0].lastIndexOf(".")).concat(".cdb"));
		EnPPDB.loadCDBFromPPDB(sourcePath, destinationPath);
		EnFactory.loadMikolov();
		EnFactory.loadTurian();
		//args[1]: threshold for PPEngine
		PPEngine.set_sgramThres(Double.valueOf(args[1]));
		PPEngine.set_lcsThres(Double.valueOf(args[1]));

		//List<String> files = Arrays.asList("test.xml","test2.xml","test3.xml");
		List<String> files = Arrays.asList("RTE2_dev.xml","RTE2_test.xml","RTE3_dev.xml","RTE3_test.xml","RTE4_test.xml","RTE5_dev.xml","RTE5_test.xml");
		for(String file : files) {
			RunnablePPThread rd = new RunnablePPThread(file, true, false);
			rd.start();
		}

	}
}
