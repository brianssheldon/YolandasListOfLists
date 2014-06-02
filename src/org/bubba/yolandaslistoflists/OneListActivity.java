package org.bubba.yolandaslistoflists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bubba.yolandaslistoflists.dragndrop.DragNDropListActivity;
import org.bubba.yolandaslistoflists.sql.KnownItemsDao;
import org.bubba.yolandaslistoflists.sql.ListOfListsDataSource;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

    
//	private String getKnownItemsAsString()
//	{
//		List<KnownItem> items = getKnownItems();
//		String stringOfItems = "";
//		
//		for (int i = 0; i < items.size(); i++)
//		{
//			stringOfItems += items.get(i).getItem() + "\n";
//		}
//		return stringOfItems;
//	}

	private void displayItems()
	{
		List<OneListItem> itemsInList = datasource.getAllItemsForOneList(listNameToShow);
		
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = null;
		
		for(OneListItem item : itemsInList)
		{
			if(!"".equals(item.getItem()))
			{
				map = new HashMap<String, String>();
				map.put("oneItem", item.getItem());
				map.put("oneQuantity", "" + item.getQuantity());
				mylist.add(map);
			}
		}

		SimpleAdapter adapter = new SimpleAdapter(this, mylist, R.layout.list_main_items,
            new String[] {"oneItem", "oneQuantity"}, 
            new int[] {R.id.oneItem, R.id.oneQuantity});

		setListAdapter(adapter);
		getListView().setOnItemClickListener(getItemClickListener());
		getListView().setFastScrollEnabled(true);
	}

	private OnItemClickListener getItemClickListener()
	{
		OnItemClickListener listViewOnClickListener = new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				final AdapterView adapterView = arg0; 
				@SuppressWarnings("unchecked")
				HashMap<String,String> map = (HashMap<String,String>)getListAdapter().getItem(arg2);

				if(map == null) return;
				
				final String item = (String) map.get("oneItem");
				
		        CharSequence[] items = new CharSequence[103];
		        items[0] = "Delete Item '" + item + "' ?";
		        items[1] = "Cancel";
		        for (int i = 0; i < 101; i++) items[i+2]=""+(i+1);

		        AlertDialog.Builder builder = new AlertDialog.Builder(arg0.getContext());
		        builder.setIcon(android.R.drawable.ic_dialog_alert);
		        builder.setTitle("Delete Item '" + item + "'");
		        builder.setItems(items, new OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						if(which == 0)
						{
							datasource.deleteComment(listNameToShow, item);
						}
						else if(which == 1)
						{
//							displayItems();
						}
						else if(which > 1)
						{
							OneListItem oneItem = datasource.getItem(listNameToShow, item);
							oneItem.setQuantity(which - 1);
							int rowsUpdated = datasource.updateItem(oneItem);
							
							if(rowsUpdated == 0) 
							{
								Toast.makeText(adapterView.getContext(), "\nSorry\n\nupdate failed.\n\n", Toast.LENGTH_LONG).show();
							}
						}

						displayItems();
					}
				});
		        
		        AlertDialog alert = builder.create();
		        alert.show();
		        alert.getWindow().setLayout(400, 600);
			}
		};
		return listViewOnClickListener;
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
			case R.id.action_discard:
				deleteAllItemsOnList();
				break;
				
			case R.id.action_share2:
				break;
	
			case android.R.id.home:
				finish();
				break;
				
			case R.id.action_undo:
				int lastDeleteNumber = datasource.undoDelete(listNameToShow);
				
				if(lastDeleteNumber == 0)
				{
					Toast.makeText(this, "\nSorry\n\nnothing to undo.\n\n", Toast.LENGTH_LONG).show();
				}
				else
				{
					displayItems();
				}
				break;
				
			case R.id.action_sort_toggle:
				Intent oneListIntent = new Intent(getBaseContext(), DragNDropListActivity.class);
				oneListIntent.putExtra(getString(R.string.listnametoshow), listNameToShow);
		    	startActivityForResult(oneListIntent, 105);
				break;
				
		    default:
		    	return super.onOptionsItemSelected(item);
	    }
        return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}
	
	private void deleteAllItemsOnList()
	{
		CharSequence[] items = new CharSequence[2];
		items[0] = "Yes, please delete ALL Items on this list.";
		items[1] = "Cancel";

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("Delete list " + listNameToShow);
		builder.setItems(items, new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
//				@SuppressWarnings("unchecked")
//				ArrayAdapter<OneListItem> adapter = (ArrayAdapter<OneListItem>) getListAdapter();
				
				if(which == 0) // delete
				{
					datasource.deleteAll(listNameToShow);
					displayItems();
				}
				else // cancel
				{
					return;
				}
			}
		});
		
		AlertDialog alert = builder.create();
		alert.show();
		alert.getWindow().setLayout(400, 400);
	}
	
	public void onClick(View view)
	{
		SimpleAdapter adapter = (SimpleAdapter) getListAdapter();
		
		switch (view.getId())
		{
			case R.id.add:
				AutoCompleteTextView tv = (AutoCompleteTextView)findViewById(R.id.actv);
				String text = tv.getText().toString();
				
				if(text == null || text.trim().length() == 0) return;
				
				tv.setText("");
				
				datasource.createComment(listNameToShow, text, 1);
				
				knownItemsDao.createKnownItem(text);
				loadKnownItemsView();
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