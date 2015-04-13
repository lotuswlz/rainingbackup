package com.thoughtworks.cathywu.smsbackup.ui;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ListView;
import android.widget.TextView;

import com.thoughtworks.cathywu.smsbackup.R;
import com.thoughtworks.cathywu.smsbackup.api.ContactReader;
import com.thoughtworks.cathywu.smsbackup.common.Constants;
import com.thoughtworks.cathywu.smsbackup.model.Conversation;
import com.thoughtworks.cathywu.smsbackup.model.SmsData;
import com.thoughtworks.cathywu.smsbackup.ui.adapter.ConversationSmsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConversationDetailActivity extends Activity {

    private Conversation conversation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_detail);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        conversation = (Conversation) getIntent().getSerializableExtra(Constants.CONVERSATION_INTENT_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadConversation();
    }

    private void loadConversation() {
        String phoneNumber = conversation.getKey().replaceFirst("_", "");
        String contactName = ContactReader.getInstance(getContentResolver()).findDisplayNameFor(phoneNumber);
        if (contactName == null) {
            contactName = phoneNumber;
        } else {
            contactName = contactName + "/" + phoneNumber;
        }
        TextView label = (TextView) findViewById(R.id.tv_sms_phone_number);
        label.setText(contactName);
        ListView listView = (ListView) findViewById(R.id.conversation_sms_list_view);
        List<SmsData> smsDatas = new ArrayList<SmsData>();
        smsDatas.addAll(conversation.getSmsDatas());
        Collections.sort(smsDatas);
        ConversationSmsAdapter adapter = new ConversationSmsAdapter(this, R.layout.conversation_sms_view, smsDatas);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.conversation_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_conversation_detail, container, false);
            return rootView;
        }
    }
}
