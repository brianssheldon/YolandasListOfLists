package org.bubba.yolandaslistoflists;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class YolandasSqlHelper extends SQLiteOpenHelper
{
	public static final String TABLE_COMMENTS = "comments";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_LIST_NAME = "listName";
	public static final String COLUMN_ITEM = "item";
	public static final String COLUMN_QUANTITY = "quantity";
	public static final String COLUMN_DELETED_NUMBER = "deletedNumber";

	private static final String DATABASE_NAME = "commments.db";
	private static final int DATABASE_VERSION = 2;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_COMMENTS + "(" 
				+ COLUMN_ID 			+ " integer primary key autoincrement, " 
				+ COLUMN_LIST_NAME		+ " text not null,"
				+ COLUMN_ITEM 			+ " text not null,"
				+ COLUMN_QUANTITY 		+ " text not null,"
				+ COLUMN_DELETED_NUMBER + " text not null);";

	public YolandasSqlHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(YolandasSqlHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
		onCreate(db);
	}
}
