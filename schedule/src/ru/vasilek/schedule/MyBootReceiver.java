package ru.vasilek.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBootReceiver extends BroadcastReceiver {
	private static String MyTag = "MyBootReciever_LOG";
	@Override
	public void onReceive(Context context, Intent intent) {
	    Log.d(MyTag, "onReceive " + intent.getAction());
//	    context.startService(new Intent(context, NotificationService.class));
	}
	

}
