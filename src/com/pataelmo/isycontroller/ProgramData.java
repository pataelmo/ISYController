package com.pataelmo.isycontroller;

public class ProgramData {
	public int mId = -1;
	public String mName = null;
	public String mAddress = null;
	public boolean mIsFolder = false;
	public String mStatus = null;
	public String mParent = null;
	public boolean mEnabled = false;
	public boolean mRunAtStartup = false;
	public String mRunning = null;
	public int mLastRunTime = -1;
	public int mLastEndTime = -1;
	
	public ProgramData() {
		// Boring constructor
	}
	
	public ProgramData(int id, String name, String address, int isFolder, String status, String parent, int enabled, int runAtStartup, String running, int lastRunTime, int lastEndTime) {
		mId = id;
		mName = name;          
		mAddress = address; 
		if (isFolder > 0) {
			mIsFolder = true;
		}
		mStatus = status;        
		mParent = parent;     
		if (enabled > 0) {
			mEnabled = true; 
		}
		if (runAtStartup > 0) {
			mRunAtStartup = true;
		}
		mRunning = running;       
		mLastRunTime = lastRunTime;        
		mLastEndTime = lastEndTime;        
	}

}