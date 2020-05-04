package aTGUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

import fxcmIF.*;
import pureData.*;
import tools.GUISettings;
import tools.Pures;

import javax.swing.*;


public class GUIIF extends JFrame{
	
	//Interfaces
	private ReadListener rl = null;
	public aTrader nat = null;
	
	//GUI
	private static final long serialVersionUID = 3L;
	private JPanel forexPanel = null;
	public JLabel statusLabel = null;
	private JButton orderbut = null;
	private JButton remBut = null;
	private JButton settingBut = null;
	private JLabel LogLab = null;
	public MTM[] Tmodel = new MTM[5];
	ActLis rLis = null;
	public JLabel MaxEL = null;
	public JLabel accountL = null;
	public JLabel SesPr = null;
	private JComboBox<String> instList = null;
	private JComboBox<String> timeList = null;
	private JTextField fromD = null;
	private JTextField toD = null;
	private JLabel RNum = null;
	private JButton hisBut = null;
	private JButton hisdb = null;
	private WHPGetter WHPG = null;
	
	//Data
	public ArrayList<InstPanel> FXGroup = new ArrayList<InstPanel>();
	HisPriceGetter HPG = null;
	public String[] HisPriceOrderComp = new String[4];
	GUISettings guis = null;
	
	public static void main(String[] args) throws NumberFormatException, Exception {
		GUIIF GIF = new GUIIF();
		ReadListener re = new ReadListener(GIF);
		GIF.start(re);
	}
	
	public GUIIF() throws IOException, InterruptedException{
		
		//reading settings
		guis = new GUISettings();
		guis.readSettings();
		/*if (rsflag){
			SettingsWindow dialog = new SettingsWindow(guis.settingsAd, this);
			dialog.setVisible(true);
		}*/
		
		//creating gui
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
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setSize(width,height);
		this.setTitle("aTrader V7");
		this.setLocation(0,0);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		JPanel mainPanel = new JPanel(null);
		add(mainPanel);
		addWindowListener(new WindowAd(this));
		
		//Groups
		forexPanel = new JPanel(null);
		forexPanel.setSize(width - 10,450);
		JScrollPane FXSPanel = new JScrollPane(forexPanel);
		FXSPanel.setSize(width - 10, height - lower);
		FXSPanel.setLocation(7,30);
		mainPanel.add(FXSPanel);
		
		//Tabs
		JTabbedPane tabbedPane = new JTabbedPane();
				
		//Account Panel.
		JPanel accountPane = new JPanel(null);
		String[] AColumn = {"Name", "Equity", "Day P/L", "Used Mr","Used Main Mr", "Usable Mr", "Gross P/L"};
		Tmodel[0] = new MTM(AColumn);
		JTable Accounts = new JTable(Tmodel[0]);
		JScrollPane sPane1 = new JScrollPane(Accounts);
		Accounts.setFillsViewportHeight(true);
		sPane1.setLocation(5, 5);
		sPane1.setSize(width - 20, lheight - 35);
		accountPane.add(sPane1);
				
		//Order Panel
		JPanel orderPane = new JPanel(null);
		String[] OColumn = {"Order ID","Account", "Status", "Symbol", "Amt K", "Sell/Buy", "Rate","Stop", "Limit", "Time"};
		Tmodel[1] = new MTM(OColumn);
		JTable Orders = new JTable(Tmodel[1]);
		JScrollPane OSPane1 = new JScrollPane(Orders);
		Orders.setFillsViewportHeight(true);
		OSPane1.setLocation(5, 5);
		OSPane1.setSize(width - 20, lheight - 35);
		orderPane.add(OSPane1);
				
		//Trade Panel
		JPanel tradePane = new JPanel(null);
		String[] Tcolumn = {"Trade ID", "Account", "Symbol", "Amt K", "Sell/Buy", "Open", "Close", "P/L", "Gross P/L" , "Commission",
						"Roll", "Used Mr", "Time"};
		Tmodel[2] = new MTM(Tcolumn);
		JTable Trades = new JTable(Tmodel[2]);
		JScrollPane TSPanel = new JScrollPane(Trades);
		Trades.setFillsViewportHeight(true);
		TSPanel.setLocation(5, 5);
		TSPanel.setSize(width - 20, lheight - 35);
		tradePane.add(TSPanel);
				
		//Close Trade Panel
		JPanel ctradePane = new JPanel(null);
		String[] CTcolumn = {"Trade ID", "Account", "Symbol", "Amt K", "Sell/Buy", "Open", "Close", "P/L", "Gross P/L" , "Commission",
					"Roll", "Open TIme", "Close Time"};
		Tmodel[3] = new MTM(CTcolumn);
		JTable CTrades = new JTable(Tmodel[3]);
		JScrollPane CTSPanel = new JScrollPane(CTrades);
		CTrades.setFillsViewportHeight(true);
		CTSPanel.setLocation(5, 5);
		CTSPanel.setSize(width - 20, lheight - 35);
		ctradePane.add(CTSPanel);
		
		//Historic Prices Panel
		JPanel HisPane = new JPanel(null);
		JLabel instL = new JLabel();
		instL.setText("Pair: ");
		instL.setLocation(6,2);
		instL.setSize(70,20);
		HisPane.add(instL);
		JLabel timeL = new JLabel();
		timeL.setText("Time: ");
		timeL.setLocation(200,2);
		timeL.setSize(70,20);
		HisPane.add(timeL);
		JLabel fromL = new JLabel();
		fromL.setText("From: ");
		fromL.setLocation(400,2);
		fromL.setSize(70,20);
		HisPane.add(fromL);
		JLabel toL = new JLabel();
		toL.setText("To: ");
		toL.setLocation(600,2);
		toL.setSize(70,20);
		HisPane.add(toL);
		instList = new JComboBox<String>();
		instList.setLocation(36, 2);
		instList.setSize(100,20);
		HisPane.add(instList);
		timeList = new JComboBox<String>();
		timeList.setLocation(236,2);
		timeList.setSize(100,20);
		HisPane.add(timeList);
		fromD = new JTextField();
		fromD.setLocation(436, 2);
		fromD.setSize(150,20);
		HisPane.add(fromD);
		toD = new JTextField();
		toD.setLocation(623,2);
		toD.setSize(150,20);
		HisPane.add(toD);
		RNum = new JLabel();
		RNum.setLocation(1180, 2);
		RNum.setSize(400,20);
		HisPane.add(RNum);
		hisBut = new JButton();
		hisBut.setText("Get Prices");
		hisBut.setLocation(800,2);
		hisBut.setSize(150,20);
		HisPane.add(hisBut);
		hisBut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				HisPriceOrderComp[0] = instList.getSelectedItem().toString();
				HisPriceOrderComp[1] = timeList.getSelectedItem().toString();
				HisPriceOrderComp[2] = fromD.getText();
				HisPriceOrderComp[3] = toD.getText();
				HPG = new HisPriceGetter(nat, RNum, Tmodel[4], fromD.getText(), 
						toD.getText(), instList.getSelectedItem().toString(), 
						timeList.getSelectedItem().toString(), hisBut, hisdb);
				new Thread(HPG).start();
			}			
		});
		hisdb = new JButton();
		hisdb.setText("CSV Write");
		hisdb.setLocation(1000, 2);
		hisdb.setSize(150,20);
		HisPane.add(hisdb);
		hisdb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				HistoricPriceWriter HPW = new HistoricPriceWriter(
						guis.dataFolder + "his/", HisPriceOrderComp, Tmodel[4], 
						RNum, hisBut, hisdb);
				new Thread(HPW).start();
			}
		});
		String[] Hiscolumn = {"#", "Date/Time","BidOpen", "BidHigh", "BidLow", "BidClose", "AskOpen", "AskHigh", "AskLow", "AskClose"};
		Tmodel[4] = new MTM(Hiscolumn);
		JTable HisPrices = new JTable(Tmodel[4]);
		JScrollPane HisPanel = new JScrollPane(HisPrices);
		HisPrices.setFillsViewportHeight(true);
		HisPanel.setLocation(5 ,25);
		HisPanel.setSize(width - 20, lheight - 50);
		HisPane.add(HisPanel);
		
		//Weekly Historic Price Getter Panel
		JPanel HPPane = new JPanel(null);
		WHPG = new WHPGetter(HPPane,guis);
		
		//Log Panel
		JPanel LogPane = new JPanel(null);
		LogLab = new JLabel();
		LogLab.setText("<html>");
		LogLab.setOpaque(true);
		LogLab.setBackground(Color.BLACK);
		LogLab.setForeground(Color.YELLOW);
		LogLab.setFont(LogLab.getFont().deriveFont(13.0f));
		JScrollPane LogSPanel = new JScrollPane(LogLab);
		JScrollBar vertical = LogSPanel.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
		LogSPanel.setLocation(5, 5);
		LogSPanel.setSize(width - 20, lheight - 35);
		LogPane.add(LogSPanel);
				
		tabbedPane.add("Accounts", accountPane);
		tabbedPane.add("Open Trades", tradePane);
		tabbedPane.add("Orders",orderPane);
		tabbedPane.add("Closed Trades",ctradePane);
		tabbedPane.add("Historic Prices", HisPane);
		tabbedPane.addTab("Weekly HP", HPPane);
		tabbedPane.add("Log/Status", LogPane);
		tabbedPane.setSize(width - 10,lheight);
		tabbedPane.setLocation(7,height - lower + 30);
		mainPanel.add(tabbedPane);
		
		statusLabel = new JLabel();
		statusLabel.setLocation(10,height - 45);
		statusLabel.setSize(500,20);
		statusLabel.setText("FFFF");
		mainPanel.add(statusLabel);
		
		orderbut = new JButton();
		orderbut.setText("Run Order");
		orderbut.setLocation(7, 5);
		orderbut.setSize(150,20);
		mainPanel.add(orderbut);
		
		remBut = new JButton();
		remBut.setText("Remove Order");
		remBut.setLocation(167, 5);
		remBut.setSize(150,20);
		mainPanel.add(remBut);
		
		settingBut = new JButton();
		settingBut.setText("Settings");
		settingBut.setLocation(327, 5);
		settingBut.setSize(150,20);
		settingBut.addActionListener(new ActLis(guis.settingsAd, this));
		mainPanel.add(settingBut);
		
		JLabel PrAncL = new JLabel();
		PrAncL.setText("Session Profit: ");
		PrAncL.setLocation(width - 440, 5);
		PrAncL.setSize(100,20);
		PrAncL.setFont(new Font("Courier New", Font.PLAIN, 11));
		mainPanel.add(PrAncL);
		SesPr = new JLabel();
		SesPr.setText("Waiting.");
		SesPr.setLocation(width - 355,5);
		SesPr.setSize(70,20);
		SesPr.setForeground(Color.blue);
		mainPanel.add(SesPr);
		
		JLabel MEAncL = new JLabel();
		MEAncL.setText("Max Euity: ");
		MEAncL.setLocation(width - 265, 5);
		MEAncL.setSize(70,20);
		MEAncL.setFont(new Font("Courier New", Font.PLAIN, 11));
		mainPanel.add(MEAncL);
		MaxEL = new JLabel();
		MaxEL.setText("Waiting.");
		MaxEL.setLocation(width - 205,5);
		MaxEL.setSize(70,20);
		MaxEL.setForeground(Color.blue);
		mainPanel.add(MaxEL);
		
		JLabel AcAncL = new JLabel();
		AcAncL.setText("Account: ");
		AcAncL.setLocation(width - 125,5);
		AcAncL.setSize(60,20);
		AcAncL.setFont(new Font("Courier New", Font.PLAIN, 11));
		mainPanel.add(AcAncL);
		accountL = new JLabel();
		accountL.setText("Waiting.");
		accountL.setLocation(width - 75,5);
		accountL.setSize(90,20);
		accountL.setForeground(Color.blue);
		accountL.setText(guis.accountNum);
		mainPanel.add(accountL);
		
	}
		
	public void start(ReadListener re) throws InterruptedException{
		rl = re;
		//nat = new aTrader("D172494811001","1230", "http://www.fxcorporate.com/Hosts.jsp", "Demo", re);
		nat = new aTrader(guis.userName, guis.passWrd, guis.serverAd, guis.acType, re);
		this.setVisible(true);
		nat.connect();
		Thread.sleep(1000);
		System.out.println(rl.toString());
		instList.setModel(new JComboBox<String>(getInsts()).getModel());
		timeList.setModel(new JComboBox<String>(nat.TimeFrames).getModel());
		//BT.BTinstList.setModel(new JComboBox<String>(getInsts()).getModel());
		//BT.BTtimeList.setModel(new JComboBox<String>(nat.TimeFrames).getModel());
		//BT.setNat(nat);
		WHPG.BTinstList.setModel(new JComboBox<String>(getInsts()).getModel());
		WHPG.BTtimeList.setModel(new JComboBox<String>(nat.TimeFrames).getModel());
		WHPG.setNat(nat);
		ActLis oLis = new ActLis(Tmodel[0].getColumn(0),getOffers(), nat);
		orderbut.addActionListener(oLis);
		//ActLis setLis = new ActLis(SettingsAD, this);
		//settingBut.addActionListener(setLis);
		for (int i = 0; i < FXGroup.size(); i++){
			FXGroup.get(i).timeF.setModel(new JComboBox<String>(nat.TimeFrames).getModel());
		}

	}

	
	public void add(ReadEvent sre){
		if (sre.getET() == EventTypes.FXOFFER){
			FXOffer clique = sre.getFXOffer();
			InstPanel PG = new InstPanel(clique, guis);
			FXGroup.add(PG);
			forexPanel.add(PG.getPricePanel());
			forexPanel.setPreferredSize(new Dimension(1 + clique.getIndex() * 250 + 250, 510));
			forexPanel.revalidate();
			forexPanel.repaint();
		}else if (sre.getET() == EventTypes.ACCOUNT)
			Tmodel[0].addNewRow(sre.getFXAccount().getAccountRow());
		else if (sre.getET() == EventTypes.ORDER){
			Tmodel[1].addNewRow(sre.getFXOrder().getOrderRow());
			remBut.removeActionListener(rLis);
			rLis = null;
			rLis = new ActLis(Tmodel[0].getColumn(0), Tmodel[1].getColumn(0), nat);
			remBut.addActionListener(rLis);
		}else if (sre.getET() == EventTypes.OPENPOSITION){
			Tmodel[2].addNewRow(sre.getFXTrade().getTradeRow());
			MaxEquityCal();
		}else if (sre.getET() == EventTypes.CLOSEDPOSITION){
			Tmodel[3].addNewRow(sre.getFXCTrade().getClosedTRow());
			ProfitCal();
		}else if (sre.getET() == EventTypes.STATUS){
			statusLabel.setText(sre.getStatus());
			LogLab.setText(LogLab.getText() + "<br>STATUS: " + sre.getStatus());
		}else if (sre.getET() == EventTypes.LOG)
			LogLab.setText(LogLab.getText() + "<br>LOG: " + sre.getLog());
	}
	
	public void change(ReadEvent sre){
		if (sre.getET() == EventTypes.FXOFFER){
			FXGroup.get(indexFinder(sre.getET(),sre.getFXOffer().getofferID())).change(sre.getFXOffer());
		}else if (sre.getET() == EventTypes.ACCOUNT)
			Tmodel[0].UpdateRow(indexFinder(sre.getET(), sre.getFXAccount().getAccountName()),sre.getFXAccount().getAccountRow());
		else if (sre.getET() == EventTypes.ORDER)
			Tmodel[1].UpdateRow(indexFinder(sre.getET(),sre.getFXOrder().getOrderID()),sre.getFXOrder().getOrderRow());
		else if (sre.getET() == EventTypes.OPENPOSITION)
			Tmodel[2].UpdateRow(indexFinder(sre.getET(),sre.getFXTrade().getTradeID()),sre.getFXTrade().getTradeRow());
		else if (sre.getET() == EventTypes.CLOSEDPOSITION){
			Tmodel[3].UpdateRow(indexFinder(sre.getET(),sre.getFXCTrade().getClosedID()),sre.getFXCTrade().getClosedTRow());
			//ProfitCal();
		}else if (sre.getET() == EventTypes.STATUS){
			LogLab.setText(LogLab.getText() + "<br>Status: " + sre.getStatus());
		}
	}
	
	public void remove(ReadEvent sre){
		if (sre.getET() == EventTypes.FXOFFER){
			//Offers.add(sre.getFXOffer());
		}else if (sre.getET() == EventTypes.ACCOUNT)
			Tmodel[0].RemoveRow(indexFinder(sre.getET(), sre.getFXAccount().getAccountName()));
		else if (sre.getET() == EventTypes.ORDER){
			Tmodel[1].RemoveRow(indexFinder(sre.getET(),sre.getFXOrder().getOrderID()));
			remBut.removeActionListener(rLis);
			rLis = null;
			rLis = new ActLis(Tmodel[0].getColumn(0), Tmodel[1].getColumn(0), nat);
			remBut.addActionListener(rLis);
		}else if (sre.getET() == EventTypes.OPENPOSITION){
			Tmodel[2].RemoveRow(indexFinder(sre.getET(),sre.getFXTrade().getTradeID()));
			MaxEquityCal();
		}else if (sre.getET() == EventTypes.CLOSEDPOSITION)
			Tmodel[3].RemoveRow(indexFinder(sre.getET(),sre.getFXCTrade().getClosedID()));
	}
	
	public void MaxEquityCal(){
		double maxEcal = 0;//MaxE;
		for(int i = 0; i < Tmodel[2].getRowCount(); i++)
			maxEcal = maxEcal - Double.parseDouble(Tmodel[2].getData()[i][10]);
		/*try {
			maxEquity.take();
			maxEquity.put(maxEcal);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		MaxEL.setText(maxEquity.peek().toString());*/
	}
	
	public void ProfitCal(){
		double NP = 0;
		for (int i= 0; i < Tmodel[3].getRowCount(); i++)
			NP += Double.parseDouble(Tmodel[3].getData()[i][8]);
		SesPr.setText(Pures.doubleFPoint(NP));
		if (NP > 0)
			SesPr.setForeground(Color.GREEN);
		else if (NP < 0)
			SesPr.setForeground(Color.RED);
		else
			SesPr.setForeground(Color.BLACK);
		//SesPr.setText("INProfictCal");*/
		//System.out.println("in CT3");
	}
	
	private int indexFinder(EventTypes et, String word){
		int index = 0;
		if (et == EventTypes.FXOFFER){
			index = -1;
			for (int i = 0; i < FXGroup.size(); i++)
				if (FXGroup.get(i).getFXOffer().getofferID().equals(word)){
					index = i;
					break;
				}
		}else if (et == EventTypes.ACCOUNT){
			int columnN = Tmodel[0].searchColumns("Name");
			index = Tmodel[0].searchTable(columnN, word);
		}else if (et == EventTypes.ORDER){
			int columnN = Tmodel[1].searchColumns("Order ID");
			index = Tmodel[1].searchTable(columnN, word);
		}else if (et == EventTypes.OPENPOSITION){
			int columnN = Tmodel[2].searchColumns("Trade ID");
			index = Tmodel[2].searchTable(columnN, word);
		}else if (et == EventTypes.CLOSEDPOSITION){
			int columnN = Tmodel[3].searchColumns("Trade ID");
			index = Tmodel[3].searchTable(columnN, word);
		}
		
		return index;
	}
	
	private FXOffer[] getOffers(){
		FXOffer[] insts = new FXOffer[FXGroup.size()];
		for(int i = 0; i < insts.length; i++)
			insts[i] = FXGroup.get(i).getFXOffer();
		return insts;
	}
	
	private String[] getInsts(){
		String[] insts = new String[FXGroup.size()];
		for (int i = 0; i < insts.length; i++)
			insts[i] = FXGroup.get(i).getFXOffer().getInstrument();
		return insts;
	}
	
}

class WindowAd extends WindowAdapter {

	GUIIF window = null;
	 
    WindowAd(GUIIF window) {
    	this.window = window;
    }
    
	public void windowClosing(WindowEvent e) {
		boolean StopF = true;
		for (int i = 0; i < window.FXGroup.size(); i++)
			if (window.FXGroup.get(i).SSBut.getText().equals("Stop"))
				StopF = false;
		if (StopF){
			try {
				window.statusLabel.setText("Please wait. Termintation is Started ...");
				System.out.println("Termination is Started ...");
				//for (int i = 0; i < window.workerList.size(); i++){
				//	window.workerList.get(i).nat.disconnect();
				//	System.out.println("Worker " + (i + 1) + " is disconnected!");
				//}
				window.nat.disconnect();
				System.out.println("Termination is Done!!");
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.exit(0);
		}else
			new WindowColseErrorDialog().setVisible(true);
	}
}

class ActLis implements ActionListener{

	String[] acIDs = null;
	FXOffer[] offers = null;
	String[] timef = null;
	String[] oIDs = null;
	String SAd = "";
	int type = 0;
	aTrader actnat = null;
	GUIIF IF = null;
	
	public ActLis(FXOffer[] ins, String[] tf){
		offers = ins;
		timef = tf;
		type = 1;
	}
	
	public ActLis(String[] aid, FXOffer[] ins, aTrader an){
		offers = ins;
		acIDs = aid;
		type = 2;
		actnat = an;
	}
	
	public ActLis(String[] aid, String[] OID, aTrader an){
		type = 3;
		acIDs = aid;
		oIDs = OID;
		actnat = an;
	}
	
	public ActLis(String ad, GUIIF an){
		SAd = ad;
		IF = an;
		type = 4;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (type == 1){
			//HistoricPrices dialog = new HistoricPrices(insts);
			//dialog.setVisible(true);
		}else if (type == 2){
			OrderWindow dialog = new OrderWindow(acIDs, offers, actnat);
			dialog.setVisible(true);
		}else if (type == 3){
			RemoveWindow dialog = new RemoveWindow(acIDs, oIDs, actnat);
			dialog.setVisible(true);
		}else if (type == 4){
			SettingsWindow dialog = new SettingsWindow(SAd, IF);
			dialog.setVisible(true);
		}
	}
	
}
