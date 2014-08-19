import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
public class RailroadPanel extends JPanel implements MouseListener, KeyListener, MouseWheelListener {

	public ArrayList<Nonterminal> nonts;
	public Node selection;

	public int scrollx, scrolly;

	public RailroadPanel () {
		nonts = new ArrayList<Nonterminal>();
		Nonterminal start = new Nonterminal ("START");
		start.definition = new Dummy();
		nonts.add (start);
		
		selection = null;
		scrollx = 40;
		scrolly = 30;
	}

	public void addNonterminal (String name) {
		Nonterminal n = new Nonterminal (name);
		n.definition = new Dummy();
		nonts.add (n);
	}

	public void addNonterminal (String name, Node def) {
		Nonterminal n = new Nonterminal (name);
		n.definition = def;
		nonts.add (n);
	}

	public void paintComponent (Graphics g) {
		requestFocus();
		g.setColor (Color.WHITE);
		g.fillRect (0,0,getWidth(), getHeight());
		Graphics2D h = (Graphics2D) g;
		h.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		h.translate (scrollx, scrolly);

		int[] ypos = new int[nonts.size()];
		Node first = nonts.get(0).definition;
		ypos[0] = scrolly + 40;
		first.clearName();
		first.computeName();
		first.computeBounds();
		for (int i=1; i < nonts.size(); i++) {
			Node n = nonts.get(i).definition;
			n.clearName ();
			n.computeName ();
			n.computeBounds ();
			ypos[i] = ypos[i-1] + nonts.get(i-1).definition.bounds.size.y + Settings.TOPLEVEL_Y_SPACING;
		}

		for (int i=0; i < nonts.size(); i++) {
			g.setFont (Settings.LABEL_FONT);
			g.setColor (Settings.LABEL_COLOR);
			g.drawString (nonts.get(i).name + ":", 20 + scrollx, ypos[i]-20);
			nonts.get(i).definition.paint (h, new Point (scrollx + 20, ypos[i]));
		}
	}

	public ArrayList<Production> generateProductions () {
		ArrayList<Production> res = new ArrayList<Production> ();
		for (Nonterminal n : nonts) {
			res.addAll (n.definition.generateProductions());
		}
		return res;
	}

	public void mouseEntered (MouseEvent me) {}
	public void mouseExited  (MouseEvent me) {}
	public void mouseClicked (MouseEvent me) {}
	public void mouseReleased (MouseEvent me) {}

	public void mousePressed (MouseEvent me) {
		Point clickpoint = new Point (me.getX(), me.getY()).sub (new Point (scrollx, scrolly));
		System.out.println ("Mouse pressed! at " + clickpoint);
		selection = null;
		for (Nonterminal n : nonts) {
			n.definition.doSelection (clickpoint);
		}
		System.out.println ("Selection is " + selection);
		repaint();

	}

	public void mouseWheelMoved (MouseWheelEvent mwe) {
		scrolly -= mwe.getWheelRotation () * 8;
		repaint();
	}

	public void keyTyped (KeyEvent ke) {}
	public void keyReleased (KeyEvent ke) {}

	private int state = 0;	// 0 is normal state, 1 is waiting for second letter of replacement command
							// 2 is waiting for terminal name, 3 is waiting for nonterminal name for replacement 
							// 4 is waiting for new nonterimnal name
	private StringBuffer sb = new StringBuffer();
	private char replace_type;

	public void replaceSelection (Node n) {
		System.out.println ("Replacing selection (" + selection + ") with " + n);
		if (n == null) {
			n = new Dummy();
		}
		if (n instanceof LeafNode) {
			// check to see if we have a definition for the symbol, and if so, make its symbol point to the appropriate toplevel definition
			// if not, perhaps prompt the user to create a new nonterminal. Or do so automatically?
			LeafNode ln = (LeafNode) n;
			if (ln.n instanceof Nonterminal) {
				for (Nonterminal nt : nonts) {
					if (nt.name.equals (ln.n.name)) {
						ln.n = nt;
						break;
					}
				}
			}
		}
		if (selection != null) {
			if (selection.parent == null) {	// then we're replacing something at the top level.
				for (int i=0; i < nonts.size(); i++) {
					if (nonts.get(i).definition == selection) {
						nonts.get(i).definition = n;
						n.n = nonts.get(i);
						repaint();
						return;
					}
				}
			} else {
				selection.parent.replace (selection, n);
				repaint();
			}
		}
	}

	public void keyPressed (KeyEvent ke) {
		char c = ke.getKeyChar();
		System.out.println ("Got key pressed; char = " + c + "; key code is " + ke.getKeyCode());
		if (state == 1) {
			state = 0;
			replace_type = c;
			switch (c) {
				case 'a':
					replaceSelection (new AltNode (null, new Dummy(), new Dummy()));	break;
				case 'c':
					replaceSelection (new ConcatNode (null, new Dummy()));	break;
				case 'd':
					replaceSelection (new Dummy());	break;
				case 'l':
					replaceSelection (new LoopNode (null, new Dummy(), new Dummy()));	break;
				case 'n':
					sb = new StringBuffer();
					state = 3;	break;
				case 't':
					sb = new StringBuffer();
					state = 2;
			}
		} else if (state == 2 || state == 3) {
			if (c == '\n') {
				LeafNode n;
				// eventually we should do lookups in global lists of existing terms/nonts and use references to them,
				// and add the new one to the list if it does not already exist
				if (state == 2) {
					n = new LeafNode (new Terminal (sb.toString()));
				} else {
					n = new LeafNode (new Nonterminal (sb.toString()));
				}
				replaceSelection (n);
				state = 0;
			} else {
				sb.append (c);
			}
		} else if (state == 4) {
			// we're getting the name of a new nonterminal to add.
			if (c == '\n') {
				state = 0;
				Nonterminal n = new Nonterminal (sb.toString());
				n.definition = new ConcatNode (n, new Dummy());
				nonts.add (n);
				repaint();
			} else {
				sb.append(c);
			}
		} else {
			if (c == 'n') {	// new nonterminal
				sb = new StringBuffer();
				state = 4;
			} else if (c == 'r') {	// replace selection
				state = 1;
			} else if (c == 'b') {	// add branch to selected alternation
				if (selection instanceof AltNode) {
					Dummy d = new Dummy();
					((AltNode) selection).options.add (d);
					d.parent = selection;
					repaint();
				}
			} else if (c == 'm') {	// merge selected nested alternation
				if (selection instanceof AltNode) {
					if (selection.parent != null) {
						if (selection.parent instanceof AltNode) {
							AltNode ch = (AltNode) selection;
							AltNode par = (AltNode) selection.parent;
							par.options.remove (ch);
							for (Node x : ch.options) {
								x.parent = par;
								par.options.add (x);
							}
							repaint();
						}
					}
				}
			}
		}
	}
}
