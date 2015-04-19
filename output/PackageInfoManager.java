import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class PackageInfoManager {
	Map<String, PackageInfo> packageInfoMap;
	int i;
	public PackageInfoManager() {
		packageInfoMap = new LinkedHashMap<String, PackageInfo>();
		
	}

	public void update(ClassInfo c) {
		if(packageInfoMap.containsKey(c.packageName)) {
			System.out.println("Update existing package. ");
			PackageInfo pi = packageInfoMap.get(c.packageName);
			if(c.pi.keywords.size() != 0) {
				pi.keywords.addAll(c.pi.keywords);
				pi.keywordsSet.addAll(c.pi.keywordsSet);
				pi.specialChars.addAll(c.pi.specialChars);
				pi.specialCharsSet.addAll(c.pi.specialChars);
			}

			pi.constants.addAll(c.constants);
			pi.constantsSet.addAll(c.constantsSet);
			pi.udis.addAll(c.udis);
			pi.udisSet.addAll(c.udisSet);
			pi.keywords.addAll(c.keywords);
			pi.keywordsSet.addAll(c.keywordsSet);
			pi.specialChars.addAll(c.specialChars);
			pi.specialCharsSet.addAll(c.specialCharsSet);

		} else {
			System.out.println("Creating a new package.");
			PackageInfo newInfo = new PackageInfo();
			newInfo.packageName = c.packageName;

			newInfo.keywords.addAll(c.pi.keywords);
			newInfo.keywordsSet.addAll(c.pi.keywordsSet);

			newInfo.specialChars.addAll(c.pi.specialChars);
			newInfo.specialCharsSet.addAll(c.pi.specialChars);


			newInfo.constants.addAll(c.constants);
			newInfo.constantsSet.addAll(c.constantsSet);
			newInfo.udis.addAll(c.udis);
			newInfo.udisSet.addAll(c.udisSet);
			newInfo.keywords.addAll(c.keywords);
			newInfo.keywordsSet.addAll(c.keywordsSet);
			newInfo.specialChars.addAll(c.specialChars);
			newInfo.specialCharsSet.addAll(c.specialCharsSet);
			packageInfoMap.put(newInfo.packageName, newInfo);
		}	
	}

	public void update(PackageInfo p) {
		packageInfoMap.put(p.packageName, p);
		System.out.println("on updating from database: ");
		System.out.println(p.constants.toString());
	}

	public void clear() {
		packageInfoMap.clear();
	}

	public Iterator iterator() {
		return packageInfoMap.entrySet().iterator();
	}

}
