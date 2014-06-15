/*
 * Copyright (C) 2010 Eric Harlow
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bubba.yolandaslistoflists.dragndrop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bubba.yolandaslistoflists.KnownItem;
import org.bubba.yolandaslistoflists.OneListActivity;
import org.bubba.yolandaslistoflists.OneListItem;
import org.bubba.yolandaslistoflists.R;
import org.bubba.yolandaslistoflists.prefs.PrefsBO;
import org.bubba.yolandaslistoflists.prefs.PrefsDao;
import org.bubba.yolandaslistoflists.sql.KnownItemsDao;
import org.bubba.yolandaslistoflists.sql.ListOfListsDataSource;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DragNDropListActivity extends ListActivity
{
	private ListOfListsDataSource datasource;
	private KnownItemsDao knownItemsDao;
	public static String listNameToShow; 
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dndmain);//R.layout.dragndroplistview);

        listNameToShow = (String) getIntent().getExtras().get(getString(R.string.listnametoshow));
        
        openDb();
		
		loadKnownItemsView();
		createDndList();
        
		getActionBar().show();
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
	   
	   AutoCompleteTextView actvDev = (AutoCompleteTextView)findViewById(R.id.dndactv);
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

			((AutoCompleteTextView)findViewById(R.id.dndactv)).setText("");

			createDndList();
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

	private void createDndList()
	{
		List<OneListItem> itemsInList = datasource.getAllItemsForOneListSortByNumber(listNameToShow);
        
        ArrayList<String> content = new ArrayList<String>(itemsInList.size());
        for (int i=0; i < itemsInList.size(); i++)
        {
        	if(!"".equals(itemsInList.get(i).getItem()))
        		content.add(itemsInList.get(i).getItem());
        }
        
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

        DragNDropAdapter adapter = new DragNDropAdapter(this, 
        	new int[]{R.layout.dragitem}, 
			new String[] {"oneItem", "oneQuantity"}, 
			new int[] {R.id.oneItem, R.id.oneQuantity},
        	mylist);
		
        setListAdapter(adapter);
        ListView listView = getListView();
        
        if (listView instanceof DragNDropListView) {
        	((DragNDropListView) listView).setDropListener(mDropListener);
        	((DragNDropListView) listView).setRemoveListener(mRemoveListener);
        	((DragNDropListView) listView).setDragListener(mDragListener);
        }
		listView.setOnItemClickListener(getItemClickListener());
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

						createDndList();
					}
				});
		        
		        AlertDialog alert = builder.create();
		        alert.show();
		        alert.getWindow().setLayout(400, 600);
			}
		};
		return listViewOnClickListener;
	}

	private DropListener mDropListener = 
		new DropListener() {
        public void onDrop(int from, int to, String listNameToShow) {
        	ListAdapter adapter = getListAdapter();
        	if (adapter instanceof DragNDropAdapter) {
        		((DragNDropAdapter)adapter).onDrop(from, to, listNameToShow);
        		getListView().invalidateViews();
        	}
        }
    };
    
    private RemoveListener mRemoveListener =
        new RemoveListener() {
        public void onRemove(int which) {
        	ListAdapter adapter = getListAdapter();
        	if (adapter instanceof DragNDropAdapter) {
        		((DragNDropAdapter)adapter).onRemove(which);
        		getListView().invalidateViews();
        	}
        }
    };
    
    private DragListener mDragListener =
    	new DragListener() {

    	int backgroundColor = 0xe0103010;
    	int defaultBackgroundColor;
    	
			public void onDrag(int x, int y, ListView listView) {
				// TODO Auto-generated method stub
			}

			public void onStartDrag(View itemView) {
				itemView.setVisibility(View.INVISIBLE);
				defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
				itemView.setBackgroundColor(backgroundColor);
				TextView iv = (TextView)itemView.findViewById(R.id.oneItem);
				if (iv != null) iv.setVisibility(View.INVISIBLE);
			}

			public void onStopDrag(View itemView) {
				itemView.setVisibility(View.VISIBLE);
				itemView.setBackgroundColor(defaultBackgroundColor);
				TextView iv = (TextView)itemView.findViewById(R.id.oneItem);
				if (iv != null) iv.setVisibility(View.VISIBLE);
			}
    };
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.dnd_activity_actions, menu);
	    
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
					createDndList();
				}
				break;
				
			case R.id.action_sort_toggle:
				PrefsBO bo = PrefsDao.readFile(getBaseContext());
				bo.setOneListSort(PrefsBO.ALPHA_SORT_ORDER);
				PrefsDao.writeFile(bo, getBaseContext());
				
				Intent oneListIntent = new Intent(getBaseContext(), OneListActivity.class);
				oneListIntent.putExtra(getString(R.string.listnametoshow), listNameToShow);
		    	startActivityForResult(oneListIntent, 106);
				break;
				
		    default:
		    	return super.onOptionsItemSelected(item);
	    }
        return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{	// called when returning from an intent
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}
	
	public void onClick(View view)
	{
		DragNDropAdapter adapter = (DragNDropAdapter) getListAdapter();
		
		switch (view.getId())
		{
			case R.id.dndadd:
				AutoCompleteTextView tv = (AutoCompleteTextView)findViewById(R.id.dndactv);
				String text = tv.getText().toString();
				
				if(text == null || text.trim().length() == 0) return;
				
				tv.setText("");
				
				datasource.createComment(listNameToShow, text, 1);
				
				knownItemsDao.createKnownItem(text);
				loadKnownItemsView();
				createDndList();
				break;
		}
		adapter.notifyDataSetChanged();
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
				if(which == 0) // delete
				{
					datasource.deleteAll(listNameToShow);
					createDndList();
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
}