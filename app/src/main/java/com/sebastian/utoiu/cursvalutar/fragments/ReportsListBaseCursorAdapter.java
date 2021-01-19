package com.sebastian.utoiu.cursvalutar.fragments;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sebastian.utoiu.cursvalutar.App;
import com.sebastian.utoiu.cursvalutar.DateHelper;
import com.sebastian.utoiu.cursvalutar.R;
import com.sebastian.utoiu.cursvalutar.database.DB;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class ReportsListBaseCursorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

	public static final int TYPE_GRAPH = 0;
	public static final int TYPE_MIN_MAX = 1;
	private Cursor mCursor;
	private int mRowIDColumn;

	public ReportsListBaseCursorAdapter( Cursor cursor, boolean hasStableIds )
	{
		mCursor = cursor;
		mRowIDColumn = cursor == null ? -1 : cursor.getColumnIndexOrThrow( BaseColumns._ID );

		setHasStableIds( hasStableIds );
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType )
	{
		View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.reports_list_item, parent, false );
		ListOfReportsViewHolder holder = new ListOfReportsViewHolder( view );

		return holder;
	}

	@Override
	public void onBindViewHolder( @NonNull RecyclerView.ViewHolder holder, int position )
	{

		if( mCursor != null )
		{
			mCursor.moveToPosition( position );

			String baseCurrency;
			String currency;
			String startDate;
			String endDate;
			String timeStamp;
			String dateWhenGenerated;

			String reportName;
			if( reportType() == TYPE_GRAPH )
			{
				baseCurrency = mCursor.getString( mCursor.getColumnIndex( DB.GraphReport.BASE_CURRENCY ) );
				currency = mCursor.getString( mCursor.getColumnIndex( DB.GraphReport.CURRECY ) );
				startDate = mCursor.getString( mCursor.getColumnIndex( DB.GraphReport.START_DATE ) );
				endDate = mCursor.getString( mCursor.getColumnIndex( DB.GraphReport.END_DATE ) );
				timeStamp = mCursor.getString( mCursor.getColumnIndex( DB.GraphReport.TIMESTAMP ) );
				dateWhenGenerated = DateHelper.getDisplayDateFromTimestamp( timeStamp );

				reportName = App.context.getString( R.string.graph_report_for ) + baseCurrency + "/" + currency;
			}
			else
			{
				baseCurrency = mCursor.getString( mCursor.getColumnIndex( DB.ListReport.BASE_CURRENCY ) );
				startDate = mCursor.getString( mCursor.getColumnIndex( DB.ListReport.START_DATE ) );
				endDate = mCursor.getString( mCursor.getColumnIndex( DB.ListReport.END_DATE ) );
				timeStamp = mCursor.getString( mCursor.getColumnIndex( DB.ListReport.TIMESTAMP ) );
				dateWhenGenerated = DateHelper.getDisplayDateFromTimestamp( timeStamp );

				reportName = App.context.getString( R.string.min_max_values_for_all_currencies_vs ) + baseCurrency;
			}

			ListOfReportsViewHolder reportsViewHolder = (ListOfReportsViewHolder) holder;
			reportsViewHolder.startDate.setText( App.context.getString( R.string.start_date ) + startDate );
			reportsViewHolder.endDate.setText( App.context.getString( R.string.end_date ) + endDate );
			reportsViewHolder.reportName.setText( reportName );
			reportsViewHolder.dateWhenGenerated.setText( App.context.getString( R.string.generated_at ) + dateWhenGenerated );

			reportsViewHolder.itemView.setOnClickListener( new View.OnClickListener()
			{
				@Override
				public void onClick( View view )
				{
					onItemClick( mCursor, timeStamp );
				}
			} );
		}
	}

	protected abstract void onItemClick( Cursor cursor, String timestamp );

	protected abstract int reportType();

	@Override
	public int getItemCount()
	{
		return mCursor == null ? 0 : mCursor.getCount();
	}

	@Override
	public long getItemId( int position )
	{
		if( mCursor != null && mCursor.moveToPosition( position ) )
		{
			return mCursor.getLong( mRowIDColumn );
		}
		else
		{
			return 0;
		}
	}

	public Cursor getCursor()
	{
		return mCursor;
	}

	public Object getItem( int position )
	{
		if( mCursor != null )
		{
			mCursor.moveToPosition( position );
		}

		return mCursor;
	}

	public void swapCursor( Cursor newCursor )
	{
		if( newCursor != mCursor )
		{
			Cursor oldCursor = mCursor;
			mCursor = newCursor;
			mRowIDColumn = newCursor == null ? -1 : newCursor.getColumnIndexOrThrow( "_id" );
			notifyDataSetChanged();
			if( oldCursor != null )
			{
				oldCursor.close();
			}
		}
	}

	public static class ListOfReportsViewHolder extends RecyclerView.ViewHolder
	{

		TextView startDate;
		TextView endDate;
		TextView reportName;
		TextView dateWhenGenerated;

		public ListOfReportsViewHolder( @NonNull View itemView )
		{
			super( itemView );

			startDate = itemView.findViewById( R.id.startDate );
			endDate = itemView.findViewById( R.id.endDate );
			dateWhenGenerated = itemView.findViewById( R.id.report_generated_at );
			reportName = itemView.findViewById( R.id.report_name );
		}
	}
}