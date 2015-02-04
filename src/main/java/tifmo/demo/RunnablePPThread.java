package tifmo.demo;

import org.apache.commons.io.input.ReversedLinesFileReader;
import tifmo.allEngine.PPEngine;
import tifmo.coreNLP.Pair;
import tifmo.utils.EnUtils;

import java.io.*;
import java.util.*;

/**
 * Created by bdthinh on 12/12/14.
 */
public class RunnablePPThread implements Runnable {
	private Thread _thread;
	private String _threadName;
	private boolean _runMode;
	private boolean _keepMode;

	public String get_threadName() {
		return _threadName;
	}

	public RunnablePPThread set_threadName(String _threadName) {
		this._threadName = _threadName;
		return this;
	}

	public boolean is_runMode() {
		return _runMode;
	}

	public boolean is_keepMode() {
		return _keepMode;
	}

	public RunnablePPThread set_runMode(boolean _runMode) {
		this._runMode = _runMode;
		return this;
	}

	public List<String> getChosens() {
		return chosens;
	}

	public List<String> getTypes() {
		return types;
	}

	public RunnablePPThread set_keepMode(boolean _keepMode) {
		this._keepMode = _keepMode;
		return this;

	}

	List<String> chosens = Arrays.asList("mikolov","turian","lcs");
	List<String> types = Arrays.asList("corefFirst", "corefFirstReverse", "corefLater", "corefLaterReverse");

	public RunnablePPThread(String threadName, boolean runMode, boolean keepMode) {
		this._threadName = threadName;
		this._runMode = runMode;
		this._keepMode = keepMode;
		System.out.println("Creating "+ threadName);
	}

	public void runWithKeepModeTrue(){
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
			Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(this.get_threadName()));
			Map<String, Map<String, Map<Integer, Pair>>> targetPairs = new HashMap<>();
			for (String type : this.getTypes()) {
				targetPairs.put(type, new HashMap<String, Map<Integer, Pair>>());
				for (String chosen : this.getChosens()) {
					targetPairs.get(type).put(chosen, new HashMap<Integer, Pair>());
				}
			}
			File cachedFile = new File(currentPath.concat("/resources/input/cached/").concat(this.get_threadName().substring(0, this.get_threadName().lastIndexOf("."))).concat(".txt"));
			int beginId = 0;
			if (this.is_runMode() && cachedFile.exists()) {
				ReversedLinesFileReader rlfr = new ReversedLinesFileReader(cachedFile);
				String line = rlfr.readLine();
				if(line == null) {
					System.out.println(this.get_threadName() + ": Cache's empty. Let's start at beginning.");
					cachedFile.createNewFile();
				}
				else {
					System.out.println(this.get_threadName() + ": Cache's loaded.");
					beginId = Integer.valueOf(line.split("\\|\\|\\|")[0]);
				}
			}
			else if (!cachedFile.exists())
				cachedFile.createNewFile();
			for (Integer id : originPairs.keySet()) {
				if(id <= beginId)
					continue;
				Pair originPair = originPairs.get(id);
				System.out.println(this.get_threadName().substring(0, this.get_threadName().indexOf(".")) + "--id: " + id);
				Map<String, Map<String, Pair>> totalPairs = PPEngine.substituteAllLongestPP(originPair, chosens, types);
				List<String> lines = new ArrayList<>();
				for (String type : totalPairs.keySet()) {
					Map<String, Pair> pairsInType = totalPairs.get(type);
					for (String chosen : pairsInType.keySet()) {
						Pair pairTmp = pairsInType.get(chosen);
						pairTmp.set_id(originPair.get_id()).set_entailment(originPair.get_entailment()).set_task(originPair.get_task());
						if(!this.is_runMode())
							targetPairs.get(type).get(chosen).put(id, pairTmp);
						lines.add(pairTmp.get_id() + "|||" + type + "|||" + chosen + "|||" + pairTmp.get_entailment() + "|||" + pairTmp.get_task() + "|||" + pairTmp.get_text() + "|||" + pairTmp.get_hypo());
					}
				}
				BufferedWriter bw = new BufferedWriter(new FileWriter(cachedFile.getAbsoluteFile(), this.is_runMode()));
				for (String line : lines) {
					bw.write(line);
					bw.newLine();
				}
				bw.flush();
				bw.close();
			}
			if(!this.is_runMode()) {
				for (String type : this.getTypes()) {
					for (String chosen : this.getChosens()) {
						String destinationFilePath = currentPath.concat("/resources/input/") + type + "/" + chosen + "_" + type + "_" + this.get_threadName();
						EnUtils.writePairs(destinationFilePath, targetPairs.get(type).get(chosen));
					}
				}
			}
			else{
				BufferedReader br = new BufferedReader(new FileReader(cachedFile));
				String line;
				while((line = br.readLine()) != null){
					String[] splitted = line.split("\\|\\|\\|");
					Pair pairOfLine = new Pair(Integer.valueOf(splitted[0]),splitted[5],splitted[6],splitted[3],splitted[4]);
					targetPairs.get(splitted[1]).get(splitted[2]).put(pairOfLine.get_id(),pairOfLine);
				}
				for (String type : this.getTypes()) {
					for (String chosen : this.getChosens()) {
						String destinationFilePath = currentPath.concat("/resources/input/") + type + "/" + chosen + "_" + type + "_" + this.get_threadName();
						EnUtils.writePairs(destinationFilePath, targetPairs.get(type).get(chosen));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void runWithKeepModeFalse(){
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();

				Map<Integer, Pair> originPairs = EnUtils.readPairs(currentPath.concat("/resources/input/").concat(_threadName));
				Map<String, Map<String, Map<Integer, Pair>>> targetPairs = new HashMap<>();
				for (String type : types) {
					targetPairs.put(type, new HashMap<String, Map<Integer, Pair>>());
					for (String chosen : chosens) {
						targetPairs.get(type).put(chosen, new HashMap<Integer, Pair>());
					}
				}
				File cachedFile = new File(currentPath.concat("/resources/input/cachedExtend/").concat(_threadName.substring(0, _threadName.lastIndexOf("."))).concat(".txt"));
				int beginId = 0;
				if (_runMode && cachedFile.exists()) {
					ReversedLinesFileReader rlfr = new ReversedLinesFileReader(cachedFile);
					String line = rlfr.readLine();
					if(line == null) {
						System.out.println(_threadName + ": Cache's empty. Let's start at beginning.");
						cachedFile.createNewFile();
					}
					else {
						System.out.println(_threadName + ": Cache's loaded.");
						beginId = Integer.valueOf(line.split("\\|\\|\\|")[0]);
					}
				}
				else if (!cachedFile.exists())
					cachedFile.createNewFile();
				for (Integer id : originPairs.keySet()) {
					if(id <= beginId)
						continue;
					Pair originPair = originPairs.get(id);
					System.out.println(_threadName.substring(0, _threadName.indexOf(".")) + "--id: " + id);
					Map<String, Map<String, Pair>> totalPairs = PPEngine.substituteAllShortestPP(originPair, chosens, types);
					List<String> lines = new ArrayList<>();
					for (String type : totalPairs.keySet()) {
						Map<String, Pair> pairsInType = totalPairs.get(type);
						for (String chosen : pairsInType.keySet()) {
							Pair pairTmp = pairsInType.get(chosen);
							pairTmp.set_id(originPair.get_id()).set_entailment(originPair.get_entailment()).set_task(originPair.get_task());
							if(!this.is_runMode())
								targetPairs.get(type).get(chosen).put(id, pairTmp);
							lines.add(pairTmp.get_id() + "|||" + type + "|||" + chosen + "|||" + pairTmp.get_entailment() + "|||" + pairTmp.get_task() + "|||" + pairTmp.get_text() + "|||" + pairTmp.get_hypo());
						}
					}
					BufferedWriter bw = new BufferedWriter(new FileWriter(cachedFile.getAbsoluteFile(), _runMode));
					for (String line : lines) {
						bw.write(line);
						bw.newLine();
					}
					bw.flush();
					bw.close();
				}
				if(!_runMode) {
					for (String type : types) {
						for (String chosen : chosens) {
							String destinationFilePath = currentPath.concat("/resources/input/") + type + "/ss_" + chosen + "_" + type + "_" + _threadName;
							EnUtils.writePairs(destinationFilePath, targetPairs.get(type).get(chosen));
						}
					}
				}
				else{
					BufferedReader br = new BufferedReader(new FileReader(cachedFile));
					String line;
					while((line = br.readLine()) != null){
						String[] splitted = line.split("\\|\\|\\|");
						Pair pairOfLine = new Pair(Integer.valueOf(splitted[0]),splitted[5],splitted[6],splitted[3],splitted[4]);
						targetPairs.get(splitted[1]).get(splitted[2]).put(pairOfLine.get_id(),pairOfLine);
					}
					for (String type : types) {
						for (String chosen : chosens) {
							String destinationFilePath = currentPath.concat("/resources/input/") + type + "/ss_" + chosen + "_" + type + "_" + _threadName;
							EnUtils.writePairs(destinationFilePath, targetPairs.get(type).get(chosen));
						}
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		if(_keepMode)
			runWithKeepModeTrue();
		else
			runWithKeepModeFalse();
	}

	public void start(){
		System.out.println("Starting "+ _threadName);
		if(_thread == null){
			_thread = new Thread(this, _threadName);
			_thread.start();
		}

	}
}

