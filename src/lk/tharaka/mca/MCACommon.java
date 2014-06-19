package lk.tharaka.mca;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

public class MCACommon {
	private static String missedCallsFrom = " Missed call\\(s\\) from ";
	private static String missedCallDetails = "Missed calls details";
	
	
	
	public ArrayList<MissedCall> getMissedCalls(String sms) {
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
			}
			
		}
		
		return missedCalls;


	}
	
	public String createSMS(ArrayList<MissedCall> calls, Context context) {
		
		String output = "";
		
		if(calls.size() == 1) {
			MissedCall call = calls.get(0);
			
			output = call.count + " Missed call" + ((call.count > 1)?"s ":" ") + "from "+ getNameFor(call.number, context) +" on "+ call.date;
		} else if(calls.size() > 1) {
			output = "Missed call details: ";
			for (MissedCall missedCall : calls) {
				
				output += missedCall.count + 
						"from "+ getNameFor(missedCall.number, context) +
						" on "+ missedCall.date + ", ";
			}
			
			output = output.replaceAll("(.*), $", "$1.");
		}
		
		return output;
		
	}
	
	
	/* gets the name for the given number
	 * 
	 */
	public String getNameFor(String phoneNumber, Context context)
	{
		ContentResolver resolver = context.getContentResolver();
		Cursor names = resolver.query(
				Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber)),
				null, null, null, null);

		names.moveToFirst();
		String name = "";
		if(!names.isAfterLast())
		{
			name = names.getString(names.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
		}
		else
		{
			name = phoneNumber;
		}
		
		names.close();
		
		return name;
	}
}
