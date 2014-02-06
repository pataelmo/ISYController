package com.pataelmo.isycontroller;

public class VariableData {

	public int mId;
	public String mName;
	public String mAddress;
	public String mType;
	public int mInit;
	public int mValue;
	public int mLastChanged;

	public VariableData() {
		// Boring constructor
		mId = -1;
		mName = "";
		mAddress = "";
		mType = "";
		mInit = 0;
		mValue = 0;
		mLastChanged = -1;
	}

	public VariableData(int id, String name, String address,
			String type, int init, int value, int lastChanged) {
		mId = id;
		mName = name;
		mAddress = address;
		mType = type;
		mInit = init;
		mValue = value;
		mLastChanged = lastChanged;
	}

	public String getValueStr() {
		return Integer.toString(mValue);
	}

}
