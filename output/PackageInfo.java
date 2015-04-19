import java.util.ArrayList;
import java.util.LinkedHashSet;


public class PackageInfo {
	String packageName = "";
	String className = "";
	String lt = "";
	ArrayList<String> keywords = new ArrayList<String>();
	ArrayList<String> udis = new ArrayList<String>();
	ArrayList<String> constants = new ArrayList<String>();
	ArrayList<String> specialChars = new ArrayList<String>();
	LinkedHashSet<String> keywordsSet = new LinkedHashSet<String>();
	LinkedHashSet<String> udisSet = new LinkedHashSet<String>();
	LinkedHashSet<String> constantsSet = new LinkedHashSet<String>();
	LinkedHashSet<String> specialCharsSet = new LinkedHashSet<String>();
	
	@Override
	public String toString() {
		return packageName;
	}
	
	public String getString() {
		StringBuilder sb= new StringBuilder();
		sb.append("package " + packageName);
		sb.append("\n");
		sb.append("\tunique keywords: " + keywordsSet.size() + "  //" + keywordsSet.toString().substring(1, keywordsSet.toString().length()-1));
		sb.append("\n");
		sb.append("\tunique UDIs: " + udisSet.size() + "  //" + udisSet.toString().substring(1, udisSet.toString().length()-1));
		sb.append("\n");
		sb.append("\tunique constants: " + constantsSet.size() + "  //" + constantsSet.toString().substring(1, constantsSet.toString().length()-1));
		sb.append("\n");
		sb.append("\tunique special chars: " + specialCharsSet.size() + "  //" + specialCharsSet.toString().substring(1, specialCharsSet.toString().length()-1));
		sb.append("\n");
		sb.append("\ttotal keywords: " + keywords.size() + "  //" + keywords.toString().substring(1, keywords.toString().length()-1));
		sb.append("\n");
		sb.append("\ttotal UDIs: " + udis.size() + "  //" + udis.toString().substring(1, udis.toString().length()-1));
		sb.append("\n");
		sb.append("\ttotal constants: " + constants.size() + "  //" + constants.toString().substring(1, constants.toString().length()-1));
		sb.append("\n");
		sb.append("\ttotal special chars: " + specialChars.size() + "  //" + specialChars.toString().substring(1, specialChars.toString().length()-1));
		sb.append("\n");
		return sb.toString();
	}
	
	public String getStringSimple() {
		StringBuilder sb= new StringBuilder();
		sb.append("package " + packageName);
		sb.append("\n");
		sb.append("\tunique keywords: " + keywordsSet.size() + "  //" + keywordsSet.toString().substring(1, keywordsSet.toString().length()-1));
		sb.append("\n");
		sb.append("\tunique UDIs: " + udisSet.size() + "  //" + udisSet.toString().substring(1, udisSet.toString().length()-1));
		sb.append("\n");
		sb.append("\tunique constants: " + constantsSet.size() + "  //" + constantsSet.toString().substring(1, constantsSet.toString().length()-1));
		sb.append("\n");
		sb.append("\tunique special chars: " + specialCharsSet.size() + "  //" + specialCharsSet.toString().substring(1, specialCharsSet.toString().length()-1));
		sb.append("\n");
		sb.append("\ttotal keywords: " + keywords.size());
		sb.append("\n");
		sb.append("\ttotal UDIs: " + udis.size());
		sb.append("\n");
		sb.append("\ttotal constants: " + constants.size());
		sb.append("\n");
		sb.append("\ttotal special chars: " + specialChars.size());
		sb.append("\n");
		return sb.toString();
	}
	
	public void cleanAll() {
		keywords.clear();
		keywordsSet.clear();
		constants.clear();
		constantsSet.clear();
		udis.clear();
		udisSet.clear();
		specialChars.clear();
		specialCharsSet.clear();
	}
	
	
	
}
