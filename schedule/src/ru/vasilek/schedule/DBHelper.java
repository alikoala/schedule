package ru.vasilek.schedule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private static String MyTag = "DBHelper_LOG";
	public DBHelper(Context context) {
	      super(context, "myDB", null, 2);
	    }

	    @Override
	    public void onCreate(SQLiteDatabase db) {
	      Log.d(MyTag, "--- onCreate database ---");
	      // создаем таблицу с полями
	      db.execSQL("create table schedule ("
	          + "id integer primary key," 
	          + "lesson text,"
	          + "teacher text,"
	          + "time_start integer,"
	          + "time_end integer,"
	          + "type text,"
	          + "classroom text,"
	          + "day int,"
	          + "weektype int," 
	          + "photo text"+ ");");
	    }

	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    }
	  

}
