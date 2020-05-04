package fxcmIF;

import java.util.EventObject;

import pureData.FXAccount;
import pureData.FXClosedTrade;
import pureData.FXOffer;
import pureData.FXOrder;
import pureData.FXTrade;

public class ReadEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private EventTypes ET = null;
	private String Status = "";
	private String Log = "";
	private FXOffer FXO = null;
	private FXAccount FXA = null;
	private FXOrder FXOr = null;
	private FXTrade FXT = null;
	private FXClosedTrade FXCT = null;
	
	public ReadEvent(Object source, String Str, StringTypes st){
		super(source);
		if (st == StringTypes.STATUS){
			ET = EventTypes.STATUS;
			Status = Str;
		}else if (st == StringTypes.LOG){
			ET = EventTypes.LOG;
			Log = Str;
		}
	}
    
    public ReadEvent(Object source, FXOffer fxo){
    	super(source);
    	ET = EventTypes.FXOFFER;
    	FXO = fxo;
    }
    
    public ReadEvent(Object source, FXAccount fxa){
    	super(source);
    	ET = EventTypes.ACCOUNT;
    	FXA = fxa;
    }
    
    public ReadEvent(Object source, FXOrder fxo){
    	super(source);
    	ET = EventTypes.ORDER;
    	FXOr = fxo;
    }
    
    public ReadEvent(Object source, FXTrade fxa){
    	super(source);
    	ET = EventTypes.OPENPOSITION;
    	FXT = fxa;
    }
    
    public ReadEvent(Object source, FXClosedTrade fxa){
    	super(source);
    	ET = EventTypes.CLOSEDPOSITION;
    	FXCT = fxa;
    }
    
    
    public EventTypes getET(){
    	return ET;
    }
    
    public String getStatus(){
    	return Status;
    }
    
    public String getLog(){
    	return Log;
    }
    
   public FXOffer getFXOffer(){
	   return FXO;
   }
   
   public FXAccount getFXAccount(){
	   return FXA;
   }
   
   public FXOrder getFXOrder(){
	   return FXOr;
   }
   
   public FXTrade getFXTrade(){
	   return FXT;
   }
   
   public FXClosedTrade getFXCTrade(){
	   return FXCT;
   }
    
}
