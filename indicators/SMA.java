package indicators;

import java.util.ArrayList;

import pureData.priceRow;
import tools.Pures;
import tools.priceFields;

public class SMA {
	
	public double SMA = 0;
	int SMAStep = 0;
	
	private double MAInit(ArrayList<Double> data){
		double sum = 0;
		for (int i = 0; i < data.size(); i++){
			sum += data.get(i);
		}
		return sum / data.size();
	}
	
	public SMA(ArrayList<Double> data){
		SMA = MAInit(data);
		SMAStep = data.size();
	}
	
	public SMA(ArrayList<priceRow> pr, int step, priceFields pf){
		SMA = MAInit(Pures.getPrices(pr, step, pf));
		SMAStep = step;
		for(int i = step; i < pr.size(); i++){
			NewMA(Pures.getPrice(pr.get(i),pf));
		}
	}
	
	public double NewMA(double newData){
		double PreSum = SMA * SMAStep;
		SMA = (PreSum - SMA + newData) / SMAStep;
		return SMA;
	}

}
