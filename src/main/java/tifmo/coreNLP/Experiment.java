package tifmo.coreNLP;

/**
 * Created by bdthinh on 12/25/14.
 */
public class Experiment {
	public int get_truePositive() {
		return _truePositive;
	}

	public Experiment set_truePositive(int _truePositive) {
		this._truePositive = _truePositive;
		return this;
	}

	public int get_trueNegative() {
		return _trueNegative;
	}

	public Experiment set_trueNegative(int _trueNegative) {
		this._trueNegative = _trueNegative;
		return this;
	}

	public int get_falsePositive() {
		return _falsePositive;
	}

	public Experiment set_falsePositive(int _falsePositive) {
		this._falsePositive = _falsePositive;
		return this;
	}

	public int get_falseNegative() {
		return _falseNegative;
	}

	public Experiment set_falseNegative(int _falseNegative) {
		this._falseNegative = _falseNegative;
		return this;
	}

	private int _truePositive;
	private int _trueNegative;
	private int _falsePositive;
	private int _falseNegative;

	public int getPositiveOutcome(){
		return _truePositive + _falsePositive;
	}
	public int getNegativeOutcome(){
		return _trueNegative + _falseNegative;
	}
	public int getPositiveCondition(){
		return _truePositive + _falseNegative;
	}
	public int getNegativeCondition(){
		return _trueNegative + _falsePositive;
	}
	public double getPrecision(){
		return (double)_truePositive / (getPositiveOutcome());
	}
	public double getRecall(){
		return (double)_truePositive / (getPositiveCondition());
	}
	public double getF1Score(){
		return (2.0 * getPrecision() * getRecall()) / (getPrecision() + getRecall());
	}
	public double getGScore(){
		return Math.sqrt(getPrecision() * getRecall());
	}
	public double getAccuracy(){
		return (double)(_truePositive + _trueNegative) / (getPositiveCondition() + getNegativeCondition());
	}
	public double getPrevalence(){
		return (double)getPositiveCondition() / (getPositiveCondition() + getNegativeCondition());
	}
	public double getFalseOmissionRate(){
		return (double)_falseNegative / getNegativeOutcome();
	}
	public double getFalseDiscoveryRate(){
		return (double)_falsePositive / getPositiveOutcome();
	}
	public double getNegativePredictiveValue(){
		return (double)_trueNegative / getNegativeOutcome();
	}
	public double getFallOut(){
		return (double)_falsePositive / getNegativeCondition();
	}
	public double getSpecificity(){
		return (double)_trueNegative / getNegativeCondition();
	}
	public double getFalseNegativeRate(){
		return (double)_falseNegative /getNegativeCondition();
	}

	public double getPositiveLikelihoodRatio(){
		return getRecall() / getFallOut(); //LRplus
	}
	public double getNegativeLikelihoodRatio(){
		return getFalseNegativeRate() / getSpecificity(); //LRminus
	}
	public double getDiagnosticOddsRatio(){
		return getPositiveLikelihoodRatio() / getNegativeLikelihoodRatio();
	}
}
