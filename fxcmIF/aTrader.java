package fxcmIF;

import java.text.*;
import java.util.*;

import com.fxcore2.*;

import pureData.*;
import tools.*;

public class aTrader{
	
	private String Uname = ""; //"D172385885001";
	private String Passw = ""; //"1671";
	private String SURL = ""; //"http://www.fxcorporate.com/Hosts.jsp";
	private String Atype = ""; //"Demo";
	private String moID = "";
	public String[] TimeFrames = null;
	
	private O2GSession mSession = null;
	private SSL statusListener = null;
	private RLfHP HisPriResLis = null;
	private ResponseListener responseListener = null;
	
	public static ArrayList<String[]> Instrument = new ArrayList<String[]>();
	private ReadListener outListener = null;
	

	public aTrader(String un, String pw, String U, String T, ReadListener rl){
		
		Uname  = un; Passw = pw; SURL = U; Atype = T;
		mSession =  O2GTransport.createSession();
		outListener = rl;
		statusListener = new SSL(mSession,rl);
		mSession.subscribeSessionStatus(statusListener);
		mSession.useTableManager(O2GTableManagerMode.YES, null);
		HisPriResLis = new RLfHP();
		mSession.subscribeResponse(HisPriResLis);
		responseListener = new ResponseListener(mSession, outListener);
		mSession.subscribeResponse(responseListener);
		
	}
	
	public void setInst(String mI){
		moID = mI;
	}
	
	
	public void connect() throws InterruptedException{
		
		mSession.login(Uname, Passw, SURL, Atype);
		statusReport("NAT > Connecting ...");
		
		while (!statusListener.mConnected && !statusListener.mError){
			Thread.sleep(50);
		}
		
		statusReport(statusListener.getmStatus().toString());
		
		if (statusListener.mConnected){
			statusReport("NAT > Connected to server ...");
		
			O2GTableManager TableMgr = mSession.getTableManager();
			while (TableMgr.getStatus() == O2GTableManagerStatus.TABLES_LOADING){
				Thread.sleep(50);
			}
		
			if (TableMgr.getStatus() == O2GTableManagerStatus.TABLES_LOADED) {
				
				OffersListener olistener = null;
				if (moID.isEmpty())
					olistener = new OffersListener(outListener);
				else
					olistener = new OffersListener(outListener, moID);
				O2GOffersTable offers = (O2GOffersTable)TableMgr.getTable(O2GTableType.OFFERS);
				offers.subscribeUpdate(O2GTableUpdateType.UPDATE, olistener);
				
				TableListener Tlistener = null;
				if (moID.isEmpty())
					Tlistener = new TableListener(outListener);
				else
					Tlistener = new TableListener(outListener, moID);
				
				O2GAccountsTable accounts = null;
				if (moID.isEmpty()){
					accounts =(O2GAccountsTable)TableMgr.getTable(O2GTableType.ACCOUNTS);
					accounts.subscribeUpdate(O2GTableUpdateType.INSERT, Tlistener);
					accounts.subscribeUpdate(O2GTableUpdateType.UPDATE, Tlistener);
					accounts.subscribeUpdate(O2GTableUpdateType.DELETE, Tlistener);
				}
				
				O2GOrdersTable  orders = (O2GOrdersTable)TableMgr.getTable(O2GTableType.ORDERS);
				orders.subscribeUpdate(O2GTableUpdateType.INSERT, Tlistener);
				orders.subscribeUpdate(O2GTableUpdateType.UPDATE, Tlistener);
				orders.subscribeUpdate(O2GTableUpdateType.DELETE, Tlistener);
				
				O2GTradesTable  trades = (O2GTradesTable)TableMgr.getTable(O2GTableType.TRADES);
				trades.subscribeUpdate(O2GTableUpdateType.INSERT, Tlistener);
				trades.subscribeUpdate(O2GTableUpdateType.UPDATE, Tlistener);
				trades.subscribeUpdate(O2GTableUpdateType.DELETE, Tlistener);
				
				O2GClosedTradesTable ctrades = (O2GClosedTradesTable)TableMgr.getTable(O2GTableType.CLOSED_TRADES);
				ctrades.subscribeUpdate(O2GTableUpdateType.INSERT, Tlistener);
				ctrades.subscribeUpdate(O2GTableUpdateType.UPDATE, Tlistener);
				ctrades.subscribeUpdate(O2GTableUpdateType.DELETE, Tlistener);
				
				InitOfferGoups(offers);
				if (moID.isEmpty())
					InitAccountTab(accounts);
				
				InitOrderTab(orders);
				InitTradeTab(trades);
				InitClosedTradeTab(ctrades);
				
				TimeFrames = getTimeFrames();
				

			}else if(statusListener.mError){
				statusReport(statusListener.MSG);
			}
				
		}
	}
	
	public void disconnect() throws InterruptedException{
		
		if (!statusListener.mError){
			mSession.logout();
			while (!statusListener.mDisconnected)
				Thread.sleep(50);
		}
		
		mSession.unsubscribeSessionStatus(statusListener);
		mSession.dispose();
		statusReport(statusListener.getmStatus().toString());
		
	}
	
	private String[] getTimeFrames(){
		O2GRequestFactory requestFactory = mSession.getRequestFactory();
		O2GTimeframeCollection timeFrames = requestFactory.getTimeFrameCollection();
		String[] TF = new String[timeFrames.size()];
		for (int i = 0; i < TF.length; i++)
			TF[i] = timeFrames.get(i).getId();
		return TF;
	}
	
	private void InitOfferGoups(O2GOffersTable offers){
			try{
				O2GTableIterator iterator = new O2GTableIterator();
				O2GOfferTableRow offer = offers.getNextRow(iterator);
				ArrayList<FXOffer> OfferTable = new ArrayList<FXOffer>();
				while (offer != null){
					if (moID.isEmpty()){
						if (offer.getSubscriptionStatus().equals("T")){
							String[] inst = {offer.getOfferID(), offer.getInstrument()};
							Instrument.add(inst);
							FXOffer noffer = new FXOffer(offer.getOfferID(), offer.getInstrument(), offer.getBid(), offer.getAsk(), offer.getLow(),
									offer.getHigh(), offer.getVolume(), offer.getTime());
							OfferTable.add(noffer);
						}
					}else
						if (offer.getInstrument().equals(moID)){
							FXOffer noffer = new FXOffer(offer.getOfferID(), offer.getInstrument(), offer.getBid(), offer.getAsk(), offer.getLow(),
									offer.getHigh(), offer.getVolume(), offer.getTime());
							OfferTable.add(noffer);
						}
					offer = offers.getNextRow(iterator);
				}
				for (int i = 1; i < OfferTable.size(); i++)
					for (int j = i; j > 0 && Integer.parseInt(OfferTable.get(j-1).getofferID()) > 
																				Integer.parseInt(OfferTable.get(j).getofferID()); j--){
						FXOffer rstr = OfferTable.get(j-1);
						OfferTable.set(j-1, OfferTable.get(j));
						OfferTable.set(j,  rstr);
					}
				for (int i = 0; i < OfferTable.size(); i++){
					FXOffer rStr = OfferTable.get(i);
					rStr.setIndex(i);
					outListener.onAdded(new ReadEvent(this,rStr));
				}
			}catch (Exception e){
				logReport("Exception in getOffer();. " + e.getMessage());
			}
			
	}
	
	private void InitAccountTab(O2GAccountsTable accounts){
		try{
			O2GTableIterator iterator = new O2GTableIterator();
			O2GAccountTableRow account = accounts.getNextRow(iterator);
			while (account != null){
				FXAccount FXA = new FXAccount(account.getAccountName(),account.getEquity(),account.getDayPL(),
						account.getGrossPL(),account.getUsedMargin(),account.getUsedMargin3(),account.getUsableMargin());
				outListener.onAdded(new ReadEvent(this,FXA));
				account = accounts.getNextRow(iterator);
			}
		} catch (Exception e) {
			logReport("Exception in getAccount(). " + e.getMessage());
		}
	}
	
	private void InitOrderTab(O2GOrdersTable orders){
		try{
			O2GTableIterator iterator = new O2GTableIterator();
			O2GOrderTableRow order = orders.getNextRow(iterator);
			while (order != null){
				if (moID.isEmpty() || moID.equals(order.getOfferID())){
					FXOrder FXO = new FXOrder(order.getOrderID(), order.getAccountID(), order.getStatus(),order.getOfferID(),
							order.getAmount(), order.getBuySell(), order.getRate(), order.getStop(), order.getLimit(), order.getStatusTime());
					outListener.onAdded(new ReadEvent(this,FXO));
				}
				order = orders.getNextRow(iterator);
			}
		} catch (Exception e) {
			logReport("Exception in getOrder(). " + e.getMessage());
		}
	}

	private void InitTradeTab(O2GTradesTable Trades){
		try{
			O2GTableIterator iterator = new O2GTableIterator();
			O2GTradeTableRow trow = Trades.getNextRow(iterator);
			while (trow != null){
				if (moID.isEmpty() || moID.equals(trow.getOfferID())){
					FXTrade FXT = new FXTrade(trow.getTradeID(), trow.getAccountID(), trow.getOfferID(), trow.getAmount(),
							trow.getBuySell(), trow.getOpenRate(), trow.getClose(), trow.getPL(), trow.getGrossPL(), trow.getCommission(), 
							trow.getRolloverInterest(), trow.getUsedMargin(), trow.getOpenTime());
					outListener.onAdded(new ReadEvent(this,FXT));
				}
				trow = Trades.getNextRow(iterator);

			}
		} catch (Exception e) {
			logReport("Exception in getTrade(). " + e.getMessage());
		}
	}

	private void InitClosedTradeTab(O2GClosedTradesTable CTrades){
		try{
			O2GTableIterator iterator = new O2GTableIterator();
			O2GClosedTradeTableRow ctrow = CTrades.getNextRow(iterator);
			while (ctrow != null){
				if (moID.isEmpty() || moID.equals(ctrow.getOfferID())){
					FXClosedTrade FXCT = new FXClosedTrade(ctrow.getTradeID(), ctrow.getAccountID(), ctrow.getOfferID(),
							ctrow.getAmount(), ctrow.getBuySell(), ctrow.getOpenRate(), ctrow.getCloseRate(), ctrow.getPL(), 
							ctrow.getGrossPL(), ctrow.getCommission(), ctrow.getRolloverInterest(), ctrow.getOpenTime(), ctrow.getCloseTime());
					outListener.onAdded(new ReadEvent(this,FXCT));
				}
				ctrow = CTrades.getNextRow(iterator);
			}
		} catch (Exception e) {
			logReport("Exception in getClosedTrade(). " + e.getMessage());
		}
	}

	public static String OfferIDtoInstrument(String offerID){
		String inst = "";
		for (int i = 0; i < Instrument.size(); i++)
			if (Instrument.get(i)[0].equals(offerID))
				inst = Instrument.get(i)[1];
		return inst;
	}
	
	public static String InstrumenttoOfferID(String Inst){
		String offerID = "";
		for (int i = 0; i < Instrument.size(); i++)
			if (Instrument.get(i)[1].equals(Inst))
				offerID = Instrument.get(i)[0];
		return offerID;
	}
	
	private void statusReport(String Status){
		outListener.onAdded(new ReadEvent(this, Status, StringTypes.STATUS));
	}
	
	private void logReport(String log){
		outListener.onAdded(new ReadEvent(this, log, StringTypes.LOG));
	}
	
	public HPTable getHistorics(String Inst, String TimeFrame, Calendar From, Calendar To) throws ParseException, InterruptedException{
		HPTable hpt = new HPTable(Inst, TimeFrame, From, To, new ArrayList<priceRow>());
		Calendar nextStep = CalTools.CalJump(From, TimeFrame, 300);
		From = CalTools.CalMover(From, "b");
		To = CalTools.CalMover(To, "b");
		nextStep = CalTools.CalMover(nextStep, "b");
		while (nextStep.before(To)){
			BuildHisPriceTable(hpt, From, nextStep);
			From = nextStep;
			nextStep = CalTools.CalJump(nextStep, TimeFrame, 300);
			nextStep = CalTools.CalMover(nextStep, "b");
			if (From.equals(nextStep)){
				From = new GregorianCalendar(From.get(Calendar.YEAR), From.get(Calendar.MONTH),
						From.get(Calendar.DAY_OF_MONTH) + 2, 17, 0, 0);
				nextStep = CalTools.CalJump(From, TimeFrame, 300);
			}
		}
		BuildHisPriceTable(hpt, From, To);
		String ret = Pures.HisPriceTester(hpt);
		//logReport(ret);
		hpt.TestR = ret;
		if (ret.equals("p"))
			logReport("Correctly returned " + hpt.size());
		else 
			logReport("Test failed on the historic price table!!");
		return hpt;
	}
	
	public HPTable BuildHisPriceTable(HPTable hpt, Calendar calFrom, Calendar calTo) throws InterruptedException, NullPointerException, ParseException{
		logReport("HisPrice: " + hpt.getInst() + " - " + hpt.getTFrame() + " - " + calFrom.getTime() + " - " + calTo.getTime());
		O2GRequestFactory requestFactory = mSession.getRequestFactory();
		O2GTimeframeCollection timeFrames = requestFactory.getTimeFrameCollection();
		O2GTimeframe timeFrame = timeFrames.get(hpt.getTFrame());
		O2GRequest request = requestFactory.createMarketDataSnapshotRequestInstrument(hpt.getInst(), timeFrame, 300);
		requestFactory.fillMarketDataSnapshotRequestTime(request, calFrom, calTo,false,
				O2GCandleOpenPriceMode.PREVIOUS_CLOSE);
		mSession.sendRequest(request);
		Thread.sleep(1000);
		if (!HisPriResLis.hasError()){
			O2GResponse response = HisPriResLis.getResponse();
			if (response != null){
				O2GResponseReaderFactory responseFactory = mSession.getResponseReaderFactory();
				O2GMarketDataSnapshotResponseReader reader = responseFactory.createMarketDataSnapshotReader(response);
				int mReaderSize = reader.size();
				O2GTimeConverter converter = mSession.getTimeConverter();
				logReport("Returned Historic Price Rows: " + mReaderSize);
				if (mReaderSize > 0){
					for (int i = 0; i < mReaderSize; i++) {
						Calendar BarDate = reader.getDate(i);
						BarDate = converter.convert(BarDate, O2GTimeConverterTimeZone.LOCAL);
						double[] prices = {reader.getBidOpen(i), reader.getBidHigh(i), reader.getBidLow(i),
								reader.getBidClose(i), reader.getAskOpen(i), reader.getAskHigh(i),
								reader.getAskLow(i), reader.getAskClose(i)};
						priceRow pr = new priceRow(BarDate, prices);
						boolean eDate = false;
						if (hpt.rows.size() > 0)
							if (pr.rDate.equals(hpt.rows.get(hpt.rows.size() - 1).rDate)){
								//System.out.println("Equal: "+ pr.rDate.getTime());
								eDate = true;
							}
						if (!eDate)
							hpt.rows.add(pr);
					}	
				}
			}
		}
		return hpt;
	}
		
	public void ImmediateRun(String Type, String accountID, FXOffer offer, FXTrade Trade, String BuySell, int Amount) throws Exception{
		O2GRequestFactory requestFactory = mSession.getRequestFactory();
        if (requestFactory != null) {
        	O2GValueMap valuemap = requestFactory.createValueMap();
        	valuemap.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.CreateOrder);
        	valuemap.setString(O2GRequestParamsEnum.ACCOUNT_ID, accountID);
        	valuemap.setString(O2GRequestParamsEnum.BUY_SELL, BuySell);
        	valuemap.setString(O2GRequestParamsEnum.TIME_IN_FORCE, Constants.TIF.FOK);
            valuemap.setInt(O2GRequestParamsEnum.AMOUNT, Amount);
            if (Type.equals("open")){
            	valuemap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.TrueMarketOpen);
            	valuemap.setString(O2GRequestParamsEnum.OFFER_ID, offer.getofferID());
            	valuemap.setString(O2GRequestParamsEnum.CUSTOM_ID, "OpenMarketOrder");
            }else if (Type.equals("close")){
            	valuemap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.TrueMarketClose);
            	valuemap.setString(O2GRequestParamsEnum.TRADE_ID, Trade.getTradeID());
            	valuemap.setString(O2GRequestParamsEnum.OFFER_ID, Trade.getOfferID());
            	valuemap.setString(O2GRequestParamsEnum.CUSTOM_ID, "CloseMarketOrder");
            }else{
            	logReport("Wrong order type. Type should be open or close.");
            	return;
            }
        	O2GRequest request = requestFactory.createOrderRequest(valuemap);
        	if (request != null) {
        		RunOrder(request);
        	}else{
        		logReport(requestFactory.getLastError());
        	}
        }else{
        	logReport("Cannot create request factory");
        }
	}
	
	public void EntryRun(String type, String accountID, String offerID, double dRate, String BuySell, int Amount) throws Exception{
		O2GRequestFactory requestFactory = mSession.getRequestFactory();
        if (requestFactory != null) {
        	O2GValueMap valuemap = requestFactory.createValueMap();
        	valuemap.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.CreateOrder);
        	valuemap.setString(O2GRequestParamsEnum.ACCOUNT_ID, accountID);
        	valuemap.setString(O2GRequestParamsEnum.BUY_SELL, BuySell);
        	if (type.equals("entry")){
        		valuemap.setInt(O2GRequestParamsEnum.AMOUNT, Amount);
        		valuemap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.Entry);
        		//valuemap.setString(O2GRequestParamsEnum.TIME_IN_FORCE, Constants.TIF.FOK);
        	}else if (type.equals("stop")){
        		valuemap.setString(O2GRequestParamsEnum.NET_QUANTITY, "Y");
        		valuemap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.StopEntry);
        	}else if (type.equals("limit")){
        		valuemap.setString(O2GRequestParamsEnum.NET_QUANTITY, "Y");
        		valuemap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.LimitEntry);
        	}
            valuemap.setString(O2GRequestParamsEnum.OFFER_ID, offerID);
            valuemap.setDouble(O2GRequestParamsEnum.RATE, dRate);
            valuemap.setString(O2GRequestParamsEnum.CUSTOM_ID, "EntryOrder");
            O2GRequest request = requestFactory.createOrderRequest(valuemap);
        	if (request != null){
        		RunOrder(request);
        	}else{
        		logReport(requestFactory.getLastError());
        	}
        }else{
        	logReport("Cannot create request factory");
        }
	}
	
	public void RemoveOrder(String accountID, String orderID) throws Exception{
		O2GRequestFactory requestFactory = mSession.getRequestFactory();
		if (requestFactory != null){
			O2GValueMap valuemap = requestFactory.createValueMap();
			valuemap.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.DeleteOrder);
	        valuemap.setString(O2GRequestParamsEnum.ACCOUNT_ID, accountID);
	        valuemap.setString(O2GRequestParamsEnum.ORDER_ID, orderID);
	        valuemap.setString(O2GRequestParamsEnum.CUSTOM_ID, "RemoveEntryOrder");
	        O2GRequest request = requestFactory.createOrderRequest(valuemap);
	        if (request != null){
	        	RunOrder(request);
        	}else{
        		logReport(requestFactory.getLastError());
        	}
        }else{
        	logReport("Cannot create request factory");
        }
	}
	
	private boolean RunOrder(O2GRequest request) throws Exception{
		responseListener.setRequestID(request.getRequestId()); // Store requestId
        mSession.sendRequest(request);
        responseListener.waitEvents();
        return true;
	}

}