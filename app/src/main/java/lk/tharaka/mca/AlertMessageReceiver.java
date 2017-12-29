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
            /*
            12-29 13:25:42.477 13045-13045/lk.tharaka.mca D/MCA: android.telephony.extra.SUBSCRIPTION_INDEX(class java.lang.Integer)=1
            12-29 13:25:42.477 13045-13045/lk.tharaka.mca D/MCA: format(class java.lang.String)=3gpp
            12-29 13:25:42.478 13045-13045/lk.tharaka.mca D/MCA: pdus(class [[B)=[[B@5c74dc
            12-29 13:25:42.479 13045-13045/lk.tharaka.mca D/MCA: slot(class java.lang.Integer)=0
            12-29 13:25:42.479 13045-13045/lk.tharaka.mca D/MCA: phone(class java.lang.Integer)=0
            12-29 13:25:42.480 13045-13045/lk.tharaka.mca D/MCA: subscription(class java.lang.Integer)=1
             */
            Object[] pdusObj = (Object[]) bundle.get("pdus");

			for (int i = 0; i < pdusObj.length; i++) {

                SmsMessage currentMessage = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i], bundle.getString("format"));
                } else {
                    currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

                }

                String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                int simSlot = bundle.getInt("slot", MCACommon.SIM_SLOT_DEFAULT);

//                Log.i("MCA", phoneNumber);
				if(MCACommon.KNOWN_OPERATOR_MCA_SMSPORTS.contains(phoneNumber)){	
					//means this came from a known missed call alert source number

					//abortBroadcast();		//we dont do this anymore. we're not the default app.

					MCACommon common = new MCACommon(context);
					common.processSMS(
							currentMessage.getMessageBody(), 
							currentMessage.getDisplayOriginatingAddress(),
							currentMessage.getServiceCenterAddress(),
                            simSlot);		//we have to use the service center to identify the destination

				}
			}
		} else {
			
			//do nothing. we're disabled.
			Log.i("MCA", "MCA silent");
		}

	}


}
