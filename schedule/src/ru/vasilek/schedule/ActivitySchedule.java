package ru.vasilek.schedule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ActivitySchedule extends Activity implements OnClickListener {
	
	
	private static final String MyTag = "ActivitySchedule_LOG";
	private Handler h;
//	private TextView tv;
	private ArrayList<ScheduleItem> items;
	private int curday = 1;
	private Button button_back;
	private Button button_forward;
	DBHelper dbHelper;
	SQLiteDatabase db;
	SharedPreferences settings;
	private boolean needPE; 
	ListAdapter la;
	
	private void getInfoFromCache(){
		
		items = tools.getScheduleListSchedule(getApplicationContext(), needPE);
	}
	
	//TODO: Выводим и не сохраняем в кэш
	private void getInfoFromInternet(){
		HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://bpi1401.esy.es/utils/near.php?"+tools.getCurrentTime());
        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("model", tools.getDeviceName()));
        nameValuePairs.add(new BasicNameValuePair("action","1"));
        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }    	        
        
        try {
        	Log.d(MyTag,"ScheduleActivity before execute");
            HttpResponse response = httpclient.execute(httppost);
            Log.d(MyTag,"ScheduleActivity after execute");
            String request = tools.inputStreamToString(response.getEntity().getContent());
            Log.v(MyTag, request + "!");
            String[] res = request.split("#");
            items = new ArrayList<ScheduleItem>();

            for (int i = 0; i<res.length; i++){
            	String[] s = res[i].split(";");
            	Log.d(MyTag,"processing "+i+"res[i]="+res[i]);
            	Log.d(MyTag,"processing "+i+". Length="+s.length);
            	items.add(new ScheduleItem(s));
            }
            
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    
	}
	
	//Принимаем данные от Update сервиса
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(MyTag, "ActivityResult: " + resultCode);
		if (requestCode==MainActivity.UPDATE_OK){
			Editor edit = settings.edit();
			edit.putBoolean(MyPreferenceActivity.SETTINGS_CACHE_EXIST, true);
			edit.commit();
			getInfoFromCache();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void getInfo(){
    	new Thread(new Runnable() {    		 
    	    @Override
    	    public void run() {
    	    	boolean cacheauto = settings.getBoolean(MyPreferenceActivity.SETTINGS_CACHE_AUTO, false);
    	    	boolean cacheexist = settings.getBoolean(MyPreferenceActivity.SETTINGS_CACHE_EXIST, false);
    	        Log.d(MyTag, "GetInfo" +String.format("; auto:%b; exist:%b", cacheauto, cacheexist));
    	    	if (cacheauto && cacheexist){
    	    		Log.d(MyTag, "GetInfoFromCache");
    	        	getInfoFromCache();
    	    	} else if (cacheauto && !cacheexist){
    	    		Log.d(MyTag, "CreateCache!");
    	    		UpdateService.UpdateBD(getApplicationContext());
    	    		getInfoFromCache();
    			} else {
    	    		Log.d(MyTag, "getInfoFromInternet");
    	        	getInfoFromInternet();
    	    	}
    	    	curday = tools.getCurrentDay();
    	    	curday = (curday == 7) ? 1 : curday;
    	    	Message m = new Message();
    	        Bundle b = new Bundle();
    	        m.setData(b);
    	        h.sendMessage(m);
    	    }
    	 
    	    
    	 
    	}).start();
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);
//		tv = (TextView) findViewById(R.id.schedule_textview1);
//		tv.setMovementMethod(new ScrollingMovementMethod());
		Log.d(MyTag,"ScheduleActivity Runned");
//		button_back = (Button) findViewById(R.id.schedule_back);
//		button_back.setActivated(false);
//		button_back.setClickable(false);
//		button_forward = (Button) findViewById(R.id.schedule_forward);
//		button_back.setOnClickListener(this);
//		button_forward.setOnClickListener(this);
		h = new Handler() {
            public void handleMessage(android.os.Message msg) {
//              tv.setText(tools.getOneDayText(items, curday,needPE));
              
              
              la = new ListAdapter(getApplicationContext(), items);
              // настраиваем список
              ListView lvMain = (ListView) findViewById(R.id.schedule_lvMain);
              lvMain.setAdapter(la);
              
              Log.d(MyTag,"NextActivity Set Text");
//              tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            };
        };
        dbHelper = new DBHelper(this);
		db = dbHelper.getWritableDatabase();
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		needPE = settings.getBoolean(MyPreferenceActivity.SETTINGS_VIEW_NEEDPE, true);
        getInfo();
  
        
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.schedule_back:{
				Log.d(MyTag, "<<<<");
				curday--;
				button_forward.setActivated(true);
				button_forward.setClickable(true);
				if (curday == 1){
					button_back.setActivated(false);
					button_back.setClickable(false);
				}
//				tv.setText(tools.getOneDayText(items, curday,needPE));
//				tv.scrollTo(0, 0);
				break;
			}
			case R.id.schedule_forward:{
				Log.d(MyTag, ">>>>");
				curday++;
				button_back.setActivated(true);
				button_back.setClickable(true);
				if (curday == 7){
					button_forward.setActivated(false);
					button_forward.setClickable(false);
				}
//				tv.setText(tools.getOneDayText(items, curday,needPE));
//				tv.scrollTo(0, 0);
				break;
			}
		}
	}

}
