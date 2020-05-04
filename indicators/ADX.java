package indicators;

import java.util.ArrayList;

import pureData.priceRow;
import tools.Pures;
import tools.priceFields;

public class ADX {
	
	public double ADX = 0;
	MA ADXMA = null; 
	double lHigh = 0;
	double lLow = 0;
	SMA DMP = null;
	SMA DMM = null;
	ATR ADXATR = null;
	int ADXStep = 0;
		
	int InitStep = 0;
	ArrayList<Double> DX = new ArrayList<Double>(); 
	
	public ADX(ArrayList<Double> High, ArrayList<Double> Low, ArrayList<Double> Close){
		ADXATR = new ATR(High, Low, Close);
		ArrayList<Double> UM = new ArrayList<Double>();
		ArrayList<Double> DM = new ArrayList<Double>();
		for (int i = 0; i < High.size(); i++){
			double upM = High.get(i) - lHigh;
			double downM = lLow - Low.get(i);
			lHigh = High.get(i);
			lLow = Low.get(i);
			if (upM < 0 && downM < 0){
				UM.add((double) 0);
				DM.add((double) 0);
			}else if (upM >= downM){
				UM.add(upM);
				DM.add((double) 0);
			}else if (downM > upM){
				DM.add(downM);
				UM.add((double) 0);
			}
		}
		DMP = new SMA(UM);
		DMM = new SMA(DM);
		double DIP = DMP.SMA / ADXATR.ATR;
		double DIM = DMM.SMA / ADXATR.ATR;
		ADX = Math.abs(DIP - DIM) / (DIP + DIM);
		DX.add(ADX);
		InitStep++;
		ADXStep = High.size();
	}
	
	public ADX(ArrayList<priceRow> pr,int step, priceFields h, priceFields l, priceFields c){
		ArrayList<Double> High = Pures.getPrices(pr,step,h);
		ArrayList<Double> Low = Pures.getPrices(pr,step,l);
		ArrayList<Double> Close = Pures.getPrices(pr,step,c);
		ADXATR = new ATR(High, Low, Close);
		ArrayList<Double> UM = new ArrayList<Double>();
		ArrayList<Double> DM = new ArrayList<Double>();
		for (int i = 0; i < High.size(); i++){
			double upM = High.get(i) - lHigh;
			double downM = lLow - Low.get(i);
			lHigh = High.get(i);
			lLow = Low.get(i);
			if (upM < 0 && downM < 0){
				UM.add((double) 0);
				DM.add((double) 0);
			}else if (upM >= downM){
				UM.add(upM);
				DM.add((double) 0);
			}else if (downM > upM){
				DM.add(downM);
				UM.add((double) 0);
			}
		}
		DMP = new SMA(UM);
		DMM = new SMA(DM);
		double DIP = DMP.SMA / ADXATR.ATR;
		double DIM = DMM.SMA / ADXATR.ATR;
		ADX = Math.abs(DIP - DIM) / (DIP + DIM);
		DX.add(ADX);
		InitStep++;
		ADXStep = High.size();
		for(int i = step; i < pr.size(); i++){
			NewADX(pr.get(i).rBH, pr.get(i).rBL, pr.get(i).rBC);
		}
		
	}
	
	public double NewADX(double High, double Low, double Close){
		ADXATR.NewATR(High, Low, Close);
		double upM = High - lHigh;
		double downM = lLow - Low;
		double UM = 0;
		double DM = 0;
		lHigh = High;
		lLow = Low;
		if (upM < 0 && downM < 0){
			UM = 0;
			DM = 0;
		}else if (upM >= downM){
			UM = upM;
			DM = 0;
		}else if (downM > upM){
			DM = downM;
			UM = 0;
		}
		DMP.NewMA(UM);
		DMM.NewMA(DM);
		double DIP = DMP.SMA / ADXATR.ATR;
		double DIM = DMM.SMA / ADXATR.ATR;
		ADX = Math.abs(DIP - DIM) / (DIP + DIM);
		if (InitStep < ADXStep - 1){
			DX.add(ADX);
			InitStep++;
		}else if (InitStep == ADXStep - 1){
			ADXMA = new MA(DX);
			ADXMA.NewMA(ADX);
			ADX = ADXMA.MA;
		}else{
			ADXMA.NewMA(ADX);
			ADX = ADXMA.MA;
		}
		return ADX;
	}
	
	
}

