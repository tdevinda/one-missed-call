package lk.tharaka.mca;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class AlertMessageReceiver extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		MCAPreference prefs = new MCAPreference(context);
		
		//check if the app is enabled in settings.
		if(prefs.isAppEnabled()) {
			Bundle bundle = intent.getExtras();
			Object[] pdusObj = (Object[]) bundle.get("pdus");

			for (int i = 0; i < pdusObj.length; i++) {

				SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				String phoneNumber = currentMessage.getDisplayOriginatingAddress();

				Log.i("MCA", phoneNumber);
				if(MCACommon.KNOWN_OPERATOR_MCA_SMSPORTS.matches(".*"+ phoneNumber + ".*")){
					//means this came from a known missed call alert source number
					//Log.i("MCA", currentMessage.getMessageBody());
					abortBroadcast();
					//deleteSMS(context, currentMessage.getMessageBody(), phoneNumber);

					MCACommon common = new MCACommon(context);
					common.processSMS(
							currentMessage.getMessageBody(), 
							currentMessage.getDisplayOriginatingAddress(),
							currentMessage.getServiceCenterAddress());		//we have to use the service center to identify the destination 

				}
			}
		} else {
			
			//do nothing. we're disabled.
			Log.i("MCA", "MCA silent");
		}

	}


}
