package aTWorker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Semaphore;

import pureData.priceRow;
import stratDataTypes.TradeInfo;
import stratDataTypes.TradeType;
import stratDataTypes.TradesCollectionData;
import tools.Pures;

public class Trader extends Thread {
	
	Worker wrkr = null;
	String wrkLog = "";
	
	//strat data
	private boolean injectionF = false;
	public strategy strat = null;
	private ArrayList<priceRow> initPriceList = new ArrayList<priceRow>();
	private int step = 0;
	private int ind = 0;
	private double TotalProfit = 0;
	private double TotalLoss = 0;
	private double NetProfit = 0;
	public ArrayList<TradeInfo> closedTs = new ArrayList<TradeInfo>(); 
	public ArrayList<TradeInfo> openTs = new ArrayList<TradeInfo>();
	
	public priceRow pr = null;
	public dataType dt = null;
	
	static Semaphore sem = new Semaphore(1);
	
	public Trader(Worker w, strategy st, int s){
		wrkr = w;
		strat = st;
		step = strat.getLen();
	}
	
	public void run(){
		try {
			sem.acquire();
		try{
		wrkr.threadcount++;
		//long startTime = System.nanoTime();
		wrkLog = pr.rDate.getTime().toString();
		if (dt.equals(dataType.Candle)){
			if (step > 0){
				if (!injectionF){
					if (initPriceList.size() < step){
						initPriceList.add(pr);
						wrkLog += "," + initPriceList.size() + "," + "NotInjected\n";
					}else{
						injectionF = true;
						strat.DataEntry(initPriceList);
						wrkLog += "," + initPriceList.size() + "," + "injection\n";
					}
				}else{
					strat.DataHandler(pr);
					wrkr.stratLogger.write(strat.getLog());
					if (wrkr.amt > 0)
						opencloseHandler();
					ind++;
					updateSP();
					updateTP();
				}
			}else{
				injectionF = true;
				wrkLog += "," + "No injection needed!";
				strat.DataHandler(pr);
				wrkr.stratLogger.write(strat.getLog());
				if (wrkr.amt > 0)
					opencloseHandler();
				ind++;
				updateSP();
				updateTP();
			}
		}else{
			strat.TickHandler(pr);
			wrkr.stratLogger.write(strat.getTickerLog());
			if (wrkr.amt > 0)
				opencloseHandler();
		}
		wrkr.workLogger.write(wrkLog);
		//long estimatedTime = System.nanoTime() - startTime;
		wrkr.threadcount--;
		} finally {
			sem.release();
		}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void opencloseHandler(){
		TradesCollectionData TD = strat.getTradesData();
		wrkLog += "," + TD.openTL.size() + "," + openTs.size() + "," + wrkr.wFX.Trades.size();
		if (TD.openTL.size() == openTs.size() + 1){
			String Ty = "";
			if (TD.State.equals(TradeType.Long))
				Ty = "B";
			else 
				Ty ="S";
			try {
				wrkr.wFX.nat.ImmediateRun("open", wrkr.guis.accountNum, wrkr.wFX.mOffer,null,Ty,wrkr.amt);
				Thread.sleep(1000);
				wrkLog += "," + "TPassed";
				TradeInfo ti = new TradeInfo();
				ti.entryTime = wrkr.wFX.Trades.get(wrkr.wFX.Trades.size() - 1).getOpenTime();
				ti.entryPrice = wrkr.wFX.Trades.get(wrkr.wFX.Trades.size() - 1).getOpenRate();
				ti.currPrice = wrkr.wFX.Trades.get(wrkr.wFX.Trades.size() - 1).getOpenRate();
				ti.Type = TD.State; 
				openTs.add(ti);
			} catch (Exception e) {
				System.out.println("Couldn't open the position!! It is a carsh.");
				e.printStackTrace();
				wrkLog += "," + "TFailed";
			}
		}else if (TD.openTL.size() == openTs.size()){
			for (int i = 0; i < openTs.size(); i++){
				openTs.get(i).currPrice = wrkr.wFX.Trades.get(i).getClose();
				openTs.get(i).profit = wrkr.wFX.Trades.get(i).getPL();
			}
			wrkLog += "," + "updated";
		}else if (TD.openTL.size() < openTs.size()){
			int num = openTs.size() - TD.openTL.size(); 
			closeAll(num);
			wrkLog += "," + "closed";
		}else{
			wrkr.SP.status.setLabel(0, "Error: not consistent");
		}
		wrkLog += "\n";
		
	}
	
	public void closeAll(int cNum){
		if (cNum > 0){
			String Ty = "";
			TradeInfo ti = openTs.get(0);
			if (ti.Type.equals(TradeType.Long))
				Ty = "S";
			else 
				Ty ="B";
			for (int i = 0; i < cNum; i++){
				closer(Ty);
			}
			/*int acNum = cNum;
			if (openTs.size() == cNum)
				if (wrkr.wFX.Trades.size() > cNum)
					acNum = wrkr.wFX.Trades.size();
			try{
				wrkr.wFX.nat.ImmediateRun("close", wrkr.guis.accountNum,wrkr.wFX.mOffer, null, Ty,acNum * wrkr.amt);
				Thread.sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			for (int i = 0; i < wrkr.wFX.Trades.size(); i++)
				try{
					wrkr.wFX.nat.ImmediateRun("close", wrkr.guis.accountNum,wrkr.wFX.mOffer, wrkr.wFX.Trades.get(i),Ty, wrkr.amt);
					//System.out.print(wrkr.guis.accountNum + " - " + wrkr.wFX.mOffer + " - " + wrkr.wFX.Trades.get(i).getTradeID() + " - " + Ty + " - " + wrkr.amt);
					Thread.sleep(200);
				} catch (Exception e) {
					e.printStackTrace();
				}
			wrkLog += "," + "Clossed";
		}
	}
	
	private void closer(String Ty){
		TradeInfo ti = openTs.get(0);
		ti.currPrice = wrkr.wFX.Trades.get(0).getClose();
		ti.exitPrice = wrkr.wFX.Trades.get(0).getClose();
		ti.exitTime = Calendar.getInstance();
		ti.profit = wrkr.wFX.Trades.get(0).getPL();
		openTs.remove(0);
		closedTs.add(ti);
		if (ti.profit >= 0)
			TotalProfit += ti.profit;
		else
			TotalLoss += ti.profit;
		NetProfit += ti.profit;
		String tlog = ti.entryTime.getTime() + "," +
				ti.exitTime.getTime() + "," + 
				ti.Type.toString() + "," +
				Pures.doubleFPoint(ti.entryPrice) + "," +
				Pures.doubleFPoint(ti.maxP) + "," +
				Pures.doubleFPoint(ti.minP) + "," +
				Pures.doubleFPoint(ti.exitPrice) + "," +
				Pures.doubleFPoint(ti.profit) + ti.tradeTime;
		if (ti.profit >= 0)
			tlog += ",Profit\n";
		else
			tlog += ",Loss\n";
		wrkr.trdLogger.write(tlog);
	}
	
	private void updateSP(){
		TradesCollectionData TD = strat.getTradesData();
		wrkr.SP.counts.labels[0].setText(strat.getTradesData().State.toString());
		//Father.SP.states.labels[1].setText(Father.strat.getTradesData().tStatus.toString());
		wrkr.SP.counts.labels[1].setText(Integer.toString(ind));
		wrkr.SP.counts.labels[2].setText(Integer.toString(wrkr.wFX.minMissed));
		wrkr.SP.tradeN.labels[0].setText(Integer.toString(TD.TradeNum));
		wrkr.SP.tradeN.labels[1].setText(Integer.toString(TD.PTNum));
		wrkr.SP.tradeN.labels[2].setText(Integer.toString(TD.LTNum));
		wrkr.SP.pls.LChangeD(0, TD.NetProfit);
		wrkr.SP.pls.LChangeD(1,TD.TotalProfit);
		wrkr.SP.pls.LChangeD(2, TD.TotalLoss);
		wrkr.SP.Apls.LChangeD(0, NetProfit);
		wrkr.SP.Apls.LChangeD(1,TotalProfit);
		wrkr.SP.Apls.LChangeD(2, TotalLoss);
	}
	
	private void updateTP(){
		TradesCollectionData TD = strat.getTradesData();
		if (TD.State.equals(TradeType.NotIn))
			wrkr.TP.erase();
		else{
			wrkr.TP.startD.labels[0].setText(TD.openTL.get(0).entryTime.getTime().toString());
			wrkr.TP.prices.setLabel(0, TD.State.toString());
			//wrkr.TP.prices.LChangeD(1, TD.openTL.get(0).entryPrice);
			wrkr.TP.prices.setLabel(1, Integer.toString(TD.openTL.size()));
			wrkr.TP.prices.LChangeD(2,TD.openTL.get(0).currPrice);
			/*if (TD.State.equals(Status.inBuy)){
				Father.TP.spreads.LChangeD(0,TD.TradeCurrP - TD.TradeEntryP);	
			}else if (TD.tStatus.equals(Status.inSell)){
				Father.TP.spreads.LChangeD(0, TD.TradeEntryP - TD.TradeCurrP);
			}*/
		}
	}
}
