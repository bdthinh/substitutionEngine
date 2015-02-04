package tifmo.demo;

import tifmo.allEngine.DIRTEngine;
import tifmo.coreNLP.Pair;
import tifmo.coreNLP.Parser;
import tifmo.en.EnFactory;
import tifmo.utils.EnUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bdthinh on 12/15/14.
 */
public class RunnableDIRTThread implements Runnable{
	public Thread get_thread() {
		return _thread;
	}

	public RunnableDIRTThread set_thread(Thread _thread) {
		this._thread = _thread;
		return this;
	}

	public String get_threadName() {
		return _threadName;
	}

	public RunnableDIRTThread set_threadName(String _threadName) {
		this._threadName = _threadName;
		return this;
	}

	private Thread _thread;
	private String _threadName;

	public RunnableDIRTThread(String _threadName) {
		this._threadName = _threadName;
		System.out.println("Creating "+ _threadName);
	}

	@Override
	public void run() {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EnFactory.loadDIRT();
		Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(_threadName));
		Map<Integer, Pair> substitutedPairs = new HashMap<>();
		for(Integer id : originPairs.keySet()){
			Map<String, Object> textAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_text());
			Map<String, Object> hypoAnno = Parser.parseTextToAnnotation(originPairs.get(id).get_hypo());
			Pair substitutedPair = DIRTEngine.substitutePair(textAnno, hypoAnno);
			substitutedPairs.put(id, substitutedPair);
		}
		String destinationFilePath = currentPath.concat("/resources/input/dirt_") + _threadName;
		EnUtils.writePairs(destinationFilePath, substitutedPairs);
	}
	public void start(){
		System.out.println("Starting "+ _threadName);
		if(_thread == null){
			_thread = new Thread(this, _threadName);
			_thread.start();
		}
	}
}
