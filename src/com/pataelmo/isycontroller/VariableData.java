package com.pataelmo.isycontroller;

public class VariableData {

	public int mId;
	public String mName;
	public String mAddress;
	public String mType;
	public int mInit;
	public int mValue;
	public Long mLastChanged;

	public VariableData() {
		// Boring constructor
		mId = -1;
		mName = "";
		mAddress = "";
		mType = "";
		mInit = 0;
		mValue = 0;
		mLastChanged = 0L;
	}

	public VariableData(int id, String name, String address,
			String type, int init, int value, Long lastChanged) {
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

	public String getInitValueStr() {
		return Integer.toString(mInit);
	}
	
	@Override 
	public String toString() {
		String output = "";
		output += "id="+mId+"\n";
		output += "name="+mName+"\n";
		output += "address="+mAddress+"\n";
		output += "type="+mType+"\n";
		output += "init="+mInit+"\n";
		output += "value="+mValue+"\n";
		output += "lastChanged="+mLastChanged.toString()+"\n";
		return output;
	}

}
