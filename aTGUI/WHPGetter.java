package aTGUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import pureData.HPTable;
import pureData.priceRow;
import tools.CalTools;
import tools.GUISettings;
import tools.TXTWriter;
import fxcmIF.aTrader;

public class WHPGetter {
	
	public aTrader nat = null;
	
	public JComboBox<String> BTstratList = null;
	public JComboBox<String> BTinstList = null;
	public JComboBox<String> BTtimeList = null;
	public JTextField BTfromD = null;
	public JTextField BTtoD = null;
	public JLabel LogL = null;
	private JLabel RNum = null;
	private JButton hisBut = null;
	private GUISettings guis = null;
	
	public WHPGetter(JPanel HPPane, GUISettings gs){
		guis = gs;
		JLabel BTInstL = new JLabel();
		BTInstL.setText("Pair: ");
		BTInstL.setLocation(6,2);
		BTInstL.setSize(70,20);
		HPPane.add(BTInstL);
		JLabel BTTimeFL = new JLabel();
		BTTimeFL.setText("TF: ");
		BTTimeFL.setLocation(170,2);
		BTTimeFL.setSize(70,20);
		HPPane.add(BTTimeFL);
		JLabel BTfromL = new JLabel();
		BTfromL.setText("From: ");
		BTfromL.setLocation(310,2);
		BTfromL.setSize(70,20);
		HPPane.add(BTfromL);
		JLabel BTtoL = new JLabel();
		BTtoL.setText("WNum: ");
		BTtoL.setLocation(520,2);
		BTtoL.setSize(70,20);
		HPPane.add(BTtoL);
		BTinstList = new JComboBox<String>();
		BTinstList.setLocation(40,2);
		BTinstList.setSize(100,20);
		HPPane.add(BTinstList);
		BTtimeList = new JComboBox<String>();
		BTtimeList.setLocation(190,2);
		BTtimeList.setSize(100,20);
		HPPane.add(BTtimeList);
		BTfromD = new JTextField();
		BTfromD.setLocation(350, 2);
		BTfromD.setSize(150,20);
		HPPane.add(BTfromD);
		BTtoD = new JTextField();
		BTtoD.setLocation(570,2);
		BTtoD.setSize(150,20);
		HPPane.add(BTtoD);
		RNum = new JLabel();
		RNum.setLocation(920, 2);
		RNum.setSize(400,20);
		HPPane.add(RNum);
		LogL = new JLabel();
		LogL.setText("<html>");
		LogL.setOpaque(true);
		LogL.setBackground(Color.BLACK);
		LogL.setForeground(Color.YELLOW);
		LogL.setFont(LogL.getFont().deriveFont(13.0f));
		JScrollPane LogSPanel = new JScrollPane(LogL);
		JScrollBar vertical = LogSPanel.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
		LogSPanel.setLocation(5, 30);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) screenSize.getWidth();
		int height = (int)screenSize.getHeight() - 40;
		int lower = 340;
		int lheight = 260;
		int upper = 500;
		if (height - lower > upper){
			lower = height - upper - 30;
			lheight = height - upper - 110;
		}
		LogSPanel.setSize(width - 20, lheight - 65);
		HPPane.add(LogSPanel);
		hisBut = new JButton();
		hisBut.setText("Get Prices");
		hisBut.setLocation(750,2);
		hisBut.setSize(150,20);
		HPPane.add(hisBut);
		hisBut.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Calendar From = Calendar.getInstance();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String fromD = BTfromD.getText() + " 17:00:00";
				try {
					From.setTime(df.parse(fromD.trim()));
					if (From.get(Calendar.DAY_OF_WEEK) != 1){
						int day = From.get(Calendar.DAY_OF_WEEK);
						day = 7 - day + 1;
						From = CalTools.CalJump(From, "D1", day);
					}
					int wNum = Integer.parseInt(BTtoD.getText());
					DataGetter dt = new DataGetter(hisBut, LogL, RNum, From, wNum,
							nat, BTinstList.getSelectedItem().toString(), 
							BTtimeList.getSelectedItem().toString(), guis);
					new Thread(dt).start();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
		});
	}
	
	public void setNat(aTrader n){
		nat = n;
	}

}

class DataGetter extends Thread{
	
	JButton getter = null;
	JLabel Log = null;
	JLabel flag = null;
	Calendar sCal = null;
	int wNum = 0;
	String inst = "";
	String TF = "";
	aTrader nat = null;
	GUISettings guis = null;
	
	public DataGetter(JButton g, JLabel l, JLabel fl, Calendar sc, int wn, aTrader n,
			String ins, String tf, GUISettings gs){
		guis = gs;
		getter = g;
		Log = l;
		flag = fl;
		sCal = sc;
		wNum = wn;
		nat = n;
		inst = ins;
		TF = tf;
	}
	
	public void run(){
		getter.setEnabled(false);
		Calendar from = sCal;
		ArrayList<Calendar> fFails = new ArrayList<Calendar>();
		try {
			Calendar to = tools.CalTools.CalJump(from, "D1", 5);
			Log.setText(Log.getText() + "<br>Getting Prices:");
			for (int i = 0; i < wNum; i++){
				flag.setText("Getting his prices: Week " + (i + 1));
				Log.setText(Log.getText() + "<br>From: " + from.getTime() + " - To: " +
						to.getTime());
				HPTable hpt = nat.getHistorics(inst, TF, from, to);
				Log.setText(Log.getText() + " - Rows: " + hpt.size() + " - Test: " + hpt.TestR);
				if (hpt.TestR.equals("p")){
					String fn = guis.dataFolder + "weekly/"+ 
							inst.replace("/","") + "-" + TF + "-" + (from.get(Calendar.MONTH) + 1)
							+ "-" + from.get(Calendar.DAY_OF_MONTH) +"-" +
							(to.get(Calendar.MONTH) + 1) + "-" + to.get(Calendar.DAY_OF_MONTH) + ".csv";
					TXTWriter writer = new TXTWriter(fn,false);
					for (int j = 0; j < hpt.size(); j++){
						priceRow pr = hpt.rows.get(j);
						String row = pr.rDate.getTime() + "," + pr.rBO + "," + pr.rBH + "," +
						pr.rBL + "," + pr.rBC + "," + pr.rAO + "," + pr.rAH + "," + pr.rAL +
						"," + pr.rAC + "\r\n";
						writer.write(row);
					}
					Log.setText(Log.getText() + " - written: " + fn);
				}else{
					fFails.add(from);
				}
				from = tools.CalTools.CalJump(from, "D1", 7);
				to = tools.CalTools.CalJump(from, "D1", 5);
			}
			Log.setText(Log.getText() + "<br>All is Done!!");
			flag.setText("Done!!");
		} catch (ParseException | InterruptedException e) {
			e.printStackTrace();
		}
		getter.setEnabled(true);
	}
}
