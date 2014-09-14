import java.util.ArrayList;
import java.awt.*;
public class LoopNode extends InternalNode implements SequentiallySelectable {


	/*  
	 *  A is TOP (BOT TOP)*
	 *
	 *  A:           
	 *  --+--> TOP ---+-->
	 *    ^--- BOT <--'
	 *
	 * Generates:
	 *
	 * A ::= TOP
	 * A ::= TOP B
	 * B ::= BOT TOP		// this rule could be replaced with B ::= e
	 * B ::= BOT TOP B
	 *
	 * If TOP is null, this is equivalent to BOT*
	 *
	 * A ::= e
	 * A ::= BOT A
	 *
	 * If BOT is null, this is equivalent to TOP+   that is, TOP TOP*
	 * 
	 * A ::= TOP
	 * A ::= TOP A
	*/

	public ConcatNode top;
	public ConcatNode bot;

	private int midwidth;
	private int bottomy;

	public LoopNode (Nonterminal n, Node top, Node bot) {
		super ((Symbol) n);
		if (top == null) {
			this.top = new ConcatNode (null);
		} else if (top instanceof InternalNode) {
			ConcatNode ct = new ConcatNode (null, (InternalNode) top);
			top.parent = ct;
			this.top = ct;
		} else {
			this.top = (ConcatNode) top;
		}
		if (bot == null) {
			this.bot = new ConcatNode(null);
		} else if (bot instanceof InternalNode) {
			ConcatNode cb = new ConcatNode (null, (InternalNode) bot);
			bot.parent = cb;
			this.bot = cb;
		} else {
			this.bot = (ConcatNode) bot;
		}
		top.parent = this;
		bot.parent = this;
	}

	public boolean doSelection (Point p) {
		boolean child_sel = top.doSelection (p) | bot.doSelection (p);
		if (globounds.contains (p)) {
			is_selected = !child_sel;
		} else {
			is_selected = false;
		}
		if (is_selected) Railroad.rp.selection = this;
		return is_selected || child_sel;
	}

	public void  setSelection (Node s) {
		super.setSelection (s);
		top.setSelection (s);
		bot.setSelection (s);
	}

	public Node getNextNode (Node sel) {
		if (sel == top) return bot;
		return sel;
	}

	public Node getPreviousNode (Node sel) {
		if (sel == bot) return top;
		return sel;
	}

	public void clearName () {
		if (parent != null) n = null;
		top.clearName();
		bot.clearName();
	}

	public void computeName () {
		if (n == null) {
			n = new Nonterminal (parent.n.name + "_LOOP");
		}
		if (!top.isEffectivelyEmpty()) top.n = new Nonterminal (n.name + "_TOP");
		if (!bot.isEffectivelyEmpty()) bot.n = new Nonterminal (n.name + "_BOT");
		top.computeName ();	// for the children of top.
		bot.computeName ();
	}

	public  void computeBounds () {
		top.computeBounds ();
		bot.computeBounds ();
		midwidth = Math.max (top.bounds.size.x, bot.bounds.size.x);
		bottomy = top.bounds.maxCoords().y - bot.bounds.minCoords().y + Settings.BRANCH_SPACING;
		int miny = top.bounds.minCoords().y;
		int maxy = bottomy + bot.bounds.maxCoords().y;
		bounds = new Rect (new Point (0, miny), new Point (midwidth + 2*(Settings.CURVE_RADIUS + Settings.CONNECTOR_LENGTH), maxy - miny));
	}

	public void paint (Graphics2D g, Point pipein) {
		DrawUtils.selected |= is_selected;
		this.pipe = pipein;
		this.globounds = DrawUtils.adjustRect (bounds.translate (pipe));
		int boty = bottomy + pipein.y;
		int branchx1 = pipein.x + Settings.CONNECTOR_LENGTH;
		int chstartx = branchx1 + Settings.CURVE_RADIUS;
		int branchx2 = chstartx + midwidth + Settings.CURVE_RADIUS;

		DrawUtils.line (g, pipein, new Point (chstartx, pipein.y));	// input pipe continuation
		top.paint (g, new Point (chstartx, pipein.y));
		DrawUtils.line (g, new Point (chstartx + top.bounds.size.x, pipein.y), new Point (branchx2 + Settings.CONNECTOR_LENGTH, pipein.y));	// line from end of top through to output pipe
		DrawUtils.arc  (g, new Point (chstartx + midwidth, pipein.y), Settings.EAST, true);	// downwards arc from top
		Point p = new Point (branchx2, boty - Settings.CURVE_RADIUS);
		DrawUtils.line (g, new Point (branchx2, pipein.y + Settings.CURVE_RADIUS), p);	// downwards line on right 
		DrawUtils.arc  (g, p, Settings.SOUTH, true);	// leftwards arc into bot

		Point botpipe = new Point (chstartx + midwidth, boty);

		DrawUtils.pushFlip (botpipe.x);
		bot.paint (g, botpipe);
		DrawUtils.popFlip ();

		DrawUtils.line (g, botpipe.add (-bot.bounds.size.x, 0), new Point (chstartx, boty));	// extend bottom output pipe back to left branch
		DrawUtils.arc  (g, new Point (chstartx, boty), Settings.WEST, true);	// arc upwards
		Point q = new Point (branchx1, pipein.y + Settings.CURVE_RADIUS);
		DrawUtils.line (g, new Point (branchx1, p.y), q);	// upwards line
		DrawUtils.arc  (g, q, Settings.NORTH, true);	// arc back into top

		if (Settings.DRAW_BOUNDING_RECTS) {
			g.setColor (Color.MAGENTA);
			g.drawRect (globounds.tl.x, globounds.tl.y, globounds.size.x, globounds.size.y);
		}
		DrawUtils.selected &= !is_selected;

	}

	public void replace (Node ch, Node n) {
		n.parent = this;
		ConcatNode cn = null;
		if (n instanceof InternalNode) {
			ConcatNode wrapper = new ConcatNode (null, (InternalNode) n);
			n.parent = wrapper;
			wrapper.parent = this;
			cn = wrapper;
		} else {
			cn = (ConcatNode) n;
		}
		if (ch == top) {
			top = cn;
		} else if (ch == bot) {
			bot = cn;
		} else {
			System.out.println ("Trying to replace nonexistent child of LoopNode");
		}
	}

	public ArrayList<Production> generateProductions () {
		ArrayList<Production> res = new ArrayList<Production>();
		if (top.isEffectivelyEmpty()) {
			res.add (new Production (n));
			if (bot.isEffectivelyEmpty()) {
				// then this is just A ::= e, which we already added
			} else {
				// A ::= e
				// A ::= BOT A
				Production bot_a = new Production (n, bot.n, n);
				res.add (bot_a);
			}
		} else {
			if (bot.isEffectivelyEmpty()) {
				// A ::= TOP
				// A ::= TOP A
				res.add (new Production (n, top.n));
				res.add (new Production (n, top.n, n));
			} else {
				Nonterminal b = new Nonterminal (n.name + "_loop_temp");
				res.add (new Production (n, top.n));
				res.add (new Production (n, top.n, b));
				res.add (new Production (b));
				res.add (new Production (b, bot.n, top.n, b));
			}
		}
		res.addAll (top.generateProductions());
		res.addAll (bot.generateProductions());
		return res;
		
	}

	public String toString () {
		return "loop (top = " + top + ", bot = " + bot + ")";
	}
}
