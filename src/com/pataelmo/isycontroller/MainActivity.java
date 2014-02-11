package com.pataelmo.isycontroller;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;

public class MainActivity extends FragmentActivity implements NodeTreeFragment.OnListSelectListener,
ProgramTreeFragment.OnListSelectListener,VariableTreeFragment.OnListSelectListener {
	
	DatabaseHelper dbh = null;
	SimpleCursorAdapter mAdapter;
	ListView mList;
	String baseUrl;
	String loginUser;
	String loginPass;
	String mParentId;
	int mListPosition = 0;
	String mParentType;
	private boolean mDualPane = false;
	private ViewGroup mViewGroup = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_shell);
		// Show the Up button in the action bar.
		setupActionBar();

		// Check for intent data
		Intent intent = getIntent();
	  	mParentId = intent.getStringExtra("parent_id");
	  	mParentType = intent.getStringExtra("parent_type");
	  	
	  	mViewGroup = (ViewGroup) findViewById(R.id.columnLayout);

	  	if (savedInstanceState == null) {
		  	if (mViewGroup != null) {
		  		mDualPane = true;
				Bundle bundle = new Bundle();
				bundle.putString("parent_id", mParentId);
				bundle.putString("parent_type", mParentType);
				FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				NodeTreeFragment fragment = new NodeTreeFragment();
				fragment.setArguments(bundle);
				fragmentTransaction.add(R.id.primaryColumn, fragment);
				fragmentTransaction.commit();
			  	
		  	} else {
				Bundle bundle = new Bundle();
				bundle.putString("parent_id", mParentId);
				bundle.putString("parent_type", mParentType);
				FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				NodeTreeFragment fragment = new NodeTreeFragment();
				fragment.setArguments(bundle);
				fragmentTransaction.add(R.id.frame, fragment);
				fragmentTransaction.commit();
		  	}
	  	} else {
	  		Log.i("MainActivity","Restarted-IE Orientation Change");
	  	}
        Log.i("MainActivity","Created:"+this);
	  	
	}
	
	@Override
	public void loadNodeTree(String parent_id) {
		// Update fragment
		//NodeTreeFragment frag = (NodeTreeFragment) getSupportFragmentManager().findFragmentById(R.id.frame);
		if (mDualPane) {
			NodeTreeFragment newNodeFrag = new NodeTreeFragment();
			Bundle args = new Bundle();
			args.putString("parent_id",parent_id);
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.primaryColumn, newNodeFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		} else {
			NodeTreeFragment newNodeFrag = new NodeTreeFragment();
			Bundle args = new Bundle();
			args.putString("parent_id",parent_id);
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.frame, newNodeFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void loadProgramTree() {
		// Update fragment
		if (mDualPane) {
			ProgramTreeFragment newNodeFrag = new ProgramTreeFragment();
			Bundle args = new Bundle();
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.primaryColumn, newNodeFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		} else {
			ProgramTreeFragment newNodeFrag = new ProgramTreeFragment();
			Bundle args = new Bundle();
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.frame, newNodeFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void loadProgramTree(String parent_id) {
		// Update fragment
		if (mDualPane) {
			ProgramTreeFragment newNodeFrag = new ProgramTreeFragment();
			Bundle args = new Bundle();
			args.putString("parent_id", parent_id);
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.primaryColumn, newNodeFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		} else {
			ProgramTreeFragment newNodeFrag = new ProgramTreeFragment();
			Bundle args = new Bundle();
			args.putString("parent_id", parent_id);
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.frame, newNodeFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void loadVariableTree() {
		// Update fragment
		if (mDualPane) {
			VariableTreeFragment newNodeFrag = new VariableTreeFragment();
			Bundle args = new Bundle();
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.primaryColumn, newNodeFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		} else {
			VariableTreeFragment newNodeFrag = new VariableTreeFragment();
			Bundle args = new Bundle();
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.frame, newNodeFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void loadVariableTree(String parent_id) {
		// Update fragment
		if (mDualPane) {
			VariableTreeFragment newNodeFrag = new VariableTreeFragment();
			Bundle args = new Bundle();
			args.putString("parent_id",parent_id);
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.primaryColumn, newNodeFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		} else {
			VariableTreeFragment newNodeFrag = new VariableTreeFragment();
			Bundle args = new Bundle();
			args.putString("parent_id",parent_id);
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.frame, newNodeFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}
	
	@Override
	public void loadNode(String id) {
		// Update fragment
		if (mDualPane) {
			NodeViewFragment newNodeFrag = new NodeViewFragment();
			Bundle args = new Bundle();
			args.putString("id",id);
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.nodeArea, newNodeFrag);
			//transaction.addToBackStack(null);
			transaction.commit();
		} else {
			NodeViewFragment newNodeFrag = new NodeViewFragment();
			Bundle args = new Bundle();
			args.putString("id",id);
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.frame, newNodeFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void loadProgram(String id) {
		// Update fragment
		if (mDualPane) {
			ProgramViewFragment newNodeFrag = new ProgramViewFragment();
			Bundle args = new Bundle();
			args.putString("id",id);
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.nodeArea, newNodeFrag);
			//transaction.addToBackStack(null);
			transaction.commit();
		} else {
			ProgramViewFragment newNodeFrag = new ProgramViewFragment();
			Bundle args = new Bundle();
			args.putString("id",id);
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.frame, newNodeFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}
	
	@Override
	public void loadVariable(String id) {
		// Update fragment
		if (mDualPane) {
			VariableViewFragment newNodeFrag = new VariableViewFragment();
			Bundle args = new Bundle();
			args.putString("id",id);
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.nodeArea, newNodeFrag);
			//transaction.addToBackStack(null);
			transaction.commit();
		} else {
			VariableViewFragment newNodeFrag = new VariableViewFragment();
			Bundle args = new Bundle();
			args.putString("id",id);
			newNodeFrag.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.frame, newNodeFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.v("MainActivity","Paused:"+this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.v("MainActivity","Stopped:"+this);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.v("MainActivity","Restarted:"+this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		mList.setSelectionFromTop(mListPosition,0);
		Log.v("MainActivity","Resumed:"+this);
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setActionBarTitle(String title) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setTitle(title);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			//NavUtils.navigateUpFromSameTask(this);
//			
//			NavUtils.
//			Intent i = getParentActivityIntent();
//			String id = i.getStringExtra("parent_id");
//			Log.v("ACTIVITY","Nav Up with Intent parent_id="+id);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@TargetApi(11)
	public void setActionBarTitle(String main, String sub) {
		getActionBar().setTitle(main);
		getActionBar().setSubtitle(sub);
	}

} // End Class MainActivity
