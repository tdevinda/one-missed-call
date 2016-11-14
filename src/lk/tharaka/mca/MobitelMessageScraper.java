package lk.tharaka.mca;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

public class MobitelMessageScraper implements MessageScraper {

	private static String mcaSenderPort = "Alert";
	
	private static String startText = "You have missed ";
	private static String startTextForSMS = "You have missed: ";
	
	@Override
	public ArrayList<MissedCall> getMissedCallsFromSMS(String sms) {
		
		ArrayList<MissedCall> calls = new ArrayList<MissedCall>();
		
		sms = sms.replace("\n", "");
		sms = sms.replace("\r", "");
		Log.i("MCA", sms);
		
		Pattern messagePattern = Pattern.compile("(\\d+) calls? from (\\d+) at ([\\d\\-:\\w,]+M); ?");
		Matcher matcher = messagePattern.matcher(sms);
		
		if(sms.startsWith(startText)) {
			
			while(matcher.find()) {
				
				MissedCall currentMissedCall = new MissedCall();
				currentMissedCall.count = Integer.parseInt(matcher.group(1));
				currentMissedCall.number = matcher.group(2);
				currentMissedCall.date = matcher.group(3);
				
				Log.i("MCA", currentMissedCall.toString());
				
				calls.add(currentMissedCall);
			}
		}
		
		return calls;
		
	}
	

	@Override
	public String getSMSFromMissedCalls(ArrayList<MissedCall> missedCalls,
			Context context) {
		
		MCACommon common = new MCACommon(context);
		MCAPreference prefs = new MCAPreference(context);
		
		
		String callDetais = "";
		for (MissedCall missedCall : missedCalls) {
			String counter = "", namePart = "";
			
			if(prefs.isReplaceNumbersWithText() && missedCall.count < 10) {
				counter = context.getResources().getStringArray(R.array.messageCounter)[missedCall.count - 1];
			} else {
				counter = missedCall.count + "";
			}
			
			namePart = common.getNameFor(missedCall.number) + (prefs.isIncludeNumbersInSMS()?" ("+ missedCall.number + ")":"");
			
			callDetais += counter +" call"+ ((missedCall.count > 1)?"s":"") + " from "+ namePart +" at "+ missedCall.date + ";";
		}
		
		callDetais = callDetais.replaceAll("(.*);$", "$1");
		
		return startTextForSMS + callDetais;
	}

	@Override
	public String getMissedCallAlertSMSSenderName() {
		return mcaSenderPort;
	}

}
