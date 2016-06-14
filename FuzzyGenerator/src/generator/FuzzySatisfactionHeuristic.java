package generator;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.io.*;
import java.lang.Math;
import java.util.*;
import javax.swing.*;
import java.util.Random;

public class FuzzySatisfactionHeuristic extends Algorithmus{

	private int[][] bedarf = new int[7][48];
	protected int[][] besetzung = new int[7][48];
	//[][0] = Platz/Rang; [][1] = Deckung; [][2]= Generiert mit 0 = nein und 1 = ja
	private int[][] tagesdeckung = new int[7][3];
	private int wochendeckung = 0;
	int id;
	int zaehler = 0;
	int[] maIDa;
	int[] maIDb;
	int[] maIDd;

	public FuzzySatisfactionHeuristic(){
		//Fülle Tagesdeckung
		for (int i = 0;i<7;i++){
			//Rang = Tag - Ausgangslage
			tagesdeckung[i][0] = i;
			//Generiert = 0  Alle Tage noch nicht generiert
			tagesdeckung[i][2] = 0;
		}
		
		//Erstelle Bedarf (evtl auch noch auswählbar machen vorm Start)
		protokolliere("\n-----\n *Bedarf:");
		for(int i =0;i<7;i++){
			protokolliere("\n Tag " + i + ": ");
			for(int j = 0;j<bedarf[i].length;j++){
				if(j<=12){
						bedarf[i][j]=0;
				} else if(j<=24 && ((i*4)%5) >= 4) {
					bedarf[i][j]=3+(i/2);
				} else if(j<=24 && ((i*4)%5) < 4) {
					bedarf[i][j]=3+(((i*4)%5));
				} else if(j<=40) {
					bedarf[i][j]=2+(i/2);
				} else if(j<=47) {
					bedarf[i][j]=0;
				}
				protokolliere(bedarf[i][j] + "");
			}
		}
		
	}
	
	@Override
	public void run(){	
		
		//Sortierung der Wochentage nach der rel. Deckung (Berechnet Wochendeckung)
		sortiereWochentage();
		//Starte Generator
		naechsterTag(1);
		//Start Optimierer
		optimiere(1,6);
		optimiere(1,5);
		optimiere(1,4);
		optimiere(1,3);
		optimiere(1,2);
		optimiere(1,1);
		optimiere(1,0);
		}
	
	private void generiere(int schritt, int tag, int ma){
		
		switch (schritt) {
			
			//1. MA werden tageweise (Montag - Sonntag) in absteigender Reihenfolge nach A-Punkten in die A-Zeiten verplant. 
			//Wenn die relative Tagesdeckung die relative Wochendeckung am jeweiligen Tag erreicht, wird zum nächsten Tag übergegangen. 
			//Dabei werden die Wochentage mit der geringsten relativen Deckung zuerst berücksichtigt.
		
			case 1:
				//Tag als generiert markieren
				tagesdeckung[tag][2] = 1;
				
				
				int schicht = -2;
				int beginnAWunsch = -1;
				int endeAWunsch = -1;
				int laengeAWunsch = -1;
				boolean geplant = false;
				//Prüfe ob der Mitarbeiter für diesen Tag schon verplant ist 
				if(maWunsch[ma][2][tag].equals("keine SchichtDefinition")){
					//Prüfe ob der MA A Wunsch hat an diesem Tag
					if(maWunsch[ma][1][tag].contains("A")){
						//Ausgabe wenn A Wunsch vorhanden.
						//protokolliere("\n\nMitarbeiter " + ma + " wünscht sich " + maWunsch[ma][1][tag] + " an Tag " + tag);
						
						//Prüfe ob passende Schicht zum A-Wunsch
							//Beginn, Länge und Ende des A Wunsches verifizieren
							beginnAWunsch = maWunsch[ma][1][tag].indexOf("A")*30;
							endeAWunsch = (maWunsch[ma][1][tag].lastIndexOf("A")+1)*30;
							laengeAWunsch = endeAWunsch - beginnAWunsch;
							
							protokolliere("\n Mitarbeiter:" + ma + "\n Beginn A Wunsch: " + beginnAWunsch + ",  Ende A Wunsch: " + endeAWunsch + ", Länge A Wunsch: " + laengeAWunsch + " am Tag " + tag);
							//protokolliere("\nAnzahl zu prüfender Schichten: "+ schichtenInfo.length);
							//Passende Schicht suchen
							for (int i =0; i < schichtenInfo.length; i++ ){
								//protokolliere("\nSchicht wird getestet: " + i);
								//Wenn Schichtdefinition zu A-Wunsch passt
								if ((schichtenInfo[i][0] <= beginnAWunsch) && (schichtenInfo[i][1] >= endeAWunsch)){
									//und Schicht gleiche Länge wie A Wunsch hat
									if(schichtenInfo[i][2] == laengeAWunsch){
										//Prüfe, ob Mitarbeiter noch genug Stunden offen hat
										int verplanteStunden = berechneWochenstunden(ma);
										//protokolliere("\nMitarbeiter hat " + verplanteStunden + " Stunden bereits verplant");
										if ((verplanteStunden*60) + schichtenInfo[i][2] <= (maMinuten[ma][1]*60)){
											//Passende Schicht wird ausgewählt
											schicht = i;
											//protokolliere("\nGefundene Schicht = " + (schicht) + " mit der Dauer: " + schichtenInfo[i][2]);
											//protokolliere("\nMitarbeiter " + ma + " kommt damit auf " +(verplanteStunden + (schichtenInfo[i][2]/60)) + " von " + maMinuten[ma][1] + " Wochenstunden.");
											
											//Prüfe ob rel. Tagesdeckung unter rel. Wochendeckung bleibt
											//Wenn es eine passende Schicht gibt
											if(schicht > -1){
												for(int j = (beginnAWunsch/30); j <= ((endeAWunsch/30)-1);j++){
													besetzung[tag][j] +=1;
												}
											}
											//Ausgabe Bedarf/Besetzung Tag
											//protokolliere("\n\n*Tag "+ tag + " mit MA" + ma);
											//protokolliere("\nBedarf: ");
											for(int j = 0; j < bedarf[tag].length;j++){
												//protokolliere("" + bedarf[tag][j]);
											}
											//protokolliere("\nBesetzung: ");
											for(int j = 0; j < besetzung[tag].length;j++){
												//protokolliere("" + besetzung[tag][j]);
											}
											//Berechne Tages- und Wochendeckung neu. 
											sortiereWochentage();
													//Es wird geprüft, ob der MA seine A-Zeit an dem Wochentag bekommen kann, d.h. die relative Tagesdeckung überschreitet nicht die relative Wochendeckung.
													if(tagesdeckung[tag][1]<= wochendeckung){
														
														geplant = true;
														break;
														
													//Wenn nicht, wird zum nächsten Tag übergegangen,	
													} else{
														//Ausgabe
														protokolliere("\n\n*Mitarbeiter" + ma + " wurde an Tag " + tag + " mit Schicht " +  schicht + " nicht verplant, weil die Tagesdeckung " +tagesdeckung[tag][1] + "  kleiner der Wochendeckung: " + wochendeckung);
														//Ma nicht einplanen - Von der Besetzung wieder abziehen
														for(int k = (beginnAWunsch/30); k <= ((endeAWunsch/30)-1);k++){
															
															besetzung[tag][k] -=1;
														}
														
													}

										//Mitarbeiter hat nicht mehr genug Stunden
										} else {
											//protokolliere("Mitarbeiter " + ma + " kann Schicht " + i + " nicht bekommen, da er sonst mit " + (verplanteStunden + (schichtenInfo[i][2]/60)) + " seine Wochenstunden von " + maMinuten[ma][1] + " überschreitet.");
										}
									//Nicht passende Schichtdauer	
									} else {
										//protokolliere("\n Abweichende Dauer von: " + schichtenInfo[i][2]);
									}
								//Schichtdefinition passt nicht (Korridor passt nicht)
								} else {
									//protokolliere("\n Keine Schicht gefunden. Getestet wurde: " + schichtenInfo[i][0] + " <= " + beginnAWunsch + " und " + schichtenInfo[i][1] + " <= " + endeAWunsch );
								}
							//Ende for Schleife durch die Schichten --> keine passende Schicht gefunden
							}
							//Wenn Schicht gefunden und verplant wurde
							if(schicht != -2 && geplant){
								//Der Mitarbeiter wird geplant
								maWunsch[ma][2][tag] = Integer.toString(schicht);
								maWunsch[ma][3][tag] = Integer.toString(beginnAWunsch);
								maWunsch[ma][4][tag] = Integer.toString(endeAWunsch);
								
								//Ausgabe verplante Schicht
								protokolliere("\nMitarbeiter " + ma + "wurde am Tag " +tag+ " mit der Schicht " + maWunsch[ma][2][tag] + " von "+maWunsch[ma][3][tag] + " bis " + maWunsch[ma][3][tag] + "verplant.");
								
								//Wenn weiterer MA vorhanden 
								if (id+1 < maIDa.length){
									//Weiter mit dem nächsten MA gem. Sortierung
									id += 1;
									generiere(1,tag, maIDa[id]);
								//Sonst (also wenn kein weiterer MA vorhanden)
								} else {
									//Nächster Tag gemäß neuer Sortierung
									naechsterTag(1);
								}
							//Wenn Schicht gefunden aber nicht verplant wurde	
							} else if (schicht != -2 && !geplant) {
								//MA zurück sortieren
								sortiereWochentage();
								//Weiter gehen zu nächstem Tag
								naechsterTag(1);
							}
							// Und keine Schicht gefunden wurde
							else if(schicht == -2){
								protokolliere("\nAlle Schichten wurden geprüft. Es war keine passende dabei.");
								//Wenn weiterer MA vorhanden 
								if (id+1 < maIDa.length){
									//Weiter mit dem nächsten MA gem. Sortierung
									id += 1;
									generiere(1,tag, maIDa[id]);
								//Sonst (also wenn kein weiterer MA vorhanden)
								} else {
									//Nächster Tag gemäß neuer Sortierung
									naechsterTag(1);
								}
							}
					//Keinen A Wunsch
					}else {
						//Protokolliere Prüfergebnis
						protokolliere("\nMitarbeiter " + ma + " wünscht sich " + maWunsch[ma][1][tag] + " an Tag " + tag + ". \nDas ist kein A Wunsch.");
						//Prüfe ob weiterer Ma vorhanden
						if (id+1 < maIDa.length){
							//Weiter mit dem nächsten MA gem. Sortierung
							id +=1;
							generiere(1,tag, maIDa[id]);
							//Sonst (also wenn kein weiterer MA vorhanden)
						} else {
							//Nächster Tag gemäß neuer Sortierung
							naechsterTag(1);
						}
					}
				//Mitarbeiter wurde schon verplant
				}else {
					//Ausgabe
					protokolliere("\n\nMitarbeiter "+ ma + " wurde bereits in vorherigen Schritten am Tag " +tag+ " mit der Schicht " + maWunsch[ma][2][tag] + " von "+maWunsch[ma][3][tag] + " bis " + maWunsch[ma][3][tag] + " verplant.");
					if (id+1 < maIDa.length){
						//Weiter mit dem nächsten MA gem. Sortierung 
						id+=1;
						generiere(1,tag, maIDa[id]);
						//Sonst (also wenn kein weiterer MA vorhanden)
					} else {
						//Nächster Tag gemäß neuer Sortierung
						naechsterTag(1);
					}
				}
				break;
			case 2:
				//Tag als generiert markieren
				tagesdeckung[tag][2] = 2;
				
				
				schicht = -2;
				int beginnBWunsch = -1;
				int endeBWunsch = -1;
				int laengeBWunsch = -1;
				geplant = false;
				//Prüfe ob der Mitarbeiter für diesen Tag schon verplant ist 
				if(maWunsch[ma][2][tag].equals("keine SchichtDefinition")){
					//Prüfe ob der MA nur B Wunsch hat an diesem Tag
					if(maWunsch[ma][1][tag].contains("B") && (maWunsch[ma][1][tag].contains("A") == false)){
						//Ausgabe wenn B Wunsch vorhanden.
						//protokolliere("\n\nMitarbeiter " + ma + " wünscht sich " + maWunsch[ma][1][tag] + " an Tag " + tag);
						
						//Prüfe ob passende Schicht zum B-Wunsch
							//Beginn, Länge und Ende des B Wunsches verifizieren
							beginnBWunsch = maWunsch[ma][1][tag].indexOf("B")*30;
							endeBWunsch = (maWunsch[ma][1][tag].lastIndexOf("B")+1)*30;
							laengeBWunsch = endeBWunsch - beginnBWunsch;
							
							protokolliere("\n Mitarbeiter:" + ma + "\n Beginn B Wunsch: " + beginnBWunsch + ",  Ende B Wunsch: " + endeBWunsch + ", Länge B Wunsch: " + laengeBWunsch + " am Tag " + tag);
							//protokolliere("\nAnzahl zu prüfender Schichten: "+ schichtenInfo.length);
							//Passende Schicht suchen
							for (int i =0; i < schichtenInfo.length; i++ ){
								//protokolliere("\nSchicht wird getestet: " + i);
								//Wenn Schichtdefinition zu B-Wunsch passt
								if (((schichtenInfo[i][0] +  schichtenInfo[i][2]) <= endeBWunsch) && ((schichtenInfo[i][1] -  schichtenInfo[i][2]) >= beginnBWunsch)){
										//Prüfe, ob Mitarbeiter noch genug Stunden offen hat
										int verplanteStunden = berechneWochenstunden(ma);
										//protokolliere("\nMitarbeiter hat " + verplanteStunden + " Stunden bereits verplant");
										if ((verplanteStunden*60) + schichtenInfo[i][2] <= (maMinuten[ma][1]*60)){
											//Passende Schicht wird ausgewählt
											schicht = i;
											//protokolliere("\nGefundene Schicht = " + (schicht) + " mit der Dauer: " + schichtenInfo[i][2]);
											//protokolliere("\nMitarbeiter " + ma + " kommt damit auf " +(verplanteStunden + (schichtenInfo[i][2]/60)) + " von " + maMinuten[ma][1] + " Wochenstunden.");
											//Prüfe ob rel. Tagesdeckung unter rel. Wochendeckung bleibt
							//Wenn es eine passende Schicht gibt
							if(schicht > -1){
								for(int j = (beginnBWunsch/30); j <= (((beginnBWunsch + schichtenInfo[schicht][2])/30)-1);j++){
									besetzung[tag][j] +=1;
								}
							
							//Ausgabe Bedarf/Besetzung Tag
							//protokolliere("\n\n*Tag "+ tag + " mit MA" + ma);
							//protokolliere("\nBedarf: ");
							for(int j = 0; j < bedarf[tag].length;j++){
								//protokolliere("" + bedarf[tag][j]);
							}
							//protokolliere("\nBesetzung: ");
							for(int j = 0; j < besetzung[tag].length;j++){
								//protokolliere("" + besetzung[tag][j]);
							}
							//Berechne Tages- und Wochendeckung neu. 
							sortiereWochentage();
									//Es wird geprüft, ob der MA seine B-Zeit an dem Wochentag bekommen kann, d.h. die relative Tagesdeckung überschreitet nicht die relative Wochendeckung.
									if(tagesdeckung[tag][1]<= wochendeckung){
										
										geplant = true;
										break;
										
									//Wenn nicht, wird zum nächsten Tag übergegangen,	
									} else{
										//Ausgabe
										protokolliere("\n\n*Mitarbeiter" + ma + " wurde an Tag " + tag + " mit Schicht " +  schicht + " nicht verplant, weil die Tagesdeckung " +tagesdeckung[tag][1] + "  kleiner der Wochendeckung: " + wochendeckung);
										//Ma nicht einplanen - Von der Besetzung wieder abziehen
										for(int k = (beginnBWunsch/30); k <= (((beginnBWunsch + schichtenInfo[schicht][2])/30)-1);k++){
											
											besetzung[tag][k] -=1;
										}
										
									}
							}

										//Mitarbeiter hat nicht mehr genug Stunden
										} else {
											//protokolliere("Mitarbeiter " + ma + " kann Schicht " + i + " nicht bekommen, da er sonst mit " + (verplanteStunden + (schichtenInfo[i][2]/60)) + " seine Wochenstunden von " + maMinuten[ma][1] + " überschreitet.");
										}
								//Schichtdefinition passt nicht (Korridor passt nicht)
								} else {
									//protokolliere("\n Keine Schicht gefunden. Getestet wurde: " + schichtenInfo[i][0] + " <= " + beginnBWunsch + " und " + schichtenInfo[i][1] + " <= " + endeBWunsch );
								}
							//Ende for Schleife durch die Schichten --> keine passende Schicht gefunden
							}
							//Wenn Schicht gefunden und geplant wurde
							if (schicht != -2 && geplant){
								//Der Mitarbeiter wird geplant
								maWunsch[ma][2][tag] = Integer.toString(schicht);
								maWunsch[ma][3][tag] = Integer.toString(beginnBWunsch);
								maWunsch[ma][4][tag] = Integer.toString((beginnBWunsch + schichtenInfo[schicht][2]));
								
								//Ausgabe verplante Schicht
								protokolliere("\nMitarbeiter " + ma + "wurde am Tag " +tag+ " mit der Schicht " + maWunsch[ma][2][tag] + " von "+maWunsch[ma][3][tag] + " bis " + maWunsch[ma][3][tag] + "verplant.");
								//Wenn weiterer MA vorhanden 
								if (id+1 < maIDb.length){
									//Weiter mit dem nächsten MA gem. Sortierung
									id += 1;
									generiere(2,tag, maIDb[id]);
								//Sonst (also wenn kein weiterer MA vorhanden)
								} else {
									//Nächster Tag gemäß neuer Sortierung
									naechsterTag(2);
								}
							//Wenn Schicht gefunden aber nicht geplant wurde	
							} else if (schicht != -2 && !geplant){
								
								//MA zurück sortieren
								sortiereWochentage();
								//Weiter gehen zu nächstem Tag
								naechsterTag(2);
							// Und wenn keine Schicht gefunden wurde
							}else if(schicht == -2){
								protokolliere("\nAlle Schichten wurden geprüft. Es war keine passende dabei.");
								//Wenn weiterer MA vorhanden 
								if (id+1 < maIDb.length){
									//Weiter mit dem nächsten MA gem. Sortierung
									id += 1;
									generiere(2,tag, maIDb[id]);
								//Sonst (also wenn kein weiterer MA vorhanden)
								} else {
									//Nächster Tag gemäß neuer Sortierung
									naechsterTag(2);
								}
							}
					//Keinen A Wunsch
					}else {
						//Protokolliere Prüfergebnis
						protokolliere("\nMitarbeiter " + ma + " wünscht sich " + maWunsch[ma][1][tag] + " an Tag " + tag + ". \nDas ist kein B Wunsch.");
						//Prüfe ob weiterer Ma vorhanden
						if (id+1 < maIDb.length){
							//Weiter mit dem nächsten MA gem. Sortierung
							id +=1;
							generiere(2,tag, maIDb[id]);
							//Sonst (also wenn kein weiterer MA vorhanden)
						} else {
							//Nächster Tag gemäß neuer Sortierung
							naechsterTag(2);
						}
					}
				//Mitarbeiter wurde schon verplant
				}else {
					//Ausgabe
					protokolliere("\n\nMitarbeiter "+ ma + " wurde bereits in vorherigen Schritten am Tag " +tag+ " mit der Schicht " + maWunsch[ma][2][tag] + " von "+maWunsch[ma][3][tag] + " bis " + maWunsch[ma][3][tag] + " verplant.");
					if (id+1 < maIDb.length){
						//Weiter mit dem nächsten MA gem. Sortierung 
						id+=1;
						generiere(2,tag, maIDb[id]);
						//Sonst (also wenn kein weiterer MA vorhanden)
					} else {
						//Nächster Tag gemäß neuer Sortierung
						naechsterTag(2);
					}
				}
				break;
				
			case 3:
				//Tag als generiert markieren
				tagesdeckung[tag][2] = 3;
				
				
				schicht = -2;
				int beginnDWunsch = -1;
				int endeDWunsch = -1;
				int laengeDWunsch = -1;
				geplant = false;
				
				//Prüfe ob der Mitarbeiter für diesen Tag schon verplant ist 
				if(maWunsch[ma][2][tag].equals("keine SchichtDefinition")){
					//Prüfe ob der MA nur D Wunsch hat an diesem Tag
					if(maWunsch[ma][1][tag].contains("D") && (maWunsch[ma][1][tag].contains("A") == false)&& (maWunsch[ma][1][tag].contains("B") == false)){
						//Ausgabe wenn D Wunsch vorhanden.
						//protokolliere("\n\nMitarbeiter " + ma + " wünscht sich " + maWunsch[ma][1][tag] + " an Tag " + tag);
						
						//Prüfe ob passende Schicht zum D-Wunsch
							//Beginn, Länge und Ende des D Wunsches verifizieren
							beginnDWunsch = maWunsch[ma][1][tag].indexOf("D")*30;
							endeDWunsch = (maWunsch[ma][1][tag].lastIndexOf("D")+1)*30;
							laengeDWunsch = endeDWunsch - beginnDWunsch;
							
							protokolliere("\n Mitarbeiter:" + ma + "\n Beginn D Wunsch: " + beginnDWunsch + ",  Ende B Wunsch: " + endeDWunsch + ", Länge D Wunsch: " + laengeDWunsch + " am Tag " + tag);
							//protokolliere("\nAnzahl zu prüfender Schichten: "+ schichtenInfo.length);
							//Passende Schicht suchen
							for (int i =0; i < schichtenInfo.length; i++ ){
								//protokolliere("\nSchicht wird getestet: " + i);
								//Wenn Schichtdefinition zu D-Wunsch passt 
								if (((schichtenInfo[i][0] + schichtenInfo[i][2]) <= endeDWunsch) && ((schichtenInfo[i][1] - schichtenInfo[i][2]) >= beginnDWunsch)){
									//und Schicht gleiche Länge wie D Wunsch hat
									//if(schichtenInfo[i][2] == laengeDWunsch){
										//Prüfe, ob Mitarbeiter noch genug Stunden offen hat
										int verplanteStunden = berechneWochenstunden(ma);
										//protokolliere("\nMitarbeiter hat " + verplanteStunden + " Stunden bereits verplant");
										if ((verplanteStunden*60) + schichtenInfo[i][2] <= (maMinuten[ma][1]*60)){
											//Passende Schicht wird ausgewählt
											schicht = i;
											//protokolliere("\nGefundene Schicht = " + (schicht) + " mit der Dauer: " + schichtenInfo[i][2]);
											//protokolliere("\nMitarbeiter " + ma + " kommt damit auf " +(verplanteStunden + (schichtenInfo[i][2]/60)) + " von " + maMinuten[ma][1] + " Wochenstunden.");
//											//Prüfe ob rel. Tagesdeckung unter rel. Wochendeckung bleibt
											//Wenn es eine passende Schicht gibt
											if(schicht > -1){
												
												if(schichtenInfo[i][0] <= beginnDWunsch){
													
													for(int j = (beginnDWunsch/30); j <= (((beginnDWunsch + schichtenInfo[schicht][2])/30)-1);j++){
														besetzung[tag][j] +=1;
													}
													
												} else
												if(schichtenInfo[i][0] > beginnDWunsch) {
													
													for(int j = (schichtenInfo[i][0]/30); j <= (((schichtenInfo[i][0] + schichtenInfo[schicht][2])/30)-1);j++){
														besetzung[tag][j] +=1;
													}
														
												}
												
												
											
											//Ausgabe Bedarf/Besetzung Tag
											//protokolliere("\n\n*Tag "+ tag + " mit MA" + ma);
											//protokolliere("\nBedarf: ");
											for(int j = 0; j < bedarf[tag].length;j++){
												//protokolliere("" + bedarf[tag][j]);
											}
											//protokolliere("\nBesetzung: ");
											for(int j = 0; j < besetzung[tag].length;j++){
												//protokolliere("" + besetzung[tag][j]);
											}
											//Berechne Tages- und Wochendeckung neu. 
											sortiereWochentage();

													//Es wird geprüft, ob der MA seine D-Zeit an dem Wochentag bekommen kann, d.h. die relative Tagesdeckung überschreitet nicht die relative Wochendeckung.
													if(tagesdeckung[tag][1]<= wochendeckung){
														geplant = true;
														break;
														
													//Wenn nicht, wird zum nächsten Tag übergegangen,	
													} else{
														//Ausgabe
														protokolliere("\n\n*Mitarbeiter" + ma + " wurde an Tag " + tag + " mit Schicht " +  schicht + " nicht verplant, weil die Tagesdeckung " +tagesdeckung[tag][1] + "  kleiner der Wochendeckung: " + wochendeckung);
														//Ma nicht einplanen - Von der Besetzung wieder abziehen
														if(schichtenInfo[i][0] <= beginnDWunsch){
															
															for(int j = (beginnDWunsch/30); j <= (((beginnDWunsch + schichtenInfo[schicht][2])/30)-1);j++){
																besetzung[tag][j] -=1;
															}
															
														} else
														if(schichtenInfo[i][0] > beginnDWunsch) {
															
															for(int j = (schichtenInfo[i][0]/30); j <= (((schichtenInfo[i][0] + schichtenInfo[schicht][2])/30)-1);j++){
																besetzung[tag][j] -=1;
															}
																
														}
														
													}}
										
										

										//Mitarbeiter hat nicht mehr genug Stunden
										} else {
											protokolliere("\nMitarbeiter " + ma + " kann Schicht " + i + " nicht bekommen, da er sonst mit " + (verplanteStunden + (schichtenInfo[i][2]/60)) + " seine Wochenstunden von " + maMinuten[ma][1] + " überschreitet.");
										
										}
								//Schichtdefinition passt nicht (Korridor passt nicht)
								} else {
									protokolliere("\n D Korridor nicht in Schicht. Getestet wurde: " + schichtenInfo[i][0] + " <= " + beginnDWunsch + " und " + schichtenInfo[i][1] + " >= " + endeDWunsch );
								}
							//Ende for Schleife durch die Schichten --> keine passende Schicht gefunden
							}
							//Wenn Schicht gefunden und geplant
							if(schicht != -2 && geplant){
								//Der Mitarbeiter wird geplant
								maWunsch[ma][2][tag] = Integer.toString(schicht);
								maWunsch[ma][3][tag] = Integer.toString(beginnDWunsch);
								maWunsch[ma][4][tag] = Integer.toString((beginnDWunsch + schichtenInfo[schicht][2]));
								
								//Ausgabe verplante Schicht
								protokolliere("\nMitarbeiter " + ma + "wurde am Tag " +tag+ " mit der Schicht " + maWunsch[ma][2][tag] + " von "+maWunsch[ma][3][tag] + " bis " + maWunsch[ma][3][tag] + "verplant.");
								
								//Wenn weiterer MA vorhanden 
								if (id+1 < maIDd.length){
									//Weiter mit dem nächsten MA gem. Sortierung
									id += 1;
									generiere(3,tag, maIDd[id]);
								//Sonst (also wenn kein weiterer MA vorhanden)
								} else {
									//Nächster Tag gemäß neuer Sortierung
									naechsterTag(3);
								}
							//Wenn Schicht gefunden und nicht geplant	
							} else if (schicht != -2 && !geplant){
								//MA zurück sortieren
								sortiereWochentage();
								//Weiter gehen zu nächstem Tag
								naechsterTag(3);
								
							// Und wenn keine Schicht gefunden wurde
							} else if(schicht == -2){
								protokolliere("\nAlle Schichten wurden geprüft. Es war keine passende dabei.");
								//Wenn weiterer MA vorhanden 
								if (id+1 < maIDd.length){
									//Weiter mit dem nächsten MA gem. Sortierung
									id += 1;
									generiere(3,tag, maIDd[id]);
								//Sonst (also wenn kein weiterer MA vorhanden)
								} else {
									//Nächster Tag gemäß neuer Sortierung
									naechsterTag(3);
								}
							}
					//Keinen A Wunsch
					}else {
						//Protokolliere Prüfergebnis
						protokolliere("\nMitarbeiter " + ma + " wünscht sich " + maWunsch[ma][1][tag] + " an Tag " + tag + ". \nDas ist kein D Wunsch.");
						//Prüfe ob weiterer Ma vorhanden
						if (id+1 < maIDd.length){
							//Weiter mit dem nächsten MA gem. Sortierung
							id +=1;
							generiere(3,tag, maIDd[id]);
							//Sonst (also wenn kein weiterer MA vorhanden)
						} else {
							//Nächster Tag gemäß neuer Sortierung
							naechsterTag(3);
						}
					}
				//Mitarbeiter wurde schon verplant
				}else {
					//Ausgabe
					protokolliere("\n\nMitarbeiter "+ ma + " wurde bereits in vorherigen Schritten am Tag " +tag+ " mit der Schicht " + maWunsch[ma][2][tag] + " von "+maWunsch[ma][3][tag] + " bis " + maWunsch[ma][3][tag] + " verplant.");
					if (id+1 < maIDd.length){
						//Weiter mit dem nächsten MA gem. Sortierung 
						id+=1;
						generiere(3,tag, maIDd[id]);
						//Sonst (also wenn kein weiterer MA vorhanden)
					} else {
						//Nächster Tag gemäß neuer Sortierung
						naechsterTag(3);
					}
				}
				
				break;
				
			case 4:
				//Tag als generiert markieren
				tagesdeckung[tag][2] = 4;
				
				
				schicht = -2;
				beginnAWunsch = -1;
				endeAWunsch = -1;
				laengeAWunsch = -1;
				geplant = false;
				//Prüfe ob der Mitarbeiter für diesen Tag schon verplant ist 
				if(maWunsch[ma][2][tag].equals("keine SchichtDefinition")){
					//Prüfe ob der MA A Wunsch hat an diesem Tag
					if(maWunsch[ma][1][tag].contains("A")){
						//Ausgabe wenn A Wunsch vorhanden.
						//protokolliere("\n\nMitarbeiter " + ma + " wünscht sich " + maWunsch[ma][1][tag] + " an Tag " + tag);
						
						//Prüfe ob passende Schicht zum A-Wunsch
							//Beginn, Länge und Ende des A Wunsches verifizieren
							beginnAWunsch = maWunsch[ma][1][tag].indexOf("A")*30;
							endeAWunsch = (maWunsch[ma][1][tag].lastIndexOf("A")+1)*30;
							laengeAWunsch = endeAWunsch - beginnAWunsch;
							
							protokolliere("\n Mitarbeiter:" + ma + "\n Beginn A Wunsch: " + beginnAWunsch + ",  Ende A Wunsch: " + endeAWunsch + ", Länge A Wunsch: " + laengeAWunsch + " am Tag " + tag);
							//protokolliere("\nAnzahl zu prüfender Schichten: "+ schichtenInfo.length);
							//Passende Schicht suchen
							for (int i =0; i < schichtenInfo.length; i++ ){
								//protokolliere("\nSchicht wird getestet: " + i);
								//Wenn Schichtdefinition zu A-Wunsch passt
								if ((schichtenInfo[i][0] <= beginnAWunsch) && (schichtenInfo[i][1] >= endeAWunsch)){
									//und Schicht gleiche Länge wie A Wunsch hat
									if(schichtenInfo[i][2] == laengeAWunsch){
										//Prüfe, ob Mitarbeiter noch genug Stunden offen hat
										int verplanteStunden = berechneWochenstunden(ma);
										//protokolliere("\nMitarbeiter hat " + verplanteStunden + " Stunden bereits verplant");
										if ((verplanteStunden*60) + schichtenInfo[i][2] <= (maMinuten[ma][1]*60)){
											//Passende Schicht wird ausgewählt
											schicht = i;
											//protokolliere("\nGefundene Schicht = " + (schicht) + " mit der Dauer: " + schichtenInfo[i][2]);
											//protokolliere("\nMitarbeiter " + ma + " kommt damit auf " +(verplanteStunden + (schichtenInfo[i][2]/60)) + " von " + maMinuten[ma][1] + " Wochenstunden.");
											
											
											//Wenn es eine passende Schicht gibt
											if(schicht > -1){
												for(int j = (beginnAWunsch/30); j <= ((endeAWunsch/30)-1);j++){
													besetzung[tag][j] +=1;
												}
											
											//Ausgabe Bedarf/Besetzung Tag
											//protokolliere("\n\n*Tag "+ tag + " mit MA" + ma);
											//protokolliere("\nBedarf: ");
											for(int j = 0; j < bedarf[tag].length;j++){
												//protokolliere("" + bedarf[tag][j]);
											}
											//protokolliere("\nBesetzung: ");
											for(int j = 0; j < besetzung[tag].length;j++){
												//protokolliere("" + besetzung[tag][j]);
											}
											//Berechne Tages- und Wochendeckung neu. 
											sortiereWochentage();
														
														geplant = true;
														break;
											}
										
										//Mitarbeiter hat nicht mehr genug Stunden
										} else {
											//protokolliere("Mitarbeiter " + ma + " kann Schicht " + i + " nicht bekommen, da er sonst mit " + (verplanteStunden + (schichtenInfo[i][2]/60)) + " seine Wochenstunden von " + maMinuten[ma][1] + " überschreitet.");
										}
									//Nicht passende Schichtdauer	
									} else {
										//protokolliere("\n Abweichende Dauer von: " + schichtenInfo[i][2]);
									}
								//Schichtdefinition passt nicht (Korridor passt nicht)
								} else {
									//protokolliere("\n Keine Schicht gefunden. Getestet wurde: " + schichtenInfo[i][0] + " <= " + beginnAWunsch + " und " + schichtenInfo[i][1] + " <= " + endeAWunsch );
								}
							//Ende for Schleife durch die Schichten --> keine passende Schicht gefunden
							}
							//Wenn Schicht gefunden und verplant wurde
							if(schicht != -2 && geplant){
								//Der Mitarbeiter wird geplant
								maWunsch[ma][2][tag] = Integer.toString(schicht);
								maWunsch[ma][3][tag] = Integer.toString(beginnAWunsch);
								maWunsch[ma][4][tag] = Integer.toString(endeAWunsch);
								
								//Ausgabe verplante Schicht
								protokolliere("\nMitarbeiter " + ma + "wurde am Tag " +tag+ " mit der Schicht " + maWunsch[ma][2][tag] + " von "+maWunsch[ma][3][tag] + " bis " + maWunsch[ma][3][tag] + "verplant.");
								
								//Wenn weiterer MA vorhanden 
								if (id+1 < maIDa.length){
									//Weiter mit dem nächsten MA gem. Sortierung
									id += 1;
									generiere(4,tag, maIDa[id]);
								//Sonst (also wenn kein weiterer MA vorhanden)
								} else {
									//Nächster Tag gemäß neuer Sortierung
									naechsterTag(4);
								}
							//Wenn Schicht gefunden aber nicht verplant wurde	
							} else if (schicht != -2 && !geplant) {
								//MA zurück sortieren
								sortiereWochentage();
								//Weiter gehen zu nächstem Tag
								naechsterTag(4);
							}
							// Und keine Schicht gefunden wurde
							else if(schicht == -2){
								protokolliere("\nAlle Schichten wurden geprüft. Es war keine passende dabei.");
								//Wenn weiterer MA vorhanden 
								if (id+1 < maIDa.length){
									//Weiter mit dem nächsten MA gem. Sortierung
									id += 1;
									generiere(4,tag, maIDa[id]);
								//Sonst (also wenn kein weiterer MA vorhanden)
								} else {
									//Nächster Tag gemäß neuer Sortierung
									naechsterTag(4);
								}
							}
					//Keinen A Wunsch
					}else {
						//Protokolliere Prüfergebnis
						protokolliere("\nMitarbeiter " + ma + " wünscht sich " + maWunsch[ma][1][tag] + " an Tag " + tag + ". \nDas ist kein A Wunsch.");
						//Prüfe ob weiterer Ma vorhanden
						if (id+1 < maIDa.length){
							//Weiter mit dem nächsten MA gem. Sortierung
							id +=1;
							generiere(4,tag, maIDa[id]);
							//Sonst (also wenn kein weiterer MA vorhanden)
						} else {
							//Nächster Tag gemäß neuer Sortierung
							naechsterTag(4);
						}
					}
				//Mitarbeiter wurde schon verplant
				}else {
					//Ausgabe
					protokolliere("\n\nMitarbeiter "+ ma + " wurde bereits in vorherigen Schritten am Tag " +tag+ " mit der Schicht " + maWunsch[ma][2][tag] + " von "+maWunsch[ma][3][tag] + " bis " + maWunsch[ma][3][tag] + " verplant.");
					if (id+1 < maIDa.length){
						//Weiter mit dem nächsten MA gem. Sortierung 
						id+=1;
						generiere(4,tag, maIDa[id]);
						//Sonst (also wenn kein weiterer MA vorhanden)
					} else {
						//Nächster Tag gemäß neuer Sortierung
						naechsterTag(4);
					}
				}
				break;

				
			case 5:
				//Tag als generiert markieren
				tagesdeckung[tag][2] = 5;
				
				
				schicht = -2;
				beginnBWunsch = -1;
				endeBWunsch = -1;
				laengeBWunsch = -1;
				geplant = false;
				//Prüfe ob der Mitarbeiter für diesen Tag schon verplant ist 
				if(maWunsch[ma][2][tag].equals("keine SchichtDefinition")){
					//Prüfe ob der MA nur B Wunsch hat an diesem Tag
					if(maWunsch[ma][1][tag].contains("B") && (maWunsch[ma][1][tag].contains("A") == false)){
						//Ausgabe wenn B Wunsch vorhanden.
						//protokolliere("\n\nMitarbeiter " + ma + " wünscht sich " + maWunsch[ma][1][tag] + " an Tag " + tag);
						
						//Prüfe ob passende Schicht zum B-Wunsch
							//Beginn, Länge und Ende des B Wunsches verifizieren
							beginnBWunsch = maWunsch[ma][1][tag].indexOf("B")*30;
							endeBWunsch = (maWunsch[ma][1][tag].lastIndexOf("B")+1)*30;
							laengeBWunsch = endeBWunsch - beginnBWunsch;
							
							protokolliere("\n Mitarbeiter:" + ma + "\n Beginn B Wunsch: " + beginnBWunsch + ",  Ende B Wunsch: " + endeBWunsch + ", Länge B Wunsch: " + laengeBWunsch + " am Tag " + tag);
							//protokolliere("\nAnzahl zu prüfender Schichten: "+ schichtenInfo.length);
							//Passende Schicht suchen
							for (int i =0; i < schichtenInfo.length; i++ ){
								//protokolliere("\nSchicht wird getestet: " + i);
								//Wenn Schichtdefinition zu B-Wunsch passt
								if (((schichtenInfo[i][0] +  schichtenInfo[i][2]) <= endeBWunsch) && ((schichtenInfo[i][1] -  schichtenInfo[i][2]) >= beginnBWunsch)){
										//Prüfe, ob Mitarbeiter noch genug Stunden offen hat
										int verplanteStunden = berechneWochenstunden(ma);
										//protokolliere("\nMitarbeiter hat " + verplanteStunden + " Stunden bereits verplant");
										if ((verplanteStunden*60) + schichtenInfo[i][2] <= (maMinuten[ma][1]*60)){
											//Passende Schicht wird ausgewählt
											schicht = i;
											//protokolliere("\nGefundene Schicht = " + (schicht) + " mit der Dauer: " + schichtenInfo[i][2]);
											//protokolliere("\nMitarbeiter " + ma + " kommt damit auf " +(verplanteStunden + (schichtenInfo[i][2]/60)) + " von " + maMinuten[ma][1] + " Wochenstunden.");
											//Prüfe ob rel. Tagesdeckung unter rel. Wochendeckung bleibt
							//Wenn es eine passende Schicht gibt
							if(schicht > -1){
								for(int j = (beginnBWunsch/30); j <= (((beginnBWunsch + schichtenInfo[schicht][2])/30)-1);j++){
									besetzung[tag][j] +=1;
								}
							
							//Ausgabe Bedarf/Besetzung Tag
							//protokolliere("\n\n*Tag "+ tag + " mit MA" + ma);
							//protokolliere("\nBedarf: ");
							for(int j = 0; j < bedarf[tag].length;j++){
								//protokolliere("" + bedarf[tag][j]);
							}
							//protokolliere("\nBesetzung: ");
							for(int j = 0; j < besetzung[tag].length;j++){
								//protokolliere("" + besetzung[tag][j]);
							}
							//Berechne Tages- und Wochendeckung neu. 
							sortiereWochentage();
									
										
										geplant = true;
										break;
										
							}

										//Mitarbeiter hat nicht mehr genug Stunden
										} else {
											//protokolliere("Mitarbeiter " + ma + " kann Schicht " + i + " nicht bekommen, da er sonst mit " + (verplanteStunden + (schichtenInfo[i][2]/60)) + " seine Wochenstunden von " + maMinuten[ma][1] + " überschreitet.");
										}
								//Schichtdefinition passt nicht (Korridor passt nicht)
								} else {
									//protokolliere("\n Keine Schicht gefunden. Getestet wurde: " + schichtenInfo[i][0] + " <= " + beginnBWunsch + " und " + schichtenInfo[i][1] + " <= " + endeBWunsch );
								}
							//Ende for Schleife durch die Schichten --> keine passende Schicht gefunden
							}
							//Wenn Schicht gefunden und geplant wurde
							if (schicht != -2 && geplant){
								//Der Mitarbeiter wird geplant
								maWunsch[ma][2][tag] = Integer.toString(schicht);
								maWunsch[ma][3][tag] = Integer.toString(beginnBWunsch);
								maWunsch[ma][4][tag] = Integer.toString((beginnBWunsch + schichtenInfo[schicht][2]));
								
								//Ausgabe verplante Schicht
								protokolliere("\nMitarbeiter " + ma + "wurde am Tag " +tag+ " mit der Schicht " + maWunsch[ma][2][tag] + " von "+maWunsch[ma][3][tag] + " bis " + maWunsch[ma][3][tag] + "verplant.");
								//Wenn weiterer MA vorhanden 
								if (id+1 < maIDb.length){
									//Weiter mit dem nächsten MA gem. Sortierung
									id += 1;
									generiere(5,tag, maIDb[id]);
								//Sonst (also wenn kein weiterer MA vorhanden)
								} else {
									//Nächster Tag gemäß neuer Sortierung
									naechsterTag(5);
								}
							//Wenn Schicht gefunden aber nicht geplant wurde	
							} else if (schicht != -2 && !geplant){
								
								//MA zurück sortieren
								sortiereWochentage();
								//Weiter gehen zu nächstem Tag
								naechsterTag(5);
							// Und wenn keine Schicht gefunden wurde
							}else if(schicht == -2){
								protokolliere("\nAlle Schichten wurden geprüft. Es war keine passende dabei.");
								//Wenn weiterer MA vorhanden 
								if (id+1 < maIDb.length){
									//Weiter mit dem nächsten MA gem. Sortierung
									id += 1;
									generiere(5,tag, maIDb[id]);
								//Sonst (also wenn kein weiterer MA vorhanden)
								} else {
									//Nächster Tag gemäß neuer Sortierung
									naechsterTag(5);
								}
							}
					//Keinen A Wunsch
					}else {
						//Protokolliere Prüfergebnis
						protokolliere("\nMitarbeiter " + ma + " wünscht sich " + maWunsch[ma][1][tag] + " an Tag " + tag + ". \nDas ist kein B Wunsch.");
						//Prüfe ob weiterer Ma vorhanden
						if (id+1 < maIDb.length){
							//Weiter mit dem nächsten MA gem. Sortierung
							id +=1;
							generiere(5,tag, maIDb[id]);
							//Sonst (also wenn kein weiterer MA vorhanden)
						} else {
							//Nächster Tag gemäß neuer Sortierung
							naechsterTag(5);
						}
					}
				//Mitarbeiter wurde schon verplant
				}else {
					//Ausgabe
					protokolliere("\n\nMitarbeiter "+ ma + " wurde bereits in vorherigen Schritten am Tag " +tag+ " mit der Schicht " + maWunsch[ma][2][tag] + " von "+maWunsch[ma][3][tag] + " bis " + maWunsch[ma][3][tag] + " verplant.");
					if (id+1 < maIDb.length){
						//Weiter mit dem nächsten MA gem. Sortierung 
						id+=1;
						generiere(5,tag, maIDb[id]);
						//Sonst (also wenn kein weiterer MA vorhanden)
					} else {
						//Nächster Tag gemäß neuer Sortierung
						naechsterTag(5);
					}
				}
				break;
				
			case 6:
				//Tag als generiert markieren
				tagesdeckung[tag][2] = 6;
				
				
				schicht = -2;
				beginnDWunsch = -1;
				endeDWunsch = -1;
				laengeDWunsch = -1;
				geplant = false;
				
				//Prüfe ob der Mitarbeiter für diesen Tag schon verplant ist 
				if(maWunsch[ma][2][tag].equals("keine SchichtDefinition")){
					//Prüfe ob der MA nur D Wunsch hat an diesem Tag
					if(maWunsch[ma][1][tag].contains("D") && (maWunsch[ma][1][tag].contains("A") == false)&& (maWunsch[ma][1][tag].contains("B") == false)){
						//Ausgabe wenn D Wunsch vorhanden.
						//protokolliere("\n\nMitarbeiter " + ma + " wünscht sich " + maWunsch[ma][1][tag] + " an Tag " + tag);
						
						//Prüfe ob passende Schicht zum D-Wunsch
							//Beginn, Länge und Ende des D Wunsches verifizieren
							beginnDWunsch = maWunsch[ma][1][tag].indexOf("D")*30;
							endeDWunsch = (maWunsch[ma][1][tag].lastIndexOf("D")+1)*30;
							laengeDWunsch = endeDWunsch - beginnDWunsch;
							
							protokolliere("\n Mitarbeiter:" + ma + "\n Beginn D Wunsch: " + beginnDWunsch + ",  Ende B Wunsch: " + endeDWunsch + ", Länge D Wunsch: " + laengeDWunsch + " am Tag " + tag);
							//protokolliere("\nAnzahl zu prüfender Schichten: "+ schichtenInfo.length);
							//Passende Schicht suchen
							for (int i =0; i < schichtenInfo.length; i++ ){
								//protokolliere("\nSchicht wird getestet: " + i);
								//Wenn Schichtdefinition zu D-Wunsch passt 
								if (((schichtenInfo[i][0] + schichtenInfo[i][2]) <= endeDWunsch) && ((schichtenInfo[i][1] - schichtenInfo[i][2]) >= beginnDWunsch)){
									//und Schicht gleiche Länge wie D Wunsch hat
									//if(schichtenInfo[i][2] == laengeDWunsch){
										//Prüfe, ob Mitarbeiter noch genug Stunden offen hat
										int verplanteStunden = berechneWochenstunden(ma);
										//protokolliere("\nMitarbeiter hat " + verplanteStunden + " Stunden bereits verplant");
										if ((verplanteStunden*60) + schichtenInfo[i][2] <= (maMinuten[ma][1]*60)){
											//Passende Schicht wird ausgewählt
											schicht = i;
											//protokolliere("\nGefundene Schicht = " + (schicht) + " mit der Dauer: " + schichtenInfo[i][2]);
											//protokolliere("\nMitarbeiter " + ma + " kommt damit auf " +(verplanteStunden + (schichtenInfo[i][2]/60)) + " von " + maMinuten[ma][1] + " Wochenstunden.");
//											//Prüfe ob rel. Tagesdeckung unter rel. Wochendeckung bleibt
											//Wenn es eine passende Schicht gibt
											if(schicht > -1){
												for(int j = (beginnDWunsch/30); j <= (((beginnDWunsch + schichtenInfo[schicht][2])/30)-1);j++){
													besetzung[tag][j] +=1;
												}
											
											//Ausgabe Bedarf/Besetzung Tag
											//protokolliere("\n\n*Tag "+ tag + " mit MA" + ma);
											//protokolliere("\nBedarf: ");
											for(int j = 0; j < bedarf[tag].length;j++){
												//protokolliere("" + bedarf[tag][j]);
											}
											//protokolliere("\nBesetzung: ");
											for(int j = 0; j < besetzung[tag].length;j++){
												//protokolliere("" + besetzung[tag][j]);
											}
											//Berechne Tages- und Wochendeckung neu. 
											sortiereWochentage();

													
														geplant = true;
														break;
														
													}
										
										

										//Mitarbeiter hat nicht mehr genug Stunden
										} else {
											protokolliere("\nMitarbeiter " + ma + " kann Schicht " + i + " nicht bekommen, da er sonst mit " + (verplanteStunden + (schichtenInfo[i][2]/60)) + " seine Wochenstunden von " + maMinuten[ma][1] + " überschreitet.");
										
										}
								//Schichtdefinition passt nicht (Korridor passt nicht)
								} else {
									protokolliere("\n D Korridor nicht in Schicht. Getestet wurde: " + schichtenInfo[i][0] + " <= " + beginnDWunsch + " und " + schichtenInfo[i][1] + " >= " + endeDWunsch );
								}
							//Ende for Schleife durch die Schichten --> keine passende Schicht gefunden
							}
							//Wenn Schicht gefunden und geplant
							if(schicht != -2 && geplant){
								//Der Mitarbeiter wird geplant
								maWunsch[ma][2][tag] = Integer.toString(schicht);
								maWunsch[ma][3][tag] = Integer.toString(beginnDWunsch);
								maWunsch[ma][4][tag] = Integer.toString((beginnDWunsch + schichtenInfo[schicht][2]));
								
								//Ausgabe verplante Schicht
								protokolliere("\nMitarbeiter " + ma + "wurde am Tag " +tag+ " mit der Schicht " + maWunsch[ma][2][tag] + " von "+maWunsch[ma][3][tag] + " bis " + maWunsch[ma][3][tag] + "verplant.");
								
								//Wenn weiterer MA vorhanden 
								if (id+1 < maIDd.length){
									//Weiter mit dem nächsten MA gem. Sortierung
									id += 1;
									generiere(6,tag, maIDd[id]);
								//Sonst (also wenn kein weiterer MA vorhanden)
								} else {
									//Nächster Tag gemäß neuer Sortierung
									naechsterTag(6);
								}
							//Wenn Schicht gefunden und nicht geplant	
							} else if (schicht != -2 && !geplant){
								//MA zurück sortieren
								sortiereWochentage();
								//Weiter gehen zu nächstem Tag
								naechsterTag(6);
								
							// Und wenn keine Schicht gefunden wurde
							} else if(schicht == -2){
								protokolliere("\nAlle Schichten wurden geprüft. Es war keine passende dabei.");
								//Wenn weiterer MA vorhanden 
								if (id+1 < maIDd.length){
									//Weiter mit dem nächsten MA gem. Sortierung
									id += 1;
									generiere(6,tag, maIDd[id]);
								//Sonst (also wenn kein weiterer MA vorhanden)
								} else {
									//Nächster Tag gemäß neuer Sortierung
									naechsterTag(6);
								}
							}
					//Keinen A Wunsch
					}else {
						//Protokolliere Prüfergebnis
						protokolliere("\nMitarbeiter " + ma + " wünscht sich " + maWunsch[ma][1][tag] + " an Tag " + tag + ". \nDas ist kein D Wunsch.");
						//Prüfe ob weiterer Ma vorhanden
						if (id+1 < maIDd.length){
							//Weiter mit dem nächsten MA gem. Sortierung
							id +=1;
							generiere(6,tag, maIDd[id]);
							//Sonst (also wenn kein weiterer MA vorhanden)
						} else {
							//Nächster Tag gemäß neuer Sortierung
							naechsterTag(6);
						}
					}
				//Mitarbeiter wurde schon verplant
				}else {
					//Ausgabe
					protokolliere("\n\nMitarbeiter "+ ma + " wurde bereits in vorherigen Schritten am Tag " +tag+ " mit der Schicht " + maWunsch[ma][2][tag] + " von "+maWunsch[ma][3][tag] + " bis " + maWunsch[ma][3][tag] + " verplant.");
					if (id+1 < maIDd.length){
						//Weiter mit dem nächsten MA gem. Sortierung 
						id+=1;
						generiere(6,tag, maIDd[id]);
						//Sonst (also wenn kein weiterer MA vorhanden)
					} else {
						//Nächster Tag gemäß neuer Sortierung
						naechsterTag(6);
					}
				}
				
				break;
				
			case 7: // Solange nicht alle MA geplant sind wiederhole Schritt 4,5 und 6
				//Wie überprüfe ich, ob alle MA ausreichend geplant sind ?!
				protokolliere("\n\n-----ENDE-----");
				
				//Sortierung der MA nach B-Punkten aufsteigend
				//sortiereMA(maID, maBPunkte, "asc");
				break;
				
			default: 
				break;
		
		}
	}
	
	private void sortiereMA(int[] maIDs, int[][] a,String dir){
		
		QuickSort quick;
		
		int[] sortiert = new int[maIDs.length];
		protokolliere("\n\nSortiert vor dem Sortieren:");
		for(int i = 0;i<maIDs.length;i++){
			sortiert[i] = a[maIDs[i]][1];
			protokolliere("\nAPunkte von Index " + i + " = " + sortiert[i]);
		}
		//ASC = aufsteigend
		if (dir.equals("asc")){
			 quick = new QuickSort(sortiert);
		}
		//DES = absteigend
		else if (dir.equals("des")){
			 quick = new QuickSort(sortiert);
			 arrayUmdrehen(sortiert);
		}
		//IDs an Reihenfolge anpassen
		protokolliere("\n\nNach dem Sortieren:");
		for(int i = 0;i<sortiert.length;i++){
			protokolliere("\nSortiert["+i+"] = "+ sortiert[i]);
			for (int j=0;j<a.length;j++){
				
				if(sortiert[i] == a[j][1] && i != j){
					protokolliere("\nmaIDs["+i+"] = "+ j);
					maIDs[i] = j;
				}
			}
			
			}
		
		// Ausgabe
		protokolliere("\n Sortierte MA");
		for (int i = 0; i< maIDs.length;i++){
			protokolliere("\n MA " + maIDs[i] + ": Wochensoll: " + maMinuten[maIDs[i]][1] + ", A-Punkte: " + a[maIDs[i]][1] + ", B-Punkte: " + maBPunkte[maIDs[i]][1] + "\n");
			protokolliere("Wünsche: \n");
			for (int j = 0; j <7;j++){
				protokolliere("Tag " + j + ": " + maWunsch[maIDs[i]][1][j]+"\n");
			}
			protokolliere("-----");
		}
	}
	
	
	//Tabelle Umdrehen
	private void arrayUmdrehen(int[] a){
		
		int[] temp = new int[a.length];
		for (int i = 0; i<a.length;i++){
			temp[i] = a[i];
		}
		for (int i =0;i<temp.length;i++){
			a[i] =temp[temp.length-1-i];
		}
	}
	
	//Sortierer für alle MA-Tabellen
	private static class QuickSort {
		
		public QuickSort(int[] x){
			sortiere(x);
		}
		 public static void sortiere(int[] x) {
		      qSort(x, 0, x.length-1);
		   }
		    
		   public static void qSort(int x[], int links, int rechts) {
		      if (links < rechts) {
		         int i = partition(x,links,rechts);
		         qSort(x,links,i-1);
		         qSort(x,i+1,rechts);
		      }
		   }
		    
		   public static int partition(int x[], int links, int rechts) {
		      int pivot, i, j, helpx;
		      pivot = x[rechts];               
		      i     = links;
		      j     = rechts-1;
		      while(i<=j) {
		         if (x[i] > pivot) {     
		            // tausche x[i] und x[j]
		            helpx = x[i]; 
		            x[i] = x[j]; 
		            x[j] = helpx; 
		            
		           
		            j--;
		         } else i++;            
		      }
		      // tausche x[i] und x[rechts]
		      helpx      = x[i];
		      x[i]      = x[rechts];
		      x[rechts] = helpx; 
		        
		      return i;
		   }
		   
		   
	}
	
	//rel. Deckung eines Tages berechnen und in Tabelle aktualisieren
	private void berechneRelativeTagesdeckung(int i){
		int deckung = 0 ;
		
		for (int j = 0; j<48;j++){
	
				int intervall = bedarf[i][j] - besetzung[i][j];
				deckung -= intervall;
				
		}
			//deckung = (deckung/48);
			
		tagesdeckung[i][1] = deckung;
		
	}
	
	//Sortierung der Wochentage nach der rel. Deckung
	private void sortiereWochentage(){
		wochendeckung = 0;
		//protokolliere("\n\n *Tagesdeckung unsortiert: ");
		
			for(int i = 0; i<7;i++){
				//Tagesdeckungen und Wochendeckung berechnen
				berechneRelativeTagesdeckung(i);
				wochendeckung += tagesdeckung[i][1];
				
				//Ausgabe Tagesdeckung
				//protokolliere("\n Tagesdeckung von " + i + ": " + tagesdeckung[i][1] + " mit [i][0] " + tagesdeckung[i][0]);
			}
			//Durchschnitt Wochendeckung
			wochendeckung = wochendeckung / 7;
			
			//Ausgabe Wochendeckung
			//protokolliere("\n\n *Wochendeckung: " + wochendeckung);
			
			//Tagesdeckungen sortieren
			sortiereTagesdeckungen();
		
			//Ausgabe sortierter Tagesdeckung
			protokolliere("\n\n *Tagesdeckung sortiert: ");
			for(int i = 0; i<7;i++){
				protokolliere("\n Tagesdeckung von Tag " + i + " ist auf Platz " + tagesdeckung[i][0] + ": " + tagesdeckung[i][1]);
			}
	}
	
	private void sortiereTagesdeckungen(){
		
		QuickSort quick;

		int[] sortiert = new int[7];
		
		for(int i = 0;i<7;i++){
			//Befüllung von Sortiert
			sortiert[i] = tagesdeckung[i][1];	
		}
		
		//Sortierung durchführen
		quick = new QuickSort(sortiert);
		arrayUmdrehen(sortiert);
		
		//Prüfsumme für doppelte Deckungen
		int summe =0;
		for(int i = 0;i<7;i++){
			//Berechne (Prüf-)Summe aus sortiert 
			summe += sortiert[i];
		}
		
		//IDs an Reihenfolge anpassen
		for(int i = 0;i<7;i++){
			for (int j=0;j<7;j++){
				if(sortiert[j] == tagesdeckung[i][1]){
						
						tagesdeckung[i][0] = j;
						sortiert[j] = summe;
						break;
					
				} 
			}
			
		}
		
	}
	
	private void naechsterTag(int schritt) {
		//Nächster Tag gemäß neuer Sortierung
		protokolliere("\n\n\n---------\n Nächster Tag. \nBei " + zaehler + " generierten Tagen. \nIm Schritt" + schritt);
		int min =0;
		for(int k = 0; k < 7;k++){
			
			if (tagesdeckung[k][0] > tagesdeckung[min][0] && tagesdeckung[k][2]!= schritt){
				//protokolliere("\nMin war " +  min);
				min = k;
				//protokolliere("\nMin ist jetzt " +  min +  "\nund Platz von Tag " + k + " ist " + tagesdeckung[k][0]);
			}
			
		}
		
		zaehler +=1;
		//Wenn noch ein Tag übrig und dies nicht der selbe Tag wie eben ist
		if(zaehler <= 7){
		//Mitarbeiter nach Wünschen sortieren
		sortiereNachWuenschen(min);
		id= 0;
		switch (schritt) {
		case 1:
			if(maIDa.length > 0){
			//Sortierung der MA mit A-Tagen nach A-Punkten absteigend
			sortiereMA(maIDa, maAPunkte, "des");
			//Starte Generieren
			protokolliere("\nStarte generieren mit Schritt " + schritt + ", Tag " + min + ", MA " + maIDa[id]);
			generiere(schritt,min,maIDa[id]);
			}else {
				tagesdeckung[min][2] = 1;
				naechsterTag(1);
			}
			break; 
		case 2:
			if(maIDb.length > 0){
			//Sortierung der MA mit B-Tagen nach A-Punkten absteigend
			sortiereMA(maIDb, maAPunkte, "des");
			//Starte Generieren
			protokolliere("\nStarte generieren mit Schritt " + schritt + ", Tag " + min + ", MA " + maIDb[id]);
			generiere(schritt,min,maIDb[id]);
			}else {
				tagesdeckung[min][2] = 2;
				naechsterTag(2);
			}
			break;
		case 3:
			if(maIDd.length > 0){
			//Sortierung der MA mit D-Tagen nach B-Punkten aufsteigend
			sortiereMA(maIDd, maBPunkte, "asc");
			//Starte Generieren
			protokolliere("\nStarte generieren mit Schritt " + schritt + ", Tag " + min + ", MA " + maIDd[id]);
			generiere(schritt,min,maIDd[id]);
			}else {
				tagesdeckung[min][2] = 3;
				naechsterTag(3);
			}
			break;
		case 4:
			if(maIDa.length > 0){
			//Sortierung der MA mit A-Tagen nach A-Punkten absteigend
			sortiereMA(maIDa, maAPunkte, "des");
			//Starte Generieren
			protokolliere("\nStarte generieren mit Schritt " + schritt + ", Tag " + min + ", MA " + maIDa[id]);
			generiere(schritt,min,maIDa[id]);
			}else {
				tagesdeckung[min][2] = 4;
				naechsterTag(4);
			}
			break;
		case 5:
			if(maIDb.length > 0){
			//Sortierung der MA mit A-Tagen nach A-Punkten absteigend
			sortiereMA(maIDb, maAPunkte, "des");
			//Starte Generieren
			protokolliere("\nStarte generieren mit Schritt " + schritt + ", Tag " + min + ", MA " + maIDb[id]);
			generiere(schritt,min,maIDb[id]);
			}else {
				tagesdeckung[min][2] = 5;
				naechsterTag(5);
			}
			break;
		case 6:
			if(maIDd.length > 0){
			//Sortierung der MA mit D-Tagen nach B-Punkten aufsteigend
			sortiereMA(maIDd, maBPunkte, "asc");
			//Starte Generieren
			protokolliere("\nStarte generieren mit Schritt " + schritt + ", Tag " + min + ", MA " + maIDd[id]);
			generiere(schritt,min,maIDd[id]);
			}else {
				tagesdeckung[min][2] = 6;
				naechsterTag(6);
			}
			break;
		case 7:
			
			//Starte Generieren
			protokolliere("\nStarte generieren mit Schritt " + schritt + ", Tag " + min + ", MA " + maIDa[id]);
			generiere(schritt,min,maIDa[id]);
			break;
		}
		
		//Wenn keine Tag mehr übrig starte mit nächstem Schritt
		} else {
			zaehler = 1;
			//Ausgabe Ergebis von PLanungsschritt
			protokolliere("\n\nPLANUNGSSCHRITT " +schritt + "ABGESCHLOSSEN.");
			protokolliere("\nERGEBNIS:");
			for(int i = 0;i<7;i++){
				protokolliere("\nTag "+ i + ":");
				
				protokolliere("\nBedarf: \n");
				for(int j = 0; j < bedarf[i].length;j++){
					protokolliere("" + bedarf[i][j]);
				}
				protokolliere("\nBesetzung: \n");
				for(int j = 0; j < besetzung[i].length;j++){
					protokolliere("" + besetzung[i][j]);
				}
				protokolliere("\nMitarbeiter: \n");
				String ausgabe = "";
				for(int j = 0; j < maID.length;j++){
					ausgabe = ausgabe + "MA " + j + " mit Schicht " + maWunsch[j][2][i] + " von " + maWunsch[j][3][i]+ " bis " + maWunsch[j][4][i] +".\n";
				}
				protokolliere(ausgabe);
			}
			protokolliere("\nWOCHENSTUNDEN DER MA:");
			for(int j = 0; j < maID.length;j++){
				protokolliere("\nMitarbeiter " + j + " wurde " + berechneWochenstunden(j) + " Stunden verplant." );
			}
			id= 0;
			
			for(int k = 0; k < 7;k++){
				//Berechne TagesMinimum für nächsten Schritt
				if (tagesdeckung[k][0] > tagesdeckung[min][0] && tagesdeckung[k][2]!= (schritt+1)){
					//protokolliere("\nMin war " +  min);
					min = k;
					//protokolliere("\nMin ist jetzt " +  min +  "\nund Platz von Tag " + k + " ist " + tagesdeckung[k][0]);
				}
				
			}
			protokolliere("\n\n\nStarte generieren mit nächstem Schritt " + (schritt+1) + ", Tag " + min + ", MA " + maID[id]);
			//Starte Generieren nächsten Schritt
			generiere((schritt+1),min,maID[id]);
		}
	}

	private int berechneWochenstunden(int ma){
		
		int stunden =0;
		
		for(int i =0; i<7;i++){
			
			if(maWunsch[ma][2][i].equals("keine SchichtDefinition") == false){
				stunden += schichtenInfo[Integer.parseInt(maWunsch[ma][2][i])][2];
				//protokolliere("\nStunden erhöht sich durch Schicht " +schichtenInfo[Integer.parseInt(maWunsch[ma][2][i])][2] + " auf " +  stunden + " Minuten");
			}
		}
		//protokolliere("\nMitarbeiter " + ma + " ist bereits " + stunden + " Minuten verplant.");
		stunden = stunden / 60;
		//protokolliere("\nDas sind " + stunden + " Stunden.");
		return stunden;
	}

private void sortiereNachWuenschen(int tag){
	int a = 0;
	int b = 0;
	int d = 0;
	
	// Anzahl der Mitarbeiter mit A-, B- und D- Tagen ermitteln.
	for(int i =0; i<maID.length;i++){
		//Mitarbeiter mit A Tagen (auf jeden Fall A)
		if(maWunsch[i][1][tag].contains("A")){
			a+=1;
		//Mitarbeiter mit B Tagen	(B aber nicht A)
		} else if (maWunsch[i][1][tag].contains("B") && !maWunsch[i][1][tag].contains("A")) {
			b+=1;
		//Mitarbeiter mit D Tagen	(nicht B und nicht A)
		}else if(!maWunsch[i][1][tag].contains("B") && !maWunsch[i][1][tag].contains("A")) {
			d+=1;
		}
	}
	//Erstellen von Arrays der entsprechenden Länge
	maIDa = new int[a];
	maIDb = new int[b];
	maIDd = new int[d];
	
	//Arrays befüllen
	a = 0;
	b = 0;
	d = 0;
	for(int i =0; i<maID.length;i++){
		//Mitarbeiter mit A Tagen (auf jeden Fall A)
		if(maWunsch[i][1][tag].contains("A")){
			maIDa[a] = maID[i];
			a+=1;
		//Mitarbeiter mit B Tagen	(B aber nicht A)
		} else if (maWunsch[i][1][tag].contains("B") && !maWunsch[i][1][tag].contains("A")) {
			maIDb[b] = maID[i];
			b+=1;
		//Mitarbeiter mit D Tagen	(nicht B und nicht A)
		}else if(!maWunsch[i][1][tag].contains("B") && !maWunsch[i][1][tag].contains("A")) {
			maIDd[d] = maID[i];
			d+=1;
		}
	}
}
private void optimiere(int schritt, int tag){
	
	protokolliere("\n\n\n----START OPTIMIERER----");
	protokolliere("\n\nSchritt " + schritt);
	sortiereNachWuenschen(tag);
	switch (schritt) {
	//Die MA mit D-Tagen (Tage an denen der MA weder eine A-Zeit noch eine B-Zeit angegeben hat) ...
	case 1:
		//...werden anhand ihres B-Punktekontos aufsteigend optimiert
		sortiereMA(maIDd,maBPunkte,"asc");
		int id=0;
		//Prüfe ob der Mitarbeiter für diesen Tag schon verplant ist 
		while(id < maIDd.length){
		if(!maWunsch[maIDd[id]][2][tag].equals("keine SchichtDefinition")){
			//Der Schichtbeginn wird zunächst auf die frühestmögliche Anfangszeit gesetzt
			
			// Ausgabe zur Pürfung.
			protokolliere("\nMitarbeiter " + maIDd[id] + " wurde geplant mit " + maWunsch[maIDd[id]][2][tag]);
			
			protokolliere("\nWunsch: " + maWunsch[maIDd[id]][1][tag]);
			protokolliere("\nSchichtID: " + maWunsch[maIDd[id]][2][tag]);
			protokolliere("\nSchichtbeginn: " + maWunsch[maIDd[id]][3][tag]);
			protokolliere("\nSchichtende: " + maWunsch[maIDd[id]][4][tag]);
			
			protokolliere("\nKorridor-Beginn: " + schichtenInfo[Integer.parseInt(maWunsch[maIDd[id]][2][tag])][0]);
			protokolliere("\nKorridor-Ende: " + schichtenInfo[Integer.parseInt(maWunsch[maIDd[id]][2][tag])][1]);
			protokolliere("\nLaenge: " + schichtenInfo[Integer.parseInt(maWunsch[maIDd[id]][2][tag])][2]);

			int beginnDWunsch = maWunsch[maIDd[id]][1][tag].indexOf("D")*30;
			int endeDWunsch = (maWunsch[maIDd[id]][1][tag].lastIndexOf("D")+1)*30;
			int laengeDWunsch = endeDWunsch - beginnDWunsch;
			
			int beginnSchicht = Integer.parseInt(maWunsch[maIDd[id]][3][tag]);
			
			int korridorBeginn = schichtenInfo[Integer.parseInt(maWunsch[maIDd[id]][2][tag])][0];
			int korridorEnde = schichtenInfo[Integer.parseInt(maWunsch[maIDd[id]][2][tag])][1];
			int schichtLaenge = schichtenInfo[Integer.parseInt(maWunsch[maIDd[id]][2][tag])][2];
			
			//Gemerkte Schicht = Erster Schichtbeginn
			int merke = beginnSchicht/30;
			
			//Durchlauf
			int startDurchlauf = 0;
			int abbruch = 0;
			
			// RMS berechnen
			double rmsGemerkt = berechneRMS(tag);
			protokolliere("\nRMS alt: " + rmsGemerkt);
			
			//Mitarbeiter mit der Bestehenden Planung ausplanen
			for(int k = (beginnDWunsch/30); k <= (((beginnDWunsch + schichtLaenge)/30)-1);k++){
				
				besetzung[tag][k] -=1;
			}
			
			
			//Setze die Schicht auf erst möglichen Beginn des Schichtkorridors
			
						
			//Wenn Schichtkorridor Beginn > WunschBeginn dann gilt Schichtkorridor Beginn als kleinster
			//Wenn Schichtkorridor Beginn <= WunschBeginn dann gilt WunschBeginn als kleinster 
			if(korridorBeginn <= beginnDWunsch){
				
				startDurchlauf = beginnDWunsch;
				startDurchlauf = startDurchlauf/30;
				
			} else
			if(korridorBeginn > beginnDWunsch) {
				
				startDurchlauf = korridorBeginn;
				startDurchlauf = startDurchlauf/30;
					
			}
			
			// Letzeten möglichen Schichtbeginn festlegen
			if(korridorEnde <= endeDWunsch) {
				
				abbruch = korridorEnde - schichtLaenge;
				abbruch = abbruch/30;
				
			} else
			if(korridorEnde > endeDWunsch) {
				abbruch = endeDWunsch - schichtLaenge;
				abbruch = abbruch/30;
			}
			
			protokolliere("\nGetestet werden soll von " + startDurchlauf + " bis " + abbruch);
			
		
		
			
			//Gehe mögliche Schichtbeginne durch:
			
			while(startDurchlauf  <= abbruch){
				for(int k = (startDurchlauf); k <= (((startDurchlauf*30 + schichtLaenge)/30)-1);k++){
					
					besetzung[tag][k] +=1;
				}
				// neuen RMS berechnen
				double rmsNeu = berechneRMS(tag);
				protokolliere("\nRMS neu: " + rmsNeu);
				
				if(rmsGemerkt > rmsNeu){
					merke = startDurchlauf;
					rmsGemerkt = rmsNeu;
				}
				
				startDurchlauf++;
			}
			protokolliere("\nBeste Schicht: Beginn: " + merke + ", mit RMS: "+ rmsGemerkt);
			
			//Plane Mitarbeiter auf besten Schichtbeginn
			for(int k = (merke); k <= (((merke*30 + schichtLaenge)/30)-1);k++){
				
				besetzung[tag][k] +=1;
			}
			
			maWunsch[maIDd[id]][3][tag] = Integer.toString(merke * 30);
			maWunsch[maIDd[id]][4][tag] = Integer.toString(merke*30 + schichtLaenge);
			protokolliere("\n\nNach Optimeirung von MA " + maIDd[id] + ":");
			protokolliere("\nSchichtbeginn: " + maWunsch[maIDd[id]][3][tag]);
			protokolliere("\nSchichtende: " + maWunsch[maIDd[id]][4][tag]);
			
			
			
		}
		else{
			protokolliere("\nMitarbeiter " + maIDd[id]+ " wurde an diesem Tag nicht geplant.");
		}
		id +=1;
	}
		
		protokolliere("\n\nKeine weiteren Mitarbeiter für Schritt " + schritt);
		optimiere(2,tag);
		
		break;
		//Die MA mit B-Tagen (Tage an denen der MA eine B-Zeit aber keine A-Zeit angegeben hat)...
	case 2:
		//...werden anhand ihres A-Punktekontos absteigend optimiert
				sortiereMA(maIDb,maAPunkte,"des");
				id=0;
				//Prüfe ob der Mitarbeiter für diesen Tag schon verplant ist 
				while(id < maIDb.length){
				if(!maWunsch[maIDb[id]][2][tag].equals("keine SchichtDefinition")){
					//Der Schichtbeginn wird zunächst auf die frühestmögliche Anfangszeit gesetzt
					
					// Ausgabe zur Pürfung.
					protokolliere("\nMitarbeiter " + maIDb[id] + " wurde geplant mit " + maWunsch[maIDb[id]][2][tag]);
					
					protokolliere("\nWunsch: " + maWunsch[maIDb[id]][1][tag]);
					protokolliere("\nSchichtID: " + maWunsch[maIDb[id]][2][tag]);
					protokolliere("\nSchichtbeginn: " + maWunsch[maIDb[id]][3][tag]);
					protokolliere("\nSchichtende: " + maWunsch[maIDb[id]][4][tag]);
					
					protokolliere("\nKorridor-Beginn: " + schichtenInfo[Integer.parseInt(maWunsch[maIDb[id]][2][tag])][0]);
					protokolliere("\nKorridor-Ende: " + schichtenInfo[Integer.parseInt(maWunsch[maIDb[id]][2][tag])][1]);
					protokolliere("\nLaenge: " + schichtenInfo[Integer.parseInt(maWunsch[maIDb[id]][2][tag])][2]);

					int beginnBWunsch = maWunsch[maIDb[id]][1][tag].indexOf("B")*30;
					int endeBWunsch = (maWunsch[maIDb[id]][1][tag].lastIndexOf("B")+1)*30;
					int laengeBWunsch = endeBWunsch - beginnBWunsch;
					
					int beginnSchicht = Integer.parseInt(maWunsch[maIDb[id]][3][tag]);
					
					int korridorBeginn = schichtenInfo[Integer.parseInt(maWunsch[maIDb[id]][2][tag])][0];
					int korridorEnde = schichtenInfo[Integer.parseInt(maWunsch[maIDb[id]][2][tag])][1];
					int schichtLaenge = schichtenInfo[Integer.parseInt(maWunsch[maIDb[id]][2][tag])][2];
					
					//Gemerkte Schicht = Erster Schichtbeginn
					int merke = beginnSchicht/30;
					
					//Durchlauf
					int startDurchlauf = 0;
					int abbruch = 0;
					
					// RMS berechnen
					double rmsGemerkt = berechneRMS(tag);
					protokolliere("\nRMS alt: " + rmsGemerkt);
					
					//Mitarbeiter mit der Bestehenden Planung ausplanen
					for(int k = (beginnBWunsch/30); k <= (((beginnBWunsch + schichtLaenge)/30)-1);k++){
						
						besetzung[tag][k] -=1;
					}
					
					
					//Setze die Schicht auf erst möglichen Beginn des Schichtkorridors
					
								
					//Wenn Schichtkorridor Beginn > WunschBeginn dann gilt Schichtkorridor Beginn als kleinster
					//Wenn Schichtkorridor Beginn <= WunschBeginn dann gilt WunschBeginn als kleinster 
					if(korridorBeginn <= beginnBWunsch){
						
						startDurchlauf = beginnBWunsch;
						startDurchlauf = startDurchlauf/30;
						
					} else
					if(korridorBeginn > beginnBWunsch) {
						
						startDurchlauf = korridorBeginn;
						startDurchlauf = startDurchlauf/30;
							
					}
					
					// Letzeten möglichen Schichtbeginn festlegen
					if(korridorEnde <= endeBWunsch) {
						
						abbruch = korridorEnde - schichtLaenge;
						abbruch = abbruch/30;
						
					} else
					if(korridorEnde > endeBWunsch) {
						abbruch = endeBWunsch - schichtLaenge;
						abbruch = abbruch/30;
					}
					
					protokolliere("\nGetestet werden soll von " + startDurchlauf + " bis " + abbruch);
					
				
				
					
					//Gehe mögliche Schichtbeginne durch:
					
					while(startDurchlauf  <= abbruch){
						for(int k = (startDurchlauf); k <= (((startDurchlauf*30 + schichtLaenge)/30)-1);k++){
							
							besetzung[tag][k] +=1;
						}
						// neuen RMS berechnen
						double rmsNeu = berechneRMS(tag);
						protokolliere("\nRMS neu: " + rmsNeu);
						
						if(rmsGemerkt > rmsNeu){
							merke = startDurchlauf;
							rmsGemerkt = rmsNeu;
						}
						
						startDurchlauf++;
					}
					protokolliere("\nBeste Schicht: Beginn: " + merke + ", mit RMS: "+ rmsGemerkt);
					
					//Plane Mitarbeiter auf besten Schichtbeginn
					for(int k = (merke); k <= (((merke*30 + schichtLaenge)/30)-1);k++){
						
						besetzung[tag][k] +=1;
					}
					
					maWunsch[maIDb[id]][3][tag] = Integer.toString(merke * 30);
					maWunsch[maIDb[id]][4][tag] = Integer.toString(merke*30 + schichtLaenge);
					protokolliere("\n\nNach Optimeirung von MA " + maIDb[id] + ":");
					protokolliere("\nSchichtbeginn: " + maWunsch[maIDb[id]][3][tag]);
					protokolliere("\nSchichtende: " + maWunsch[maIDb[id]][4][tag]);
					
					
					
				}
				else{
					protokolliere("\nMitarbeiter " + maIDb[id]+ " wurde an diesem Tag nicht geplant.");
				}
				id +=1;
			}
				
				protokolliere("\n\nKeine weiteren Mitarbeiter für Schritt " + schritt);
				optimiere(3,tag);
		break;
		//Die MA mit A-Tagen (Tage an denen der MA eine A-Zeit angegeben hat)
	case 3:
		//...werden anhand ihres A-Punktekontos aufsteigend innerhalb ihrer B-Zeiten optimiert
		sortiereMA(maIDa,maAPunkte,"asc");
		id=0;
		//Prüfe ob der Mitarbeiter für diesen Tag schon verplant ist 
		while(id < maIDa.length){
		if(!maWunsch[maIDa[id]][2][tag].equals("keine SchichtDefinition")){
			//Der Schichtbeginn wird zunächst auf die frühestmögliche Anfangszeit gesetzt
			
			// Ausgabe zur Pürfung.
			protokolliere("\nMitarbeiter " + maIDa[id] + " wurde geplant mit " + maWunsch[maIDa[id]][2][tag]);
			
			protokolliere("\nWunsch: " + maWunsch[maIDa[id]][1][tag]);
			protokolliere("\nSchichtID: " + maWunsch[maIDa[id]][2][tag]);
			protokolliere("\nSchichtbeginn: " + maWunsch[maIDa[id]][3][tag]);
			protokolliere("\nSchichtende: " + maWunsch[maIDa[id]][4][tag]);
			
			protokolliere("\nKorridor-Beginn: " + schichtenInfo[Integer.parseInt(maWunsch[maIDa[id]][2][tag])][0]);
			protokolliere("\nKorridor-Ende: " + schichtenInfo[Integer.parseInt(maWunsch[maIDa[id]][2][tag])][1]);
			protokolliere("\nLaenge: " + schichtenInfo[Integer.parseInt(maWunsch[maIDa[id]][2][tag])][2]);
			int beginnWunsch = 0;
			int endeWunsch = 0;
			
			if(maWunsch[maIDa[id]][1][tag].contains("B")){
			if(maWunsch[maIDa[id]][1][tag].indexOf("B")*30 < maWunsch[maIDa[id]][1][tag].indexOf("A")*30){
				beginnWunsch = maWunsch[maIDa[id]][1][tag].indexOf("B")*30;
				if(maWunsch[maIDa[id]][1][tag].lastIndexOf("B")*30 > maWunsch[maIDa[id]][1][tag].lastIndexOf("A")*30){
					endeWunsch = (maWunsch[maIDa[id]][1][tag].lastIndexOf("B")+1)*30;
				} else if(maWunsch[maIDa[id]][1][tag].lastIndexOf("B")*30 < maWunsch[maIDa[id]][1][tag].lastIndexOf("A")*30){
					endeWunsch = (maWunsch[maIDa[id]][1][tag].lastIndexOf("A")+1)*30;
				}
			} else if(maWunsch[maIDa[id]][1][tag].indexOf("B")*30 > maWunsch[maIDa[id]][1][tag].indexOf("A")*30){
				beginnWunsch = maWunsch[maIDa[id]][1][tag].indexOf("A")*30;
				if(maWunsch[maIDa[id]][1][tag].lastIndexOf("B")*30 > maWunsch[maIDa[id]][1][tag].lastIndexOf("A")*30){
					endeWunsch = (maWunsch[maIDa[id]][1][tag].lastIndexOf("B")+1)*30;
				} else if(maWunsch[maIDa[id]][1][tag].lastIndexOf("B")*30 < maWunsch[maIDa[id]][1][tag].lastIndexOf("A")*30){
					endeWunsch = (maWunsch[maIDa[id]][1][tag].lastIndexOf("A")+1)*30;
				}
			}
			
			}else {
				beginnWunsch = maWunsch[maIDa[id]][1][tag].indexOf("A")*30;
				endeWunsch = (maWunsch[maIDa[id]][1][tag].lastIndexOf("A")+1)*30;
			}
			
			
			int laengeWunsch = endeWunsch - beginnWunsch;
			
			int beginnSchicht = Integer.parseInt(maWunsch[maIDa[id]][3][tag]);
			
			int korridorBeginn = schichtenInfo[Integer.parseInt(maWunsch[maIDa[id]][2][tag])][0];
			int korridorEnde = schichtenInfo[Integer.parseInt(maWunsch[maIDa[id]][2][tag])][1];
			int schichtLaenge = schichtenInfo[Integer.parseInt(maWunsch[maIDa[id]][2][tag])][2];
			
			//Gemerkte Schicht = Erster Schichtbeginn
			int merke = beginnSchicht/30;
			
			//Durchlauf
			int startDurchlauf = 0;
			int abbruch = 0;
			
			// RMS berechnen
			double rmsGemerkt = berechneRMS(tag);
			protokolliere("\nRMS alt: " + rmsGemerkt);
			
			//Mitarbeiter mit der Bestehenden Planung ausplanen
			for(int k = (beginnWunsch/30); k <= (((beginnWunsch + schichtLaenge)/30)-1);k++){
				
				besetzung[tag][k] -=1;
			}
			
			
			//Setze die Schicht auf erst möglichen Beginn des Schichtkorridors
			
						
			//Wenn Schichtkorridor Beginn > WunschBeginn dann gilt Schichtkorridor Beginn als kleinster
			//Wenn Schichtkorridor Beginn <= WunschBeginn dann gilt WunschBeginn als kleinster 
			if(korridorBeginn <= beginnWunsch){
				
				startDurchlauf = beginnWunsch;
				startDurchlauf = startDurchlauf/30;
				
			} else
			if(korridorBeginn > beginnWunsch) {
				
				startDurchlauf = korridorBeginn;
				startDurchlauf = startDurchlauf/30;
					
			}
			
			// Letzeten möglichen Schichtbeginn festlegen
			if(korridorEnde <= endeWunsch) {
				
				abbruch = korridorEnde - schichtLaenge;
				abbruch = abbruch/30;
				
			} else
			if(korridorEnde > endeWunsch) {
				abbruch = endeWunsch - schichtLaenge;
				abbruch = abbruch/30;
			}
			
			protokolliere("\nGetestet werden soll von " + startDurchlauf + " bis " + abbruch);
			
		
		
			
			//Gehe mögliche Schichtbeginne durch:
			
			while(startDurchlauf  <= abbruch){
				for(int k = (startDurchlauf); k <= (((startDurchlauf*30 + schichtLaenge)/30)-1);k++){
					
					besetzung[tag][k] +=1;
				}
				// neuen RMS berechnen
				double rmsNeu = berechneRMS(tag);
				protokolliere("\nRMS neu: " + rmsNeu);
				
				if(rmsGemerkt > rmsNeu){
					merke = startDurchlauf;
					rmsGemerkt = rmsNeu;
				}
				
				startDurchlauf++;
			}
			protokolliere("\nBeste Schicht: Beginn: " + merke + ", mit RMS: "+ rmsGemerkt);
			
			//Plane Mitarbeiter auf besten Schichtbeginn
			for(int k = (merke); k <= (((merke + schichtLaenge)/30)-1);k++){
				
				besetzung[tag][k] +=1;
			}
			
			maWunsch[maIDa[id]][3][tag] = Integer.toString(merke * 30);
			maWunsch[maIDa[id]][4][tag] = Integer.toString(merke*30 + schichtLaenge);
			protokolliere("\n\nNach Optimeirung von MA " + maIDa[id] + ":");
			protokolliere("\nSchichtbeginn: " + maWunsch[maIDa[id]][3][tag]);
			protokolliere("\nSchichtende: " + maWunsch[maIDa[id]][4][tag]);
			
			
			
		}
		else{
			protokolliere("\nMitarbeiter " + maIDa[id]+ " wurde an diesem Tag nicht geplant.");
		}
		id +=1;
	}
		
		protokolliere("\n\nKeine weiteren Mitarbeiter für Schritt " + schritt);
		optimiere(4,tag);
		break;
		//Die MA mit A- oder B-Tagen
	case 4:
		//...werden anhand ihres B-Punktekontos aufsteigend optimiert
				int[] maIDab = new int[(maIDa.length + maIDb.length)];
				if(maIDa.length != 0){
					for (int i =0; i< maIDa.length;i++){
						maIDab[i] = maIDa[i];
					}
				}
				if(maIDb.length != 0){
					for (int i = 0; i< maIDb.length;i++) {
						maIDab[maIDa.length+i] = maIDb[i];
					}
				}
				sortiereMA(maIDab,maBPunkte,"asc");
				id=0;
				//Prüfe ob der Mitarbeiter für diesen Tag schon verplant ist 
				while(id < maIDab.length){
				if(!maWunsch[maIDab[id]][2][tag].equals("keine SchichtDefinition")){
					//Der Schichtbeginn wird zunächst auf die frühestmögliche Anfangszeit gesetzt
					
					// Ausgabe zur Pürfung.
					protokolliere("\nMitarbeiter " + maIDab[id] + " wurde geplant mit " + maWunsch[maIDab[id]][2][tag]);
					
					protokolliere("\nWunsch: " + maWunsch[maIDab[id]][1][tag]);
					protokolliere("\nSchichtID: " + maWunsch[maIDab[id]][2][tag]);
					protokolliere("\nSchichtbeginn: " + maWunsch[maIDab[id]][3][tag]);
					protokolliere("\nSchichtende: " + maWunsch[maIDab[id]][4][tag]);
					
					protokolliere("\nKorridor-Beginn: " + schichtenInfo[Integer.parseInt(maWunsch[maIDab[id]][2][tag])][0]);
					protokolliere("\nKorridor-Ende: " + schichtenInfo[Integer.parseInt(maWunsch[maIDab[id]][2][tag])][1]);
					protokolliere("\nLaenge: " + schichtenInfo[Integer.parseInt(maWunsch[maIDab[id]][2][tag])][2]);

					int beginnDWunsch = maWunsch[maIDab[id]][1][tag].indexOf("D")*30;
					int endeDWunsch = (maWunsch[maIDab[id]][1][tag].lastIndexOf("D")+1)*30;
					int laengeDWunsch = endeDWunsch - beginnDWunsch;
					
					int beginnSchicht = Integer.parseInt(maWunsch[maIDab[id]][3][tag]);
					
					int korridorBeginn = schichtenInfo[Integer.parseInt(maWunsch[maIDab[id]][2][tag])][0];
					int korridorEnde = schichtenInfo[Integer.parseInt(maWunsch[maIDab[id]][2][tag])][1];
					int schichtLaenge = schichtenInfo[Integer.parseInt(maWunsch[maIDab[id]][2][tag])][2];
					
					//Gemerkte Schicht = Erster Schichtbeginn
					int merke = beginnSchicht/30;
					
					//Durchlauf
					int startDurchlauf = 0;
					int abbruch = 0;
					
					// RMS berechnen
					double rmsGemerkt = berechneRMS(tag);
					protokolliere("\nRMS alt: " + rmsGemerkt);
					
					//Mitarbeiter mit der Bestehenden Planung ausplanen
					for(int k = (beginnDWunsch/30); k <= (((beginnDWunsch + schichtLaenge)/30)-1);k++){
						
						besetzung[tag][k] -=1;
					}
					
					
					//Setze die Schicht auf erst möglichen Beginn des Schichtkorridors
					
								
					//Wenn Schichtkorridor Beginn > WunschBeginn dann gilt Schichtkorridor Beginn als kleinster
					//Wenn Schichtkorridor Beginn <= WunschBeginn dann gilt WunschBeginn als kleinster 
					if(korridorBeginn <= beginnDWunsch){
						
						startDurchlauf = beginnDWunsch;
						startDurchlauf = startDurchlauf/30;
						
					} else
					if(korridorBeginn > beginnDWunsch) {
						
						startDurchlauf = korridorBeginn;
						startDurchlauf = startDurchlauf/30;
							
					}
					
					// Letzeten möglichen Schichtbeginn festlegen
					if(korridorEnde <= endeDWunsch) {
						
						abbruch = korridorEnde - schichtLaenge;
						abbruch = abbruch/30;
						
					} else
					if(korridorEnde > endeDWunsch) {
						abbruch = endeDWunsch - schichtLaenge;
						abbruch = abbruch/30;
					}
					
					protokolliere("\nGetestet werden soll von " + startDurchlauf + " bis " + abbruch);
					
				
				
					
					//Gehe mögliche Schichtbeginne durch:
					
					while(startDurchlauf  <= abbruch){
						for(int k = (startDurchlauf); k <= (((startDurchlauf*30 + schichtLaenge)/30)-1);k++){
							
							besetzung[tag][k] +=1;
						}
						// neuen RMS berechnen
						double rmsNeu = berechneRMS(tag);
						protokolliere("\nRMS neu: " + rmsNeu);
						
						if(rmsGemerkt > rmsNeu){
							merke = startDurchlauf;
							rmsGemerkt = rmsNeu;
						}
						
						startDurchlauf++;
					}
					protokolliere("\nBeste Schicht: Beginn: " + merke + ", mit RMS: "+ rmsGemerkt);
					
					//Plane Mitarbeiter auf besten Schichtbeginn
					for(int k = (merke); k <= (((merke*30 + schichtLaenge)/30)-1);k++){
						
						besetzung[tag][k] +=1;
					}
					
					maWunsch[maIDab[id]][3][tag] = Integer.toString(merke * 30);
					maWunsch[maIDab[id]][4][tag] = Integer.toString(merke*30 + schichtLaenge);
					protokolliere("\n\nNach Optimeirung von MA " + maIDab[id] + ":");
					protokolliere("\nSchichtbeginn: " + maWunsch[maIDab[id]][3][tag]);
					protokolliere("\nSchichtende: " + maWunsch[maIDab[id]][4][tag]);
					
					
					
				}
				else{
					protokolliere("\nMitarbeiter " + maIDab[id]+ " wurde an diesem Tag nicht geplant.");
				}
				id +=1;
			}
				
				protokolliere("\n\nKeine weiteren Mitarbeiter für Schritt " + schritt);
				
				for(int i = 0;i<7;i++){
					protokolliere("\nTag "+ i + ":");
					
					protokolliere("\nBedarf: \n");
					for(int j = 0; j < bedarf[i].length;j++){
						protokolliere("" + bedarf[i][j]);
					}
					protokolliere("\nBesetzung: \n");
					for(int j = 0; j < besetzung[i].length;j++){
						protokolliere("" + besetzung[i][j]);
					}
					protokolliere("\nMitarbeiter: \n");
					String ausgabe = "";
					for(int j = 0; j < maID.length;j++){
						ausgabe = ausgabe + "MA " + j + " mit Schicht " + maWunsch[j][2][i] + " von " + maWunsch[j][3][i]+ " bis " + maWunsch[j][4][i] +".\n";
					}
					protokolliere(ausgabe);
				}
				
		break;
	default:
		break;
	}
}

private double berechneRMS(int tag){
	
	double rms = 0;
	double summeBedarf =0;
	double summeBesetzung =0;
	double summeRelDeckung =0;
	double[] relDeckung = new double[bedarf[tag].length];
	
	for(int i = 0;i<bedarf[tag].length;i++){
		summeBedarf += bedarf[tag][i];
		summeBesetzung += besetzung[tag][i];
		
		if(bedarf[tag][i] != 0){
			double besetzungInt = (double)besetzung[tag][i];
			double bedarfInt = (double) bedarf[tag][i];
			double differenz =(besetzungInt/bedarfInt);
			relDeckung[i] = (differenz-1);
			//protokolliere("\nRel. Deckung von " + i + " = " + relDeckung[i] + " = (" + besetzung[tag][i] + " / " + bedarf[tag][i] + ")-1 ist " + differenz);
		}else {
			relDeckung[i] = 0;
		}
	}
	//protokolliere("\nSumme Bedarf: " + summeBedarf);
	//protokolliere("\nSumme Besetzung: " + summeBesetzung);
	
	
	if(summeBedarf != 0){
		summeRelDeckung = ((summeBesetzung/summeBedarf)-1);	
	}
	
	//protokolliere("\nSumme rel. Deckung: " + summeRelDeckung);
	double[] abweichungen = new double[bedarf[tag].length];
	double summeAbweichungenPotenzen=0;
	
	for(int i = 0; i<abweichungen.length;i++){
		abweichungen[i] = (relDeckung[i]-summeRelDeckung);
		//protokolliere("\nAbweichung: "+ i + " = " + abweichungen[i] +" = " + relDeckung[i] + " - " + summeRelDeckung);
		summeAbweichungenPotenzen += Math.pow(abweichungen[i],2);
	}
	//protokolliere("\nSumme Abweichungen potenziert: " + summeAbweichungenPotenzen);
	rms = (Math.sqrt(summeAbweichungenPotenzen/48))*100;
	
	return rms;
	
}

}