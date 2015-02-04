package tifmo.en;

import com.strangegizmo.cdb.Cdb;

import java.io.File;
import java.io.IOException;

/**
 * Created by bdthinh on 12/27/14.
 */
public class EnDEPSContextInverse extends EnDEPSContext {
	@Override
	public void init(int dim) {
		_dim = dim;
		if (_cdb != null)
			return;
		try {
			_destinationPath = new File("").getCanonicalPath().concat(EnDEPSContext.get_relativeDestPath().substring(0,EnDEPSContext.get_relativeDestPath().lastIndexOf(".")).concat("Inverse.cdb"));
			if (new File(_destinationPath).exists()) {
				System.out.println("Load DEPS context Inverse cdb...");
				_cdb = new Cdb(_destinationPath);
			} else {
				System.out.println("Build DEPS context Inverse Cdb from pack...");
				EnDEPSContext.buildWordVecCbdFromPack(EnDEPSContext.get_relativeSourcePath(), EnDEPSContext.get_relativeDestPath(), _dim);
				System.out.println("Load DEPS context Inverse Cdb...");
				_cdb = new Cdb(_destinationPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
