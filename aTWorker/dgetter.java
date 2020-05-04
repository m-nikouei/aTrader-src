package aTWorker;

import java.util.ArrayList;

import pureData.priceRow;
import stratDataTypes.TradesCollectionData;

public class dgetter implements strategy {
	
	TradesCollectionData TD = null;
	
	public dgetter(){
		TD = new TradesCollectionData(0,0,false);
	}

	@Override
	public void DataEntry(ArrayList<priceRow> prl) {
			
	}

	@Override
	public void DataHandler(priceRow pr) {
			
	}

	@Override
	public void SignalSender(priceRow pr) {
				
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
	public void TickHandler(priceRow pr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getHisLen() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public String getTickerLog() {
		// TODO Auto-generated method stub
		return "";
	}

}
