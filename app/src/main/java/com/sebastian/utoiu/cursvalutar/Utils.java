package com.sebastian.utoiu.cursvalutar;

import android.util.Log;
import android.widget.Toast;

import com.sebastian.utoiu.cursvalutar.models.ExchangeSimpleModel;
import com.sebastian.utoiu.cursvalutar.network.ExchangeResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import androidx.annotation.Nullable;

public class Utils
{

	public static void showMessageWithToast( String message )
	{
		Toast.makeText( App.context, message, Toast.LENGTH_LONG ).show();
	}

	public static void logInfo( String message )
	{

		if( message != null )
		{
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			String className = stackTrace[3].getClassName();
			String tag = className.substring( className.lastIndexOf( '.' ) + 1 );
			Log.i( tag, message );
		}
	}

	public static void logError( String message, Exception e )
	{

		if( message != null )
		{
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			String className = stackTrace[3].getClassName();
			String tag = className.substring( className.lastIndexOf( '.' ) + 1 );
			Log.e( tag, message );
		}

		if( e != null )
		{
			e.printStackTrace();
		}
	}

	public static String readStringFromStream( InputStream is ) throws IOException
	{
		byte[] buffer = new byte[1024];
		int bytesRead = 0;
		StringBuilder sb = new StringBuilder();
		while( ( bytesRead = is.read( buffer ) ) != -1 )
		{
			sb.append( new String( buffer, 0, bytesRead ) );
		}

		return sb.toString();
	}

	public static int getResIdByCurrencyName( String name )
	{
		String nameToCheck = name;

		if( name.toLowerCase().equals( "try" ) )
		{
			nameToCheck = "tur";//this is an issue with turkis lira, try is reserved keyword
		}

		int resId = App.context.getResources().getIdentifier( nameToCheck.toLowerCase(), "drawable", App.context.getPackageName() );

		return resId;
	}

	public static void printStackTrace( @Nullable Throwable t )
	{
		if( t != null )
		{
			t.printStackTrace();
		}
	}

	public static ArrayList<ExchangeSimpleModel> getExchangeListFromResponse( ExchangeResponse response )
	{
		ArrayList<ExchangeSimpleModel> lista = new ArrayList<>();

		for( String aKey : response.rates.keySet() )
		{
			Double value = response.rates.get( aKey );
			ExchangeSimpleModel aModel = new ExchangeSimpleModel();
			aModel.baseCurrency = response.base;
			aModel.exchangeCurrency = aKey;
			aModel.value = value;
			lista.add( aModel );
		}

		return lista;
	}
}
