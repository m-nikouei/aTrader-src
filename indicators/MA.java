package indicators;

import java.util.ArrayList;

import pureData.priceRow;
import tools.Pures;
import tools.priceFields;

public class MA {
	
	public double MA = 0;
	ArrayList<Double> Data = new ArrayList<Double>();
	
	private double MAInit(ArrayList<Double> data){
		double sum = 0;
		for (int i = 0; i < Data.size(); i++){
			sum += Data.get(i);
		}
		return sum / Data.size();
	}
	
	public MA(ArrayList<Double> data){
		Data = data;
		MA = MAInit(Data);
	}
	
	public MA(ArrayList<priceRow> pr, int step, priceFields pf){
		Data = Pures.getPrices(pr, step, pf);
		MA = MAInit(Data);
		for(int i = step; i < pr.size(); i++){
			NewMA(Pures.getPrice(pr.get(i),pf));
		}
	}
	
	
	public double NewMA(double newData){
		double rem = Data.get(0);
		Data.remove(0);
		Data.add(newData);
		MA = MA + (newData - rem) / Data.size();
		return MA;
	}
	
	public int getSteps(){
		return Data.size();
	}
	
	
}
