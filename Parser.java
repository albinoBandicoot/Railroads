import java.util.*;
import java.io.*;
public class Parser {

	private int[][] table;
	/* Encoding for the table:
	 * Each entry has its most significant 2 bits corresponding to the type of action to take:
	 * 00 - ERROR (the rest of the int will also be 0 in this case)
	 * 01 - SHIFT (bits 29..0 are the new state)
	 * 10 - REDUCE (bits 29..0 are the rule to apply)
	 * 11 - GOTO (bits 29..0 are the new state to go to)
	*/
	private int[] npop;	// number of trees to pop for each production in the grammar
	private int[] prodsyms;	// one element for each production. Stores the id of the symbol on the lhs.

	private Stack<Integer> state;
	private Stack<Tree> trees;

	private int nterms, nnonts;
	
	private Lexer lexer;

	private static final int ACCEPT = 1 << 31;	// reduce according to 0, which is S -> oldstart 

	public Parser (Parsegen pg, File lexfile) throws IOException {
		/* ASSUMES THE PG HAS RUN */
		table = new int[pg.isets.size()][pg.tsize];
		for (int i=0; i < table.length; i++) {
			for (int j=0; j < table[0].length; j++) {
				Action act = pg.isets.get(i).actions[j];
				if (act.type == Action.ACCEPT) {
					table[i][j] = ACCEPT;
				} else {
					table[i][j] = (act.type << 30) | (act.num & 0x3fffffff);
				}
			}
		}
		npop = new int[pg.grammar.items.size()];
		prodsyms = new int[npop.length];
		int i = 0;
		for (Production p : pg.grammar.items) {
			npop[i] = p.rule.size();
			prodsyms[i] = p.sym.id;
			i++;
		}

		state = new Stack<Integer>();
		state.push (0);
		trees = new Stack<Tree>();

		lexer = new Lexer (lexfile);
	}

	public Parser (File tableFile, File lexfile) throws IOException {
		DataInputStream inp = new DataInputStream (new FileInputStream (tableFile));
		int nprods = inp.readInt();
		int nrows = inp.readInt();
		nterms = inp.readInt();
		nnonts = inp.readInt();
		int ncols = nterms + nnonts;
		npop = new int[nprods];
		prodsyms = new int[nprods];
		for (int i=0; i < nprods; i++) {
			npop[i] = inp.readInt();
			prodsyms[i] = inp.readInt();
		}
		table = new int[nrows][ncols];
		for (int r=0; r < nrows; r++) {
			System.out.print (r + "  ");
			for (int c = 0; c < ncols; c++) {
				table[r][c] = inp.readInt();
				System.out.print (new Action(table[r][c]) + " ");
			}
			System.out.println();
		}
		inp.close();
		state = new Stack<Integer>();
		state.push (0);
		trees = new Stack<Tree>();

		lexer = new Lexer (lexfile);
	}

	public void printStateStack () {
		System.out.print ("[");
		for (int i=0; i < state.size(); i++) {
			System.out.print (state.get(i) + " ");
		}
		System.out.println("]");
	}

	public Tree parse (String s) throws LexerException, ParserException  {
		ArrayList<Token> tokens = lexer.tokenize (s);
		tokens = ParseUtils.filter (tokens);

		int i = 0;
		while (i < tokens.size()) {
			Token t = tokens.get(i);
			System.out.println ("Looking at " + t + "; state = " + state.peek());

			int col = t.term.id;
			int action = table[state.peek()][col];
			if (action == ACCEPT) {
			}
			int type = (action >> 30) & 3;
			int num  = action & 0x3fffffff;
			System.out.println ("Found action type = " + type + "; num = " + num);
			System.out.print ("The current state stack is: ");
			printStateStack ();

			switch (type) {
				case Action.ERROR:
					throw new ParserException ();
				case Action.SHIFT:
					// encapsulate the token in a tree
					Tree tr = new Tree (Treetype.TOKEN, t);
					trees.push (tr);
					i++;
					state.push (num);
					break;
				case Action.REDUCE:
					// make a call to parseUtils to nicely construct a tree.
					System.out.println ("Reducing to symbol " + prodsyms[num] + " via rule " + num + "; popping " + npop[num] + " trees from the stack.");
					ArrayList<Tree> children = new ArrayList<Tree>();
					for (int j=0; j < npop[num]; j++) {
						children.add (0, trees.pop());
						state.pop();
					}
					trees.push (ParseUtils.reduce (prodsyms[num], children));
					//state.pop();
					if (prodsyms[num] == nterms) {	// then we just reduced to the start symbol.
						if (trees.size() == 1) {
							return trees.get(0);
						} else {
							throw new ParserException ("Should have only 1 tree on stack at ACCEPT state; I have " + trees.size());
						}
					}

					// now we follow the goto.
					System.out.print ("Followed goto at state " + state.peek() + " to ");
					state.push (table[state.peek()][prodsyms[num]] & 0x3fffffff);
					System.out.println (state.peek());
					break;
					
				case Action.GOTO:
					state.pop();
					state.push (table[state.peek()][prodsyms[num]] & 0x3fffffff);
			}
		}
		throw new ParserException ("Reached end of file before hitting ACCEPT");
	}

	public static void main (String[] args) throws ParserException, IOException, LexerException {
		Parser p = new Parser (new File ("parsetable"), new File ("lexfile"));
		p.parse ("1+0*1").print();
	}

}
