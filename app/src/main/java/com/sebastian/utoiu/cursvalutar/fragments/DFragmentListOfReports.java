package com.sebastian.utoiu.cursvalutar.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sebastian.utoiu.cursvalutar.MainActivity;
import com.sebastian.utoiu.cursvalutar.R;
import com.sebastian.utoiu.cursvalutar.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.sebastian.utoiu.cursvalutar.fragments.ReportsListBaseCursorAdapter.TYPE_GRAPH;

public class DFragmentListOfReports extends Fragment
{
	RecyclerView recyclerView;

	DFragmentViewModel mViewModel;

	@Nullable
	@Override
	public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState )
	{

		return inflater.inflate( R.layout.fragment_d_list_of_reports, container, false );
	}

	@Override
	public void onActivityCreated( @Nullable Bundle savedInstanceState )
	{
		super.onActivityCreated( savedInstanceState );

		androidx.appcompat.widget.Toolbar toolbar = getView().findViewById( R.id.main_toolbar );
		toolbar.setTitle( getString( R.string.d_fragment_title ) );
		( (AppCompatActivity) getActivity() ).setSupportActionBar( toolbar );

		recyclerView = getView().findViewById( R.id.list_of_reports );
		recyclerView.setLayoutManager( new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, false ) );

		if( mViewModel == null )
		{
			mViewModel = new ViewModelProvider( this ).get( DFragmentViewModel.class );

			//load default graph when accessed
			mViewModel.loadListOfReportsGraph();
		}

		mViewModel.observeListOfReports( this, new Observer<Pair<Integer, Cursor>>()
		{
			@Override
			public void onChanged( Pair<Integer, Cursor> integerCursorPair )
			{

				//toast message for no reports found in DB
				if( integerCursorPair.second.getCount() == 0 )
				{
					String typeOfReportsNotFound;
					if( integerCursorPair.first == TYPE_GRAPH )
					{
						typeOfReportsNotFound = "graph.";
					}
					else
					{
						typeOfReportsNotFound = "min/max list.";
					}
					Utils.showMessageWithToast( "No reports found for " + typeOfReportsNotFound + " Please generate some" );
				}

				//adapter
				ReportsListBaseCursorAdapter adapter = new ReportsListBaseCursorAdapter( integerCursorPair.second, false )
				{
					@Override
					protected void onItemClick( Cursor cursor, String timeStamp )
					{

						DFragmentReportDetails fragment = DFragmentReportDetails.newInstance( timeStamp, integerCursorPair.first );
						(( MainActivity)getActivity()).showDetail( fragment,false );
					}

					@Override
					protected int reportType()
					{
						return integerCursorPair.first.intValue();
					}
				};

				recyclerView.setAdapter( adapter );
			}
		}, new Observer<Exception>()
		{
			@Override
			public void onChanged( Exception e )
			{
				Utils.showMessageWithToast( "Database error" );
				Utils.printStackTrace( e );
			}
		} );

		View buttonGraph = getView().findViewById( R.id.graph_reports );
		buttonGraph.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( View v )
			{
				mViewModel.loadListOfReportsGraph();
			}
		} );

		View buttonList = getView().findViewById( R.id.list_reports );
		buttonList.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( View v )
			{
				mViewModel.loadListOfReportsMinMax();
			}
		} );
	}
}
