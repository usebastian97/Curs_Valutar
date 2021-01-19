package com.sebastian.utoiu.cursvalutar.fragments;

import com.sebastian.utoiu.cursvalutar.Utils;
import com.sebastian.utoiu.cursvalutar.network.ExchangeResponse;
import com.sebastian.utoiu.cursvalutar.network.NetworkManager;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class AFragmentViewModel extends BaseLiveViewModel
{

	private MutableLiveData<ExchangeResponse> exchangeResponse = new MutableLiveData<>();
	private MutableLiveData<Exception> exchangeResponseErrr = new MutableLiveData<>();

	public ExchangeResponse getCursValutar()
	{
		return exchangeResponse.getValue();
	}

	void loadCursValutar()
	{
		executeAsync( () -> {

			try
			{
				ExchangeResponse response = NetworkManager.getInstance().getAllExchangeRatesForToday();
				exchangeResponse.postValue( response );
			}
			catch( Exception e )
			{
				Utils.logError( "Eroare ", e );
				exchangeResponseErrr.postValue( e );
			}
		} );
	}

	public void observeCursValutar( LifecycleOwner owner, Observer<ExchangeResponse> observerExchange, Observer<Exception> observerExceptie )
	{
		exchangeResponse.observe( owner, observerExchange );
		exchangeResponseErrr.observe( owner, observerExceptie );
	}
}
