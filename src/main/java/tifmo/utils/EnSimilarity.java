package tifmo.utils;

/**
 * Created by bdthinh on 10/25/14.
 */
public class EnSimilarity {

	public EnSimilarity() {
	}

	public EnSimilarity(int _dim) {

		this._dim = _dim;
	}

	protected int _dim;

	public double cosine(double[] x, double[] y) {
		return (arrayDot(x, y) / arrayNorm(x)) / arrayNorm(y);
	}

	public double arrayNorm(double[] x) {
		double norm = 0.0;
		for (int i = 0; i < _dim; i++)
			norm = norm + x[i] * x[i];
		if (norm == 0.0)
			return 1.0;
		return Math.sqrt(norm);
	}

	public double arrayDot(double[] x, double[] y) {
		double sum = 0.0;
		for (int i = 0; i < _dim; i++) {
			sum = sum + x[i] * y[i];
		}
		return sum;
	}

	public double[] arraySum(double[] x, double[] y) {
		double[] ret = new double[_dim];
		for (int i = 0; i < _dim; i++)
			ret[i] = x[i] + y[i];
		return ret;
	}

	public double[] arraySubtract(double[] x, double[] y) {
		double[] ret = new double[_dim];
		for (int i = 0; i < _dim; i++)
			ret[i] = x[i] - y[i];
		return ret;
	}

	public double[] todoubleArray(float[] tmp) {
		double[] output = new double[tmp.length];
		for (int i = 0; i < tmp.length; i++)
			output[i] = tmp[i];
		return output;
	}

}
