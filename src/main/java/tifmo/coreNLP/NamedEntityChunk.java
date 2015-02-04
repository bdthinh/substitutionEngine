package tifmo.coreNLP;

/**
 * Created by bdthinh on 1/27/15.
 */
public class NamedEntityChunk {
	public String get_label() {
		return _label;
	}

	public void set_label(String _label) {
		this._label = _label;
	}

	public String get_chunk() {
		return _chunk;
	}

	public void set_chunk(String _chunk) {
		this._chunk = _chunk;
	}

	String _label;
	String _chunk;

	public NamedEntityChunk(String _label, String _chunk) {
		this._chunk = _chunk;
		this._label = _label;
	}

	public boolean equals(NamedEntityChunk chunk) {
		if(_chunk.toLowerCase().equals(chunk.get_chunk().toLowerCase()))
			return true;
		return false;
	}
	public boolean contains(NamedEntityChunk chunk){
		if(_chunk.toLowerCase().contains(chunk.get_chunk().toLowerCase()))
			return true;
		return false;
	}
}
