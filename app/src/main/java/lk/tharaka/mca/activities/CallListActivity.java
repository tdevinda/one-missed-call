package lk.tharaka.mca.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.service.carrier.CarrierMessagingService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lk.tharaka.mca.MCACommon;
import lk.tharaka.mca.MCAPreference;
import lk.tharaka.mca.MissedCall;
import lk.tharaka.mca.R;

public class CallListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_list);


    }

    @Override
    protected void onStart() {
        super.onStart();
        SMSTask task = new SMSTask();
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(getApplicationContext());
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            Intent showSettingsIntent = new Intent(getApplicationContext(), MCASettingsActivity.class);
            startActivity(showSettingsIntent);
        }

        return true;

    }

    private List<MissedCall> getMissedCallsFromSMS() {
        List<MissedCall> missedCalls = new ArrayList<>();
        MCACommon common = new MCACommon(getApplicationContext());

        Cursor smsCursor = getContentResolver().query(
                Uri.parse("content://sms/inbox"),
                null,
                "address=?", new String[] {"Alert"},
                null);

        smsCursor.moveToFirst();

        while (!smsCursor.isAfterLast()) {
            List<MissedCall> currentSMSCalls = common.extractMissedCallsFromSMS(
                    smsCursor.getString(smsCursor.getColumnIndex("body")),
                    smsCursor.getString(smsCursor.getColumnIndex("address")),
                    smsCursor.getString(smsCursor.getColumnIndex("service_center")));

            missedCalls.addAll(currentSMSCalls);
            smsCursor.moveToNext();
        }
        smsCursor.close();

        return  missedCalls;

    }

    private void callNumber(String number) {
        Intent callPhoneIntent = new Intent(getApplicationContext(), NotificationActionActivity.class);
        callPhoneIntent.putExtra(MCACommon.EXTRA_NEXT_ACTION, MCACommon.ACTION_CALL_PHONE);
        callPhoneIntent.putExtra(MCACommon.EXTRA_PHONE_NUMBER, number);

        startActivity(callPhoneIntent);
        finish();

    }

    private class SMSTask extends AsyncTask<Void, Void, List<MissedCall>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(getApplicationContext(), "Fetching data...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected List<MissedCall> doInBackground(Void... voids) {
            MCACommon common = new MCACommon(getApplicationContext());

            List<MissedCall> missedCalls = getMissedCallsFromSMS();
            for (MissedCall call : missedCalls) {
                call.name = common.getNameFor(call.number);
            }
            return missedCalls;
        }

        @Override
        protected void onPostExecute(List<MissedCall> missedCalls) {
            super.onPostExecute(missedCalls);

            CallItemAdapter adapter = new CallItemAdapter(
                    getApplicationContext(),
                    R.layout.item_missedcall,
                    missedCalls
            );


            final ListView lv = (ListView) findViewById(R.id.listView_callList);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    callNumber(((MissedCall) lv.getItemAtPosition(i)).number);

                }
            });
        }
    }

}
