package lk.tharaka.mca;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class NotificationActionActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(MCACommon.EXTRA_NEXT_ACTION)) {
			if(extras.getString(MCACommon.EXTRA_NEXT_ACTION).compareTo(MCACommon.ACTION_CALL_PHONE) == 0) {
				//phone call
				//clear the notification
				NotificationManager notificationManager = 
						  (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
				
				notificationManager.cancel(0);
				
				
				//proceed with the call
				String numberToCall = extras.getString(MCACommon.EXTRA_PHONE_NUMBER);
				
				String actionType = Intent.ACTION_CALL;		//TODO: set a preference. Either call direct, or go to dialer
				Intent callContactIntent = new Intent(actionType, Uri.parse("tel:"+ numberToCall));
				
				startActivity(callContactIntent);
				this.finish();
			}
		} else {
			
			//no next action
			finish();
		}
	}
}
