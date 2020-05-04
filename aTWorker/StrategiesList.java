package aTWorker;

import strategies.*;
import strategybuilder.*;

public enum StrategiesList {
	
	pmacd,
	mct,
	bband,
	hedge,
	eur17,
	reur,
	drrsieur,
	rsivN,
	rsiv,
	dgetter,
	eur;
	
	public static String[] StratList(){
		StrategiesList[] stats = StrategiesList.values();
		String[] Stats = new String[stats.length];
		for (int i = 0; i < stats.length; i++)
			Stats[i] = stats[i].toString();
		return Stats;
	}
	
	public static String StratListStr(){
		String str = "\n";
		StrategiesList[] stats = StrategiesList.values();
		for (int i = 0; i < stats.length; i++)
			str += i + " " + stats[i].toString() + "\n";
		return str;
	}
	
	public static strategy stratMaker(String stratName){
		strategy strat = null;
		if (stratName.equals(StrategiesList.dgetter.toString())){
			strat = new dgetter();
		}else if (stratName.equals(StrategiesList.eur17.toString())){
				strat = new RDRRSIst(18,18,0.25,0,9,11,false);
		}else if (stratName.equals(StrategiesList.drrsieur.toString())){
			strat = new DRRSIst(19,19,0.0002,0,16,16,true);
		}else if (stratName.equals(StrategiesList.reur.toString())){
			strat = new RDRRSIst(19,19,0.0002,0,10,12,false);
		}else if (stratName.equals(StrategiesList.eur.toString())){
			strat = new DRRSIst(19,19,0.0002,0,10,12,false);
		}else if (stratName.equals(StrategiesList.rsiv.toString())){
			strat = new RSIVals(14,14);
		}else if (stratName.equals(StrategiesList.rsivN.toString())){
			strat = new RSIValsN(14,14,0.5);
		}else if (stratName.equals(StrategiesList.hedge.toString()))
			strat = new Hedge1(0.001,0.001,4);
		else if (stratName.equals(StrategiesList.bband.toString()))
			strat = new BBStrat(20,2,0.003,0.003,0);
		else if (stratName.equals(StrategiesList.mct.toString()))
			strat = new MCTester(5,50,1,0.00025,0.00005);
		else if (stratName.equals(StrategiesList.pmacd.toString()))
			strat = new PMACD(50,200,0.0020,0.0040);
		return strat;
		
	}

}

