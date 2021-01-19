package com.sebastian.utoiu.cursvalutar.network;

public class ConnectionException extends Exception
{
	private static final long serialVersionUID = 1L;

	public int responseCode;
	public String responseMessage;
	public Object error;

	public ConnectionException( int responseCode, String responseMessage, Object error )
	{
		super( "" + responseCode + " - " + responseMessage );

		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.error = error;
	}

	@Override
	public String toString()
	{
		return responseMessage;
	}
}