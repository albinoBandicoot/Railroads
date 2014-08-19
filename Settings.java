import java.awt.*;
public class Settings {

	public static Color LINE_COLOR = Color.BLACK;
	public static Color SELECTION_COLOR = Color.RED;
	public static Color DUMMY_COLOR = Color.CYAN;
	public static Color TERMINAL_COLOR = new Color (0.2f, 0.0f, 0.3f);
	public static Color NONTERMINAL_COLOR = new Color (0.4f, 0.15f, 0.5f);
	public static Color LABEL_COLOR = Color.BLACK;
	public static Stroke LINE_STROKE = new BasicStroke (2.0f);
	public static Stroke LEAF_STROKE = new BasicStroke (2.0f);
	public static int CURVE_RADIUS = 15;
	public static int ARROW_SIZE = 10;
	public static int ARROW_ADJUST = 0;
	
	public static int LEAF_HEIGHT = 23;
	public static int NONTERMINAL_ROUNDING = 4;
	public static int TERMINAL_ROUNDING = 20;
	public static int DUMMY_SIZE = 16;
	public static int LEAF_TEXT_PAD = 8;
	public static int CONNECTOR_LENGTH = 15;	// minimum; really double this b/c each node will do one.
												// dummy nodes may shorten this to save space & look dumber
	public static int DUMMY_CONNECTOR_LENGTH = 15;
	public static int BRANCH_SPACING = 15;
	public static int TOPLEVEL_Y_SPACING = 40;


	public static Font NONTERMINAL_FONT = new Font ("Courier", Font.BOLD, 16);
	public static Font TERMINAL_FONT = new Font ("Courier", Font.PLAIN, 14);
	public static Font LABEL_FONT = new Font ("Courier", Font.PLAIN, 18);
	public static int  BASELINE_SHIFT = 1;

	public static boolean DRAW_BOUNDING_RECTS = false;

	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;

}
