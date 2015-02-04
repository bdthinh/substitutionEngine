package tifmo.demo;

import tifmo.coreNLP.Pair;
import tifmo.allEngine.PPEngine;
import tifmo.en.EnFactory;
import tifmo.en.EnPPDB;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bdthinh on 10/22/14.
 */
public class DemoCheck {

	public static void main(String[] args) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//resourcePath
		String ppdbPath = currentPath.concat("/resources/").concat(args[0]);
		String mikolovPath = currentPath.concat("/resources/cdb/Mikolov13.cdb");
		String turianPath = currentPath.concat("/resources/cdb/Turian10.cdb");
		String datasetPath = currentPath.concat("/resources/input/");
		System.out.println("Path to PPDB: "+ ppdbPath);
		if((new File(ppdbPath)).exists())
			System.out.println("PPDB exists");
		System.out.println("Path to Mikolov: "+ mikolovPath);
		if((new File(mikolovPath)).exists())
			System.out.println("Mikolov exists");
		else{
			if((new File(currentPath.concat("/resources/").concat("Mikolov13-GoogleNews-vectors-negative300.txt.gz"))).exists())
				System.out.printf("Dont worry. System will create it");
			else
				System.out.println("Check Mikolov data out");
		}
		System.out.println("Path to Turian: "+ turianPath);
		if((new File(turianPath)).exists())
			System.out.println("Turian exists");
		else{
			if((new File(currentPath.concat("/resources/").concat("Turian10-embeddings-scaled.EMBEDDING_SIZE=50.txt.gz"))).exists())
				System.out.printf("Dont worry. System will create it");
			else
				System.out.println("Check Turian data out");
		}
		List<String> files = Arrays.asList("RTE2_dev.xml","RTE2_test.xml","RTE3_dev.xml","RTE3_test.xml","RTE4_test.xml","RTE5_dev.xml","RTE5_test.xml");
		for(String file : files){
			if(!(new File(datasetPath.concat(file))).exists())
				System.out.println(file+" is missed. Check it out");
		}

	}

}
