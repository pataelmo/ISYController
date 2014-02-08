package com.pataelmo.isycontroller;


import java.util.ArrayList;
import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
    static final String DROP_NODES_TABLE = "DROP TABLE IF EXISTS " + NODES_TABLE_NAME + ";"; 
	
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

    static final String PROGRAMS_TABLE_NAME = "isyPrograms";
    
    static final String KEY_ISFOLDER = "isFolder";			// Boolean (is this a folder, int=1 for true)
    static final String KEY_STATUS = "status";              // Boolean (is the condition is true int=1, else int=0)
    static final String KEY_ENABLED = "enabled";            // Boolean (int or null)
    static final String KEY_RUNATSTARTUP = "runAtStartup";  // Boolean (int or null)
    static final String KEY_RUNNING = "running";            // Text ("idle", "running"?)
    static final String KEY_LASTRUNTIME = "lastRunTime";    // Integer DateTime stamp in milliseconds
    static final String KEY_LASTENDTIME = "lastEndTime";    // Integer DateTime stamp in milliseconds
    
    
    static final String CREATE_PROGRAMS_TABLE = 
    		"CREATE TABLE " + PROGRAMS_TABLE_NAME + "( "
    		+ KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_NAME + " TEXT NOT NULL,"
			+ KEY_ADDRESS + " TEXT NOT NULL,"
			+ KEY_ISFOLDER + " INTEGER NOT NULL,"
			+ KEY_STATUS + " INTEGER NOT NULL,"
		    + KEY_PARENT + " TEXT,"
			+ KEY_ENABLED  + " INTEGER," 
			+ KEY_RUNATSTARTUP  + " INTEGER," 
			+ KEY_RUNNING  + " TEXT," 
			+ KEY_LASTRUNTIME  + " INTEGER," 
			+ KEY_LASTENDTIME  + " INTEGER);";
    static final String DROP_PROGRAMS_TABLE = "DROP TABLE IF EXISTS " + PROGRAMS_TABLE_NAME + ";"; 
    
    static final String ADD_PROGRAMS_TESTDATA_0 = "INSERT INTO " + PROGRAMS_TABLE_NAME
    		+ " VALUES (NULL,'Program Folder 1','0001','1','0',NULL,NULL,NULL,NULL,NULL,NULL);";
    static final String ADD_PROGRAMS_TESTDATA_1 = "INSERT INTO " + PROGRAMS_TABLE_NAME
    		+ " VALUES (NULL,'Program Folder 2','0002','1','0',NULL,NULL,NULL,NULL,NULL,NULL);";
    static final String ADD_PROGRAMS_TESTDATA_2 = "INSERT INTO " + PROGRAMS_TABLE_NAME
    		+ " VALUES (NULL,'First Program','0003','0','0','0001','1','1','idle','0','0');";
    static final String ADD_PROGRAMS_TESTDATA_3 = "INSERT INTO " + PROGRAMS_TABLE_NAME
    		+ " VALUES (NULL,'Next Program','0004','0','0','0001','1','1','idle','0','0');";
    static final String ADD_PROGRAMS_TESTDATA_4 = "INSERT INTO " + PROGRAMS_TABLE_NAME
    		+ " VALUES (NULL,'Lonely Program','0005','0','0','0002','1','1','idle','0','0');";
    

    static final String VARS_TABLE_NAME = "isyVariables";

    static final String KEY_INIT = "init";    				// Integer value variable is initalized to on boot
    static final String KEY_LASTCHANGED = "lastChanged";    // Integer DateTime stamp in milliseconds of when variable last changed
    
    static final String CREATE_VARS_TABLE = 
    		"CREATE TABLE " + VARS_TABLE_NAME + "( "
    		+ KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_NAME + " TEXT NOT NULL,"
			+ KEY_ADDRESS + " TEXT NOT NULL,"
			+ KEY_TYPE + " TEXT NOT NULL,"
			+ KEY_INIT + " INTEGER NOT NULL,"
		    + KEY_VALUE + " INTEGER,"
			+ KEY_LASTCHANGED  + " INTEGER);";
    static final String DROP_VARS_TABLE = "DROP TABLE IF EXISTS " + VARS_TABLE_NAME + ";"; 
    
    static final String ADD_VARS_TESTDATA_0 = "INSERT INTO " + VARS_TABLE_NAME
    		+ " VALUES (NULL,'Integer Variable 1','1','1','0','0',NULL);";
    static final String ADD_VARS_TESTDATA_1 = "INSERT INTO " + VARS_TABLE_NAME
    		+ " VALUES (NULL,'Integer Variable 2','2','1','0','2',NULL);";
    static final String ADD_VARS_TESTDATA_2 = "INSERT INTO " + VARS_TABLE_NAME
    		+ " VALUES (NULL,'State Variable 1','1','2','0','0',NULL);";
    static final String ADD_VARS_TESTDATA_3 = "INSERT INTO " + VARS_TABLE_NAME
    		+ " VALUES (NULL,'State Variable 2','2','2','0','1',NULL);";
    
    
    DatabaseHelper(Context context) {
    	super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_NODES_TABLE);
		db.execSQL(CREATE_PROGRAMS_TABLE);
		db.execSQL(CREATE_VARS_TABLE);
		// DEBUG 
//		db.execSQL(ADD_NODES_TESTDATA_0);
//		db.execSQL(ADD_NODES_TESTDATA_1);
//		db.execSQL(ADD_NODES_TESTDATA_2);
//		db.execSQL(ADD_NODES_TESTDATA_3);
//		db.execSQL(ADD_NODES_TESTDATA_4);
//		db.execSQL(ADD_PROGRAMS_TESTDATA_0);
//		db.execSQL(ADD_PROGRAMS_TESTDATA_1);
//		db.execSQL(ADD_PROGRAMS_TESTDATA_2);
//		db.execSQL(ADD_PROGRAMS_TESTDATA_3);
//		db.execSQL(ADD_PROGRAMS_TESTDATA_4);
//		db.execSQL(ADD_VARS_TESTDATA_0);
//		db.execSQL(ADD_VARS_TESTDATA_1);
//		db.execSQL(ADD_VARS_TESTDATA_2);
//		db.execSQL(ADD_VARS_TESTDATA_3);
		// END DEBUG
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Wipe out old database and regenerate
		db.execSQL(DROP_NODES_TABLE);
		db.execSQL(DROP_PROGRAMS_TABLE);
		db.execSQL(DROP_VARS_TABLE);
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
		//db.delete(NODES_TABLE_NAME, null, null);
		// Add all new data
		Iterator<ContentValues> i = valuesList.iterator();
		ContentValues c;
		while(i.hasNext()) {
			c = i.next();
			Cursor cursor = db.query(NODES_TABLE_NAME, null,KEY_ADDRESS+" = ?", new String[]{c.getAsString(KEY_ADDRESS)}, null, null, null, "1");
			if(cursor.getCount() > 0) {
				cursor.moveToFirst();
				Log.v("DATABASE UPDATE","Row = "+cursor.getString(cursor.getColumnIndex(KEY_ROWID)));
				db.update(NODES_TABLE_NAME, c, KEY_ROWID+" = ?", new String[]{cursor.getString(cursor.getColumnIndex(KEY_ROWID))});
			} else {
				db.insert(NODES_TABLE_NAME, null, c);
				Log.v("DATABASE INSERT:", c.toString());
			}

		}
		Cursor cursor = db.query(NODES_TABLE_NAME, new String[]{"COUNT(_id)"}, KEY_TYPE+" = ?", new String[]{"System"}, null, null, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		if (count < 2) {
			ContentValues extras = new ContentValues();
			extras.put(KEY_NAME,"Programs");
			extras.put(KEY_TYPE,"System");
			extras.put(KEY_ADDRESS,"Programs");
			db.insert(NODES_TABLE_NAME, null, extras);
			extras.clear();
			extras.put(KEY_NAME,"Variables");
			extras.put(KEY_TYPE,"System");
			extras.put(KEY_ADDRESS,"Vars");
			db.insert(NODES_TABLE_NAME, null, extras);
		}
	}
	
	public Cursor getCursorAllData() {
		SQLiteDatabase db = getDatabase();
		
		Cursor cursor = db.query(NODES_TABLE_NAME, null,null,null,null,null,KEY_NAME+" ASC",null);
		
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
			cursor = db.query(NODES_TABLE_NAME, null,"parent = ?",new String[]{parent},null,null,KEY_NAME+" ASC",null);
			Log.v("DATABASE","Non-root Cursor Found: "+cursor.getCount());
		} else {
			cursor = db.query(NODES_TABLE_NAME, null,"parent is null",null,null,null,KEY_NAME+" ASC",null);
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
		} else if(type.equals("System")) {
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


	public void updateNodeValue(String id, String value, String rawValue) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = getDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_VALUE, value);
		values.put(KEY_RAW_VALUE, rawValue);
		db.update(NODES_TABLE_NAME, values, "_id = ?", new String[]{id});
	}


	public String getProgramNameFromId(String id) {
		SQLiteDatabase db = getDatabase();
		Cursor lookup = db.query(PROGRAMS_TABLE_NAME, new String[]{"name"},"_id = "+id,null,null,null,null,null);
		lookup.moveToFirst();
		String result = lookup.getString(lookup.getColumnIndex(KEY_NAME));
		lookup.close();
		return result;
	}


	public ProgramData getProgramData(String id) {
		SQLiteDatabase db = getDatabase();
		Cursor c = db.query(PROGRAMS_TABLE_NAME, null,"_id = "+id,null,null,null,null,null);
		c.moveToFirst();
		ProgramData result = new ProgramData(
				c.getInt(c.getColumnIndex(KEY_ROWID)),
				c.getString(c.getColumnIndex(KEY_NAME)),
				c.getString(c.getColumnIndex(KEY_ADDRESS)),
				c.getInt(c.getColumnIndex(KEY_ISFOLDER)),
				c.getString(c.getColumnIndex(KEY_STATUS)),
				c.getString(c.getColumnIndex(KEY_PARENT)),
				c.getInt(c.getColumnIndex(KEY_ENABLED)),
				c.getInt(c.getColumnIndex(KEY_RUNATSTARTUP)),
				c.getString(c.getColumnIndex(KEY_RUNNING)),
				c.getLong(c.getColumnIndex(KEY_LASTRUNTIME)),
				c.getLong(c.getColumnIndex(KEY_LASTENDTIME)));
		c.close();

		return result;
	}
	
	public VariableData getVarData(String id) {
		SQLiteDatabase db = getDatabase();
		Cursor c = db.query(VARS_TABLE_NAME, null,"_id = "+id,null,null,null,null,null);
		c.moveToFirst();
		VariableData result = new VariableData(
				c.getInt(c.getColumnIndex(KEY_ROWID)),
				c.getString(c.getColumnIndex(KEY_NAME)),
				c.getString(c.getColumnIndex(KEY_ADDRESS)),
				c.getString(c.getColumnIndex(KEY_TYPE)),
				c.getInt(c.getColumnIndex(KEY_INIT)),
				c.getInt(c.getColumnIndex(KEY_VALUE)),
				c.getInt(c.getColumnIndex(KEY_LASTCHANGED)));
		c.close();
		return result;
	}
	
	public String getVarNameFromId(String id) {
		// TODO Auto-generated method stub
		return null;
	}


	public Cursor getVarList() {
		SQLiteDatabase db = getDatabase();
		Cursor cursor = null;
		
		try {
			cursor = db.query(VARS_TABLE_NAME, null,null,null,null,null,KEY_TYPE+" ASC,"+KEY_NAME+" ASC",null);
			Log.v("DATABASE","Root Cursor Found: "+cursor.getCount());
		} catch (SQLiteException e) {
			onUpgrade(db,0,0);
			Log.e("DATABASE","getProgramList Fault:"+e.toString());
		}
		
		return cursor;
	}

	public void dumpDatabase() {
		SQLiteDatabase db = getDatabase();
		onUpgrade(db,0,0);
	}
	
	public Cursor getProgramsList(String id) {
		SQLiteDatabase db = getDatabase();
	
		String parent = null;
		Cursor cursor = null;
		
		try {
			Log.v("DATABASE","Cursor request...parent_id="+id);
			if (id != null) {
				Cursor lookup = db.query(PROGRAMS_TABLE_NAME, new String[]{"address"},"_id = "+id,null,null,null,null,null);
				Log.v("DATABASE","Parent Search: "+lookup.getCount()+" Found");
				lookup.moveToFirst();
				parent = lookup.getString(lookup.getColumnIndex(KEY_ADDRESS));
				Log.v("DATABASE","Parent Found: "+ parent);
				cursor = db.query(PROGRAMS_TABLE_NAME, null,"parent = ?",new String[]{parent},null,null,KEY_NAME+" ASC",null);
				Log.v("DATABASE","Non-root Cursor Found: "+cursor.getCount());
			} else {
				cursor = db.query(PROGRAMS_TABLE_NAME, null,"parent is null",null,null,null,KEY_NAME+" ASC",null);
				Log.v("DATABASE","Root Cursor Found: "+cursor.getCount());
			}
		} catch (SQLiteException e) {
			onUpgrade(db,0,0);
			Log.e("DATABASE","getProgramList Fault:"+e.toString());
		}
		
		return cursor;
	}


	public boolean isProgramIdFolder(String id) {
		SQLiteDatabase db = getDatabase();
		Cursor lookup = db.query(PROGRAMS_TABLE_NAME, new String[]{KEY_ISFOLDER},"_id = "+id,null,null,null,null,null);
		lookup.moveToFirst();
		int result = lookup.getInt(lookup.getColumnIndex(KEY_ISFOLDER));
		lookup.close();
		if (result == 0){
			return false;
		} else {
			return true;
		}
	}


	public void updateProgramsTable(ArrayList<ContentValues> valuesList) {
		SQLiteDatabase db = getDatabase();
		// Drop existing data
		//db.delete(PROGRAMS_TABLE_NAME, null, null);
		// Add all new data
		Iterator<ContentValues> i = valuesList.iterator();
		ContentValues c;
		while(i.hasNext()) {
			c = i.next();
			Cursor cursor = db.query(PROGRAMS_TABLE_NAME, null,KEY_ADDRESS+" = ?", new String[]{c.getAsString(KEY_ADDRESS)}, null, null, null, "1");
			if(cursor.getCount() > 0) {
				cursor.moveToFirst();
				Log.v("DATABASE UPDATE","Row = "+cursor.getString(cursor.getColumnIndex(KEY_ROWID)));
				db.update(PROGRAMS_TABLE_NAME, c, KEY_ROWID+" = ?", new String[]{cursor.getString(cursor.getColumnIndex(KEY_ROWID))});
			} else {
				db.insert(PROGRAMS_TABLE_NAME, null, c);
				Log.v("DATABASE INSERT:", c.toString());
			}
		}
		
	}


	public void updateVarsTable(ArrayList<ContentValues> values) {
		SQLiteDatabase db = getDatabase();
		// Drop existing data
		//db.delete(VARS_TABLE_NAME, null, null);
		// Add all new data
		Iterator<ContentValues> i = values.iterator();
		ContentValues c;
		while(i.hasNext()) {
			c = i.next();
			Cursor cursor = db.query(VARS_TABLE_NAME, null,KEY_ADDRESS+" = ? AND "+KEY_TYPE+" = ?", new String[]{c.getAsString(KEY_ADDRESS),c.getAsString(KEY_TYPE)}, null, null, null, "1");
			if(cursor.getCount() > 0) {
				cursor.moveToFirst();
				Log.v("DATABASE UPDATE","Row = "+cursor.getString(cursor.getColumnIndex(KEY_ROWID)));
				db.update(VARS_TABLE_NAME, c, KEY_ROWID+" = ?", new String[]{cursor.getString(cursor.getColumnIndex(KEY_ROWID))});
			} else {
				db.insert(VARS_TABLE_NAME, null, c);
			}
			cursor.close();
			Log.v("DATABASE INSERT:", c.toString());
		}
	}
}
