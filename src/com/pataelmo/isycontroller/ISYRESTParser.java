package com.pataelmo.isycontroller;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.ContentValues;
import android.util.Log;

public class ISYRESTParser {
	
	private InputStream mInputStream;
	private Document mDOM;
	private Element mRoot;
	private String mRootName;
	private boolean mSuccess;
	private HashMap<String,String> mVarNameMap = null;
	private ArrayList<ContentValues> mDbEntries = null;

	public ISYRESTParser() {
		// Boring Constructor
	}
	
	public ISYRESTParser(InputStream is) {
		mInputStream = is;
		try {
			mDOM = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(mInputStream);
		} catch (IOException e) {
			Log.e("ISYRESTParser","IO Error:"+e.toString());
		} catch (SAXException e) {
			Log.e("ISYRESTParser","SAX Error:"+e.toString());
		} catch (ParserConfigurationException e) {
			Log.e("ISYRESTParser","Parser Error:"+e.toString());
		}
		mRoot = mDOM.getDocumentElement();
		mRootName = mRoot.getNodeName();
		parse();
	}
	
	public String getRootName() {
		return mRootName;
	}

	public String parse() {
		if (mRootName.equals("nodes")) {
			parseNodes(mRoot);
		} else if (mRootName.equals("nodeInfo")) {
			parseNodeInfo(mRoot);
		} else if (mRootName.equalsIgnoreCase("RestResponse")) {
			parseRestResponse(mRoot);
		} else if (mRootName.equals("properties")) {
			parseNodeProperties(mRoot);
		} else if (mRootName.equals("programs")) {
			parsePrograms(mRoot);
		} else if (mRootName.equals("vars")) {
			parseVars(mRoot);
		} else if (mRootName.equals("CList")) {
			parseVarNames(mRoot);
		} else {
			return null;
		}
		return mRootName;
	}
	
	public HashMap<String,String> getVarNameMap() {
		return mVarNameMap ;
	}
	
	
	private void parseVarNames(Element root) {
		// TODO Auto-generated method stub
		mVarNameMap = new HashMap<String,String>();
		NodeList variables = root.getElementsByTagName("e");
		for (int i=0;i<variables.getLength();i++) {
			Node variable = variables.item(i);
			NamedNodeMap attributes = variable.getAttributes();
			String id = attributes.getNamedItem("id").getNodeValue();
			String name = attributes.getNamedItem("name").getNodeValue();
			Log.v("ISYRESTParser.parseVarNames","id="+id+",name="+name);
			mVarNameMap.put(id,name);
		}
	}

	private void parseVars(Element root) {
		// TODO Auto-generated method stub
		mDbEntries = new ArrayList<ContentValues>();
		NodeList variables = root.getElementsByTagName("var");
		for (int i=0;i<variables.getLength();i++) {
			ContentValues content = new ContentValues();
			Node variable = variables.item(i);
			NamedNodeMap attributes = variable.getAttributes();
			String id = attributes.getNamedItem("id").getNodeValue();
			String type = attributes.getNamedItem("type").getNodeValue();
			Log.v("ISYRESTParser.parseVars","id="+id+",type="+type);
			content.put(DatabaseHelper.KEY_ADDRESS, id);
			content.put(DatabaseHelper.KEY_TYPE, type);
			NodeList properties = variable.getChildNodes();
			for (int j=0;j<properties.getLength();j++) {
				Node property = properties.item(j);
				String pName = property.getNodeName();
				if(pName.equals("init")) {
					content.put(DatabaseHelper.KEY_INIT,property.getFirstChild().getNodeValue());
					Log.v("ISYRESTParser.parseVars","init="+property.getFirstChild().getNodeValue());
				} else if (pName.equals("val")) {
					content.put(DatabaseHelper.KEY_VALUE,property.getFirstChild().getNodeValue());
					Log.v("ISYRESTParser.parseVars","value="+property.getFirstChild().getNodeValue());
				} else if (pName.equals("ts")) {
					String time = property.getFirstChild().getNodeValue();
					SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd kk:mm:ss", Locale.US);	// example value 20140205 14:41:22
					Date date = null;
					try {
						date = dateFormatter.parse(time);
						content.put(DatabaseHelper.KEY_LASTCHANGED, date.getTime());
						Log.v("ISYRESTParser.parseVars","lastChanged="+date.toString());
					} catch (java.text.ParseException e) {
						Log.e("ISYRESTParser","Date Parsing Exception:"+e.toString());
						e.printStackTrace();
					} 
				}
			}
			// Push contentvalues into stack
			mDbEntries.add(content);
		}
	}

	private void parsePrograms(Element root) {
		// TODO Auto-generated method stub
		mDbEntries = new ArrayList<ContentValues>();
		NodeList programs = root.getElementsByTagName("program");

		// Parse Program Data
		for (int i=0;i<programs.getLength();i++) {
			Node program = programs.item(i);
			ContentValues content = new ContentValues();
			NamedNodeMap attributes = program.getAttributes();
			content.put(DatabaseHelper.KEY_ADDRESS, attributes.getNamedItem("id").getNodeValue());
			if (attributes.getNamedItem("status").getNodeValue().equals("true")) {
				content.put(DatabaseHelper.KEY_STATUS, 1);
			} else {
				content.put(DatabaseHelper.KEY_STATUS, 0);
			}
			boolean folder;
			if (attributes.getNamedItem("folder").getNodeValue().equals("true")) {
				content.put(DatabaseHelper.KEY_ISFOLDER, 1);
				folder = true;
			} else {
				content.put(DatabaseHelper.KEY_ISFOLDER, 0);
				folder = false;
			}
			
			Node parent = attributes.getNamedItem("parentId");
			if (parent != null) {
				content.put(DatabaseHelper.KEY_PARENT, attributes.getNamedItem("parentId").getNodeValue());
			}
			if (!folder) {
				if (attributes.getNamedItem("enabled").getNodeValue().equals("true")) {
					content.put(DatabaseHelper.KEY_ENABLED, 1);
				} else {
					content.put(DatabaseHelper.KEY_ENABLED, 0);
				}
				if (attributes.getNamedItem("runAtStartup").getNodeValue().equals("true")) {
					content.put(DatabaseHelper.KEY_RUNATSTARTUP, 1);
				} else {
					content.put(DatabaseHelper.KEY_RUNATSTARTUP, 0);
				}
				content.put(DatabaseHelper.KEY_RUNNING,attributes.getNamedItem("running").getNodeValue());
			}

			NodeList properties = program.getChildNodes();
			for (int j=0;j<properties.getLength();j++){
				Node property = properties.item(j);
				String name = property.getNodeName();
				if (name.equalsIgnoreCase("name")) {
					content.put(DatabaseHelper.KEY_NAME, property.getFirstChild().getNodeValue());
				} else if (name.equalsIgnoreCase("lastRunTime")) {
					if (property.getFirstChild() != null) {
						String time = property.getFirstChild().getNodeValue();
						SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd K:mm:ss a", Locale.US);	// example value 2014/02/05 9:52:22 PM
						Date date = null;
						try {
							date = dateFormatter.parse(time);
							content.put(DatabaseHelper.KEY_LASTRUNTIME, date.getTime());
						} catch (java.text.ParseException e) {
							Log.e("ISYRESTParser","Date Parsing Exception:"+e.toString());
							e.printStackTrace();
						}
					}
				} else if (name.equalsIgnoreCase("lastFinishTime")) {
					if (property.getFirstChild() != null) {
						String time = property.getFirstChild().getNodeValue();
						SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd K:mm:ss a", Locale.US);	// example value 2014/02/05 9:52:22 PM
						Date date = null;
						try {
							date = dateFormatter.parse(time);
							content.put(DatabaseHelper.KEY_LASTENDTIME, date.getTime());
						} catch (java.text.ParseException e) {
							Log.e("ISYRESTParser","Date Parsing Exception:"+e.toString());
							e.printStackTrace();
						}
					}
				}
			}
			// Store entry in database
			mDbEntries.add(content);
			Log.v("XML PARSE","New Progam Data = "+content);
		}
	}

	private void parseNodeProperties(Node root) {
		// TODO Auto-generated method stub
		NodeList properties = root.getChildNodes();
		for (int i=0;i<properties.getLength();i++) {
//			Node property = properties.item(i);
//			NamedNodeMap props = property.getAttributes();
//			String id = props.getNamedItem("id").getNodeValue();
//			String value = props.getNamedItem("value").getNodeValue();
//			String formatted = props.getNamedItem("formatted").getNodeValue();
//			String uom = props.getNamedItem("uom").getNodeValue();
			// Post results somehow
		}
	}

	private void parseRestResponse(Element root) {
		// TODO Auto-generated method stub
		if (root.getAttribute("succeeded").equalsIgnoreCase("true")) {
			mSuccess = true;
		} else {
			mSuccess = false;
		}
	}
	
	public boolean getSuccess() {
		return mSuccess;
	}

	private void parseNodeInfo(Element root) {
		// TODO Auto-generated method stub
		NodeList nl = root.getElementsByTagName("node");
		parseNode(nl.item(0));
		nl = root.getElementsByTagName("properties");
		parseNodeProperties(nl.item(0));
	}

	private void parseNode(Node node) {
		// TODO Auto-generated method stub
		
	}

	private void parseNodes(Element root) {
		// TODO Auto-generated method stub
		mDbEntries = new ArrayList<ContentValues>();
		NodeList folders = root.getElementsByTagName("folder");
		NodeList nodes = root.getElementsByTagName("node");
		NodeList groups = root.getElementsByTagName("group");
		
		// Parse Folder Data
		for (int i=0;i<folders.getLength();i++) {
			Node folder = folders.item(i);
			NodeList properties = folder.getChildNodes();
			ContentValues content = new ContentValues();
			content.put(DatabaseHelper.KEY_TYPE, "Folder");
			for (int j=0;j<properties.getLength();j++){
				Node property = properties.item(j);
				String name = property.getNodeName();
				if (name.equalsIgnoreCase("address")) {
					content.put(DatabaseHelper.KEY_ADDRESS, property.getFirstChild().getNodeValue());
				} else if (name.equalsIgnoreCase("name")) {
					content.put(DatabaseHelper.KEY_NAME, property.getFirstChild().getNodeValue());
				} else if (name.equalsIgnoreCase("parent")) {
					content.put(DatabaseHelper.KEY_PARENT, property.getFirstChild().getNodeValue());
				}
			}
			// Store entry in database
			mDbEntries.add(content);
		}
		
		// Parse Node Data
		for (int i=0;i<nodes.getLength();i++) {
			Node node = nodes.item(i);
			NodeList properties = node.getChildNodes();
			ContentValues content = new ContentValues();
			content.put(DatabaseHelper.KEY_TYPE, "Node");
			for (int j=0;j<properties.getLength();j++){
				Node property = properties.item(j);
				String name = property.getNodeName();
				if (name.equalsIgnoreCase("address")) {
					content.put(DatabaseHelper.KEY_ADDRESS, property.getFirstChild().getNodeValue());
				} else if (name.equalsIgnoreCase("name")) {
					content.put(DatabaseHelper.KEY_NAME, property.getFirstChild().getNodeValue());
				} else if (name.equalsIgnoreCase("parent")) {
					content.put(DatabaseHelper.KEY_PARENT, property.getFirstChild().getNodeValue());
				} else if (name.equalsIgnoreCase("property")) {
					NamedNodeMap attributes = property.getAttributes();
					content.put(DatabaseHelper.KEY_VALUE, attributes.getNamedItem("formatted").getNodeValue());
					content.put(DatabaseHelper.KEY_RAW_VALUE, attributes.getNamedItem("value").getNodeValue());
				}
			}
			// Store entry in database
			mDbEntries.add(content);
		}
		
		// Parse Group Data
		for (int i=0;i<groups.getLength();i++) {
			Node group = groups.item(i);
			NodeList properties = group.getChildNodes();
			ContentValues content = new ContentValues();
			content.put(DatabaseHelper.KEY_TYPE, "Scene");
			for (int j=0;j<properties.getLength();j++){
				Node property = properties.item(j);
				String name = property.getNodeName();
				if (name.equalsIgnoreCase("address")) {
					content.put(DatabaseHelper.KEY_ADDRESS, property.getFirstChild().getNodeValue());
				} else if (name.equalsIgnoreCase("name")) {
					content.put(DatabaseHelper.KEY_NAME, property.getFirstChild().getNodeValue());
				} else if (name.equalsIgnoreCase("parent")) {
					content.put(DatabaseHelper.KEY_PARENT, property.getFirstChild().getNodeValue());
				}
			}
			// Store entry in database
			mDbEntries.add(content);
		}
	}

	public ArrayList<ContentValues> getDatabaseValues() {
		return mDbEntries;
	}
}
