package indicators;

import java.util.ArrayList;

import pureData.priceRow;
import tools.Pures;
import tools.priceFields;

public class EMA {
	
	public double EMA = 0;
	//ArrayList<Double> Data = null;
	double alpha = 0;
	int EMAStep = 0;
	
	private double MAInit(ArrayList<Double> data){
		double sum = 0;
		for (int i = 0; i < data.size(); i++){
			sum += data.get(i);
		}
		return sum / data.size();
	}
	
	public EMA(ArrayList<Double> data){
		EMA = MAInit(data);
		alpha = 2 / ((double) data.size() + 1);
		EMAStep = data.size();
	}
	
	public EMA(ArrayList<priceRow> pr, int step, priceFields pf){
		EMA = MAInit(Pures.getPrices(pr, step, pf));
		alpha = 2 / ((double) step + 1);
		EMAStep = step;
		for(int i = step; i < pr.size(); i++){
			NewMA(Pures.getPrice(pr.get(i),pf));
		}
	}
	
	public double NewMA(double newData){
		EMA = (1 - alpha) * EMA + alpha * newData;
		return EMA;
	}
	
	public int getSteps(){
		return EMAStep;
	}

}
