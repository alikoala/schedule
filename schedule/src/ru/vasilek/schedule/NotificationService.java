package ru.vasilek.schedule;

import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NotificationService extends Service {
	private static String MyTag = "NotificationService_LOG";
	NotificationManager nm;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	public void onCreate() {
	    super.onCreate();
	    nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(MyTag, "Start Service");
		try {
	      TimeUnit.SECONDS.sleep(5);
	    } catch (InterruptedException e) {
	      e.printStackTrace();
	    }
	    
	    sendNotif();
	    return super.onStartCommand(intent, flags, startId);
	  }
	
	@SuppressWarnings("deprecation")
	void sendNotif() {
	    // 1-� �����
	    Notification notif = new Notification(R.drawable.logo, "����� ����!", 
	      System.currentTimeMillis());
	    
	    // 3-� �����
	    Intent intent = new Intent(this, MainActivity.class);
	    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
	    
	    // 2-� �����
	    notif.setLatestEventInfo(this, "�������", "���. 1023", pIntent);
	    
	    // ������ ����, ����� ����������� ������� ����� �������
	    notif.flags |= Notification.FLAG_AUTO_CANCEL;
	    
	    // ����������
	    nm.notify(1, notif);
	  }

}
