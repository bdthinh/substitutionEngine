package tifmo.en;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.strangegizmo.cdb.Cdb;
import com.strangegizmo.cdb.CdbMake;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import org.apache.commons.io.IOUtils;
import tifmo.coreNLP.Token;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bdthinh on 11/10/14.
 */
public class EnDIRT {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost/original_dirt";
	private static final String _username = "root";
	private static final String _password = "Nohacking";
	private static final String _relativeDestPath = "/resources/cdb/dirt.cdb";

	public void set_destinationPath(String _destinationPath) {
		this._destinationPath = _destinationPath;
	}

	public String get_destinationPath() {
		return _destinationPath;
	}

	private String _destinationPath;

	public Cdb get_cdb() {
		return _cdb;
	}

	private Cdb _cdb;

	public static void main(String[] args) {
		String currentPath = "";
		try {
			currentPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadCDBFromDIRTDB(currentPath.concat(_relativeDestPath));
		System.out.println("Done");
//		loadCDBSynsetFromDIRTDB(currentPath.concat(_relativeDestPathSynset));
//		System.out.println("Done");
	}

	public static boolean loadCDBFromDIRTDB(String destinationPath){
		if(new File(destinationPath).exists())
			return true;
		try {
			Class.forName(JDBC_DRIVER);
			Connection conn = DriverManager.getConnection(DB_URL, _username, _password);
			System.out.println("Connection successful...");
			String sql = "Select max(id) from od_easyfirst_templates";
			Statement stt = conn.createStatement();
			ResultSet rsCount = stt.executeQuery(sql);
			rsCount.next();
			int identify = rsCount.getInt(1);
			CdbMake maker = new CdbMake();
			maker.start(destinationPath);
			int count = 0;
			for(int i = 1 ; i <= identify; i++) {
				sql = "Select A1.description, A2.description from od_rules, od_easyfirst_templates as A1, od_easyfirst_templates as A2" +
								" where A1.id = od_rules.left_element_id and A2.id = od_rules.right_element_id" +
								" and A1.id = ?";
				PreparedStatement pstt = conn.prepareStatement(sql);
				pstt.setInt(1, i);
				ResultSet rs = pstt.executeQuery();
				while (rs.next()) {
					String[] reg = new String[]{".*<v:(([a-z\\s])*):v>.*",".*<n:(([a-z\\s])*):n>.*",".*<a:(([a-z\\s])*):a>.*"};
					String leftPar = rs.getString(1);
					String rightPar = rs.getString(2);
					boolean flagL,flagR;
					flagL = false;
					flagR = false;
					for(int j = 0 ; j < reg.length ; j++) {
						if (leftPar.matches(reg[j]))
							flagL = true;
						if (rightPar.matches(reg[j]))
							flagR = true;
					}
					if(!flagL || !flagR){
						continue;
					}
					String leftElement = "";
					String rightElement = "";
					for(int j = 0 ; j < reg.length ; j++){
						Pattern pattern = Pattern.compile(reg[j]);
						Matcher matcherL = pattern.matcher(leftPar);
						Matcher matcherR = pattern.matcher(rightPar);
						if(matcherL.find() && leftElement.equals(""))
							leftElement = matcherL.group(1);
						if(matcherR.find() && rightElement.equals(""))
							rightElement = matcherR.group(1);
						if (!leftElement.equals("") && !rightElement.equals(""))
							break;
					}
					String key = leftElement.toLowerCase() + "||" + rightElement.toLowerCase();
					//String key = leftElement.toLowerCase();
					String value = leftPar.toLowerCase() + "||" + rightPar.toLowerCase();
					maker.add(key.getBytes("UTF-8"), value.getBytes("UTF-8"));
					if(count++ % 50000 == 0);
					System.out.println(count);
				}
			}
			System.out.println(count);
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
				//System.out.println("Load Dirt Cdb...");
				_cdb = new Cdb(_destinationPath);
			} else {
				System.out.println("Build Dirt Cdb from database...");
				if(loadCDBFromDIRTDB(_destinationPath)) {
					System.out.println("Load Dirt Cdb...");
					_cdb = new Cdb(_destinationPath);
				}
				else
					System.out.println("Error on connecting to database DIRT");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static char getPosOfHead(String dirtElement){
		char posWN = 'v';
		String patternHead = ".*<([a-z]):(([a-z\\s])*):([a-z])>.*";
		Pattern pattern = Pattern.compile(patternHead);
		Matcher matcher = pattern.matcher(dirtElement);
		if(matcher.find())
			posWN= matcher.group(1).toLowerCase().charAt(0);
		return posWN;
	}

	public Multimap<String, String> lookUp(String headLeft, String headRight){
		Multimap<String, String> ret = ArrayListMultimap.create();
		init();
		try{
			String key = headLeft.toLowerCase() + "||" + headRight.toLowerCase();
			byte[] byteTarget;
			while ((byteTarget = _cdb.findnext(key.getBytes("UTF-8"))) != null) {
				String value = IOUtils.toString(new ByteArrayInputStream(byteTarget), "UTF-8");
				String[] parts = value.split("\\|\\|");
//				String headOfRightElement = parts[0];
//				String leftElement = parts[0];
//				String rightElement = parts[1];
				ret.put(parts[0], parts[1]);
			}
		} catch(Exception ex){
			ex.printStackTrace();
		} finally {
			_cdb.close();
			_cdb = null;
		}
		return ret;
	}

}

