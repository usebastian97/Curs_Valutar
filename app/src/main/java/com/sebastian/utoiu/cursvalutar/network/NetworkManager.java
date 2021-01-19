package com.sebastian.utoiu.cursvalutar.network;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManager
{
	//singleton
	private static final class Holder
	{
		private static final NetworkManager instance = new NetworkManager();
	}

	public static NetworkManager getInstance()
	{
		return Holder.instance;
	}

	public static final MediaType JSON = MediaType.parse( "application/json; charset=utf-8" );

	protected OkHttpClient client;

	//constructor
	public NetworkManager()
	{
		OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
		builder.connectTimeout( 1, TimeUnit.MINUTES );
		builder.writeTimeout( 1, TimeUnit.MINUTES );
		builder.readTimeout( 1, TimeUnit.MINUTES );
		client = builder.build();
	}

	public ExchangeResponse getAllExchangeRatesForToday() throws Exception
	{
		String url = "http://data.fixer.io/api/latest?access_key=11c01117d37a6d8def49765132700db5&format=1";

		return get( url, new Parser<ExchangeResponse>()
		{
			@Override
			protected ExchangeResponse parse( InputStream is, long size ) throws IOException
			{

				JsonReader reader = new JsonReader( new InputStreamReader( is ) );

				return new Gson().fromJson( reader, ExchangeResponse.class );
			}
		} );
	}

//    public Posts[] getAllPostsFromTheServer() throws Exception{
//
//	    String url= "https://jsonplaceholder.typicode.com/posts";
//
//	    return get(url, new Parser<Posts[]>() {
//            @Override
//            protected Posts[] parse(InputStream is, long size) throws IOException {
//
//                JsonReader reader=new JsonReader(new InputStreamReader(is));
//
//                return new Gson().fromJson(reader, Posts[].class);
//            }
//        });
//    }

	//------------------------------

	public <T> T get( String url, Parser<T> parser ) throws ConnectionException
	{
		return execute( url, "GET", null, parser );
	}

	public <T> T post( String url, String body, Parser<T> parser ) throws ConnectionException
	{
		return execute( url, "POST", body, parser );
	}

	public <T> T put( String url, String body, Parser<T> parser ) throws ConnectionException
	{
		return execute( url, "PUT", body, parser );
	}

	public <T> T delete( String url, Parser<T> parser ) throws ConnectionException
	{
		return execute( url, "DELETE", null, parser );
	}

	// ////////////////////////////////////////////////////////////////
	public <T> T execute( String url, String method, String body, Parser<T> parser ) throws ConnectionException
	{
		Request.Builder builder = new Request.Builder();
		try
		{
			builder.url( url );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return null;
		}

		RequestBody requestBody = body == null ? null : RequestBody.create( JSON, body );
		builder.method( method, requestBody );

		builder.addHeader( "Content-type", "application/json; charset=UTF-8" );
		builder.addHeader( "Accept-Encoding", "gzip, deflate" );

		Request request = builder.build();

		Response response = null;
		T result = null;

		try
		{
			response = client.newCall( request ).execute();

			if( parser != null )
			{
				String encoding = response.header( "Content-Encoding" );
				InputStream is = response.body().source().inputStream();
				if( TextUtils.equals( encoding, "gzip" ) || TextUtils.equals( encoding, "x-gzip" ) )
				{
					is = new GZIPInputStream( is );
				}

				if( response.isSuccessful() )
				{
					result = parser.parseSuccess( is, response.body().contentLength() );
				}
				else
				{
					Object error = parser.parseError( is, response.code() );

					throw new ConnectionException( response.code(), response.message(), error );
				}
			}
		}
		catch( UnknownHostException e )
		{
			throw new ConnectionException( 0, e.getMessage(), "No network connection!" );
		}
		catch( IOException e )
		{
			throw new ConnectionException( 0, e.getMessage(), "Network error!" );
		}
		finally
		{
			if( response != null )
			{
				response.close();
			}
		}

		return result;
	}
}
