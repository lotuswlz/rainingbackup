package com.thoughtworks.cathywu.smsbackup.common;

import android.content.Context;
import android.util.Log;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.thoughtworks.cathywu.smsbackup.model.Conversation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Utils {

    public static String generateDateDisplayForSms(Date date) {
        return generateDateDisplayForSms(date, false);
    }

    public static String generateDateDisplayForSms(Date date, boolean isAccurate) {
        Date currentDate = new Date();
        SimpleDateFormat simpleDateFormat;
        if (currentDate.getTime() - date.getTime() >= 24 * 3600 * 1000) {
            String pattern = isAccurate ? "M/d/yyyy HH:mm" : "M/d/yyyy";
            simpleDateFormat = new SimpleDateFormat(pattern);
        } else {
            simpleDateFormat = new SimpleDateFormat("HH:mm");
        }
        return simpleDateFormat.format(date);
    }

    public static synchronized String generateJsonFileFrom(Context context, List<Conversation> conversations) throws IOException {
        Date currentDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        File exportFile = new File(context.getCacheDir(), simpleDateFormat.format(currentDate) + ".json");
        Files.write(new Gson().toJson(conversations), exportFile, Charset.defaultCharset());
        return exportFile.getAbsolutePath();
    }
}
