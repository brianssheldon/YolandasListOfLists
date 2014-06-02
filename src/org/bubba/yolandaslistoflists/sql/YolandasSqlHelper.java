package org.bubba.yolandaslistoflists.sql;

import android.content.Context;

public class YolandasSqlHelper extends AbstractSqlHelper
{
	public static final String LIST_OF_LISTS_TABLE = "listofliststable";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_LIST_NAME = "listName";
	public static final String COLUMN_ITEM = "item";
	public static final String COLUMN_QUANTITY = "quantity";
	public static final String COLUMN_DELETED_NUMBER = "deletedNumber";
	public static final String COLUMN_SORT_ON_THIS_NUMBER = "sortOnThisNumber";

	private static final String DATABASE_NAME = "listoflists.db";
	static final int DATABASE_VERSION = 1;

	public YolandasSqlHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
}