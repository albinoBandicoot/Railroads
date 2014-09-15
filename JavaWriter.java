import java.io.*;
public class JavaWriter extends Writer {

	public JavaWriter () {
	}

	public void write (Parsegen pg, File dir) throws IOException, FileNotFoundException{
		/* Things to write: 
		 * 
		 * parse table in compressed format
		 * for each production, how many trees to pop, and which symbol it has on its lhs
		 * Treetype enum file
		 * templates in parseUtils 
		*/

		File table_file = new File (dir.getCanonicalPath() + "/parsetable");
		File treetype_file = new File (dir.getCanonicalPath() + "/Treetype.java");
		File parseutils_file = new File (dir.getCanonicalPath() + "/ParseUtils.java");

		System.out.println ("Will write parsetable to " + table_file.getCanonicalPath());

		DataOutputStream table = new DataOutputStream (new FileOutputStream (table_file));
		PrintWriter treetype = new PrintWriter (treetype_file);
		PrintWriter parseutils = new PrintWriter (parseutils_file);

		/* First output the table 
		 * 	number of productions (int)
		 * 	number of table rows; that is, number of item sets (int)
		 * 	number of terminals
		 * 	number of nonterminals
		 *  for each production, (npop[i], prodsym[i]), 4 bytes each
		 *  #prods * (#terminals + #nonterminals) ints, the table entries.
		 */

		table.writeInt (pg.grammar.items.size());
		table.writeInt (pg.isets.size());
		table.writeInt (pg.nterms);
		table.writeInt (pg.nonts.size());

		for (Production p : pg.grammar.items) {
			table.writeInt (p.rule.size());
			table.writeInt (p.sym.id);
		}

		for (ItemSet is : pg.isets) {
			for (Action a : is.actions) {
				int act = a == null ? 0 : a.getInt();
				table.writeInt (act);
			}
		}
		table.close();

		/* Now output the Treetype enum file. This will have one treetype for each nonterminal,
		 * and one additional one called TOKEN. */
		treetype.println ("/* This file is computer generated. Be careful your edits aren't overwritten! */");
		treetype.println ("public enum Treetype {");
		treetype.print ("\tTOKEN");
		for (Nonterminal n : pg.nonts) {
			treetype.print (", " + n.name.toUpperCase());
		}
		treetype.println (";");
		treetype.println ("}");
		treetype.close();

		/* Now for the ParseUtils file */
		parseutils.println ("/* This file is computer generated. Be careful your edits aren't overwritten! */");
		parseutils.println ("import java.util.ArrayList;");
		parseutils.println ("public class ParseUtils {\n");

		parseutils.println ("\tpublic static ArrayList<Token> filter (ArrayList<Token> tokens) {");
		parseutils.println ("\t\treturn tokens;");
		parseutils.println ("\t}\n");

		parseutils.println ("\tpublic static Tree reduce (int lhs, ArrayList<Tree> children) throws ParserException {");
		parseutils.println ("\t\tswitch (lhs) {");
		for (Nonterminal n : pg.nonts) {
			parseutils.println ("\t\t\tcase " + n.id + ": // " + n.name);
			parseutils.println ("\t\t\t\treturn reduce_" + n.name + "(children);");
		}
		parseutils.println ("\t\t}");
		parseutils.println ("\t\tthrow new ParserException (\"Bad lhs symbol (\" + lhs + \") in reduction dispatcher\");");
		parseutils.println ("\t}\n");

		parseutils.println ("\tpublic static Tree defaultReduction (Treetype type, ArrayList<Tree> children) {");
		parseutils.println ("\t\treturn new Tree (type, children);");
		parseutils.println ("\t}\n");

		for (Nonterminal n : pg.nonts) {
			parseutils.println ("\tpublic static Tree reduce_" + n.name + " (ArrayList<Tree> children) {");
			parseutils.println ("\t\treturn defaultReduction (Treetype." + n.name.toUpperCase() + ", children);");
			parseutils.println ("\t}\n");
		}

		parseutils.println ("}");
		parseutils.close();
	}

}
