package org.bubba.yolandaslistoflists;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class OneListActivity extends ListActivity
{
	private ListOfListsDataSource datasource;
	private String listNameToShow; 

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.onelistmain);
		
		listNameToShow = (String) getIntent().getExtras().get(getString(R.string.listnametoshow));
		System.err.println("OneListActivity listNameToshow '" + listNameToShow + "'");

		datasource = new ListOfListsDataSource(this);
		datasource.open();

		displayItems();
		
		getActionBar().setTitle(listNameToShow);
	}

	private void displayItems()
	{
		List<OneListItem> itemsInList = datasource.getAllItemsForOneList(listNameToShow);
		List<String> itemList = new ArrayList<String>();
		for(OneListItem item : itemsInList)
		{
			itemList.add(item.getItem());
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, R.layout.list_item, itemList);
		setListAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.one_list_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    switch (item.getItemId())
	    {
		case R.id.action_text_message:

			break;
			
		case R.id.action_discard:
			datasource.deleteAll(listNameToShow);
			displayItems();
			
			break;
			
		case R.id.action_email:
			break;
	 
	        default:
	            return super.onOptionsItemSelected(item);
	    }
        return true;
	}
	

	@Override
	protected void onResume()
	{
		System.err.println("onelistactiviry  open");
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		System.err.println("onelistactiviry  close");
		datasource.close();
		super.onPause();
	}
}
