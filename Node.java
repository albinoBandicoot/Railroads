import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.event.*;
public abstract class Node {

	public Symbol n;
	public Rect bounds;
	public Rect globounds;	// global-space, 'normalized' rectangle (size positive)
	public Point pipe;
	public boolean is_selected;
	public Node parent;

	public Node () {
	}

	public Node (Symbol n) {
		this.n = n;
	}


	public abstract ArrayList <Production> generateProductions ();

	public abstract void computeBounds ();
	public abstract void paint (Graphics2D g, Point pipein);
	
	public abstract boolean  doSelection (Point p);	// update the is_selected flags to reflect a click on point p
	public void  setSelection (Node s) {
		is_selected = this == s;
	}
	public abstract void	 replace (Node ch, Node n);	// replace child node ch with node n

	public abstract void	 clearName ();
	public abstract void 	 computeName ();

	public final char getTypeChar () {
		if (this instanceof Dummy) return 'd';
		if (this instanceof LeafNode) return 'f';
		if (this instanceof LoopNode) return 'L';
		if (this instanceof ConcatNode) return 'C';
		if (this instanceof AltNode) return 'A';
		return '?';
	}

	/* Painting rules:
	 * - Paint only in your bounding rectangle
	 * - Bounding rectangle is relative to the incoming pipe end
	 * - Outgoing pipe end is at same Y coordinate as incoming, but is on the right edge of the bounds
	*/

	/* Painting works in two phases:
	 *
	 * Phase 1: Bounds computation
	 * - The tree is walked recursively, producing the pipe-relative bounds for each node.
	 *
	 * Phase 2: Drawing
	 * - The initial input pipe location is determined, and the top level node is painted,
	 *   which will invoke childrens' paint methods, passing them their appropriate (global space)
	 *   pipe-in locations. 
	*/





}
