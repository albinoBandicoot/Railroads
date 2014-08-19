public class Terminal extends Symbol {

	public String regex;
	public boolean keyword;

	public Terminal (String n) {
		super(n);
	}

	public Terminal (String n, String regex, boolean keyword) {
		super (n);
		this.regex = regex;
		this.keyword = keyword;
	}

	public String toString () {
		return name.toLowerCase();
	}

}
