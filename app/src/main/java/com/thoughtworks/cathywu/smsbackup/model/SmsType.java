package com.thoughtworks.cathywu.smsbackup.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum SmsType implements Serializable {
    INBOX(1), SENT(2);

    private int value;
    private SmsType(int val) {
        this.value = val;
    }

    public int getValue() {
        return value;
    }

    private static Map<Integer, SmsType> valueMap = new HashMap<Integer, SmsType>();
    static {
        for (SmsType type : SmsType.values()) {
            valueMap.put(type.getValue(), type);
        }
    }

    public static SmsType fromValue(int val) {
        if (!valueMap.containsKey(val)) {
            throw new RuntimeException("No such sms type with value:" + val);
        }
        return valueMap.get(val);
    }
}
