package ru.vasilek.schedule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

public class tools {
	public static String MyTag = "tools_LOG";
	public static String imagefolder = "file:///data/data/ru.vasilek.schedule/files/";
	public static String getDeviceName() {
	    String manufacturer = Build.MANUFACTURER;
	    String model = Build.MODEL;
	    if (model.startsWith(manufacturer)) {
	        return capitalize(model);
	    } else {
	        return capitalize(manufacturer) + " " + model;
	    }
	}


	private static String capitalize(String s) {
	    if (s == null || s.length() == 0) {
	        return "";
	    }
	    char first = s.charAt(0);
	    if (Character.isUpperCase(first)) {
	        return s;
	    } else {
	        return Character.toUpperCase(first) + s.substring(1);
	    }
	} 
	
	public static int getCurrentDay(){
		Calendar calendar = Calendar.getInstance();
		int day = 1;
		switch (calendar.get(Calendar.DAY_OF_WEEK)){
	    	case Calendar.MONDAY: day = 1; break;
	    	case Calendar.TUESDAY: day = 2; break;
	    	case Calendar.WEDNESDAY: day = 3; break;
	    	case Calendar.THURSDAY: day = 4; break;
	    	case Calendar.FRIDAY: day = 5; break;        	
	    	case Calendar.SATURDAY: day = 6; break;
	    	case Calendar.SUNDAY: day = 7; break;
		}
		return day;
	}
	
	public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int day = 0;
        switch (calendar.get(Calendar.DAY_OF_WEEK)){
        	case Calendar.MONDAY: day = 1; break;
        	case Calendar.TUESDAY: day = 2; break;
        	case Calendar.WEDNESDAY: day = 3; break;
        	case Calendar.THURSDAY: day = 4; break;
        	case Calendar.FRIDAY: day = 5; break;        	
        	case Calendar.SATURDAY: day = 6; break;
        	case Calendar.SUNDAY: day = 7; break;
        }
        int weektype = calendar.get(Calendar.WEEK_OF_YEAR) % 2 + 1;
        int minute = calendar.get(Calendar.MINUTE);
        Log.d(MyTag, String.format("w=%d&d=%d&h=%02d&m=%02d",weektype, day, hour, minute));
        return String.format("w=%d&d=%d&h=%02d&m=%02d",weektype, day, hour, minute); // ЧЧ:ММ:СС - формат времени
    }
	static String inputStreamToString(InputStream is) throws IOException {
        String line = "";
        StringBuilder total = new StringBuilder();
 
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
 
        while ((line = rd.readLine()) != null)
            total.append(line);
 
        return total.toString();
    }
	
	public static int getCurrentWeekday(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.WEEK_OF_YEAR) % 2 + 1;
	}
		
	
	
	public static ArrayList<ScheduleItem> getScheduleListSchedule(Context context, boolean needPE){
		ArrayList<ScheduleItem> r = new ArrayList<ScheduleItem>();
		ScheduleItem[] it = getScheduleItems(context);
		int day = 0;
		for (int i = 0; i<it.length; i++){
			if (day != it[i].day){
				day = it[i].day;
				ScheduleItem sc = new ScheduleItem();
				sc.day = it[i].day;
				r.add(sc);
			}
			if (!needPE && it[i].lesson.toLowerCase().contains("физическая")){
				continue;
			}
			r.add(it[i]);
		}
		return r;
		
	}
	
	public static ArrayList<ScheduleItem> getScheduleList(Context context, boolean needPE){
		ArrayList<ScheduleItem> r = new ArrayList<ScheduleItem>();
		ScheduleItem[] it = getScheduleItems(context);
		int day = 0;
		for (int i = 0; i<it.length; i++){
			r.add(it[i]);
		}
		return r;
		
	}
	
	public static ScheduleItem[] getScheduleItems(Context context){
		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
        int curWeek = getCurrentWeekday();
        curWeek = (getCurrentDay() == 7) ? (curWeek % 2+1) : curWeek; 
        Cursor c = db.query("schedule", null, "(weektype=3) OR (weektype="+curWeek+")", null, null, null, "day, time_start");
        ScheduleItem[] items = new ScheduleItem[c.getCount()];
        int i = 0;
        if (c.moveToFirst()){
        	int iLesson = c.getColumnIndex("lesson");
            int iTeacher = c.getColumnIndex("teacher");
            int iDay = c.getColumnIndex("day");
            int iStart_time = c.getColumnIndex("time_start");
            int iEnd_time = c.getColumnIndex("time_end");
            int iWeekday = c.getColumnIndex("weektype");
            int iClassroom = c.getColumnIndex("classroom");
            int iType = c.getColumnIndex("type");    	    
            int iPhoto = c.getColumnIndex("photo");    	
            do{
            	int wd = c.getInt(iWeekday);
            	Log.d(MyTag, "item row: "+c.getPosition()+"; les="+c.getString(iLesson));
            	if ((wd == curWeek) || (wd == 3)){
            		
            		Log.d(MyTag, "Item "+i+" add");
            		items[i] = new ScheduleItem();
            		items[i].lesson = c.getString(iLesson);
            		items[i].teacher = c.getString(iTeacher);
            		items[i].day = c.getInt(iDay);
            		items[i].start_time = c.getInt(iStart_time);
            		items[i].end_time = c.getInt(iEnd_time);
            		items[i].classroom = c.getString(iClassroom);
            		items[i].type = c.getString(iType);
            		items[i].weektype = c.getInt(iWeekday);
            		items[i].image = c.getString(iPhoto);
            		i++;
            	}
            } while (c.moveToNext());  
        }
        return items;
	}
	
	public static String dayName(int day){
		String weekDay;
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		int offset = day-getCurrentDay();
		calendar.add(Calendar.HOUR_OF_DAY, 24*offset);
		weekDay = dayFormat.format(calendar.getTime());
		return capitalize(weekDay);
	}
	
	public static String smallDayName(int day){
		String weekDay;
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		int offset = day-getCurrentDay();
		calendar.add(Calendar.HOUR_OF_DAY, 24*offset);
		weekDay = dayFormat.format(calendar.getTime());
		return capitalize(weekDay);
	}
	
	public static ScheduleItem getCurrentLesson(ArrayList<ScheduleItem> items, boolean needPE){
		Calendar c = Calendar.getInstance();
		int time;
		int day = getCurrentDay();
		time = day*24*60+c.get(Calendar.HOUR_OF_DAY)*60+c.get(Calendar.MINUTE);
		
		int wt = getCurrentWeekday();		
		for (int i = 0; i < items.size(); i++){
			if (((items.get(i).weektype == 3) || (items.get(i).weektype == wt))){
				if ((items.get(i).day*24*60+items.get(i).start_time<=time) && (time<=items.get(i).day*24*60+items.get(i).end_time)){
					if (!needPE && items.get(i).lesson.toLowerCase().contains("физическая"))
						continue;
					return items.get(i);
				}
			}
		}
		return ScheduleItem.Null();
	}
	
	private static int getCurrentLessonIndex(ScheduleItem[] items){
		Calendar c = Calendar.getInstance();
		int time;
		time = c.get(Calendar.HOUR_OF_DAY)*60+c.get(Calendar.MINUTE);
		int wt = getCurrentWeekday();
		int day = getCurrentDay();
		for (int i = 0; i < items.length; i++){
			if ((items[i].day == day) && ((items[i].weektype == 3) || (items[i].weektype == wt))){
				if ((items[i].start_time<=time) && (time<=items[i].end_time)){
					return i;
				}
			}
		}
		return -1;
	}
	
	public static ScheduleItem getNextLesson(ArrayList<ScheduleItem> items, int day, int h, int m, int wt, boolean needPE){
		ScheduleItem item;
		int time;
		time = day*24*60+h*60+m;	
		Log.d(MyTag, "Time:" + time);	
		int mintime = 3600, minindex = 0;
		
		int i = 0;
		while ((i<items.size())){
			Log.d(MyTag, "item " + items.get(i).lesson+" "+(items.get(i).day*24*60+items.get(i).start_time));
			if ((items.get(i).weektype == 3) || (items.get(i).weektype == wt)){
				int t = items.get(i).day*24*60+items.get(i).start_time;
				if (t>time){
					if (!needPE && items.get(i).lesson.toLowerCase().contains("физическая")){
						i++;
						continue;
					}
					return items.get(i);
				}
			}
			i++;
		}
		wt = (wt==1)?2:1;
		i = 0;
		while ((i<items.size())){
			if ((items.get(i).weektype == 3) || (items.get(i).weektype == wt)){
				if (!needPE && items.get(i).lesson.toLowerCase().contains("физическая")){
					i++;
					continue;
				}
				return items.get(i);
			}
			i++;
		}
	
		return ScheduleItem.Null();
	}
	public static ScheduleItem getNextLesson(ArrayList<ScheduleItem> items, boolean needPE){
		Calendar c = Calendar.getInstance();
		int day = getCurrentDay();
		int wt = getCurrentWeekday();
		return getNextLesson(items, day, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), wt, needPE);
	}
		
	public static String getNextActivityText(ArrayList<ScheduleItem> items, boolean needPE){
		ScheduleItem cur, next;
		String s = "";
		cur = getCurrentLesson(items, needPE);
		next = getNextLesson(items, needPE);
		if (cur.day == 0){
			s = s + "Сейчас пар нет:)";
		} else {
			s = s + "Сейчас идёт "+cur.lesson+"("+cur.type+")"+"\n";
			s = s + "Ауд. " + cur.classroom + "\n";
			s = s + "Начало в " + cur.start_time / 60 + ":" + cur.start_time % 60 + "\n";
			s = s + cur.teacher + "\n";
		}
		s = s + "\n\n";
		s = s + "Следующая пара:\n"+dayName(next.day)+"\n"+next.lesson+"("+next.type+")"+"\n";
		s = s + "Ауд. " + next.classroom + "\n";
		s = s + "Начало в " + next.start_time / 60 + ":" + next.start_time % 60 + "\n";
		s = s + next.teacher + "\n";
		return s;
	}


	public static CharSequence getTimeStr(int start_time) {
		String s = "";
		s = String.format("%02d:%02d", start_time / 60, start_time % 60 );
		return s;
	}
	
}
