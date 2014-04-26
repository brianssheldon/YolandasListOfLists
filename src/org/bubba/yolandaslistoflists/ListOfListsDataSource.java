package org.bubba.yolandaslistoflists;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class ListOfListsDataSource
{
	private SQLiteDatabase database;
	private YolandasSqlHelper dbHelper;
	private String[] allColumns =
	{ YolandasSqlHelper.COLUMN_ID, YolandasSqlHelper.COLUMN_LIST_NAME, YolandasSqlHelper.COLUMN_ITEM, YolandasSqlHelper.COLUMN_QUANTITY };

	public ListOfListsDataSource(Context context)
	{
		System.err.println("CommentsDataSource constructor");
		dbHelper = new YolandasSqlHelper(context);
	}

	public void open() throws SQLException
	{
		System.err.println("CommentsDataSource open");
		database = dbHelper.getWritableDatabase();
	}

	public void close()
	{
		System.err.println("CommentsDataSource close");
		dbHelper.close();
	}

	public OneListItem createComment(String listName, String item, int quantity)
	{
		ContentValues values = new ContentValues();
		values.put(YolandasSqlHelper.COLUMN_LIST_NAME, listName);
		values.put(YolandasSqlHelper.COLUMN_ITEM, item);
		values.put(YolandasSqlHelper.COLUMN_QUANTITY, quantity);
		
		long insertId = database.insert(YolandasSqlHelper.TABLE_COMMENTS, null, values);
		
		Cursor cursor = database.query(YolandasSqlHelper.TABLE_COMMENTS,
				allColumns, YolandasSqlHelper.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
		OneListItem newComment = cursorToComment(cursor);
		cursor.close();
		return newComment;
	}

	public void deleteComment(OneListItem oneListItem)
	{
		database.delete(YolandasSqlHelper.TABLE_COMMENTS,
				YolandasSqlHelper.COLUMN_LIST_NAME + " = '" + oneListItem.getListName() + "'", null);
	}

	public List<OneListItem> getAllComments()
	{
		List<OneListItem> listItems = new ArrayList<OneListItem>();

		Cursor cursor = database.query(YolandasSqlHelper.TABLE_COMMENTS,
				allColumns, null, null, YolandasSqlHelper.COLUMN_LIST_NAME, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			OneListItem comment = cursorToComment(cursor);
			listItems.add(comment);
			System.err.println(comment.myToString());
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return listItems;
	}

	public List<OneListItem> getAllLists()
	{
		List<OneListItem> comments = new ArrayList<OneListItem>();

		Cursor cursor = database.query(YolandasSqlHelper.TABLE_COMMENTS,
				allColumns, YolandasSqlHelper.COLUMN_LIST_NAME, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			OneListItem comment = cursorToComment(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return comments;
	}

	public List<OneListItem> getAllItemsForOneList(String listName)
	{
		List<OneListItem> listItems = new ArrayList<OneListItem>();
//
//		Cursor cursor = database.query(YolandasSqlHelper.TABLE_COMMENTS,
//				allColumns, null, null, YolandasSqlHelper.COLUMN_LIST_NAME, null, null);
		Cursor cursor = database.query(
				YolandasSqlHelper.TABLE_COMMENTS,
				allColumns, 
				YolandasSqlHelper.COLUMN_LIST_NAME + " = '" + listName + "'", 
				null, 
				null, 
				null, 
				YolandasSqlHelper.COLUMN_LIST_NAME);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			OneListItem comment = cursorToComment(cursor);
			listItems.add(comment);
			System.err.println(comment.myToString());
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return listItems;
		
		
		
//		System.err.println("getAllForOneList '" + listName + "'");
//		List<OneListItem> listItems = new ArrayList<OneListItem>();
//		String[] listNameArray = {listName};
//return getAllComments();
		
		
		
		
//		Cursor cursor = database.query(YolandasSqlHelper.TABLE_COMMENTS,
//				allColumns, YolandasSqlHelper.COLUMN_LIST_NAME, null, null, null, null);
////		Cursor cursor = database.query(YolandasSqlHelper.TABLE_COMMENTS,
////				allColumns, YolandasSqlHelper.COLUMN_LIST_NAME, listNameArray, null, null, 
////				YolandasSqlHelper.COLUMN_LIST_NAME);
//
//		System.err.println("nbr of rows: " + cursor.getCount());
//		
//		cursor.moveToFirst();
//		
//		while (!cursor.isAfterLast())
//		{
//			OneListItem comment = cursorToComment(cursor);
//			listItems.add(comment);
//			cursor.moveToNext();
//			System.err.println(comment.toString());
//		}
//
//		cursor.close();
//		return listItems;
	}

	private OneListItem cursorToComment(Cursor cursor)
	{
		OneListItem comment = new OneListItem();
		comment.setId(cursor.getLong(0));
		comment.setListName(cursor.getString(1));
		comment.setItem(cursor.getString(2));
		comment.setQuantity(Integer.parseInt(cursor.getString(3)));
		return comment;
	}

	public boolean copyList(String listNameToCopy, String newName, ArrayAdapter<OneListItem> adapter)
	{
		if(getAllItemsForOneList(newName).isEmpty())
		{
			List<OneListItem> originalList = getAllItemsForOneList(listNameToCopy);
			
			for(OneListItem originalBO : originalList)
			{
				createComment(newName, originalBO.getItem(), originalBO.getQuantity());
			}
			
			OneListItem oneListItem = new OneListItem();
			oneListItem.setListName(newName);
			oneListItem.setItem("");
			adapter.add(oneListItem);
			
			return true;
		}
		
		return false;
	}
}