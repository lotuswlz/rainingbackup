package com.thoughtworks.cathywu.smsbackup.api;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.thoughtworks.cathywu.smsbackup.model.Conversation;
import com.thoughtworks.cathywu.smsbackup.model.SmsData;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class SmsReader {

    private static final String SMS_URI_SENT = "content://sms/sent";
    private static final String SMS_URI_IN = "content://sms/inbox";
    private static final String SMS_URI_CONVERSATION = "content://sms/conversations";

    private ContentResolver contentResolver;

    public SmsReader(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    private Map<Integer, Set<SmsData>> readMessage(String uriAddress) {
        Uri uri = Uri.parse(uriAddress);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        Map<Integer, Set<SmsData>> result = new HashMap<Integer, Set<SmsData>>();
        SmsData smsData;
        int threadId;
        while (cursor.moveToNext()) {
            threadId = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.THREAD_ID));
            Set<SmsData> datas = result.get(threadId);
            if (datas == null) {
                datas = new TreeSet<SmsData>();
                result.put(threadId, datas);
            }
            smsData = new SmsData();
            smsData.setDate(new Date(cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE))));
            smsData.setContent(cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY)));
            smsData.setPhoneNumber(cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS)));
            smsData.setType(cursor.getInt(cursor.getColumnIndex(Telephony.Sms.TYPE)));
            datas.add(smsData);
        }
        cursor.close();
        return result;
    }

    private List<Conversation> readConversations() {
        Uri uri = Uri.parse(SMS_URI_CONVERSATION);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        List<Conversation> conversations = new ArrayList<Conversation>();

        while (cursor.moveToNext()) {
            int threadId = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.THREAD_ID));
            String snippet = cursor.getString(cursor.getColumnIndex(Telephony.Threads.SNIPPET));
            conversations.add(new Conversation(threadId, snippet));
        }
        cursor.close();
        return conversations;
    }

    public List<Conversation> read() {
        Map<Integer, Set<SmsData>> inMsgs = readMessage(SMS_URI_IN);
        Map<Integer, Set<SmsData>> sentMsgs = readMessage(SMS_URI_SENT);

        List<Conversation> conversations = readConversations();
        for (Conversation con : conversations) {
            int threadId = con.getThreadId();
            Date lastSmsDate = null;
            if (inMsgs.containsKey(threadId)) {
                con.addSmsDatas(inMsgs.get(threadId));
                Set<SmsData> inSmsSet = inMsgs.get(threadId);
                for (SmsData inSms : inSmsSet) {
                    con.getTargetParticipantMobiles().add(inSms.getPhoneNumber());
                }
                SmsData lastSms = inSmsSet.iterator().next();
                lastSmsDate = lastSms.getDate();
            }
            if (sentMsgs.containsKey(threadId)) {
                Set<SmsData> sentSmsSet = sentMsgs.get(threadId);
                con.addSmsDatas(sentSmsSet);
                for (SmsData sentSms : sentSmsSet) {
                    con.getTargetParticipantMobiles().add(sentSms.getPhoneNumber());
                }
                SmsData lastSms = sentSmsSet.iterator().next();
                if (lastSmsDate == null || lastSmsDate.before(lastSms.getDate())) {
                    lastSmsDate = lastSms.getDate();
                }
            }
            con.setLastSmsTime(lastSmsDate);
        }
        return conversations;
    }
}
