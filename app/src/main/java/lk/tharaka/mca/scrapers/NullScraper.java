package lk.tharaka.mca.scrapers;

import java.util.ArrayList;

import android.content.Context;

import lk.tharaka.mca.MissedCall;
import lk.tharaka.mca.scrapers.MessageScraper;

public class NullScraper implements MessageScraper {
	
	private String sender;
	
	public NullScraper(String sender) {
	}
	
	private NullScraper(){}
	
	@Override
	public ArrayList<MissedCall> getMissedCallsFromSMS(String sms) {
		return null;
	}

	@Override
	public String getSMSFromMissedCalls(ArrayList<MissedCall> missedCalls,
			Context context) {
		return null;
	}

	@Override
	public String getMissedCallAlertSMSSenderName() {
		return sender;
	}

}
