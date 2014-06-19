package lk.tharaka.mca;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
				Log.i("MCA", currentMessage.getMessageBody());
				abortBroadcast();
				
				
				MCACommon common = new MCACommon();
				String messageToInsert = common.createSMS(common.getMissedCalls(currentMessage.getMessageBody()), context);
				Log.i("MCA", messageToInsert);
				
				ContentValues values = new ContentValues();
				values.put("address", phoneNumber);
				values.put("body", messageToInsert);
				values.put("read", false);
				context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
			}
		}

	}
	
	

}
