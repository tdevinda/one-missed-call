package lk.tharaka.mca;

import java.util.ArrayList;

import android.content.Context;

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
