package org.bubba.yolandaslistoflists;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

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

			
		case R.id.action_undo:
			break;
	 
	        default:
	            return super.onOptionsItemSelected(item);
	    }
        return true;
	}
	
	// Will be called via the onClick attribute of the buttons in main.xml
	public void onClick(View view)
	{
		@SuppressWarnings("unchecked")
		ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListAdapter();
		
		switch (view.getId())
		{
			case R.id.add:
				AutoCompleteTextView tv = (AutoCompleteTextView)findViewById(R.id.actv);
				String text = tv.getText().toString();
				
				if(text == null || text.trim().length() == 0) return;
				
				tv.setText("");
				
				datasource.createComment(listNameToShow, text, 1);
				displayItems();
				
//				knownItemsDao.createKnownItem(text);
//				loadKnownItemsView();
//				
//				groceryListDao.createItem(text, 1);
//				
//				((AutoCompleteTextView)findViewById(R.id.actv)).setText("");
//	
//				List<GroceryItem> groceryItems = getGroceryList();
//				ArrayAdapter<GroceryItem> adapter2 = new ArrayAdapter<GroceryItem>(view.getContext(), android.R.layout.simple_list_item_1, groceryItems);
//				setListAdapter(adapter2);
//				loadGroceryItems();
				break;
			}
			adapter.notifyDataSetChanged();
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