import java.awt.*;
import java.util.ArrayList;
public class Dummy extends InternalNode {

	public Dummy () {
		super (null);
	}

	public boolean doSelection (Point p) {
		is_selected = globounds.contains (p);
		if (is_selected) Railroad.rp.selection = this;
		return is_selected;
	}

	public void clearName () {}
	public void computeName () {}

	public void computeBounds () {
		bounds = new Rect (new Point (0, -Settings.DUMMY_SIZE / 2), new Point (Settings.DUMMY_SIZE + 2*Settings.DUMMY_CONNECTOR_LENGTH, Settings.DUMMY_SIZE));
	}

	public void paint (Graphics2D g, Point pipein) {
		this.pipe = pipein;
		this.globounds = DrawUtils.adjustRect (bounds.translate (pipe));
		DrawUtils.selected |= is_selected;

		Point l = pipein.add (Settings.DUMMY_CONNECTOR_LENGTH, 0);
		Point r = l.add (Settings.DUMMY_SIZE, 0);
		/*
		DrawUtils.line  (g, pipein, l);
		DrawUtils.arrow (g, l, Settings.EAST);
		DrawUtils.line  (g, r, r.add (Settings.DUMMY_CONNECTOR_LENGTH, 0));
		DrawUtils.rect  (g, l.add (Settings.DUMMY_SIZE/2, 0), new Point (Settings.DUMMY_SIZE), 0, Settings.DUMMY_COLOR, false);
		*/
		DrawUtils.line (g, pipein, pipein.add (bounds.size.x, 0));

		if (Settings.DRAW_BOUNDING_RECTS) {
			g.setColor (Color.BLUE);
			g.drawRect (globounds.tl.x, globounds.tl.y, globounds.size.x, globounds.size.y);
		}
		DrawUtils.selected &= !is_selected;
	}

	public void replace (Node ch, Node n) {
		System.out.println ("Should not have called replace on Dummy");
	}

	public ArrayList<Production> generateProductions () {
		return new ArrayList<Production>();
	}

	public String toString () {
		return "dummy";
	}

}
