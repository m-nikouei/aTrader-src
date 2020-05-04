package indicators;

import java.util.ArrayList;

import pureData.priceRow;
import tools.Pures;
import tools.priceFields;

public class NMA {
	
	ArrayList<Double> data = new ArrayList<Double>();
	int MAStep = 0;
	double MA = 0;
	
	private double MACal(){
		double ma = 0;
		double sum = 0;
		for (int i = 0; i < data.size(); i++)
			sum += data.get(i);
		ma = sum / MAStep;
		return ma;
	}
	
	public NMA(ArrayList<Double> d){
		for (int i = 0; i < d.size(); i++){
			data.add(d.get(i));
		}
		MAStep = d.size();
		MA = MACal();
	}
	
	public NMA(ArrayList<priceRow> pr, int step, priceFields pf){
		MAStep = step;
		ArrayList<Double> d = Pures.getPrices(pr, step, pf);
		for (int i = 0; i < d.size(); i++){
			data.add(d.get(i));
		}
		MA = MACal();
		for(int i = step; i < pr.size(); i++){
			NewMA(Pures.getPrice(pr.get(i),pf));
		}
	}
	
	public double NewMA(double newData){
		data.remove(0);
		data.add(newData);
		MA = MACal();
		return MA;
	}

}
