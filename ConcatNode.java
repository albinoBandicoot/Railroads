import java.util.ArrayList;
import java.awt.*;
public class ConcatNode extends Node implements SequentiallySelectable {

	public ArrayList<InternalNode> list;
	private ArrayList<Point> child_offsets;

	public ConcatNode (Symbol n) {
		super (n);
		list = new ArrayList<InternalNode>();
		child_offsets = new ArrayList<Point>();
	}

	public ConcatNode (Symbol n, InternalNode... list) {
		super (n);
		this.list = new ArrayList<InternalNode>();
		child_offsets = new ArrayList<Point>();
		for (InternalNode x : list) {
			this.list.add (x);
			x.parent = this;
		}
	}

	public boolean doSelection (Point p) {
		is_selected = false;	// you can't select a concatNode
		boolean found_child = false;
		for (Node n : list) {
			found_child |= n.doSelection (p);
		}
		return found_child;
	}
	
	public void setSelection (Node s) {
		super.setSelection (s);
		for (Node n : list) {
			n.setSelection (s);
		}
	}

	public Node getPreviousNode (Node sel) {
		int idx = list.indexOf (sel);
		if (idx == -1 || idx == 0) return sel;
		return list.get (idx - 1);
	}

	public Node getNextNode (Node sel) {
		int idx = list.indexOf (sel);
		if (idx == -1 || idx == list.size() - 1) return sel;
		return list.get (idx + 1);
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

	public boolean isEffectivelyEmpty () {
		return list.isEmpty() || list.size() == 1 && list.get(0) instanceof Dummy;
	}

	public void replace (Node ch, Node n) {
		for (int i=0; i < list.size(); i++) {
			if (ch == list.get(i)) {
				if (n instanceof ConcatNode) {
					list.remove (i);
					int w = i;
					for (InternalNode in : ((ConcatNode) n).list) {
						list.add (w, in);
						w++;
					}
				} else {
					list.set (i, (InternalNode) n);
				}
				n.parent = this;
			}
		}
	}

	public void insert (Node sel, Node rep, int action) {
		int idx = list.indexOf (sel);
		rep.parent = this;
		idx += action == RailroadPanel.INSERT_AFTER ? 1 : 0;
		if (rep instanceof ConcatNode) {
			int w = idx;
			for (InternalNode in : ((ConcatNode) rep).list) {
				list.add (w++, in);
			}
		} else {
			list.add (idx, (InternalNode) rep);
		}
	}

	public void remove (Node s) {
		list.remove (s);
		if (list.isEmpty()) {
			// usually we'll insert a dummy; however, if our parent is an AltNode, we want to delete our branch.
			if (parent instanceof AltNode) {	// if parent is null, this will evaluate to false
				((AltNode) parent).removeBranch (this);
				return;
			}

			Dummy d = new Dummy();
			d.parent = this;
			list.add (d);
		}
	}

	public void collapseDummies () {
		for (int i = list.size()-1; i > 0; i--) {
			if (list.get(i) instanceof Dummy && list.get(i-1) instanceof Dummy) {
				list.remove (i);
			}
		}
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
