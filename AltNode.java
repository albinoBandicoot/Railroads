import java.util.ArrayList;
import java.awt.*;
public class AltNode extends InternalNode implements SequentiallySelectable {

	public ArrayList<ConcatNode> options;

	private ArrayList<Point> child_offsets;
	private int collectorX;
	private int brancherX;
	private int chstartX;

	public AltNode (Nonterminal x) {
		super (x);
		options = new ArrayList<ConcatNode>();
		child_offsets = new ArrayList<Point>();
	}

	public AltNode (Nonterminal x, Node... opts) {
		super (x);
		options = new ArrayList<ConcatNode>();
		child_offsets = new ArrayList<Point>();
		for (Node k : opts) {
			if (k instanceof ConcatNode) {
				options.add ((ConcatNode) k);
				k.parent =this;
			} else {
				ConcatNode c = new ConcatNode (null, (InternalNode) k);
				options.add (c);
				c.parent = this;
			}
		}
	}

	public boolean doSelection (Point p) {
		boolean found_child = false;
		for (Node n : options) {
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

	public void setSelection (Node s) {
		super.setSelection (s);
		for (ConcatNode n : options) {
			n.setSelection (s);
		}
	}

	public Node getPreviousNode (Node sel) {
		int idx = options.indexOf (sel);
		if (idx == -1 || idx == 0) return sel;
		return options.get (idx - 1);
	}

	public Node getNextNode (Node sel) {
		int idx = options.indexOf (sel);
		if (idx == -1 || idx == options.size() - 1) return sel;
		return options.get (idx + 1);
	}

	public void removeBranch (Node s) {
		options.remove (s);
		if (options.size() == 1) {
			// not much of an alternation if there's one option, is it?
		}
	}

	public void clearName () {
		if (parent != null) n = null;
		for (Node x : options) {
			x.clearName();
		}
	}

	public void computeName () {
		if (n == null) {
			n = new Nonterminal (parent.n.name + "_ALT", this);
			n.autogen = true;
		}
		int i = 1;
		for (Node x : options) {
			if (! (x instanceof Dummy || x instanceof LeafNode)) {
				x.n = new Nonterminal (n.name + "_a" + i, x);
				x.n.autogen = true;
				x.computeName ();
			}
			i++;
		}
	}

	public void computeBounds (){
		brancherX = Settings.CONNECTOR_LENGTH + Settings.CURVE_RADIUS;
		chstartX = brancherX + Settings.CONNECTOR_LENGTH;
		child_offsets.clear();
		ArrayList<Rect> chbounds = new ArrayList<Rect>();
		for (Node ch : options) {
			ch.computeBounds ();
			chbounds.add (ch.bounds);
		}
		Point pipe = new Point (chstartX, 0);
		child_offsets.add (pipe);
		for (int i=1; i < options.size(); i++) {
			pipe = pipe.add (0, chbounds.get(i-1).maxCoords().y);
			pipe = pipe.add (0, - chbounds.get(i).minCoords().y);
			pipe = pipe.add (0, Settings.BRANCH_SPACING);
			child_offsets.add(pipe);
		}
		
		for (int i=0; i < options.size(); i++) {
			chbounds.set (i, chbounds.get(i).translate (child_offsets.get(i)));
		}

		Rect b = Rect.boundingBox (chbounds);
		for (Node ch : options) {
			if (ch instanceof Dummy) {
				ch.bounds.size.x = b.size.x;
			} else if (ch instanceof ConcatNode) {
				ConcatNode c = (ConcatNode) ch;
				if (c.isEffectivelyEmpty()) {
					System.out.println ("Modifying dummy size to " + b.size.x);
					c.list.get(0).bounds.size.x = b.size.x;
					c.bounds.size.x = b.size.x;
				}
			}
		}

		collectorX = b.maxCoords().x + Settings.CURVE_RADIUS;

		bounds = new Rect (new Point (0, b.tl.y), new Point (collectorX + Settings.CURVE_RADIUS + Settings.CONNECTOR_LENGTH, b.size.y));
	}

	public void paint (Graphics2D g, Point pipein) {
		DrawUtils.selected |= is_selected;
		this.pipe = pipein;
		this.globounds = DrawUtils.adjustRect (bounds.translate (pipe));
		int brX = brancherX + pipein.x;
		int colX = collectorX + pipein.x;
		int chstX = chstartX + pipein.x;

		DrawUtils.line (g, pipein, new Point (chstX, pipein.y));	// initial pipe line
		DrawUtils.arc  (g, pipein.add (Settings.CONNECTOR_LENGTH, 0), Settings.EAST, true);	// downwards arc

		/* Brancher main line */
		int boty = pipein.y + child_offsets.get (child_offsets.size()-1).y - Settings.CURVE_RADIUS;
		DrawUtils.line (g, new Point (brX, pipein.y + Settings.CURVE_RADIUS), new Point (brX, boty));

		/* Collector main line */
		DrawUtils.line (g, new Point (colX, pipein.y + Settings.CURVE_RADIUS), new Point (colX, boty));

		/* Draw the children */
		options.get(0).paint (g, new Point (chstX, pipein.y));
		DrawUtils.line (g, new Point (chstX + options.get(0).bounds.size.x, pipein.y), new Point (colX + Settings.CURVE_RADIUS + Settings.CONNECTOR_LENGTH, pipein.y));

		for (int i=1; i < options.size(); i++) {
			// incoming arc
			DrawUtils.arc (g, new Point (brX, pipein.y + child_offsets.get(i).y - Settings.CURVE_RADIUS), Settings.SOUTH, false);
			int ypos = pipein.y + child_offsets.get(i).y;
			options.get (i).paint (g, new Point (chstX, ypos));
			// line from end of child to collector
			Point arcp = new Point (colX - Settings.CURVE_RADIUS, ypos);
			DrawUtils.line (g, new Point (chstX + options.get(i).bounds.size.x, ypos), arcp);
			// outgoing arc
			DrawUtils.arc (g, arcp, Settings.EAST, false);
		}

		DrawUtils.arc (g, new Point (colX, pipein.y + Settings.CURVE_RADIUS), Settings.NORTH, true);

		/* Draw the bounding rectangle */
		if (Settings.DRAW_BOUNDING_RECTS) {
			g.setColor (Color.MAGENTA);
			g.drawRect (globounds.tl.x, globounds.tl.y, globounds.size.x, globounds.size.y);
		}
		DrawUtils.selected &= !is_selected;
	}

	public void replace (Node ch, Node n) {
		for (int i=0; i < options.size(); i++) {
			if (ch == options.get(i)) {
				if (n instanceof ConcatNode) {
					options.set (i, (ConcatNode) n);
					n.parent = this;
				} else {
					ConcatNode c = new ConcatNode (null, (InternalNode) n);
					options.set (i, c);
					c.parent = this;
				}
			}
		}
	}

	public ArrayList<Production> generateProductions () {
		ArrayList<Production> res = new ArrayList<Production> ();
		for (Node node : options) {
			if (! (node instanceof Dummy)) {
				Production p = new Production (n);
				p.add (node.n);
				res.add (p);
				res.addAll (node.generateProductions());
			}
		}
		return res;
	}

	public String toString () {
		StringBuffer sb = new StringBuffer("alt (");
		for (Node n : options) {
			sb.append (n.toString() + ", ");
		}
		sb.append (")");
		return sb.toString();
	}
}
