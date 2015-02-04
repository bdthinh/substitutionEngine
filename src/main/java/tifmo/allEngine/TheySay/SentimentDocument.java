package tifmo.allEngine.TheySay;

/**
 * Created by bdthinh on 1/8/15.
 */
public class SentimentDocument {
	private String _text;
	private String _label;
	private Double _positive;
	private Double _negative;
	private Double _neutral;

	@Override
	public String toString(){
		String text = _label + "," + _positive.toString() + "," + _neutral.toString() + "," + _negative.toString();
		return text;
	}
	public SentimentDocument(String textSentence, String label, Double positive, Double neutral, Double negative) {
		_text = textSentence;
		_label = label;
		_positive = positive;
		_neutral = neutral;
		_negative = negative;
	}

	public String get_label() {
		return _label;
	}

	public SentimentDocument set_label(String _label) {
		this._label = _label;
		return this;
	}

	public Double get_negative() {
		return _negative;
	}

	public SentimentDocument set_negative(Double _negative) {
		this._negative = _negative;
		return this;
	}

	public Double get_neutral() {
		return _neutral;
	}

	public SentimentDocument set_neutral(Double _neutral) {
		this._neutral = _neutral;
		return this;
	}

	public Double get_positive() {
		return _positive;
	}

	public SentimentDocument set_positive(Double _positive) {
		this._positive = _positive;
		return this;
	}

	public String get_text() {
		return _text;
	}

	public SentimentDocument set_text(String _text) {
		this._text = _text;
		return this;
	}
}
