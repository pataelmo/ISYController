package com.pataelmo.isycontroller;

public class NodeData {
	public String mId = null;
    public String mName = null;
    public String mType = null;
    public String mAddress = null;
    public String mValue = null;
    public String mRawValue = null;
	public String mParent;
    
    NodeData() {
    	// Boring constructor
    }
    
    NodeData(String id,String name, String type, String address, String value, String rawValue, String parent) {
    	mId = id;       
    	mName = name;     
    	mType = type;     
    	mAddress = address;  
    	mValue = value;    
    	mRawValue = rawValue; 
    	mParent = parent;
    	
    	if (mValue == null) {
    		mValue = "N/A";
    	}
    	if (mRawValue == null) {
    		mRawValue = "N/A";
    	}
    }
}
