package ru.vasilek.schedule;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class MyPreferenceActivity extends PreferenceActivity{

	public static String SETTINGS_CACHE_EXIST = "cache_exist";
	public static String SETTINGS_CACHE_AUTO = "cache_auto";
	public static String SETTINGS_CACHE_TIME = "cache_update_time";
	public static String SETTINGS_NOTIFICATIONS_TIME = "notifications_time";
	public static String SETTINGS_NOTIFICATIONS_ON = "notifications_on";
	public static String SETTINGS_NOTIFICATIONS_SOUND = "notifications_sound";
	public static String SETTINGS_NOTIFICATIONS_VIBRATION = "notifications_vibration";
	public static String SETTINGS_LAST_UPDATE = "lastupdate";
	public static String SETTINGS_VIEW_NEEDPE = "view_needPE";
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.activity_preferences);
		
		
	}

}
