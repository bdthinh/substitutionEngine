package tifmo.allEngine.TheySay;

/**
 * Created by bdthinh on 1/8/15.
 */
public class SentimentSentence {
	private String _text;
	private String _label;
	private Double _positive;
	private Double _negative;
	private Double _neutral;

	public SentimentSentence(String textSentence, String label, Double positive, Double neutral, Double negative) {
		_text = textSentence;
		_label = label;
		_positive = positive;
		_neutral = neutral;
		_negative = negative;
	}

	public String get_label() {
		return _label;
	}

	public SentimentSentence set_label(String _label) {
		this._label = _label;
		return this;
	}

	public Double get_negative() {
		return _negative;
	}

	public SentimentSentence set_negative(Double _negative) {
		this._negative = _negative;
		return this;
	}

	public Double get_neutral() {
		return _neutral;
	}

	public SentimentSentence set_neutral(Double _neutral) {
		this._neutral = _neutral;
		return this;
	}

	public Double get_positive() {
		return _positive;
	}

	public SentimentSentence set_positive(Double _positive) {
		this._positive = _positive;
		return this;
	}

	public String get_text() {
		return _text;
	}

	public SentimentSentence set_text(String _text) {
		this._text = _text;
		return this;
	}
}
