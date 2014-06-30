package lk.tharaka.mca;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

public class MobitelMessageScraper implements MessageScraper {

	private static String mcaSenderPort = "Alert";
	
	private static String startText = "You have missed ";
	
	@Override
	public ArrayList<MissedCall> getMissedCallsFromSMS(String sms) {
		
		ArrayList<MissedCall> calls = new ArrayList<MissedCall>();
		
		sms = sms.replace("\n", "");
		sms = sms.replace("\r", "");
		Log.i("MCA", sms);
		
		Pattern messagePattern = Pattern.compile(" (\\d+) call from (//d+) at (.*);");
		Matcher matcher = messagePattern.matcher(sms);
		
		if(sms.startsWith(startText)) {
			Log.i("MCA","starts with");
			while(matcher.matches()) {
				Log.v("MCA", "match");
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
				counter = context.getResources().getStringArray(R.array.messageCounter)[missedCall.count];
			} else {
				counter = missedCall.count + "";
			}
			
			namePart = common.getNameFor(missedCall.number) + (prefs.isIncludeNumbersInSMS()?" ("+ missedCall.number + ")":"");
			
			callDetais += counter +" "+ namePart +" "+ missedCall.date + ";";
		}
		
		callDetais = callDetais.replaceAll("(.*);$", "$1");
		
		return startText + callDetais;
	}

	@Override
	public String getMissedCallAlertSMSSenderName() {
		return mcaSenderPort;
	}

}
