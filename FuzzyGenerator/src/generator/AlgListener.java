package generator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.*;
import javax.swing.table.TableColumn;

public class AlgListener implements ActionListener{
	
	protected ProtokollFrame f;
	protected Object o;
	protected String oString;
	private Algorithmus fuzzy;
	
	public AlgListener(ProtokollFrame f, Object o){
		this.f = f;
		this.o = o;
		oString = o.toString();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		f.TextLabel.append("\n-----\n*Generiert mit: " + oString);
		switch (oString) {
		
		case "FuzzySatisfactionHeuristik": fuzzy = new FuzzySatisfactionHeuristic();
				break;
		
		}
		fuzzy.setSchichten(f.schichtenInfo);
		fuzzy.setMitarbeiter(f.maMinuten, f.maWunsch, f.maSchwelle, f.maAPunkte, f.maBPunkte);
		fuzzy.run();
		f.TextLabel.append(fuzzy.getProtokoll());
		
		
		 f.valueConstraints.gridx = 1;
		 f.valueConstraints.gridy = 5;
		 f.valueConstraints.weighty = 10;
		 
		 String[][] rowData = new String[fuzzy.maID.length][49];
		 

		 String[] columnNames =  {
				 "0","30","60","90","120","150","180","210","240","270","300","330", "360","390","420","450","480","510","540","570","600","630","660","690","720","750","780","810","840","870","900","930","960","990","1020","1050","1080","1110","1140","1170","1200","1230","1260","1290","1320","1350","1380","1410","1440"
		 };
		 
		 f.graphik = new JTable(rowData,columnNames);
		 f.add(f.graphik, f.valueConstraints);
	}

}
