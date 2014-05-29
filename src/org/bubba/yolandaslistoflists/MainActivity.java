package org.bubba.yolandaslistoflists;

import java.util.List;

import org.bubba.yolandaslistoflists.sql.KnownItemsDao;
import org.bubba.yolandaslistoflists.sql.ListOfListsDataSource;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity
{
	private ListOfListsDataSource datasource;
	private KnownItemsDao knownItemsDao;
	private OneListItem oneListItemBO = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		datasource = new ListOfListsDataSource(this);
		datasource.open();
		knownItemsDao = new KnownItemsDao(this);
		knownItemsDao.open();
		displayList();
	}

	private void displayList()
	{
		List<OneListItem> values = datasource.getAllComments();
		
		// use the SimpleCursorAdapter to show the elements in a ListView
		ArrayAdapter<OneListItem> adapter = new ArrayAdapter<OneListItem>(this,
				android.R.layout.simple_list_item_1, values);
		
		setListAdapter(adapter);
		getListView().setOnItemClickListener(getItemClickListener());
		getListView().setOnItemLongClickListener(getOnItemLongClickListener());
		
		getActionBar().show();
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    switch (item.getItemId())
	    {
		case android.R.id.home:
			finish();
			break;
			
		case R.id.action_add:
			alertDialogAddList();
			break;
			
		case R.id.action_discard:
			if(oneListItemBO == null)
			{
				Toast.makeText(getBaseContext(), 
						"'Long Press' a list to Delete",
						Toast.LENGTH_SHORT).show();
				break;
			}
			discardList(getListView(), oneListItemBO);
			break;
			
		case R.id.action_copy:
			if(oneListItemBO == null)
			{
				Toast.makeText(getBaseContext(), 
						"'Long Press' a list to Copy",
						Toast.LENGTH_SHORT).show();
				break;
			}
			alertDialogCopy(oneListItemBO.getListName());
			break;
	 
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	    
	    oneListItemBO = null;
	    
        return true;
	}	

	public OnItemClickListener getItemClickListener()
	{
		OnItemClickListener listViewOnClickListener = new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				final OneListItem oneListItemBO2 = (OneListItem) getListAdapter().getItem(arg2);
				
				Intent oneListIntent = new Intent(arg0.getContext(), OneListActivity.class);
				oneListIntent.putExtra(getString(R.string.listnametoshow), oneListItemBO2.getListName());
		    	startActivityForResult(oneListIntent, 101);
			}
		};
		return listViewOnClickListener;
	}

	private OnItemLongClickListener getOnItemLongClickListener()
	{
		OnItemLongClickListener oilcl = new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
			{
				oneListItemBO = (OneListItem) getListAdapter().getItem(position);
				
				if(oneListItemBO == null || oneListItemBO.getListName() == null || oneListItemBO.getListName().length() == 0)
				{
					Toast.makeText(getBaseContext(), 
							"Error error error" + oneListItemBO.getListName(), 
							Toast.LENGTH_SHORT).show();
					return false;
				}
				
				int x = oneListItemBO.getListName().length();
				
				String blanks = "";
				if(x < 10) blanks = "          ".substring(x);
				
				Toast.makeText(getBaseContext(), 
					"Click icon to\n      delete\n         or\n      copy\n " + blanks + oneListItemBO.getListName(), 
					Toast.LENGTH_SHORT).show();
				return true;
			}	
		};
		return oilcl;
	}

	void discardList(AdapterView<?> arg0,
			final OneListItem oneListItemBO)
	{
		CharSequence[] items = new CharSequence[2];
		items[0] = "Delete Item   '" + oneListItemBO.getListName() + "' ?";
		items[1] = "Cancel";

		AlertDialog.Builder builder = new AlertDialog.Builder(arg0.getContext());
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("Delete list   '" + oneListItemBO.getListName() + "'");
		builder.setItems(items, new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				ArrayAdapter<OneListItem> adapter = (ArrayAdapter<OneListItem>) getListAdapter();
				
				if(which == 0) // delete
				{
					datasource.deleteComment(oneListItemBO);
					adapter.remove(oneListItemBO);
					adapter.notifyDataSetChanged();
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
	
	void alertDialogCopy(final String listNameToCopy)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(2, 2, 2, 2);

        TextView tv = new TextView(this);
        tv.setText("Copy List");
        tv.setPadding(40, 40, 40, 40);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);

        final EditText et = new EditText(this);
        et.setText(listNameToCopy);
        TextView tv1 = new TextView(this);
        tv1.setText("New list name: ");

        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv1Params.bottomMargin = 5;
        layout.addView(tv1,tv1Params);
        layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setCustomTitle(tv);

        // Setting Negative "Cancel" Button
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        // Setting Positive "Yes" Button
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) 
            {
        		ArrayAdapter<OneListItem> adapter = (ArrayAdapter<OneListItem>) getListAdapter();
			
        		if(datasource.copyList(listNameToCopy, et.getText().toString(), adapter))
				{
//        			displayList();
					adapter.notifyDataSetChanged();
				}
				else
				{
					Toast.makeText(getBaseContext(), "\nUnable to copy.\n\nDoes '" + et.getText().toString() + "' already exist?\n", Toast.LENGTH_LONG).show();
				}
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
	}

	void alertDialogAddList()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(2, 2, 2, 2);

        TextView tv = new TextView(this);
        tv.setText("New List");
        tv.setPadding(40, 40, 40, 40);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);

        final EditText et = new EditText(this);
        et.setText("");
        TextView tv1 = new TextView(this);
        tv1.setText("New list name: ");

        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv1Params.bottomMargin = 5;
        layout.addView(tv1,tv1Params);
        layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setCustomTitle(tv);

        // Setting Negative "Cancel" Button
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        // Setting Positive "Yes" Button
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) 
            {
        		ArrayAdapter<OneListItem> adapter = (ArrayAdapter<OneListItem>) getListAdapter();
        		
        		String newName = et.getText().toString();
        		
        		if(null == newName || "".equals(newName)) return;
        		
				datasource.createComment(newName, "", 0);
				adapter.add(new OneListItem(newName));
//				adapter.notifyDataSetChanged();
				displayList();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
	}
	
	@Override
	protected void onResume()
	{
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		datasource.close();
		super.onPause();
	}
}