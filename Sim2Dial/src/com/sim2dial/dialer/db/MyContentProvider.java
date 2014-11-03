package com.sim2dial.dialer.db;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider{

	private static final int History=1;
	private static final int History_Id=2;
	private static final int History_distinct=3;
	private static final String Authority="com.sim2dial.dialer.contentprovider";
	private static final String Base_Path="historyStatus";
	
	public static final Uri Content_Uri=Uri.parse("content://"+Authority+"/"+Base_Path);
	public static final Uri Distinct_Uri=Uri.parse("content://"+Authority+"/"+Base_Path+".distinct");
		
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
		      + "/historyStatus";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
      + "/historyStatus";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
      sURIMatcher.addURI(Authority, Base_Path, History);
      sURIMatcher.addURI(Authority, Base_Path + "/#", History_Id);
      sURIMatcher.addURI(Authority, Base_Path + ".distinct", History_distinct);
    }
    
    private HistoryStatusHelper datab;
    
    @Override
	public boolean onCreate() {
		datab=new HistoryStatusHelper(getContext());
		return false;
	}
    
    @Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
    	
    	// Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        boolean useDistinct=false;
        String groupby=null;	
        
        // Check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(HistoryStatus.Table_Name);
        SQLiteDatabase db = datab.getWritableDatabase();
        
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
        case History:
          break;
        case History_Id:
          // Adding the ID to the original query
          queryBuilder.appendWhere(HistoryStatus.Column_ID + "="
              + uri.getLastPathSegment());
          break;
        case History_distinct:
        	//useDistinct=true;
        	groupby=HistoryStatus.Column_No;
        	break;
        default:
          throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        
       // queryBuilder.setDistinct(useDistinct);
        
        Cursor cursor = queryBuilder.query(db, projection, selection,
            selectionArgs, groupby, null, sortOrder);
        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
	}
    
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = datab.getWritableDatabase();
	    int rowsDeleted = 0;
	    switch (uriType) {
	    case History:
	      rowsDeleted = sqlDB.delete(HistoryStatus.Table_Name, selection,
	          selectionArgs);
	      break;
	    case History_Id:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsDeleted = sqlDB.delete(HistoryStatus.Table_Name,
	        		HistoryStatus.Column_ID + "=" + id, 
	            null);
	      } else {
	        rowsDeleted = sqlDB.delete(HistoryStatus.Table_Name,
	        		HistoryStatus.Column_ID + "=" + id 
	            + " and " + selection,
	            selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsDeleted;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		  int uriType = sURIMatcher.match(uri);
		    SQLiteDatabase sqlDB = datab.getWritableDatabase();
		    int rowsDeleted = 0;
		    long id = 0;
		    switch (uriType) {
		    case History:
		      id = sqlDB.insert(HistoryStatus.Table_Name, null, values);
		      break;
		    default:
		      throw new IllegalArgumentException("Unknown URI: " + uri);
		    }
		    getContext().getContentResolver().notifyChange(uri, null);
		    return Uri.parse(Base_Path + "/" + id);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = datab.getWritableDatabase();
	    int rowsUpdated = 0;
	    switch (uriType) {
	    case History:
	      rowsUpdated = sqlDB.update(HistoryStatus.Table_Name, 
	          values, 
	          selection,
	          selectionArgs);
	      break;
	    case History_Id:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsUpdated = sqlDB.update(HistoryStatus.Table_Name, 
	            values,
	            HistoryStatus.Column_ID + "=" + id, 
	            null);
	      } else {
	        rowsUpdated = sqlDB.update(HistoryStatus.Table_Name, 
	            values,
	            HistoryStatus.Column_ID + "=" + id 
	            + " and " 
	            + selection,
	            selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	}
	
	private void checkColumns(String[] projection) {
	    String[] available = { HistoryStatus.Column_ID,HistoryStatus.Column_Name,HistoryStatus.Column_Date,
	        HistoryStatus.Column_Duration,HistoryStatus.Column_No,HistoryStatus.Column_Status,
	        HistoryStatus.Column_Time,HistoryStatus.Column_Timestamp};
	    
	    if (projection != null) {
	      HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
	      HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
	      // Check if all columns which are requested are available
	      if (!availableColumns.containsAll(requestedColumns)) {
	        throw new IllegalArgumentException("Unknown columns in projection");
	      }
	    }
	  }

}
