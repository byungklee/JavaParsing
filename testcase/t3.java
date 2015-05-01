public class T3 {
	T3() {}
	public int f1(int a, int b, double c) {
		T3 temp = new T3();
		return 4;
	}

	public int f2(int a, int b, double c) {
		int i = 4;
		int j;
		j = i + 5;
		int c = f1(i,j,3);
		return j;
	}
}