package strategybuilder;

import java.util.ArrayList;

import aTWorker.strategy;
import indicators.RSI;
import pureData.priceRow;
import stratDataTypes.TradesCollectionData;
import tools.Pures;
import tools.priceFields;

public class RSIValsN implements strategy {
	
	int aRSIStep = 14;
	int bRSIStep = 14;
	RSI aRSI = null;
	RSI bRSI = null;
	
	double range = 0;
	
	public TradesCollectionData TD = null;
	

	String WLog = "";
	
	public RSIValsN(int ast, int bst, double r){
		aRSIStep = ast;
		bRSIStep = bst;
		range = r;
		TD = new TradesCollectionData(0, 0.0001,false);
	}

	@Override
	public void DataEntry(ArrayList<priceRow> prl) {
		aRSI = new RSI(prl, aRSIStep, priceFields.AskClose);
		bRSI = new RSI(prl, bRSIStep, priceFields.BidClose);
	}

	@Override
	public void DataHandler(priceRow pr) {
		aRSI.NewRSI(pr.rAC);
		bRSI.NewRSI(pr.rBC);
		
		double ar = Math.round(aRSI.RSI * 10);
		ar = ar / 10;
		double br = Math.round(bRSI.RSI * 10);
		br = br / 10;
		
		double arr1 = ar - range;
		double arr2 = ar + range;
		double brr1 = br - range;
		double brr2 = br + range;
		
		
		WLog = pr.rDate.getTime() + "," + Pures.doubleFPoint(arr1) + "," +
				Pures.doubleFPoint(arr2) + "," + Pures.doubleFPoint(brr1) + "," +
				Pures.doubleFPoint(brr2);
		WLog += "\n";
		
		
		
	}

	@Override
	public void SignalSender(priceRow pr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TradesCollectionData getTradesData() {
		return TD;
	}

	@Override
	public String getLog() {
		return WLog;
	}

	@Override
	public int getLen() {
		int max = aRSIStep;
		if (max  < bRSIStep)
			max = bRSIStep;
		return max;
	}

	@Override
	public void TickHandler(priceRow pr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getHisLen() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getTickerLog() {
		// TODO Auto-generated method stub
		return "";
	}


}
