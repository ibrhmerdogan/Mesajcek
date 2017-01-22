package com.example.ibrhm.mesajcek;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends Activity {


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1905;
    private GoogleApiClient client;
    private GoogleApiClient client2;


    TextView mesajal, yedek, yedek1;
    Cursor cursor;
    String msgData = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mesajal = (TextView) findViewById(R.id.mesaj);
        yedek1 = (TextView) findViewById(R.id.textView2);
        if (!checkAndRequestPermissions()) {//Manifest permission
            return;
        }
        smsFunction();//read sms
       // Call();//read call log

        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();



    }




    public void smsFunction(){
        Map<Integer, List<Sms>> smsMap = getAllSms();

        for (Map.Entry<Integer, List<Sms>> entry : smsMap.entrySet()) {
            Log.d("sms_sample", String.format("Month %d: %d sms", entry.getKey(), entry.getValue().size()));
            String mesaj = smsMap.toString();
            mesajal.setText(mesaj);

        }

    }
    /*public void Call(){
        yedek = (TextView) findViewById(R.id.textView);
        Cursor cursor1 = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = cursor1.getColumnIndex(CallLog.Calls.NUMBER);
        int date = cursor1.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor1.getColumnIndex(CallLog.Calls.DURATION);
        int type = cursor1.getColumnIndex(CallLog.Calls.TYPE);
        StringBuilder sb = new StringBuilder();

        while (cursor1.moveToNext()) {

            String pnumber = cursor1.getString(number);
            String callduration = cursor1.getString(duration);
            String calltype = cursor1.getString(type);
            String calldate = cursor1.getString(date);

            Date date1 = new Date(Long.valueOf(calldate));


            String callTypeStr = "";
            switch (Integer.parseInt(calltype)) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callTypeStr = "OutgoingCall";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callTypeStr = "IncomingCall";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callTypeStr = "missedCall";
                    break;
            }
            sb.append("PhoneNumber:" + pnumber);
            sb.append("CallDate:" + date1);
            sb.append("CallDuration:" + callduration);
            sb.append("CallType:" + callTypeStr);
            sb.append("****************");
            sb.append(System.getProperty("line.seperator"));


        }

        yedek.setText(sb.toString());
}

*/
    public Map<Integer, List<Sms>> getAllSms() {
        Map<Integer, List<Sms>> smsMap = new TreeMap<Integer, List<Sms>>();
        Sms objSms = null;
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = getContentResolver();
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);

        Cursor c = cr.query(message, null, null, null, null);
       startManagingCursor(c);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                objSms = new Sms();
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
                objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.setReadState(c.getString(c.getColumnIndex("read")));
                objSms.setTime(c.getLong(c.getColumnIndexOrThrow("date")));

                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolderName("inbox");
                } else {
                    objSms.setFolderName("sent");
                }

                cal.setTimeInMillis(objSms.getTime());
                int month = cal.get(Calendar.MONTH);

                if (!smsMap.containsKey(month))
                    smsMap.put(month, new ArrayList<Sms>());

                smsMap.get(month).add(objSms);

                c.moveToNext();
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c.close();

        return smsMap;
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        AppIndex.AppIndexApi.start(client2, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client2, getIndexApiAction());
        client2.disconnect();
    }
//////////////////////////////////////////////////////////////////////////////////////////

    private boolean checkAndRequestPermissions() {
        int permissionINTERNET = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);
        int permissionACCESS_NETWORK_STATE = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE);
        int permissionACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
       // int permissionACCESS_VIBRATE = ContextCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE);
       // int permissionCAMERA = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
      //  int permissionACCESS_LOCATION_EXTRA_COMMANDS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS);
        int permissionACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
       // int permissionWRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionACCESS_WIFI_STATE = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_WIFI_STATE);
       // /int permissionCHANGE_WIFI_STATE = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CHANGE_WIFI_STATE);
       int permissionREAD_PHONE_STATE = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
      //  int permissionRECEIVE_BOOT_COMPLETED = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_BOOT_COMPLETED);
       // int permissionBLUETOOTH_ADMIN = ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN);
       // int permissionBLUETOOTH = ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH);
       int permissionREADSMS=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int permissionREADCALLLOG= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
       int permissionREADCALENDAR=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        List<String> listPermissionsNeeded = new ArrayList<>();
       if (permissionREADSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionREADCALLLOG!= PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG);
        }
        if (permissionREADCALENDAR != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALENDAR);
        }

     if (permissionACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
     }
        if (permissionINTERNET != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }
        if (permissionACCESS_NETWORK_STATE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
         if (permissionACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionACCESS_WIFI_STATE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
         if (permissionREAD_PHONE_STATE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

}
