package generator;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.io.*;

import javax.swing.*;



public class KonfigurationFrame extends JFrame {
	
	protected JLabel SchichtAusgabe;
	protected JLabel MAAusgabe;
	protected File MAFile;
	protected File SchichtFile;
	protected JTextArea text;
	protected KonfigurationFrame frame;

	public KonfigurationFrame() {
		this.frame = this;
		
		//Layout
		 setLayout(new GridBagLayout());
		 GridBagConstraints valueConstraints = new GridBagConstraints();
		 valueConstraints.fill = GridBagConstraints.BOTH;
		 valueConstraints.insets = new Insets(2,2,2,2);

		
		//Mitarbeiter laden
		 valueConstraints.gridx = 1;
		 valueConstraints.gridy = 1;
		 JLabel MaLabel = new JLabel("Mitarbeiter laden:");
		 add(MaLabel, valueConstraints);
		 
		 valueConstraints.gridx = 2;
		 valueConstraints.gridy = 1;
		 JButton MADatei = new JButton("Datei");
		 add(MADatei, valueConstraints);
		 
		 OpenListener openListenerMA = new OpenListener(this, "m");
			MADatei.addActionListener(openListenerMA);
		 
		// Prüfzeile MA
		 valueConstraints.gridx = 3;
		 valueConstraints.gridy = 1;
		 MAAusgabe = new JLabel("");
		 add(MAAusgabe, valueConstraints);
		 
		//Schichtdefinitionen laden
		 valueConstraints.gridx = 1;
		 valueConstraints.gridy = 2;
		 JLabel SchichtdefinitionenLabel = new JLabel("Schichtdefinitionen laden:");
		 add(SchichtdefinitionenLabel, valueConstraints);
		 
		 valueConstraints.gridx = 2;
		 valueConstraints.gridy = 2;
		 JButton SchichtdefinitionenDatei = new JButton("Datei");
		 add(SchichtdefinitionenDatei, valueConstraints);
		 
		 OpenListener openListenerSchicht = new OpenListener(this, "s");
		 	SchichtdefinitionenDatei.addActionListener(openListenerSchicht);
		 
		// Prüfzeile Schicht
		 valueConstraints.gridx = 3;
		 valueConstraints.gridy = 2;
		 SchichtAusgabe = new JLabel("");
		 add(SchichtAusgabe, valueConstraints);
		 
		 
		//Start und Zurücksetzen Button
		 
		 valueConstraints.gridwidth = 2;

		 valueConstraints.gridx = 1;
		 valueConstraints.gridy = 3;
		 JButton start = new JButton("Starten");
		 add(start, valueConstraints);
		 
		 ButtonListener buttonListenerStart = new ButtonListener(this, "s");
		 	start.addActionListener(buttonListenerStart);
		 
		 valueConstraints.gridx = 1;
		 valueConstraints.gridy = 4;
		 JButton back = new JButton("Zurücksetzen");
		 add(back, valueConstraints);
		 
		 ButtonListener buttonListenerBack = new ButtonListener(this, "z");
		 	back.addActionListener(buttonListenerBack);
		 	
		 //Ausgabe der eingegebenen Dateien
		 	text = new JTextArea("");
		 	valueConstraints.gridwidth = 3;
		 	valueConstraints.gridx = 1;
			valueConstraints.gridy = 5;
		 	add(new JScrollPane(text));
		 	add(text, valueConstraints);
		 
	}
}
