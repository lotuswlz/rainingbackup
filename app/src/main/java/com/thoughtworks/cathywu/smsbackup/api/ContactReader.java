package com.thoughtworks.cathywu.smsbackup.api;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ContactReader {

    private final String[] projection = new String[] { ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER};

    private Map<String, String> contactMaps;

    private static ContactReader contactReader = null;

    private ContactReader(ContentResolver contentResolver) {
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ?", new String[]{"1"}, null);
        contactMaps = new HashMap<String, String>();
        while (cursor.moveToNext()) {
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contactMaps.put(phoneNumber, displayName);
        }
        cursor.close();
    }

    public static ContactReader getInstance(ContentResolver contentResolver) {
        if (contactReader == null) {
            contactReader = new ContactReader(contentResolver);
        }
        return contactReader;
    }

    @Deprecated
    public List<String> readContacts(String phoneNumber) {
        return null;
    }

    public String findDisplayNameFor(String phoneNumber) {
        return contactMaps.get(phoneNumber);
    }
}
