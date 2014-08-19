import java.util.ArrayList;
import java.awt.*;
public class ConcatNode extends Node {

	public ArrayList<Node> list;
	private ArrayList<Point> child_offsets;

	public ConcatNode (Symbol n) {
		super (n);
		list = new ArrayList<Node>();
		child_offsets = new ArrayList<Point>();
	}

	public ConcatNode (Symbol n, Node... list) {
		super (n);
		this.list = new ArrayList<Node>();
		child_offsets = new ArrayList<Point>();
		for (Node x : list) {
			this.list.add (x);
			x.parent = this;
		}
	}

	public boolean doSelection (Point p) {
		boolean found_child = false;
		for (Node n : list) {
			found_child |= n.doSelection (p);
		}
		if (globounds.contains (p)) {
			is_selected = !found_child;
		} else {
			is_selected = false;
		}
		if (is_selected) Railroad.rp.selection = this;
		return is_selected || found_child;
	}

	public void clearName () {
		if (parent != null) n = null;
		for (Node x : list) {
			x.clearName();
		}
	}

	public void computeName () {
		if (n == null) {
			n = new Nonterminal (parent.n.name + "_C", this);
		}
		int i = 1;
		for (Node x : list) {
			if (! (x instanceof Dummy || x instanceof LeafNode)) {
				x.n = new Nonterminal (n.name + "_c" + i, x);
				x.computeName ();
			}
			i++;
		}
	}

	public void computeBounds () {
		child_offsets.clear();
		ArrayList<Rect> chbounds = new ArrayList<Rect>();
		Point pipe = new Point (0,0);
		child_offsets.add (pipe);
		for (Node ch : list) {
			ch.computeBounds();
			chbounds.add (ch.bounds.translate (pipe));
			pipe = new Point (chbounds.get(chbounds.size()-1).maxCoords().x, 0);
			child_offsets.add (pipe);
		}
		bounds = Rect.boundingBox (chbounds);
	}

	public void paint (Graphics2D g, Point pipein) {
		DrawUtils.selected |= is_selected;
		this.pipe = pipein;
		this.globounds = DrawUtils.adjustRect (bounds.translate (pipe));
		for (int i=0; i < list.size(); i++) {
			list.get(i).paint (g, pipein.add (child_offsets.get(i)));
		}
		if (Settings.DRAW_BOUNDING_RECTS) {
			g.setColor (Color.RED);
			g.drawRect (globounds.tl.x, globounds.tl.y, globounds.size.x, globounds.size.y);
		}
		DrawUtils.selected &= !is_selected;
	}

	public void replace (Node ch, Node n) {
		for (int i=0; i < list.size(); i++) {
			if (ch == list.get(i)) {
				list.set (i, n);
				n.parent = this;
			}
		}
		if (! (list.get (list.size()-1) instanceof Dummy)) {
			Dummy d = new Dummy();
			d.parent = this;
			list.add (d);
		}
		flatten();
	}

	public void collapseDummies () {
		for (int i = list.size()-1; i > 0; i--) {
			if (list.get(i) instanceof Dummy && list.get(i-1) instanceof Dummy) {
				list.remove (i);
			}
		}
	}

	public void flatten () {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof ConcatNode) {
				ConcatNode c = (ConcatNode) list.get(i);
				list.remove (i);
				for (Node n : c.list) {
					n.parent = this;
				}
				list.addAll (i, c.list);
			}
		}
		collapseDummies();
	}

	public ArrayList<Production> generateProductions () {
		ArrayList<Production> res = new ArrayList<Production>();
		Production p = new Production (n);
		for (Node node : list) {
			if (! (node instanceof Dummy)) {
				p.add (node.n);
				res.addAll (node.generateProductions());
			}
		}
		res.add (p);
		return res;
	}

	public String toString () {
		StringBuffer sb = new StringBuffer("concat (");
		for (Node n : list) {
			sb.append (n.toString() + ", ");
		}
		sb.append (")");
		return sb.toString();
	}

}