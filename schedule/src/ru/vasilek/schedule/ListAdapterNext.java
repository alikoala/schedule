package ru.vasilek.schedule;

import java.util.ArrayList;

import android.R.color;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListAdapterNext extends BaseAdapter {

	private static final String Log_Tag = "Schedule_ListAdapter";
	Context ctx;
	LayoutInflater lInflater;
	ArrayList<ScheduleItem> objects;
	Resources rs;
	ListAdapterNext(Context context, ArrayList<ScheduleItem> items) {
	    ctx = context;
	    rs = ctx.getResources();
	    objects = items;
	    lInflater = (LayoutInflater) ctx
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	  // кол-во элементов
	  @Override
	  public int getCount() {
	    return objects.size();
	  }

	  // элемент по позиции
	  @Override
	  public Object getItem(int position) {
	    return objects.get(position);
	  }

	  // id по позиции
	  @Override
	  public long getItemId(int position) {
	    return position;
	  }

	  // пункт списка
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    // используем созданные, но не используемые view
	    View view = null;
	    Log.d(Log_Tag, "pos: "+String.valueOf(position));
	    ScheduleItem it = (ScheduleItem) getItem(position);
	    if (view == null){
	    	if (it.start_time != 0) {
	    		view = lInflater.inflate(R.layout.item, parent, false);
	    	} else {
	    		view = lInflater.inflate(R.layout.itemday, parent, false);   
	    	}
	    }
	    Log.d(Log_Tag, "1");
	    if (it == null || view == null)
	    	return view;
	    if (it.start_time != 0){
	    	Log.d(Log_Tag, "2");
		    TextView tv1 = (TextView) view.findViewById(R.id.lvDescription);
		    tv1.setTextColor(Color.BLACK);
		    tv1.setText(it.lesson);
		    Log.d(Log_Tag, "3");
		    tv1 = ((TextView) view.findViewById(R.id.lvTime));
		    tv1.setTextColor(Color.BLACK);
		    tv1.setText(tools.smallDayName(it.day)+", "+tools.getTimeStr(it.start_time)+" - "+tools.getTimeStr(it.end_time));
		    
		    tv1 = ((TextView) view.findViewById(R.id.lvClassroom));
		    tv1.setTextColor(Color.BLACK);
		    tv1.setText(it.classroom);
		    ImageView im = (ImageView) view.findViewById(R.id.lvImage);
	//	    im.setBackgroundColor(Color.GREEN);
		    im.setImageResource(rs.getIdentifier(it.image, "raw", ctx.getPackageName()));
	    } else {
	    	Log.d(Log_Tag, "4");
	    	TextView tv1 = (TextView) view.findViewById(R.id.lvDayName);
	    	if (position == 0)
	    		if (it.end_time == 1)
	    			tv1.setText(ctx.getString(R.string.nolessons));
	    		else
	    			tv1.setText(ctx.getString(R.string.currentLesson));
	    	else
	    		tv1.setText(ctx.getString(R.string.nextlesson));
	    }
	    Log.d(Log_Tag, "5");
	    return view;
	  }



}
