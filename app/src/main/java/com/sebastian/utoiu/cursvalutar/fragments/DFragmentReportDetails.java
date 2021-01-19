package com.sebastian.utoiu.cursvalutar.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sebastian.utoiu.cursvalutar.App;
import com.sebastian.utoiu.cursvalutar.R;
import com.sebastian.utoiu.cursvalutar.Utils;
import com.sebastian.utoiu.cursvalutar.database.DB;
import com.sebastian.utoiu.cursvalutar.models.MinMaxModel;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.sebastian.utoiu.cursvalutar.fragments.ReportsListBaseCursorAdapter.TYPE_GRAPH;

public class DFragmentReportDetails extends Fragment
{
	RecyclerView recyclerView;
	GraphView graphView;

	DFragmentViewModel mViewModel;

	private static final String CLASS = DFragmentReportDetails.class.getName();
	public static final String ARG_TIMESTAMP = CLASS + "timestamp";
	public static final String ARG_TYPE = CLASS + "type";

	public static DFragmentReportDetails newInstance( String timestamp, int type )
	{
		Bundle args = new Bundle();
		args.putString( ARG_TIMESTAMP, timestamp );
		args.putInt( ARG_TYPE, type );
		DFragmentReportDetails fragment = new DFragmentReportDetails();
		fragment.setArguments( args );
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState )
	{

		return inflater.inflate( R.layout.fragment_d_details, container, false );
	}

	@Override
	public void onActivityCreated( @Nullable Bundle savedInstanceState )
	{
		super.onActivityCreated( savedInstanceState );

		androidx.appcompat.widget.Toolbar toolbar = getView().findViewById( R.id.main_toolbar );
		toolbar.setTitle( getString( R.string.d_fragment_details_title ) );
		( (AppCompatActivity) getActivity() ).setSupportActionBar( toolbar );

		recyclerView = getView().findViewById( R.id.min_max_list );
		recyclerView.setLayoutManager( new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, false ) );

		graphView = getView().findViewById( R.id.graph );
		mViewModel = new ViewModelProvider( this ).get( DFragmentViewModel.class );

		String timestamp = getArguments().getString( ARG_TIMESTAMP );
		int type = getArguments().getInt( ARG_TYPE );
		if( timestamp != null )
		{
			if( type == TYPE_GRAPH )
			{
				mViewModel.observeGraphDetails( this, cursor -> {
					setupPlot( cursor );
				}, e -> {
					Utils.showMessageWithToast( "Eroare DB" );
				} );
				mViewModel.loadGraphDetails( timestamp );

			}
			else
			{
				mViewModel.observeListDetails( this, cursor -> {
					setupList( cursor );
				}, e -> {
					Utils.showMessageWithToast( "Eroare DB" );
				} );
				mViewModel.loadListDetails( timestamp );

			}
		}
	}

	private void setupList( Cursor cursor )
	{
		if( cursor.moveToFirst() )
		{
			String baseCurrency = cursor.getString( cursor.getColumnIndex( DB.ListReport.BASE_CURRENCY ) );
			String startDate = cursor.getString( cursor.getColumnIndex( DB.ListReport.START_DATE ) );
			String endDate = cursor.getString( cursor.getColumnIndex( DB.ListReport.END_DATE ) );
			String jsonValues = cursor.getString( cursor.getColumnIndex( DB.ListReport.VALUES ) );

			//header
			TextView tvName = getView().findViewById( R.id.detailsHeaderTv );
			String reportName = App.context.getString( R.string.min_max_values_for_all_currencies_vs ) + baseCurrency + " between " + startDate + " and " + endDate;
			tvName.setText( reportName );

			//list
			MinMaxModel[] dataSourceArray = new Gson().fromJson( jsonValues, MinMaxModel[].class );

			MinMaxAdapter adapter = new MinMaxAdapter()
			{
				@Override
				void onRateClick( MinMaxModel rate )
				{
					//
				}
			};

			recyclerView.setVisibility( View.VISIBLE );
			recyclerView.setAdapter( adapter );
			adapter.setData( new ArrayList<>( Arrays.asList( dataSourceArray ) ) );
		}
	}

	private void setupPlot( Cursor cursor )
	{
		if( cursor.moveToFirst() )
		{
			String baseCurrency = cursor.getString( cursor.getColumnIndex( DB.GraphReport.BASE_CURRENCY ) );
			String currency = cursor.getString( cursor.getColumnIndex( DB.GraphReport.CURRECY ) );
			String startDate = cursor.getString( cursor.getColumnIndex( DB.GraphReport.START_DATE ) );
			String endDate = cursor.getString( cursor.getColumnIndex( DB.GraphReport.END_DATE ) );
			String jsonValues = cursor.getString( cursor.getColumnIndex( DB.GraphReport.VALUES ) );

			//header
			TextView tvName = getView().findViewById( R.id.detailsHeaderTv );
			String reportName = App.context.getString( R.string.graph_report_for ) + baseCurrency + "/" + currency + " between " + startDate + " and " + endDate;
			tvName.setText( reportName );

			//graph
			DataPoint[] points = new Gson().fromJson( jsonValues, DataPoint[].class );
			LineGraphSeries<DataPoint> series = new LineGraphSeries<>( points );

			graphView.setVisibility( View.VISIBLE );
			graphView.removeAllSeries();
			graphView.addSeries( series );
		}
	}
}
