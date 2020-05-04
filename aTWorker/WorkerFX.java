package aTWorker;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import fxcmIF.EventTypes;
import fxcmIF.ReadEvent;
import fxcmIF.ReadListener;
import fxcmIF.aTrader;
import pureData.FXClosedTrade;
import pureData.FXOffer;
import pureData.FXOrder;
import pureData.FXTrade;
import pureData.priceRow;
import stratDataTypes.Signal;
import stratDataTypes.TradeType;
import stratDataTypes.TradesCollectionData;
import tools.Pures;

public class WorkerFX {
	
	public aTrader nat = null;
	
	//data
	Worker wrkr = null;
	public FXOffer mOffer = null;
	ArrayList<FXOrder> Orders = new ArrayList<FXOrder>();
	ArrayList<FXTrade> Trades = new ArrayList<FXTrade>();
	ArrayList<FXClosedTrade> CTrades = new ArrayList<FXClosedTrade>();
	public int tickNum = 0;
	public int maxS = 0;
	private ReadListener rl = null;
	Timer SpeedMeter = null;
	
	ArrayList<FXOffer> evenOffers = new ArrayList<FXOffer>();
	ArrayList<FXOffer> oddOffers = new ArrayList<FXOffer>();
	int minMissed = 0;
	int minute = -1;
	
	
	//prices
	//priceRow pr = null;
	
	public WorkerFX(Worker f){
		wrkr = f;
	}
	
	public void setListener(ReadListener re){
		rl = re;
	}
	
	public void start() {
		try {
			nat = new aTrader(wrkr.guis.userName, wrkr.guis.passWrd, wrkr.guis.serverAd, wrkr.guis.acType, rl);
			nat.setInst(wrkr.OID);
			nat.connect();
			SpeedMeter = new Timer();
			SpeedMeter.schedule(new TimerTask(){
				public void run() {
					wrkr.SP.speed.setLabel(0, "Max: " + Integer.toString(maxS)) ;
					wrkr.SP.speed.setLabel(2, "Speed: " + Integer.toString(tickNum));
					tickNum  = 0;
				}
			}, 0, 1000);
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("Error: Worker couldn't start!");
			e.printStackTrace();
		}
	}
	
	public boolean stop(){
		boolean flag = false;
		try {
			String upLog = "";
			TradesCollectionData TD = wrkr.TR.strat.getTradesData();
			int sz = TD.openTL.size();
			if (sz > 0){
				
				Signal sig = null;
				if (wrkr.TR.openTs.get(0).Type.equals(TradeType.Long))
					sig = Signal.Sell;
				else 
					sig = Signal.Buy;
				for(int i = 0; i < sz; i++){
					upLog += TD.UpdateTrade(sig, wrkr.TR.pr);
				}
			}
			//wrkr.TR.opencloseHandler();
			wrkr.stratLogger.write(upLog);
			/*int num = wrkr.TR.openTs.size();
			if (num > 0){
				wrkr.TR.closeAll(num);
			}*/
			nat.disconnect();
			SpeedMeter.cancel();
			flag = true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	private int indexFinder(EventTypes et, String word){
		int index = 0;
		if (et == EventTypes.ORDER){
			for (int i = 0; i < Orders.size(); i++)
				if (Orders.get(i).getOrderID().equals(word)){
					index = i;
					break;
				}
		}else if (et == EventTypes.OPENPOSITION){
			for (int i = 0; i < Trades.size(); i++)
				if (Trades.get(i).getTradeID().equals(word)){
					index = i;
					break;
				}
		}else if (et == EventTypes.CLOSEDPOSITION){
			for (int i = 0; i < CTrades.size(); i++)
				if (CTrades.get(i).getClosedID().equals(word)){
					index = i;
					break;
				}
		}
		return index;
	}
	
	public void add(ReadEvent sre){
		if (sre.getET() == EventTypes.ORDER)
			Orders.add(sre.getFXOrder());
		else if (sre.getET() == EventTypes.OPENPOSITION)
			Trades.add(sre.getFXTrade());
		else if (sre.getET() == EventTypes.CLOSEDPOSITION){
			String[] CT = {sre.getFXCTrade().getClosedTime().getTime().toString(), sre.getFXCTrade().getBuySell(), Pures.doubleFPoint(sre.getFXCTrade().getGrossPL())};
			wrkr.SP.TradesT.addNewRow(CT);
			CTrades.add(sre.getFXCTrade());
		}else if (sre.getET() == EventTypes.STATUS){
			String status = sre.getStatus();
			wrkr.SP.status.setLabel(0, status);
		}else if (sre.getET() == EventTypes.LOG){
			wrkr.SP.status.setLabel(0, "LOG: " + sre.getLog());
		}
	}
	
	public void change(ReadEvent sre) throws NullPointerException{
		if (sre.getET() == EventTypes.FXOFFER){
			
			tickNum++;
			if (maxS < tickNum)
				maxS = tickNum;
						
			mOffer = sre.getFXOffer();
		
			if (wrkr.TF.equals("t1")){
				double[] nps = {0,0,0,mOffer.getBid(),0,0,0,mOffer.getAsk()};
				priceRow npr = new priceRow(mOffer.getTimeStamp(), nps);
				wrkr.TR.pr = npr;
				new Thread(wrkr.TR).start();
			}else if (wrkr.TF.equals("m1")){
				MinuteChange();
			}else{
				if (wrkr.mTF < 60)
					MinuteChange();
				else
					HourChange();
				
			}
			//System.out.println(mOffer.getTimeStamp().get(Calendar.SECOND) + " - " + 
			//Pures.doubleFPoint(mOffer.getBid())+ " - " + Pures.doubleFPoint(mOffer.getAsk()));
		}else if (sre.getET() == EventTypes.ORDER){
			int index = indexFinder(sre.getET(), sre.getFXOrder().getOrderID());
			Orders.set(index, sre.getFXOrder());
		}else if (sre.getET() == EventTypes.OPENPOSITION){
			int index = indexFinder(sre.getET(), sre.getFXTrade().getTradeID());
			Trades.set(index, sre.getFXTrade());
		}else if (sre.getET() == EventTypes.CLOSEDPOSITION){
			int index = indexFinder(sre.getET(), sre.getFXCTrade().getClosedID());
			CTrades.set(index, sre.getFXCTrade());
		}else if (sre.getET() == EventTypes.STATUS){
			wrkr.SP.status.setLabel(0, "Status: " + sre.getStatus());
			System.out.println("Demon Status: " + sre.getStatus());
			String sts = sre.getStatus();
			if (sts.equals("lost") || sts.equals("unk")){
				System.out.println("Error: lost demon @ " + Calendar.getInstance().getTime() + "!");
				nat = new aTrader(wrkr.guis.userName, wrkr.guis.passWrd, wrkr.guis.serverAd, wrkr.guis.acType, rl);
				nat.setInst(wrkr.OID);
				try {
					Thread.sleep(1000);
					nat.connect();
				} catch (InterruptedException e) {
					System.out.println("Error: Reconnection failed!");
					e.printStackTrace();
				}
			}
		}
	}
	
	public void remove(ReadEvent sre) throws NullPointerException{
		if (sre.getET() == EventTypes.ORDER){
			int index = indexFinder(sre.getET(), sre.getFXOrder().getOrderID());
			Orders.remove(index);
		}else if (sre.getET() == EventTypes.OPENPOSITION){
			int index = indexFinder(sre.getET(), sre.getFXTrade().getTradeID());
			Trades.remove(index);
		}else if (sre.getET() == EventTypes.CLOSEDPOSITION){
			int index = indexFinder(sre.getET(), sre.getFXCTrade().getClosedID());
			CTrades.remove(index);
		}
	}
	
	private void MinuteChange(){
		if ((mOffer.getTimeStamp().get(Calendar.MINUTE) / wrkr.mTF) % 2 == 0){
			evenOffers.add(mOffer);
			wrkr.SP.states.labels[2].setText("Ticks: " + Integer.toString(evenOffers.size()));
		}else{
			oddOffers.add(mOffer);
			wrkr.SP.states.labels[2].setText("Ticks: " + Integer.toString(oddOffers.size()));
		}
		
		Calendar ts = mOffer.getTimeStamp();
		
		//initial minute is assigned.
		if (minute == -1){
			minute = ts.get(Calendar.MINUTE);
		}
		
		if (ts.get(Calendar.MINUTE) % wrkr.mTF == 0 && minute != ts.get(Calendar.MINUTE)){
			if (ts.get(Calendar.MINUTE) > minute + wrkr.mTF){
				minMissed += ts.get(Calendar.MINUTE) - minute - 1;
				wrkr.SP.status.setLabel(0, "Err: Missed mins @ " + ts.getTime());
				System.out.println("Err: Missed mins @ " + ts.getTime());
			}
			int tMin = minute;
			minute = ts.get(Calendar.MINUTE);
			priceRow npr = null;
			if ((tMin /wrkr.mTF)  % 2 == 1){
				npr = CandleMaker(oddOffers);
				//evenOffers.add(0,oddOffers.get(oddOffers.size() - 1));
				oddOffers.clear();
			}else{
				npr = CandleMaker(evenOffers);
				//oddOffers.add(0,evenOffers.get(evenOffers.size() - 1));
				evenOffers.clear();
			}
			wrkr.hisLogger.write(npr.rDate.getTime() + "," + npr.rBO + "," + npr.rBH
				+ "," + npr.rBL + "," + npr.rBC + "," + npr.rAO + ","
				+ npr.rAH + "," + npr.rAL + "," + npr.rAC + "\n");
			wrkr.TR.pr = npr;
			wrkr.TR.dt = dataType.Candle;
			new Thread(wrkr.TR).start();
			wrkr.SP.states.labels[0].setText("MIH: " + Integer.toString(minute));
		}else{
			//if (wrkr.threadcount == 0){
				double[] nps = {0,0,0,mOffer.getBid(),0,0,0,mOffer.getAsk()};
				priceRow npr = new priceRow(mOffer.getTimeStamp(), nps);
				wrkr.TR.pr = npr;
				wrkr.TR.dt = dataType.Tick;
				new Thread(wrkr.TR).start();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			/*}else{
				System.out.println("More than one Thread. We need to wait for half a second!!");
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				wrkr.threadcount = 0;
			}*/
		}
	}
	
	private void HourChange(){
		if ((mOffer.getTimeStamp().get(Calendar.HOUR) / wrkr.hTF) % 2 == 0){
			evenOffers.add(mOffer);
			wrkr.SP.states.labels[2].setText("Ticks: " + Integer.toString(evenOffers.size()));
		}else{
			oddOffers.add(mOffer);
			wrkr.SP.states.labels[2].setText("Ticks: " + Integer.toString(oddOffers.size()));
		}
		
		Calendar ts = mOffer.getTimeStamp();
		
		//initial minute is assigned.
		if (minute == -1){
			minute = ts.get(Calendar.HOUR);
		}
		
		if (ts.get(Calendar.HOUR) % wrkr.hTF == 0 && minute != ts.get(Calendar.HOUR)){
			if (ts.get(Calendar.HOUR) > minute + wrkr.hTF){
				minMissed += ts.get(Calendar.HOUR) - minute - 1;
				wrkr.SP.status.setLabel(0, "Err: Missed hours @ " + ts.getTime());
				System.out.println("Err: Missed hours @ " + ts.getTime());
			}
			int tMin = minute;
			minute = ts.get(Calendar.HOUR);
			priceRow npr = null;
			if ((tMin /wrkr.hTF)  % 2 == 1){
				npr = CandleMaker(oddOffers);
				//evenOffers.add(0,oddOffers.get(oddOffers.size() - 1));
				oddOffers.clear();
			}else{
				npr = CandleMaker(evenOffers);
				//oddOffers.add(0,evenOffers.get(evenOffers.size() - 1));
				evenOffers.clear();
			}
			wrkr.hisLogger.write(npr.rDate.getTime() + "," + npr.rBO + "," + npr.rBH
				+ "," + npr.rBL + "," + npr.rBC + "," + npr.rAO + ","
				+ npr.rAH + "," + npr.rAL + "," + npr.rAC + "\n");
			wrkr.TR.pr = npr;
			wrkr.TR.dt = dataType.Candle;
			new Thread(wrkr.TR).start();
			wrkr.SP.states.labels[0].setText("Hour: " + Integer.toString(minute));
			
		}else{
			//if (wrkr.threadcount == 0){
				double[] nps = {0,0,0,mOffer.getBid(),0,0,0,mOffer.getAsk()};
				priceRow npr = new priceRow(mOffer.getTimeStamp(), nps);
				wrkr.TR.pr = npr;
				wrkr.TR.dt = dataType.Tick;
				new Thread(wrkr.TR).start();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			/*}else{
				System.out.println("More than one Thread. We need to wait for half a second!!");
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				wrkr.threadcount = 0;
			}*/
		}
	}
	
	/*private void BigMinChange(){
		if ((mOffer.getTimeStamp().get(Calendar.MINUTE) / wrkr.mTF) % 2 == 0){
			wrkr.SP.states.labels[2].setText("Ticks: " + Integer.toString(evenOffers.size()));
		}else{
			wrkr.SP.states.labels[2].setText("Ticks: " + Integer.toString(oddOffers.size()));
		}
		
		Calendar ts = mOffer.getTimeStamp();
		
		//initial minute is assigned.
		if (minute == -1){
			minute = ts.get(Calendar.MINUTE);
		}
		
		if (ts.get(Calendar.MINUTE) % wrkr.mTF == 0){
			if (minute != ts.get(Calendar.MINUTE)){
				if (ts.get(Calendar.MINUTE) > minute + wrkr.mTF){
					minMissed += ts.get(Calendar.MINUTE) - minute - 1;
					wrkr.SP.status.setLabel(0, "Err: Missed mins @ " + ts.getTime());
					System.out.println("Err: Missed mins @ " + ts.getTime());
				}else{
				minute = ts.get(Calendar.MINUTE);
				priceRow npr = null;
				try {
					Calendar from = tools.CalTools.CalJump(tools.CalTools.CalJump(ts, wrkr.TF, -2),
							"m1",2);
					Calendar to = tools.CalTools.CalJump(ts, "m1", 2);
					ArrayList<priceRow> rows = wrkr.hispricer(from, to); 
					npr = rows.get(rows.size() - 1);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				wrkr.hisLogger.write(npr.rDate.getTime() + "," + npr.rBO + "," + npr.rBH
					+ "," + npr.rBL + "," + npr.rBC + "," + npr.rAO + ","
					+ npr.rAH + "," + npr.rAL + "," + npr.rAC + "\n");
				wrkr.TR.pr = npr;
				new Thread(wrkr.TR).start();
				}
			}
		}
		wrkr.SP.states.labels[0].setText("MIH: " + ts.get(Calendar.MINUTE));
	}
	
	private void HourChange(){
		if ((mOffer.getTimeStamp().get(Calendar.HOUR) / wrkr.mTF) % 2 == 0){
			wrkr.SP.states.labels[2].setText("Ticks: " + Integer.toString(evenOffers.size()));
		}else{
			wrkr.SP.states.labels[2].setText("Ticks: " + Integer.toString(oddOffers.size()));
		}
		
		Calendar ts = mOffer.getTimeStamp();
		
		//initial minute is assigned.
		if (minute == -1){
			minute = ts.get(Calendar.HOUR);
		}
		
		if (ts.get(Calendar.HOUR) % wrkr.mTF == 0){
			if (minute != ts.get(Calendar.HOUR)){
				if (ts.get(Calendar.HOUR) > minute + wrkr.mTF){
					minMissed += ts.get(Calendar.HOUR) - minute - 1;
					wrkr.SP.status.setLabel(0, "Err: Missed mins @ " + ts.getTime());
					System.out.println("Err: Missed mins @ " + ts.getTime());
				}
				minute = ts.get(Calendar.HOUR);
				priceRow npr = null;
				try {
					Calendar from = tools.CalTools.CalJump(ts, wrkr.TF, -1);
					npr = nat.getHistorics(aTrader.OfferIDtoInstrument(wrkr.OID), wrkr.TF,from, ts)
							.rows.get(0);
					Thread.sleep(50);
				} catch (ParseException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				wrkr.hisLogger.write(npr.rDate.getTime() + "," + npr.rBO + "," + npr.rBH
					+ "," + npr.rBL + "," + npr.rBC + "," + npr.rAO + ","
					+ npr.rAH + "," + npr.rAL + "," + npr.rAC + "\n");
				wrkr.TR.pr = npr;
				new Thread(wrkr.TR).start();
		
			}
		}
		wrkr.SP.states.labels[0].setText("MIH: " + ts.get(Calendar.HOUR));
	}*/
	
	
	
	private priceRow CandleMaker(ArrayList<FXOffer> list){
		double[] prs = {list.get(0).getBid(),list.get(0).getBid(),
			list.get(0).getBid(),list.get(0).getBid(),list.get(0).getAsk(),
			list.get(0).getAsk(),list.get(0).getAsk(),list.get(0).getAsk()};
		priceRow npr = new priceRow(list.get(0).getTimeStamp(),prs);
		try {
			npr.rDate = tools.CalTools.CalJump(npr.rDate, "s1", 
				-npr.rDate.get(Calendar.SECOND));
		} catch (ParseException e) {
			System.out.println("Error: Candle's date cannot be generated!");
			e.printStackTrace();
		}
		npr.rBC = list.get(list.size() - 1).getBid();
		npr.rAC = list.get(list.size() - 1).getAsk();
		for (int i = 0; i < list.size(); i++ ){
			if (npr.rBH < list.get(i).getBid())
				npr.rBH = list.get(i).getBid();
			if (npr.rBL > list.get(i).getBid())
				npr.rBL = list.get(i).getBid();
			if (npr.rAH < list.get(i).getAsk())
				npr.rAH = list.get(i).getAsk();
			if (npr.rAL > list.get(i).getAsk())
				npr.rAL = list.get(i).getAsk();
		}
		return npr;
	}

}
