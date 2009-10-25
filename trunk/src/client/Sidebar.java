package client;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Sidebar extends JPanel {
	private static final long serialVersionUID = 1435646083804109826L;
	static JLabel lblNick = new JLabel(Zincgull.nick);
	
	public Sidebar(){
		this.add(lblNick);
	}
}
