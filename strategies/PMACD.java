package strategies;

import java.util.ArrayList;

import aTWorker.strategy;
import indicators.EMA;
import pureData.priceRow;
import stratDataTypes.MATrend;
import stratDataTypes.Signal;
import stratDataTypes.TradeType;
import stratDataTypes.TradesCollectionData;
import tools.Pures;
import tools.priceFields;

enum stopJump{
	nojump,
	jump20,
	jump30
}

public class PMACD implements strategy {
	
	int FN = 50;
	int SN = 200;
	EMA HFE = null;
	EMA HSE = null;
	EMA LFE = null;
	EMA LSE = null;
	double xHigh = 0;
	double xLow = 0;
	int sleepT = 2;
	int slept = 0;
	
	double stopVal = 0;
	double limitVal = 0;
	
	boolean startF = false;
	MATrend Flag = MATrend.SiG;
	
	stopJump SJF = stopJump.nojump;
	
	public TradesCollectionData TD = null;
	String WLog = "";
	
	public PMACD(int fn, int sn,double sv, double lv) {
		FN = fn;
		SN = sn;
		stopVal = sv;
		limitVal = lv;
		TD = new TradesCollectionData(stopVal, limitVal,false);
	}

	@Override
	public void DataEntry(ArrayList<priceRow> prl) {
		HFE =  new EMA(prl,FN,priceFields.BidHigh);
		HSE =  new EMA(prl,SN,priceFields.BidHigh);
		LFE =  new EMA(prl,FN,priceFields.BidLow);
		LSE =  new EMA(prl,SN,priceFields.BidLow);
	}

	@Override
	public void DataHandler(priceRow pr) {
		 HFE.NewMA(pr.rBH);
		 HSE.NewMA(pr.rBH);
		 LFE.NewMA(pr.rBL);
		 LSE.NewMA(pr.rBL);
		xHigh = (2 * (FN * HSE.EMA - SN * HFE.EMA) + (SN + FN + SN * FN - 1) * (HFE.EMA - HSE.EMA)) / (2 * (FN - SN));
		xLow = (2 * (FN * LSE.EMA - SN * LFE.EMA) + (SN + FN + SN * FN - 1) * (LFE.EMA - LSE.EMA)) / (2 * (FN - SN));
		 if (TD.State.equals(TradeType.NotIn)) {
				if (slept < sleepT) {
					slept++;
				}
		}
		startF = true;
	}
	
	@Override
	public void TickHandler(priceRow pr) {
		if (startF) {
			/*if (state.equals(TradeType.NotIn)) {
				if (slept >= sleepT) {
					SignalSender(pr);
				}else {
					WLog = pr.rDate.getTime() + "," + "sleep\n";
				}
			}else {
				if (slept > 0)
					slept = 0;
				SignalSender(pr);
			}
			 */
			for (int i = 0;  i < TD.openTL.size(); i++) {
				double pDiff = Math.abs(pr.rBC - TD.openTL.get(i).entryPrice);
				if (pDiff >= 0.0020 && pDiff < 0.0030 && SJF.equals(stopJump.nojump)) {
					if (TD.openTL.get(i).Type.equals(TradeType.Long)) {
						TD.openTL.get(i).solidStop = TD.openTL.get(i).entryPrice + 0.0008;
					}else if(TD.openTL.get(i).Type.equals(TradeType.Short)) {
						TD.openTL.get(i).solidStop = TD.openTL.get(i).entryPrice - 0.0008;
					}
					SJF = stopJump.jump20;
					System.out.println("Stop Jumped to +8!!");
				}else if (pDiff >= 0.0030 && SJF.equals(stopJump.jump20)) {
					if (TD.openTL.get(i).Type.equals(TradeType.Long)) {
						TD.openTL.get(i).solidStop = TD.openTL.get(i).entryPrice + 0.0020;
					}else if(TD.openTL.get(i).Type.equals(TradeType.Short)) {
						TD.openTL.get(i).solidStop = TD.openTL.get(i).entryPrice - 0.0020;
					}
					SJF = stopJump.jump30;
					System.out.println("Stop Jumped to +20!!");
				}
				/*
				System.out.println(i + " " + Pures.doubleFPoint(TD.openTL.get(i).entryPrice) +
					 	" " + Pures.doubleFPoint(TD.openTL.get(i).solidStop) + 
					  	" " + Pures.doubleFPoint(TD.openTL.get(i).solidLimit));
				 */
			}
			SignalSender(pr);
		}
	}

	@Override
	public void SignalSender(priceRow pr) {
		if (Flag.equals(MATrend.SiG)){
			if (HFE.EMA - HSE.EMA < 0 && pr.rBC >= xHigh)
				Flag = MATrend.UpT;
			else if (LFE.EMA - LSE.EMA > 0 && pr.rBC <= xLow)
				Flag = MATrend.DnT;
		}else if (Flag.equals(MATrend.DnT)){
			Flag = MATrend.SiG;
		}else if (Flag.equals(MATrend.UpT)){
			Flag = MATrend.SiG;
		}
		
		Signal sig = Signal.Wait;
		if (TD.State.equals(TradeType.NotIn)) {
			if (Flag.equals(MATrend.UpT)) {
				sig = Signal.Buy;
				TD.State = TradeType.Long;
				SJF = stopJump.nojump;
			}else if (Flag.equals(MATrend.DnT)) {
				sig = Signal.Sell;
				TD.State = TradeType.Short;
				SJF = stopJump.nojump;
			}
		}else if (TD.State.equals(TradeType.Long)) {
			if (Flag.equals(MATrend.DnT)) {
				sig = Signal.Sell;
				TD.State = TradeType.NotIn;
			}
		}else if (TD.State.equals(TradeType.Short)) {
			if (Flag.equals(MATrend.UpT)) {
				sig = Signal.Buy;
				TD.State = TradeType.NotIn;
			}
		}
			
		WLog = pr.rDate.getTime() + "," + Pures.doubleFPoint(pr.rBC) 
		   + "," + Pures.doubleFPoint(HFE.EMA - HSE.EMA) 
		   + "," + Pures.doubleFPoint(xHigh)   
		   + "," + Pures.doubleFPoint(LFE.EMA - LSE.EMA)
		   + "," + Pures.doubleFPoint(xLow) + "," + Flag.toString() 
		   + "," + sig.toString() + 	"," + TD.State.toString();
		WLog += TD.UpdateTrade(sig, pr) + "\n";
	}

	@Override
	public TradesCollectionData getTradesData() {
		return TD;
	}


	@Override
	public String getLog() {
		return "";
	}

	@Override
	public int getLen() {
		return 0;
	}


	@Override
	public int getHisLen() {
		// TODO Auto-generated method stub
		return SN;
	}

	@Override
	public String getTickerLog() {
		// TODO Auto-generated method stub
		return WLog;
	}

}
