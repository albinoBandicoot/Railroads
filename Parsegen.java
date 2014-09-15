import java.util.ArrayList;
import java.io.*;

public class Parsegen {

	public ItemSet grammar;
	public Production start;
	public ArrayList<ItemSet> isets;
	public ArrayList<Nonterminal> nonts;
	public ArrayList<Terminal> terms;
	public int nterms;
	public int tsize;

	public int precedence = Action.SHIFT;


	public Parsegen (ItemSet gram, Production s) {
		grammar = gram;
		start = s;
		isets = new ArrayList<ItemSet>();
		isets.add (new ItemSet (start).closure(grammar));
		nonts = new ArrayList<Nonterminal>();
		terms = new ArrayList<Terminal>();
	}

	public int find (ItemSet is) {
		for (int i=0; i < isets.size(); i++) {
			if (isets.get(i).equals (is)) return i;
		}
		return -1;
	}

	public Symbol symbol (int i) {
		if (i < 0 || i >= tsize) return null;
		if (i < nterms) return terms.get(i);
		return nonts.get (i-nterms);
	}

	public void findSymbols () {
		nonts.clear();
		terms.clear();
		terms.add (Terminal.EOF);
		for (Production p : grammar.items) {
			if (!nonts.contains (p.sym)) {
				p.sym.id = nonts.size();
				nonts.add ((Nonterminal) p.sym);
			}
			for (Symbol s : p.rule) {
				if (s instanceof Terminal) {
					if (!terms.contains (s)) {
						s.id = terms.size();
						terms.add ((Terminal) s);
					}
				} else if (s instanceof Nonterminal) {
					if (!nonts.contains (s)) {
						s.id = nonts.size();
						nonts.add ((Nonterminal) s);
					}
				}
			}
		}
		nterms = terms.size();
		tsize = nterms + nonts.size();
		/* The ID numbers will correspond to column numbers in the action table */
		for (Nonterminal n : nonts) {
			n.id += nterms;
		}
		System.out.println ("Found " + nterms + " terminals and " + nonts.size() + " nonterminals");
	}

	public void generateTable () {
		findSymbols();
		/* The first column of the action table will be for the special $ (EOF) symbol.
		 * The next columns will be for the terminals, then for the nonterminals.
		 * Each row corresponds to an item set, and is stored as an array of actions
		 * within the item set. */
		for (int i = 0; i < isets.size(); i++) {
			ItemSet cur = isets.get(i);
			System.out.println ("Looking at item set " + i + ":\n" + cur);
			ArrayList<ItemSet> tr = cur.getTransitionSets(grammar);
			cur.actions = new Action [nterms + nonts.size()];
			int w = 1;
			for (ItemSet is : tr) {
				// the symbol to the left of the dot in the first item in 'is' 
				// is where we came from; that is, the position of the table we need to update.
				int idx = find (is);	// if it's already here, we want to use a reference to the old one
				if (idx == -1) {
					idx = isets.size();
					isets.add (is);
					w++;
				}
				Symbol s = is.items.get(0).getLeft();
				cur.actions[s.id] = new Action (s.id < nterms ? Action.SHIFT : Action.GOTO, idx);
			}
		}

		/* Now we convert the transition table into the real action table.
		 * - The nonterminal goto section is just left as is.
		 * - The terminal shift actions are left as is.
		 * - Whenever there is a rule A -> asdf . in an item set with a dot at the end, it's row of the
		 *   	table is filled entirely with REDUCE k, where k is the # of the production A -> asdf
		 *   	in the grammar
		 * - The $ EOF symbol is introduced. If an item set contains S -> asdf . then in its row,
		 *   	ACCEPT is placed under $.
		*/
		for (int j=0; j < isets.size(); j++) {
			ItemSet is = isets.get(j);
			int rct = 0;
			for (Production p : is.items) {
				if (p.atEnd()) {
					if (p.sym == start.sym) {
						is.actions[0] = new Action (Action.ACCEPT, 0);
					} else {
						rct ++;
						int idx = grammar.find (p);
						for (int i=0; i < nterms; i++) {
							if (is.actions[i] != null) {
								if (is.actions[i].type == Action.SHIFT) { 
									System.out.println ("Shift-Reduce conflict for item set " + j + ", on symbol " + symbol(i) + ", with shift action " + is.actions[i] + " and reduce r" + idx);
								} else if (is.actions[i].type == Action.REDUCE) {
									System.out.println ("Reduce-Reduce conflict for item set " + j + "; " + is.actions[j] + " conflicting with r" + idx);
									break;	// no need to spit out that error for every column.
								}
							}
							is.actions[i] = new Action (Action.REDUCE, idx);
						}
					}
				}
			}
		}


		/* Print the table */
		int[] namelens = new int[nterms + nonts.size()];
		System.out.print ("#  ");
		for (int i=0; i<terms.size(); i++) {
			System.out.print (pad ("|" + terms.get(i).name, 4));
			namelens[i] = Math.max (4, terms.get(i).name.length()+1);
		}
		for (int i=0; i <nonts.size(); i++) {
			System.out.print (pad ("|" + nonts.get(i).name, 4));
			namelens[i+nterms] = Math.max (4,nonts.get(i).name.length()+1);
		}
		System.out.println();
		for (int j = 0; j < isets.size(); j++) {
			System.out.print (j + (j < 10 ? "  " : " "));
			ItemSet is = isets.get(j);
			for (int i = 0; i < is.actions.length; i++) {
				if (is.actions[i] == null) {
					System.out.print(pad ("|", namelens[i]));
				} else {
					System.out.print (pad("|" + is.actions[i], namelens[i]));
				}
			}
			System.out.println();
		}
	}

	private static String pad (String s, int len) {
		if (s.length() >= len) return s;
		int ns = len - s.length();
		StringBuffer sb = new StringBuffer (s);
		for (int i=0; i < ns; i++) sb.append (" ");
		return sb.toString();
	}

	public static void main (String[] args) throws IOException {
		Nonterminal S = new Nonterminal ("S");
		Nonterminal E = new Nonterminal ("E");
		Nonterminal B = new Nonterminal ("B");
		Terminal zero = new Terminal ("0");
		Terminal one = new Terminal ("1");
		Terminal plus = new Terminal ("+");
		Terminal times = new Terminal ("*");
		ItemSet grammar = new ItemSet ();
		grammar.items.add (new Production (S, E));
		grammar.items.add (new Production (E, E, times, B));
		grammar.items.add (new Production (E, E, plus, B));
		grammar.items.add (new Production (E, B));
		grammar.items.add (new Production (B, one));
		grammar.items.add (new Production (B, zero));

		System.out.println ("The grammar is " + grammar);
		Parsegen pg = new Parsegen (grammar, grammar.items.get(0));
		pg.generateTable();

		JavaWriter jw = new JavaWriter ();
		jw.write (pg, new File ("./"));

	}


}
