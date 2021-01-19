package com.sebastian.utoiu.cursvalutar.fragments;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.androidplot.xy.XYPlot;
import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sebastian.utoiu.cursvalutar.DateHelper;
import com.sebastian.utoiu.cursvalutar.R;
import com.sebastian.utoiu.cursvalutar.Utils;
import com.sebastian.utoiu.cursvalutar.database.DB;
import com.sebastian.utoiu.cursvalutar.database.DbProvider;
import com.sebastian.utoiu.cursvalutar.models.ExchangeSimpleModel;
import com.sebastian.utoiu.cursvalutar.models.MinMaxModel;
import com.sebastian.utoiu.cursvalutar.network.ExchangeResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BFragment extends Fragment
{

	private static final String CLASS = BFragment.class.getName();
	public static final String ARG_EXCHANGE_RATES = CLASS + "ARG_EXCHANGE_RATES";

	BFragmentViewModel mViewModel;

	CurrencyPicker currencyPicker;
	DatePicker startDatePicker;
	DatePicker endDatePicker;
	private XYPlot plot;
	RecyclerView minMaxRecyclerView;

	public static BFragment newInstance( ExchangeResponse response )
	{

		//GraphFunction function;
		Bundle args = new Bundle();
		args.putString( ARG_EXCHANGE_RATES, new Gson().toJson( response ) );
		BFragment fragment = new BFragment();
		fragment.setArguments( args );
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState )
	{

		return inflater.inflate( R.layout.fragment_b, container, false );
	}

	@Override
	public void onActivityCreated( @Nullable Bundle savedInstanceState )
	{
		super.onActivityCreated( savedInstanceState );

		androidx.appcompat.widget.Toolbar toolbar = getView().findViewById( R.id.main_toolbar );
		String argJso = getArguments().getString( ARG_EXCHANGE_RATES );
		if( argJso != null )
		{
			ExchangeResponse response = new Gson().fromJson( argJso, ExchangeResponse.class );

			toolbar.setTitle( getString( R.string.b_fragment_title ) + " ( " + response.base + " )" );
		}

		( (AppCompatActivity) getActivity() ).setSupportActionBar( toolbar );

		minMaxRecyclerView = getView().findViewById( R.id.min_max_list );
		minMaxRecyclerView.setLayoutManager( new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, false ) );

		mViewModel = new ViewModelProvider( this ).get( BFragmentViewModel.class );

		updateChooseCurrency();
		setupCurrencyPicker();
		setupStartDatePicker();
		setupEndDatePicker();
		setupChoseEndDateButton();
		setupChoseStartDateButton();

		setupGenerateGraphButton();
		setupGenerateListButton();
	}

	public void setupGenerateListButton()
	{

		View v = getView().findViewById( R.id.generate_list_button );
		v.setOnClickListener( v1 -> {

			if( mViewModel.startDateChosen == null || mViewModel.endDateChosen == null || mViewModel.currencyChosen == null )
			{
				Utils.showMessageWithToast( "Please complete all fields" );
			}
			else
			{
				setupList();
			}
		} );
	}

	public void setupGenerateGraphButton()
	{

		View v = getView().findViewById( R.id.generate_graph_button );
		v.setOnClickListener( v1 -> {

			if( mViewModel.startDateChosen == null || mViewModel.endDateChosen == null || mViewModel.currencyChosen == null )
			{
				Utils.showMessageWithToast( "Please complete all fields" );
			}
			else
			{
				setupPlot();
			}
		} );
	}

	public void setupList()
	{

		View graphContaner = getView().findViewById( R.id.graph_container );
		View listContaiiner = getView().findViewById( R.id.list_container );
		graphContaner.setVisibility( View.GONE );
		listContaiiner.setVisibility( View.VISIBLE );

		int daysBetween = DateHelper.daysBetween( mViewModel.startDateChosen, mViewModel.endDateChosen );

		String argJso = getArguments().getString( ARG_EXCHANGE_RATES );
		ExchangeResponse response = new Gson().fromJson( argJso, ExchangeResponse.class );

		String listTitle = "Min max for currencies versus  " + response.base + " between " +
				DateHelper.getDisplayStringFromDate( mViewModel.startDateChosen ) + " and " +
				DateHelper.getDisplayStringFromDate( mViewModel.endDateChosen ) + " (" + daysBetween + " days)";

		TextView minMaxTitle = getView().findViewById( R.id.min_max_list_title );
		minMaxTitle.setText( listTitle );

		ArrayList<MinMaxModel> datasource = new ArrayList<>();

		for( String currencyName : response.rates.keySet() )
		{

			double pivotValue = response.rates.get( currencyName );

			double percentVariation = 10.0f; // will take a 10% variation

			double variationValue = ( pivotValue * percentVariation ) / 100.0f;
			double minRange = pivotValue - variationValue;
			double maxRange = pivotValue + variationValue;

			Random r = new Random();

			//generate random values for evolution for each currency
			ArrayList<Double> valuesForCurrentCurrency = new ArrayList<>();

			for( int i = 0; i < daysBetween; i++ )
			{
				double randomValue = minRange + ( maxRange - minRange ) * r.nextDouble();
				valuesForCurrentCurrency.add( randomValue );
			}

			Double maxValue = Collections.max( valuesForCurrentCurrency );
			Double minValue = Collections.min( valuesForCurrentCurrency );

			MinMaxModel aModel = new MinMaxModel();
			aModel.maxValue = maxValue;
			aModel.minValue = minValue;
			aModel.baseCurrency = response.base;
			aModel.exchageCurrency = currencyName;

			datasource.add( aModel );
		}

		MinMaxAdapter adapter = new MinMaxAdapter()
		{
			@Override
			void onRateClick( MinMaxModel rate )
			{
				//
			}
		};

		minMaxRecyclerView.setAdapter( adapter );
		adapter.setData( datasource );

		//also save in sqlite database
		saveGeneratedListReportIntoDababase( DateHelper.getDisplayStringFromDate( mViewModel.startDateChosen ),
				DateHelper.getDisplayStringFromDate( mViewModel.endDateChosen ),
				mViewModel.currencyChosen.exchangeCurrency,
				mViewModel.currencyChosen.baseCurrency,
				datasource );
	}

	public void setupPlot()
	{

		View graphContaner = getView().findViewById( R.id.graph_container );
		View listContaiiner = getView().findViewById( R.id.list_container );
		graphContaner.setVisibility( View.VISIBLE );
		listContaiiner.setVisibility( View.GONE );

		int daysBetween = DateHelper.daysBetween( mViewModel.startDateChosen, mViewModel.endDateChosen );

		String argJso = getArguments().getString( ARG_EXCHANGE_RATES );
		ExchangeResponse response = new Gson().fromJson( argJso, ExchangeResponse.class );

		String graphTitle = "Evolution for " + response.base + "/" + mViewModel.currencyChosen.exchangeCurrency + " between " +
				DateHelper.getDisplayStringFromDate( mViewModel.startDateChosen ) + " and " +
				DateHelper.getDisplayStringFromDate( mViewModel.endDateChosen ) + " (" + daysBetween + " days)";

		TextView graphTitleTV = getView().findViewById( R.id.textViewGraphTitle );
		graphTitleTV.setText( graphTitle );

		final GraphView graph = (GraphView) getView().findViewById( R.id.graph );

		ArrayList<DataPoint> credibleRandomPoints = new ArrayList<>();
		Double pivotValue = mViewModel.currencyChosen.value;

		double percentVariation = 10.0f; // will take a 10% variation

		double variationValue = ( pivotValue * percentVariation ) / 100.0f;
		double minRange = pivotValue - variationValue;
		double maxRange = pivotValue + variationValue;

		Random r = new Random();

		for( int i = 0; i < daysBetween; i++ )
		{
			double randomValue = minRange + ( maxRange - minRange ) * r.nextDouble();
			DataPoint point = new DataPoint( i, randomValue );
			credibleRandomPoints.add( point );
		}

		DataPoint[] array = credibleRandomPoints.toArray( new DataPoint[credibleRandomPoints.size()] );

		LineGraphSeries<DataPoint> series = new LineGraphSeries<>( array );

		graph.removeAllSeries();
		graph.addSeries( series );

		//also save in sqlite database
		saveGeneratedGraphReportIntoDababase( DateHelper.getDisplayStringFromDate( mViewModel.startDateChosen ),
				DateHelper.getDisplayStringFromDate( mViewModel.endDateChosen ),
				mViewModel.currencyChosen.exchangeCurrency,
				mViewModel.currencyChosen.baseCurrency,
				array );
	}

	public void saveGeneratedListReportIntoDababase( String startDate, String endDate, String currency, String baseCurrecy, ArrayList<MinMaxModel> values )
	{

		mViewModel.executeAsync( new Runnable()
		{
			@Override
			public void run()
			{
				String timeStamp = "" + new Date().getTime();

				ContentValues record = new ContentValues();
				record.put( DB.ListReport.TIMESTAMP, timeStamp );
				record.put( DB.ListReport.START_DATE, startDate );
				record.put( DB.ListReport.END_DATE, endDate );
				record.put( DB.ListReport.BASE_CURRENCY, baseCurrecy );
				record.put( DB.ListReport.CURRECY, currency );
				record.put( DB.ListReport.VALUES, new Gson().toJson( values ) );
				DbProvider.getInstance().insert( DB.ListReport.TABLE_NAME, record );
			}
		} );
	}

	public void saveGeneratedGraphReportIntoDababase( String startDate, String endDate, String currency, String baseCurrecy, DataPoint[] dataPoints )
	{

		mViewModel.executeAsync( new Runnable()
		{
			@Override
			public void run()
			{
				String timeStamp = "" + new Date().getTime();

				ContentValues record = new ContentValues();
				record.put( DB.GraphReport.TIMESTAMP, timeStamp );
				record.put( DB.GraphReport.START_DATE, startDate );
				record.put( DB.GraphReport.END_DATE, endDate );
				record.put( DB.GraphReport.BASE_CURRENCY, baseCurrecy );
				record.put( DB.GraphReport.CURRECY, currency );
				record.put( DB.GraphReport.VALUES, new Gson().toJson( dataPoints ) );
				DbProvider.getInstance().insert( DB.GraphReport.TABLE_NAME, record );
			}
		} );
	}

	public void setupChoseStartDateButton()
	{
		Button b = getView().findViewById( R.id.choose_start_date );
		if( mViewModel.startDateChosen != null )
		{
			b.setText( DateHelper.getDisplayStringFromDate( mViewModel.startDateChosen ) );
		}

		b.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( View v )
			{
				startDatePicker.show( getChildFragmentManager(), "TAG_START_DATE_PICKER" );
			}
		} );
	}

	public void setupChoseEndDateButton()
	{
		Button b = getView().findViewById( R.id.choose_end_date );
		if( mViewModel.endDateChosen != null )
		{
			b.setText( DateHelper.getDisplayStringFromDate( mViewModel.endDateChosen ) );
		}

		b.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( View v )
			{

				if( mViewModel.startDateChosen == null )
				{
					Utils.showMessageWithToast( "Please choose start date first" );
				}
				else
				{
					endDatePicker.minDate = mViewModel.startDateChosen;
					Date now = new Date();

					if( now.after( mViewModel.startDateChosen ) )
					{
						endDatePicker.date = now;
					}

					endDatePicker.show( getChildFragmentManager(), "TAG_END_DATE_PICKER" );
				}
			}
		} );
	}

	public void setupStartDatePicker()
	{
		startDatePicker = new DatePicker();
		startDatePicker.date = new Date();
		startDatePicker.listener = new DatePickerDialog.OnDateSetListener()
		{
			@Override
			public void onDateSet( android.widget.DatePicker view, int year, int month, int dayOfMonth )
			{
				mViewModel.startDateChosen = DateHelper.getDate( dayOfMonth, month, year );
				setupChoseStartDateButton();
			}
		};
	}

	public void setupEndDatePicker()
	{
		endDatePicker = new DatePicker();
		endDatePicker.date = new Date();

		endDatePicker.listener = new DatePickerDialog.OnDateSetListener()
		{
			@Override
			public void onDateSet( android.widget.DatePicker view, int year, int month, int dayOfMonth )
			{
				mViewModel.endDateChosen = DateHelper.getDate( dayOfMonth, month, year );
				setupChoseEndDateButton();
			}
		};
	}

	public void updateChooseCurrency()
	{
		Button tv = getView().findViewById( R.id.choose_currency );
		if( mViewModel.currencyChosen != null )
		{
			tv.setText( "Chosen currenncy : " + mViewModel.currencyChosen.exchangeCurrency );
		}
		tv.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( View v )
			{
				currencyPicker.show( getFragmentManager(), "TAG_CURRENCY_PICKER" );
			}
		} );
	}

	public void setupCurrencyPicker()
	{
		String argJso = getArguments().getString( ARG_EXCHANGE_RATES );
		if( argJso != null )
		{
			ExchangeResponse response = new Gson().fromJson( argJso, ExchangeResponse.class );

			currencyPicker = CurrencyPicker.newInstance( response );

			currencyPicker.setListener( new CurrencyAdapter.CurrecyAdapterOnClickListener()
			{
				@Override
				public void onRateClick( ExchangeSimpleModel rate )
				{
					mViewModel.currencyChosen = rate;
					updateChooseCurrency();
					currencyPicker.dismiss();
				}
			} );
		}
	}
}
