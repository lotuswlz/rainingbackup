package com.thoughtworks.cathywu.smsbackup.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.thoughtworks.cathywu.smsbackup.R;
import com.thoughtworks.cathywu.smsbackup.api.ContactReader;
import com.thoughtworks.cathywu.smsbackup.common.Constants;
import com.thoughtworks.cathywu.smsbackup.common.SMSUtils;
import com.thoughtworks.cathywu.smsbackup.model.Conversation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsListActivity extends Activity {

    private List<Conversation> conversations;
    private Map<String, Conversation> conversationMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_list);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillWithSms();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sms_list, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_sms_list, container, false);
            return rootView;
        }
    }

    private void prepareDatas() throws IOException {
        conversations = SMSUtils.readSmsFromStorage();
        Collections.sort(conversations);
        conversationMap = generateConversationMap(conversations);

    }

    private void fillWithSms() {
        try {
            prepareDatas();
            List<Map<String, String>> datas = new ArrayList<Map<String, String>>();

            if (conversations == null) {
                return;
            }

            ContactReader contactReader = ContactReader.getInstance(getContentResolver());
            for (Conversation conv : conversations) {
                Map<String, String> valueMap = conv.generateShortDisplayMap();
                StringBuilder names = new StringBuilder();
                for (String phoneNumber : conv.getTargetParticipantMobiles()) {
                    String contactName = contactReader.findDisplayNameFor(phoneNumber);
                    if (contactName == null) {
                        contactName = phoneNumber;
                    }
                    names.append(contactName).append(",");
                }
                String displayName;
                if (names.toString().equals("")) {
                    displayName = valueMap.get(Constants.CONVERSATION_SHORT_DISPLAY_PHONE);
                } else {
                    displayName = names.toString();
                }
                valueMap.put(Constants.CONVERSATION_SHORT_DISPLAY_NAME, displayName);
                datas.add(valueMap);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(this, datas, R.layout.conversation_shortcut, new String[]{Constants.CONVERSATION_SHORT_DISPLAY_NAME, Constants.CONVERSATION_SHORT_DISPLAY_PHONE, Constants.CONVERSATION_SHORT_DISPLAY_DATE, Constants.CONVERSATION_SHORT_DISPLAY_CONTENT, Constants.CONVERSATION_SHORT_DISPLAY_COUNT}, new int[]{R.id.tv_sms_contact_name, R.id.tv_sms_phone, R.id.tv_sms_date, R.id.tv_sms_short_content, R.id.tv_sms_count});
            ListView myCityView = (ListView) findViewById(R.id.sms_list_view);
            myCityView.setAdapter(simpleAdapter);
        } catch (IOException e) {
            Log.e("SmsListActivity", "Exception occurs while reading sms from file.", e);
        }
    }

    private Map<String, Conversation> generateConversationMap(List<Conversation> conversations) {
        Map<String, Conversation> map = new HashMap<String, Conversation>();
        for (Conversation conversation : conversations) {
            map.put(conversation.getKey(), conversation);
        }
        return map;
    }

    public void onDisplayConversation(View view) {
        Intent intent = new Intent(this, ConversationDetailActivity.class);
        String action = getIntent().getAction();
        intent.setAction(action);
        Bundle bundle = new Bundle();
        String key = "_" + ((TextView) view.findViewById(R.id.tv_sms_phone)).getText().toString();
        bundle.putSerializable(Constants.CONVERSATION_INTENT_KEY, conversationMap.get(key));
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
    }
}
