package aTWorker;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import aTGUI.*;
import fxcmIF.ReadListener;
import fxcmIF.aTrader;
import pureData.HPTable;
import pureData.priceRow;
import tools.CalTools;
import tools.GUISettings;
import tools.TXTWriter;

public class Worker extends Thread {
	
	public GUISettings guis = null;
	public String OID = "";
	public String TF = "";
	public int mTF = 0;
	public int hTF = 0;
	public int amt = 0;
	public String inst = "";
	
	public int threadcount = 0;
	
	//Workers
	WorkerFX wFX = null;
	Trader TR = null;
	
	//GUI parts
	public StratRep SP = null;
	public TradeRep TP = null;

	//loggers
	TXTWriter hisLogger = null;
	TXTWriter stratLogger = null;
	TXTWriter trdLogger = null;
	TXTWriter workLogger = null;
	
	public Worker(GUISettings f, String tf, int a, String oid, StratRep sp, TradeRep tp,strategy st){
		guis = f;
		TF = tf;
		mTF = TFStrToInt(tf);
		amt = a;
		OID = oid;
		inst = aTrader.OfferIDtoInstrument(OID);
		SP = sp;
		TP = tp;
		SP.show();
		wFX = new WorkerFX(this);
		ReadListener re = new ReadListener(wFX);
		wFX.setListener(re);
		TR = new Trader(this,st,st.getLen());
		Calendar now = Calendar.getInstance();
		hisLogger = new TXTWriter(guis.dataFolder + "livep/his-" + 
			OID + "-" + TF + "-" + now.get(Calendar.MONTH) + "_" +  
			now.get(Calendar.DAY_OF_MONTH) + "_" + now.get(Calendar.HOUR_OF_DAY)+ "_" + now.get(Calendar.MINUTE)+ ".csv",false);
		stratLogger = new TXTWriter(guis.dataFolder + "livelog/log-" +
				OID + "-" + TF + "-" + now.get(Calendar.MONTH) + "_" +  
				now.get(Calendar.DAY_OF_MONTH) + "_" + now.get(Calendar.HOUR_OF_DAY)+ "_" + now.get(Calendar.MINUTE)+ ".csv",false);
		trdLogger = new TXTWriter(guis.dataFolder + "livelog/trdlog-" +
				OID + "-" + TF + "-" + now.get(Calendar.MONTH) + "_" +  
				now.get(Calendar.DAY_OF_MONTH) + "_" + now.get(Calendar.HOUR_OF_DAY)+ "_" + now.get(Calendar.MINUTE)+ ".csv",false);
		workLogger = new TXTWriter(guis.dataFolder + "livelog/wrklog-" +
				OID + "-" + TF + "-" + now.get(Calendar.MONTH) + "_" +  
				now.get(Calendar.DAY_OF_MONTH) + "_" + now.get(Calendar.HOUR_OF_DAY)+ "_" + now.get(Calendar.MINUTE)+ ".csv",false);
	}

	public void run(){
		wFX.start();
		
		//Reading His prices.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			if (TR.strat.getHisLen() > 0){
				Calendar to = Calendar.getInstance();
				Calendar from = CalTools.CalJump(to, TF, - TR.strat.getHisLen() - 10);
				if (from.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || 
					from.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
					from.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
					from = CalTools.CalJump(to, "D1", - 7);
				}
				to = CalTools.CalJump(to, "m1", -(to.get(Calendar.MINUTE) % 5 ) - 1);
				ArrayList<priceRow> his = hispricer(from,to);
				TR.strat.DataEntry(his);
				for (int i = 0; i < his.size(); i++){
					String row = his.get(i).rDate.getTime() + "," + his.get(i).rBO + ","+ his.get(i).rBH + ","
							+ his.get(i).rBL + ","+ his.get(i).rBC + ","+ his.get(i).rAO + ","+ his.get(i).rAH + ","
							+ his.get(i).rAL + ","+ his.get(i).rAC + "\r\n"; 
					hisLogger.write(row);
				}
			}
		} catch (ParseException e) {
			System.out.println("Couldn't get from date");
			e.printStackTrace();
		}
		
	}
	
	public ArrayList<priceRow> hispricer(Calendar from, Calendar to){
		HPTable hpt = null;
		try {
			 hpt = wFX.nat.getHistorics(inst, TF, from, to);
			 System.out.println("\n" + "New: " + from.getTime() + " - " + to.getTime());
			 System.out.println(inst + " - " + TF + " - " + hpt.TestR);
			 /*for (int i = 0; i < hpt.rows.size(); i++){
			 	priceRow pr = hpt.rows.get(i);
				System.out.println(pr.rDate.getTime() + "," + pr.rBO + "," + pr.rBH
							+ "," + pr.rBL + "," + pr.rBC + "," + pr.rAO + ","
							+ pr.rAH + "," + pr.rAL + "," + pr.rAC);
				}*/
		} catch (ParseException | InterruptedException e) {
			e.printStackTrace();
		}
		return hpt.rows;
	}
	
	public void Stop(){
		wFX.stop();
		SP.erase();
		TP.erase();
	}
	
	public int TFStrToInt(String tf){
		int itf = 0;
		if (tf.equals("m1"))
			itf = 1;
		else if (tf.equals("m5"))
			itf = 5;
		else if (tf.equals("m15"))
			itf = 15;
		else if (tf.equals("m30"))
			itf = 30;
		else if (tf.equals("H1")){
			itf = 60;
			hTF = 1;
		}
		return itf;
		
	}
	
}
