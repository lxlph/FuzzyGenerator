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

public class Algorithmus {
	
	private String p ="";
	protected int[][] schichtenInfo;
	protected int[] maID;
	protected int[][] maMinuten;
	protected String[][][] maWunsch;
	protected int[][] maSchwelle;
	protected int[][] maAPunkte;
	protected int[][] maBPunkte;
	
	protected int[][] besetzung;
	
	
	public void setSchichten(int[][] schichtenInfo){
		
		//Tabellen füllen
		this.schichtenInfo = new int[schichtenInfo.length][3];
		this.schichtenInfo = schichtenInfo;
		
		protokolliere("\n-----\n *****Verfügbare Schichten: \n");
		
		for (int i = 0; i< schichtenInfo.length;i++){
			protokolliere("Schicht " + (i+1) + ": Zwischen: " + (schichtenInfo[i][0] / 60) + ":" + (schichtenInfo[i][0] % 60) + "Uhr und " + (schichtenInfo[i][1] / 60) + ":" + (schichtenInfo[i][1] % 60) + "Uhr mit " + (schichtenInfo[i][2] / 60) + ":" + (schichtenInfo[i][2] % 60) + " Länge. \n");
		}
	}
	
	public void setMitarbeiter(int[] maMinuten, String[][] maWunsch, int[] maSchwelle, int[] maAPunkte, int[] maBPunkte){
		
		//Tabellen füllen
		maID = new int[maMinuten.length];
		for(int i = 0; i < maID.length;i++){
		maID[i] = i;
		}
		this.maMinuten = new int[maMinuten.length][2];
		for (int i = 0;i<maMinuten.length;i++){
		this.maMinuten[i][1] = maMinuten[i];
		this.maMinuten[i][0] = i;
		}
		
		this.maWunsch = new String[maWunsch.length][5][7];
		for(int i = 0; i < maWunsch.length;i++){
			for(int j = 0; j < 7;j++){
				this.maWunsch[i][1][j] = maWunsch[i][j];
				this.maWunsch[i][2][j] = "keine SchichtDefinition";
				this.maWunsch[i][3][j] = "kein SchichtBeginn";
				this.maWunsch[i][4][j] = "kein SchichtEnde";
				this.maWunsch[i][0][j] = "MaID";

			}
		}
		
		
		this.maSchwelle = new int[maSchwelle.length][2];
		for (int i = 0;i<maSchwelle.length;i++){
			this.maSchwelle[i][1] = maSchwelle[i];
			this.maSchwelle[i][0] = i;
			}
		
		this.maAPunkte = new int[maAPunkte.length][2];
		for (int i = 0;i<maAPunkte.length;i++){
			this.maAPunkte[i][1] = maAPunkte[i];
			this.maAPunkte[i][0] = i;
			}
		
		this.maBPunkte = new int[maBPunkte.length][2];
		for (int i = 0;i<maBPunkte.length;i++){
			this.maBPunkte[i][1] = maBPunkte[i];
			this.maBPunkte[i][0] = i;
			}
		
		protokolliere("\n-----\n *****Mitarbeiter:");
		
		for (int i = 0; i< maMinuten.length;i++){
			protokolliere("\n MA " + maID[i] + ": Wochensoll: " + this.maMinuten[i][1] + ", A-Punkte: " + this.maAPunkte[i][1] + ", B-Punkte: " + this.maBPunkte[i][1] + "\n");
			protokolliere("Wünsche: \n");
			for (int j = 0; j <7;j++){
				protokolliere("Tag " +j + ": ");
				protokolliere(this.maWunsch[i][1][j]+"\n");
				protokolliere(this.maWunsch[i][2][j]+"\n");
				protokolliere(this.maWunsch[i][3][j]+"\n");
				protokolliere(this.maWunsch[i][4][j]+"\n");
			}
			protokolliere("-----");
		}
	}
		
	public void run(){
		
	}
	
	protected void protokolliere(String p){
		
		this.p = this.p  + p;
		
	}
	
	public String getProtokoll(){
		return p;
	}
	
	
}
