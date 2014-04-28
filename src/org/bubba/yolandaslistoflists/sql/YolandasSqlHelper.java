package org.bubba.yolandaslistoflists.sql;

import android.content.Context;

public class YolandasSqlHelper extends AbstractSqlHelper
{
	public static final String TABLE_COMMENTS = "comments";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_LIST_NAME = "listName";
	public static final String COLUMN_ITEM = "item";
	public static final String COLUMN_QUANTITY = "quantity";
	public static final String COLUMN_DELETED_NUMBER = "deletedNumber";

	private static final String DATABASE_NAME = "commments.db";
	private static final int DATABASE_VERSION = 2;

	public YolandasSqlHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
}