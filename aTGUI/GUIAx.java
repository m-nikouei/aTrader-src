package aTGUI;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import aTWorker.StrategiesList;
import aTWorker.Worker;
import aTWorker.strategy;
import pureData.*;
import fxcmIF.*;
import tools.*;


public class GUIAx {

}



//new Class comes here
class InstPanel{

	private int top = 5;
	private int left = 1;
	private int height = 500;
	private int width = 250;
	private JPanel priceP = null;
	private FramePart PA1 = null;
	private FramePart PA2 = null;
	private JComboBox<String> stratList = null;
	public JComboBox<String> timeF = null;
	private JTextField AmtF = null;
	public JButton SSBut = null;
	private JLabel WNP = null;
	private int TradeSF = 0;
	private GUISettings guis = null;
	
	private Worker wIF = null;
	
	private FXOffer FX = null;
	
	public StratRep SP = null;
	public TradeRep TP = null;
	
	public InstPanel(FXOffer FXO, GUISettings s){
		FX = FXO;
		guis = s;
		left = 1 + FX.getIndex() * width;
		TitledBorder title;
		title = BorderFactory.createTitledBorder(FX.getInstrument());
		priceP = new JPanel(null);
		priceP.setBorder(title);
		priceP.setSize(width,height);
		priceP.setLocation(left, top);
		FramePart.COLOR[] CSet =  {FramePart.COLOR.B, FramePart.COLOR.B, FramePart.COLOR.B};
		PA1 = new FramePart(FramePart.FType.A, CSet);
		PA1.Frame.setLocation(5, 18);
		priceP.add(PA1.Frame);
		PA2 = new FramePart(FramePart.FType.A, CSet);
		PA2.Frame.setLocation(5, 38);
		priceP.add(PA2.Frame);
		PA1.LChangeD(0, FX.getBid());
		PA1.LChangeD(1, FX.getSpread());
		PA1.LChangeD(2, FX.getAsk());
		PA2.LChangeD(0, FX.getLow());
		PA2.LChangeD(1, FX.getVolume());
		PA2.LChangeD(2, FX.getHigh());
		JSeparator wSep1 = new JSeparator(SwingConstants.HORIZONTAL);
		wSep1.setLocation(5,60);
		wSep1.setSize(240, 7);
		priceP.add(wSep1);
		stratList = new JComboBox<String>();
		stratList.setLocation(10, 67);
		stratList.setSize(70,20);
		stratList.setModel(new JComboBox<String>(StrategiesList.StratList()).getModel());
		priceP.add(stratList);
		timeF = new JComboBox<String>();
		timeF.setLocation(90, 67);
		timeF.setSize(70,20);
		//timeF.setModel(new JComboBox<String>().getModel());
		priceP.add(timeF);
		AmtF = new JTextField();
		AmtF.setLocation(170, 67);
		AmtF.setSize(70,20);
		AmtF.setText("1000");
		priceP.add(AmtF);
		SSBut = new JButton();
		SSBut.setLocation(10, 90);
		SSBut.setSize(70,20);
		SSBut.setText("Start");
		priceP.add(SSBut);
		SSBut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (TradeSF == 0){
						TradeSF = 1;
						AmtF.setEnabled(false);
						stratList.setEnabled(false);
						timeF.setEnabled(false);
						strategy st = aTWorker.StrategiesList.stratMaker(stratList.getSelectedItem().toString());
						WNP.setText("Trading " + stratList.getSelectedItem().toString());
						SSBut.setText("Stop");
						wIF = new Worker(guis, timeF.getSelectedItem().toString(), 
								Integer.parseInt(AmtF.getText()), FXO.getofferID(), SP, TP,st);
						//ReadListener re = new ReadListener(fxIF);
						//fxIF.setListener(re);
						new Thread(wIF).start();
				}else if (TradeSF == 1){
					TradeSF = 0;
					AmtF.setEnabled(true);
					stratList.setEnabled(true);
					timeF.setEnabled(true);
					wIF.Stop();
					WNP.setText("Not Trading");
					SSBut.setText("Start");
				}
			}
		});
		WNP = new JLabel();
		WNP.setLocation(90,90);
		WNP.setSize(150,20);
		WNP.setText("Not Trading");
		priceP.add(WNP);
		SP = new StratRep(priceP);
		JSeparator wSep2 = new JSeparator(SwingConstants.HORIZONTAL);
		wSep2.setLocation(10,267);
		wSep2.setSize(230, 8);
		priceP.add(wSep2);
		TP = new TradeRep(priceP);
	}
	
	public JPanel getPricePanel(){
		return priceP;
	}
	
	public FXOffer getFXOffer(){
		return FX;
	}
	
	public void change(FXOffer FXO){
		FX = FXO;
		PA1.LChangeD(0, FX.getBid());
		PA1.LChangeD(1, FX.getSpread());
		PA1.LChangeD(2, FX.getAsk());
		PA2.LChangeD(0, FX.getLow());
		PA2.LChangeD(1, FX.getVolume());
		PA2.LChangeD(2, FX.getHigh());
	}
	
}


class OrderWindow extends JFrame{

	private static final long serialVersionUID = 1L;
	String[] types = { "Open", "Entry", "Stop", "Limit", "Close" };
	String[] BSs = {"Buy","Sell"};
	FXOffer[] offers = null;
	String[] accountIDs = null;
	aTrader nat = null;
	
	JTextField rateT = null;
	JComboBox<String> typeList = null;
	JComboBox<String> accountList = null;
	JComboBox<String> instList = null;
	JComboBox<String> BSList = null;
	JTextField amountT = null;
	
	public OrderWindow(String[] ids, FXOffer[] ins, aTrader an){
		this.setSize(500,145);
		this.setTitle("Run Order Window");
		this.setLocation(500,200);
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setAlwaysOnTop(true);
		this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		JPanel mainPanel = new JPanel(null);
		add(mainPanel);
		
		closeLis cl = new closeLis(this);
		nat = an;
		
		JButton runner = new JButton();
		runner.setText("Run Order");
		runner.setLocation(390,20);
		runner.setSize(100,20);
		runner.setFont(new Font("Courier New", Font.PLAIN, 12));
		runner.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String bs = "";
				if (BSList.getSelectedItem().equals("Buy"))
					bs = "B";
				else bs = "S";
				FXOffer fo = offers[instList.getSelectedIndex()];
				if (typeList.getSelectedItem().equals("Open")){
					FXTrade tr = null;
					try {
						nat.ImmediateRun("open", accountList.getSelectedItem().toString(), fo, tr, bs, Integer.parseInt(amountT.getText()));
					} catch (NumberFormatException e1) {
						e1.printStackTrace();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else {
					try {
						nat.EntryRun(typeList.getSelectedItem().toString().toLowerCase(),accountList.getSelectedItem().toString(), fo.getofferID(), Double.parseDouble(rateT.getText()), bs, Integer.parseInt(amountT.getText()));
					} catch (NumberFormatException e1) {
						e1.printStackTrace();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		runner.addActionListener(cl);
		mainPanel.add(runner);
		
		JButton canceler = new JButton();
		canceler.setText("Cancel");
		canceler.setLocation(390,50);
		canceler.setSize(100,20);
		canceler.setFont(new Font("Courier New", Font.PLAIN, 12));
		canceler.addActionListener(cl);
		mainPanel.add(canceler);
		
		JLabel typeL = new JLabel();
		typeL.setText("Type:");
		typeL.setLocation(10,20);
		typeL.setAlignmentX(RIGHT_ALIGNMENT);
		typeL.setSize(100,20);
		typeL.setFont(new Font("Courier New", Font.PLAIN, 12));
		mainPanel.add(typeL);
		
		JLabel accountL = new JLabel();
		accountL.setText("Account ID:");
		accountL.setLocation(200,20);
		accountL.setAlignmentX(RIGHT_ALIGNMENT);
		accountL.setSize(100,20);
		accountL.setFont(new Font("Courier New", Font.PLAIN, 12));
		mainPanel.add(accountL);
		
		typeList = new JComboBox<String>(types);
		typeList.setLocation(80,20);
		typeList.setSize(100,20);
		mainPanel.add(typeList);
		
		accountIDs = ids;
		accountList = new JComboBox<String>(accountIDs);
		accountList.setLocation(275,20);
		accountList.setSize(100,20);
		mainPanel.add(accountList);
		
		JLabel InstL = new JLabel();
		InstL.setText("Instrument:");
		InstL.setLocation(10,50);
		InstL.setAlignmentX(RIGHT_ALIGNMENT);
		InstL.setSize(100,20);
		InstL.setFont(new Font("Courier New", Font.PLAIN, 12));
		mainPanel.add(InstL);
		
		JLabel BSL = new JLabel();
		BSL.setText("Buy/Sell");
		BSL.setLocation(200,50);
		BSL.setAlignmentX(RIGHT_ALIGNMENT);
		BSL.setSize(100,20);
		BSL.setFont(new Font("Courier New", Font.PLAIN, 12));
		mainPanel.add(BSL);
		
		offers = ins;
		String[] insts = new String[offers.length];
		for (int i = 0; i < insts.length; i++)
			insts[i] = offers[i].getInstrument();
		instList = new JComboBox<String>(insts);
		instList.setLocation(80,50);
		instList.setSize(100,20);
		mainPanel.add(instList);
		
		BSList = new JComboBox<String>(BSs);
		BSList.setLocation(275,50);
		BSList.setSize(100,20);
		mainPanel.add(BSList);
		
		JLabel amountL = new JLabel();
		amountL.setText("Amount:");
		amountL.setLocation(10,80);
		amountL.setAlignmentX(RIGHT_ALIGNMENT);
		amountL.setSize(100,20);
		amountL.setFont(new Font("Courier New", Font.PLAIN, 12));
		mainPanel.add(amountL);
		
		JLabel rateL = new JLabel();
		rateL.setText("Rate:");
		rateL.setLocation(200,80);
		rateL.setAlignmentX(RIGHT_ALIGNMENT);
		rateL.setSize(100,20);
		rateL.setFont(new Font("Courier New", Font.PLAIN, 12));
		mainPanel.add(rateL);
		
		amountT = new JTextField();
		amountT.setText("1000");
		amountT.setLocation(80,80);
		amountT.setSize(100,20);
		mainPanel.add(amountT);
		
		rateT = new JTextField();
		rateT.setLocation(275,80);
		rateT.setSize(100,20);
		mainPanel.add(rateT);
		
	}
}

class RemoveWindow extends JFrame{
	
	private static final long serialVersionUID = 1L;
	String[] accountIDs = null;
	aTrader nat = null;
	String[] OIDs = null;
	
	JComboBox<String> typeList = null;
	JComboBox<String> accountList = null;
	
	public RemoveWindow(String[] ids, String[] oid, aTrader an){
		this.setSize(500,145);
		this.setTitle("Remove Order Window");
		this.setLocation(500,200);
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setAlwaysOnTop(true);
		this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		JPanel mainPanel = new JPanel(null);
		add(mainPanel);
		
		closeLis cl = new closeLis(this);
		nat = an;
		
		JButton runner = new JButton();
		runner.setText("Remove Order");
		runner.setLocation(390,20);
		runner.setSize(100,20);
		runner.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try {
					nat.RemoveOrder(typeList.getSelectedItem().toString(), accountList.getSelectedItem().toString());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		runner.addActionListener(cl);
		mainPanel.add(runner);
		
		JButton canceler = new JButton();
		canceler.setText("Cancel");
		canceler.setLocation(390,50);
		canceler.setSize(100,20);
		canceler.addActionListener(cl);
		mainPanel.add(canceler);
		
		JLabel typeL = new JLabel();
		typeL.setText("Account ID");
		typeL.setLocation(10,20);
		typeL.setAlignmentX(RIGHT_ALIGNMENT);
		typeL.setSize(100,20);
		mainPanel.add(typeL);
		
		JLabel accountL = new JLabel();
		accountL.setText("Order ID:");
		accountL.setLocation(200,20);
		accountL.setAlignmentX(RIGHT_ALIGNMENT);
		accountL.setSize(100,20);
		mainPanel.add(accountL);
		
		accountIDs = ids;
		typeList = new JComboBox<String>(accountIDs);
		typeList.setLocation(80,20);
		typeList.setSize(100,20);
		mainPanel.add(typeList);
		
		OIDs = oid;
		accountList = new JComboBox<String>(OIDs);
		accountList.setLocation(275,20);
		accountList.setSize(100,20);
		mainPanel.add(accountList);
	}
}

class SettingsWindow extends JFrame{
	
	private static final long serialVersionUID = 1L;
	String SFad = "";
	GUIIF IF = null;
	
	Double mEquity = 0D;
	String Account = "";
	String dbad = "";
	
	JTextField adText = null;
	JTextField meText = null;
	JTextField acText = null;
	
	public SettingsWindow(String ad, GUIIF na){
		IF = na;
		SFad = ad;
		try {
			readSettings();
		} catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
		}
		this.setSize(500,145);
		this.setTitle("Settings Window");
		this.setLocation(500,200);
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setAlwaysOnTop(true);
		this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		JPanel mainPanel = new JPanel(null);
		add(mainPanel);
		
		SFad = ad;
		
		closeLis cl = new closeLis(this);
		
		JButton runner = new JButton();
		runner.setText("Change");
		runner.setLocation(390,20);
		runner.setSize(100,20);
		runner.setFont(new Font("Courier New", Font.PLAIN, 12));
		runner.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try {
					String toBeSaved = "Database = " + adText.getText() + "\n" + 
							"Account = " + acText.getText() + "\n" + 
							"MaxEquity = " + meText.getText();
					FileWriter fileWriter = new FileWriter(SFad, false);
					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
					bufferedWriter.write(toBeSaved);
					bufferedWriter.close();
					//IF.DBAd.take();
					//IF.DBAd.put(adText.getText());
					//IF.Account.take();
					//IF.Account.put(acText.getText());
					//IF.MaxE = Double.parseDouble(meText.getText());
					//IF.MaxEquityCal();
					//IF.MaxEL.setText(IF.maxEquity.peek().toString());
					//IF.accountL.setText(IF.Account.peek());
				} catch (IOException e1) {
					e1.printStackTrace();
				//} catch (InterruptedException e1) {
				//	e1.printStackTrace();
				}
			}
		});
		runner.addActionListener(cl);
		mainPanel.add(runner);
		
		JButton canceler = new JButton();
		canceler.setText("Cancel");
		canceler.setLocation(390,50);
		canceler.setSize(100,20);
		canceler.addActionListener(cl);
		canceler.setFont(new Font("Courier New", Font.PLAIN, 12));
		mainPanel.add(canceler);
		
		JLabel typeL = new JLabel();
		typeL.setText("Database Address:");
		typeL.setLocation(10,20);
		typeL.setAlignmentX(RIGHT_ALIGNMENT);
		typeL.setSize(150,20);
		typeL.setFont(new Font("Courier New", Font.PLAIN, 11));
		mainPanel.add(typeL);
		
		JLabel maxEL = new JLabel();
		maxEL.setText("Max Equity:");
		maxEL.setLocation(10,50);
		maxEL.setAlignmentX(RIGHT_ALIGNMENT);
		maxEL.setSize(100,20);
		maxEL.setFont(new Font("Courier New", Font.PLAIN, 12));
		mainPanel.add(maxEL);
		
		JLabel accountL = new JLabel();
		accountL.setText("Account:");
		accountL.setLocation(200,50);
		accountL.setAlignmentX(RIGHT_ALIGNMENT);
		accountL.setSize(100,20);
		accountL.setFont(new Font("Courier New", Font.PLAIN, 12));
		mainPanel.add(accountL);
		
		adText = new JTextField();
		adText.setLocation(125,20);
		adText.setSize(250,20);
		mainPanel.add(adText);
		
		meText = new JTextField();
		meText.setLocation(80,50);
		meText.setSize(100,20);
		mainPanel.add(meText);
		
		acText = new JTextField();
		acText.setLocation(255,50);
		acText.setSize(120,20);
		mainPanel.add(acText);
		
		meText.setText(mEquity.toString());
		adText.setText(dbad);
		acText.setText(Account);
		
	}
	
	public void readSettings() throws IOException, InterruptedException{
		try{
			FileReader fileReader = new FileReader(SFad);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = bufferedReader.readLine();
			while (line  != null){
				String[] splitted = line.split(" = ");
				if (splitted[0].equals("Database"))
					dbad = splitted[1];
				else if (splitted[0].equals("Account"))
					Account = splitted[1];
				else if (splitted[0].equals("MaxEquity"))
					mEquity = Double.parseDouble(splitted[1]);
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
		}catch(FileNotFoundException ex) {
		}
	}
	
}


class closeLis implements ActionListener{
	
	private JFrame mframe = null;
	
	public closeLis(JFrame f){
		mframe = f;
	}

	public void actionPerformed(ActionEvent e) {
		mframe.dispose();
	}
	
}

class HisPriceGetter extends Thread{
	
	aTrader nat = null;
	JLabel rnum = null;
	MTM TModel = null;
	String fromD = "";
	String toD = "";
	String instList = "";
	String timeList = "";
	JButton hisget = null;
	JButton hisdb = null;
	
	public HisPriceGetter(aTrader nt, JLabel rn, MTM tm, String fD, String tD, String iList, String tList, JButton hp, JButton hdb){
		nat= nt;
		rnum = rn;
		TModel = tm;
		fromD = fD;
		toD = tD;
		instList = iList;
		timeList = tList;
		hisget = hp;
		hisdb = hdb;
	}
	
	public void run(){
		try {
			hisget.setEnabled(false);
			hisdb.setEnabled(false);
			TModel.clearTable();
			rnum.setText("Historic Prices are coming. Check the Log to see the progress...");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar From = Calendar.getInstance();
			Calendar To = Calendar.getInstance();
			From.setTime(df.parse(fromD.trim()));
			To.setTime(df.parse(toD.trim()));
			HPTable hpt = nat.getHistorics(instList, timeList, From, To);
			rnum.setText("TestResult: " + hpt.TestR + ". Number of Rows: " + Integer.toString(hpt.rows.size()));
			if (hpt.rows.size() > 0){
				for(int i= 0; i < hpt.rows.size(); i++){
					String[] hrow = new String[10];
					hrow[0] = Integer.toString(i + 1);
					hrow[1] = hpt.rows.get(i).rDate.getTime().toString();
					hrow[2] = Double.toString(hpt.rows.get(i).rBO);
					hrow[3] = Double.toString(hpt.rows.get(i).rBH);
					hrow[4] = Double.toString(hpt.rows.get(i).rBL);
					hrow[5] = Double.toString(hpt.rows.get(i).rBC);
					hrow[6] = Double.toString(hpt.rows.get(i).rAO);
					hrow[7] = Double.toString(hpt.rows.get(i).rAH);
					hrow[8] = Double.toString(hpt.rows.get(i).rAL);
					hrow[9] = Double.toString(hpt.rows.get(i).rAC);
					TModel.addNewRow(hrow);
				}
			}
			hisget.setEnabled(true);
			hisdb.setEnabled(true);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
}

class HistoricPriceWriter extends Thread{

	String FileAddress = "";
	String[] HPOPram = null;
	MTM tmodel = null;
	JLabel rnum = null;
	JButton hisget = null;
	JButton hisdb = null;
	
	public HistoricPriceWriter(String fAd, String[] pram, MTM tm, JLabel rn, JButton hp, JButton hdb){
		FileAddress = fAd + pram[0].replace("/","") + "-" + pram[1] + "-" + pram[2].replace(" 17:00:00",  "").replace(":", "-").replace(" ","-") + "-" + pram[3].replace(" 17:00:00",  "").replace(":", "-").replace(" ","-") + ".csv" ;
		HPOPram = pram;
		tmodel = tm;
		rnum = rn;
		hisget = hp;
		hisdb = hdb;
	}
	
	public void run(){
			hisget.setEnabled(false);
			hisdb.setEnabled(false);
			TXTWriter hisWriter = new TXTWriter(FileAddress, false);
			rnum.setText("Writing historic data to disk!!");
			//hisWriter.write(HPOPram[0] + "," + HPOPram[1] + "," + HPOPram[2] + "," + HPOPram[3] + "\r\n");
			//hisWriter.write("Date,BidOpen,BidHigh,BidLow,BidClose,AskOpen,AskHigh,AskLow,AskClose\r\n");
			for (int i = 0; i < tmodel.getRowCount(); i++){
				String SQL = (String) tmodel.getValueAt(i, 1);
				for (int j = 2; j < tmodel.getColumnCount(); j++)
					SQL += "," + tmodel.getValueAt(i, j);
				SQL += "\n";
				hisWriter.write(SQL);
				if (i % 100 == 0)
					rnum.setText(i + " rows out of " + tmodel.getRowCount() + " are written.");
			}
			rnum.setText("Written to disk!");
			hisget.setEnabled(true);
			hisdb.setEnabled(true);
	}
	
}

class WindowColseErrorDialog extends JFrame{
	
	private static final long serialVersionUID = 1L;

	public WindowColseErrorDialog(){
		this.setSize(330,100);
		this.setTitle("Window Close");
		this.setLocation(500,200);
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setAlwaysOnTop(true);
		JPanel epan = new JPanel(null);
		add(epan);
		JLabel msg = new JLabel();
		msg.setLocation(10,15);
		msg.setSize(480,20);
		msg.setText("There are running stretegies!! Please close them first.");
		closeLis cl = new closeLis(this);
		JButton okB = new JButton();
		okB.setText("OK");
		okB.setLocation(215, 40);
		okB.setSize(100,20);
		okB.addActionListener(cl);
		epan.add(msg);
		epan.add(okB);
	}
	
}

