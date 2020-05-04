package fxcmIF;

import com.fxcore2.*;
import java.util.concurrent.*;
import pureData.*;



public class LowLevelClasses {

}

enum HappendEvent{
	
	ADDED,
	CHANGED,
	DELETED
	
}

enum Tables{
	
	ACCOUNT,
	ORDERS,
	TRADES,
	CTRADES
	
}

class SSL implements IO2GSessionStatus {
	
	public boolean mConnected;
    public boolean mDisconnected;
    public boolean mError;
    public String MSG = "";
    private O2GSession mSession;
    private O2GSessionStatusCode mStatus;
    private ReadListener outListener = null;
    
    
    public SSL(O2GSession session, ReadListener rl){
    	mSession = session;
    	outListener = rl;
    	System.out.println(mSession);
    }
    
    public void onSessionStatusChanged(O2GSessionStatusCode status){
    	setmStatus(status);
    	
    	if (status == O2GSessionStatusCode.CONNECTED){
    		mConnected = true;
    	}else{
    		mConnected = false;
    	}
    	
    	if (status == O2GSessionStatusCode.DISCONNECTED){
    		mDisconnected = true;
    	}else{
        	mDisconnected = false;
    	}
    	
    	if (status == O2GSessionStatusCode.SESSION_LOST){
    		ReadEvent re = new ReadEvent(this,"lost",StringTypes.STATUS);
    		outListener.onChanged(re);
    	}
    	
    	if (status == O2GSessionStatusCode.UNKNOWN){
    		ReadEvent re = new ReadEvent(this,"unk",StringTypes.STATUS);
    		outListener.onChanged(re);
    	}
    	
    	if (status == O2GSessionStatusCode.TRADING_SESSION_REQUESTED)
    		MSG ="Argument for trading session ID is missing";
    	
    }
    
    public void onLoginFailed(String sError) {
        MSG = "Login error: " + sError;
        mError = true;
    }

	public O2GSessionStatusCode getmStatus() {
		return mStatus;
	}

	public void setmStatus(O2GSessionStatusCode mStatus) {
		this.mStatus = mStatus;
	}  

}


class TableListener implements IO2GTableListener {
	
	//private static final HappendEvent HappendEvent = null;
	private ReadListener outListener = null;
	private String offerID = "";
	
	public TableListener(ReadListener ol){
    	outListener = ol;
    }
	
	public TableListener(ReadListener ol, String in){
		outListener = ol;
		offerID = in;
	}
	
	private ReadEvent syntRow(O2GRow row, HappendEvent he){
		ReadEvent RetRow = null;
		if (row.getTableType().equals(O2GTableType.ACCOUNTS)){
			O2GAccountTableRow account = (O2GAccountTableRow)row;
			if (offerID.isEmpty()){
				FXAccount FXA = new FXAccount(account.getAccountName(),account.getEquity(),account.getDayPL(),
						account.getGrossPL(),account.getUsedMargin(),account.getUsedMargin3(),account.getUsableMargin());
				RetRow = new ReadEvent(this,FXA);
				logReport(FXA.getAccountName(),Tables.ACCOUNT,he);
			}
		}else if (row.getTableType().equals(O2GTableType.ORDERS)){
			O2GOrderTableRow order = (O2GOrderTableRow)row;
			if (offerID.isEmpty() || offerID.equals(order.getOfferID())){
				FXOrder FXO = new FXOrder(order.getOrderID(), order.getAccountID(), order.getStatus(),order.getOfferID(),
						order.getAmount(), order.getBuySell(), order.getRate(), order.getStop(), order.getLimit(), order.getStatusTime());
				RetRow = new ReadEvent(this,FXO);
				logReport(FXO.getOrderID(),Tables.ORDERS,he);
			}
		}else if (row.getTableType().equals(O2GTableType.TRADES)){
			O2GTradeTableRow trow = (O2GTradeTableRow)row;
			if (offerID.isEmpty() || offerID.equals(trow.getOfferID())){
				FXTrade FXT = new FXTrade(trow.getTradeID(), trow.getAccountID(), trow.getOfferID(), trow.getAmount(),
						trow.getBuySell(), trow.getOpenRate(), trow.getClose(), trow.getPL(), trow.getGrossPL(), trow.getCommission(),
						trow.getRolloverInterest(), trow.getUsedMargin(), trow.getOpenTime());
				RetRow = new ReadEvent(this,FXT);
				logReport(FXT.getTradeID(),Tables.TRADES,he);
			}
		}else if (row.getTableType().equals(O2GTableType.CLOSED_TRADES)){
			O2GClosedTradeTableRow ctrow = (O2GClosedTradeTableRow)row;
			if (offerID.isEmpty() || offerID.equals(ctrow.getOfferID())){
				FXClosedTrade FXCT = new FXClosedTrade(ctrow.getTradeID(), ctrow.getAccountID(), ctrow.getOfferID(),
						ctrow.getAmount(), ctrow.getBuySell(), ctrow.getOpenRate(), ctrow.getCloseRate(), ctrow.getPL(), 
						ctrow.getGrossPL(), ctrow.getCommission(), ctrow.getRolloverInterest(), ctrow.getOpenTime(), ctrow.getCloseTime());
				RetRow = new ReadEvent(this,FXCT);
				logReport(FXCT.getClosedID(),Tables.CTRADES,he);
			}
		}
		return RetRow;
	}
		
	public void onAdded(String string, O2GRow o2grow) {
		ReadEvent re = syntRow(o2grow, fxcmIF.HappendEvent.ADDED);
		outListener.onAdded(re);
    }
 
    public void onChanged(String string, O2GRow o2grow) {
    	ReadEvent re = syntRow(o2grow, fxcmIF.HappendEvent.CHANGED);
		outListener.onChanged(re);
    }
 
    public void onDeleted(String string, O2GRow o2grow) {
    	ReadEvent re = syntRow(o2grow, fxcmIF.HappendEvent.DELETED);
		outListener.onDeleted(re);
    }
 
    public void onStatusChanged(O2GTableStatus ogts) {
   
    }
    
    private void logReport(String ID, Tables TType, HappendEvent he){
    	String log = "";
    	if (TType == Tables.ACCOUNT){
    		if (he == fxcmIF.HappendEvent.ADDED){
    			log = "Account number " + ID + " is added to Account table.";
    		//}else if (he == HappendEvent.CHANGED){
    		//	log = "Account number " + ID + " is changed.";
    		}else if (he == fxcmIF.HappendEvent.DELETED){
    			log = "Account number " + ID + " is deleted from Account table.";
    		}
    	}else if (TType == Tables.ORDERS){
    		if (he == fxcmIF.HappendEvent.ADDED){
    			log = "Order number " + ID + " is added to Order table.";
    		//}else if (he == HappendEvent.CHANGED){
    		//	log = "Order number " + ID + " is changed.";
    		}else if (he == fxcmIF.HappendEvent.DELETED){
    			log = "Order number " + ID + " is deleted from Order table.";
    		}
    	}else if (TType == Tables.TRADES){
    		if (he == fxcmIF.HappendEvent.ADDED){
    			log = "Trade number " + ID + " is added to Trade table.";
    		//}else if (he == HappendEvent.CHANGED){
    		//	log = "Order number " + ID + " is changed.";
    		}else if (he == fxcmIF.HappendEvent.DELETED){
    			log = "Trade number " + ID + " is deleted from Trade table.";
    		}
    	}else if (TType == Tables.CTRADES){
    		if (he == fxcmIF.HappendEvent.ADDED){
    			log = "Trade number " + ID + " is closed.";
    		//}else if (he == HappendEvent.CHANGED){
    		//	log = "Order number " + ID + " is changed.";
    		}else if (he == fxcmIF.HappendEvent.DELETED){
    			log = "Closed trade number " + ID + " is deleted from Closed Trades table.";
    		}
    	}
    	if (!log.equals(""))
    		outListener.onAdded(new ReadEvent(this, log, StringTypes.LOG));
	}
}


class OffersListener implements IO2GTableListener {
	 
    private String mInstrument = null;
    private ReadListener outListener = null;
    
    public OffersListener(ReadListener ol){
    	outListener = ol;
    }
    
    public OffersListener(ReadListener ol, String Inst){
    	outListener = ol;
    	mInstrument = Inst;
    }
 
    public void onAdded(String string, O2GRow o2grow) {
 
    }
 
    public void onChanged(String string, O2GRow o2grow) {
    	O2GOfferTableRow offer = (O2GOfferTableRow)o2grow;
        if (mInstrument == null || mInstrument.isEmpty() || offer.getOfferID().equals(mInstrument)) {
        	if (offer.getSubscriptionStatus().equals("T")){
        		FXOffer noffer = new FXOffer(offer.getOfferID(), offer.getInstrument(), offer.getBid(), offer.getAsk(), offer.getLow(),
        				offer.getHigh(), offer.getVolume(), offer.getTime());
        		outListener.onChanged(new ReadEvent(this,noffer));
        	}
        }
    }
 
    public void onDeleted(String string, O2GRow o2grow) {
 
    }
 
    public void onStatusChanged(O2GTableStatus ogts) {
 
    }
 
}

class RLfHP implements IO2GResponseListener {
	 
    private boolean mError = false;
    private O2GResponse mResponse = null;
 
    public O2GResponse getResponse() {
        return mResponse;
    }
 
    public final boolean hasError() {
        return mError;
    }
 
    public RLfHP() {

    }
 
    public void onRequestCompleted(String requestID, O2GResponse response) {
        mResponse = response;
        mError = false;
    }
 
    public void onRequestFailed(String requestID, String error) {
        System.out.println("not inside: Request " + requestID + " failed. Error = " + error);
        mError = true;
    }
 
    public void onTablesUpdates(O2GResponse response) {
    }
 
}


class ResponseListener implements IO2GResponseListener {

    private O2GSession mSession;
    private String mRequestID;
    private O2GResponse mResponse;
    private Semaphore mSemaphore; 
    private ReadListener ol;
    public boolean res = true;

    // ctor
    public ResponseListener(O2GSession session, ReadListener olis) {
        mSession = session;
        mRequestID = "";
        mResponse = null;
        mSemaphore = new Semaphore(0);
        System.out.println(mSession);
        ol = olis;
    }

    public void setRequestID(String sRequestID) {
        mResponse = null;
        mRequestID = sRequestID;
    }

    public boolean waitEvents() throws Exception {
        return mSemaphore.tryAcquire(2, TimeUnit.SECONDS);
    }

    public O2GResponse getResponse() {
        return mResponse;
    }

    public void onRequestCompleted(String sRequestId, O2GResponse response) {
        if (mRequestID.equals(response.getRequestId())) {
            mResponse = response;
            if (response.getType() != O2GResponseType.CREATE_ORDER_RESPONSE) {
                mSemaphore.release();
            }
        }
    }

    public void onRequestFailed(String sRequestID, String sError) {
        if (mRequestID.equals(sRequestID)) {
        	ol.onAdded(new ReadEvent(this, "Request failed: " + sError, StringTypes.LOG));
        	res = false;
            mSemaphore.release();
        }
    }
    
    public void onTablesUpdates(O2GResponse response) {
    }
   
}

