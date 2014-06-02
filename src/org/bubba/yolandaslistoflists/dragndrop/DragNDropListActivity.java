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
import java.util.List;

import org.bubba.yolandaslistoflists.OneListItem;
import org.bubba.yolandaslistoflists.R;
import org.bubba.yolandaslistoflists.sql.ListOfListsDataSource;

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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DragNDropListActivity extends ListActivity
{
	private ListOfListsDataSource datasource;
	private String listNameToShow; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dragndroplistview);

        listNameToShow = (String) getIntent().getExtras().get(getString(R.string.listnametoshow));
        
        datasource = new ListOfListsDataSource(this);
		datasource.open();
		List<OneListItem> itemsInList = datasource.getAllItemsForOneListSortByNumber(listNameToShow);
        
        ArrayList<String> content = new ArrayList<String>(itemsInList.size());
        for (int i=0; i < itemsInList.size(); i++)
        {
        	if(!"".equals(itemsInList.get(i).getItem()))
        		content.add(itemsInList.get(i).getItem());
        }
        
        setListAdapter(new DragNDropAdapter(this, new int[]{R.layout.dragitem}, new int[]{R.id.TextView01}, content));//new DragNDropAdapter(this,content)
        ListView listView = getListView();
        
        if (listView instanceof DragNDropListView) {
        	((DragNDropListView) listView).setDropListener(mDropListener);
        	((DragNDropListView) listView).setRemoveListener(mRemoveListener);
        	((DragNDropListView) listView).setDragListener(mDragListener);
        }
        
		getActionBar().show();
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
    }

	private DropListener mDropListener = 
		new DropListener() {
        public void onDrop(int from, int to) {
        	ListAdapter adapter = getListAdapter();
        	if (adapter instanceof DragNDropAdapter) {
        		((DragNDropAdapter)adapter).onDrop(from, to);
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
				TextView iv = (TextView)itemView.findViewById(R.id.itemTextView);
				if (iv != null) iv.setVisibility(View.INVISIBLE);
			}

			public void onStopDrag(View itemView) {
				itemView.setVisibility(View.VISIBLE);
				itemView.setBackgroundColor(defaultBackgroundColor);
				TextView iv = (TextView)itemView.findViewById(R.id.itemTextView);
				if (iv != null) iv.setVisibility(View.VISIBLE);
			}
    	
    };
    
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
//					displayItems();
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
				ArrayAdapter<OneListItem> adapter = (ArrayAdapter<OneListItem>) getListAdapter();
				
				if(which == 0) // delete
				{
//					datasource.deleteAll(listNameToShow);
//					displayItems();
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
    
//    private static String[] mListContent={"Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7"};
}