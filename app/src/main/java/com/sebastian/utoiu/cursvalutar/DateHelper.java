package com.sebastian.utoiu.cursvalutar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateHelper
{

	public static Date getDate( int day, int month, int year )
	{

		Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.YEAR, year );
		calendar.set( Calendar.MONTH, month );
		calendar.set( Calendar.DAY_OF_MONTH, day );
		calendar.set( Calendar.HOUR, 0 );
		calendar.set( Calendar.MINUTE, 0 );
		calendar.set( Calendar.SECOND, 0 );

		return calendar.getTime();
	}

	public static String getDisplayStringFromDate( Date date )
	{
		SimpleDateFormat formater = new SimpleDateFormat( "yyyy-MM-dd" );

		return formater.format( date );
	}


	public static int daysBetween (Date startDate, Date endDate)
	{
		long diff = endDate.getTime() - startDate.getTime();
		return 1+ (int)TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS); // we want to keep the margins
	}

	public static String getDisplayDateFromTimestamp(String timestamp)
	{
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		String dateString = formatter.format(new Date(Long.parseLong(timestamp)));
		return dateString;
	}

}
