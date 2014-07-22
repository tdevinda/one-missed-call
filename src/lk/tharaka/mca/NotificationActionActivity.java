package lk.tharaka.mca;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class NotificationActionActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_flash_layout);

		Bundle extras = getIntent().getExtras();
		MCAPreference preferences = new MCAPreference(getApplicationContext());

		if(extras.containsKey(MCACommon.EXTRA_NEXT_ACTION)) {
			if(extras.getString(MCACommon.EXTRA_NEXT_ACTION).compareTo(MCACommon.ACTION_CALL_PHONE) == 0) {
				//phone call
				//clear the notification
				NotificationManager notificationManager = 
						(NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

				//notificationManager.cancel(0);
				notificationManager.cancelAll();

				//proceed with the call
				String numberToCall = extras.getString(MCACommon.EXTRA_PHONE_NUMBER);
				Log.i("MCA", "call number requested "+ numberToCall);
				
				
				//check whether the user wants to dial direct or just populate the dialler. 
				//populate the dialler by default if settings issue.
				String actionType = Intent.ACTION_DIAL;

				if(preferences.isCallImmediatelyWhenClicked()) {
					actionType = Intent.ACTION_CALL;
				} else {
					actionType = Intent.ACTION_DIAL;
				}

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
