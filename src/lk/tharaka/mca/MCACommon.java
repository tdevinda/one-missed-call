package lk.tharaka.mca;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ViewDebug.FlagToString;

public class MCACommon {


	protected static final String EXTRA_NEXT_ACTION = "mca.nextAction";
	protected static final String ACTION_CALL_PHONE = "mca.callPhone";
	protected static final String EXTRA_PHONE_NUMBER = "mca.phoneNumber";
	
	protected static final String KNOWN_OPERATOR_MCA_SMSPORTS = "Alert";
	
	
	
	private Context context;
	private MessageScraper scraper;
	
	
	private MCACommon() {}

	public MCACommon(Context ctx) {
		context = ctx;
	}

	public void processSMS(String sms, String from, String to) {

		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		String operator = tm.getNetworkOperator();
		
		String destinationSIMOperator = to.replaceAll("\\+?(\\d{4}).+", "$1");
		Log.i("MCA", "destination operator = "+ destinationSIMOperator);
		
		if(operator.matches(MessageScraper.OPERATOR_DIALOG)) {
			scraper = new DialogMessageScraper();
			
		} else if(operator.matches(MessageScraper.OPERATOR_MOBITEL)) {
			scraper = new MobitelMessageScraper();
		}
		else {
			scraper = new NullScraper(from);
		}
		
		
		//TODO remove this. testing purpose.
		//scraper = new MobitelMessageScraper();
		
		ArrayList<MissedCall> missedCalls = scraper.getMissedCallsFromSMS(sms);
		if(missedCalls != null) {
			insertSMSInDatabase(
					scraper.getMissedCallAlertSMSSenderName(), 
					scraper.getSMSFromMissedCalls(missedCalls, context));
		} else {
			//there is no implementation for this operator. we write the SMS back! But we must show the alert notification.
			insertSMSInDatabase(scraper.getMissedCallAlertSMSSenderName(), sms);
		}

		addNotification(missedCalls, scraper.getMissedCallAlertSMSSenderName());
	}




	public void insertSMSInDatabase(String alertNumber, String sms) {
		ContentValues values = new ContentValues();
		values.put("address", alertNumber);
		values.put("body", sms);
		values.put("read", false);
		context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
	}


	/* gets the name for the given number
	 * 
	 */
	public String getNameFor(String phoneNumber)
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
	public void addNotification(ArrayList<MissedCall> missedCalls, String messageSenderPort) {

		Notification.Builder builder = new Notification.Builder(context);
		//main title
		builder.setContentTitle(context.getString(R.string.notificationTitle));
		builder.setAutoCancel(true);

		//default action opens the sms app with the Alert thread focused
		Intent openSMSIntent = new Intent(Intent.ACTION_VIEW);
		openSMSIntent.setType("vnd.android-dir/mms-sms");
		openSMSIntent.putExtra("address", messageSenderPort);
		builder.setContentIntent(PendingIntent.getActivity(context, 0, openSMSIntent, PendingIntent.FLAG_ONE_SHOT));
		builder.setSmallIcon(android.R.drawable.sym_call_missed);

		if(missedCalls == null) {
			builder.setContentText(
					context.getString(R.string.notificationMissedCallsUnknown)); 
			
			
		} else {

			if(missedCalls.size() == 1) {
				Log.i("MCA", "adding notification");
				builder.setContentText(
						context.getString(R.string.notificationMissedCallFromSingle) + 
						getNameFor(missedCalls.get(0).number));


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
						getNameFor(missedCalls.get(0).number) + context.getString(R.string.notificationMissedCallAnd) + 
						getNameFor(missedCalls.get(1).number);

				builder.setContentText(mcaContentText);

			} else if(missedCalls.size() >= 3) {
				Log.i("MCA", "adding notification 3");
				String mcaContentText = context.getString(R.string.notificationMissedCallFromMultiple) + 
						getNameFor(missedCalls.get(0).number) + context.getString(R.string.notificationMissedCallComma) + 
						getNameFor(missedCalls.get(1).number);

				if(missedCalls.size() == 3) {
					mcaContentText += context.getString(R.string.notificationMissedCallAnd) + getNameFor(missedCalls.get(2).number);
				} else {
					mcaContentText += context.getString(R.string.notificationMissedCallAnd) + context.getString(R.string.notificationMissedCallMore);
				}

				builder.setContentText(mcaContentText);
			}
			else {
				Log.i("MCA", "adding notification else for " + missedCalls.size());
			}
		}

		Notification notification = builder.build();
		//notification.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager notificationManager = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, notification);
	}
}
