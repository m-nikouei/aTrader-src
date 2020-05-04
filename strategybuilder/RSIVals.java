package strategybuilder;

import java.util.ArrayList;

import aTWorker.*;
import indicators.RSI;
import pureData.priceRow;
import stratDataTypes.TradesCollectionData;
import tools.Pures;
import tools.priceFields;

public class RSIVals implements strategy {
	
	int aRSIStep = 14;
	int bRSIStep = 14;
	RSI aRSI = null;
	RSI bRSI = null;
	
	public TradesCollectionData TD = null;
	
	String WLog = "";
	
	public RSIVals(int ast, int bst){
		aRSIStep = ast;
		bRSIStep = bst;
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
		
		WLog = pr.rDate.getTime() + "," + Pures.doubleFPoint(aRSI.RSI) + "," +
				Pures.doubleFPoint(bRSI.RSI);
		WLog += "\r\n";
		
		
		
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
