package lk.tharaka.mca;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import lk.tharaka.mca.activities.NotificationActionActivity;
import lk.tharaka.mca.scrapers.DialogMessageScraper;
import lk.tharaka.mca.scrapers.MessageScraper;
import lk.tharaka.mca.scrapers.MobitelMessageScraper;
import lk.tharaka.mca.scrapers.NullScraper;

public class MCACommon {

	public static final String EXTRA_NEXT_ACTION = "mca.nextAction";
	public static final String ACTION_CALL_PHONE = "mca.callPhone";
	public static final String EXTRA_PHONE_NUMBER = "mca.phoneNumber";

	public static final String KNOWN_OPERATOR_MCA_SMSPORTS = "Alert";


	private Context context;
	private MessageScraper scraper;


	private MCACommon() {
	}

	public MCACommon(Context ctx) {
		context = ctx;
	}

	public void processSMS(String sms, String from, String to) {


		List<MissedCall> missedCalls = extractMissedCallsFromSMS(sms, from, to);

		/*
			// we're no longer doing this. we will be posting the notification, and will insert
			// data to our own screen from the sms db.
		if(missedCalls != null) {
			insertSMSInDatabase(
					scraper.getMissedCallAlertSMSSenderName(), 
					scraper.getSMSFromMissedCalls(missedCalls, context));
		} else {
			//there is no implementation for this operator. we write the SMS back! But we must show the alert notification.
			insertSMSInDatabase(scraper.getMissedCallAlertSMSSenderName(), sms);
		}
		*/

		addNotification(missedCalls, scraper.getMissedCallAlertSMSSenderName());
	}

	/**
	 * Gets the missed calls from the given sms and its details
	 * @param sms
	 * @param from
	 * @param to
     * @return
     */
	public List<MissedCall> extractMissedCallsFromSMS(String sms, String from, String to) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		String operator = tm.getNetworkOperator();

		String destinationSIMOperator = to.replaceAll("\\+?(\\d{4}).+", "$1");
		Log.i("MCA", "destination operator = " + destinationSIMOperator);

		if (operator.matches(MessageScraper.OPERATOR_DIALOG)) {
			scraper = new DialogMessageScraper();

		} else if (operator.matches(MessageScraper.OPERATOR_MOBITEL)) {
			scraper = new MobitelMessageScraper();
		} else {
			scraper = new NullScraper(from);
		}

		return scraper.getMissedCallsFromSMS(sms);

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
	public String getNameFor(String phoneNumber) {
		ContentResolver resolver = context.getContentResolver();
		Cursor names = resolver.query(
				Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber)),
				null, null, null, null);

		names.moveToFirst();
		String name = "";
		if (!names.isAfterLast()) {
			name = names.getString(names.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
		} else {
			name = phoneNumber;
		}

		names.close();

		return name;
	}


	public void addNotification(List<MissedCall> missedCalls, String messageSenderPort) {

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		//main title
		builder.setContentTitle(context.getString(R.string.notificationTitle));
		builder.setAutoCancel(true);

		//default action opens the sms app with the Alert thread focused
		Intent openSMSIntent = new Intent(Intent.ACTION_VIEW);
		openSMSIntent.setType("vnd.android-dir/mms-sms");
		openSMSIntent.putExtra("address", messageSenderPort);

		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		builder.setContentIntent(PendingIntent.getActivity(context, 0, openSMSIntent, PendingIntent.FLAG_UPDATE_CURRENT));
		builder.setSmallIcon(R.drawable.appicon);
		builder.setSound(soundUri);

		if (missedCalls == null) {
			builder.setContentText(
					context.getString(R.string.notificationMissedCallsUnknown));


		} else {

			if (missedCalls.size() == 1) {
				Log.i("MCA", "adding notification for " + missedCalls.get(0).number);
				builder.setContentText(
						context.getString(R.string.notificationMissedCallFromSingle) +
								getNameFor(missedCalls.get(0).number));

				if (android.os.Build.VERSION.SDK_INT >= 16) {
					Intent callContactIntent = new Intent(context, NotificationActionActivity.class);
					callContactIntent.putExtra(EXTRA_NEXT_ACTION, ACTION_CALL_PHONE);
					callContactIntent.putExtra(EXTRA_PHONE_NUMBER, missedCalls.get(0).number);

					builder.addAction(
							android.R.drawable.ic_menu_call,
							context.getString(R.string.notificationActionCallback),
							PendingIntent.getActivity(context, 0, callContactIntent, PendingIntent.FLAG_UPDATE_CURRENT));
				}

			} else if (missedCalls.size() == 2) {
				Log.i("MCA", "adding notification 2");
				String mcaContentText = context.getString(R.string.notificationMissedCallFromMultiple) +
						getNameFor(missedCalls.get(0).number) + context.getString(R.string.notificationMissedCallAnd) +
						getNameFor(missedCalls.get(1).number);

				builder.setContentText(mcaContentText);

			} else if (missedCalls.size() >= 3) {
				Log.i("MCA", "adding notification 3");
				String mcaContentText = context.getString(R.string.notificationMissedCallFromMultiple) +
						getNameFor(missedCalls.get(0).number) + context.getString(R.string.notificationMissedCallComma) +
						getNameFor(missedCalls.get(1).number);

				if (missedCalls.size() == 3) {
					mcaContentText += context.getString(R.string.notificationMissedCallAnd) + getNameFor(missedCalls.get(2).number);
				} else {
					mcaContentText += context.getString(R.string.notificationMissedCallAnd) + context.getString(R.string.notificationMissedCallMore);
				}

				builder.setContentText(mcaContentText);
			} else {
				Log.i("MCA", "adding notification else for " + missedCalls.size());
			}
		}

		Notification notification = builder.build();
		//notification.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager notificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
		notificationManager.notify(0, notification);


		//vibrate and sound
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		switch (audioManager.getRingerMode()) {

			case AudioManager.RINGER_MODE_NORMAL:
				//ring
			case AudioManager.RINGER_MODE_VIBRATE:
				vibrator.vibrate(new long[]{0, 200, 100, 200}, -1);
				break;
			case AudioManager.RINGER_MODE_SILENT:
				break;

		}


	}
}
