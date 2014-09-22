import java.util.*;
public class Nonterminal extends Symbol {

	public HashSet <Terminal> firsts;
	public Node definition;
	public boolean autogen;

	public Nonterminal (String n) {
		super (n);
		firsts = null;
		definition = null;
		autogen = 0;
	}

	public Nonterminal (String s, Node def) {
		super (s);
		firsts =null;
		definition = def;
		autogen = 0;
	}

	public void computeFirsts () {
	}

	public String toString () {
		return name.toUpperCase();
	}
}

