package ru.vasilek.schedule;

import android.media.Image;

public class ScheduleItem{
	public String lesson;
	public String teacher;
	public int start_time;
	public int end_time;
	public String type;
	public String classroom;
	public int day;
	public int weektype;
	public String image;
	public ScheduleItem(String[] s){
		lesson = s[0];
		teacher = s[1];
		start_time = Integer.parseInt(s[2]);
		end_time = Integer.parseInt(s[3]);
		type = s[4];
		classroom = s[5];
		day = Integer.parseInt(s[6]);
		
	}
	public ScheduleItem(){
		lesson = "";
		teacher = "";
		start_time = 0;
		end_time = 0;
		type = "";
		classroom = "";
		day = 0;
		weektype = 3;
	}
	public static ScheduleItem Null(){
		return new ScheduleItem();
	}
	
	
	public String getText(){
		String s = "";
		s = tools.dayName(day) + "\n" + start_time / 60 + ":" + start_time % 60 + "\n";
		s = s + lesson + " " + type + "\n";
		s = s + "¿Û‰. " + classroom + ".\n" + teacher;
		return s;
		
	}
}