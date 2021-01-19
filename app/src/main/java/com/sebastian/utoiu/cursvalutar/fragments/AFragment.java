package com.sebastian.utoiu.cursvalutar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sebastian.utoiu.cursvalutar.MainActivity;
import com.sebastian.utoiu.cursvalutar.R;
import com.sebastian.utoiu.cursvalutar.Utils;
import com.sebastian.utoiu.cursvalutar.models.ExchangeSimpleModel;
import com.sebastian.utoiu.cursvalutar.network.ExchangeResponse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.sebastian.utoiu.cursvalutar.Utils.getExchangeListFromResponse;

public class AFragment extends Fragment
{

	RecyclerView recyclerView;
	AFragmentViewModel mViewModel;

	@Nullable
	@Override
	public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState )
	{

		return inflater.inflate( R.layout.fragment_a, container, false );
	}

	@Override
	public void onActivityCreated( @Nullable Bundle savedInstanceState )
	{
		super.onActivityCreated( savedInstanceState );

		androidx.appcompat.widget.Toolbar toolbar = getView().findViewById( R.id.main_toolbar );
		toolbar.setTitle( getString( R.string.a_fragment_title ) );
		( (AppCompatActivity) getActivity() ).setSupportActionBar( toolbar );

		recyclerView = getView().findViewById( R.id.exchange_rates_list );
		recyclerView.setLayoutManager( new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, false ) );

		View b_and_c = getView().findViewById( R.id.b_c_button );
		b_and_c.setOnClickListener( v -> {
			ExchangeResponse response = mViewModel.getCursValutar();
			if( response != null )
			{
				BFragment fragment = BFragment.newInstance( response );
				( (MainActivity) getActivity() ).showDetail( fragment, false );
			}
			else
			{
				Utils.showMessageWithToast( "Cannot navigate. No data received from server" );
			}
		} );

		View d = getView().findViewById( R.id.d_button );

		d.setOnClickListener( v -> {

			DFragmentListOfReports fragment = new DFragmentListOfReports();
			( (MainActivity) getActivity() ).showDetail( fragment, false );
		} );

		mViewModel = new ViewModelProvider( this ).get( AFragmentViewModel.class );

		mViewModel.observeCursValutar( this, exchangeResponse -> {

			reloadUI();
		}, e -> {

			Utils.showMessageWithToast( "Error from server. Please check your internet connection and try again" );
		} );

		mViewModel.loadCursValutar();
	}

	public void reloadUI()
	{
		ExchangeResponse cursValutar = mViewModel.getCursValutar();
		if( cursValutar != null )
		{
			//main currency
			TextView mainCurrencyTv = getView().findViewById( R.id.main_currency_textview );
			mainCurrencyTv.setText( cursValutar.base );

			ImageView mainFlag = getView().findViewById( R.id.main_currency_flag );

			mainFlag.setImageResource( Utils.getResIdByCurrencyName( cursValutar.base ) );

			//list
			ExchangeRatesAdapter adapter = new ExchangeRatesAdapter()
			{
				@Override
				void onRateClick( ExchangeSimpleModel rate )
				{
					Utils.showMessageWithToast( "Click pe " + rate.exchangeCurrency );
				}
			};

			recyclerView.setAdapter( adapter );
			adapter.setData( getExchangeListFromResponse( cursValutar ) );
		}
	}
}
