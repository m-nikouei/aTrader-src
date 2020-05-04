package aTWorker;

import java.util.ArrayList;
//import org.jfree.data.time.TimeSeries;
import stratDataTypes.TradesCollectionData;
import pureData.priceRow;

public interface strategy {
	
	public void DataEntry(ArrayList<priceRow> prl);
	
	public void DataHandler(priceRow pr);
		
	public void SignalSender(priceRow pr);
	
	public void TickHandler(priceRow pr);
	
	public TradesCollectionData getTradesData();
	
	//public ArrayList<TimeSeries> getSeries();
	
	public String getLog();
	
	public int getLen();
	
	public int getHisLen();
	
	public String getTickerLog();

}
