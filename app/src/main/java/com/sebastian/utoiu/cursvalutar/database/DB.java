package com.sebastian.utoiu.cursvalutar.database;

import android.provider.BaseColumns;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class DB
{
	public interface GraphReport extends BaseColumns{
		String TABLE_NAME = "graph_report";
		String TIMESTAMP = TABLE_NAME + "_" + "timestamp";
		String BASE_CURRENCY = TABLE_NAME + "_" + "base";
		String CURRECY = TABLE_NAME + "_" + "currency";
		String VALUES = TABLE_NAME + "_" + "values";
		String START_DATE = TABLE_NAME + "_" + "startdate";
		String END_DATE = TABLE_NAME + "_" + "enddate";
	}


	public interface ListReport extends BaseColumns{
		String TABLE_NAME = "list_report";
		String TIMESTAMP = TABLE_NAME + "_" + "timestamp";
		String BASE_CURRENCY = TABLE_NAME + "_" + "base";
		String CURRECY = TABLE_NAME + "_" + "currency";
		String VALUES = TABLE_NAME + "_" + "values";
		String START_DATE = TABLE_NAME + "_" + "startdate";
		String END_DATE = TABLE_NAME + "_" + "enddate";
	}


	@Retention( RetentionPolicy.RUNTIME )
	@Target( ElementType.FIELD )
	@interface ForeignKey
	{
		String table();

		String column();

		boolean onDeleteCascade() default false;
	}
}
