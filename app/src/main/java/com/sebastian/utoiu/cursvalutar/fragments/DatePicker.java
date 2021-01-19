package com.sebastian.utoiu.cursvalutar.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DatePicker extends DialogFragment
{
	DatePickerDialog.OnDateSetListener listener;
	Date minDate;
	Date maxDate;

	Date date;

	@NonNull
	@Override
	public Dialog onCreateDialog( @Nullable Bundle savedInstanceState )
	{
		Calendar calendar = Calendar.getInstance();
		if( date != null )
		{
			calendar.setTime( date );
		}

		int year = calendar.get( Calendar.YEAR );
		int month = calendar.get( Calendar.MONTH );
		int day = calendar.get( Calendar.DAY_OF_MONTH );
		DatePickerDialog dialog = new DatePickerDialog( getContext(), listener, year, month, day );
		if( maxDate != null )
		{
			dialog.getDatePicker().setMaxDate( maxDate.getTime() );
		}

		if( minDate != null )
		{
			dialog.getDatePicker().setMinDate( minDate.getTime() );
		}

		return dialog;
	}
}
