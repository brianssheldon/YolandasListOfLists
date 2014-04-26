package org.bubba.yolandaslistoflists;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class OneListActivity extends ListActivity
{
	private ListOfListsDataSource datasource;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.onelistmain);
		
		String listNameToShow = (String) getIntent().getExtras().get(getString(R.string.listnametoshow));
		System.err.println("OneListActivity listNameToshow '" + listNameToShow + "'");

		datasource = new ListOfListsDataSource(this);
		datasource.open();

//		List<OneListItem> itemsInList = datasource.getAllComments();//getAllItemsForOneList(listNameToShow);

		List<OneListItem> itemsInList = datasource.getAllItemsForOneList(listNameToShow);
		List<String> itemList = new ArrayList<String>();
		for(OneListItem item : itemsInList)
		{
			itemList.add(item.getItem());
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, R.layout.list_item, itemList);
		setListAdapter(adapter);
		// use the SimpleCursorAdapter to show the elements in a ListView
//		ArrayAdapter<OneListItem> adapter = new ArrayAdapter<OneListItem>(this,
//				android.R.layout.simple_list_item_1, values);
//		
//		setListAdapter(adapter);
//		getListView().setOnItemClickListener(getItemClickListener());
	}
	
	// Will be called via the onClick attribute of the buttons in main.xml
//	public void onClick(View view)
//	{
//		@SuppressWarnings("unchecked")
//		ArrayAdapter<OneListItem> adapter = (ArrayAdapter<OneListItem>) getListAdapter();
//		OneListItem comment = null;
//		
//		System.err.println("clickkk: '" + view.getId() + "'");
//		
//		switch (view.getId())
//		{
//		case R.id.add:
//			String[] comments = new String[] { "Cool", "Very nice", "Hate it" };
//			int nextInt = new Random().nextInt(3);
//			comment = datasource.createComment(comments[nextInt], "itemName", nextInt);
//			adapter.add(comment);
//			break;
//			
//		case R.id.delete:
//			if (getListAdapter().getCount() > 0)
//			{
//				comment = (OneListItem) getListAdapter().getItem(0);
//				datasource.deleteComment(comment);
//				adapter.remove(comment);
//			}
//			break;
//		}
//		adapter.notifyDataSetChanged();
//	}

//	public OnItemClickListener getItemClickListener()
//	{
//		OnItemClickListener listViewOnClickListener = new OnItemClickListener()
//		{
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
//			{
//				OneListItem comment = (OneListItem) getListAdapter().getItem(arg2);
//				System.err.println("this one? " + comment.myToString());
//				
//				Intent bigListIntent = new Intent(this, BigListActivity.class);
//		    	startActivityForResult(bigListIntent, 101);
//			}
//		};
//		return listViewOnClickListener;
//	}

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
