public abstract class InternalNode extends Node {

	public InternalNode (Symbol s) {
		super (s);
	}

	public abstract Node getNextNode (Node s);
	public abstract Node getPreviousNode (Node s);

}
