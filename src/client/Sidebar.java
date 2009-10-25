package client;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Sidebar extends JPanel {
	private static final long serialVersionUID = 1435646083804109826L;
	static JLabel lblNick = new JLabel(Zincgull.nick);
	static JLabel lblHost = new JLabel(Zincgull.host);
	static JLabel lblRand = new JLabel(Double.toString(Zincgull.random));
	
	public Sidebar(){
		this.add(lblNick);
		this.add(lblHost);
		this.add(lblRand);
		this.setPreferredSize(new Dimension(140, 60));
	}
}
