import java.util.ArrayList;
public class Production {

	public Symbol sym;
	public ArrayList<Symbol> rule;

	/*
	public Production (Symbol s) {
		sym = s;
		rule = new ArrayList<Symbol>();
	}
	*/

	public Production (Symbol s, Symbol... syms) {
		sym = s;
		rule = new ArrayList<Symbol>();
		for (Symbol symbol : syms) {
			rule.add (symbol);
		}
	}

	public void add (Symbol... syms) {
		for (Symbol s : syms) {
			rule.add (s);
		}
	}

	public String toString () {
		StringBuffer sb = new StringBuffer (sym.name.toUpperCase() + " ::= ");
		if (rule.isEmpty()) {
			sb.append ("< empty >");
		} else {
			for (Symbol s : rule) {
				sb.append (s + " ");
			}
		}
		return sb.toString();
	}

}
