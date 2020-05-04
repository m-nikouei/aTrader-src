package aTGUI;

import java.util.Calendar;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class StratRep {

	public FramePart startD = null;
	public FramePart status = null;
	public FramePart speed = null;
	public FramePart states = null;
	public FramePart counts = null;
	public FramePart tradeN = null;
	public FramePart pls = null;
	public FramePart Apls = null;
	public MTM TradesT = null;
	JScrollPane TSPanel = null;
	
	
	public StratRep(JPanel parent){
		FramePart.COLOR[] CSetN = {FramePart.COLOR.N, FramePart.COLOR.N, FramePart.COLOR.N};
		FramePart.COLOR[] CSetG = {FramePart.COLOR.G, FramePart.COLOR.G, FramePart.COLOR.G};
		startD = new FramePart(FramePart.FType.E, CSetN);
		startD.Frame.setLocation(5, 112);
		parent.add(startD.Frame);
		status = new FramePart(FramePart.FType.E, CSetN);
		status.Frame.setLocation(5, 127);
		parent.add(status.Frame);
		speed = new FramePart(FramePart.FType.A, CSetN);
		speed.Frame.setLocation(5, 145);
		parent.add(speed.Frame);
		states = new FramePart(FramePart.FType.A, CSetN);
		states.Frame.setLocation(5, 165);
		parent.add(states.Frame);
		counts = new FramePart(FramePart.FType.A, CSetN);
		counts.Frame.setLocation(5, 185);
		parent.add(counts.Frame);
		tradeN = new FramePart(FramePart.FType.A, CSetN);
		tradeN.Frame.setLocation(5, 205);
		parent.add(tradeN.Frame);
		pls = new FramePart(FramePart.FType.A, CSetG);
		pls.Frame.setLocation(5, 225);
		parent.add(pls.Frame);
		Apls = new FramePart(FramePart.FType.A, CSetG);
		Apls.Frame.setLocation(5, 245);
		parent.add(Apls.Frame);
		String[] Tcolumn = {"Date", "B/S", "P/L"};
		TradesT = new MTM(Tcolumn);
		JTable Trades = new JTable(TradesT);
		TSPanel = new JScrollPane(Trades);
		Trades.setFillsViewportHeight(true);
		TSPanel.setLocation(5, 365);
		TSPanel.setSize(240, 130);
		parent.add(TSPanel);
		TSPanel.setVisible(false);
		
	}
	
	public void show(){
		startD.setLabel(0, Calendar.getInstance().getTime().toString());
		speed.setLabel(0,"Max: 0 t/s");
		speed.setLabel(2,"Spd: 0 t/s");
		Calendar now = Calendar.getInstance();
		states.setLabel(0,"MIH: " + now.get(Calendar.MINUTE));
		counts.setLabel(0,"DNI");
		counts.setLabel(1,"0");
		counts.setLabel(2,"0");
		tradeN.setLabel(0,"0");
		tradeN.setLabel(1,"0");
		tradeN.setLabel(2,"0");
		pls.setLabel(0,"0");
		pls.setLabel(1,"0");
		pls.setLabel(2,"0");
		Apls.setLabel(0,"0");
		Apls.setLabel(1,"0");
		Apls.setLabel(2,"0");
		TSPanel.setVisible(true);
	}
	
	public void erase(){
		startD.erase();
		status.erase();
		speed.erase();
		states.erase();
		counts.erase();
		tradeN.erase();
		pls.erase();
		Apls.erase();
		TSPanel.setVisible(false);
		TradesT.clearTable();
	}
	
}
