package aTGUI;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import tools.Pures;

public class FramePart {
	
	public enum FType {A, E};
	public enum COLOR {B, G, N};

	JPanel Frame = null;
	FType FrameT = null;
	COLOR[] CSet = null;
	
	public JLabel[] labels = null;
	
	public FramePart(FType FT, COLOR[] C){
		FrameT = FT;
		CSet = C;
		Frame = new JPanel(null);
		Frame.setSize(240,20);
		if (FrameT.equals(FType.A)){
			labels = new JLabel[3];
			labels[0] = new JLabel();
			labels[0].setLocation(8, 0);
			labels[0].setSize(70,20);
			labels[0].setHorizontalAlignment(SwingConstants.LEFT);
			Frame.add(labels[0]);
			labels[1] = new JLabel();
			labels[1].setLocation(85, 0);
			labels[1].setSize(70,20);
			labels[1].setHorizontalAlignment(SwingConstants.CENTER);
			Frame.add(labels[1]);
			labels[2] = new JLabel();
			labels[2].setLocation(162, 0);
			labels[2].setSize(70,20);
			labels[2].setHorizontalAlignment(SwingConstants.RIGHT);
			Frame.add(labels[2]);
		}else if (FrameT.equals(FType.E)){
			labels = new JLabel[1];
			labels[0] = new JLabel();
			labels[0].setLocation(10, 0);
			labels[0].setSize(220,20);
			labels[0].setHorizontalAlignment(SwingConstants.CENTER);
			labels[0].setFont(labels[0].getFont().deriveFont(10.0f));
			Frame.add(labels[0]);
		}
			
	}
	
	public void LChangeD(int LNum, Double Dvalue){
		double oldV = 0;
		String LT = labels[LNum].getText();
		if (LT.equals("")){
			labels[LNum].setText(Pures.doubleFPoint(Dvalue));
			labels[LNum].setForeground(Color.BLACK);
		}else{
			oldV = Double.parseDouble(LT);
			labels[LNum].setText(Pures.doubleFPoint(Dvalue));
			if (CSet[LNum].equals(COLOR.B)){
				if (oldV < Dvalue)
					labels[LNum].setForeground(Color.BLUE);
				else if (oldV > Dvalue)
					labels[LNum].setForeground(Color.RED);
				else
					labels[LNum].setForeground(Color.BLACK);
			}else if (CSet[LNum].equals(COLOR.G)){
				if (Dvalue > 0)
					labels[LNum].setForeground(Color.GREEN);
				else if (Dvalue < 0)
					labels[LNum].setForeground(Color.RED);
				else
					labels[LNum].setForeground(Color.BLACK);
			}
		}
	}
	
	
	public void setLabel(int LNum, String newS){
		labels[LNum].setText(newS);
	}
	
	public void erase(){
		if (FrameT.equals(FType.A)){
			labels[0].setText("");
			labels[1].setText("");
			labels[2].setText("");
		}else if (FrameT.equals(FType.E))
			labels[0].setText("");
	}

}
