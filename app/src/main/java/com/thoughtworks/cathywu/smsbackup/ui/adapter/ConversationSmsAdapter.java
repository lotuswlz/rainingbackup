package com.thoughtworks.cathywu.smsbackup.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtworks.cathywu.smsbackup.R;
import com.thoughtworks.cathywu.smsbackup.common.Utils;
import com.thoughtworks.cathywu.smsbackup.model.SmsData;

import java.util.List;

public class ConversationSmsAdapter extends ArrayAdapter<SmsData> {

    private final int layout;
    private final LayoutInflater inflater;
    private List<SmsData> smsDatas;
    private Activity activity;

    public ConversationSmsAdapter(Activity activity, int layout, List<SmsData> smsDatas) {
        super(activity, layout, smsDatas);
        this.layout = layout;
        this.smsDatas = smsDatas;
        this.activity = activity;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return smsDatas.size();
    }

    @Override
    public SmsData getItem(int position) {
        return smsDatas.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = convertView == null ? inflater.inflate(layout, parent, false) : convertView;
        final SmsData sms = getItem(position);

        View itemView = view.findViewById(R.id.conversation_sms_view);
        TextView receivedMessageView = (TextView) itemView.findViewById(R.id.tv_received_message);
        TextView sentMessageView = (TextView) itemView.findViewById(R.id.tv_sent_message);
        TextView receivedDateView = (TextView) itemView.findViewById(R.id.tv_received_sms_date);
        TextView sentDateView = (TextView) itemView.findViewById(R.id.tv_sent_sms_date);

        if (sms.isIn()) {
            receivedMessageView.setText(sms.getContent());
            receivedDateView.setText(Utils.generateDateDisplayForSms(sms.getDate(), true));
            shiftDisplay(itemView, true);
        } else if (sms.isSent()) {
            sentMessageView.setText(sms.getContent());
            sentDateView.setText(Utils.generateDateDisplayForSms(sms.getDate(), true));
            shiftDisplay(itemView, false);
        }
        return view;
    }

    private void shiftDisplay(View itemView, boolean isMessageReceived) {
        TextView receivedMessageView = (TextView) itemView.findViewById(R.id.tv_received_message);
        TextView sentMessageView = (TextView) itemView.findViewById(R.id.tv_sent_message);
        TextView receivedDateView = (TextView) itemView.findViewById(R.id.tv_received_sms_date);
        TextView sentDateView = (TextView) itemView.findViewById(R.id.tv_sent_sms_date);
        ImageView contactLogo = (ImageView) itemView.findViewById(R.id.received_profile_logo);
        ImageView selfLogo = (ImageView) itemView.findViewById(R.id.sent_profile_logo);

        if (isMessageReceived) {
            receivedDateView.setVisibility(View.VISIBLE);
            receivedMessageView.setVisibility(View.VISIBLE);
            contactLogo.setVisibility(View.VISIBLE);
            sentDateView.setVisibility(View.INVISIBLE);
            sentMessageView.setVisibility(View.INVISIBLE);
            selfLogo.setVisibility(View.INVISIBLE);
        } else {
            sentDateView.setVisibility(View.VISIBLE);
            sentMessageView.setVisibility(View.VISIBLE);
            selfLogo.setVisibility(View.VISIBLE);
            receivedDateView.setVisibility(View.INVISIBLE);
            receivedMessageView.setVisibility(View.INVISIBLE);
            contactLogo.setVisibility(View.INVISIBLE);
        }
    }
}
