public class Token {

	public String name;
	public int id;
	public String text;

	public Token (String name, String text, int id) {
		this.name = name;
		this.text = text;
		this.id = id;
	}

	public String toString () {
		return name + " [" + text + "]";
	}
}
