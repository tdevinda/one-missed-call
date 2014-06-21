package lk.tharaka.mca;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class AlertMessageReceiver extends BroadcastReceiver {
	
	private Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle bundle = intent.getExtras();
		Object[] pdusObj = (Object[]) bundle.get("pdus");
		
		for (int i = 0; i < pdusObj.length; i++) {

			SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
			String phoneNumber = currentMessage.getDisplayOriginatingAddress();
			
			Log.i("MCA", phoneNumber);
			if(phoneNumber.matches("Alert")){
				//Log.i("MCA", currentMessage.getMessageBody());
				abortBroadcast();
				//deleteSMS(context, currentMessage.getMessageBody(), phoneNumber);
				
				MCACommon common = new MCACommon();
				ArrayList<MissedCall> missedCalls = common.getMissedCalls(currentMessage.getMessageBody());
				
				String messageToInsert = common.createSMS(missedCalls, context);
				Log.i("MCA", messageToInsert);
				
				ContentValues values = new ContentValues();
				values.put("address", phoneNumber);
				values.put("body", messageToInsert);
				values.put("read", false);
				context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
				
				common.addNotification(missedCalls, context);
			}
		}

	}
	
	 public void deleteSMS(Context context, String message, String number) {
		    try {
		        Uri uriSms = Uri.parse("content://sms/inbox");
		        Cursor c = context.getContentResolver().query(
		                uriSms,
		                new String[] { "_id", "thread_id", "address", "person",
		                        "date", "body" }, "address", new String[]{"Alert"}, null);
		        Log.i("MCA", c.getCount()+ " sms found to be searched");
		        if (c != null && c.moveToFirst()) {
		            do {
		                long id = c.getLong(0);
		                long threadId = c.getLong(1);
		                String address = c.getString(2);
		                String body = c.getString(5);
		                String date = c.getString(3);
		                Log.e("MCA",
		                        "0>" + c.getString(0) + "1>" + c.getString(1)
		                                + "2>" + c.getString(2) + "<-1>"
		                                + c.getString(3) + "4>" + c.getString(4)
		                                + "5>" + c.getString(5));
		                Log.e("MCA", "date" + c.getString(0));

		                if (message.equals(body) && address.equals(number)) {
		                    // mLogger.logInfo("Deleting SMS with id: " + threadId);
		                    context.getContentResolver().delete(
		                            Uri.parse("content://sms/" + id), "date=?",
		                            new String[] { c.getString(4) });
		                    Log.e("MCA", "Delete success.........");
		                }
		            } while (c.moveToNext());
		        }
		    } catch (Exception e) {
		        Log.e("MCA", e.toString());
		    }
		}
	

}
