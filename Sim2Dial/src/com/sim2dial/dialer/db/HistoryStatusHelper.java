package com.sim2dial.dialer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryStatusHelper extends SQLiteOpenHelper {

	private static final String DB_Name="Sim2Dial";
	private static final int DB_Version=2;
	
	public HistoryStatusHelper(Context context) {
		super(context, DB_Name, null, DB_Version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		HistoryStatus.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		HistoryStatus.onUpgrade(db, oldVersion, newVersion);
	}

}
