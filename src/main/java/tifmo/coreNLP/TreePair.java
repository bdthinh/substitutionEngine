package tifmo.coreNLP;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bdthinh on 12/12/14.
 */
public class TreePair {
	public TreePair() {
		this._textTree = "";
		this._hypoTree = "";
	}

	public TreePair(String _textTree, String _hypoTree) {

		this._textTree = _textTree;
		this._hypoTree = _hypoTree;
	}

	public String get_textTree() {

		return _textTree;
	}

	public TreePair set_textTree(String _textTree) {
		this._textTree = _textTree;
		return this;
	}

	public String get_hypoTree() {
		return _hypoTree;
	}

	public TreePair set_hypoTree(String _hypoTree) {
		this._hypoTree = _hypoTree;
		return this;
	}

	String _textTree;
	String _hypoTree;

	public static TreePair getTreePairFromPair(Map<Integer,String> textTrees, Map<Integer,String> hypoTrees) {
		String treeOfText = "";
		String treeOfHypo = "";
		for (Integer sentText : textTrees.keySet()){
			treeOfText = treeOfText + textTrees.get(sentText) + " ";
		}
		treeOfText = "(ROOT " + treeOfText.trim() + ")";

		for(Integer sentText : hypoTrees.keySet()){
			treeOfHypo = treeOfHypo + hypoTrees.get(sentText) + " ";
		}
		treeOfHypo = "(ROOT " + treeOfHypo.trim() + ")";
		return new TreePair(treeOfText, treeOfHypo);
	}
	public static Map<Integer, TreePair> getTreePairs(Map<Integer, Pair> pairs) {
		Map<Integer, TreePair> ret = new HashMap<>();
		for (Integer id : pairs.keySet()) {

			Map<Integer, String> textTrees = Parser.getStringOfTrees(Parser.parseTextToAnnotation(pairs.get(id).get_text()));
			Map<Integer, String> hypoTrees = Parser.getStringOfTrees(Parser.parseTextToAnnotation(pairs.get(id).get_hypo()));
			System.out.println(id + " is transformed.");
			ret.put(id, getTreePairFromPair(textTrees,hypoTrees));
		}
		return ret;
	}
	public static void WriteEntailmentFile(String filePath, Map<Integer, TreePair> treePairs){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			for(Integer id : treePairs.keySet()){

				bw.write(treePairs.get(id).get_textTree());
				bw.newLine();
				bw.write(treePairs.get(id).get_hypoTree());
				bw.newLine();
				bw.flush();
				System.out.println(id + " is flushed.");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
