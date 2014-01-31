package com.pataelmo.isycontroller;


import java.util.ArrayList;
import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	static SQLiteDatabase mDb = null;
	
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "isy";
    
	//isyNode table
	// _id - row id for linking tables in future
	// name - Unit name  eg ("Lamp","Scene A","1st Floor")
	// type - Item type eg ("Node","Scene","Folder")
	// address - item address ("xx xx xx xx","xxxx","xxxx")
	// value - Items' last known state (0-99,0-255,"On","Off","")
	// parent - Address of parent ("xxxx","")

	private static final String NODES_TABLE_NAME = "isyNodes";
	
	static final String KEY_ROWID 			= "_id";		// database row_id

	static final String KEY_NAME	        = "name";
	static final String KEY_TYPE	        = "type";
	static final String KEY_ADDRESS			= "address";
	static final String KEY_VALUE			= "value";
	static final String KEY_RAW_VALUE		= "rawValue";
	static final String KEY_PARENT			= "parent";
	

    static final String CREATE_NODES_TABLE  = 
			 "CREATE TABLE " + NODES_TABLE_NAME + "( " 
			+ KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_NAME + " TEXT NOT NULL,"
			+ KEY_TYPE + " TEXT NOT NULL,"
			+ KEY_ADDRESS + " TEXT NOT NULL,"
			+ KEY_VALUE + " TEXT,"
		    + KEY_RAW_VALUE + " TEXT,"
			+ KEY_PARENT  + " TEXT);";
    static final String DROP_NODES_TABLE = "DROP TABLE IF EXIXTS " + NODES_TABLE_NAME + ";"; 
	
    static final String ADD_NODES_TESTDATA_0 = "INSERT INTO " + NODES_TABLE_NAME
    		+ " VALUES (NULL,'First Folder','Folder','1',NULL,NULL,NULL);";
    static final String ADD_NODES_TESTDATA_1 = "INSERT INTO " + NODES_TABLE_NAME
    		+ " VALUES (NULL,'Second Folder','Folder','2',NULL,NULL,NULL);";
    static final String ADD_NODES_TESTDATA_2 = "INSERT INTO " + NODES_TABLE_NAME
    		+ " VALUES (NULL,'First Device','Node','11 11 11 11','Off','0','1');";
    static final String ADD_NODES_TESTDATA_3 = "INSERT INTO " + NODES_TABLE_NAME
    		+ " VALUES (NULL,'First Scene','Scene','333',NULL,NULL,'2');";
    static final String ADD_NODES_TESTDATA_4 = "INSERT INTO " + NODES_TABLE_NAME
    		+ " VALUES (NULL,'3rd Folder','Folder','3',NULL,NULL,NULL);";
    
    
    DatabaseHelper(Context context) {
    	super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_NODES_TABLE);
		// DEBUG 
		db.execSQL(ADD_NODES_TESTDATA_0);
		db.execSQL(ADD_NODES_TESTDATA_1);
		db.execSQL(ADD_NODES_TESTDATA_2);
		db.execSQL(ADD_NODES_TESTDATA_3);
		db.execSQL(ADD_NODES_TESTDATA_4);
		// END DEBUG
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Wipe out old database and regenerate
		db.execSQL(DROP_NODES_TABLE);
		onCreate(db);
	}
	
	SQLiteDatabase getDatabase() {
		
		//Create or open the database for read+write. The database is cached,
		// when it is opened.
		// It may fail to open or to write to database, but with retry
		// it may succeed.
		if ( mDb == null )
		{
			mDb = getWritableDatabase();
		}

		return mDb;
	}
	
	public void updateNodeTable(ArrayList<ContentValues> valuesList) {
		SQLiteDatabase db = getDatabase();
		// Drop existing data
		db.delete(NODES_TABLE_NAME, null, null);
		// Add all new data
		Iterator<ContentValues> i = valuesList.iterator();
		ContentValues c;
		while(i.hasNext()) {
			c = i.next();
			db.insert(NODES_TABLE_NAME, null, c);
			Log.i("DATABASE INSERT:", c.toString());
		}
	}
	
	public Cursor getCursorAllData() {
		SQLiteDatabase db = getDatabase();
		
		Cursor cursor = db.query(NODES_TABLE_NAME, null,null,null,null,null,null,null);
		
		return cursor;
	}
	

	public Cursor getCursorListData(String parent_id) {
		SQLiteDatabase db = getDatabase();
		
		String parent = null;
		Cursor cursor;

		Log.v("DATABASE","Cursor request...parent_id="+parent_id);
		if (parent_id != null) {
			Cursor lookup = db.query(NODES_TABLE_NAME, new String[]{"address"},"_id = "+parent_id,null,null,null,null,null);
			Log.v("DATABASE","Parent Search: "+lookup.getCount()+" Found");
			lookup.moveToFirst();
			parent = lookup.getString(lookup.getColumnIndex(KEY_ADDRESS));
			Log.v("DATABASE","Parent Found: "+ parent);
			cursor = db.query(NODES_TABLE_NAME, null,"parent = "+parent,null,null,null,null,null);
			Log.v("DATABASE","Non-root Cursor Found: "+cursor.getCount());
		} else {
			cursor = db.query(NODES_TABLE_NAME, null,"parent is null",null,null,null,null,null);
			Log.v("DATABASE","Root Cursor Found: "+cursor.getCount());
		}
		
		return cursor;
	}

	public boolean isFolder(String id) {
		SQLiteDatabase db = getDatabase();
		
		Cursor lookup = db.query(NODES_TABLE_NAME, new String[]{"type"},"_id = "+id,null,null,null,null,null);
		lookup.moveToFirst();
		String type = lookup.getString(lookup.getColumnIndex(KEY_TYPE));
		Log.v("DATABASE","Folder check on id:"+id+" Type="+type);
		if (type.equals("Folder")) {
			return true;
		} else {
			return false;
		}
		
	}


	public String getNameFromId(String id) {
		SQLiteDatabase db = getDatabase();
		Cursor lookup = db.query(NODES_TABLE_NAME, new String[]{"name"},"_id = "+id,null,null,null,null,null);
		lookup.moveToFirst();
		String result = lookup.getString(lookup.getColumnIndex(KEY_NAME));
		lookup.close();
		return result;
	}


	public String getTypeFromId(String id) {
		SQLiteDatabase db = getDatabase();
		Cursor lookup = db.query(NODES_TABLE_NAME, new String[]{KEY_TYPE},"_id = "+id,null,null,null,null,null);
		lookup.moveToFirst();
		String result =  lookup.getString(lookup.getColumnIndex(KEY_TYPE));
		lookup.close();
		return result;
	}


	public String getAddressFromId(String id) {
		SQLiteDatabase db = getDatabase();
		Cursor lookup = db.query(NODES_TABLE_NAME, new String[]{KEY_ADDRESS},"_id = "+id,null,null,null,null,null);
		lookup.moveToFirst();
		String result =  lookup.getString(lookup.getColumnIndex(KEY_ADDRESS));
		lookup.close();
		return result;
	}


	public String getValueFromId(String id) {
		SQLiteDatabase db = getDatabase();
		Cursor lookup = db.query(NODES_TABLE_NAME, new String[]{KEY_VALUE},"_id = "+id,null,null,null,null,null);
		lookup.moveToFirst();
		String result =  lookup.getString(lookup.getColumnIndex(KEY_VALUE));
		lookup.close();
		return result;
	}


	public String getRawValueFromId(String id) {
		SQLiteDatabase db = getDatabase();
		Cursor lookup = db.query(NODES_TABLE_NAME, new String[]{KEY_RAW_VALUE},"_id = "+id,null,null,null,null,null);
		lookup.moveToFirst();
		String result = lookup.getString(lookup.getColumnIndex(KEY_RAW_VALUE));
		lookup.close();
		return result;
	}
}
