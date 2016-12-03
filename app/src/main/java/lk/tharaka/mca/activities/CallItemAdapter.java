package lk.tharaka.mca.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import lk.tharaka.mca.MCAPreference;
import lk.tharaka.mca.MissedCall;
import lk.tharaka.mca.R;

/**
 * Created by Tharu on 2016-10-03.
 */

public class CallItemAdapter extends ArrayAdapter<MissedCall> {

    private Context context;
    private int resourceID;
    private List<MissedCall> itemList;

    private MCAPreference preference;

    public CallItemAdapter(Context context, int resource, List<MissedCall> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resourceID = resource;
        this.itemList = objects;

        preference = new MCAPreference(context);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View item = inflater.inflate(R.layout.item_missedcall, null);
        TextView nameTextView = (TextView) item.findViewById(R.id.text_item_callername);
        TextView callCounterTextView = (TextView) item.findViewById(R.id.text_item_count_counter);
        TextView callCounterLabelTextView = (TextView) item.findViewById(R.id.text_item_count_label);
        TextView timestampTextView = (TextView) item.findViewById(R.id.text_item_date);

        int callCount = itemList.get(position).count;

        nameTextView.setText(itemList.get(position).name);
        callCounterTextView.setText(!preference.isIncludeNumbersInSMS()?
                callCount + "":
                context.getResources().getStringArray(R.array.messageCounter)[callCount - 1]);

        callCounterLabelTextView.setText((itemList.get(position).count > 1)?
                context.getString(R.string.messageListItemCallLabelMultiple):
                context.getString(R.string.messageListItemCallLabelSingle));
        timestampTextView.setText(itemList.get(position).date);
        return item;

    }


}
