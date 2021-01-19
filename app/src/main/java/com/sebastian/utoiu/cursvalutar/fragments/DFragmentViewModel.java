package com.sebastian.utoiu.cursvalutar.fragments;

import android.database.Cursor;
import android.util.Pair;

import com.sebastian.utoiu.cursvalutar.Utils;
import com.sebastian.utoiu.cursvalutar.database.DB;
import com.sebastian.utoiu.cursvalutar.database.DbProvider;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import static com.sebastian.utoiu.cursvalutar.fragments.ReportsListBaseCursorAdapter.TYPE_GRAPH;
import static com.sebastian.utoiu.cursvalutar.fragments.ReportsListBaseCursorAdapter.TYPE_MIN_MAX;

public class DFragmentViewModel extends BaseLiveViewModel
{

	private MutableLiveData<Pair<Integer, Cursor> > cursorLiveData = new MutableLiveData<>();
	private MutableLiveData<Exception> cursorError = new MutableLiveData<>();

	private MutableLiveData<Cursor> detailsGraphCursor = new MutableLiveData<>();
	private MutableLiveData<Cursor> detailsListCursor = new MutableLiveData<>();


	//reports list
	void loadListOfReportsGraph()
	{
		executeAsync( () -> {

			try
			{
				Cursor cursor = DbProvider.getInstance().query( false, DB.GraphReport.TABLE_NAME,
						null, null, null, null, null, null, null );
				cursorLiveData.postValue( new Pair<>( new Integer( TYPE_GRAPH ), cursor ) );
			}
			catch( Exception e )
			{
				Utils.logError( "Eroare ", e );
				cursorError.postValue( e );
			}
		} );
	}

	void loadListOfReportsMinMax()
	{
		executeAsync( () -> {

			try
			{
				Cursor cursor = DbProvider.getInstance().query( false, DB.ListReport.TABLE_NAME,
						null, null, null, null, null, null, null );
				cursorLiveData.postValue( new Pair<>( new Integer( TYPE_MIN_MAX ), cursor ) );
			}
			catch( Exception e )
			{
				Utils.logError( "Eroare ", e );
				cursorError.postValue( e );
			}
		} );
	}



	public void observeListOfReports( LifecycleOwner owner, Observer<Pair<Integer,Cursor>> observerExchange, Observer<Exception> observerExceptie )
	{
		cursorLiveData.observe( owner, observerExchange );
		cursorError.observe( owner, observerExceptie );
	}

	//---report details ---

	void loadGraphDetails(String timestamp)
	{
		executeAsync( () -> {

			try
			{

				String selection = DB.GraphReport.TIMESTAMP + "=?";
				String[] selectionArgs = {timestamp};

				Cursor cursor = DbProvider.getInstance().query( false, DB.GraphReport.TABLE_NAME,
						null, selection, selectionArgs, null, null, null, null );
				detailsGraphCursor.postValue( cursor );
			}
			catch( Exception e )
			{
				Utils.logError( "Eroare DB", e );
				cursorError.postValue( e );
			}
		} );
	}


	void loadListDetails(String timestamp)
	{
		executeAsync( () -> {

			try
			{

				String selection = DB.ListReport.TIMESTAMP + "=?";
				String[] selectionArgs = {timestamp};

				Cursor cursor = DbProvider.getInstance().query( false, DB.ListReport.TABLE_NAME,
						null, selection, selectionArgs, null, null, null, null );
				detailsListCursor.postValue( cursor );
			}
			catch( Exception e )
			{
				Utils.logError( "Eroare DB", e );
				cursorError.postValue( e );
			}
		} );
	}

	public void observeGraphDetails( LifecycleOwner owner, Observer<Cursor> observerExchange, Observer<Exception> observerExceptie )
	{
		detailsGraphCursor.observe( owner, observerExchange );
		cursorError.observe( owner, observerExceptie );
	}

	public void observeListDetails( LifecycleOwner owner, Observer<Cursor> observerExchange, Observer<Exception> observerExceptie )
	{
		detailsListCursor.observe( owner, observerExchange );
		cursorError.observe( owner, observerExceptie );
	}
}
