package com.sim2dial.dialer.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HistoryStatus {

	//Database table
	
	public static final String Table_Name="callhistory";
	public static final String Column_ID="_id";
	public static final String Column_Name="name";
	public static final String Column_No="number";
	public static final String Column_Time="time";
	public static final String Column_Status="status";
	public static final String Column_Date="date";
	public static final String Column_Duration="duration";
	public static final String Column_Timestamp="timestamp";
	
	// Database creation SQL statement
	  private static final String DATABASE_CREATE = "create table " 
	      + Table_Name
	      + "(" 
	      + Column_ID + " integer primary key autoincrement, " 
	      + Column_Name + " text not null, " 
	      + Column_No + " text not null," 
	      + Column_Time + " text not null,"
	      + Column_Status + " text not null,"
	      + Column_Date + " text not null,"
	      + Column_Duration + " text not null,"
	      + Column_Timestamp + " text not null"
	      + ");";
	  
	  public static void onCreate(SQLiteDatabase database) {
		    database.execSQL(DATABASE_CREATE);
	  }

	  public static void onUpgrade(SQLiteDatabase database, int oldVersion,
	      int newVersion) {
	    Log.w(HistoryStatus.class.getName(), "Upgrading database from version "
	        + oldVersion + " to " + newVersion
	        + ", which will destroy all old data");
	    database.execSQL("DROP TABLE IF EXISTS " + Table_Name);
	    onCreate(database);
	  }
	  
}
