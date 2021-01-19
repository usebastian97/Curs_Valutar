package com.sebastian.utoiu.cursvalutar.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sebastian.utoiu.cursvalutar.R;
import com.sebastian.utoiu.cursvalutar.Utils;
import com.sebastian.utoiu.cursvalutar.models.MinMaxModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class MinMaxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

	abstract void onRateClick( MinMaxModel rate );

	private ArrayList<MinMaxModel> exchangeRatesList;

	public void setData( ArrayList<MinMaxModel> newData )
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
		View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.min_max_list_item, parent, false );
		ExchangeRateViewHolder tip2ViewHolder = new ExchangeRateViewHolder( view );
		return tip2ViewHolder;
	}

	@Override
	public void onBindViewHolder( @NonNull RecyclerView.ViewHolder holder, int position )
	{
		ExchangeRateViewHolder exchangeRateViewHolder = (ExchangeRateViewHolder) holder;
		MinMaxModel model = exchangeRatesList.get( position );

		exchangeRateViewHolder.currecyName.setText( model.exchageCurrency );
		exchangeRateViewHolder.minValue.setText( "Min: " + model.minValue );
		exchangeRateViewHolder.maxValue.setText( "Max: " + model.maxValue );
		exchangeRateViewHolder.currencyFlag.setImageResource( Utils.getResIdByCurrencyName( model.exchageCurrency ) );
		holder.itemView.setOnClickListener( v -> onRateClick( model ) );
	}

	public static class ExchangeRateViewHolder extends RecyclerView.ViewHolder
	{

		ImageView currencyFlag;
		TextView minValue;
		TextView maxValue;
		TextView currecyName;

		public ExchangeRateViewHolder( @NonNull View itemView )
		{
			super( itemView );

			currencyFlag = itemView.findViewById( R.id.currency_flag );
			currecyName = itemView.findViewById( R.id.currency_name );
			minValue = itemView.findViewById( R.id.min_value );
			maxValue = itemView.findViewById( R.id.max_value );
		}
	}
}
