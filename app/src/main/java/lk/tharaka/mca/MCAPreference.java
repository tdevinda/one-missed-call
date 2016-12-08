package lk.tharaka.mca;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MCAPreference {
	
	private boolean appEnabled;
	private boolean replaceNumbersWithText;
	private boolean callImmediatelyWhenClicked;
	private boolean includeNumbersInSMS;
	private boolean blockOperatorAds;
	
	private Context context;
	
	private MCAPreference() {};
	
	public MCAPreference(Context context) {
		this.context = context;
		
		loadSettings();
	}
	
	private void loadSettings() {
		SharedPreferences settingsPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		appEnabled = settingsPrefs.getBoolean("pref_enableMCA", false);
		replaceNumbersWithText = settingsPrefs.getBoolean("pref_useWordsForNumbers", true);
		callImmediatelyWhenClicked = settingsPrefs.getBoolean("pref_callbackOption", true);
		includeNumbersInSMS = settingsPrefs.getBoolean("pref_includeNumber", true);
		blockOperatorAds = settingsPrefs.getBoolean("pref_blockAds", true);
	}

	public boolean isAppEnabled() {
		return appEnabled;
	}

	public void setAppEnabled(boolean appEnabled) {
		this.appEnabled = appEnabled;
	}

	public boolean isReplaceNumbersWithText() {
		return replaceNumbersWithText;
	}

	public void setReplaceNumbersWithText(boolean replaceNumbersWithText) {
		this.replaceNumbersWithText = replaceNumbersWithText;
	}

	public boolean isCallImmediatelyWhenClicked() {
		return callImmediatelyWhenClicked;
	}

	public void setCallImmediatelyWhenClicked(boolean callImmediatelyWhenClicked) {
		this.callImmediatelyWhenClicked = callImmediatelyWhenClicked;
	}

	public boolean isIncludeNumbersInSMS() {
		return includeNumbersInSMS;
	}

	public void setIncludeNumbersInSMS(boolean includeNumbersInSMS) {
		this.includeNumbersInSMS = includeNumbersInSMS;
	}

	public boolean isBlockOperatorAds() {
		return blockOperatorAds;
	}

	public void setBlockOperatorAds(boolean blockOperatorAds) {
		this.blockOperatorAds = blockOperatorAds;
	}
	
	
	
}
