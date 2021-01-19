package com.sebastian.utoiu.cursvalutar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{

	private static final String DETAIL_TAG = "Detail tag for fragment";

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
	}


	public void showDetail( Fragment fragment, boolean replace )
	{
		if( fragment != null )
		{
			try
			{
				if( replace )
				{
					getSupportFragmentManager().popBackStackImmediate( DETAIL_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE );
				}

				///show fragment

				showFragment( fragment, DETAIL_TAG, true );
			}
			catch( Exception e )
			{
				Utils.logError( "Eroare show details for fragment", e );
			}
		}
	}

	private void showFragment( Fragment fragment, String tag, boolean addToBackStack )
	{
		try
		{
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace( R.id.fragment_container, fragment, tag );
			if( addToBackStack )
			{
				ft.addToBackStack( tag );
			}
			ft.commit();
		}
		catch( Exception e )
		{
			Utils.logError( "ddsadsa", e );
		}
	}
}