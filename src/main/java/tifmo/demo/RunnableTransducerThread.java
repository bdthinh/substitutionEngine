package tifmo.demo;

import tifmo.allEngine.DIRTEngine;
import tifmo.allEngine.TransducerEngine;
import tifmo.coreNLP.Pair;
import tifmo.coreNLP.Parser;
import tifmo.en.EnFactory;
import tifmo.utils.EnUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bdthinh on 12/18/14.
 */
public class RunnableTransducerThread implements Runnable{
	public String get_threadName() {
		return _threadName;
	}

	public RunnableTransducerThread set_threadName(String _threadName) {
		this._threadName = _threadName;
		return this;
	}

	public RunnableTransducerThread(String _threadName) {
		this._threadName = _threadName;
	}

	public Thread get_thread() {
		return _thread;
	}

	public RunnableTransducerThread set_thread(Thread _thread) {
		this._thread = _thread;
		return this;
	}

	private String _threadName;
	private Thread _thread;

	@Override
	public void run() {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EnFactory.loadTransducer();
		String filePath = currentPath.concat("/resources/dict/transducerDict/").concat(_threadName);
		Map<Integer, Pair> substitutedPairs = TransducerEngine.substitutePairs(filePath);
		String destinationFilePath = currentPath.concat("/resources/output/transducer/transducer_") + _threadName;
		EnUtils.writePairs(destinationFilePath, substitutedPairs);
	}
	public void start(){
		System.out.println("Starting " + _threadName);
		if(_thread == null){
			_thread = new Thread(this, _threadName);
			_thread.start();
		}
	}
}
