package org.bubba.yolandaslistoflists.sql;

import java.util.ArrayList;
import java.util.List;

import org.bubba.yolandaslistoflists.OneListItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.widget.ArrayAdapter;

public class ListOfListsDataSource
{
	private SQLiteDatabase database;
	private YolandasSqlHelper dbHelper;
	private String[] allColumns =
	{ YolandasSqlHelper.COLUMN_ID,
			YolandasSqlHelper.COLUMN_LIST_NAME,
			YolandasSqlHelper.COLUMN_ITEM,
			YolandasSqlHelper.COLUMN_QUANTITY,
			YolandasSqlHelper.COLUMN_DELETED_NUMBER,
			YolandasSqlHelper.COLUMN_SORT_ON_THIS_NUMBER};

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
		int nextNbr = getBiggestSortOnThisNumber(listName) + 1;
		
		ContentValues values = new ContentValues();
		values.put(YolandasSqlHelper.COLUMN_LIST_NAME, listName);
		values.put(YolandasSqlHelper.COLUMN_ITEM, item);
		values.put(YolandasSqlHelper.COLUMN_QUANTITY, quantity);
		values.put(YolandasSqlHelper.COLUMN_DELETED_NUMBER, 0);
		values.put(YolandasSqlHelper.COLUMN_SORT_ON_THIS_NUMBER, getNbr(nextNbr));
		
		long insertId = database.insert(YolandasSqlHelper.LIST_OF_LISTS_TABLE, null, values);
		
		Cursor cursor = database.query(YolandasSqlHelper.LIST_OF_LISTS_TABLE,
				allColumns, YolandasSqlHelper.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
		OneListItem newComment = cursorToComment(cursor);
		cursor.close();
		return newComment;
	}

	public void deleteComment(OneListItem oneListItem)
	{
		database.delete(YolandasSqlHelper.LIST_OF_LISTS_TABLE,
				YolandasSqlHelper.COLUMN_LIST_NAME + " = '" + oneListItem.getListName() + "'", null);
	}

	public List<OneListItem> getAllComments()
	{
		List<OneListItem> listItems = new ArrayList<OneListItem>();

		Cursor cursor = database.query(YolandasSqlHelper.LIST_OF_LISTS_TABLE,
				allColumns, null, null, YolandasSqlHelper.COLUMN_LIST_NAME, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			OneListItem comment = cursorToComment(cursor);
			listItems.add(comment);
			System.err.println(comment.myToString());
			cursor.moveToNext();
		}

		cursor.close();
		return listItems;
	}

//	public List<OneListItem> getAllLists()
//	{
//		List<OneListItem> comments = new ArrayList<OneListItem>();
//
//		Cursor cursor = database.query(YolandasSqlHelper.LIST_OF_LISTS_TABLE,
//				allColumns, YolandasSqlHelper.COLUMN_LIST_NAME, null, null, null, null);
//
//		cursor.moveToFirst();
//		while (!cursor.isAfterLast())
//		{
//			OneListItem comment = cursorToComment(cursor);
//			comments.add(comment);
//			cursor.moveToNext();
//		}
//		// make sure to close the cursor
//		cursor.close();
//		return comments;
//	}

	public List<OneListItem> getAllItemsForOneList(String listName)
	{
		List<OneListItem> listItems = new ArrayList<OneListItem>();

		Cursor cursor = database.query(
				YolandasSqlHelper.LIST_OF_LISTS_TABLE,
				allColumns, 
				YolandasSqlHelper.COLUMN_LIST_NAME + " = '" + listName + "' and "
				  + YolandasSqlHelper.COLUMN_DELETED_NUMBER + " = '0' ", 
				null, 
				null, 
				null, 
				YolandasSqlHelper.COLUMN_ITEM);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			OneListItem comment = cursorToComment(cursor);
			listItems.add(comment);
			System.err.println(comment.myToString());
			cursor.moveToNext();
		}

		cursor.close();
		return listItems;
	}

	public List<OneListItem> getAllItemsForOneListSortByNumber(String listName)
	{
		List<OneListItem> listItems = new ArrayList<OneListItem>();

		Cursor cursor = database.query(
				YolandasSqlHelper.LIST_OF_LISTS_TABLE,
				allColumns, 
				YolandasSqlHelper.COLUMN_LIST_NAME + " = '" + listName + "' and "
				  + YolandasSqlHelper.COLUMN_DELETED_NUMBER + " = '0' ", 
				null, 
				null, 
				null, 
				YolandasSqlHelper.COLUMN_SORT_ON_THIS_NUMBER);

		cursor.moveToFirst();
		
		while (!cursor.isAfterLast())
		{
			OneListItem comment = cursorToComment(cursor);
			listItems.add(comment);
			System.err.println(comment.myToString());
			cursor.moveToNext();
		}

		cursor.close();
		return listItems;
	}

	private OneListItem cursorToComment(Cursor cursor)
	{
		OneListItem comment = new OneListItem();
		comment.setId(cursor.getLong(0));
		comment.setListName(cursor.getString(1));
		comment.setItem(cursor.getString(2));
		comment.setQuantity(Integer.parseInt(cursor.getString(3)));
		comment.setDeletedNumber(Integer.parseInt(cursor.getString(4)));
		comment.setSortOnThisNumber(cursor.getString(5));
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

	public void deleteAll(String listNameToShow)
	{
		int nextDeleteNumber = getBiggestDeleteNumber(listNameToShow) + 1;

		String sqlStmt = "UPDATE " + YolandasSqlHelper.LIST_OF_LISTS_TABLE
				+ " set " + YolandasSqlHelper.COLUMN_DELETED_NUMBER + " = '" + nextDeleteNumber + "' "
				+ "where " + YolandasSqlHelper.COLUMN_LIST_NAME + " = '" + listNameToShow + "'"
	    		+ " and " + YolandasSqlHelper.COLUMN_DELETED_NUMBER + " = '0'";
		
		final SQLiteStatement stmt = database.compileStatement(sqlStmt );

		//int recordsUpdated = 
		stmt.executeUpdateDelete();
	}

	public int undoDelete(String listNameToShow)
	{
		int lastDeleteNumber = getBiggestDeleteNumber(listNameToShow);

		if(lastDeleteNumber == 0) return 0;
		
		String sqlStmt = "UPDATE " + YolandasSqlHelper.LIST_OF_LISTS_TABLE
				+ " set " + YolandasSqlHelper.COLUMN_DELETED_NUMBER + " = '0' "
				+ "where " + YolandasSqlHelper.COLUMN_LIST_NAME + " = '" + listNameToShow + "'"
		    	+ " and " + YolandasSqlHelper.COLUMN_DELETED_NUMBER + " = '" + lastDeleteNumber + "'";
		
		final SQLiteStatement stmt = database.compileStatement(sqlStmt );

		int recordsUpdated = stmt.executeUpdateDelete();
		return recordsUpdated;
	}
	
	public void deleteComment(String listNameToShow, String item)
	{
		int nextDeleteNumber = getBiggestDeleteNumber(listNameToShow) + 1;
		
		String sqlStmt = "UPDATE " + YolandasSqlHelper.LIST_OF_LISTS_TABLE
				+ " set " + YolandasSqlHelper.COLUMN_DELETED_NUMBER + " = '" + nextDeleteNumber + "' "
	    		+ "where " + YolandasSqlHelper.COLUMN_LIST_NAME + " = '" + listNameToShow + "'"
	    		+ " and item = '" + item + "'";
		
		final SQLiteStatement stmt = database.compileStatement(sqlStmt );

		//int recordsUpdated = 
		stmt.executeUpdateDelete();
	}

	private int getBiggestSortOnThisNumber(String listNameToShow)
	{
	    String sqlStmt = "SELECT MAX("+ YolandasSqlHelper.COLUMN_SORT_ON_THIS_NUMBER
	    	+ ") FROM " + YolandasSqlHelper.LIST_OF_LISTS_TABLE
	    	+ " where " + YolandasSqlHelper.COLUMN_LIST_NAME + " = '" + listNameToShow + "'";
	    
		final SQLiteStatement stmt = database.compileStatement(
	    	sqlStmt);

	    return (int) stmt.simpleQueryForLong();
	}

	private int getBiggestDeleteNumber(String listNameToShow)
	{
	    String sqlStmt = "SELECT MAX("+ YolandasSqlHelper.COLUMN_DELETED_NUMBER
    		+ ") FROM " + YolandasSqlHelper.LIST_OF_LISTS_TABLE
    		+ " where " + YolandasSqlHelper.COLUMN_LIST_NAME + " = '" + listNameToShow + "'";
	    
		final SQLiteStatement stmt = database.compileStatement(
	    	sqlStmt);

	    return (int) stmt.simpleQueryForLong();
	}

	public OneListItem getItem(String listName, String item)
	{
		List<OneListItem> listItems = new ArrayList<OneListItem>();

		Cursor cursor = database.query(
				YolandasSqlHelper.LIST_OF_LISTS_TABLE,
				allColumns, 
				YolandasSqlHelper.COLUMN_LIST_NAME + " = '" + listName + "' and "
				  + YolandasSqlHelper.COLUMN_DELETED_NUMBER + " = '0' and "
				  + YolandasSqlHelper.COLUMN_ITEM + " = '" + item + "'", 
				null, 
				null, 
				null, 
				YolandasSqlHelper.COLUMN_ITEM);

		cursor.moveToFirst();
		
		while (!cursor.isAfterLast())
		{
			OneListItem comment = cursorToComment(cursor);
			listItems.add(comment);
			System.err.println(comment.myToString());
			cursor.moveToNext();
		}

		cursor.close();
		if(listItems.isEmpty()) return null;
		return listItems.get(0);
	}

	public int updateItem(OneListItem oneItem)
	{
		String sqlStmt = "UPDATE " + YolandasSqlHelper.LIST_OF_LISTS_TABLE
				+ " set " + YolandasSqlHelper.COLUMN_QUANTITY + " = '" + oneItem.getQuantity() + "' "
		    		+ "where " + YolandasSqlHelper.COLUMN_ID + " = '" + oneItem.getId() + "'";
		
		final SQLiteStatement stmt = database.compileStatement(sqlStmt );

		int recordsUpdated = stmt.executeUpdateDelete();
		return recordsUpdated;
	}

	public int updateSortByThisNumber(String listNameToShow, String item, int newNbr)
	{
		String sqlStmt = "UPDATE " + YolandasSqlHelper.LIST_OF_LISTS_TABLE
			+ " set " + YolandasSqlHelper.COLUMN_SORT_ON_THIS_NUMBER + " = '" + getNbr(newNbr) + "' "
			+ "where " + YolandasSqlHelper.COLUMN_ITEM + " = '" + item + "' "
			+ "and " + YolandasSqlHelper.COLUMN_LIST_NAME + " = '" + listNameToShow + "'";
	
		final SQLiteStatement stmt = database.compileStatement(sqlStmt );
		
		int recordsUpdated = stmt.executeUpdateDelete();
		return recordsUpdated;
	}

	private String getNbr(int newNbr)
	{
		String nbr = "000000" + newNbr; 
				
		String substring = nbr.substring(("" + newNbr).length());
		System.err.println("blah '" + substring + "'");
		return substring;
	}
}