package tifmo.en;

import com.strangegizmo.cdb.Cdb;
import com.strangegizmo.cdb.CdbMake;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import tifmo.utils.EnSimilarity;

import java.io.*;

/**
 * Created by bdthinh on 1/7/15.
 */
public class EnGlove extends EnVec {
	private static String _relativeSourcePath = "/resources/glove.42B.300d.txt.gz";
	private static String _relativeDestPath = "/resources/cdb/glove42b.cdb";
	public static String get_relativeDestPath() {
		return _relativeDestPath;
	}
	public static String get_relativeSourcePath(){
		return _relativeSourcePath;
	}
	private static String[] _destinationCDBPath;
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
		File sourceFile = new File(sourcePath);
		File destinationFile = new File(destinationPath);
		if(destinationFile.exists())
			return;

		try {
			CdbMake maker = new CdbMake();
			maker.start(destinationPath);
			CompressorInputStream cis = new CompressorStreamFactory().createCompressorInputStream(
							new BufferedInputStream(new FileInputStream(sourceFile)));
			BufferedReader br = new BufferedReader(new InputStreamReader(cis));
			String line = "";
			int count = 0 ;
			int nwCount = 0;
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
				String matcher = key.toLowerCase();
				if(matcher.matches("^[a-z].*"))
					maker.add(key.getBytes("UTF-8"), baos.toByteArray());
				else {
					System.out.println(key);
					if ((++nwCount) % 50000 == 0)
						System.out.println(nwCount);
				}
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
			_destinationPath = new File("").getCanonicalPath().concat(_relativeDestPath);
			if (new File(_destinationPath).exists()) {
				System.out.println("Load Glove Cdb...");
				_cdb = new Cdb(_destinationPath);
			} else {
				System.out.println("Build Glove Cdb from pack...");
				buildWordVecCbdFromPack(new File("").getCanonicalPath().concat(_relativeSourcePath), _destinationPath, _dim);
				System.out.println("Load Glove Cdb...");
				_cdb = new Cdb(_destinationPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
