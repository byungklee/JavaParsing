import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;


public class MainController {
	private static MainView view;
	private PackageInfoManager packageInfoManager;
	private ClassInfoManager classInfoManager;
	private DbAdapter dbAdapter;
	private ArrayList<String> duplicateClassNames;

	public MainController() {
		view = new MainView(this);
		view.setVisible(true);
		classInfoManager = new ClassInfoManager();
		packageInfoManager = new PackageInfoManager();
		dbAdapter = new DbAdapter();
		setDuplicateClassNames(new ArrayList<String>());
	}

	public void executeParse() {
		File[] files = openFileSelector(true);
		if(files == null) {
			return;
		}
		classInfoManager.clear();
		packageInfoManager.clear();
		
		for(File f: files) {
			ClassInfo c = parse(f.getAbsolutePath());
			
			if(c.packageName.equals("")) {
				System.out.println("PackageName is Emtpy");
				c.packageName = "default";
			}
			
			packageInfoManager.update(c);
			
			//Class should only contain after Class Definition.
			//So anything above such as import and package are removed.
		
			classInfoManager.update(c);
			
			
		}
		System.out.println("SIZE: " + classInfoManager.classInfoMap.size() + " " + packageInfoManager.packageInfoMap.size());
		
		DefaultListModel packageListModel = new DefaultListModel();
		Iterator it = packageInfoManager.iterator();
		while(it.hasNext()) {
			Entry entry = (Entry) it.next();
			PackageInfo pi = (PackageInfo) entry.getValue();
			packageListModel.addElement(pi);
		}

		view.openParseView(packageListModel);
	}

	private File[] openFileSelector(boolean isMulti) {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				".java files", "java");
		chooser.setFileFilter(filter);
		if(isMulti)
			chooser.setMultiSelectionEnabled(true);

		//chooser.
		int returnVal = chooser.showOpenDialog(view);
		File[] files = null;
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			//System.out.println("You chose to open this file: " +
			//chooser.getSelectedFile().getName());
			if(isMulti)
				files = chooser.getSelectedFiles();
			else {
				files = new File[1];
				files[0] = chooser.getSelectedFile();
			}
				
			
			for(File f: files) {
				System.out.println(f.getName());

			}
		}
		return files;
	}

	private ClassInfo parse(String path) {
		JavaParser parser = null;
		File initialFile = new File(path);
		try {
			InputStream targetStream = new FileInputStream(initialFile);
			ANTLRInputStream input = new ANTLRInputStream(targetStream);
			new ANTLRInputStream();
			JavaLexer lexer = new JavaLexer(input, true);

			CommonTokenStream tokens = new CommonTokenStream(lexer);

			parser = new JavaParser(tokens);

			parser.compilationUnit();
//			System.out.println("***** PACKAGE *******");
//			System.out.println(parser.ci.packageName);
//			System.out.println("***** CLASS NAME ****");
//			System.out.println(parser.ci.className);
//			System.out.println("********** TOTAL ************");
//			System.out.println(parser.ci.keywords.toString() + " " + parser.ci.keywords.size());
//			System.out.println(parser.ci.udis.toString() + " " + parser.ci.udis.size());
//			System.out.println(parser.ci.constants.toString() + " " + parser.ci.constants.size());
//			System.out.println(parser.ci.specialChars.toString() + " " + parser.ci.specialChars.size());
//			System.out.println("********** Unique ************");
//			System.out.println(parser.ci.keywordsSet.toString() + " " + parser.ci.keywordsSet.size());
//			System.out.println(parser.ci.udisSet.toString() + " " + parser.ci.udisSet.size());
//			System.out.println(parser.ci.constantsSet.toString() + " " + parser.ci.constantsSet.size());
//			System.out.println(parser.ci.specialCharsSet.toString() + " " + parser.ci.specialCharsSet.size());
		}	catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return parser.ci;
	}
	
	public void openClassListAndDetailOfPackage(PackageInfo pInfo) {
		DefaultListModel classModel = new DefaultListModel();
		Iterator it = classInfoManager.iterator();
		while(it.hasNext()) {
			Entry e =(Entry) it.next();
			ClassInfo ci = (ClassInfo) e.getValue();
			if(pInfo.packageName.equals(ci.packageName)) {
				classModel.addElement(ci);
			}
		}
		
		 view.openClassAndDetailOfPackage(pInfo, classModel);
		 
		 view.validate();
	}
	
	public void openClassDetail(ClassInfo ci) {
		view.openClassDetail(ci);
	}
	
	public void saveCurrentInfoToSQL() {
		System.out.println("Save currentInfoToSql: " + classInfoManager.classInfoMap.size());
		dbAdapter.insertClassInfos(classInfoManager);
		//System.out.println("Save2 currentInfoToSql: " + classInfoManager.classInfoMap.size());
		//dbAdapter.insertCCMData(classInfoManager);
		dbAdapter.insertPackageInfos(packageInfoManager);
		System.out.println("Saving Done!");
	}
	
	public DefaultListModel getAllPackage() {
		return dbAdapter.getAllPackage();
	}
	
	public DefaultListModel getClassByPackage(PackageInfo info) {
		return dbAdapter.getClassNameByPackage(info.packageName);
	}
	
	public void openDisplayDatabase() {
		//Call all class.
		view.openDisplayDatabaseView(getAllPackage());
	}
	
	HashMap<String, ClassInfo> classesByPackage = new HashMap<>();
	
	//classesByPackage.clear();
	
	public void checkDuplicate() {
		Iterator it = packageInfoManager.iterator();
		HashMap<String,ArrayList> hm = new HashMap<String,ArrayList>();
		getDuplicateClassNames().clear();
		
		while(it.hasNext()) {
			Entry e = (Entry) it.next();
			PackageInfo pi = (PackageInfo) e.getValue();
			DefaultListModel m = dbAdapter.getClassNameByPackage(pi.packageName);
			
	//		ArrayList<String> al = new ArrayList<String>();
			for(int i=0;i<m.size();i++) {
				ClassInfo c = (ClassInfo) m.get(i);
				if(classInfoManager.classInfoMap.containsKey(pi.packageName + "." + c.className)) {
					classesByPackage.put(pi.packageName + "." + c.className, c);
				}
//				al.add(c.className);
			}
//			
//			hm.put(pi.packageName, al);	
		}
		
//		it = classInfoManager.iterator();
//		int j=0;
//		while(it.hasNext()) {
//			Entry e = (Entry) it.next();
//			ClassInfo ci = (ClassInfo) e.getValue();
//			ArrayList al = hm.get(ci.packageName);
//			if(al.contains(ci.className)) {
//				getDuplicateClassNames().add(ci.className);
//				//classesByPackage.put(ci.className, (ClassInfo) m.get(i));
//			}
//			j++;
//			//ci.className;
//		}
		//classInfoManager.classInfoMap
		//dbAdapter.getClassNameByPackage(packageName)
	}
	
	//Remove class info that has same name as whatever in database.
	public void removeDuplicates() {
		Iterator it = classInfoManager.iterator();
		while(it.hasNext()) {
			Entry e = (Entry) it.next();
			ClassInfo ci = (ClassInfo) e.getValue();
			if(classesByPackage.containsKey(ci.packageName+"."+ci.className)) {
				classInfoManager.classInfoMap.remove(ci.packageName+ "." +ci.className);
			}
		}
	}
	
	
	//Update Package Information by non duplicate classes
	public void updatePackageByNonDuplicateClass() {
		Iterator it = classInfoManager.iterator();
		while(it.hasNext()) {
			Entry e = (Entry) it.next();
			ClassInfo ci = (ClassInfo) e.getValue();
			if(!classesByPackage.containsKey(ci.packageName+"."+ci.className)) {
				//classInfoManager.classInfoMap.remove(ci.packageName+ "." +ci.className);
				
				if(loaded.containsKey(ci.packageName)) {
					System.out.println("Inserting non duplicate classes!");
					packageInfoManager.update(ci);
				}
			}
		}
	}
	HashMap<String,Boolean> loaded = new HashMap<>();
	//Load duplicated package into packgeInfoManager.
	public void loadPackages() {
		Iterator it = packageInfoManager.iterator();
		loaded.clear();
		while(it.hasNext()) {
			Entry e = (Entry) it.next();
			PackageInfo pi = (PackageInfo) e.getValue();
			PackageInfo pi2 = dbAdapter.getSelectedPackage(pi.packageName);
			if(pi2 != null) {
				loaded.put(pi.packageName, true);
				System.out.println("Existing package in database " + pi.packageName);
				System.out.println("--------------- ");
				System.out.println(pi.getString());
				pi2.packageName = pi.packageName;
				//packageInfoManager.update(pi2);
				packageInfoManager.packageInfoMap.remove(pi.packageName);
				packageInfoManager.packageInfoMap.put(pi.packageName, pi2);
				System.out.println(packageInfoManager.packageInfoMap.size());
			} 
		}
	}
	
	private void removeListByList(ArrayList al1, ArrayList al2) {
		System.out.println("removeListbyList");
//		System.out.println(al1.toString());
//		System.out.println(al2.toString());
		for(int i =0;i<al2.size();i++) {
			al1.remove(al2.get(i));
		}
//		System.out.println(al1.toString());
	}
	
	//Update duplications from existing data
	public void updateDuplicates() {
		Iterator it = classInfoManager.iterator();
		while(it.hasNext()) {
			Entry e = (Entry) it.next();
			ClassInfo ci = (ClassInfo) e.getValue();
			if(classesByPackage.containsKey(ci.packageName+"."+ci.className)) {
				//classInfoManager.classInfoMap.remove(ci.packageName+ "." +ci.className);
				//dbAdapter.updateDuplicateClass(ci);
				System.out.println("UPDATE DUPLICATE!!!");
				packageInfoManager.packageInfoMap.get(ci.packageName);
				ClassInfo ciOld = classesByPackage.get(ci.packageName+"."+ci.className);
				PackageInfo pi = packageInfoManager.packageInfoMap.get(ci.packageName);
				removeListByList(pi.constants, ciOld.constants);
				removeListByList(pi.udis, ciOld.udis);
				removeListByList(pi.keywords, ciOld.keywords);
				removeListByList(pi.specialChars, ciOld.specialChars);
				packageInfoManager.update(ci);
				dbAdapter.updateDuplicateClass(ci);
				//classInfoManager.update(classesByPackage.get(ci.packageName+"."+ci.className));
			}
		}
	}

	public void analyzeFile() {
		// TODO Auto-generated method stub
		File[] files = openFileSelector(false);
		if(files == null) {
			return;
		}
		for(File f: files) {
			parseFilelength(f.getAbsolutePath());
		}		
	}
	
	
	public static void parseFilelength(String path) {
		JavaParser parser = null;
		File initialFile = new File(path);
		try {
			InputStream targetStream = new FileInputStream(initialFile);
			ANTLRInputStream input = new ANTLRInputStream(targetStream);
			new ANTLRInputStream();
			JavaLexer lexer = new JavaLexer(input,false);
			

			CommonTokenStream tokens = new CommonTokenStream(lexer);

			int sumForTotal = 0;
			int commentCounter = 0;
			int whiteSpaceCounter = 0;
			
			tokens.reset();
			
			while(tokens.LT(1).getType() != JavaLexer.EOF) {
				System.out.println(tokens.LT(1).getText().length());
				if(tokens.LT(1).getType() == JavaLexer.WS) {
					whiteSpaceCounter += tokens.LT(1).getText().length();
				} else if(tokens.LT(1).getType() == JavaLexer.COMMENT || 
						tokens.LT(1).getType() == JavaLexer.LINE_COMMENT) {
					commentCounter+= tokens.LT(1).getText().length();
				}
				
				 
//				sum += tokens.LT(1).getText().length();
				System.out.println(tokens.LT(1).getText());
				sumForTotal += tokens.LT(1).getText().length();
				tokens.consume();
			}
			
//			if(tokens.LT(1).getType() == JavaLexer.EOF) {
//				System.out.println("EOF");
//			}
			
			//To include EOF
			sumForTotal += tokens.LT(1).getText().length();
			System.out.println("Last one " + tokens.LT(1).getText().length());
			
			System.out.println(tokens.size());
			
			StringBuilder sb = new StringBuilder();
			sb.append("Selected file: " + path);
			sb.append("\n");
			sb.append("Total chars in a file: " + sumForTotal);
			sb.append("\n");
			sb.append("Total whitespace in a file: " + whiteSpaceCounter);
			sb.append("\n");
			sb.append("Total comments in a file: " + commentCounter);
			sb.append("\n");
			sb.append("Overall Percentage of comment: " + ((float) commentCounter / (float) sumForTotal) * 100.00f + "%");
			sb.append("\n");
			sb.append("Overall Percentage of whitespace: " + ((float) whiteSpaceCounter / (float) sumForTotal) * 100.00f + "%");
			JOptionPane.showMessageDialog(view, sb.toString());
		}	catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	public ArrayList<String> getDuplicateClassNames() {
		return duplicateClassNames;
	}

	public void setDuplicateClassNames(ArrayList<String> duplicateClassNames) {
		this.duplicateClassNames = duplicateClassNames;
	}

	
}
