public class Action {

	public static final int ERROR = 0;
	public static final int SHIFT = 1;
	public static final int REDUCE = 2;
	public static final int GOTO = 3;
	public static final int ACCEPT = 4;

	public int type;
	public int num;

	public Action (int t, int n) {
		type = t;
		num = n;
	}

	public String toString () {
		switch (type) {
			case ERROR:	return "err";
			case SHIFT: return "s" + num;
			case REDUCE: return "r" + num;
			case GOTO: return "g" + num;
			case ACCEPT: return "acc";
		}
		return "???";
	}
}
