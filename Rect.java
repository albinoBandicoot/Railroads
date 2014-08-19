import java.util.ArrayList;
public class Rect {

	public Point tl;
	public Point size;

	public Rect (Point tl, Point size) {
		this.tl = tl;
		this.size = size;
	}

	public boolean contains (Point p) {
		Point q = p.sub (minCoords());
		return q.x >= 0 && q.x < Math.abs(size.x) && q.y >= 0 && q.y < Math.abs(size.y);
	}

	public Point minCoords () {
		Point res = new Point (tl.x, tl.y);
		if (size.x < 0) res.x += size.x;
		if (size.y < 0) res.y += size.y;
		return res;
	}

	public Point maxCoords () {
		Point res = new Point (tl.x, tl.y);
		if (size.x > 0) res.x += size.x;
		if (size.y > 0) res.y += size.y;
		return res;
	}

	public Rect translate (Point offset) {
		return new Rect (tl.add (offset), size);
	}

	public Rect normalize () {
		return new Rect (minCoords(), maxCoords().sub(minCoords()));
	}
		

	public static Rect boundingBox (ArrayList<Rect> boxen) {
		Point min = new Point (1000000, 1000000);
		Point max = new Point (-1000000, -1000000);
		for (Rect r : boxen) {
			min = min.min (r.minCoords());
			max = max.max (r.maxCoords());
		}
		return new Rect (min, max.sub(min));
	}

}

