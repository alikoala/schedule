package ru.vasilek.schedule;



import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private static final int MENU_SITE = 0;
	private static final int MENU_SETTINGS = 1;	
	private static final int MENU_EXIT = 2;	
	public static int UPDATE_OK=777;
	public static int UPDATE_BAD=666;
	public static int UPDATE_COMMAND_CODE=1;
	public AlarmManager alarmManager;
	PendingIntent alarmIntent;
	private static String MyTag = "MainActivity_LOG";
    public RelativeLayout lmain;
    TextView tv;
    ImageView image1;
    Button b1;
	Button b2;
	SharedPreferences settings;
	Handler h;
	
	public void setAlarm(){
		if (alarmManager != null){
			return;
		}
		if (!settings.getBoolean(MyPreferenceActivity.SETTINGS_CACHE_AUTO, true))
			return;
		Log.d(MyTag, "Setting Alarm!!");
		alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), UpdateReciever.class);
		alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
		long time = Long.parseLong(settings.getString(MyPreferenceActivity.SETTINGS_CACHE_TIME, ""));
		//time в часах, а нужен в мс. Умножаем на 1000*60*60
		time = time * 1000 * 60 * 60;
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()-time,
		        time, alarmIntent);
	}
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1 = (Button) findViewById(R.id.button1);
        b1.setOnClickListener(this);
        b2 = (Button) findViewById(R.id.buttonweek);
        b2.setOnClickListener(this);
        image1 = (ImageView) findViewById(R.id.imageView1);
        image1.setOnClickListener(this);
        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Log.d(MyTag, "MainActivity Create");
        long lastupdate = settings.getLong(MyPreferenceActivity.SETTINGS_LAST_UPDATE, 0);
        h = new Handler(){
            public void handleMessage(android.os.Message msg) {
            	image1.clearAnimation();
            	Toast.makeText(getApplicationContext(), "Данные загружены", Toast.LENGTH_LONG).show();
            }
		};
		Log.d(MyTag, "Before");
        if (lastupdate==0){
        	Editor edit = settings.edit();
        	edit.putBoolean(MyPreferenceActivity.SETTINGS_CACHE_AUTO, true);
        	edit.putString(MyPreferenceActivity.SETTINGS_CACHE_TIME, "1");
        	edit.commit();
        }
        Log.d(MyTag, "After");
        
        setAlarm();
//        startService(new Intent(this, UpdateService.class));
    }


    @Override
	public void onBackPressed() {
//    	Toast.makeText(getApplicationContext(), "No", Toast.LENGTH_SHORT).show();
		super.onBackPressed();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(0,MENU_EXIT,MENU_EXIT, getString(R.string.menu_label_exit));
        menu.add(0,MENU_SITE,MENU_SITE, getString(R.string.menu_label_site));
        menu.add(0,MENU_SETTINGS, MENU_SETTINGS, getString(R.string.menu_label_preferences));
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case MENU_EXIT:{
				finish();
				break;
			}
			case MENU_SITE:{
				Uri uri = Uri.parse("http://bpi1401.esy.es/");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				break;
			}
			case MENU_SETTINGS:{
				Intent intent = new Intent(this, MyPreferenceActivity.class);
				startActivity(intent);
				break;
			}
		}
		return super.onOptionsItemSelected(item);		
		
	}


	@Override
	protected void onResume() {
		super.onResume();
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
			case R.id.button1: {
				Intent intent = new Intent(this, ActivityNext.class);
			    startActivity(intent);
				break;
			}
			case R.id.buttonweek: {
				Intent intent = new Intent(this, ActivitySchedule.class);
			    startActivity(intent);
				break;
			}
			case R.id.imageView1: {
				Log.d(MyTag, "Animation.start");
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.loading);
				image1.startAnimation(animation);
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						UpdateService.UpdateBD(getApplicationContext());
						Message m = new Message();
		    	        Bundle b = new Bundle();
		    	        m.setData(b);
		    	        h.sendMessage(m);					
					}
				}).start();
				
			}
		
		}
	}
    
}
