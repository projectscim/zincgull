package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class Sidebar extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1435646083804109826L;
	private JButton btnExit = new JButton("Exit");
	
	public Sidebar(){
		this.add(btnExit);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnExit) {
			System.exit(0);
		}
	}
}
