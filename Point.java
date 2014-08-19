public class Point {

	public int x, y;

	public Point (int c) {
		x = c;
		y = c;
	}

	public Point (int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point add (Point p) {
		return new Point (x + p.x, y + p.y);
	}

	public Point add (int x, int y) {
		return new Point (this.x + x, this.y + y);
	}

	public Point sub (Point p) {
		return new Point (x - p.x, y - p.y);
	}

	public Point mul (double scale) {
		return new Point ((int) (x * scale), (int) (y*scale));
	}

	public Point min (Point p) {
		return new Point (Math.min (x, p.x), Math.min (y, p.y));
	}

	public Point max (Point p) {
		return new Point (Math.max (x, p.x), Math.max (y, p.y));
	}

	public String toString () {
		return "(" + x + ", " + y + ")";
	}
}
