import javax.swing.*;
import java.awt.event.*;
public class Railroad extends JFrame implements ActionListener {

	public static RailroadPanel rp;

	public Railroad () {
		super ("Railroads");
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

		JPanel content = new JPanel ();
		content.setOpaque (true);
		content.setLayout (null);

		rp = new RailroadPanel ();
		rp.setBounds (5,5, 1000, 700);
		rp.addMouseListener (rp);
		rp.addKeyListener (rp);
		rp.addMouseWheelListener (rp);

		content.add (rp);

		setContentPane (content);
		pack();
		setSize (1010, 750);
		setVisible (true);
	}

	public static void main (String[] args) {
		Railroad r = new Railroad ();
	}

	public void actionPerformed (ActionEvent e) {
	}

}
