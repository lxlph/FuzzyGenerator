package generator;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;



public class ProtokollFrame extends JFrame {
	
	protected ArrayList<String> maList = new ArrayList<String>();
	protected ArrayList<String> schichtList= new ArrayList<String>();
	
	protected int[] maMinuten;
	protected String[][] maWunsch;
	protected int[] maSchwelle;
	protected int[] maAPunkte;
	protected int[] maBPunkte;
	protected int[][] schichtenInfo;
	protected JTextArea TextLabel;
	protected JTable graphik;
	protected GridBagConstraints valueConstraints;
	
	
	public ProtokollFrame() {

		
		//Layout
		 setLayout(new GridBagLayout());
		 valueConstraints = new GridBagConstraints();
		 valueConstraints.fill = GridBagConstraints.BOTH;
		 valueConstraints.insets = new Insets(2,2,2,2);
		 
		 valueConstraints.gridx = 1;
		 valueConstraints.gridy = 1;
		 valueConstraints.weightx = 200;
		 valueConstraints.weighty = 400;
		 TextLabel = new JTextArea("Protokoll: \n -----");
		 JScrollPane scroll = new JScrollPane(TextLabel);
		 add(scroll, valueConstraints);
	
		 valueConstraints.gridx = 1;
		 valueConstraints.gridy = 2;
		 valueConstraints.weighty = 5;
		 String[] algAuswahl = {"FuzzySatisfactionHeuristik", "erweiterteFuzzySatisfactionHeuristik"};
		 JComboBox<String> algAuswahlMenue = new JComboBox<String>(algAuswahl);
		 add(algAuswahlMenue, valueConstraints);
		 
		 valueConstraints.gridx = 1;
		 valueConstraints.gridy = 3;
		 valueConstraints.weighty = 10;
		 JButton algGo = new JButton("Start");
		 add(algGo, valueConstraints);
		 
		 AlgListener listenerAlg = new AlgListener(this, algAuswahlMenue.getSelectedItem());
		 	algGo.addActionListener(listenerAlg);
		 	
		 valueConstraints.gridx = 1;
		 valueConstraints.gridy = 4;
		 valueConstraints.weighty = 10;
		// graphik = new JTable(0,48);
		 //add(graphik, valueConstraints);
		
	}
	
	protected void listenAuslesen(){
				//Listen auslesen
				String[] ma = new String[maList.size()];
				maList.toArray(ma);
				String[] schichten = new String[schichtList.size()];
				schichtList.toArray(schichten);
		
				//Arrays initialisieren
				maMinuten = new int[maList.size()-1];
				maWunsch = new String[maList.size()-1][7];
				maSchwelle = new int[maList.size()-1];
				maAPunkte = new int[maList.size()-1];
				maBPunkte = new int[maList.size()-1];
				schichtenInfo = new int[schichtList.size()-1][3];
				
				try {
					
					//Inhalt einer Zeile
					String[] inhalt;
					
			//Mitarbeiter laden
					//Statuszeile auslesen
					String[] status = new String[ma[0].codePointCount(0, ma[0].length()) +1 ];
					status = ma[0].split(";");
					
					 for (int i = 1; i < ma.length; i++){
						 //Inhalt einer Zeile auslesen
						 inhalt = new String[ma[i].codePointCount(0, ma[i].length()) +1 ];
						 inhalt = ma[i].split(";");
						 //Inhalt den Arrays zuordnen
						 for (int j = 0; j < inhalt.length; j++){
							 
							 maSchwelle[i-1] = 0;
							 
							 try {
									 switch (status[j]) {
									 
									 case "Wochensoll": 
										 				if (status[j].equals("Wochensoll")){
										 					maMinuten[i-1] = Integer.valueOf(inhalt[j]);
										 				}
										 				break;
									
									 case "Montag":
										 				if(status[j].equals("Montag")){
										 					maWunsch[i-1][0] =  inhalt[j];
										 				}
										 				break;
										 		
									 case "Dienstag":
										 				if(status[j].equals("Dienstag")){
										 					maWunsch[i-1][1] =  inhalt[j];
										 				}
										 				break;
										
									 case "Mittwoch":  
							 							if(status[j].equals("Mittwoch")){
							 								maWunsch[i-1][2] =  inhalt[j];
							 							}
										 				break;
										 				
									 case "Donnerstag": 
				 										if(status[j].equals("Donnerstag")){
				 											maWunsch[i-1][3] =  inhalt[j];
				 										}
										 				break;
										 
									 case "Freitag": 
														if(status[j].equals("Freitag")){
															maWunsch[i-1][4] =  inhalt[j];
														}
										 				break;
										 				
									 case "Samstag": 
										 				if(status[j].equals("Samstag")){
										 					maWunsch[i-1][5] =  inhalt[j];
										 				}
										 				break;
										 	
									 case "Sonntag":  
														if(status[j].equals("Sonntag")){
															maWunsch[i-1][6] =  inhalt[j];
														}
										 				break;
										
									 case "APunkte":
										 				if(status[j].equals("APunkte")){
										 				maAPunkte[i-1] = Integer.valueOf(inhalt[j]);
										 				}
										 				break;
										 				
									 case "BPunkte":
										 				if(status[j].equals("BPunkte")){
										 					maBPunkte[i-1] = Integer.valueOf(inhalt[j]);
										 				}
										 				break;
									
									 case "Schichten":
										 				break;
										 				
									 case "Skills":
										 				break;
									
									default: 
														break;
									 
									 }
								 } catch (ArrayIndexOutOfBoundsException e)
								 {
									 TextLabel.append("\n Statuszeile ungenügend! Daten prüfen:");
								 }
							 
						 }
						 
						 //Ausgabe
						 //TextLabel.append("\n MA" + i + ": Minuten: "+ maMinuten[i] + " A: " + maAPunkte[i] + " B: " + maBPunkte[i] + "\n Wunsch:\n" + maWunsch[i][0] + "\n"+ maWunsch[i][1] + "\n"+ maWunsch[i][2] + "\n"+ maWunsch[i][3] + "\n"+ maWunsch[i][4] + "\n"+ maWunsch[i][5] + "\n"+ maWunsch[i][6] + "\n" );
						 TextLabel.append("\n*Einlesen von MA " + i + " erfolgreich!");
						 }
					 
					 
				//Schichten laden
					 //Statuszeile auslesen
					 status = new String[schichten[0].codePointCount(0, schichten[0].length()) +1 ];
					 status = schichten[0].split(";");
					 
					// TextLabel.append("Schichten Anzahl: " + status.length + "\n");
					 
					 
						 for (int i = 1; i < schichten.length; i++){
							 
							 inhalt = new String[schichten[i].codePointCount(0, schichten[i].length()) +1];
							 inhalt = schichten[i].split(";");
							 
							//TextLabel.append("\n"+ "i: " + i + " Schichten Anzahl: " + schichten.length + " Zeile Infos: " + inhalt.length +  " Status Infos: " + status.length);
							 
							//Inhalt den Arrays zuordnen
							 for (int j = 0; j < inhalt.length; j++){
								 //TextLabel.append("\n j: " + j);
								 try {
										 switch (status[j]) {
										 
										 case "Beginn": 
								 						if(status[j].equals("Beginn")){
								 							schichtenInfo[i-1][0] = Integer.valueOf(inhalt[j]);
								 						}
											 			break;
										
										 case "Ende": 
								 						if(status[j].equals("Ende")){
								 							schichtenInfo[i-1][1] = Integer.valueOf(inhalt[j]);
								 						}
											 			break;
											 
										 case "Dauer":  
											 			if(status[j].equals("Dauer")){
											 				schichtenInfo[i-1][2] = Integer.valueOf(inhalt[j]);
											 			}
											 			break;
										
										default:
														break;
										 }
								 } catch (ArrayIndexOutOfBoundsException e)
								 {
									 TextLabel.append("\n Statuszeile ungenügend! Daten prüfen:");
								 }
							 }
							 
							// schichtenInfo[i][0] = Integer.valueOf(schichten[i].substring(0,4));
							// schichtenInfo[i][1] = Integer.valueOf(schichten[i].substring(5,9));
							// schichtenInfo[i][2] = Integer.valueOf(schichten[i].substring(10,14));
							 
							 //Ausgabe
							
							// String anfang = (schichtenInfo[i][0] / 60) + ":" + (schichtenInfo[i][0] % 60);
							// TextLabel.append("\n Schichtdefinition " + i + ": Anfang: " + anfang + " Uhr, entspricht" + schichtenInfo[i][0] + "Minuten Ende: " + schichtenInfo[i][1] + " Dauer: " + schichtenInfo[i][2]);
							 TextLabel.append("\n*Einlesen von Schicht " + i + " erfolgreich!");
						 }
				} catch (NumberFormatException e) {
					
					TextLabel.append("\n Keine korrekte Syntax!");
				}
		
	}

}
