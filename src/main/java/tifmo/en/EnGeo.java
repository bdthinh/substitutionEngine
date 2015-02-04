package tifmo.en;


import com.strangegizmo.cdb.Cdb;
import com.strangegizmo.cdb.CdbMake;
import org.apache.commons.io.IOUtils;
import tifmo.coreNLP.Geo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;

/**
 * Created by bdthinh on 11/10/14.
 */
public class EnGeo {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost/geo";
	private static final String _username = "root";
	private static final String _password = "Nohacking";
	private static final String _relativeDestPath = "/resources/geo.cdb";

	public String get_destinationPath() {
		return _destinationPath;
	}

	public void set_destinationPath(String _destinationPath) {
		this._destinationPath = _destinationPath;
	}

	private String _destinationPath;
	private Cdb _cdb;
	public Cdb get_cdb() {
		return _cdb;
	}


	public static void main(String[] args) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String destinationPath = currentPath.concat("/resources/cdb/geo.cdb");
		loadCDBFromGeoDB(destinationPath);

	}

	public static boolean loadCDBFromGeoDB(String destinationPath){
		if(new File(destinationPath).exists())
			return true;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL,_username,_password);
			System.out.println("Connection successful...");
			Statement stt = conn.createStatement();
			String sql = "Select * from tipster";
			ResultSet rs = stt.executeQuery(sql);

			CdbMake maker = new CdbMake();
			maker.start(destinationPath);
			while(rs.next()){
				String _entailing = rs.getString("entailing");
				String _entailed = rs.getString("entailed");
				System.out.println(_entailing + "|||" + _entailed);
				maker.add(_entailing.toLowerCase().getBytes("UTF-8"),_entailed.toLowerCase().getBytes("UTF-8"));
			}
			maker.finish();
			conn.close();
			System.out.println("Connection closed!");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void init(){
		if (_cdb != null)
			return;
		try {
			_destinationPath = new File("").getCanonicalPath().concat(_relativeDestPath);
			if (new File(_destinationPath).exists()) {
				System.out.println("Load GEO Cdb...");
				_cdb = new Cdb(_destinationPath);
			} else {
				System.out.println("Build GEO Cdb from database...");
				if(loadCDBFromGeoDB(_destinationPath)) {
					System.out.println("Load GEO Cdb...");
					_cdb = new Cdb(_destinationPath);
				}
				else
					System.out.println("Error on connecting to database DIRT");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Geo lookUp(String key){
		String target = "";
		try {
			if(_cdb == null)
				return new Geo(key,"");
			byte[] byteTarget = _cdb.find(key.toLowerCase().getBytes("UTF-8"));
			if(byteTarget == null)
				return null;
			target = IOUtils.toString(new ByteArrayInputStream(byteTarget), "UTF-8");
			return new Geo(key, target);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Geo(key, target);
	}

	public boolean isSubsumed(String key, String value){
		Geo tmp = lookUp(key);
		if(value.toLowerCase().equals(tmp.get_entailed()))
			return true;
		return false;
	}
}
