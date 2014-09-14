public class Terminal extends Symbol {

	public String regex;

	public static final Terminal EOF = new Terminal ("$");

	public Terminal (String n) {
		super(n);
	}

	public Terminal (String n, String regex) {
		super (n);
		this.regex = regex;
	}

	public String toString () {
		return name.toLowerCase();
	}

}
