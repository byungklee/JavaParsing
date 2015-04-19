import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class ClassInfoManager {
	Map<String, ClassInfo> classInfoMap;
	public ClassInfoManager() {
		classInfoMap = new LinkedHashMap<String, ClassInfo>();
	}
	
	public void update(ClassInfo c) {
		classInfoMap.put(c.packageName + "." + c.className , c);
	}
	
	public void clear() {
		classInfoMap.clear();
	}
	public Iterator iterator() {
		return classInfoMap.entrySet().iterator();
	}
	
}
