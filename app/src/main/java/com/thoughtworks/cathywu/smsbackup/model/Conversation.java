package com.thoughtworks.cathywu.smsbackup.model;

import com.thoughtworks.cathywu.smsbackup.common.Constants;
import com.thoughtworks.cathywu.smsbackup.common.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Conversation implements Comparable<Conversation>, Serializable {

    private static final long serialVersionUID = 1L;

    private int threadId;
    private Set<String> targetParticipantMobiles;
    private Set<SmsData> smsDatas;
    private String snippet;
    private Date lastSmsTime;

    public Conversation(int threadId, String snippet) {
        this.threadId = threadId;
        this.smsDatas = new TreeSet<SmsData>();
        this.targetParticipantMobiles = new TreeSet<String>();
        this.snippet = snippet;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public Date getLastSmsTime() {
        return lastSmsTime;
    }

    public void setLastSmsTime(Date lastSmsTime) {
        this.lastSmsTime = lastSmsTime;
    }

    public int getThreadId() {
        return threadId;
    }
    public Set<String> getTargetParticipantMobiles() {
        return targetParticipantMobiles;
    }

    public void addSmsDatas(Set<SmsData> smsDatas) {
        this.smsDatas.addAll(smsDatas);
    }

    public Set<SmsData> getSmsDatas() {
        return smsDatas;
    }

    public String getKey() {
        StringBuilder tempStr = new StringBuilder();
        for (String number : targetParticipantMobiles) {
            tempStr.append("_").append(number);
        }
        return tempStr.toString();
    }

    @Override
    public int compareTo(Conversation another) {
        if (another == null) {
            return 1;
        }
        if (another.getLastSmsTime() == null) {
            return 1;
        }
        if (this.getLastSmsTime() == null) {
            return -1;
        }
        return another.getLastSmsTime().compareTo(this.getLastSmsTime());
    }

    public Map<String, String> generateShortDisplayMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(Constants.CONVERSATION_SHORT_DISPLAY_KEY, this.getKey());
        map.put(Constants.CONVERSATION_SHORT_DISPLAY_PHONE, this.getKey().replaceFirst("_", ""));
        map.put(Constants.CONVERSATION_SHORT_DISPLAY_CONTENT, snippet);
        map.put(Constants.CONVERSATION_SHORT_DISPLAY_DATE, Utils.generateDateDisplayForSms(lastSmsTime));
        map.put(Constants.CONVERSATION_SHORT_DISPLAY_COUNT, "(" + this.smsDatas.size() + ")");
        return map;
    }

    public static List<Conversation> mergeConversations(List<Conversation> conv1, List<Conversation> conv2) {
        Map<String, Conversation> conversationMap = new HashMap<String, Conversation>();
        for (Conversation con : conv1) {
            conversationMap.put(con.getKey(), con);
        }
        for (Conversation con : conv2) {
            if (conversationMap.containsKey(con.getKey())) {
                Conversation originCon = conversationMap.get(con.getKey());
                originCon.setSnippet(con.getSnippet());
                originCon.setLastSmsTime(con.getLastSmsTime().after(originCon.getLastSmsTime()) ? con.getLastSmsTime() : originCon.getLastSmsTime());
                for (SmsData sms : con.getSmsDatas()) {
                    if (!originCon.getSmsDatas().contains(sms)) {
                        originCon.getSmsDatas().add(sms);
                    }
                }
            } else {
                conversationMap.put(con.getKey(), con);
            }
        }
        List<Conversation> mergedConversations = new ArrayList<Conversation>();
        mergedConversations.addAll(conversationMap.values());
        return mergedConversations;
    }
}
