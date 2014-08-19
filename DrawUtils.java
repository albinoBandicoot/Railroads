import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import java.util.Stack;
public class DrawUtils {

	private static Stack <Integer> flips = new Stack<Integer>();

	public static boolean selected = false;	// whether to override the default color with the selection color
	private static boolean reflect = false;
	private static Graphics2D grphx = (Graphics2D) new BufferedImage (1,1,1).getGraphics();

	public static int stringWidth (String str, Font f) {
		grphx.setFont (f);
		return grphx.getFontMetrics().stringWidth (str);
	}

	public static int[] stringBounds (String str, Font f) {
		grphx.setFont (f);
		FontMetrics fm = grphx.getFontMetrics();
		Rectangle2D rect = fm.getStringBounds (str, grphx);
		int[] bounds = {(int) rect.getWidth(), (int) rect.getHeight()};
		System.out.println ("rect.getHeight --> " + bounds[1] + "; fm.getHeight() --> " + fm.getHeight());
		//int[] bounds = {fm.stringWidth (str), fm.getHeight()};
		return bounds;
	}

	public static void pushFlip (int a) {
		flips.push (a);
		reflect = flips.size() % 2 == 1;
	}

	public static void popFlip () {
		flips.pop();
		reflect = flips.size() % 2 == 1;
	}

	/*
	public static void setFlip (boolean flip, int a) {
		reflect = flip;
		axis = a;
	}
	*/

	public static Point adjustReflection (Point p) {
		for (int i = flips.size() - 1; i >= 0; i--) {
			int axis = flips.get(i);
			p = new Point (2 * axis - p.x, p.y);
		}
		return p;
		/*
		if (reflect) {
			return new Point (2*axis - p.x, p.y);
		} else {
			return p;
		}
		*/
	}

	public static int adjustReflection (int direction) {
		if (reflect) {	// if we've flipped an odd number of times, we need to adjust the direction
			if (direction % 2 == 1) {
				return (direction + 2) % 4;
			}
		}
		return direction;
	}

	public static Rect adjustRect (Rect r) {
		Point tl = adjustReflection (r.tl);
		Point size = new Point ( r.size.x * (reflect ? -1 : 1), r.size.y);
		return new Rect (tl, size).normalize();
	}

	public static void line (Graphics2D g, Point p, Point q) {
		p = adjustReflection(p);
		q = adjustReflection(q);
		g.setStroke (Settings.LINE_STROKE);
		g.setColor (selected ? Settings.SELECTION_COLOR : Settings.LINE_COLOR);
		g.drawLine (p.x, p.y, q.x, q.y);
	}

	public static void arc (Graphics2D g, Point p, int direction, boolean clockwise) {
		p = adjustReflection(p);
		direction = adjustReflection (direction);
		if (reflect) {
			clockwise = !clockwise;
		}

		Point offset = new Point (0,0);
		Point size = new Point (0,0);
		int angle = 0;
		switch (direction) {
			case Settings.NORTH:
				offset = new Point (0, -Settings.CURVE_RADIUS);	
				size = new Point (Settings.CURVE_RADIUS*2, Settings.CURVE_RADIUS*2);
				angle = clockwise ? 90 : 0;
				break;
			case Settings.EAST:
				offset = new Point (Settings.CURVE_RADIUS, 0);
				size = new Point (-Settings.CURVE_RADIUS*2, Settings.CURVE_RADIUS*2);
				angle = clockwise ? 0 : 270;
				break;
			case Settings.SOUTH:
				offset = new Point (0, Settings.CURVE_RADIUS);
				size = new Point (-Settings.CURVE_RADIUS*2, -Settings.CURVE_RADIUS*2);
				angle = clockwise ? 270 : 180;
				break;
			case Settings.WEST:
				offset = new Point (-Settings.CURVE_RADIUS, 0);
				size = new Point (Settings.CURVE_RADIUS*2, -Settings.CURVE_RADIUS*2);
				angle = clockwise ? 180 : 90;
		}
		if (!clockwise) {
			if (direction % 2 == 0) {	// north or south; adjustReflection x
				size.x = -size.x;
			} else {	// east or west, adjustReflection y
				size.y = -size.y;
			}
		}
		Point q = p.add(offset);
		if (size.x < 0) {
			q.x += size.x;
			size.x = -size.x;
		}
		if (size.y < 0) {
			q.y += size.y;
			size.y = -size.y;
		}

		g.setStroke (Settings.LINE_STROKE);
		g.setColor (selected ? Settings.SELECTION_COLOR : Settings.LINE_COLOR);
		g.drawArc (q.x, q.y, size.x, size.y, angle, 90);
	}

	public static void arrow (Graphics2D g, Point p, int direction) {
		p = adjustReflection (p);
		direction = adjustReflection (direction);
		p = p.add (0, Settings.ARROW_ADJUST);
		
		int s = Settings.ARROW_SIZE;
		int[] xpoints = {0, -s/2, 0, s/2};
		int[] ypoints = {0, s, 3*s/4, s};
		for (int i=0; i < 4; i++) {
			int temp;
			switch (direction) {
				case Settings.SOUTH:
					ypoints[i] = -ypoints[i];	break;
				case Settings.EAST:
					temp = xpoints[i];
					xpoints[i] = -ypoints[i];
					ypoints[i] = temp;
					break;
				case Settings.WEST:
					temp = xpoints[i];
					xpoints[i] = ypoints[i];
					ypoints[i] = -temp;
			}
			xpoints[i] += p.x;
			ypoints[i] += p.y;
		}
		g.fillPolygon (xpoints, ypoints, 4);
	}

	public static void rect (Graphics2D g, Rect r, Color c) {
		rect (g, r.tl.add (r.size.mul(0.5)), r.size, 0, c, false);
	}

	public static void rect (Graphics2D g, Point center, Point size, int rounding, Color c, boolean fill) {
		center = adjustReflection (center);
		if (reflect) size.x = -size.x;
		g.setStroke (Settings.LEAF_STROKE);
		g.setColor  (c);

		Point corner = center.add (size.mul (-0.5f));
		if (size.x < 0) {
			corner.x += size.x;
			size.x = -size.x;
		}
		if (size.y < 0) {
			corner.y += size.y;
			size.y = -size.y;
		}

		if (rounding > 0) {
			if (fill) {
				g.fillRoundRect (corner.x, corner.y, size.x, size.y, rounding, rounding);
			} else {
				g.drawRoundRect (corner.x, corner.y, size.x, size.y, rounding, rounding);
			}
		} else {
			if (fill) {
				g.fillRect (corner.x, corner.y, size.x, size.y);
			} else {
				g.drawRect (corner.x, corner.y, size.x, size.y);
			}
		}
	}

	public static void string (Graphics2D g, Point loc, String s) {
		if (reflect) {
			int strwd = stringWidth (s, g.getFont());
			loc.x += strwd;
		}
		loc = adjustReflection (loc);
		g.drawString (s, loc.x, loc.y - Settings.BASELINE_SHIFT);
	}
}
