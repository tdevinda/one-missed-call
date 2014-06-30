package lk.tharaka.mca;

import java.util.ArrayList;

import android.content.Context;

public interface MessageScraper {
	
	public static final String OPERATOR_DIALOG = "41302";
	public static final String OPERATOR_MOBITEL = "41301";
	
	public ArrayList<MissedCall> getMissedCallsFromSMS(String sms);
	
	public String getSMSFromMissedCalls(ArrayList<MissedCall> missedCalls, Context context);
	
	public String getMissedCallAlertSMSSenderName();
	
}
