import java.util.ArrayList;
import java.awt.*;
public class LeafNode extends Node {

	public LeafNode (Symbol sym) {
		super (sym);
	}

	public boolean doSelection (Point p) {
		is_selected = globounds.contains (p);
		if (is_selected) Railroad.rp.selection = this;
		return is_selected;
	}

	public void clearName () {
	}

	public void computeName () {
	}

	public void computeBounds () {
		Font f = n instanceof Terminal ? Settings.TERMINAL_FONT : Settings.NONTERMINAL_FONT;
		int textwd = DrawUtils.stringWidth (n.name, f);
		bounds = new Rect (new Point (0, - Settings.LEAF_HEIGHT / 2), new Point (textwd + 2*(Settings.LEAF_TEXT_PAD + Settings.CONNECTOR_LENGTH), Settings.LEAF_HEIGHT));
	}

	public void paint (Graphics2D g, Point pipein) {
		DrawUtils.selected |= is_selected;
		this.pipe = pipein;
		this.globounds = DrawUtils.adjustRect (bounds.translate (pipe));
		int rounding = Settings.TERMINAL_ROUNDING;
		Color col = Settings.TERMINAL_COLOR;
		if (n instanceof Terminal) {
			g.setFont (Settings.TERMINAL_FONT);
		} else {
			g.setFont (Settings.NONTERMINAL_FONT);
			rounding = Settings.NONTERMINAL_ROUNDING;
			col = Settings.NONTERMINAL_COLOR;
		}
		col = is_selected ? Settings.SELECTION_COLOR : col;
		g.setColor (col);

		int[] stringbounds = DrawUtils.stringBounds (n.name, g.getFont());
		int textwd = stringbounds[0] + 2 * Settings.LEAF_TEXT_PAD;

		Point l = pipein.add (Settings.CONNECTOR_LENGTH, 0);
		Point c = l.add (textwd/2, 0);
		Point size = new Point (textwd, Settings.LEAF_HEIGHT);
		Point r = l.add (textwd, 0);

		DrawUtils.line  (g, pipein, l);
		DrawUtils.arrow (g, l, Settings.EAST);
		DrawUtils.line  (g, r, r.add (Settings.CONNECTOR_LENGTH, 0));
		DrawUtils.rect  (g, c, size, rounding, col, false);
		DrawUtils.string(g, c.add (-stringbounds[0]/2, stringbounds[1]/2), n.name);

		if (Settings.DRAW_BOUNDING_RECTS) {
			g.setColor (Color.GREEN);
			g.drawRect (globounds.tl.x, globounds.tl.y, globounds.size.x, globounds.size.y);
		}
		DrawUtils.selected &= !is_selected;
	}

	public void replace (Node ch, Node n) {
		System.out.println ("Should not have called replace on LeafNode");
	}

	public ArrayList<Production> generateProductions () {
		return new ArrayList<Production> ();
	}

	public String toString () {
		return n.toString();
	}

}
