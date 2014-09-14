import java.util.ArrayList;
public class ItemSet {
	/* This class serves both as the item sets in the parser generation and as 
	 * a representation for the entire grammar */

	public ArrayList<Production> items;
	public Action[] actions;

	public ItemSet (Production... prods) {
		items = new ArrayList<Production>();
		for (Production p : prods) {
			items.add (p);
		}
	}

	public ItemSet (ArrayList<Production> p) {
		items = p;
	}

	public ArrayList<Production> findRules (Symbol n) {
		/* Returns a list of all of the productions with n as their lhs.
		 * This admits terminals as input, but they will never be found. */
		ArrayList<Production> res = new ArrayList<Production>();
		for (Production p : items) {
			if (p.sym == n) {
				res.add (p);
			}
		}
		return res;
	}

	public boolean contains (Production p) {
		return find(p) != -1;
	}

	public int find (Production p) {
		for (int i=0; i<items.size(); i++) {
			if (items.get(i).equals (p)) return i;
		}
		return -1;
	}

	public boolean equals (ItemSet other) {
		if (items.size() != other.items.size()) return false;
		for (int i=0; i < items.size(); i++) {
			if (!other.contains (items.get(i))) return false;
		}
		return true;
	}

	public ItemSet closure (ItemSet grammar) {
		ItemSet res = new ItemSet ();
		res.items.addAll (items);
		for (int i=0; i < res.items.size(); i++) {
			ArrayList<Production> newrules = grammar.findRules (res.items.get(i).getDP());
			for (Production p : newrules) {
				if (!res.items.contains(p)) {
					res.items.add (p);
				}
			}
		}
		return res;
	}

	public ItemSet getSubsetStartingWith (Symbol s) {
		ItemSet res = new ItemSet();
		for (Production p : items) {
			if (p.getDP() == s) {
				res.items.add (p.shiftDot());
			}
		}
		return res;
	}

	public ArrayList<ItemSet> getTransitionSets (ItemSet grammar) {
		ArrayList<ItemSet> res = new ArrayList<ItemSet>();
		ArrayList<Symbol> closed = new ArrayList<Symbol>();	// keeps track of which symbols we've seen
		for (Production p : items) {
			Symbol s = p.getDP();
			if (s == null) continue;
			if (!closed.contains (s)) {
				closed.add (s);
				res.add (this.getSubsetStartingWith(s).closure(grammar));
			}
		}
		return res;
	}

	public String toString () {
		StringBuffer sb = new StringBuffer();
		for (Production p : items) {
			sb.append (p + "\n");
		}
		return sb.toString();
	}

}

