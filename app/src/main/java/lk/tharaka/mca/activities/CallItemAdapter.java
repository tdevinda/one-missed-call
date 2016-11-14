package lk.tharaka.mca.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import lk.tharaka.mca.MissedCall;
import lk.tharaka.mca.R;

/**
 * Created by Tharu on 2016-10-03.
 */

public class CallItemAdapter extends ArrayAdapter<MissedCall> {

    private Context context;
    private int resourceID;
    private List<MissedCall> itemList;

    public CallItemAdapter(Context context, int resource, List<MissedCall> objects) {
        super(context, resource, objects);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View item = inflater.inflate(R.layout.item_missedcall, null);
        TextView nameTextView = (TextView) item.findViewById(R.id.text_item_callername);
        nameTextView.setText("Test");


        return item;
    }
}
