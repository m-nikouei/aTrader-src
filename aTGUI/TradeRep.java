package aTGUI;

import javax.swing.JPanel;

public class TradeRep {

	public FramePart startD = null;
	public FramePart ticks = null;
	public FramePart prices = null;
	public FramePart spreads = null;
	
	public TradeRep(JPanel parent){
		FramePart.COLOR[] CSetN = {FramePart.COLOR.N, FramePart.COLOR.N, FramePart.COLOR.N};
		FramePart.COLOR[] CSetG = {FramePart.COLOR.G, FramePart.COLOR.G, FramePart.COLOR.G};
		FramePart.COLOR[] CSetB = {FramePart.COLOR.B, FramePart.COLOR.B, FramePart.COLOR.B};
		FramePart.COLOR[] CSetM = {FramePart.COLOR.N, FramePart.COLOR.G, FramePart.COLOR.B};
		startD = new FramePart(FramePart.FType.E,CSetN);
		startD.Frame.setLocation(5,280);
		parent.add(startD.Frame);
		ticks = new FramePart(FramePart.FType.A, CSetM);
		ticks.Frame.setLocation(5,300);
		parent.add(ticks.Frame);
		prices = new FramePart(FramePart.FType.A, CSetB);
		prices.Frame.setLocation(5,320);
		parent.add(prices.Frame);
		spreads = new FramePart(FramePart.FType.A, CSetG);
		spreads.Frame.setLocation(5,340);
		parent.add(spreads.Frame);
	}
	
	public void erase(){
		startD.erase();
		ticks.erase();
		prices.erase();
		spreads.erase();
	}
	
}
