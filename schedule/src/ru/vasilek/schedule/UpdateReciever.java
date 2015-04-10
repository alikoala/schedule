package ru.vasilek.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class UpdateReciever extends BroadcastReceiver {

	private static final String MyTag = "UpdateReciever_LOG";

	@Override
	public void onReceive(Context context, Intent intent) {
		 Log.d(MyTag, "onReceiveBroadcast");
		 Log.d(MyTag, "action = " + intent.getAction());
		 final Context ctx = context;
		 new Thread(new Runnable() {
				
				@Override
				public void run() {
					UpdateService.UpdateBD(ctx);
					
				}
			}).start();		 
	}


}
