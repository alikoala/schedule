package ru.vasilek.schedule;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class UpdateService extends Service {

	private static final String MyTag = "UpdateService_LOG";
	
	DBHelper dbHelper;
	SharedPreferences settings;
	Handler uiHandler;
	
	public static String getData() throws ClientProtocolException, IOException{
		HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://bpi1401.esy.es/utils/cache.php?");
        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("model", tools.getDeviceName()));
        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
 
        HttpResponse response = httpclient.execute(httppost);
        return tools.inputStreamToString(response.getEntity().getContent());
	}
	
	public static void writeData(DBHelper hl){
		try {
			String cache = getData();
			SQLiteDatabase db = hl.getWritableDatabase();
			Log.d(MyTag, "Cache: "+ cache);
			db.delete("schedule", null, null);
			db.execSQL(cache);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void getPhotos(Context context){
		
	}
	
	public static void UpdateBD(Context context){
		DBHelper dbHelper = new DBHelper(context);
		Log.d(MyTag, "Manual Updating");
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		writeData(dbHelper);
		Editor edit = settings.edit();
		edit.putBoolean(MyPreferenceActivity.SETTINGS_CACHE_EXIST, true);
		Calendar c = Calendar.getInstance();
		edit.putLong(MyPreferenceActivity.SETTINGS_LAST_UPDATE, c.getTimeInMillis());
		edit.commit();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		UpdateBD(getApplicationContext());
		Log.d(MyTag, "Alarm Updating");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
