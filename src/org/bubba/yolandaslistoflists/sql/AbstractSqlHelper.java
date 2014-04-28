package org.bubba.yolandaslistoflists.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class AbstractSqlHelper extends SQLiteOpenHelper
{

	protected static final String DATABASE_NAME = "yolandaslistoflists.db";
	protected static final int DATABASE_VERSION = 3;

	private static final String TABLE_CREATE_KNOWN_ITEMS = "create table if not exists "
			+ KnownItemsSqlHelper.TABLE_NAME + "(" 
				+ KnownItemsSqlHelper.COLUMN_ID + " integer primary key autoincrement, " 
				+ KnownItemsSqlHelper.COLUMN_ITEM + " text not null);";

	// Database creation sql statement
	private static final String TABLE_CREATE_LIST_OF_LISTS = "create table "
			+ YolandasSqlHelper.TABLE_COMMENTS + "(" 
				+ YolandasSqlHelper.COLUMN_ID 			+ " integer primary key autoincrement, " 
				+ YolandasSqlHelper.COLUMN_LIST_NAME		+ " text not null,"
				+ YolandasSqlHelper.COLUMN_ITEM 			+ " text not null,"
				+ YolandasSqlHelper.COLUMN_QUANTITY 		+ " text not null,"
				+ YolandasSqlHelper.COLUMN_DELETED_NUMBER + " text not null);";

	public AbstractSqlHelper(Context context, String name, CursorFactory factory, int version)
	{
		super(context, name, factory, version);
	}
	
	public void onCreate(SQLiteDatabase db)
	{
//      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		db.execSQL(TABLE_CREATE_LIST_OF_LISTS);
		db.execSQL(TABLE_CREATE_KNOWN_ITEMS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(YolandasSqlHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
		onCreate(db);
	}
}