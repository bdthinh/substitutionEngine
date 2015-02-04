package tifmo.en;

import com.strangegizmo.cdb.Cdb;
import com.strangegizmo.cdb.CdbMake;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.*;

/**
 * Created by bdthinh on 10/25/14.
 */
public class EnMikolov extends EnVec {

	private static String _relativeSourcePath = "/resources/Mikolov13-GoogleNews-vectors-negative300.txt.gz";
	private static String _relativeDestPath = "/resources/cdb/Mikolov13.cdb";
	public static String get_relativeDestPath() {
		return _relativeDestPath;
	}
	public static String get_relativeSourcePath(){
		return _relativeSourcePath;
	}

	public static void main(String[] args) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//resourcePath
		String sourcePath = currentPath.concat(_relativeSourcePath);
		//destinationPath to put on cdb file
		String destinationPath = currentPath.concat(_relativeDestPath);
		buildWordVecCbdFromPack(sourcePath, destinationPath,300);
	}
	public static void buildWordVecCbdFromPack(String sourcePath, String destinationPath, int dim){
		File _sourceFile = new File(sourcePath);
		File _destinationFile = new File(destinationPath);
		if(_destinationFile.exists())
			return;
		try {
			CdbMake maker = new CdbMake();
			maker.start(destinationPath);
			CompressorInputStream cis = new CompressorStreamFactory().createCompressorInputStream(
							new BufferedInputStream(new FileInputStream(_sourceFile)));
			BufferedReader br = new BufferedReader(new InputStreamReader(cis));
			String line = "";
			int count = 0 ;
			while ((line = br.readLine()) != null) {
				String[] split = line.split(" ");
				String key = split[0];
				float[] value = new float[dim];
				for(int i = 0 ; i < dim ; i++)
					value[i] = Float.parseFloat(split[i+1]);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(value);
				oos.close();
				System.out.println(baos.size());
				maker.add(key.getBytes("UTF-8"), baos.toByteArray());
				if ((++count) % 50000 == 0)
					System.out.println(count);
			}
			System.out.println(count);
			maker.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(int dim) {
		_dim = dim;
		if (_cdb != null)
			return;
		try {
			_destinationPath = new File("").getCanonicalPath().concat(EnMikolov.get_relativeDestPath());
			if (new File(_destinationPath).exists()) {
				System.out.println("Load Word2Vec Cdb...");
				_cdb = new Cdb(_destinationPath);
			} else {
				System.out.println("Build Word2Vec Cdb from pack...");
				buildWordVecCbdFromPack(new File("").getCanonicalPath().concat(EnMikolov.get_relativeSourcePath()), _destinationPath, _dim);
				System.out.println("Load Word2Vec Cdb...");
				_cdb = new Cdb(_destinationPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
