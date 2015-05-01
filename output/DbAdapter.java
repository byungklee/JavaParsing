import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;

public class DbAdapter {
	final String CONNECTION_NAME = "jdbc:sqlite:assignment3.db";
	final String PACKAGE_TABLE = "CREATE TABLE IF NOT EXISTS package "
			+ "(id integer primary key AUTOINCREMENT, \n"
			+ "package_name text not null, \n" +
			// "unique_keyword_count int not null, \n" +
			// "unique_udi_count int not null, \n" +
			// "unique_constant_count int not null, \n" +
			// "unique_special_char_count int not null, \n" +
			// "total_keyword_count int not null, \n" +
			// "total_udi_count int not null, \n" +
			// "total_constant_count int not null, \n" +
			// "total_special_char_count int not null, \n" +
			// "unique_keyword_list text not null, \n" +
			"total_keyword_list text not null, \n" +
			// "unique_udi_list text not null, \n" +
			"total_udi_list text not null, \n" +
			// "unique_constant_list text not null, \n" +
			"total_constant_list text not null, \n" +
			// "unique_special_char_list text not null, \n" +
			"total_special_char_list text not null " + ");";

	// final String PACKAGE_TABLE = "CREATE TABLE IF NOT EXISTS package " +
	// "(id integer primary key AUTOINCREMENT, " +
	// "package_name text not null, "
	// + "UNIQUE (package_name) );";

	final String CLASS_TABLE = "CREATE TABLE IF NOT EXISTS class "
			+ "(id integer primary key AUTOINCREMENT,\n"
			+ "package_name text not null,\n" + "class_name text not null,\n" +
			// "unique_keyword_count int not null, \n" +
			// "unique_udi_count int not null, \n" +
			// "unique_constant_count int not null, \n" +
			// "unique_special_char_count int not null, \n" +
			// "total_keyword_count int not null, \n" +
			// "total_udi_count int not null, \n" +
			// "total_constant_count int not null, \n" +
			// "total_special_char_count int not null, \n" +
			// "unique_keyword_list text not null, \n" +
			"total_keyword_list text not null, \n" +
			// "unique_udi_list text not null, \n" +
			"total_udi_list text not null, \n" +
			// "unique_constant_list text not null, \n" +
			"total_constant_list text not null, \n" +
			// "unique_special_char_list text not null, \n" +
			"total_special_char_list text not null " + ");";
	final String CCM_TABLE = "CREATE TABLE IF NOT EXISTS ccm_data "
			+ "(id integer primary key AUTOINCREMENT,\n" 
			+ "package_name text not null,\n"
			+ "class_name text not null,\n"
			+ "method_name text not null,\n"
			+ "m integer not null);";
			
	private Connection c;
	private PreparedStatement insertClassInfoPreparedStatement;
	private PreparedStatement insertPackageInfoPreparedStatement;
	// final String INSERT_PACKAGE_INFO = "insert into package (package_name, "
	// +
	// "unique_keyword_count, unique_udi_count, unique_constant_count, unique_special_char_count,"
	// +
	// " total_keyword_count, total_udi_count, total_constant_count, total_special_char_count, "
	// +
	// "unique_keyword_list, total_keyword_list, unique_udi_list, total_udi_list, "
	// +
	// "unique_constant_list, total_constant_list, unique_special_char_list, total_special_char_list)"
	// + " values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	// final String INSERT_PACKAGE_INFO = "insert into package (package_name)" +
	// " values (?);";
	final String INSERT_PACKAGE_INFO = "insert into package (package_name, total_keyword_List, total_udi_list, total_constant_list, total_special_char_list)"
			+ " values (?,?,?,?,?);";

	// final String INSERT_PACKAGE_INFO =
	// "insert or replace into package (package_name, total_keyword_List, total_udi_list, total_constant_list, total_special_char_list)"
	// +
	// " values ( ?,?,?,?,?);";

	final String INSERT_CLASS_INFO = "insert into class (package_name, class_name, "
			// +
			// "unique_keyword_count, unique_udi_count, unique_constant_count, unique_special_char_count,"
			// +
			// " total_keyword_count, total_udi_count, total_constant_count, total_special_char_count, "
			// +
			// "unique_keyword_list, total_keyword_list, unique_udi_list, total_udi_list, "
			// +
			// "unique_constant_list, total_constant_list, unique_special_char_list, total_special_char_list)"
			+ "total_keyword_list, total_udi_list, total_constant_list, total_special_char_list) "
			+ "values (?, ?, ?, ?, ?, ?);";
	// + " values ( ?, ?, ?, ?, ?,"
	// + " ?, ?, ?, ?, ?,"
	// + " ?, ?, ?, ?, ?,"
	// + " ?, ?, ?);";
	
	
	/**
	 * To insert or update if exists
	 * 
	 * insert or replace into ccm_data(id, package_name,class_name,method_name, m)
	 * values (
	 * (select id from ccm_data where package_name='default' and class_name='T2' and method_name='f4'),
	 *'default',
	 *'T2',
	 *'f4',
	 *8);
	 */
	final String INSERT_CCM_DATA = "insert into ccm_data (package_name, class_name, method_name, m) values (?,?,?,?)";
	final String SELECT_CCM_DATA = "select * from ccm_data where package_name=? and class_name=?";

	private PreparedStatement selectClassByPackagePS;
	final String SELECT_CLASS_BY_PACKAGE = "select * from class where package_name=?;";


	public DbAdapter() {
		// Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(CONNECTION_NAME);
			initTables();

			// insertClassInfoPreparedStatement.getConnection();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// System.exit(0);
		}
		System.out.println("Opened database successfully");

	}

	public void initTables() {
		try {
			Statement s = c.createStatement();
			s.execute(PACKAGE_TABLE);
			s.execute(CLASS_TABLE);
			s.execute(CCM_TABLE);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertCCMDataWithClassInfoManager(ClassInfoManager cim) {
		System.out.println("DEBUG: INSERT CCM DATA " + cim.classInfoMap.size());
		Iterator it = cim.iterator();
		while (it.hasNext()) {
			Entry e = (Entry) it.next();
			ClassInfo ci = (ClassInfo) e.getValue();
			System.out.println("WTF: " + ci.cyclomaticHash.size());
			Iterator it2 = ci.cyclomaticHash.entrySet().iterator();
			//ArrayList<Integer> values = new ArrayList<Integer>();
			while(it2.hasNext()) {
				Entry e2 = (Entry) it2.next();
//				int temp = ((ArrayList) e.getValue()).size() + 1;
//				sb.append("Method: " + e.getKey() + " ---- " + temp);
//				sb.append("\n");
//				values.add(temp);
				try {
					System.out.println("SAVING CCM_DATA " + e2.getKey().toString() + " " + e2.getValue());
					c = DriverManager.getConnection(CONNECTION_NAME);
					PreparedStatement s = c.prepareStatement(INSERT_CCM_DATA);
					s.setString(1, ci.packageName);
					s.setString(2, ci.className);
					s.setString(3, e2.getKey().toString());
					s.setInt(4, (int)e2.getValue());
					s.executeUpdate();
					c.close();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				
			}
			
			
		}
		
	}
	
	public void insertCCMDataWithClassInfo(ClassInfo ci) {
			System.out.println("insertCCMDataWithClassInfo ");
			try {
				c = DriverManager.getConnection(CONNECTION_NAME);
				Statement s = c.createStatement();
				s.execute("delete from ccm_data where package_name=\'"+ci.packageName+ "\' and class_name=\'" + ci.className + "\';");
				c = DriverManager.getConnection(CONNECTION_NAME);
				c.close();
			} catch (Exception ex) {
				
			}
			Iterator it2 = ci.cyclomaticHash.entrySet().iterator();
			//ArrayList<Integer> values = new ArrayList<Integer>();
			while(it2.hasNext()) {
				Entry e2 = (Entry) it2.next();
//				int temp = ((ArrayList) e.getValue()).size() + 1;
//				sb.append("Method: " + e.getKey() + " ---- " + temp);
//				sb.append("\n");
//				values.add(temp);
				try {
					System.out.println("SAVING CCM_DATA " + e2.getKey().toString() + " " + e2.getValue());
					c = DriverManager.getConnection(CONNECTION_NAME);
					PreparedStatement s = c.prepareStatement(INSERT_CCM_DATA);
					s.setString(1, ci.packageName);
					s.setString(2, ci.className);
					s.setString(3, e2.getKey().toString());
					s.setInt(4, (int)e2.getValue());
					s.executeUpdate();
					c.close();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				
			}
		
	}

	public void insertClassInfos(ClassInfoManager cim) {
		// cim.classInfoMap.entrySet().iterator();

		Iterator it = cim.iterator();
		while (it.hasNext()) {
			Entry e = (Entry) it.next();
			ClassInfo ci = (ClassInfo) e.getValue();
			try {
				c = DriverManager.getConnection(CONNECTION_NAME);
				Statement s = c.createStatement();
				s.execute("delete from class where package_name=\""
						+ ci.packageName + "\" and class_name=\""
						+ ci.className + "\";");
				insertClassInfoPreparedStatement = c
						.prepareStatement(INSERT_CLASS_INFO);
				// System.out.println(insertClassInfoPreparedStatement == null);
				insertClassInfoPreparedStatement.setString(1, ci.packageName);
				insertClassInfoPreparedStatement.setString(2, ci.className);
				insertClassInfoPreparedStatement.setString(3,
						ci.keywords.toString());
				insertClassInfoPreparedStatement.setString(4,
						ci.udis.toString());
				insertClassInfoPreparedStatement.setString(5,
						ci.constants.toString());
				insertClassInfoPreparedStatement.setString(6,
						ci.specialChars.toString());

				// insertClassInfoPreparedStatement.setInt(3,
				// ci.keywordsSet.size());
				// insertClassInfoPreparedStatement.setInt(4,
				// ci.udisSet.size());
				// insertClassInfoPreparedStatement.setInt(5,
				// ci.constantsSet.size());
				//
				// insertClassInfoPreparedStatement.setInt(6,
				// ci.specialCharsSet.size());
				// insertClassInfoPreparedStatement.setInt(7,
				// ci.keywords.size());
				// insertClassInfoPreparedStatement.setInt(8, ci.udis.size());
				// insertClassInfoPreparedStatement.setInt(9,
				// ci.constants.size());
				// insertClassInfoPreparedStatement.setInt(10,
				// ci.specialChars.size());
				//
				// insertClassInfoPreparedStatement.setString(11,
				// ci.keywordsSet.toString());
				// insertClassInfoPreparedStatement.setString(12,
				// ci.keywords.toString());
				// insertClassInfoPreparedStatement.setString(13,
				// ci.udisSet.toString());
				// insertClassInfoPreparedStatement.setString(14,
				// ci.udis.toString());
				// insertClassInfoPreparedStatement.setString(15,
				// ci.constantsSet.toString());
				//
				// insertClassInfoPreparedStatement.setString(16,
				// ci.constants.toString());
				// insertClassInfoPreparedStatement.setString(17,
				// ci.specialCharsSet.toString());
				// insertClassInfoPreparedStatement.setString(18,
				// ci.specialChars.toString());

				insertClassInfoPreparedStatement.executeUpdate();
				insertCCMDataWithClassInfo(ci);
				
				c.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	public void insertPackageInfos(PackageInfoManager pim) {
		// pim.InfoMap.entrySet().iterator();try

		Iterator it = pim.iterator();
		try {
			c = DriverManager.getConnection(CONNECTION_NAME);
			while (it.hasNext()) {
				Entry e = (Entry) it.next();
				PackageInfo pi = (PackageInfo) e.getValue();
				System.out.println(pi.getString());
				Statement s = c.createStatement();
				System.out.println("delete existing data if exist " + e.getKey().toString());
				s.execute("delete from package where package_name=\""
						+ pi.packageName + "\";");
				
				insertPackageInfoPreparedStatement = c
						.prepareStatement(INSERT_PACKAGE_INFO);
				insertPackageInfoPreparedStatement.setString(1, pi.packageName);
				insertPackageInfoPreparedStatement.setString(2,
						pi.keywords.toString());
				insertPackageInfoPreparedStatement.setString(3,
						pi.udis.toString());
				insertPackageInfoPreparedStatement.setString(4,
						pi.constants.toString());
				//System.out.println("inserting to databse speical chars " + pi.specialChars.size());
				insertPackageInfoPreparedStatement.setString(5,
						pi.specialChars.toString());

				// insertPackageInfoPreparedStatement.setInt(2,
				// pi.keywordsSet.size());
				// insertPackageInfoPreparedStatement.setInt(3,
				// pi.udisSet.size());
				// insertPackageInfoPreparedStatement.setInt(4,
				// pi.constantsSet.size());
				//
				// insertPackageInfoPreparedStatement.setInt(5,
				// pi.specialCharsSet.size());
				// insertPackageInfoPreparedStatement.setInt(6,
				// pi.keywords.size());
				// insertPackageInfoPreparedStatement.setInt(7, pi.udis.size());
				// insertPackageInfoPreparedStatement.setInt(8,
				// pi.constants.size());
				// insertPackageInfoPreparedStatement.setInt(9,
				// pi.specialChars.size());
				//
				// insertPackageInfoPreparedStatement.setString(10,
				// pi.keywordsSet.toString());
				// insertPackageInfoPreparedStatement.setString(11,
				// pi.keywords.toString());
				// insertPackageInfoPreparedStatement.setString(12,
				// pi.udisSet.toString());
				// insertPackageInfoPreparedStatement.setString(13,
				// pi.udis.toString());
				// insertPackageInfoPreparedStatement.setString(14,
				// pi.constantsSet.toString());
				//
				// insertPackageInfoPreparedStatement.setString(15,
				// pi.constants.toString());
				// insertPackageInfoPreparedStatement.setString(16,
				// pi.specialCharsSet.toString());
				// insertPackageInfoPreparedStatement.setString(17,
				// pi.specialChars.toString());
				insertPackageInfoPreparedStatement.executeUpdate();

			}
			c.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	final String SELECT_PACKAGE_BY_NAME = "select * from package where package_name=?;";
	private PreparedStatement selectPackageByName;

	public PackageInfo getSelectedPackage(String name) {
		PackageInfo pi = null;// new PackageInfo();
		try {
			c = DriverManager.getConnection(CONNECTION_NAME);
			selectPackageByName = c.prepareStatement(SELECT_PACKAGE_BY_NAME);
			selectPackageByName.setString(1, name);
			ResultSet rs = selectPackageByName.executeQuery();

			while (rs.next()) {
				pi = new PackageInfo();
				pi.packageName = rs.getString(2);
				databaseToListAndSet(pi.keywords, pi.keywordsSet,
						rs.getString(3)); // key
				databaseToListAndSet(pi.udis, pi.udisSet, rs.getString(4));
				databaseToListAndSet(pi.constants, pi.constantsSet,
						rs.getString(5));
				databaseToListAndSetForSChar(pi.specialChars, pi.specialCharsSet,
						rs.getString(6));
			}
			c.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return pi;
	}

	final String SELECT_ALL_PACKAGE = "select * from package;";
	public DefaultListModel getAllPackage() {
		DefaultListModel dlm = new DefaultListModel();
		try {
			c = DriverManager.getConnection(CONNECTION_NAME);
			selectClassByPackagePS = c.prepareStatement(SELECT_ALL_PACKAGE);
			ResultSet rs = selectClassByPackagePS.executeQuery();
			// rs.first();
			while (rs.next()) {
				PackageInfo pi = new PackageInfo();
				// pName
				pi.packageName = rs.getString(2);
				databaseToListAndSet(pi.keywords, pi.keywordsSet,
						rs.getString(3)); // key
				databaseToListAndSet(pi.udis, pi.udisSet, rs.getString(4));
				databaseToListAndSet(pi.constants, pi.constantsSet,
						rs.getString(5));
				databaseToListAndSetForSChar(pi.specialChars, pi.specialCharsSet,
						rs.getString(6));
				// rs.getInt(2);
				// rs.getInt(3);
				// rs.getInt(4);
				//
				// rs.getInt(5);
				// rs.getInt(6);
				// rs.getInt(7);
				// rs.getInt(8);
				// rs.getInt(9);
				//
				// rs.getString(10); //keyset
				// databaseToListAndSet(pi.keywords, pi.keywordsSet,
				// rs.getString(12)); //key
				// databaseToListAndSet(pi.udis, pi.udisSet, rs.getString(14));
				// databaseToListAndSet(pi.constants, pi.constantsSet,
				// rs.getString(16));
				// databaseToListAndSet(pi.specialChars, pi.specialCharsSet,
				// rs.getString(18));
				// rs.getString(12); //uSet
				// rs.getString(13); //u
				// rs.getString(14); // cSet
				// rs.getString(15); // c
				// rs.getString(16); //sSet
				// rs.getString(17); //s

				dlm.addElement(pi);
				System.out.println("-------in getAllPackge() method ------");
				System.out.println(pi.getString());
			}
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dlm;
	}

	private void databaseToListAndSetForSChar(ArrayList al, LinkedHashSet lhs, String s) {
		// ArrayList<String> al = new ArrayList<String>();
		//		System.out.println("in dbapater " + s);
		StringBuilder sb = new StringBuilder(s);
		sb.deleteCharAt(0);
		sb.deleteCharAt(sb.length() - 1);
		s = sb.toString();
		
		s = s.replace(" ", "");
		//String[] sArray = s.split(",");
		//		for (String str : sArray) {
		//			str = str.trim();
		//
		////			System.out.println("Foreach str: " + str);
		//			al.add(str);
		//			lhs.add(str);
		//		}
		for(int i=0;i<s.length();i++) {
			if(i%2 == 0) {
				al.add(s.charAt(i));
				lhs.add(s.charAt(i));
			}
		}
	}
	
	private void databaseToListAndSet(ArrayList al, LinkedHashSet lhs, String s) {
		// ArrayList<String> al = new ArrayList<String>();
		//		System.out.println("in dbapater " + s);
		StringBuilder sb = new StringBuilder(s);
		sb.deleteCharAt(0);
		sb.deleteCharAt(sb.length() - 1);
		s = sb.toString();
		s = s.replace(" ", "");
		String[] sArray = s.split(",");
				for (String str : sArray) {
					str = str.trim();
		
		//			System.out.println("Foreach str: " + str);
					al.add(str);
					lhs.add(str);
				}
//		for(int i=0;i<s.length();i++) {
//			if(i%2 == 0) {
//				al.add(s.charAt(i));
//				lhs.add(s.charAt(i));
//			}
//		}
	}

	public DefaultListModel getClassNameByPackage(String packageName) {
		DefaultListModel dlm = new DefaultListModel();
		try {
			c = DriverManager.getConnection(CONNECTION_NAME);
			selectClassByPackagePS = c
					.prepareStatement(SELECT_CLASS_BY_PACKAGE);
			selectClassByPackagePS.setString(1, packageName);
			ResultSet rs = selectClassByPackagePS.executeQuery();

			// rs.first();
			while (rs.next()) {
				ClassInfo ci = new ClassInfo();
				// pName
				ci.packageName = rs.getString(2);
				ci.className = rs.getString(3);
				// rs.getInt(2);
				// rs.getInt(3);
				// rs.getInt(4);
				//
				// rs.getInt(5);
				// rs.getInt(6);
				// rs.getInt(7);
				// rs.getInt(8);
				// rs.getInt(9);
				//
				// rs.getString(10); //keyset
				databaseToListAndSet(ci.keywords, ci.keywordsSet,
						rs.getString(4)); // key
				databaseToListAndSet(ci.udis, ci.udisSet, rs.getString(5));
				databaseToListAndSet(ci.constants, ci.constantsSet,
						rs.getString(6));
				databaseToListAndSetForSChar(ci.specialChars, ci.specialCharsSet,
						rs.getString(7));
				
				
				PreparedStatement ps = c.prepareStatement(SELECT_CCM_DATA);
				ps.setString(1, ci.packageName);
				ps.setString(2, ci.className);
				ResultSet rs2 = ps.executeQuery();
				while(rs2.next()) {
					ci.addCycloInfo(rs2.getString(4), rs2.getInt(5));
				}
				
				dlm.addElement(ci);
			}
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dlm;
	}

	// final String UPDATE_CLASS_INFO =
	// "insert into class (package_name, class_name, "
	// +
	// "unique_keyword_count, unique_udi_count, unique_constant_count, unique_special_char_count,"
	// +
	// " total_keyword_count, total_udi_count, total_constant_count, total_special_char_count, "
	// +
	// "unique_keyword_list, total_keyword_list, unique_udi_list, total_udi_list, "
	// +
	// "unique_constant_list, total_constant_list, unique_special_char_list, total_special_char_list)"
	// + " values ( ?, ?, ?, ?, ?,"
	// + " ?, ?, ?, ?, ?,"
	// + " ?, ?, ?, ?, ?,"
	// + " ?, ?, ?);";
	//
	final String UPDATE_CLASS_INFO = "update class "
			+ "set total_keyword_list = ?, total_udi_list = ?, total_constant_list = ?, total_special_char_list = ? "
			+ "where package_name = ? and class_name = ?";
	
	

	/*
	 * UPDATE table_name SET column1 = value1, column2 = value2...., columnN =
	 * valueN WHERE [condition];
	 */

	public void updateDuplicateClass(ClassInfo ci) {
		try {
			c = DriverManager.getConnection(CONNECTION_NAME);
			PreparedStatement ps = c.prepareStatement(UPDATE_CLASS_INFO);
			ps.setString(1, ci.keywords.toString());
			ps.setString(2, ci.udis.toString());
			ps.setString(3, ci.constants.toString());
			ps.setString(4, ci.specialChars.toString());

			ps.setString(5, ci.packageName);
			ps.setString(6, ci.className);

			// insertPackageInfoPreparedStatement.setInt(2,
			// pi.keywordsSet.size());
			// insertPackageInfoPreparedStatement.setInt(3, pi.udisSet.size());
			// insertPackageInfoPreparedStatement.setInt(4,
			// pi.constantsSet.size());
			//
			// insertPackageInfoPreparedStatement.setInt(5,
			// pi.specialCharsSet.size());
			// insertPackageInfoPreparedStatement.setInt(6, pi.keywords.size());
			// insertPackageInfoPreparedStatement.setInt(7, pi.udis.size());
			// insertPackageInfoPreparedStatement.setInt(8,
			// pi.constants.size());
			// insertPackageInfoPreparedStatement.setInt(9,
			// pi.specialChars.size());
			//
			// insertPackageInfoPreparedStatement.setString(10,
			// pi.keywordsSet.toString());
			// insertPackageInfoPreparedStatement.setString(11,
			// pi.keywords.toString());
			// insertPackageInfoPreparedStatement.setString(12,
			// pi.udisSet.toString());
			// insertPackageInfoPreparedStatement.setString(13,
			// pi.udis.toString());
			// insertPackageInfoPreparedStatement.setString(14,
			// pi.constantsSet.toString());
			//
			// insertPackageInfoPreparedStatement.setString(15,
			// pi.constants.toString());
			// insertPackageInfoPreparedStatement.setString(16,
			// pi.specialCharsSet.toString());
			// insertPackageInfoPreparedStatement.setString(17,
			// pi.specialChars.toString());
			ps.executeUpdate();
			
			insertCCMDataWithClassInfo(ci);

			c.close();

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
