package fxcmIF;

import java.util.EventListener;
import aTGUI.*;
import aTWorker.WorkerFX;

public class ReadListener implements EventListener{
	//Trader trader;
	//MPage mpage;
	//private CommandLineIF CLIF = null;
	private GUIIF GIF = null;
	private WorkerFX WIF = null;
	
	private ListenerTypes LT = null;
	
	/*public ReadListener(CommandLineIF clif){
		CLIF = clif;
		LT = ListenerTypes.COMMANDLINE;
	}*/
	
	public ReadListener(GUIIF gif){
		GIF = gif;
		LT = ListenerTypes.GRAPHICINTERFACE;
	}
	
	public ReadListener(WorkerFX w){
		WIF = w;
		LT = ListenerTypes.DEDICATEDINST;
	}
	
	public void onAdded(ReadEvent sre){
		if (LT == ListenerTypes.COMMANDLINE){
			//CLIF.add(sre);
		}else if (LT == ListenerTypes.GRAPHICINTERFACE)
			GIF.add(sre);
		else if (LT == ListenerTypes.DEDICATEDINST){
			WIF.add(sre);
		}
	}
	
	public void onChanged(ReadEvent sre){
		if (LT == ListenerTypes.COMMANDLINE){
		//	CLIF.change(sre);
		}else if (LT == ListenerTypes.GRAPHICINTERFACE)
			GIF.change(sre);
		else if (LT == ListenerTypes.DEDICATEDINST){
			WIF.change(sre);
		}
	}
	
	public void onDeleted(ReadEvent sre){
		if (LT == ListenerTypes.COMMANDLINE){
			//CLIF.remove(sre);
		}else if (LT == ListenerTypes.GRAPHICINTERFACE)
			GIF.remove(sre);
		else if (LT == ListenerTypes.DEDICATEDINST){
			WIF.remove(sre);
		}
	}
	
}
