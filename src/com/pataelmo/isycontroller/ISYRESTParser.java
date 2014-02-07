package com.pataelmo.isycontroller;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
	private ArrayList<ContentValues> mDbEntries;

	public ISYRESTParser() {
		// Boring Constructor
		mDbEntries = new ArrayList<ContentValues>();
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
		}
	}

	private void parseVarNames(Element root) {
		// TODO Auto-generated method stub
		
	}

	private void parseVars(Element root) {
		// TODO Auto-generated method stub
		
	}

	private void parsePrograms(Element root) {
		// TODO Auto-generated method stub
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
			Node property = properties.item(i);
			NamedNodeMap props = property.getAttributes();
			String id = props.getNamedItem("id").getNodeValue();
			String value = props.getNamedItem("value").getNodeValue();
			String formatted = props.getNamedItem("formatted").getNodeValue();
			String uom = props.getNamedItem("uom").getNodeValue();
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
		
	}

}
