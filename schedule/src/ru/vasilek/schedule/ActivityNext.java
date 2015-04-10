package ru.vasilek.schedule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityNext extends Activity{

	private static final String MyTag = "ActivityNext_LOG";
	private Handler h;
	private TextView tv;
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	private SharedPreferences settings;
	private boolean needPE;
	LinearLayout main; 
	protected ListAdapterNext la;
	protected ArrayList<ScheduleItem> items;
	
	
	public String getInfoFromInternet(){
		HttpClient httpclient = new DefaultHttpClient();
    	HttpPost httppost = new HttpPost("http://bpi1401.esy.es/utils/near.php?"+tools.getCurrentTime());
   	 	List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
    	nameValuePairs.add(new BasicNameValuePair("model", tools.getDeviceName()));
    	nameValuePairs.add(new BasicNameValuePair("action","0"));
    	try {
    		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
    	} catch (UnsupportedEncodingException e) {
    		e.printStackTrace();
    	}
    	 
    	try {
    		Log.d(MyTag,"NextActivity before execute");
    	    HttpResponse response = httpclient.execute(httppost);
    	    Log.d(MyTag,"NextActivity after execute");
    	    String request = tools.inputStreamToString(response.getEntity().getContent());
    	    Log.v(MyTag, request + "!");
    	    return request;
//    	            tv.setText(request);
    	} catch (ClientProtocolException e) {
    	    e.printStackTrace();
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    	return null;
    }
	
	private void getInfoFromCache(){
		ArrayList<ScheduleItem> lst = tools.getScheduleList(getApplicationContext(), needPE);
		items = new ArrayList<ScheduleItem>();
		
		ScheduleItem tmp = tools.getCurrentLesson(lst, needPE);
		if (tmp.start_time == 0){
			tmp.end_time = 1;
			items.add(tmp);
		} else {
			items.add(new ScheduleItem());
			items.add(tmp);
		}		
		items.add(new ScheduleItem());
		tmp = tools.getNextLesson(lst, needPE);
		items.add(tmp);	
	}
	
	public void getInfo(){
		new Thread(new Runnable() {    		 
    	    @Override
    	    public void run() {
    	    	boolean cacheauto = settings.getBoolean(MyPreferenceActivity.SETTINGS_CACHE_AUTO, false);
    	    	boolean cacheexist = settings.getBoolean(MyPreferenceActivity.SETTINGS_CACHE_EXIST, false);
    	        Log.d(MyTag, "GetInfo");
    	        Message m = new Message();
    	        Bundle b = new Bundle();
    	        if (cacheauto && cacheexist){
    	    		Log.d(MyTag, "GetInfoFromCache");
    	        	getInfoFromCache();
    	    	} else if (cacheauto && !cacheexist){
    	    		Log.d(MyTag, "CreateCache!");
    	    		UpdateService.UpdateBD(getApplicationContext());
    	    		getInfoFromCache();
    			} else {
    	    		Log.d(MyTag, "getInfoFromInternet");
    	        	b.putString("info", getInfoFromInternet());
    	    	}
    	    	
    	        m.setData(b);
    	        h.sendMessage(m);
    	    }
    	 
    	    
    	 
    	}).start();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_next);
		
		Log.d(MyTag,"NextActivity Runned");
		h = new Handler() {
            public void handleMessage(android.os.Message msg) {
            	la = new ListAdapterNext(getApplicationContext(), items);
                // настраиваем список
                ListView lvMain = (ListView) findViewById(R.id.next_lvMain);
                lvMain.setAdapter(la);
//              tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            };
        };
        main = (LinearLayout) findViewById(R.id.next_mainlayout);
        dbHelper = new DBHelper(this);
		db = dbHelper.getWritableDatabase();
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		needPE = settings.getBoolean(MyPreferenceActivity.SETTINGS_VIEW_NEEDPE, true);
        getInfo();
	}


}
