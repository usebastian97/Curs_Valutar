package com.sebastian.utoiu.cursvalutar.network;

import android.content.ContentValues;
import android.provider.BaseColumns;
import android.util.JsonReader;
import android.util.JsonToken;

import com.sebastian.utoiu.cursvalutar.Utils;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public abstract class Parser<T>
{

	//to be implemented for now
	protected abstract T parse( InputStream is, long size ) throws IOException;

	public T parseSuccess( InputStream is, long size )
	{
		try
		{
			return parse( is, size );
		}
		catch( Exception e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	public Object parseError( InputStream is, int responseCode ) throws IOException
	{
		String text = null;
		try
		{
			text = Utils.readStringFromStream( is );
			return new JSONObject( text ).getString( "error" );
		}
		catch( Exception e )
		{
			Utils.logError( "Error while parsing error stream: " + text, e );
		}
		return null;
	}


	//to be used when we don't care about parsing the result returned by the server

	public static final class DummyParser extends Parser<Void>
	{
		@Override
		protected Void parse( InputStream is, long size ) throws IOException
		{
			return null;
		}
	}

	String nextValue( JsonReader in ) throws IOException
	{
		if( in.peek() != JsonToken.NULL )
		{
			return in.nextString();
		}
		else
		{
			in.skipValue();
			return null;
		}
	}
}
