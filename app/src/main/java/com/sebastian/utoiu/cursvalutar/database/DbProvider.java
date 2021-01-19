package com.sebastian.utoiu.cursvalutar.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

import com.sebastian.utoiu.cursvalutar.Utils;

public class DbProvider extends Observable<DbProvider.DBObserver>
{
	private static final boolean ON_SDCARD = true;

	private DbHelper mHelper;

	private static final class Holder
	{
		private static final DbProvider sInstance = new DbProvider();
	}

	public static DbProvider getInstance()
	{
		return Holder.sInstance;
	}

	private DbProvider()
	{
		mHelper = DbHelper.newInstance( ON_SDCARD );
	}

	public Cursor query( boolean distinct, String tables, String[] projection, String selection,
						 String[] selectionArgs, String groupBy, String having, String sortOrder, String limit )
	{
		try
		{
			SQLiteDatabase db = mHelper.getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setDistinct( distinct );
			qb.setTables( tables );

			Utils.logInfo( "query on " + tables );

			return qb.query( db, projection, selection, selectionArgs, groupBy, having, sortOrder, limit );
		}
		catch( SQLiteException e )
		{
			Utils.printStackTrace( e );
			return null;
		}
	}

	public long insert( String tableName, ContentValues record )
	{
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.beginTransaction();

		try
		{
			long result = 0;

			if( record.size() > 0 )
			{
				result = db.insertWithOnConflict( tableName, BaseColumns._ID, record, SQLiteDatabase.CONFLICT_REPLACE );

				if( result > -1 )
				{
					notifyAllObservers( tableName );
				}
			}

			db.setTransactionSuccessful();

			Utils.logInfo( "record inserted into " + tableName );

			return result;
		}
		catch( Exception e )
		{
			Utils.printStackTrace( e );
			return -1;
		}
		finally
		{
			db.endTransaction();
		}
	}

	public int bulkInsert( String tableName, Iterable<ContentValues> records )
	{
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.beginTransaction();

		try
		{
			int count = 0;

			for( ContentValues record : records )
			{
				try
				{
					if( record.size() > 0 )
					{
						long result = db.insertWithOnConflict( tableName, BaseColumns._ID, record,
								SQLiteDatabase.CONFLICT_REPLACE );
						if( result > -1 )
						{
							++count;
						}
					}
				}
				catch( Exception e )
				{
					Utils.printStackTrace( e );
				}
			}

			db.setTransactionSuccessful();

			Utils.logInfo( count + " records inserted into " + tableName );

			if( count > 0 )
			{
				notifyAllObservers( tableName );
			}

			return count;
		}
		catch( Exception e )
		{
			Utils.printStackTrace( e );
			return 0;
		}
		finally
		{
			db.endTransaction();
		}
	}

	public int update( String tableName, ContentValues values, String selection, String[] selectionArgs )
	{
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.beginTransaction();

		try
		{
			int count = db.update( tableName, values, selection, selectionArgs );

			db.setTransactionSuccessful();

			Utils.logInfo( count + " records updated in " + tableName );

			if( count > 0 )
			{
				notifyAllObservers( tableName );
			}

			return count;
		}
		catch( Exception e )
		{
			Utils.printStackTrace( e );
			return 0;
		}
		finally
		{
			db.endTransaction();
		}
	}

	public int delete( String tableName, String selection, String[] selectionArgs )
	{
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.beginTransaction();

		try
		{
			int count = db.delete( tableName, selection == null ? "1" : selection, selectionArgs );

			db.setTransactionSuccessful();

			Utils.logInfo( count + " records deleted from " + tableName );

			if( count > 0 )
			{
				notifyAllObservers( tableName );
			}

			return count;
		}
		catch( Exception e )
		{
			Utils.printStackTrace( e );
			return 0;
		}
		finally
		{
			db.endTransaction();
		}
	}

	public int deleteSequential( String tableName, String selection, Iterable<String> selectionArgs )
	{
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.beginTransaction();

		try
		{
			int count = 0;

			for( String arg : selectionArgs )
			{
				try
				{
					long result = db.delete( tableName, selection == null ? "1" : selection, new String[]{ arg } );
					if( result > -1 )
					{
						++count;
					}
				}
				catch( Exception e )
				{
					Utils.printStackTrace( e );
				}
			}

			db.setTransactionSuccessful();

			Utils.logInfo( count + " records deleted from " + tableName );

			if( count > 0 )
			{
				notifyAllObservers( tableName );
			}

			return count;
		}
		catch( Exception e )
		{
			Utils.printStackTrace( e );
			return 0;
		}
		finally
		{
			db.endTransaction();
		}
	}

	public static String buildUnionQuery( boolean distinct, String[] subQueries, String sortOrder, String limit )
	{
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setDistinct( distinct );

		return builder.buildUnionQuery( subQueries, sortOrder, limit );
	}

	public void clear()
	{
		mHelper.clearAllTables();
	}

	public void notifyAllObservers( String tableName )
	{
		synchronized( this )
		{
			for( DBObserver observer : getObservers() )
			{
				observer.reload( tableName );
			}
		}
	}

	public static interface DBObserver
	{
		public void reload( String tableName );
	}
}
