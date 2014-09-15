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

	public Action (int x) {
		type = (x >> 30) & 3;
		num = x & 0x3fffffff;
	}

	public int getInt () {
		if (type == ACCEPT) return 1 << 31;
		if (type == ERROR) return 0;
		return (type << 30) | (num & 0x3fffffff);
	}

	public String toString () {
		String pad = num < 10 ? " " : "";
		switch (type) {
			case ERROR:	return "err";
			case SHIFT: return "s" + num + pad;
			case REDUCE: return "r" + num + pad;
			case GOTO: return "g" + num + pad;
			case ACCEPT: return "acc";
		}
		return "???";
	}
}
