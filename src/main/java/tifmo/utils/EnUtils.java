package tifmo.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tifmo.coreNLP.MSPair;
import tifmo.coreNLP.Pair;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by bdthinh on 10/27/14.
 */
public class EnUtils {
	public static <T> List<T> union(List<T> list1, List<T> list2) {
		Set<T> set = new HashSet<T>();

		set.addAll(list1);
		set.addAll(list2);

		return new ArrayList<T>(set);
	}

	public static <T> List<T> intersection(List<T> list1, List<T> list2) {
		List<T> list = new ArrayList<T>();

		for (T t : list1) {
			if (list2.contains(t)) {
				list.add(t);
			}
		}

		return list;
	}

	public static String mkString(String[] words) {
		StringBuilder sb = new StringBuilder();
		sb.append(words[0]);
		for (int i = 1; i < words.length; i++) {
			if (!words[i].equals(""))
				sb.append(" ").append(words[i]);
		}
		return sb.toString();
	}

	public static void writeMSPairs(String filePath, Map<Integer, MSPair> msPairs){
		try{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element rootElement = doc.createElement("entailment-corpus");
			doc.appendChild(rootElement);

			for (int keyID : msPairs.keySet()) {
				Pair pair = msPairs.get(keyID).convertToPair(keyID);
				Element pairElement = doc.createElement("pair");
				pairElement.setAttribute("id", String.valueOf(pair.get_id()));
				pairElement.setAttribute("entailment", pair.get_entailment());
				pairElement.setAttribute("task", pair.get_task());
				Element textElement = doc.createElement("t");
				Element hypoElement = doc.createElement("h");
				textElement.appendChild(doc.createTextNode(pair.get_text()));
				hypoElement.appendChild(doc.createTextNode(pair.get_hypo()));
				pairElement.appendChild(textElement);
				pairElement.appendChild(hypoElement);
				rootElement.appendChild(pairElement);
			}
			Transformer transformer = (TransformerFactory.newInstance()).newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(new DOMSource(doc), new StreamResult(new File(filePath)));
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	public static void writePairs(String filePath, Map<Integer, Pair> pairs) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element rootElement = doc.createElement("entailment-corpus");
			doc.appendChild(rootElement);

			for (int keyID : pairs.keySet()) {
				Pair pair = pairs.get(keyID);
				Element pairElement = doc.createElement("pair");
				pairElement.setAttribute("id", String.valueOf(pair.get_id()));
				pairElement.setAttribute("entailment", pair.get_entailment());
				pairElement.setAttribute("task", pair.get_task());
				Element textElement = doc.createElement("t");
				Element hypoElement = doc.createElement("h");
				textElement.appendChild(doc.createTextNode(pair.get_text()));
				hypoElement.appendChild(doc.createTextNode(pair.get_hypo()));
				pairElement.appendChild(textElement);
				pairElement.appendChild(hypoElement);
				rootElement.appendChild(pairElement);
			}
			Transformer transformer = (TransformerFactory.newInstance()).newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(new DOMSource(doc), new StreamResult(new File(filePath)));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Map<Integer, Pair> readPairs(String filePath) {
		Map<Integer, Pair> ret = new HashMap<Integer, Pair>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(filePath));
			NodeList pairs = doc.getElementsByTagName("pair");
			for (int i = 0; i < pairs.getLength(); i++) {
				if (pairs.item(i).getNodeType() == Node.ELEMENT_NODE) {
					int id = Integer.parseInt(((Element) pairs.item(i)).getAttribute("id"));
					String entailment = ((Element) pairs.item(i)).getAttribute("entailment");
					String task = ((Element) pairs.item(i)).getAttribute("task");
					String text = ((Element) pairs.item(i)).getElementsByTagName("t").item(0).
									getChildNodes().item(0).getNodeValue().trim();
					String hypo = ((Element) pairs.item(i)).getElementsByTagName("h").item(0).
									getChildNodes().item(0).getNodeValue().trim();
					String fromFile = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
					ret.put(id, new Pair(id, text, hypo, entailment, task, fromFile));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static Map<Integer, MSPair> readPairsInMSRP(String filePath){
		Map<Integer, MSPair> ret = new HashMap<>();
		//task = "paraphrase Detection";
		//fromFile = "MSRP";
		File file = new File(filePath);
		if (file.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				int id = 0;
				while ((line = br.readLine()) != null) {
					String[] parts = line.split("\\t");
					MSPair tmp = new MSPair(Integer.valueOf(parts[0]), Integer.valueOf(parts[1]), Integer.valueOf(parts[2]), parts[3], parts[4]);
					ret.put(++id, tmp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public static String getLCS(String a, String b) {
		int[][] lengths = new int[a.length() + 1][b.length() + 1];

		// row 0 and column 0 are initialized to 0 already

		for (int i = 0; i < a.length(); i++)
			for (int j = 0; j < b.length(); j++)
				if (a.charAt(i) == b.charAt(j))
					lengths[i + 1][j + 1] = lengths[i][j] + 1;
				else
					lengths[i + 1][j + 1] =
									Math.max(lengths[i + 1][j], lengths[i][j + 1]);

		// read the substring out from the matrix
		StringBuffer sb = new StringBuffer();
		for (int x = a.length(), y = b.length();
		     x != 0 && y != 0; ) {
			if (lengths[x][y] == lengths[x - 1][y])
				x--;
			else if (lengths[x][y] == lengths[x][y - 1])
				y--;
			else {
				assert a.charAt(x - 1) == b.charAt(y - 1);
				sb.append(a.charAt(x - 1));
				x--;
				y--;
			}
		}

		return sb.reverse().toString();
	}

	public static double rateAveLCS(String a, String b) {
		return 2.0 * getLCS(a, b).length() / (a.length() + b.length());
	}

	public static double rateMaxLCS(String a, String b) {
		return 1.0 * getLCS(a, b).length() / (a.length() <= b.length() ? b.length() : a.length());
	}

	public static double rateMinLCS(String a, String b) {
		return 1.0 * getLCS(a, b).length() / (a.length() >= b.length() ? b.length() : a.length());
	}

	public static boolean equals(Map<Integer, Integer> alignA, Map<Integer, Integer> alignB){
		boolean flag = true;
		for(Integer keyA : alignA.keySet()){
			Integer valueA = alignA.get(keyA);
			if(!alignB.containsKey(keyA) || !alignB.get(keyA).equals(valueA))
				flag = false;
		}
		return flag;
	}

	public static void writeDoublePairs(String filePath, Map<Integer, Pair[]> pairs) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element rootElement = doc.createElement("entailment-corpus");
			doc.appendChild(rootElement);

			for (int keyID : pairs.keySet()) {
				Pair pairA = pairs.get(keyID)[0];
				Pair pairB = pairs.get(keyID)[1];
				Element pairElement = doc.createElement("pair");
				pairElement.setAttribute("id", String.valueOf(pairA.get_id()));
				pairElement.setAttribute("entailment", pairA.get_entailment());
				pairElement.setAttribute("task", pairA.get_task());
				Element textElementA = doc.createElement("t");
				Element textElementB = doc.createElement("t1");
				Element hypoElement = doc.createElement("h");
				textElementA.appendChild(doc.createTextNode(pairA.get_text()));
				textElementB.appendChild(doc.createTextNode(pairB.get_text()));
				hypoElement.appendChild(doc.createTextNode(pairA.get_hypo()));
				pairElement.appendChild(textElementA);
				pairElement.appendChild(textElementB);
				pairElement.appendChild(hypoElement);
				rootElement.appendChild(pairElement);
			}
			Transformer transformer = (TransformerFactory.newInstance()).newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(new DOMSource(doc), new StreamResult(new File(filePath)));

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void writePairsByDIRTEngine(String filePath, Map<Integer, Pair> pairs, Map<Integer, String> coverage) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element rootElement = doc.createElement("entailment-corpus");
			doc.appendChild(rootElement);

			for (int keyID : pairs.keySet()) {
				Pair pair = pairs.get(keyID);
				Element pairElement = doc.createElement("pair");
				pairElement.setAttribute("id", String.valueOf(pair.get_id()));
				pairElement.setAttribute("entailment", pair.get_entailment());
				pairElement.setAttribute("task", pair.get_task());
				pairElement.setAttribute("found", coverage.get(keyID).split(",")[0]);
				pairElement.setAttribute("matched", coverage.get(keyID).split(",")[1]);
				pairElement.setAttribute("equal", coverage.get(keyID).split(",")[2]);
				Element textElement = doc.createElement("t");
				Element hypoElement = doc.createElement("h");
				textElement.appendChild(doc.createTextNode(pair.get_text()));
				hypoElement.appendChild(doc.createTextNode(pair.get_hypo()));
				pairElement.appendChild(textElement);
				pairElement.appendChild(hypoElement);
				rootElement.appendChild(pairElement);
			}
			Transformer transformer = (TransformerFactory.newInstance()).newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(new DOMSource(doc), new StreamResult(new File(filePath)));

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}


	public static Multimap<String, String> combineMultimap(Multimap<String, String> oldChunks, Multimap<String, String> newChunks) {
		for(String tag : newChunks.keySet())
			for(String value: newChunks.get(tag)) {
				for(String oldValue : oldChunks.get(tag))
				if (!oldChunks.containsEntry(tag, value))
					oldChunks.put(tag, value);
			}
		return oldChunks;
	}
}
