package lk.tharaka.mca;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ViewDebug.FlagToString;

public class MCACommon {
	private static String missedCallsFrom = " Missed call\\(s\\) from ";
	private static String missedCallDetails = "Missed calls details";
	
	public static final String EXTRA_NEXT_ACTION = "mca.nextAction";
	public static final String ACTION_CALL_PHONE = "mca.callPhone";
	public static final String EXTRA_PHONE_NUMBER = "mca.phoneNumber";
	
	
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
				
				missedCalls.add(currentMissedCall);
			}
			
		}
		
		return missedCalls;


	}
	
	public String createSMS(ArrayList<MissedCall> calls, Context context) {
		
		String output = "";
		
		if(calls.size() == 1) {
			MissedCall call = calls.get(0);
			
			//if settings>replace counts with text representations for small numbers
			String callCounter = "";
			if(call.count < 11) {
				callCounter = context.getResources().getStringArray(R.array.messageCounter)[call.count - 1];
			} else {
				callCounter = call.count + "";
			}
			
			output = callCounter + context.getString(R.string.messageMissedCallSimpleLetterStart) + ((call.count > 1)?"s ":" ") 
					+ context.getString(R.string.messageFrom) + getNameFor(call.number, context) 
					+" ("+ calls.get(0).number + ") " 
					+ context.getString(R.string.messageOn) + call.date;
			
		} else if(calls.size() > 1) {
			output = "Missed call details: ";
			for (MissedCall missedCall : calls) {
				
				output += missedCall.count + 
						context.getString(R.string.messageFrom) 
						+ getNameFor(missedCall.number, context) 
						+" ("+ missedCall.number + ") "  
						+ context.getString(R.string.messageOn) + missedCall.date + ", ";
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
	
	
	
	@SuppressLint("NewApi")
	public void addNotification(ArrayList<MissedCall> missedCalls, Context context) {
		
		Notification.Builder builder = new Notification.Builder(context);
		//main title
		builder.setContentTitle(context.getString(R.string.notificationTitle));
		builder.setAutoCancel(true);
		
		//default action opens the sms app with the Alert thread focused
		Intent openSMSIntent = new Intent(Intent.ACTION_VIEW);
		openSMSIntent.setType("vnd.android-dir/mms-sms");
		openSMSIntent.putExtra("address", "Alert");
		builder.setContentIntent(PendingIntent.getActivity(context, 0, openSMSIntent, PendingIntent.FLAG_ONE_SHOT));
		builder.setSmallIcon(android.R.drawable.sym_call_missed);
		
		
		if(missedCalls.size() == 1) {
			Log.i("MCA", "adding notification");
			builder.setContentText(
					context.getString(R.string.notificationMissedCallFromSingle) + 
					getNameFor(missedCalls.get(0).number, context));
			
			
			Intent callContactIntent = new Intent(context, NotificationActionActivity.class);
			callContactIntent.putExtra(EXTRA_NEXT_ACTION, ACTION_CALL_PHONE);
			callContactIntent.putExtra(EXTRA_PHONE_NUMBER, missedCalls.get(0).number);
			
			builder.addAction(
					android.R.drawable.ic_menu_call, 
					context.getString(R.string.notificationActionCallback), 
					PendingIntent.getActivity(context, 0, callContactIntent, PendingIntent.FLAG_ONE_SHOT));
			
			
		} else if(missedCalls.size() == 2) {
			Log.i("MCA", "adding notification 2");
			String mcaContentText = context.getString(R.string.notificationMissedCallFromMultiple) + 
					getNameFor(missedCalls.get(0).number, context) + context.getString(R.string.notificationMissedCallAnd) + 
					getNameFor(missedCalls.get(1).number, context);
			
			builder.setContentText(mcaContentText);
			
		} else if(missedCalls.size() >= 3) {
			Log.i("MCA", "adding notification 3");
			String mcaContentText = context.getString(R.string.notificationMissedCallFromMultiple) + 
					getNameFor(missedCalls.get(0).number, context) + context.getString(R.string.notificationMissedCallComma) + 
					getNameFor(missedCalls.get(1).number, context);
			
			if(missedCalls.size() == 3) {
				mcaContentText += context.getString(R.string.notificationMissedCallAnd) + getNameFor(missedCalls.get(2).number, context);
			} else {
				mcaContentText += context.getString(R.string.notificationMissedCallAnd) + context.getString(R.string.notificationMissedCallMore);
			}
			
			builder.setContentText(mcaContentText);
			
		} else {
			Log.i("MCA", "adding notification else for " + missedCalls.size());
		}
		
		Notification notification = builder.build();
		//notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		NotificationManager notificationManager = 
				  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, notification);
	}
}
