package lk.tharaka.mca;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class AlertMessageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle bundle = intent.getExtras();
		Object[] pdusObj = (Object[]) bundle.get("pdus");
		
		for (int i = 0; i < pdusObj.length; i++) {

			SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
			String phoneNumber = currentMessage.getDisplayOriginatingAddress();
			
			Log.i("THARAKA", phoneNumber);
			if(phoneNumber.contains("330142")){
				abortBroadcast();
				
				ContentValues values = new ContentValues();
				values.put("address", phoneNumber);
				values.put("body", "this is an insertion");
				values.put("read", false);
				context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
			}
		}

	}

}
