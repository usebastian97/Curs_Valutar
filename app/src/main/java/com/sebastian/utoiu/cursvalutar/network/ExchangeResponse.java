package com.sebastian.utoiu.cursvalutar.network;

import java.util.HashMap;

public class ExchangeResponse
{
	public boolean success;
	public long timestamp;
	public String date;
	public String base;
	public HashMap<String, Double> rates;
}
