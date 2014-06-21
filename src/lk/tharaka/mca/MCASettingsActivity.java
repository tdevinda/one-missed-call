package lk.tharaka.mca;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class MCASettingsActivity extends PreferenceActivity {
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.layout_prefs);
		ArrayList<MissedCall> calls = new ArrayList<MissedCall>();
		MissedCall call = new MissedCall();
		call.count = 1;
		call.number = "773336050";
		call.date = "today";
		
		calls.add(call);
		
		MissedCall call2 = new MissedCall();
		call2.count = 1;
		call2.number = "777336890";
		call2.date = "today";
		
		calls.add(call2);
		
		MCACommon common = new MCACommon();
		common.addNotification(calls, getApplicationContext());
		
	}
	
	
	/*
	 * Settings to include
	 * - Show missed call counters in text for small numbers true | false, default: false
	 * - When call is selected, A: call immediately B: enter number to dialpad, default: call immediately
	 * - Include number in SMS true|false, default: true
	 * - Block trailing ads in SMS true|false, default: true
	 * 
	 * 
	 */
	
}
