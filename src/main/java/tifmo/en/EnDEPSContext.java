package tifmo.en;

import com.strangegizmo.cdb.Cdb;
import com.strangegizmo.cdb.CdbMake;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.*;

/**
 * Created by bdthinh on 12/27/14.
 */
public class EnDEPSContext extends EnVec{
	private static String _relativeSourcePath = "/resources/deps.contexts.txt.gz";
	private static String _relativeDestPath = "/resources/cdb/deps.contexts.cdb";
	public static String get_relativeDestPath() {
		return _relativeDestPath;
	}

	public static void set_relativeDestPath(String path) {
		_relativeDestPath = path;
	}

	public static String get_relativeSourcePath() {
		return _relativeSourcePath;
	}

	public static void set_relativeSourcePath(String path) {
		_relativeSourcePath = path;
	}

	public static void main(String[] args) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		buildWordVecCbdFromPack(currentPath.concat(_relativeSourcePath), currentPath.concat(_relativeDestPath),300);
	}

	protected static void buildWordVecCbdFromPack(String sourcePath, String destinationPath, int dim) {
		File _sourceFile = new File(sourcePath);
		File _destinationFile = new File(destinationPath);
		File _destinationFileInverse = new File(destinationPath.substring(0,destinationPath.lastIndexOf(".")).concat("inverse.cdb"));
		if (_destinationFile.exists() && _destinationFileInverse.exists())
			return;
		try {
			CdbMake maker = new CdbMake();
			maker.start(destinationPath);
			CdbMake makerInverse = new CdbMake();
			makerInverse.start(destinationPath.substring(0,destinationPath.lastIndexOf(".")).concat("inverse.cdb"));

			CompressorInputStream cis = new CompressorStreamFactory().createCompressorInputStream(
							new BufferedInputStream(new FileInputStream(_sourceFile)));
			BufferedReader br = new BufferedReader(new InputStreamReader(cis));
			String line = "";
			int count = 0;
			while ((line = br.readLine()) != null) {
				String[] split = line.split(" ");
				String key = split[0];
				float[] value = new float[dim];
				for (int i = 0; i < dim; i++)
					value[i] = Float.parseFloat(split[i + 1]);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(value);
				oos.close();
				String dependencyRelation = key.split("_")[0];
				if(dependencyRelation.charAt(dependencyRelation.length()-1) == 'I')
					makerInverse.add(key.getBytes("UTF-8"), baos.toByteArray());
				else
					maker.add(key.getBytes("UTF-8"), baos.toByteArray());
				if ((++count) % 50000 == 0)
					System.out.println(count);
			}
			System.out.println(count);
			maker.finish();
			makerInverse.finish();
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
				System.out.println("Load DEPS context cdb...");
				_cdb = new Cdb(_destinationPath);
			} else {
				System.out.println("Build DEPS context Cdb from pack...");
				buildWordVecCbdFromPack(new File("").getCanonicalPath().concat(_relativeSourcePath), _destinationPath, _dim);
				System.out.println("Load DEPS context Cdb...");
				_cdb = new Cdb(_destinationPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
