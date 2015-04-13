package com.thoughtworks.cathywu.smsbackup.common;

import android.os.Environment;
import android.util.Log;

import com.thoughtworks.cathywu.smsbackup.model.Conversation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public final class SMSUtils {

    private static final String SD_CARD_PATH;
    static {
        File sdCard = Environment.getExternalStorageDirectory();
        SD_CARD_PATH = sdCard.getAbsolutePath();
        createAppDir();
    }

    public static synchronized void backupSms(List<Conversation> conversations) throws IOException {
        File existSmsFile = new File(getSmsFilePath());
        List<Conversation> existConversations = null;
        if (existSmsFile.exists()) {
            existConversations = readExistSms(existSmsFile);
        }
        List<Conversation> allConversations;
        if (existConversations != null) {
            allConversations = Conversation.mergeConversations(existConversations, conversations);
        } else {
            allConversations = conversations;
        }

        writeSmsToSDCard(allConversations);
    }

    public static List<Conversation> readSmsFromStorage() throws IOException {
        File existSmsFile = new File(getSmsFilePath());
        return readExistSms(existSmsFile);
    }

    private static void createAppDir() {
        File dir = new File(SD_CARD_PATH + File.separator + Constants.BACKUP_APP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Log.i("SMSUtils", "SD Card state:" + Environment.getExternalStorageState());
        Log.i("SMSUtils", "Is SD Card removed: " + String.valueOf(Environment.isExternalStorageRemovable()));
        Log.i("SMSUtils", "Dir: " + dir.getAbsolutePath());
    }

    private static void writeSmsToSDCard(List<Conversation> conversations) throws IOException {
        File existSmsFile = new File(getSmsFilePath());
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(existSmsFile));
        oos.writeObject(conversations);
        oos.close();
    }

    private static List<Conversation> readExistSms(File existSmsFile) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(existSmsFile));
        try {
            return (List<Conversation>) ois.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("SMSUtils", "Exist SMS read failure.");
            Log.e("SMSUtils", e.getMessage(), e);
        } finally {
            ois.close();
        }
        return null;
    }

    private static String getSmsFilePath() {
        return SD_CARD_PATH + File.separator + Constants.BACKUP_APP_DIR + File.separator + Constants.BACKUP_FILE_NAME;
    }
}
