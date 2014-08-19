import java.util.*;
public class Nonterminal extends Symbol {

	public HashSet <Terminal> firsts;
	public Node definition;

	public Nonterminal (String n) {
		super (n);
		firsts = null;
		definition = null;
	}

	public Nonterminal (String s, Node def) {
		super (s);
		firsts =null;
		definition = def;
	}

	public void computeFirsts () {
	}

	public String toString () {
		return name.toUpperCase();
	}
}

