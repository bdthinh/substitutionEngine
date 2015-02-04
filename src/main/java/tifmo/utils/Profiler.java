package tifmo.utils;

import java.lang.reflect.Method;

/**
 * Created by bdthinh on 10/21/14.
 */
public abstract class Profiler {

	//If you want to profile function by miliseconds
	//pdb.profileFunction("tifmo.languageResource.ParaphraseDB", "buildParaphraseDB", new Object[]{new File(_destinationPath),new File(destinationPath)});

	public static Object profileStaticFunction(String className, String functionName, Object[] args) {
		long startTime = System.nanoTime();
		//
		try {
			Class c = Class.forName(className);
			Class[] argTypes = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				argTypes[i] = args[i].getClass();
			}
			Object methodObject = c.newInstance();
			Method method = c.getDeclaredMethod(functionName, argTypes);
			Object result = method.invoke(methodObject, args);

			long endTime = System.nanoTime();
			System.out.format("invoking %s()%n", c.getName());
			System.out.println(functionName + "executionTime: " + (endTime - startTime) / 1000000);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object profileNonStaticFunction(Object methodObject, String className, String functionName, Object[] args) {
		long startTime = System.nanoTime();
		//
		try {
			Class c = Class.forName(className);
			Class[] argTypes = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				argTypes[i] = args[i].getClass();
			}
			Method method = c.getDeclaredMethod(functionName, argTypes);
			Object result = method.invoke(methodObject, args);

			System.out.format("invoking %s()%n", c.getName());
			long endTime = System.nanoTime();
			System.out.println(functionName + "executionTime: " + (endTime - startTime) / 1000000);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
