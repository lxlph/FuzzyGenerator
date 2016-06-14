package generator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.*;

public class ButtonListener implements ActionListener{
	
	protected KonfigurationFrame e;
	protected String b;
	protected ProtokollFrame protokoll;
	
	public ButtonListener(KonfigurationFrame e, String b){
		this.e = e;
		this.b = b;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
				switch (b) {
				//Button Zurücksetzen
				case "z":
						e.MAFile = null;
						e.SchichtFile = null;
						e.MAAusgabe.setText("");
						e.SchichtAusgabe.setText("");
						break;
						
				//Button Speichern
				case "s":
					e.text.setText("");
					//Protokoll öffnen
					if ((e.MAFile != null) && (e.SchichtFile != null)) {
						
						protokoll = new ProtokollFrame();
						protokoll.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						protokoll.setSize(600,800);
						protokoll.setVisible(true);
						
					}
					//MA einlesen
					if (e.MAFile != null) {
					e.text.append("Mitarbeiter: \n");
						try {
							FileReader read = new FileReader(e.MAFile);
							
							BufferedReader buf = new BufferedReader(read);
							String line;
							while ((line = buf.readLine()) != null) {
							// füllen des Prüftextes 
							e.text.append(line+ "\n");
							
							// übergeben an protokoll
							try {
								protokoll.maList.add(line);
							} catch (NullPointerException e){
								
							}
							}
							buf.close();
							
						} catch (FileNotFoundException e1) {
							e.text.append("keine Mitarbeiter-Datei gefunden \n");
						} catch (IOException e1) {
							
							e1.printStackTrace();
						} 
					}
					else {
						e.text.append("MA-Datei auswählen \n");
					}
					//Schichten einlesen
					if (e.SchichtFile != null) {
						e.text.append("Schichten: \n");
						try {
							FileReader read = new FileReader(e.SchichtFile);
							
							BufferedReader buf = new BufferedReader(read);
							String line;
							while ((line = buf.readLine()) != null) {
							//füllen des Prüftextes
							e.text.append(line+ "\n");
							
							// übergeben an protokoll
							try {
								protokoll.schichtList.add(line);
							} catch (NullPointerException e){
								
							}
							}
							buf.close();
							
						} catch (FileNotFoundException e1) {
							e.text.append("keine Schicht-Datei gefunden \n");
						} catch (IOException e1) {
							
							e1.printStackTrace();
						}
						}
						else {
							e.text.append("Schicht-Datei auswählen \n");
						}
						
						// Konfiguration schließen
						if ((e.MAFile != null) && (e.SchichtFile != null)) {
							
							e.setVisible(false); 
					        e.dispose();;
					        protokoll.listenAuslesen();
						}
						break;
				
				}
	}

}

