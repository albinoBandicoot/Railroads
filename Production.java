import java.util.ArrayList;
public class Production {

	public Symbol sym;
	public ArrayList<Symbol> rule;
	public int dp;	// dot position; ignore if not necessary

	/*
	public Production (Symbol s) {
		sym = s;
		rule = new ArrayList<Symbol>();
	}
	*/

	public Production (Production p) {	// copy constructor
		sym = p.sym;
		rule = new ArrayList<Symbol>();
		rule.addAll (p.rule);
		dp = p.dp;
	}

	public Production (Symbol s, Symbol... syms) {
		sym = s;
		rule = new ArrayList<Symbol>();
		for (Symbol symbol : syms) {
			rule.add (symbol);
		}
		dp = 0;
	}

	public Production (Symbol s, int dp, Symbol... syms) {
		this (s, syms);
		this.dp = dp;
	}

	public Production shiftDot () {
		Production res = new Production (this);
		res.dp++;
		return res;
	}

	public void add (Symbol... syms) {
		for (Symbol s : syms) {
			rule.add (s);
		}
	}

	public Symbol getLeft () {	// gets the symbol to the left of the dot.
		if (dp > 0 && dp <= rule.size()) {
			return rule.get (dp-1);
		}
		return null;
	}

	public Symbol getDP () {
		if (dp >= 0 && dp < rule.size()) {
			return rule.get(dp);
		}
		return null;
	}

	public boolean atEnd () {
		return dp == rule.size();
	}

	public boolean equals (Production other) {
		if (sym != other.sym) return false;
	//	if (dp != other.dp) return false;
		if (rule.size() != other.rule.size()) return false;
		for (int i=0; i<rule.size(); i++) {
			if (rule.get(i) != other.rule.get(i)) return false;
		}
		return true;
	}

	public String toString () {
		StringBuffer sb = new StringBuffer (sym.name.toUpperCase() + " ::= ");
		if (rule.isEmpty()) {
			sb.append ("< empty >");
		} else {
			for (int i = 0; i < rule.size(); i++) {
				if (i == dp) sb.append (" . ");
				sb.append (rule.get(i) + " ");
			}
			if (dp == rule.size()) sb.append (" . ");
		}
		return sb.toString();
	}

}
