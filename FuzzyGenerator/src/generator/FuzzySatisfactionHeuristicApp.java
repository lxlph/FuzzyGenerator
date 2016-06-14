package generator;

import javax.swing.JFrame;

public class FuzzySatisfactionHeuristicApp {

	public static void main(String[] args) {
		
		//Konfigurations-Fenster laden
		KonfigurationFrame frame = new KonfigurationFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400,200);
		frame.setVisible(true);

	}

}
