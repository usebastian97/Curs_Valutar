package com.sebastian.utoiu.cursvalutar.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sebastian.utoiu.cursvalutar.R;
import com.sebastian.utoiu.cursvalutar.Utils;
import com.sebastian.utoiu.cursvalutar.models.ExchangeSimpleModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class ExchangeRatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{


	abstract void onRateClick( ExchangeSimpleModel rate );

	private ArrayList<ExchangeSimpleModel> exchangeRatesList;

	public void setData( ArrayList<ExchangeSimpleModel> newData )
	{
		exchangeRatesList = newData;
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount()
	{
		if( exchangeRatesList == null )
		{
			return 0;
		}
		else
		{
			return exchangeRatesList.size();
		}
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType )
	{
		View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.exchange_rate_list_item, parent, false );
		ExchangeRateViewHolder tip2ViewHolder = new ExchangeRateViewHolder( view );
		return tip2ViewHolder;
	}

	@Override
	public void onBindViewHolder( @NonNull RecyclerView.ViewHolder holder, int position )
	{
		ExchangeRateViewHolder exchangeRateViewHolder = (ExchangeRateViewHolder) holder;
		ExchangeSimpleModel model = exchangeRatesList.get( position );
		exchangeRateViewHolder.currencyValue.setText( model.value + "" );
		exchangeRateViewHolder.currecyName.setText( model.exchangeCurrency + "" );
		exchangeRateViewHolder.currencyFlag.setImageResource( Utils.getResIdByCurrencyName( model.exchangeCurrency ) );
		holder.itemView.setOnClickListener( v -> onRateClick( model ) );
	}

	public static class ExchangeRateViewHolder extends RecyclerView.ViewHolder
	{

		ImageView currencyFlag;
		TextView currencyValue;
		TextView currecyName;

		public ExchangeRateViewHolder( @NonNull View itemView )
		{
			super( itemView );

			currencyFlag = itemView.findViewById( R.id.currency_flag );
			currecyName = itemView.findViewById( R.id.currency_name );
			currencyValue = itemView.findViewById( R.id.currency_value );
		}
	}
}
