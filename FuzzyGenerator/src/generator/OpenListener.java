package generator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;


import javax.swing.*;

public class OpenListener implements ActionListener{
	
	protected KonfigurationFrame e;
	protected String ausgabe;
	
	
	public OpenListener(KonfigurationFrame e, String ausgabe){
		this.e = e;
		this.ausgabe = ausgabe;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JFileChooser choose = new JFileChooser();
		int i =choose.showOpenDialog(e);
		if (i == JFileChooser.APPROVE_OPTION ){
			
				
				//Datei und Prüfzeile setzen
				switch (ausgabe) {  
				case "s": 
						e.SchichtFile =  choose.getSelectedFile();
						e.SchichtAusgabe.setText(choose.getSelectedFile().getName());
						break;
				
				case "m": 
						e.MAFile = choose.getSelectedFile();
						e.MAAusgabe.setText(choose.getSelectedFile().getName());
						break;
				}
						
	}
	}

}
