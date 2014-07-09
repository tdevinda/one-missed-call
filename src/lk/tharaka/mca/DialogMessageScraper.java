package lk.tharaka.mca;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

public class DialogMessageScraper implements MessageScraper {

	private static String missedCallsFrom = " Missed call\\(s\\) from ";
	private static String missedCallDetails = "Missed calls details";

	private static String mcaSenderPort = "Alert";

	MCAPreference preferences;

	@Override
	public ArrayList<MissedCall> getMissedCallsFromSMS(String sms) {
		sms = sms.replace("\n", "");
		sms = sms.replace("\r", "");
		Log.i("MCA", sms);
		ArrayList<MissedCall> missedCalls = new ArrayList<MissedCall>();

		if(sms.matches("^\\d"+ missedCallsFrom + ".*")) {
			//single missed call mode
			MissedCall oneCall = new MissedCall();
			oneCall.count = Integer.parseInt(sms.replaceAll("(\\d)"+ missedCallsFrom + "(\\d+) on ([\\d\\w\\-: ]+M).*", "$1"));
			oneCall.number = sms.replaceAll("(\\d)"+ missedCallsFrom + "(\\d+) on ([\\d\\w\\-: ]+M).*", "$2");
			oneCall.date = sms.replaceAll("(\\d)"+ missedCallsFrom + "(\\d+) on ([\\d\\w\\-: ]+M).*", "$3");

			Log.i("MCA", oneCall.toString());
			missedCalls.add(oneCall);

		} else if(sms.matches("^" + missedCallDetails + ".*")) {
			//multiple message format
			Pattern multiMessagePattern = Pattern.compile("(\\d) from (\\d+) on ([\\d/: ]+)[,.] ?");

			Matcher matcher = multiMessagePattern.matcher(sms);
			while(matcher.find()) {

				MissedCall currentMissedCall = new MissedCall();
				currentMissedCall.count = Integer.parseInt(matcher.group(1));
				currentMissedCall.number = matcher.group(2);
				currentMissedCall.date = matcher.group(3);

				Log.i("MCA", currentMissedCall.toString());

				missedCalls.add(currentMissedCall);
			}

		}

		return missedCalls;

	}

	@Override
	public String getSMSFromMissedCalls(ArrayList<MissedCall> missedCalls,
			Context context) {
		MCACommon common = new MCACommon(context);
		preferences = new MCAPreference(context);

		String output = "";

		if(missedCalls.size() == 1) {
			MissedCall call = missedCalls.get(0);

			//if settings>replace counts with text representations for small numbers

			String callCounter = "";
			if(preferences.isReplaceNumbersWithText() && call.count < 11) {
				callCounter = context.getResources().getStringArray(R.array.messageCounter)[call.count - 1];
			} else {
				callCounter = call.count + "";
			}

			String calledUserNumberPart = "";
			if(preferences.isIncludeNumbersInSMS()) {
				calledUserNumberPart = " ("+ missedCalls.get(0).number + ") ";
			} else {
				calledUserNumberPart = " ";
			}
			
			output = callCounter
					+ context.getString(R.string.messageMissedCallSimpleLetterStart) + ((call.count > 1)?"s":"") 
					+ context.getString(R.string.messageFrom) + common.getNameFor(call.number) 
					+ calledUserNumberPart
					+ context.getString(R.string.messageOn) + call.date;

		} else if(missedCalls.size() > 1) {
			output = "Missed call details: ";
			for (MissedCall missedCall : missedCalls) {
				
				String callCounter = "";
				if(preferences.isReplaceNumbersWithText() && missedCall.count < 11) {
					callCounter = context.getResources().getStringArray(R.array.messageCounter)[missedCall.count - 1];
				} else {
					callCounter = missedCall.count + "";
				}
				
				
				String calledUserNumberPart = "";
				if(preferences.isIncludeNumbersInSMS()) {
					calledUserNumberPart = " ("+ missedCall.number + ") ";
				} else {
					calledUserNumberPart = " ";
				}
				
				
				output += callCounter +
						context.getString(R.string.messageFrom) 
						+ common.getNameFor(missedCall.number) 
						+ calledUserNumberPart 
						+ context.getString(R.string.messageOn) + missedCall.date + ", ";
			}

			output = output.replaceAll("(.*), $", "$1.");
		}

		return output;
	}

	@Override
	public String getMissedCallAlertSMSSenderName() {
		return mcaSenderPort;
	}



}
