package com.sebastian.utoiu.cursvalutar.fragments;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.lifecycle.ViewModel;

public class BaseLiveViewModel extends ViewModel
{

	private ExecutorService executorService = Executors.newCachedThreadPool();

	public void executeAsync (Runnable runnable)
	{
		executorService.execute( runnable );
	}
}
