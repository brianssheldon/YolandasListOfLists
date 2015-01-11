package org.bubba.yolandaslistoflists;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.bubba.yolandaslistoflists.dragndrop.DragNDropListActivity;
import org.bubba.yolandaslistoflists.prefs.PrefsBO;
import org.bubba.yolandaslistoflists.prefs.PrefsDao;
import org.bubba.yolandaslistoflists.sql.KnownItemsDao;
import org.bubba.yolandaslistoflists.sql.ListOfListsDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class EditKnownItemsActivity extends Activity
{
	private ListOfListsDataSource datasource;
	private KnownItemsDao knownItemsDao;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_known_list);

		openDb();
		
		loadKnownItemsView();

		getActionBar().setTitle("Know Items");
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void openDb()
	{
		if(datasource == null) 
		{
			datasource = new ListOfListsDataSource(this);
			datasource.open();
		}
		
		if(knownItemsDao == null)
		{
			knownItemsDao = new KnownItemsDao(this);
			knownItemsDao.open();
		}
	}

	@SuppressLint("NewApi")
	private void loadKnownItemsView()
	{
		List<KnownItem> knownItems = getKnownItems();
		String knownList = "";//new String[knownItems.size()];
		int i = 0;
		for (Iterator<KnownItem> iterator = knownItems.iterator(); iterator.hasNext();)
		{
			knownList = knownList + "\n" + ((KnownItem) iterator.next()).getItem();
			i ++;
		}

        EditText et = (EditText) findViewById(R.id.editText);
        et.setText(knownList);
	}
    
	private List<KnownItem> getKnownItems()
	{
		List<KnownItem> values = knownItemsDao.getAllItems();
		
		if(values != null && values.size() > 0) 
		{
			return values; // the table is already loaded. don't load it again.
		}
		
		String[] hardCodedItems = getResources().getStringArray(R.array.food_array);
		
		for (int i = 0; i < hardCodedItems.length; i++)
		{
			knownItemsDao.createKnownItem(hardCodedItems[i]);
		}
		
		values = knownItemsDao.getAllItems();
		return values;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.edit_known_list_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    switch (item.getItemId())
	    {

            case R.id.action_save:

                saveKnownList();
                break;

            case android.R.id.home:
                saveKnownList();
                finish();
                break;
				
//			case R.id.action_undo:
//				int lastDeleteNumber = datasource.undoDelete(listNameToShow);
//
//				if(lastDeleteNumber == 0)
//				{
//					Toast.makeText(this, "\nSorry\n\nnothing to undo.\n\n", Toast.LENGTH_LONG).show();
//				}
//				else
//				{
//					displayItems();
//				}
//				break;
				
		    default:
		    	return super.onOptionsItemSelected(item);
	    }
        return true;
	}

    private void saveKnownList() {
        EditText etView = (EditText) findViewById(R.id.editText);
        String text = etView.getText().toString();

        String[] st = text.split("\n");

        for(int i = 0; i < st.length; i++)
        {
            if("".equals(st[i])) continue;

            knownItemsDao.createKnownItem(st[i]);
        }
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{	// called when returning from an intent
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}
	
	@Override
	protected void onResume()
	{
		System.err.println("editKnownItemsActivity  open");
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		System.err.println("editKnownItemsActivity  close");
		datasource.close();
		super.onPause();
	}
}