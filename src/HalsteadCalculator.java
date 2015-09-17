
public class HalsteadCalculator {
	//N = N1 + N2
	public static int calcProgramLength(int n1, int n2) {
		return n1 + n2;
	}
	
	//n = n1 + n2
	public static int calcProgramVocabulary(int n1, int n2) {
		return n1 + n2;
	}
	
	//Volume(V) = N * Log2 n 
	public static double calcVolume(int N, int n) {
		return (double) N * (Math.log(n)/Math.log(2));
	}
	
	//Difficulty (D) = (n1/2) * (N2/n2)
	public static double calcDifficulty(int n1, int N2, int n2) {
		return (n1/2d) * (N2/(float)n2);
	}
	
	//Effort (E) = D * V
	public static double calcEffort(double v, double d) {
		return v*d;
	}
	
	//Time (T) = E / 18 (sec)
	public static double calcTime(double E) {
		return E/18;
	}
	
	//Number of Bugs(B) = v/3000
	public static double calcNumBugs(double v) {
		return v/3000d;
	}
	
}
