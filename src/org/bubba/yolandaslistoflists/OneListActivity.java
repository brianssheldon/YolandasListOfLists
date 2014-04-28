package org.bubba.yolandaslistoflists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bubba.yolandaslistoflists.sql.KnownItemsDao;
import org.bubba.yolandaslistoflists.sql.ListOfListsDataSource;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public class OneListActivity extends ListActivity
{
	private ListOfListsDataSource datasource;
	private KnownItemsDao knownItemsDao;
	private String listNameToShow; 

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.onelistmain);
		
		listNameToShow = (String) getIntent().getExtras().get(getString(R.string.listnametoshow));
		System.err.println("OneListActivity listNameToshow '" + listNameToShow + "'");

		openDb();
		
		loadKnownItemsView();

		displayItems();
		
		getActionBar().setTitle(listNameToShow);
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
		String[] knownArray = new String[knownItems.size()];
		int i = 0;
		for (Iterator<KnownItem> iterator = knownItems.iterator(); iterator.hasNext();)
		{
			knownArray[i] = ((KnownItem) iterator.next()).getItem();
			i ++;
		}

	   ArrayAdapter<String> adapter2 = 
	         new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, knownArray);
	   
	   AutoCompleteTextView actvDev = (AutoCompleteTextView)findViewById(R.id.actv);
	   actvDev.setThreshold(1);
	   actvDev.setAdapter(adapter2);

	   actvDev.setOnItemClickListener(new AutoCompleteListener());
	}

    private final class AutoCompleteListener implements OnItemClickListener
	{
		public void onItemClick(AdapterView<?> parent, View textView, int position, long id)
		{	// they have selected an item from the dropdown list. add it to the grocery list
			if(textView == null
				|| ((TextView)textView).getText() == null
				|| ((TextView)textView).getText().toString() == "") return;
			
			String name = ((TextView)textView).getText().toString(); // get selected item
			
			datasource.createComment(listNameToShow, name, 1);
			
//			groceryListDao.createItem(name, 1);

			((AutoCompleteTextView)findViewById(R.id.actv)).setText("");

			displayItems();
		}
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

    
	private String getKnownItemsAsString()
	{
		List<KnownItem> items = getKnownItems();
		String stringOfItems = "";
		
		for (int i = 0; i < items.size(); i++)
		{
			stringOfItems += items.get(i).getItem() + "\n";
		}
		return stringOfItems;
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
				displayItems();
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