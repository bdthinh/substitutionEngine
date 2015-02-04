package tifmo.demo;

import java.util.Arrays;
import java.util.List;

/**
 * Created by bdthinh on 12/15/14.
 */
public class DemoDirtM {
	public static void main(String[] args) {
		List<String> files = Arrays.asList("RTE2_dev.xml", "RTE2_test.xml", "RTE3_dev.xml", "RTE3_test.xml", "RTE4_test.xml", "RTE5_dev.xml", "RTE5_test.xml");
		for(String file : files) {
			RunnableDIRTThread rd = new RunnableDIRTThread(file);
			rd.start();
		}
	}
}
