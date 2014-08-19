import java.util.ArrayList;
public class Test {

	public static void main (String[] args) {
		Nonterminal a = new Nonterminal ("A");
		Nonterminal b = new Nonterminal ("B");
		Nonterminal c = new Nonterminal ("C");
		Nonterminal d = new Nonterminal ("D");
		ConcatNode cn = new ConcatNode (d, new LeafNode (b), new LeafNode (c));
		LoopNode ln = new LoopNode (a, null, cn);
		ArrayList<Production> prods = ln.generateProductions();
		prods.addAll (cn.generateProductions());
		for (Production p : prods) {
			System.out.println (p);
		}
	}
}



