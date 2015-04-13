package com.thoughtworks.cathywu.smsbackup.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.thoughtworks.cathywu.smsbackup.R;
import com.thoughtworks.cathywu.smsbackup.api.SmsReader;
import com.thoughtworks.cathywu.smsbackup.common.SMSUtils;
import com.thoughtworks.cathywu.smsbackup.model.Conversation;

import java.io.IOException;
import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    public void onReadSMS(View view) {

    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    public void onView(View view) {
        Intent intent = new Intent(this, SmsListActivity.class);
        String action = getIntent().getAction();
        intent.setAction(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
    }

    public void onSync(View view) {
        SmsReader smsReader = new SmsReader(getContentResolver());
        List<Conversation> conversations = smsReader.read();
        try {
            SMSUtils.backupSms(conversations);
            Log.i("onSync", "Backup successfully.");
            Toast.makeText(this, getString(R.string.tip_sync_successfully), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("onSync", "Exception occurs while writing file.", e);
        }
    }

    public void onExportSms(View view) {
        SmsReader smsReader = new SmsReader(getContentResolver());
        List<Conversation> conversations = smsReader.read();
        String jsonContent = new Gson().toJson(conversations);
        showFileChooser();
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_CHOOSER);
        intent.setType("");
        intent.addCategory(Intent.CATEGORY_BROWSABLE );
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
                    1);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
