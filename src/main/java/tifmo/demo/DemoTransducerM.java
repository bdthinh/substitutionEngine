package tifmo.demo;

import tifmo.en.EnFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bdthinh on 12/18/14.
 */
public class DemoTransducerM {
	public static void main(String[] args) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//args[0]:name of PPDB
		EnFactory.loadTransducer();
		List<String> files = Arrays.asList("RTE2_dev.txt", "RTE2_test.txt", "RTE3_dev.txt", "RTE3_test.txt", "RTE4_test.txt", "RTE5_dev.txt", "RTE5_test.txt");
		for (String file : files) {
			RunnableTransducerThread rd = new RunnableTransducerThread(file);
			rd.start();
		}
	}
}
