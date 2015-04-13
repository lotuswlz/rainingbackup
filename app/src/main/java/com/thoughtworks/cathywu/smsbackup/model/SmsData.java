package com.thoughtworks.cathywu.smsbackup.model;

import java.io.Serializable;
import java.util.Date;

public class SmsData implements Comparable<SmsData>, Serializable {

    private static final long serialVersionUID = 1L;

    private String phoneNumber;
    private Date date;
    private String content;
    private SmsType type;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(int value) {
        this.type = SmsType.fromValue(value);
    }

    public boolean isSent() {
        return this.type == SmsType.SENT;
    }

    public boolean isIn() {
        return this.type == SmsType.INBOX;
    }

    @Override
    public int compareTo(SmsData another) {
        if (another == null) {
            return 1;
        }
        if (this.getDate() == null || another.getDate() == null) {
            return 0;
        }
        return -1 * this.getDate().compareTo(another.getDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SmsData)) return false;

        SmsData smsData = (SmsData) o;

        if (content != null ? !content.equals(smsData.content) : smsData.content != null)
            return false;
        if (!date.equals(smsData.date)) return false;
        if (!phoneNumber.equals(smsData.phoneNumber)) return false;
        if (type != smsData.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = phoneNumber.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + type.hashCode();
        return result;
    }
}
