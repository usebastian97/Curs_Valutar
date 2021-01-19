package com.sebastian.utoiu.cursvalutar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.sebastian.utoiu.cursvalutar.R;
import com.sebastian.utoiu.cursvalutar.network.ExchangeResponse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.sebastian.utoiu.cursvalutar.Utils.getExchangeListFromResponse;

public class CurrencyPicker extends DialogFragment
{

	private static final String CLASS = CurrencyPicker.class.getName();
	public static final String ARG_EXCHANGE_RATES = CLASS + "ARG_EXCHANGE_RATES";

	public static CurrencyPicker newInstance( ExchangeResponse response )
	{

		Bundle args = new Bundle();
		args.putString( ARG_EXCHANGE_RATES, new Gson().toJson( response ) );

		CurrencyPicker fragment = new CurrencyPicker();
		fragment.setArguments( args );
		return fragment;
	}

	private CurrencyAdapter.CurrecyAdapterOnClickListener listener;

	public void setListener( CurrencyAdapter.CurrecyAdapterOnClickListener listener)
	{
		this.listener = listener;

	}

	RecyclerView recyclerView;

//	public void setAdapter(CurrencyAdapter adapter)
//	{
//		recyclerView.setAdapter( adapter );
//		adapter.notifyDataSetChanged();
//	}


	@Nullable
	@Override
	public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState )
	{

		View view = inflater.inflate( R.layout.picker_currency, container, false );

		getDialog().setTitle( R.string.pick_currency );

		recyclerView = view.findViewById( R.id.currency_list );
		recyclerView.setLayoutManager( new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, false ) );
		recyclerView.setHasFixedSize( true );

		CurrencyAdapter adapter = new CurrencyAdapter();
		String argJso = getArguments().getString( ARG_EXCHANGE_RATES );

		if( argJso != null )
		{
			ExchangeResponse response = new Gson().fromJson( argJso, ExchangeResponse.class );
			adapter.setData( getExchangeListFromResponse( response ) );
			recyclerView.setAdapter( adapter );
			adapter.setListener( listener );
		}

		return view;
	}
}
