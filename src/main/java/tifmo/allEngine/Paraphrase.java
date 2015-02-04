package tifmo.allEngine;

import tifmo.similarityComp.DepsSimilarityComp;
import tifmo.similarityComp.GigaSimilarityComp;
import tifmo.similarityComp.GoogleSimilarityComp;
import tifmo.similarityComp.SkipGramSimilarityComp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bdthinh on 10/18/14.
 * Paraphrase stores source phrase along with a collection of its paraphrases.
 */
public class Paraphrase {
	String _source;
	List<FeatureSet> _featureSets = new ArrayList<FeatureSet>();

	public Paraphrase(String _source) {
		this._source = _source;
	}

	public Paraphrase(String source, List<FeatureSet> fs) {
		this._source = source;
		this._featureSets = new ArrayList<FeatureSet>(fs);
	}

	public Paraphrase(Paraphrase pp) {
		this._source = pp.get_source();
		this._featureSets = new ArrayList<FeatureSet>(pp.get_featureSets());
	}

	public String get_source() {
		return _source;
	}

	public void set_source(String _source) {
		this._source = _source;
	}

	public List<FeatureSet> get_featureSets() {
		return _featureSets;
	}


	public void set_featureSets(List<FeatureSet> _featureSets) {
		this._featureSets = _featureSets;
	}

	public Paraphrase filterPOS(String pos) {
		Paraphrase pp = new Paraphrase(this);
		int length = pp._featureSets.size();
		for (int i = 0; i < length; i++)
			if (!(pp._featureSets.get(i).get_posTag().get_pos().equals(pos))) {
				pp._featureSets.remove(i);
				length--;
				i--;
			}
		return pp;
	}

	public Paraphrase filterSimilarity(double[] thres) {
		Paraphrase pp = new Paraphrase(this);
		int length = pp._featureSets.size();
		for (int i = 0; i < length; i++)
			if (!pp.get_featureSets().get(i).isHighPass(thres)) {
				pp._featureSets.remove(i);
				length--;
				i--;
			}
		return pp;
	}

	public void sortByGigaSimilarity() {
		Collections.sort(_featureSets, new GigaSimilarityComp());
	}

	public void sortByGoogleSimilarity() {
		Collections.sort(_featureSets, new GoogleSimilarityComp());
	}

	public void sortBySkipGramSimilarity() {
		Collections.sort(_featureSets, new SkipGramSimilarityComp());
	}

	public void sortByDEPSSimilarity() {
		Collections.sort(_featureSets, new DepsSimilarityComp());
	}

	public void printOutSim() {
		System.out.println(_source + ": ");
		for (FeatureSet fs : _featureSets) {
			fs.printOut();
		}

	}
}
