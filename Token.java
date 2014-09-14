public class Token {

	public Terminal term;
	public String text;

	public Token (Terminal t, String s) {
		term = t;
		text = s;
	}

	public String toString () {
		return term.name + " [" + text + "]";
	}
}
