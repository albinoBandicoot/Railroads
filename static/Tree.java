import java.util.ArrayList;
public class Tree {

	public ArrayList<Tree> children;
	public Treetype type;
	public Object data;

	public Tree (Treetype t) {
		type = t;
		children = new ArrayList<Tree>();
		data =null;
	}

	public Tree (Treetype t, Token tok) {
		type = t;
		data = tok;
		children = new ArrayList<Tree>(0);
	}

	public Tree (Treetype t, ArrayList<Tree> children, Object data) {
		type = t;
		this.children = children;
		this.data = data;
	}

	public Tree (Treetype t, ArrayList<Tree> children) {
		type = t;
		this.children = children;
		data = null;
	}

	public String toString () {
		return type + ": " + (data != null ? data.toString() : "");
	}

	private void printhelper (int depth) {
		for (int i=0; i < depth; i++) {
			System.out.print ("   ");
		}
		System.out.println (this.toString());
		if (children == null) return;
		for (Tree ch : children) {
			ch.printhelper (depth+1);
		}
	}

	public void print () {
		printhelper (0);
	}
}
