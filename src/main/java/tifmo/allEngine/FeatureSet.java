package tifmo.allEngine;

/**
 * Created by bdthinh on 10/18/14.
 * FeatureSet stores all target paraphrases of an input phrase
 */
public class FeatureSet {
	PosFeature _posTag;
	String _alignment;
	String _target;
	double[] _similarities; //0 giga 1 ngram 2 sgram 3 deps

	public FeatureSet() {
		this._posTag = new PosFeature();
		this._alignment = "";
		this._target = "";
		this._similarities = new double[]{0.0, 0.0, 0.0, 0.0};
	}

	public FeatureSet(PosFeature _posTag, String _alignment, String _target, double[] _similarities) {
		this._posTag = _posTag;
		this._alignment = _alignment;
		this._target = _target;
		this._similarities = _similarities;
	}

	public FeatureSet(String _target, PosFeature posTag, String alignment) {
		this._target = _target;
		this._posTag = posTag;
		this._alignment = alignment;
		this._similarities = new double[]{0.0, 0.0, 0.0, 0.0};
	}

	public double[] get_similarities() {
		return _similarities;
	}

	public void set_similarities(double[] _similarities) {
		this._similarities = _similarities;
	}

	public double get_giga() {
		return _similarities[0];
	}

	public void set_giga(double _giga) {
		this._similarities[0] = _giga;
	}

	public double get_ngram() {
		return _similarities[1];
	}

	public void set_ngram(double _ngram) {
		this._similarities[1] = _ngram;
	}

	public double get_deps() {
		return _similarities[3];
	}

	public void set_deps(double _deps) {
		this._similarities[3] = _deps;
	}

	public double get_sgram() {
		return _similarities[2];
	}

	public void set_sgram(double _sgram) {
		this._similarities[2] = _sgram;
	}

	public PosFeature get_posTag() {
		return _posTag;
	}

	public void set_posTag(PosFeature _posTag) {
		this._posTag = _posTag;
	}

	public String get_alignment() {
		return _alignment;
	}

	public void set_alignment(String _alignment) {
		this._alignment = _alignment;
	}

	public String get_target() {
		return _target;
	}

	public void set_target(String _target) {
		this._target = _target;
	}

	public boolean isHighPass(double gigaThres, double ngramThres) {
		if (_similarities[0] < gigaThres ||
						_similarities[1] < ngramThres)
			return false;
		return true;
	}

	public boolean isHighPass(double[] thres) {
		for (int i = 0; i < _similarities.length; i++)
			if (_similarities[i] < thres[i])
				return false;
		return true;
	}

	public boolean isHighPassSgram(double sgramThres) {
		if (_similarities[2] < sgramThres)
			return false;
		return true;
	}

	public boolean isHighPassDEPS(double DEPSThres) {
		if (_similarities[3] < DEPSThres)
			return false;
		return true;
	}

	public void printOut() {
		System.out.println(_posTag.toString() + " " + _target + "[ " + _alignment + " ]");
	}
}
