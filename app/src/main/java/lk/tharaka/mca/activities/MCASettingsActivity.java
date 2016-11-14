package lk.tharaka.mca.activities;

import java.util.ArrayList;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import junit.framework.Assert;

import lk.tharaka.mca.MissedCall;
import lk.tharaka.mca.R;
import lk.tharaka.mca.scrapers.DialogMessageScraper;
import lk.tharaka.mca.scrapers.MessageScraper;

public class MCASettingsActivity extends PreferenceActivity {
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.layout_prefs);
		
		/*
		MessageScraper scraper = new MobitelMessageScraper();
		String dummyMultiple = "You have missed 1 call from 0117102317 at 14-06-27,05:07PM; 2 calls from 0773336050 at 14-06-27,05:07PM; ";
		String dummySingle =   "You have missed 1 call from 0112653782 at 14-06-22,04:06PM;Stand a change to win a micro panda car wth mTunes. Dial 777 to take part. T&C Apply.";
		
		String sms = dummyMultiple;
		MCACommon common = new MCACommon(getApplicationContext());
		ArrayList<MissedCall> missedCalls = scraper.getMissedCallsFromSMS(sms);
		if(missedCalls != null) {
			common.insertSMSInDatabase(
					scraper.getMissedCallAlertSMSSenderName(), 
					scraper.getSMSFromMissedCalls(missedCalls, getApplicationContext()));
		} else {
			//there is no implementation for this operator. we write the SMS back! But we must show the alert notification.
			common.insertSMSInDatabase(scraper.getMissedCallAlertSMSSenderName(), sms);
		}

		common.addNotification(missedCalls, scraper.getMissedCallAlertSMSSenderName());
		*/

		String sms = "*this is a test ad* 2 Missed call(s) from 773336050 on 14-Nov-16 12:03PM ";
		String multiSMS = "Missed calls details : 1 from 718390541 on 21/09 20:16, 1 from 382246260 on 21/09 20:16. *Signal Fresh Mint; brush twice a day & keep cavities away* ";
		MessageScraper dialogScraper = new DialogMessageScraper();
		ArrayList<MissedCall> missedCallsFromSMS = dialogScraper.getMissedCallsFromSMS(multiSMS);
		for (MissedCall call : missedCallsFromSMS) {
			Log.i("MCA", call.toString());
			System.out.println(call.toString());

		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		finish();
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
	
	@Override
	protected void onPause() {
		super.onPause();
		
		finish();
	}
}
