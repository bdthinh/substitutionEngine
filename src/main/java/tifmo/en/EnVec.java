package tifmo.en;

import com.strangegizmo.cdb.Cdb;
import tifmo.utils.EnSimilarity;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * Created by bdthinh on 10/25/14.
 */
public abstract class EnVec {

	public int get_dim(){
		return _dim;
	}
	public void set_dim(int dim){
		_dim = dim;
	}
	public Cdb get_cdb(){
		return _cdb;
	}
	public void set_cdb(Cdb cdb){
		_cdb = cdb;
	}
	public String get_destinationPath(){
		return _destinationPath;
	}
	public void set_destinationPath(String destinationPath){
		_destinationPath = destinationPath;
	}
	protected Cdb _cdb;
	protected int _dim;
	protected String _destinationPath;
	public void init(int dim){
		return;
	}
	public double[] lookUp(String word){
		try {
			byte[] byteTarget = _cdb.find(word.getBytes("UTF-8"));
			if(byteTarget == null)
				return null;
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(byteTarget));
			return (new EnSimilarity(this._dim)).todoubleArray((float[]) ois.readObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public double[] lookUpSequence (String[] words){
		double[] sum = new double[get_dim()];
		for(int i = 0 ; i < get_dim() ; i++)
			sum[i] = 0.0;
		for(String word : words){
			if(!EnStopWords.isStopWord(word) || EnStopWords.isPrepositionFollowedVerb(word)) {
				double[] wordScore = lookUp(word);
				if (wordScore != null)
					sum = (new EnSimilarity(this._dim)).arraySum(sum, wordScore);
			}
		}
		return sum;
	}
}
