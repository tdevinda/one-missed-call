package lk.tharaka.mca.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.service.carrier.CarrierMessagingService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lk.tharaka.mca.MCACommon;
import lk.tharaka.mca.MissedCall;
import lk.tharaka.mca.R;

public class CallListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_list);

        Log.d("CALLL", "starting to read sms");
        System.out.println("starting...");
        /*SMSTask task = new SMSTask();
        task.execute();*/

        List<MissedCall> missedCalls = getMissedCallsFromSMS();
        CallItemAdapter adapter = new CallItemAdapter(
                getApplicationContext(),
                R.layout.item_missedcall,
                missedCalls
        );

        ListView lv = (ListView) findViewById(R.id.listView_callList);
        lv.setAdapter(adapter);

    }




    private List<MissedCall> getMissedCallsFromSMS() {
        List<MissedCall> missedCalls = new ArrayList<>();
        MCACommon common = new MCACommon(getApplicationContext());

        Log.i("SMS", "starting to read");
        Cursor smsCursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, "address=?", new String[] {"Alert"}, null);

        smsCursor.moveToFirst();

        while (!smsCursor.isAfterLast()) {
            Log.i("CALLL", "====");
            for (int i = 0; i < smsCursor.getColumnCount(); i++) {
                Log.i("CALLL", String.format("%s: %s", smsCursor.getColumnName(i), smsCursor.getString(i)));
                List<MissedCall> currentSMSCalls = common.extractMissedCallsFromSMS(
                        smsCursor.getString(smsCursor.getColumnIndex("body")),
                        smsCursor.getString(smsCursor.getColumnIndex("address")),
                        smsCursor.getString(smsCursor.getColumnIndex("service_center")));

                missedCalls.addAll(currentSMSCalls);
            }

            smsCursor.moveToNext();

        }

        return  missedCalls;

    }

    private class SMSTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Starting fetch", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getMissedCallsFromSMS();
            return null;
        }
    }

}
